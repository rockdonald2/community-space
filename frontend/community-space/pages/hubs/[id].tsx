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
import Members from '@/components/Members';
import Pendings from '@/components/Pendings';

const Hub = () => {
    const { query } = useRouter();
    const { id: hubId } = query;

    const { user } = useAuthContext();

    const {
        data: hub,
        error: hubError,
        isLoading: hubIsLoading,
        isValidating: hubIsValidating,
    } = useSWR<HubType | ErrorResponse>({ key: 'hub', token: user.token, hubId: hubId }, swrHubFetcherWithAuth, {
        revalidateOnFocus: false,
        refreshWhenHidden: false,
        refreshWhenOffline: false,
    });

    return (
        <>
            <Head>
                <title>Community Space {!checkIfError(hub) ? `| ${(hub as HubType).name}` : ''}</title>
            </Head>
            <Grid container spacing={2}>
                <Grid item xs={4}>
                    <Container>
                        {(hub as HubType)?.role === 'OWNER' && (
                            <>
                                <Pendings hubId={(hub as HubType)?.id} hubRole={(hub as HubType)?.role} />
                                <Divider sx={{ mt: 2, mb: 2 }} />
                            </>
                        )}
                        <Members hubId={(hub as HubType)?.id} hubRole={(hub as HubType)?.role} />
                    </Container>
                </Grid>
                <Grid item xs={8}>
                    <Alerter isValidating={hubIsValidating} isLoading={hubIsLoading} data={hub} error={hubError} />
                    {!hubIsLoading && !hubIsValidating && !checkIfError(hub) && (
                        <>
                            <Container sx={{ marginBottom: '1rem' }}>
                                <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                                    Welcome to {(hub as HubType).name} Hub!
                                </Typography>
                                <Typography variant='body1' align='left' color='text.secondary'>
                                    {(hub as HubType).description}
                                </Typography>
                            </Container>
                            <Divider sx={{ mb: 1.5 }} />
                            <Memos hubId={hubId} />
                        </>
                    )}
                </Grid>
            </Grid>
        </>
    );
};

export default Hub;
