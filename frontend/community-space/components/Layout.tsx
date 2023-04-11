import { useAuthContext } from '@/utils/AuthContext';
import Header from './Header';
import { useColorScheme, useMediaQuery } from '@mui/material';
import { useEffect } from 'react';
import QuickActions from './QuickActions';

const Layout = ({ children }) => {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const { mode, setMode } = useColorScheme();
    const { isAuthenticated } = useAuthContext();

    useEffect(() => {
        setMode(prefersDarkMode ? 'dark' : 'light');
    }, [prefersDarkMode, setMode]);

    return (
        <>
            {isAuthenticated ? (
                <>
                    <Header />
                    <>{children}</>
                    <QuickActions />
                </>
            ) : (
                <>{children}</>
            )}
        </>
    );
};

export default Layout;
