import useSWR from 'swr';
import {
    boldSelectedElementStyle,
    checkIfError,
    sortByCreationDate,
    sortByUrgency,
    swrRecentMemosFetcherWithAuth,
} from '@/utils/Utility';
import {
    Box,
    Chip,
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
import MemoEdit from './MemoEdit';
import SearchIcon from '@mui/icons-material/Search';
import { ChangeEvent, Dispatch, SetStateAction, useState } from 'react';
import Alerter from './Alerter';

const Memos = ({ hubId }: { hubId?: string | string[] }) => {
    const theme = useTheme();

    const { user } = useAuthContext();
    const {
        data: memos,
        error,
        isLoading,
        isValidating,
    } = useSWR<MemoType[] | ErrorResponse>(
        { key: 'memos', token: user.token, hubId: hubId },
        swrRecentMemosFetcherWithAuth,
        {
            revalidateOnFocus: false,
            refreshWhenHidden: false,
            refreshWhenOffline: false,
        }
    );

    const [urgencyFilter, setUrgencyFilter] = useState<string[]>([]);
    const [titleFilter, setTitleFilter] = useState<string>('');
    const [privateFilter, setPrivateFilter] = useState<boolean>(true);
    const [publicFilter, setPublicFilter] = useState<boolean>(true);

    const handleMultiSelectChange = (
        event: SelectChangeEvent<typeof urgencyFilter>,
        setState: Dispatch<SetStateAction<any>>
    ) => {
        const {
            target: { value },
        } = event;

        setState(typeof value === 'string' ? value.split(',') : value);
    };

    return (
        <Stack spacing={2}>
            <MemoEdit hubId={hubId} />
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
                (memos as MemoType[])
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
