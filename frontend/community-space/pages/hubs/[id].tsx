import { useAuthContext } from '@/utils/AuthContext';
import { useRouter } from 'next/router';
import useSWR from 'swr';
import { Hub as HubType } from '@/types/db.types';
import { swrHubFetcherWithAuth } from '@/utils/Utility';
import { Button, Container, Divider, Grid, Typography } from '@mui/material';
import Head from 'next/head';
import Memos from '@/components/memos/Memos';
import Alerter from '@/components/layout/Alerter';
import Pendings from '@/components/hubs/Pendings';
import Link from 'next/link';
import MemoEdit from '@/components/memos/MemoEdit';
import Breadcrumbs from '@/components/layout/Breadcrumbs';
import HubsTopbar from "@/components/hubs/HubsTopbar";
import HubsSidebar from "@/components/hubs/HubsSidebar";

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
            <Breadcrumbs currRoute={{ name: hub?.name ?? '' }}/>
            <Grid container spacing={2}>
                <Grid item md={4} xs={12}>
                    <Container>
                        {hub?.role === 'OWNER' && (
                            <>
                                <Pendings hubId={hub?.id} hubRole={hub?.role}/>
                                <Divider sx={{ mt: 2, mb: 2 }}/>
                            </>
                        )}
                        <HubsSidebar hub={hub}/>
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
                                sx={{ mb: 1.5 }}
                            >
                                Explore older memos
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
                            <HubsTopbar hub={hub}/>
                            <MemoEdit hubId={hubId}/>
                            <Typography variant='h6' align='left' color='text.secondary' mb={2} mt={2}>
                                Browse recent memos
                            </Typography>
                            <Memos hubId={hubId}/>
                        </>
                    ) : (
                        <Alerter isValidating={hubIsValidating} isLoading={hubIsLoading} data={hub} error={hubError}/>
                    )}
                </Grid>
            </Grid>
        </>
    );
};

export default Hub;
