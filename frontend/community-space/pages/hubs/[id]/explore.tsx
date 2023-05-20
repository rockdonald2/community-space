import Alerter from '@/components/Alerter';
import Members from '@/components/Members';
import Memos from '@/components/Memos';
import Pendings from '@/components/Pendings';
import { Hub } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import { useAuthContext } from '@/utils/AuthContext';
import { checkIfError, swrHubFetcherWithAuth } from '@/utils/Utility';
import { Grid, Container, Divider, Typography, Button } from '@mui/material';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';
import useSWR from 'swr';

const Explore = () => {
    const { query } = useRouter();
    const { id: hubId } = query;

    const { user } = useAuthContext();

    const {
        data: hub,
        error: hubError,
        isLoading: hubIsLoading,
        isValidating: hubIsValidating,
    } = useSWR<Hub | ErrorResponse>({ key: 'hub', token: user.token, hubId: hubId }, swrHubFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    return (
        <>
            <Head>
                <title>Community Space {!checkIfError(hub) ? `| ${(hub as Hub).name}` : ''}</title>
            </Head>
            <Grid container spacing={2}>
                <Grid item xs={4}>
                    <Container>
                        <Members hubId={(hub as Hub)?.id} hubRole={(hub as Hub)?.role} />
                        <Divider sx={{ mt: 2, mb: 2 }} />
                        <Typography mb={2} color='text.secondary' variant='h6'>
                            More stuff
                        </Typography>
                        <Container sx={{ mb: 1.5 }}>
                            <Button
                                size='small'
                                fullWidth
                                type='button'
                                variant='text'
                                href={`/hubs/${(hub as Hub)?.id}`}
                                LinkComponent={Link}
                            >
                                Explore recent memos
                            </Button>
                        </Container>
                    </Container>
                </Grid>
                <Grid item xs={8}>
                    <Alerter isValidating={hubIsValidating} isLoading={hubIsLoading} data={hub} error={hubError} />
                    {!hubIsLoading && !hubIsValidating && !checkIfError(hub) && (
                        <>
                            <Container sx={{ marginBottom: '1rem' }}>
                                <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                                    Welcome to {(hub as Hub).name} Hub!
                                </Typography>
                                <Typography variant='body1' align='left' color='text.secondary'>
                                    {(hub as Hub).description}
                                </Typography>
                            </Container>
                            <Divider sx={{ mb: 1.5 }} />
                            <Typography variant='h6' align='left' color='text.secondary' mb={2} mt={2}>
                                Explore all memos
                            </Typography>
                            <Memos scope='ALL' hubId={hubId} />
                        </>
                    )}
                </Grid>
            </Grid>
        </>
    );
};

export default Explore;
