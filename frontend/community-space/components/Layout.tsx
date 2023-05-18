import { useAuthContext } from '@/utils/AuthContext';
import Header from './Header';
import { useColorScheme, useMediaQuery } from '@mui/material';
import { useEffect } from 'react';
import QuickActions from './QuickActions';
import Blob from './Blob';
import { SWRConfig } from 'swr';
import { useSnackbar } from 'notistack';
import PresenceContextProvider from '@/utils/PresenceContext';

const Layout = ({ children }) => {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const { setMode } = useColorScheme();
    const { isAuthenticated, signOut } = useAuthContext();
    const { enqueueSnackbar } = useSnackbar();

    useEffect(() => {
        setMode(prefersDarkMode ? 'dark' : 'light');
    }, [prefersDarkMode, setMode]);

    return (
        <>
            <Blob />
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
