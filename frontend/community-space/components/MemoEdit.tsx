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
} from '@mui/material';
import Item from './Item';
import { ChangeEvent, Dispatch, SetStateAction, useCallback, useState } from 'react';
import SendIcon from '@mui/icons-material/Send';
import { Visibility, Urgency } from '@/types/db.types';
import { GATEWAY_URL } from '@/utils/Constants';
import { useAuthContext } from '@/utils/AuthContext';
import { useSWRConfig } from 'swr';

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
    };
    isUpdateMode?: boolean;
    memoId?: string;
    hubId: string | string[];
    cleanupCallback?: () => void;
}) => {
    const { user, signOut } = useAuthContext();
    const { mutate } = useSWRConfig(); // get a global mutator from the SWR config

    const [visibility, setVisibility] = useState<Visibility>(initialState?.visibility ?? 'PRIVATE');
    const [urgency, setUrgency] = useState<Urgency>(initialState?.urgency ?? '');
    const [title, setTitle] = useState<string>(initialState?.title ?? '');
    const [msg, setMsg] = useState<string>(initialState?.content ?? '');
    const [isError, setError] = useState<boolean>(false);
    const [errMsg, setErrMsg] = useState<string>('');

    const handleChange = useCallback(
        (e: ChangeEvent<HTMLInputElement> | SelectChangeEvent, setState: Dispatch<SetStateAction<any>>) =>
            setState(e.target.value),
        []
    );
    const handleSubmit = useCallback(() => {
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
                    };
                    url = `${GATEWAY_URL}/api/v1/memos/${memoId}`;
                } else {
                    requestBody = {
                        content: msg,
                        title,
                        visibility,
                        urgency,
                        hubId,
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

                mutate({ key: 'memos', token: user.token, hubId: hubId });
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
                            signOut();
                        } else {
                            alert('Failed to submit memo due to bad response');
                        }
                    }
                }
            }
        };

        handleAsync();
    }, [title, msg, urgency, isUpdateMode, user.token, cleanupCallback, mutate, hubId, visibility, memoId, signOut]);

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
            </div>
            <Divider sx={{ mb: 2 }} />
            <Stack direction={'row'} justifyContent={'space-between'} alignItems={'center'}>
                <div>
                    <FormControl sx={{ mr: 1, minWidth: 120 }} size='small'>
                        <InputLabel>Urgency</InputLabel>
                        <Select
                            value={urgency}
                            label='Urgency'
                            onChange={(e: SelectChangeEvent) => handleChange(e, setUrgency)}
                        >
                            <MenuItem value='' />
                            <MenuItem value={'LOW'}>Low</MenuItem>
                            <MenuItem value={'MEDIUM'}>Medium</MenuItem>
                            <MenuItem value={'HIGH'}>High</MenuItem>
                            <MenuItem value={'URGENT'}>Urgent</MenuItem>
                        </Select>
                    </FormControl>
                    <FormControl sx={{ mr: 1, minWidth: 120 }} size='small'>
                        <InputLabel>Visibility</InputLabel>
                        <Select
                            value={visibility}
                            label='Visibility'
                            onChange={(e: SelectChangeEvent) => handleChange(e, setVisibility)}
                        >
                            <MenuItem value={'PUBLIC'}>Public</MenuItem>
                            <MenuItem value={'PRIVATE'}>Only visible to you</MenuItem>
                        </Select>
                    </FormControl>
                </div>
                <Button
                    variant='outlined'
                    size='large'
                    endIcon={<SendIcon />}
                    aria-label='send memo'
                    type='button'
                    onClick={handleSubmit}
                >
                    {isUpdateMode ? 'Edit' : 'Send'}
                </Button>
            </Stack>
        </Item>
    );
};

export default MemoEdit;
