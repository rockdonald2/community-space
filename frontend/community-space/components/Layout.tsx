import { useAuthContext } from '@/utils/AuthContext';
import Header from './Header';
import { useColorScheme, useMediaQuery } from '@mui/material';
import { useEffect } from 'react';
import QuickActions from './QuickActions';
import PresenceContextProvider from '@/utils/PresenceContext';
import MessagingWrapper from '@/utils/MessagingWrapper';

const Layout = ({ children }) => {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const { mode, setMode } = useColorScheme();
    const { user, isAuthenticated } = useAuthContext();

    useEffect(() => {
        setMode(prefersDarkMode ? 'dark' : 'light');
    }, [prefersDarkMode, setMode]);

    return (
        <>
            {isAuthenticated ? (
                <>
                    <MessagingWrapper>
                        <PresenceContextProvider user={user}>
                            <Header />
                            <>{children}</>
                            <QuickActions />
                        </PresenceContextProvider>
                    </MessagingWrapper>
                </>
            ) : (
                <>{children}</>
            )}
        </>
    );
};

export default Layout;
