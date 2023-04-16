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
import { useCallback, useEffect, useState } from 'react';
import { GATEWAY_URL } from '@/utils/Utility';
import { useAuthContext } from '@/utils/AuthContext';
import CircularLoading from './CircularLoading';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import EditIcon from '@mui/icons-material/Edit';
import Item from './Item';
import MemoEdit from './MemoEdit';
import { useSWRConfig } from 'swr';

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

    const [isOpen, setOpen] = useState<boolean>(false);
    const [isError, setError] = useState<boolean>(false);
    const [isUpdating, setUpdating] = useState<boolean>(false);
    const [memoDetails, setMemoDetails] = useState<MemoType>(null);

    useEffect(() => {
        setError(false);

        if (!memoDetails && isOpen) {
            const handleAsync = async () => {
                try {
                    const memoResponse = await fetch(`${GATEWAY_URL}/api/v1/memos/${memo.id}`, {
                        headers: { Authorization: `Bearer ${user.token}` },
                    });

                    if (!memoResponse.ok) {
                        throw new Error('Failed to pull memo details due to bad response', {
                            cause: {
                                res: memoResponse,
                            },
                        });
                    }

                    const memoBody = (await memoResponse.json()) as MemoType;
                    setMemoDetails(memoBody);
                } catch (err) {
                    console.debug(err.message, err);
                    setError(true);
                }
            };

            handleAsync();
        }
    }, [isOpen, isUpdating]);

    const handleClose = useCallback(() => {
        if (isUpdating) return setUpdating(false);
        if (isOpen) return setOpen(false);

        setOpen(true);
    }, [isOpen, setOpen, isUpdating, setUpdating]);

    const handleDelete = useCallback(() => {
        setError(false);

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

                setOpen(false);
                mutate([`${GATEWAY_URL}/api/v1/memos`, user.token]); // TODO: find a more efficient way
            } catch (err) {
                console.debug(err.message, err);
                setError(true);
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
            <MemoDialog open={isOpen} scroll='paper'>
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
                    <Chip label={memo.urgency} variant='filled' sx={{ mt: 1, mr: 1 }} />
                    <Chip label={memo.visibility.toLowerCase()} variant='filled' sx={{ mt: 1 }} />
                </DialogContent>
                <DialogContent dividers>
                    {isError ? (
                        <Alert severity='error'>
                            <AlertTitle>Oops!</AlertTitle>
                            Unexpected error has occurred.
                        </Alert>
                    ) : isUpdating ? (
                        <MemoEdit
                            initialState={{
                                title: memo.title,
                                visibility: memo.visibility,
                                urgency: memo.urgency,
                                content: memoDetails.content,
                            }}
                            memoId={memo.id}
                            isUpdateMode
                            cleanupCallback={() => {
                                setMemoDetails(null);
                                setUpdating(false);
                            }}
                        />
                    ) : memoDetails ? (
                        <>
                            <Typography sx={{ mb: 0 }} gutterBottom>
                                {memoDetails?.content}
                            </Typography>
                        </>
                    ) : (
                        <CircularLoading />
                    )}
                </DialogContent>
                {user.email === memo.author && !isUpdating && (
                    <DialogActions>
                        <Button
                            variant='outlined'
                            color='warning'
                            endIcon={<EditIcon />}
                            onClick={() => setUpdating(true)}
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
