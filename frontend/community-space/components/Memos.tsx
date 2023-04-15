import useSWR from 'swr';
import { GATEWAY_URL } from '@/utils/Utility';
import CircularLoading from './CircularLoading';
import { Paper, Stack, Typography, styled } from '@mui/material';
import { Memo } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { ErrorResponse } from '@/types/types';

const Item = styled(Paper)(({ theme }) => ({
    backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
    ...theme.typography.body2,
    padding: theme.spacing(1),
    textAlign: 'center',
    color: theme.palette.text.secondary,
}));

const dateFormatter = new Intl.DateTimeFormat('en-us', {
    formatMatcher: 'best fit',
    dateStyle: 'full',
});

const fetcher = async (args: string[]) => {
    const res = await fetch(args[0], {
        headers: { Authorization: 'Bearer ' + args[1] },
    });

    return await res.json();
};

const Memos = () => {
    const { user } = useAuthContext();

    const { data, error, isLoading } = useSWR<Memo[] | ErrorResponse>(
        [`${GATEWAY_URL}/api/v1/memos`, user.token],
        fetcher
    );

    if (error) return <p>Error has occurred...</p>;
    if (isLoading) return <CircularLoading />;

    if ('status' in data) {
        if (data.status === 404) {
            return <p>Content not found...</p>;
        } else {
            return <p>Unexpected error occurred...</p>;
        }
    }

    return (
        <Stack spacing={2}>
            {data &&
                data
                    .sort((m1, m2) => (m1.createdOn < m2.createdOn ? 1 : -1))
                    .map((memo, idx) => (
                        <Item key={idx}>
                            <Typography sx={{ fontSize: 14 }} color='text.secondary' gutterBottom>
                                {memo.author} on {dateFormatter.format(new Date(memo.createdOn))}
                            </Typography>
                            {memo?.content && <Typography variant='body2'>{memo.content}</Typography>}
                        </Item>
                    ))}
        </Stack>
    );
};

export default Memos;
