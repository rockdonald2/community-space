import { useAuthContext } from '@/utils/AuthContext';
import Header from './Header';
import { Divider, Grid, Typography, useColorScheme, useMediaQuery } from '@mui/material';
import { useEffect } from 'react';
import QuickActions from './QuickActions';
import Blob from './Blob';
import { SWRConfig } from 'swr';
import { useSnackbar } from 'notistack';
import PresenceContextProvider from '@/utils/PresenceContext';
import { useRouter } from 'next/router';
import Footer from './Footer';

const Layout = ({ children }) => {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const { setMode } = useColorScheme();
    const { isAuthenticated, signOut } = useAuthContext();
    const { enqueueSnackbar } = useSnackbar();
    const { push } = useRouter();

    useEffect(() => {
        setMode(prefersDarkMode ? 'dark' : 'light');
    }, [prefersDarkMode, setMode]);

    return (
        <>
            {/* <Blob /> */}
            {isAuthenticated ? (
                <>
                    <SWRConfig
                        value={{
                            onError: (error, _key) => {
                                if (error?.status === 401) {
                                    enqueueSnackbar('Your session has expired. Please sign in again', {
                                        variant: 'warning',
                                    });
                                    signOut();
                                } else if (error?.status === 403) {
                                    enqueueSnackbar('You do not have permission to perform this action', {
                                        variant: 'error',
                                    });
                                    push('/');
                                } else if (error?.status === 404) {
                                    enqueueSnackbar('The requested resource was not found', {
                                        variant: 'error',
                                    });
                                    push('/');
                                } else {
                                    enqueueSnackbar('An error occurred. Please try again later', {
                                        variant: 'error',
                                    });
                                }
                            },
                        }}
                    >
                        <PresenceContextProvider>
                            <Header />
                            <>{children}</>
                            <QuickActions />
                            <Footer />
                        </PresenceContextProvider>
                    </SWRConfig>
                </>
            ) : (
                <Grid container>
                    <Grid
                        item
                        md={4}
                        xs={12}
                        sx={{
                            pt: {
                                md: 10,
                                xs: 0,
                            },
                            mb: {
                                md: 0,
                                xs: 5,
                            },
                        }}
                    >
                        <Typography variant={'h3'}>Community Space 💬</Typography>
                        <Divider sx={{ mt: 3, mb: 3 }} />
                        <Typography variant={'body1'} color='text.secondary' sx={{ fontSize: 20, fontStyle: 'italic' }}>
                            A place for all your memos
                        </Typography>
                    </Grid>
                    <Grid item md={8} xs={12}>
                        <>{children}</>
                    </Grid>
                </Grid>
            )}
        </>
    );
};

export default Layout;
