import Alerter from '@/components/Alerter';
import Breadcrumbs from '@/components/Breadcrumbs';
import Members from '@/components/Members';
import Memos from '@/components/Memos';
import { Hub } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { swrHubFetcherWithAuth } from '@/utils/Utility';
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
    } = useSWR<Hub>({ key: 'hub', token: user.token, hubId: hubId }, swrHubFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    return (
        <>
            <Head>
                <title>Community Space {!hubError ? `| ${hub.name}` : ''}</title>
            </Head>
            <Breadcrumbs prevRoutes={[{ name: hub?.name, href: `/hubs/${hub?.id}` }]} currRoute={{ name: 'Explore' }} />
            <Grid container spacing={2}>
                <Grid item md={4} xs={12}>
                    <Container>
                        <Members hubId={hub?.id} hubRole={hub?.role} />
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
                                href={`/hubs/${hub?.id}`}
                                LinkComponent={Link}
                            >
                                Explore recent memos
                            </Button>
                        </Container>
                    </Container>
                </Grid>
                <Grid item md={8} xs={12}>
                    {!hubIsLoading && !hubIsValidating && !hubError ? (
                        <>
                            <Container sx={{ marginBottom: '1rem' }}>
                                <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                                    Welcome to {hub.name} Hub!
                                </Typography>
                                <Typography variant='body1' align='left' color='text.secondary'>
                                    {hub.description}
                                </Typography>
                            </Container>
                            <Divider sx={{ mb: 1.5 }} />
                            <Typography variant='h6' align='left' color='text.secondary' mb={2} mt={2}>
                                Explore all memos
                            </Typography>
                            <Memos scope='ALL' hubId={hubId} />
                        </>
                    ) : (
                        <Alerter isValidating={hubIsValidating} isLoading={hubIsLoading} data={hub} error={hubError} />
                    )}
                </Grid>
            </Grid>
        </>
    );
};

export default Explore;
