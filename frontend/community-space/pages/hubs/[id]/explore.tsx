import Alerter from '@/components/layout/Alerter';
import Breadcrumbs from '@/components/layout/Breadcrumbs';
import Members from '@/components/hubs/Members';
import Memos from '@/components/memos/Memos';
import { Hub } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { swrHubFetcherWithAuth } from '@/utils/Utility';
import { Button, Container, Divider, Grid, Typography } from '@mui/material';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';
import useSWR from 'swr';
import HubsTopbar from '@/components/hubs/HubsTopbar';
import HubsSidebar from '@/components/hubs/HubsSidebar';

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
                        <HubsSidebar hub={hub} />
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
                                sx={{ mb: 1.5 }}
                            >
                                Explore recent memos
                            </Button>
                            <Button
                                size='small'
                                fullWidth
                                type='button'
                                variant='text'
                                href={`/hubs/${hub?.id}/archive`}
                                LinkComponent={Link}
                            >
                                Explore archived memos
                            </Button>
                        </Container>
                    </Container>
                </Grid>
                <Grid item md={8} xs={12}>
                    {!hubIsLoading && !hubIsValidating && !hubError ? (
                        <>
                            <HubsTopbar hub={hub} />
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
