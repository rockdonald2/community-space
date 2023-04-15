import Memos from '@/components/Memos';
import Presence from '@/components/Presence';
import { Grid } from '@mui/material';
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
                    </Grid>
                    <Grid item xs={8}>
                        <Memos />
                    </Grid>
                </Grid>
            </main>
        </>
    );
}
