import Presence from '@/components/Presence';
import { Divider, Grid } from '@mui/material';
import Head from 'next/head';
import Activity from '@/components/Activity';
import Hubs from '@/components/Hubs';

export default function Home() {
    return (
        <>
            <Head>
                <title>Community Space</title>
            </Head>
            <main>
                <Grid container>
                    <Grid item xs={4}>
                        <Presence />
                        <Divider sx={{ mt: 2, mr: 2, mb: 2 }} />
                        <Activity />
                    </Grid>
                    <Grid item xs={8}>
                        <Hubs />
                    </Grid>
                </Grid>
            </main>
        </>
    );
}
