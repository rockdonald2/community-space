import {
    Alert,
    AlertTitle,
    Divider,
    FormControl,
    InputBase,
    InputLabel,
    Select,
    MenuItem,
    Button,
    Stack,
    SelectChangeEvent,
    ButtonGroup,
    Tooltip,
    Grid,
} from '@mui/material';
import Item from '../layout/Item';
import { ChangeEvent, Dispatch, SetStateAction, useCallback, useMemo, useState } from 'react';
import SendIcon from '@mui/icons-material/Send';
import { Visibility, Urgency } from '@/types/db.types';
import { GATEWAY_URL } from '@/utils/Constants';
import { useAuthContext } from '@/utils/AuthContext';
import { useSWRConfig } from 'swr';
import FormatBoldIcon from '@mui/icons-material/FormatBold';
import FormatItalicIcon from '@mui/icons-material/FormatItalic';
import FormatStrikethroughIcon from '@mui/icons-material/FormatStrikethrough';
import CodeIcon from '@mui/icons-material/Code';
import PermMediaIcon from '@mui/icons-material/PermMedia';
import Filter1Icon from '@mui/icons-material/Filter1';
import Filter2Icon from '@mui/icons-material/Filter2';
import Filter3Icon from '@mui/icons-material/Filter3';
import { useSnackbar } from 'notistack';
import { DateTimePicker } from '@mui/x-date-pickers';
import dayjs, { Dayjs } from 'dayjs';

