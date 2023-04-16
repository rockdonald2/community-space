import Memos from '@/components/Memos';
import Presence from '@/components/Presence';
import { Divider, Grid } from '@mui/material';
import Head from 'next/head';

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
                        <Divider sx={{ mt: 2, mr: 2 }} />
                    </Grid>
                    <Grid item xs={8}>
                        <Memos />
                    </Grid>
                </Grid>
            </main>
        </>
    );
}
