import {
    Alert,
    AlertTitle,
    Badge,
    Button,
    ButtonGroup,
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
    useMediaQuery,
    useTheme,
} from '@mui/material';
import { Memo as MemoType, MemoShort, Completion, HubShort } from '@/types/db.types';
import ArrowOutwardIcon from '@mui/icons-material/ArrowOutward';
import CloseIcon from '@mui/icons-material/Close';
import { useCallback, useState, useEffect } from 'react';
import { GATEWAY_URL } from '@/utils/Constants';
import { useAuthContext } from '@/utils/AuthContext';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import EditIcon from '@mui/icons-material/Edit';
import Item from '../layout/Item';
import MemoEdit from './MemoEdit';
import useSWR, { useSWRConfig } from 'swr';
import SkeletonLoader from '../layout/SkeletonLoader';
import { ReactMarkdown } from 'react-markdown/lib/react-markdown';
import remarkGfm from 'remark-gfm';
import { longDateShortTimeDateFormatter, swrCompletionsFetcherWithAuth, swrMemoFetcherWithAuth } from '@/utils/Utility';
import { useSnackbar } from 'notistack';
import DoneIcon from '@mui/icons-material/Done';
import Avatar from '@/components/misc/Avatar';
import EventIcon from '@mui/icons-material/Event';
import ArchiveIcon from '@mui/icons-material/Archive';
import PushPinIcon from '@mui/icons-material/PushPin';

const MemoDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiPaper-root': {
        backgroundColor: theme.palette.background.default,
    },
    '& .MuiDialogContent-root': {
        padding: theme.spacing(2),
    },
    '& .MuiDialogActions-root': {
        padding: theme.spacing(1),
    },
}));