const MemoEdit = ({
    initialState,
    isUpdateMode = false,
    memoId = '',
    hubId,
    cleanupCallback,
}: {
    initialState?: {
        title: string;
        content: string;
        visibility: Visibility;
        urgency: Urgency;
        dueDate: Date;
    };
    isUpdateMode?: boolean;
    memoId?: string;
    hubId: string | string[];
    cleanupCallback?: () => void;
}) => {
    const { user, signOut } = useAuthContext();
    const { mutate } = useSWRConfig(); // get a global mutator from the SWR config
    const { enqueueSnackbar } = useSnackbar();

    const [visibility, setVisibility] = useState<Visibility>(initialState?.visibility ?? 'PRIVATE');
    const [urgency, setUrgency] = useState<Urgency>(initialState?.urgency ?? '');
    const [title, setTitle] = useState<string>(initialState?.title ?? '');
    const [msg, setMsg] = useState<string>(initialState?.content ?? '');
    const [dueDate, setDueDate] = useState<Dayjs | null>(initialState?.dueDate ? dayjs(initialState.dueDate) : null);
    const [isError, setError] = useState<boolean>(false);
    const [errMsg, setErrMsg] = useState<string>('');

    const editorButtons: JSX.Element[] = useMemo(
        () => [
            <Button
                key='h1'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '\n#');
                }}
            >
                <Tooltip title='Heading 1'>
                    <Filter1Icon />
                </Tooltip>
            </Button>,
            <Button
                key='h2'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '\n##');
                }}
            >
                <Tooltip title='Heading 2'>
                    <Filter2Icon />
                </Tooltip>
            </Button>,
            <Button
                key='h3'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '\n###');
                }}
            >
                <Tooltip title='Heading 3'>
                    <Filter3Icon />
                </Tooltip>
            </Button>,
            <Button
                key='bold'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '**bold**');
                }}
            >
                <Tooltip title='Bold'>
                    <FormatBoldIcon />
                </Tooltip>
            </Button>,
            <Button
                key='italic'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '*italic*');
                }}
            >
                <Tooltip title='Italic'>
                    <FormatItalicIcon />
                </Tooltip>
            </Button>,
            <Button
                key='strike'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '~~strikethrough~~');
                }}
            >
                <Tooltip title='Strikethrough'>
                    <FormatStrikethroughIcon />
                </Tooltip>
            </Button>,
            <Button
                key='code'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '\n```\n\n```\n');
                }}
            >
                <Tooltip title='Code'>
                    <CodeIcon />
                </Tooltip>
            </Button>,
            <Button
                key='media'
                sx={{ color: 'text.secondary' }}
                variant='text'
                onClick={() => {
                    setMsg(msg + '\n![text](url)');
                }}
            >
                <Tooltip title='Media'>
                    <PermMediaIcon />
                </Tooltip>
            </Button>,
        ],
        [msg]
    );

    const handleChange = useCallback(
        (e: ChangeEvent<HTMLInputElement> | SelectChangeEvent, setState: Dispatch<SetStateAction<any>>) =>
            setState(e.target.value),
        []
    );
    const handleSubmit = useCallback(() => {
        if (errMsg.includes('due date') && isError) {
            // if there's a problem with the set due date, don't submit
            return;
        }

        setError(false);

        if (title.trim().length === 0 || msg.trim().length === 0 || urgency === '') {
            setError(true);
            setErrMsg('All fields must be filled in order to submit a new memo.');
            return;
        }

        const handleAsync = async () => {
            try {
                let requestBody = {};
                let url = '';
                const method = isUpdateMode ? 'PATCH' : 'POST';

                if (isUpdateMode) {
                    requestBody = {
                        content: msg,
                        visibility,
                        urgency,
                        title,
                        dueDate: dueDate?.toDate().toISOString() ?? null,
                    };
                    url = `${GATEWAY_URL}/api/v1/memos/${memoId}`;
                } else {
                    requestBody = {
                        content: msg,
                        title,
                        visibility,
                        urgency,
                        hubId,
                        dueDate: dueDate?.toDate().toISOString() ?? null,
                    };
                    url = `${GATEWAY_URL}/api/v1/memos`;
                }

                const submitResponse = await fetch(url, {
                    method: method,
                    headers: { Authorization: `Bearer ${user.token}`, 'Content-Type': 'application/json' },
                    body: JSON.stringify(requestBody),
                });

                if (!submitResponse.ok) {
                    throw new Error('Failed to submit memo due to bad response.', {
                        cause: {
                            res: submitResponse,
                        },
                    });
                }

                if (typeof cleanupCallback === 'function') {
                    cleanupCallback();
                }

                mutate((key) => key['key'] === 'memos' && key['hubId'] === hubId);
                setMsg('');
                setTitle('');
                setUrgency('');
                setVisibility('PRIVATE');
            } catch (err) {
                console.debug(err.message, err);
                setError(true);
                setErrMsg(err.message);
                if (err instanceof Error) {
                    if ('res' in (err.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to submit memo', { variant: 'error' });
                        }
                    }
                }
            }
        };

        handleAsync();
    }, [
        errMsg,
        isError,
        title,
        msg,
        urgency,
        isUpdateMode,
        user.token,
        cleanupCallback,
        mutate,
        visibility,
        dueDate,
        memoId,
        hubId,
        enqueueSnackbar,
        signOut,
    ]);

    return (
        <Item>
            {isError && (
                <Alert severity='warning' sx={{ mb: 2 }}>
                    <AlertTitle>Oops!</AlertTitle>
                    {errMsg}
                </Alert>
            )}
            <div>
                {msg && (
                    <InputBase
                        fullWidth
                        sx={{ ml: 1, mr: 1, mt: 0.5, mb: 2, flex: 1 }}
                        placeholder='Title'
                        value={title}
                        inputProps={{ 'aria-label': 'memo input field' }}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => handleChange(e, setTitle)}
                    />
                )}
                <InputBase
                    fullWidth
                    sx={{ ml: 1, mr: 1, mt: 0.5, mb: 2, flex: 1 }}
                    placeholder='Any thoughts...'
                    inputProps={{ 'aria-label': 'memo input field' }}
                    multiline
                    value={msg}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => handleChange(e, setMsg)}
                />
                {msg && (
                    <ButtonGroup
                        color={'inherit'}
                        sx={{ mb: 1, display: 'flex', flexWrap: 'wrap' }}
                        size='small'
                        aria-label='editor button group'
                    >
                        {editorButtons}
                    </ButtonGroup>
                )}
            </div>
            <Divider sx={{ mb: 2 }} />
            <Stack
                direction={{ md: 'row', xs: 'column' }}
                justifyContent={{ md: 'space-between', xs: 'center' }}
                alignItems={{ md: 'center', xs: 'stretch' }}
                component={'div'}
            >
                <Grid container spacing={1}>
                    <Grid item xs={12}>
                        <FormControl
                            sx={{
                                mr: { xs: 0, md: 1.25 },
                                mb: { xs: 1.25, md: 0 },
                                minWidth: 120,
                                width: { xs: '100%', md: 'unset' },
                            }}
                            size='small'
                        >
                            <InputLabel>Urgency</InputLabel>
                            <Select
                                sx={{ fontSize: 'medium' }}
                                value={urgency}
                                label='Urgency'
                                onChange={(e: SelectChangeEvent) => handleChange(e, setUrgency)}
                                error={isError}
                            >
                                <MenuItem value='' />
                                <MenuItem value={'LOW'}>Low</MenuItem>
                                <MenuItem value={'MEDIUM'}>Medium</MenuItem>
                                <MenuItem value={'HIGH'}>High</MenuItem>
                                <MenuItem value={'URGENT'}>Urgent</MenuItem>
                            </Select>
                        </FormControl>
                        <FormControl
                            sx={{
                                mr: { xs: 0, md: 1 },
                                mb: { xs: 1, md: 0 },
                                minWidth: 120,
                                width: { xs: '100%', md: 'unset' },
                            }}
                            size='small'
                        >
                            <InputLabel>Visibility</InputLabel>
                            <Select
                                sx={{ fontSize: 'medium' }}
                                value={visibility}
                                label='Visibility'
                                onChange={(e: SelectChangeEvent) => handleChange(e, setVisibility)}
                                error={isError}
                            >
                                <MenuItem value={'PUBLIC'}>Public</MenuItem>
                                <MenuItem value={'PRIVATE'}>Only visible to you</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>
                    {msg && (
                        <Grid item xs={12}>
                            <FormControl
                                sx={{
                                    mr: { xs: 0, md: 1 },
                                    mb: { xs: 1, md: 0 },
                                    width: { xs: '100%', md: 'unset' },
                                }}
                                size='small'
                            >
                                <DateTimePicker
                                    views={['year', 'day', 'hours', 'minutes']}
                                    label='Due date'
                                    disablePast
                                    slotProps={{
                                        textField: { size: 'small', sx: { fontSize: 'medium' } },
                                        actionBar: {
                                            actions: ['clear', 'cancel', 'accept'],
                                        },
                                    }}
                                    value={dueDate}
                                    onAccept={(dateVal: Dayjs | null) => setDueDate(dateVal)}
                                    onError={(err: unknown) => {
                                        if (err) {
                                            setError(true);
                                            setErrMsg('Invalid due date given');
                                        } else {
                                            setError(false);
                                            setErrMsg('');
                                        }
                                    }}
                                />
                            </FormControl>
                        </Grid>
                    )}
                </Grid>
                <Tooltip title='Submit your memo'>
                    <Button
                        variant='outlined'
                        size='large'
                        endIcon={<SendIcon />}
                        aria-label='send memo'
                        type='button'
                        onClick={handleSubmit}
                        sx={{ mt: { xs: 2, md: 0 } }}
                    >
                        {isUpdateMode ? 'Edit' : 'Send'}
                    </Button>
                </Tooltip>
            </Stack>
        </Item>
    );
};

export default MemoEdit;
