import { useAuthContext } from '@/utils/AuthContext';
import Header from './Header';
import { useColorScheme, useMediaQuery } from '@mui/material';
import { useEffect } from 'react';
import QuickActions from './QuickActions';
import PresenceMessagingWrapper from '@/utils/PresenceMessagingWrapper';
import { GATEWAY_ACCOUNT_WS } from '@/utils/Constants';
import Blob from './Blob';
import { SWRConfig } from 'swr';

const Layout = ({ children }) => {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const { setMode } = useColorScheme();
    const { isAuthenticated, signOut } = useAuthContext();

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
                            onError: (error, key) => {
                                if (error?.status === 401) {
                                    signOut();
                                }
                            },
                        }}
                    >
                        <PresenceMessagingWrapper url={GATEWAY_ACCOUNT_WS}>
                            <Header />
                            <>{children}</>
                            <QuickActions />
                        </PresenceMessagingWrapper>
                    </SWRConfig>
                </>
            ) : (
                <>{children}</>
            )}
        </>
    );
};

export default Layout;
