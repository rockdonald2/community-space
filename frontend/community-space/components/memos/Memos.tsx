import { Memo as MemoType, urgencies } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import {
    boldSelectedElementStyle,
    handleMultiSelectChange,
    sortByCreationDate,
    sortByUrgency,
    swrArchivedMemosFetcher,
    swrMemosFetcherWithAuth,
    swrRecentMemosFetcherWithAuth,
} from '@/utils/Utility';
import SearchIcon from '@mui/icons-material/Search';
import {
    Box,
    Chip,
    FormControl,
    InputLabel,
    MenuItem,
    OutlinedInput,
    Pagination,
    Select,
    SelectChangeEvent,
    Stack,
    TextField,
    useTheme,
} from '@mui/material';
import { ChangeEvent, useState } from 'react';
import useSWR from 'swr';
import Alerter from '../layout/Alerter';
import Memo from './Memo';

const Memos = ({ hubId, scope = 'RECENT' }: { hubId?: string | string[]; scope?: 'RECENT' | 'ALL' | 'ARCHIVED' }) => {
    const theme = useTheme();
    const { user } = useAuthContext();

    const [currPage, setCurrPage] = useState<number>(1);

    const {
        data: memos,
        error,
        isLoading,
        isValidating,
    } = useSWR<{ totalCount: number; totalPages: number; content: MemoType[] }>(
        { key: 'memos', token: user.token, hubId: hubId, page: currPage - 1, scope: scope },
        scope === 'RECENT'
            ? swrRecentMemosFetcherWithAuth
            : scope === 'ARCHIVED'
                ? swrArchivedMemosFetcher
                : swrMemosFetcherWithAuth,
        {
            revalidateOnFocus: false
        }
    );

    const [urgencyFilter, setUrgencyFilter] = useState<string[]>([]);
    const [titleFilter, setTitleFilter] = useState<string>('');
    const [privateFilter, setPrivateFilter] = useState<boolean>(true);
    const [publicFilter, setPublicFilter] = useState<boolean>(true);

    return (
        <Stack spacing={2}>
            <Box sx={{ display: 'flex', alignItems: 'flex-end' }}>
                <SearchIcon sx={{ mr: 1, my: 0.5 }}/>
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
                            sx={{ fontSize: 'medium' }}
                            fullWidth
                            size='small'
                            multiple
                            value={urgencyFilter}
                            onChange={(e: SelectChangeEvent<typeof urgencyFilter>) =>
                                handleMultiSelectChange(e, setUrgencyFilter)
                            }
                            input={<OutlinedInput label='Urgency'/>}
                            renderValue={(selected) => (
                                <Box sx={{ display: 'flex', flexWrap: 'wrap' }}>
                                    {selected.map((value) => (
                                        <Chip key={value} label={value} sx={{ m: 0.5 }}/>
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
            {!isLoading && !isValidating && !error ? (
                memos.content
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
                    .map((memo, idx) => <Memo memo={memo} key={idx}/>)
            ) : (
                <Alerter isValidating={isValidating} isLoading={isLoading} data={memos} error={error}/>
            )}
            <Pagination
                count={memos?.totalPages}
                page={currPage}
                onChange={(_e, page) => setCurrPage(page)}
                sx={{ display: 'flex', alignContent: 'center', justifyContent: 'center', mt: 1 }}
                shape='rounded'
                variant='outlined'
                showFirstButton
                showLastButton
            />
        </Stack>
    );
};

export default Memos;
