import {
    Alert,
    AlertTitle,
    Button,
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Divider,
    Grid,
    IconButton,
    Stack,
    Tooltip,
    Typography,
    styled,
} from '@mui/material';
import { Memo as MemoType, MemoShort } from '@/types/db.types';
import ArrowOutwardIcon from '@mui/icons-material/ArrowOutward';
import CloseIcon from '@mui/icons-material/Close';
import { useCallback, useState } from 'react';
import { GATEWAY_URL } from '@/utils/Constants';
import { useAuthContext } from '@/utils/AuthContext';
import CircularLoading from './CircularLoading';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import EditIcon from '@mui/icons-material/Edit';
import Item from './Item';
import MemoEdit from './MemoEdit';
import useSWR, { useSWRConfig } from 'swr';
import SkeletonLoader from './SkeletonLoader';

const MemoDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiPaper-root': {
        backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
    },
    '& .MuiDialogContent-root': {
        padding: theme.spacing(2),
    },
    '& .MuiDialogActions-root': {
        padding: theme.spacing(1),
    },
}));

const dateFormatter = new Intl.DateTimeFormat('en-gb', {
    formatMatcher: 'best fit',
    dateStyle: 'full',
    timeStyle: 'short',
});

const Memo = ({ memo }: { memo: MemoShort }) => {
    const { user } = useAuthContext();
    const { mutate } = useSWRConfig();

    const [isMemoOpen, setMemoOpen] = useState<boolean>(false);
    const [isMemoModificationError, setUserInputError] = useState<boolean>(false);
    const [isUserUpdatingMemo, setUserUpdatingMemo] = useState<boolean>(false);

    const { data, error, isLoading, isValidating } = useSWR<MemoType>(
        isMemoOpen ? `${GATEWAY_URL}/api/v1/memos/${memo.id}` : null,
        async (url) => {
            const res = await fetch(url, {
                headers: { Authorization: `Bearer ${user.token}` },
            });

            return await res.json();
        }
    );

    const handleClose = useCallback(() => {
        if (isUserUpdatingMemo) return setUserUpdatingMemo(false);
        if (isMemoOpen) return setMemoOpen(false);

        setMemoOpen(true);
    }, [isMemoOpen, setMemoOpen, isUserUpdatingMemo, setUserUpdatingMemo]);

    const handleDelete = useCallback(() => {
        setUserInputError(false);

        const handleAsync = async () => {
            try {
                const deleteResponse = await fetch(`${GATEWAY_URL}/api/v1/memos/${memo.id}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                    },
                });

                if (!deleteResponse.ok) {
                    throw new Error('Failed to delete memo due to bad response.', {
                        cause: {
                            res: deleteResponse,
                        },
                    });
                }

                setMemoOpen(false);
                mutate({ token: user.token });
            } catch (err) {
                console.debug(err.message, err);
                setUserInputError(true);
            }
        };

        handleAsync();
    }, [user?.token, memo?.id]);

    return (
        <>
            <Item>
                <Grid container alignItems={'center'} justifyContent={'center'}>
                    <Grid item xs={11}>
                        <Typography
                            variant='h6'
                            sx={{ mb: 1, mr: 1 }}
                            color='text.primary'
                            display='flex'
                            alignItems='center'
                        >
                            {memo.title}
                            <Chip sx={{ ml: 2 }} label={memo.visibility.toLowerCase()} />
                        </Typography>
                        <Stack direction={'row'} alignItems={'center'}>
                            <Typography sx={{ fontSize: 14, mb: 0 }} color='text.secondary' gutterBottom>
                                Posted by&nbsp;<strong>{memo.author}</strong>&nbsp;on{' '}
                                {dateFormatter.format(new Date(memo.createdOn))}
                            </Typography>
                            <Divider variant='middle' orientation='vertical' flexItem style={{ margin: '0 .5rem' }} />
                            <Typography sx={{ fontSize: 14, mb: 0 }} color='text.secondary' gutterBottom>
                                Urgency:&nbsp;
                                <strong style={{ color: 'var(--mui-palette-primary-light)' }}>{memo.urgency}</strong>
                            </Typography>
                        </Stack>
                    </Grid>
                    <Grid item xs={1} justifySelf={'flex-end'}>
                        <Tooltip title='See more'>
                            <IconButton onClick={handleClose}>
                                <ArrowOutwardIcon />
                            </IconButton>
                        </Tooltip>
                    </Grid>
                </Grid>
            </Item>
            <MemoDialog open={isMemoOpen} scroll='paper'>
                <DialogTitle sx={{ m: 0, p: 2 }}>
                    <Stack direction={'row'} alignItems={'center'} justifyContent={'space-between'}>
                        <Typography variant={'h6'}>{memo.title}</Typography>
                        <IconButton
                            aria-label='close'
                            onClick={handleClose}
                            sx={{
                                color: (theme) => theme.palette.grey[500],
                            }}
                        >
                            <CloseIcon />
                        </IconButton>
                    </Stack>
                </DialogTitle>
                <DialogContent>
                    <Typography sx={{ mb: 0 }} color='text.secondary'>
                        Memo posted on {dateFormatter.format(memo.createdOn)} by {memo.author}
                    </Typography>
                    <Chip label={memo.urgency.toLowerCase()} variant='filled' sx={{ mt: 1, mr: 1 }} />
                    <Chip label={memo.visibility.toLowerCase()} variant='filled' sx={{ mt: 1 }} />
                </DialogContent>
                <DialogContent dividers>
                    {isMemoModificationError || error ? (
                        <Alert severity='error'>
                            <AlertTitle>Oops!</AlertTitle>
                            Unexpected error has occurred.
                        </Alert>
                    ) : isUserUpdatingMemo ? (
                        <MemoEdit
                            initialState={{
                                title: memo.title,
                                visibility: memo.visibility,
                                urgency: memo.urgency,
                                content: data.content,
                            }}
                            memoId={memo.id}
                            isUpdateMode
                            cleanupCallback={() => {
                                setUserUpdatingMemo(false);
                            }}
                        />
                    ) : isLoading || isValidating ? (
                        <SkeletonLoader />
                    ) : (
                        <>
                            <Typography sx={{ mb: 0 }} gutterBottom>
                                {data?.content}
                            </Typography>
                        </>
                    )}
                </DialogContent>
                {user.email === memo.author && !isUserUpdatingMemo && (
                    <DialogActions>
                        <Button
                            variant='outlined'
                            color='warning'
                            endIcon={<EditIcon />}
                            onClick={() => setUserUpdatingMemo(true)}
                        >
                            Modify
                        </Button>
                        <Button variant='outlined' color='error' endIcon={<DeleteForeverIcon />} onClick={handleDelete}>
                            Delete
                        </Button>
                    </DialogActions>
                )}
            </MemoDialog>
        </>
    );
};

export default Memo;
