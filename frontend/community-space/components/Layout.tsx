import { useAuthContext } from '@/utils/AuthContext';
import Header from './Header';
import { useColorScheme, useMediaQuery } from '@mui/material';
import { useEffect } from 'react';
import QuickActions from './QuickActions';
import PresenceMessagingWrapper from '@/utils/MessagingWrapper';
import { GATEWAY_ACCOUNT_WS } from '@/utils/Constants';

const Layout = ({ children }) => {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const { setMode } = useColorScheme();
    const { isAuthenticated } = useAuthContext();

    useEffect(() => {
        setMode(prefersDarkMode ? 'dark' : 'light');
    }, [prefersDarkMode, setMode]);

    return (
        <>
            {isAuthenticated ? (
                <>
                    <PresenceMessagingWrapper url={GATEWAY_ACCOUNT_WS}>
                        <Header />
                        <>{children}</>
                        <QuickActions />
                    </PresenceMessagingWrapper>
                </>
            ) : (
                <>{children}</>
            )}
        </>
    );
};

export default Layout;