const Memo = ({ memo, hub }: { memo: MemoShort; hub: HubShort }) => {
    const { user, signOut } = useAuthContext();
    const { mutate } = useSWRConfig();
    const { enqueueSnackbar } = useSnackbar();

    const theme = useTheme();
    const fullScreenDialog = useMediaQuery(theme.breakpoints.down('md'));

    const [isMemoOpen, setMemoOpen] = useState<boolean>(false);
    const [isMemoModificationError, setUserInputError] = useState<boolean>(false);
    const [isUserUpdatingMemo, setUserUpdatingMemo] = useState<boolean>(false);

    const [prevMemoData, setPrevMemoData] = useState<MemoType>(null);

    // ? Because that we don't fetch the data before opening the memo, on closing it will disregard the data and will collapse the inner body of the dialog
    const {
        data: memoData,
        error: memoError,
        isLoading: memoIsLoading,
        isValidating: memoIsValidating,
    } = useSWR<MemoType>(
        isMemoOpen ? { key: 'memo', token: user.token, memoId: memo.id } : null,
        swrMemoFetcherWithAuth,
        {
            revalidateOnFocus: false,
        }
    );

    const {
        data: completionsData,
        error: completionsError,
        isValidating: completionsIsValidating,
        isLoading: completionsIsLoading,
    } = useSWR<Completion[]>(
        isMemoOpen && memo.author.email === user.email
            ? {
                  key: 'completions',
                  token: user.token,
                  memoId: memo.id,
              }
            : null,
        swrCompletionsFetcherWithAuth
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

    const handlePatch = useCallback(
        async (body: any) => {
            const archiveResponse = await fetch(`${GATEWAY_URL}/api/v1/memos/${memo.id}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${user.token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(body),
            });

            if (!archiveResponse.ok) {
                throw new Error('Failed to update memo due to bad response', {
                    cause: {
                        res: archiveResponse,
                    },
                });
            }

            setMemoOpen(false);
            await mutate((key) => key['key'] === 'memos' && key['hubId'] === memo.hubId);
        },
        [memo.hubId, memo.id, mutate, user.token]
    );

    const handlePin = useCallback(
        async (pinned: boolean) => {
            setUserInputError(false);

            const handleAsync = async () => {
                try {
                    await handlePatch({ pinned: pinned });
                } catch (err) {
                    console.debug(err.message, err);
                    setUserInputError(true);
                    if (err instanceof Error) {
                        if ('res' in (err.cause as any)) {
                            const res = (err.cause as any).res;
                            if (res.status === 401) {
                                enqueueSnackbar('Your session has expired. Please sign in again', {
                                    variant: 'warning',
                                });
                                signOut();
                            } else {
                                enqueueSnackbar('Failed to archive memo', { variant: 'error' });
                            }
                        }
                    }
                }
            };

            await handleAsync();
        },
        [handlePatch, enqueueSnackbar, signOut]
    );

    const handleArchive = useCallback(
        async (archived: boolean) => {
            setUserInputError(false);

            const confirmationMsg: string = archived
                ? 'Are you sure you want to archive this memo?'
                : 'Are you sure you want to unarchive this memo?';
            const confirmation: boolean = confirm(confirmationMsg);

            const handleAsync = async () => {
                try {
                    await handlePatch({ archived: archived });
                } catch (err) {
                    console.debug(err.message, err);
                    setUserInputError(true);
                    if (err instanceof Error) {
                        if ('res' in (err.cause as any)) {
                            const res = (err.cause as any).res;
                            if (res.status === 401) {
                                enqueueSnackbar('Your session has expired. Please sign in again', {
                                    variant: 'warning',
                                });
                                signOut();
                            } else {
                                enqueueSnackbar('Failed to archive memo', { variant: 'error' });
                            }
                        }
                    }
                }
            };

            if (confirmation) {
                await handleAsync();
            }
        },
        [handlePatch, enqueueSnackbar, signOut]
    );

    const handleDelete = useCallback(async () => {
        setUserInputError(false);

        const confirmation: boolean = confirm('Are you sure you want to delete this memo?');

        const handleAsync = async () => {
            try {
                const deleteResponse = await fetch(`${GATEWAY_URL}/api/v1/memos/${memo.id}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                    },
                });

                if (!deleteResponse.ok) {
                    throw new Error('Failed to delete memo due to bad response', {
                        cause: {
                            res: deleteResponse,
                        },
                    });
                }

                setMemoOpen(false);
                await mutate((key) => key['key'] === 'memos' && key['hubId'] === memo.hubId);
            } catch (err) {
                console.debug(err.message, err);
                setUserInputError(true);
                if (err instanceof Error) {
                    if ('res' in (err.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to delete memo', { variant: 'error' });
                        }
                    }
                }
            }
        };

        if (confirmation) {
            await handleAsync();
        }
    }, [memo.id, memo.hubId, user.token, mutate, enqueueSnackbar, signOut]);

    const handleCompletion = useCallback(async () => {
        const handleAsync = async () => {
            try {
                const completionResponse = await fetch(`${GATEWAY_URL}/api/v1/memos/${memo.id}/completions`, {
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                        'Content-Type': 'application/json',
                    },
                    method: 'POST',
                    body: JSON.stringify({
                        user: user.email,
                    }),
                });

                if (!completionResponse.ok) {
                    throw new Error('Failed to handle completion of memo', {
                        cause: {
                            res: completionResponse,
                        },
                    });
                }

                await mutate((key) => key['key'] === 'memos' && key['hubId'] === memo.hubId);
            } catch (err) {
                console.debug(err.message, err);
                if (err instanceof Error) {
                    if ('res' in (err.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to handle completion of memo', { variant: 'error' });
                        }
                    }
                }
            }
        };

        await handleAsync();
    }, [enqueueSnackbar, memo.hubId, memo.id, mutate, signOut, user.email, user.token]);

    return (
        <>
            <Badge
                badgeContent={
                    memo?.pinned && (
                        <PushPinIcon sx={{ color: 'text.secondary', transform: 'rotate(45deg) scale(1.1)' }} />
                    )
                }
            >
                <Item sx={{ width: '100%' }}>
                    <Grid container alignItems={'center'} justifyContent={'center'}>
                        <Grid item md={11} xs={12}>
                            <Typography
                                sx={{ mb: 1, mr: 1 }}
                                color='text.primary'
                                display='flex'
                                alignItems='center'
                                component={'div'}
                            >
                                <Typography
                                    variant='h6'
                                    component='span'
                                    sx={{ textDecoration: memo.completed ? 'line-through' : 'none' }}
                                >
                                    {memo.title}
                                </Typography>
                                <Chip size='small' sx={{ ml: 2 }} label={memo.visibility.toLowerCase()} />
                            </Typography>
                            <Stack
                                direction={{ md: 'row', xs: 'column' }}
                                alignItems={{ xs: 'flex-start', md: 'center' }}
                            >
                                <Typography sx={{ fontSize: 14, mb: 0 }} color='text.secondary' gutterBottom>
                                    Posted by&nbsp;<strong>{memo.author.name}</strong>&nbsp;on{' '}
                                    {longDateShortTimeDateFormatter.format(new Date(memo.createdOn))}
                                </Typography>
                                <Divider variant='middle' orientation='vertical' flexItem sx={{ mx: 1, my: 0 }} />
                                <Typography sx={{ fontSize: 14, mb: 0 }} color='text.secondary' gutterBottom>
                                    Urgency:&nbsp;
                                    <strong style={{ color: 'var(--mui-palette-primary-light)' }}>
                                        {memo.urgency}
                                    </strong>
                                </Typography>
                            </Stack>
                        </Grid>
                        <Grid
                            item
                            md={1}
                            xs={12}
                            justifySelf={{ md: 'flex-end', xs: 'flex-start' }}
                            sx={{
                                mt: { xs: 2, md: 0 },
                                display: { xs: 'flex', md: 'inline-flex' },
                                justifyContent: 'flex-end',
                            }}
                        >
                            <Tooltip title='See more' enterTouchDelay={0} arrow>
                                <IconButton onClick={handleCloseTrigger}>
                                    <ArrowOutwardIcon />
                                </IconButton>
                            </Tooltip>
                        </Grid>
                    </Grid>
                </Item>
            </Badge>
            <MemoDialog open={isMemoOpen} scroll='paper' maxWidth='xl' fullScreen={fullScreenDialog}>
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
                    <Typography sx={{ mb: 1 }} color='text.primary' component={'div'}>
                        <Avatar
                            user={{ email: memo.author.email }}
                            generateRandomColor
                            style={{ width: '36px', height: '36px', fontSize: '16px', marginRight: '.5rem' }}
                        />
                        {memo.author.name}
                    </Typography>
                    <Typography sx={{ mb: 0 }} color='text.secondary'>
                        Memo posted on {longDateShortTimeDateFormatter.format(memo.createdOn)}
                    </Typography>
                    <Chip size='small' label={memo.urgency.toLowerCase()} variant='filled' sx={{ mt: 1, mr: 1 }} />
                    <Chip size='small' label={memo.visibility.toLowerCase()} variant='filled' sx={{ mt: 1 }} />
                    {memo?.archived && <Chip size='small' label='archived' variant='filled' sx={{ mt: 1, ml: 1 }} />}
                    {memo?.completed && <Chip size='small' label='completed' variant='filled' sx={{ mt: 1, ml: 1 }} />}
                    {prevMemoData?.dueDate && (
                        <Typography sx={{ mb: 0, mt: 2, display: 'flex', alignContent: 'center' }}>
                            <Chip
                                icon={<EventIcon />}
                                label={`Due on ${longDateShortTimeDateFormatter.format(
                                    new Date(prevMemoData?.dueDate)
                                )}`}
                                variant='filled'
                                sx={{
                                    textDecoration:
                                        new Date(prevMemoData?.dueDate) < new Date() ? 'line-through' : 'none',
                                }}
                            />
                        </Typography>
                    )}
                </DialogContent>
                <DialogContent dividers>
                    {isMemoModificationError || memoError ? (
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
                                dueDate: memoData?.dueDate,
                            }}
                            memoId={memo.id}
                            hubId={memo.hubId}
                            isUpdateMode
                            cleanupCallback={() => {
                                setUserUpdatingMemo(false);
                            }}
                        />
                    ) : memoIsLoading || memoIsValidating ? (
                        <SkeletonLoader nrOfLayers={1} />
                    ) : (
                        <>
                            <ReactMarkdown remarkPlugins={[[remarkGfm, { singleTilde: false }]]}>
                                {memoData?.content || prevMemoData?.content}
                            </ReactMarkdown>
                        </>
                    )}
                </DialogContent>
                {user.email === memo.author.email && (
                    <DialogContent sx={{ maxWidth: '90%' }}>
                        {completionsError ? (
                            <Alert severity='error'>
                                <AlertTitle>Oops!</AlertTitle>
                                Unexpected error has occurred.
                            </Alert>
                        ) : completionsIsLoading || completionsIsValidating ? (
                            <SkeletonLoader nrOfLayers={1} />
                        ) : (
                            <>
                                <Typography variant='subtitle1' sx={{ mb: 1 }}>
                                    Completions ({completionsData?.length || 0})
                                </Typography>
                                {completionsData?.map((completion, idx) => (
                                    <Avatar
                                        key={idx}
                                        user={{ email: completion.user }}
                                        generateRandomColor
                                        style={{
                                            width: 25,
                                            height: 25,
                                            fontSize: 13,
                                            marginRight: '.25rem',
                                            marginBottom: '.25rem',
                                        }}
                                    />
                                ))}
                            </>
                        )}
                    </DialogContent>
                )}
                {(user.email === memo.author.email || user.email === hub?.owner.email) && !isUserUpdatingMemo && (
                    <DialogActions>
                        <ButtonGroup
                            variant='outlined'
                            aria-label='Memo action button group'
                            disableElevation
                            size='small'
                        >
                            {!memo?.archived && (
                                <Button
                                    variant='outlined'
                                    endIcon={<PushPinIcon />}
                                    size='small'
                                    onClick={() => handlePin(!memo?.pinned ?? true)}
                                >
                                    {memo?.pinned ? 'Unpin' : 'Pin'}
                                </Button>
                            )}
                            <Button
                                variant='outlined'
                                endIcon={<ArchiveIcon />}
                                size='small'
                                onClick={() => handleArchive(!memo?.archived ?? true)}
                            >
                                {memo?.archived ? 'Unarchive' : 'Archive'}
                            </Button>
                            <Button
                                variant='outlined'
                                endIcon={<EditIcon />}
                                onClick={() => setUserUpdatingMemo(true)}
                                size='small'
                            >
                                Modify
                            </Button>
                            <Tooltip arrow title='Delete Memo'>
                                <Button variant='outlined' color='error' onClick={handleDelete} size='small'>
                                    <DeleteForeverIcon />
                                </Button>
                            </Tooltip>
                        </ButtonGroup>
                    </DialogActions>
                )}
                {user.email !== memo.author.email && !prevMemoData?.completed ? (
                    <DialogActions>
                        {!memo?.archived && (
                            <Button
                                variant='outlined'
                                color='primary'
                                endIcon={<DoneIcon />}
                                onClick={handleCompletion}
                                disabled={prevMemoData?.dueDate && new Date(prevMemoData?.dueDate) <= new Date()}
                            >
                                {!prevMemoData?.dueDate
                                    ? 'Mark as completed'
                                    : new Date(prevMemoData?.dueDate) > new Date()
                                    ? 'Mark as completed'
                                    : 'Due is over'}
                            </Button>
                        )}
                    </DialogActions>
                ) : (
                    user.email !== memo.author.email && (
                        <DialogActions>
                            <Typography color='text.secondary' variant='caption'>
                                Already completed
                            </Typography>
                        </DialogActions>
                    )
                )}
            </MemoDialog>
        </>
    );
};

export default Memo;
