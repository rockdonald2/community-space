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
                    <Grid
                        item
                        md={4}
                        xs={12}
                        sx={{
                            mb: {
                                xs: 2,
                                md: 0
                            },
                        }}
                    >
                        <Presence />
                        <Divider sx={{ mt: 2, mr: 2, mb: 2 }} />
                        <Activity />
                    </Grid>
                    <Grid item md={8} xs={12}>
                        <Hubs />
                    </Grid>
                </Grid>
            </main>
        </>
    );
}
