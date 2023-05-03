import Memos from '@/components/Memos';
import Presence from '@/components/Presence';
import { useAuthContext } from '@/utils/AuthContext';
import MessagingWrapper from '@/utils/MessagingWrapper';
import PresenceContextProvider from '@/utils/PresenceContext';
import { Divider, Grid } from '@mui/material';
import Head from 'next/head';
import { GATEWAY_ACCOUNT_WS } from '@/utils/Constants';
import Activity from '@/components/Activity';

export default function Home() {
    const { user } = useAuthContext();

    return (
        <>
            <Head>
                <title>Community Space</title>
            </Head>
            <main>
                <Grid container>
                    <Grid item xs={4}>
                        <MessagingWrapper url={GATEWAY_ACCOUNT_WS}>
                            <PresenceContextProvider user={user}>
                                <Presence />
                            </PresenceContextProvider>
                        </MessagingWrapper>
                        <Divider sx={{ mt: 2, mr: 2, mb: 2 }} />
                        <Activity />
                    </Grid>
                    <Grid item xs={8}>
                        <Memos />
                    </Grid>
                </Grid>
            </main>
        </>
    );
}
