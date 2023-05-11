import { useAuthContext } from '@/utils/AuthContext';
import { useRouter } from 'next/router';
import useSWR from 'swr';
import { Hub as HubType } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import { checkIfError, swrHubFetcherWithAuth } from '@/utils/Utility';
import { Container, Divider, Grid, Typography } from '@mui/material';
import Head from 'next/head';
import Memos from '@/components/Memos';
import Alerter from '@/components/Alerter';

const Hub = () => {
    const { query } = useRouter();
    const { id: hubId } = query;
    const { user } = useAuthContext();

    const {
        data: hub,
        error,
        isLoading,
        isValidating,
    } = useSWR<HubType | ErrorResponse>({ key: 'hub', token: user.token, hubId: hubId }, swrHubFetcherWithAuth);

    return (
        <>
            <Head>
                <title>Community Space {!checkIfError(hub) ? `| ${(hub as HubType).name}` : ''}</title>
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
                    <Alerter isValidating={isValidating} isLoading={isLoading} data={hub} error={error} />
                    {!isLoading && !isValidating && !checkIfError(hub) ? (
                        <>
                            <Container sx={{ marginBottom: '1rem' }}>
                                <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                                    Welcome to {(hub as HubType).name} Hub!
                                </Typography>
                            </Container>
                            <Divider sx={{ mb: 1.5 }} />
                            <Memos hubId={hubId} />
                        </>
                    ) : (
                        <div />
                    )}
                </Grid>
            </Grid>
        </>
    );
};

export default Hub;
