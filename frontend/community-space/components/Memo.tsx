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
import { useCallback, useState, useEffect } from 'react';
import { GATEWAY_URL } from '@/utils/Constants';
import { useAuthContext } from '@/utils/AuthContext';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import EditIcon from '@mui/icons-material/Edit';
import Item from './Item';
import MemoEdit from './MemoEdit';
import useSWR, { useSWRConfig } from 'swr';
import SkeletonLoader from './SkeletonLoader';
import { ReactMarkdown } from 'react-markdown/lib/react-markdown';
import remarkGfm from 'remark-gfm';
import { swrMemoFetcherWithAuth } from '@/utils/Utility';
import { useSnackbar } from 'notistack';

const MemoDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiPaper-root': {
        backgroundColor: theme.palette.background.paper,
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
    const { enqueueSnackbar } = useSnackbar();

    const [isMemoOpen, setMemoOpen] = useState<boolean>(false);
    const [isMemoModificationError, setUserInputError] = useState<boolean>(false);
    const [isUserUpdatingMemo, setUserUpdatingMemo] = useState<boolean>(false);

    const [prevMemoData, setPrevMemoData] = useState<MemoType>(null);

    // ? Because that we don't fetch the data before opening the memo, on closing it will disregard the data and will collapse the inner body of the dialog
    const {
        data: memoData,
        error,
        isLoading,
        isValidating,
    } = useSWR<MemoType>(
        isMemoOpen ? { key: 'memo', token: user.token, memoId: memo.id } : null,
        swrMemoFetcherWithAuth,
        {
            revalidateOnFocus: false,
        }
    );

    useEffect(() => {
        // * possible solution for the above issue
        if (memoData) {
            setPrevMemoData(memoData);
        }
    }, [memoData]);

    const handleCloseTrigger = useCallback(() => {
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
                mutate({ key: 'memos', token: user.token, hubId: memo.hubId });
            } catch (err) {
                console.debug(err.message, err);
                enqueueSnackbar('Failed to delete memo.', { variant: 'error' });
                setUserInputError(true);
            }
        };

        handleAsync();
    }, [memo.id, memo.hubId, user.token, mutate, enqueueSnackbar]);

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
                            <IconButton onClick={handleCloseTrigger}>
                                <ArrowOutwardIcon />
                            </IconButton>
                        </Tooltip>
                    </Grid>
                </Grid>
            </Item>
            <MemoDialog open={isMemoOpen} scroll='paper' maxWidth='xl'>
                <DialogTitle sx={{ m: 0, p: 2 }}>
                    <Stack direction={'row'} alignItems={'center'} justifyContent={'space-between'}>
                        <Typography variant={'h6'}>{memo.title}</Typography>
                        <IconButton
                            aria-label='close'
                            onClick={handleCloseTrigger}
                            sx={{
                                color: (theme) => theme.palette.grey[500],
                            }}
                        >
                            <CloseIcon />
                        </IconButton>
                    </Stack>
                </DialogTitle>
                <DialogContent sx={{ overflow: 'initial' }}>
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
                                content: memoData?.content,
                            }}
                            memoId={memo.id}
                            hubId={memo.hubId}
                            isUpdateMode
                            cleanupCallback={() => {
                                setUserUpdatingMemo(false);
                            }}
                        />
                    ) : isLoading || isValidating ? (
                        <SkeletonLoader nrOfLayers={1} />
                    ) : (
                        <>
                            <ReactMarkdown remarkPlugins={[[remarkGfm, { singleTilde: false }]]}>
                                {memoData?.content || prevMemoData?.content}
                            </ReactMarkdown>
                        </>
                    )}
                </DialogContent>
                {user.email === memo.author && !isUserUpdatingMemo && (
                    <DialogActions>
                        <Button
                            variant='outlined'
                            color='primary'
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
