import { useAuthContext } from '@/utils/AuthContext';
import { useRouter } from 'next/router';
import useSWR from 'swr';
import { Hub as HubType } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import { swrHubFetcherWithAuth } from '@/utils/Utility';
import SkeletonLoader from '@/components/SkeletonLoader';
import { Alert, AlertTitle, Container, Divider, Grid, Typography } from '@mui/material';
import Head from 'next/head';
import Memos from '@/components/Memos';

const Hub = () => {
    const { query } = useRouter();
    const { id: hubId } = query;
    const { user } = useAuthContext();

    const { data, error, isLoading, isValidating } = useSWR<HubType | ErrorResponse>(
        { key: 'hub', token: user.token, hubId: hubId },
        swrHubFetcherWithAuth,
        {
            revalidateOnMount: true,
            revalidateOnReconnect: true,
            refreshWhenHidden: false,
            refreshWhenOffline: false,
        }
    );

    if (isLoading || isValidating) return <SkeletonLoader />;

    if (error) {
        // this a client error handler
        return (
            <Alert severity='error'>
                <AlertTitle>Oops!</AlertTitle>
                Unexpected error has occurred.
            </Alert>
        );
    }

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
        <>
            <Head>
                <title>Community Space | {data.name}</title>
            </Head>
            <Grid container>
                <Grid item xs={4}>
                    <Container>
                        <Typography mb={2} color='text.secondary' variant='h6'>
                            Waiting members
                        </Typography>
                        <Divider sx={{ mt: 2, mr: 2, mb: 2 }} />
                        <Typography mb={2} color='text.secondary' variant='h6'>
                            Members
                        </Typography>
                    </Container>
                </Grid>
                <Grid item xs={8}>
                    <Container sx={{ marginBottom: '1rem' }}>
                        <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                            Welcome to {data.name} Hub!
                        </Typography>
                    </Container>
                    <Divider sx={{ mb: 1.5 }} />
                    <Memos hubId={hubId} />
                </Grid>
            </Grid>
        </>
    );
};

export default Hub;
