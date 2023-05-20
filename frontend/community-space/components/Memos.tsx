import useSWR from 'swr';
import {
    boldSelectedElementStyle,
    checkIfError,
    handleMultiSelectChange,
    sortByCreationDate,
    sortByUrgency,
    swrMemosFetcherWithAuth,
    swrRecentMemosFetcherWithAuth,
} from '@/utils/Utility';
import {
    Box,
    Button,
    Chip,
    Container,
    FormControl,
    InputLabel,
    MenuItem,
    OutlinedInput,
    Select,
    SelectChangeEvent,
    Stack,
    TextField,
    useTheme,
} from '@mui/material';
import { Memo as MemoType, urgencies } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { ErrorResponse } from '@/types/types';
import Memo from './Memo';
import SearchIcon from '@mui/icons-material/Search';
import { ChangeEvent, useState } from 'react';
import Alerter from './Alerter';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

const Memos = ({ hubId, scope = 'RECENT' }: { hubId?: string | string[]; scope?: 'RECENT' | 'ALL' }) => {
    const theme = useTheme();
    const { user } = useAuthContext();

    const [currPage, setCurrPage] = useState<number>(0);

    const {
        data: memos,
        error,
        isLoading,
        isValidating,
    } = useSWR<{ totalCount: number; totalPages: number; content: MemoType[] | ErrorResponse }>(
        { key: 'memos', token: user.token, hubId: hubId, page: currPage },
        scope === 'RECENT' ? swrRecentMemosFetcherWithAuth : swrMemosFetcherWithAuth,
        {
            revalidateOnFocus: false,
        }
    );

    const [urgencyFilter, setUrgencyFilter] = useState<string[]>([]);
    const [titleFilter, setTitleFilter] = useState<string>('');
    const [privateFilter, setPrivateFilter] = useState<boolean>(true);
    const [publicFilter, setPublicFilter] = useState<boolean>(true);

    return (
        <Stack spacing={2}>
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
                            sx={{ fontSize: 'medium' }}
                            fullWidth
                            size='small'
                            multiple
                            value={urgencyFilter}
                            onChange={(e: SelectChangeEvent<typeof urgencyFilter>) =>
                                handleMultiSelectChange(e, setUrgencyFilter)
                            }
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
            <Alerter isValidating={isValidating} isLoading={isLoading} data={memos} error={error} />
            {!isLoading &&
                !isValidating &&
                !checkIfError(memos) &&
                (memos.content as MemoType[])
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
            <Container sx={{ justifyContent: 'center', alignItems: 'center', display: 'flex', mt: 4 }}>
                <Button
                    startIcon={<ArrowBackIosNewIcon />}
                    sx={{ mr: 1 }}
                    disabled={currPage === 0}
                    onClick={() => setCurrPage(currPage - 1)}
                >
                    Prev
                </Button>
                <Button
                    endIcon={<ArrowForwardIosIcon />}
                    onClick={() => setCurrPage(currPage + 1)}
                    disabled={currPage + 1 === memos?.totalPages}
                >
                    Next
                </Button>
            </Container>
        </Stack>
    );
};

export default Memos;
