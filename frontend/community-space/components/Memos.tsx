import useSWR from 'swr';
import { GATEWAY_URL, sortByCreationDate, sortByUrgency } from '@/utils/Utility';
import CircularLoading from './CircularLoading';
import { Alert, AlertTitle, Box, Chip, Stack, TextField } from '@mui/material';
import { Memo as MemoType } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { ErrorResponse } from '@/types/types';
import Memo from './Memo';
import MemoEdit from './MemoEdit';
import SearchIcon from '@mui/icons-material/Search';
import { ChangeEvent, useState } from 'react';

const swrFetcherWithAuth = async (args: string[]) => {
    const res = await fetch(args[0], {
        headers: { Authorization: `Bearer ${args[1]}` },
    });

    return await res.json();
};

const Memos = () => {
    const { user, signOut } = useAuthContext();
    const { data, error, isLoading } = useSWR<MemoType[] | ErrorResponse>(
        [`${GATEWAY_URL}/api/v1/memos`, user.token],
        swrFetcherWithAuth,
        {
            revalidateOnMount: true,
            revalidateOnReconnect: true,
            refreshInterval: 30 * 1000, // pull every 30 seconds
            refreshWhenHidden: false,
            refreshWhenOffline: false,
        }
    );

    const [titleFilter, setTitleFilter] = useState<string>('');
    const [privateFilter, setPrivateFilter] = useState<boolean>(true);
    const [publicFilter, setPublicFilter] = useState<boolean>(true);

    if (error) {
        // this a client error handler
        return (
            <Alert severity='error'>
                <AlertTitle>Oops!</AlertTitle>
                Unexpected error has occurred.
            </Alert>
        );
    }

    if (isLoading) return <CircularLoading />;

    if ('status' in data) {
        // if it's an error response, handle accordingly; this is coming from the server
        if (data.status === 404) {
            return (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    The requested content cannot be found.
                </Alert>
            );
        } else if (data.status === 401) {
            signOut();
            return (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    Unexpected error has occurred.
                </Alert>
            );
        } else {
            return (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    Unexpected error has occurred.
                </Alert>
            );
        }
    }

    return (
        <Stack spacing={2}>
            <MemoEdit />
            <Box sx={{ display: 'flex', alignItems: 'flex-end' }}>
                <SearchIcon sx={{ mr: 1, my: 0.5 }} />
                <TextField
                    id='input-with-sx'
                    label='Search by title...'
                    variant='standard'
                    fullWidth
                    onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setTitleFilter(e.target.value)}
                />
                <Stack direction={'row'}>
                    <Chip
                        label='Private'
                        sx={{ mr: 1, ml: 1 }}
                        variant={privateFilter ? 'filled' : 'outlined'}
                        onClick={() => setPrivateFilter(!privateFilter)}
                    />
                    <Chip
                        label='Public'
                        variant={publicFilter ? 'filled' : 'outlined'}
                        onClick={() => setPublicFilter(!publicFilter)}
                    />
                </Stack>
            </Box>
            {data &&
                data
                    .sort(sortByCreationDate)
                    .sort(sortByUrgency)
                    .filter((memo) => memo.title.toLowerCase().includes(titleFilter.toLowerCase()))
                    .filter((memo) => {
                        if (privateFilter && publicFilter) return true;
                        else if (privateFilter && memo.visibility === 'PRIVATE') return true;
                        else if (publicFilter && memo.visibility === 'PUBLIC') return true;
                        return false;
                    })
                    .map((memo, idx) => <Memo memo={memo} key={idx} />)}
        </Stack>
    );
};

export default Memos;
