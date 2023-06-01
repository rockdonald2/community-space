import { useAuthContext } from '@/utils/AuthContext';
import Header from './Header';
import { useColorScheme, useMediaQuery } from '@mui/material';
import { useEffect } from 'react';
import QuickActions from './QuickActions';
import Blob from './Blob';
import { SWRConfig } from 'swr';
import { useSnackbar } from 'notistack';
import PresenceContextProvider from '@/utils/PresenceContext';
import { useRouter } from 'next/router';

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
                        </PresenceContextProvider>
                    </SWRConfig>
                </>
            ) : (
                <>{children}</>
            )}
        </>
    );
};

export default Layout;
