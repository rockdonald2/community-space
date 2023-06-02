import { useAuthContext } from '@/utils/AuthContext';
import { useRouter } from 'next/router';
import useSWR from 'swr';
import { Hub as HubType } from '@/types/db.types';
import { swrHubFetcherWithAuth } from '@/utils/Utility';
import { Button, Container, Divider, Grid, Typography } from '@mui/material';
import Head from 'next/head';
import Memos from '@/components/Memos';
import Alerter from '@/components/Alerter';
import Members from '@/components/Members';
import Pendings from '@/components/Pendings';
import Link from 'next/link';
import MemoEdit from '@/components/MemoEdit';
import Breadcrumbs from '@/components/Breadcrumbs';

const Hub = () => {
    const { query } = useRouter();
    const { id: hubId } = query;

    const { user } = useAuthContext();

    const {
        data: hub,
        error: hubError,
        isLoading: hubIsLoading,
        isValidating: hubIsValidating,
    } = useSWR<HubType>({ key: 'hub', token: user.token, hubId: hubId }, swrHubFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    return (
        <>
            <Head>
                <title>Community Space {!hubError ? `| ${hub?.name}` : ''}</title>
            </Head>
            <Breadcrumbs currRoute={{name: hub?.name ?? ''}} />
            <Grid container spacing={2}>
                <Grid item md={4} xs={12}>
                    <Container>
                        {hub?.role === 'OWNER' && (
                            <>
                                <Pendings hubId={hub?.id} hubRole={hub?.role} />
                                <Divider sx={{ mt: 2, mb: 2 }} />
                            </>
                        )}
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
                                href={`/hubs/${hub?.id}/explore`}
                                LinkComponent={Link}
                            >
                                Explore older memos
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
                            <MemoEdit hubId={hubId} />
                            <Typography variant='h6' align='left' color='text.secondary' mb={2} mt={2}>
                                Browse recent memos
                            </Typography>
                            <Memos hubId={hubId} />
                        </>
                    ) : (
                        <Alerter isValidating={hubIsValidating} isLoading={hubIsLoading} data={hub} error={hubError} />
                    )}
                </Grid>
            </Grid>
        </>
    );
};

export default Hub;
