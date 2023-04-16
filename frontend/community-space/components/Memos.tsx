import useSWR from 'swr';
import {
    GATEWAY_URL,
    boldSelectedElementStyle,
    sortByCreationDate,
    sortByUrgency,
    swrFetcherWithAuth,
} from '@/utils/Utility';
import {
    Alert,
    AlertTitle,
    Box,
    Chip,
    FormControl,
    InputLabel,
    MenuItem,
    OutlinedInput,
    Select,
    SelectChangeEvent,
    Skeleton,
    Stack,
    TextField,
    useTheme,
} from '@mui/material';
import { Memo as MemoType, urgencies } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { ErrorResponse } from '@/types/types';
import Memo from './Memo';
import MemoEdit from './MemoEdit';
import SearchIcon from '@mui/icons-material/Search';
import { ChangeEvent, useState } from 'react';

const Memos = () => {
    const theme = useTheme();

    const { user } = useAuthContext();
    const { data, error, isLoading } = useSWR<MemoType[] | ErrorResponse>( // TODO: refactor with typescript compatibility
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

    const [urgencyFilter, setUrgencyFilter] = useState<string[]>([]);
    const [titleFilter, setTitleFilter] = useState<string>('');
    const [privateFilter, setPrivateFilter] = useState<boolean>(true);
    const [publicFilter, setPublicFilter] = useState<boolean>(true);

    const handleChange = (event: SelectChangeEvent<typeof urgencyFilter>) => {
        const {
            target: { value },
        } = event;

        setUrgencyFilter(typeof value === 'string' ? value.split(',') : value);
    };

    if (error) {
        // this a client error handler
        return (
            <Alert severity='error'>
                <AlertTitle>Oops!</AlertTitle>
                Unexpected error has occurred.
            </Alert>
        );
    }

    if (isLoading)
        return (
            <>
                <Skeleton />
                <Skeleton />
                <Skeleton />
            </>
        );

    if ('status' in data) {
        // if it's an error response, handle accordingly; this is coming from the server
        if (data.status === 404) {
            return (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    The requested content cannot be found.
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
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Stack direction={'row'} alignItems={'center'} sx={{ width: '100%' }}>
                    <FormControl sx={{ width: '50%' }}>
                        <InputLabel size='small'>Urgency</InputLabel>
                        <Select
                            fullWidth
                            size='small'
                            multiple
                            value={urgencyFilter}
                            onChange={handleChange}
                            input={<OutlinedInput label='Urgency' />}
                            renderValue={(selected) => (
                                <Box sx={{ display: 'flex', flexWrap: 'wrap' }}>
                                    {selected.map((value) => (
                                        <Chip key={value} label={value} sx={{ m: 0.5 }} />
                                    ))}
                                </Box>
                            )}
                        >
                            {urgencies.map((urgency) => (
                                <MenuItem
                                    key={urgency}
                                    value={urgency}
                                    style={boldSelectedElementStyle(urgency, urgencyFilter, theme)}
                                >
                                    {urgency}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
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
                    .filter((memo) => {
                        if (urgencyFilter.length === 0) return true;
                        return urgencyFilter.includes(memo.urgency);
                    })
                    .map((memo, idx) => <Memo memo={memo} key={idx} />)}
        </Stack>
    );
};

export default Memos;
