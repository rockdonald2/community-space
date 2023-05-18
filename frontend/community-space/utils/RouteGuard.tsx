import { useRouter } from 'next/router';
import { useAuthContext } from './AuthContext';
import CircularLoading from '@/components/CircularLoading';
import { isBrowser } from './Constants';
import { useMemo } from 'react';

const pathsThatRequireLogin = ['^/$', '^/hubs/create$', '^/hubs/explore$', '^/hubs/.*$'];
const pathsThatRequireNoLogin = ['^/login$', '^/register$'];

export const RouteGuard = ({ children }) => {
    const { push, asPath } = useRouter();
    const { isAuthenticated, isInitialized } = useAuthContext();

    const currentPath = asPath.split('?')[0];

    // regexp to match the current path with the paths that require login
    const isCurrentPathProtected = useMemo(
        () => new RegExp(pathsThatRequireLogin.join('|')).test(currentPath),
        [currentPath]
    );

    // regexp to match the current path with the paths that require no login
    const isCurrentPathProtectedNoLogin = useMemo(
        () => new RegExp(pathsThatRequireNoLogin.join('|')).test(currentPath),
        [currentPath]
    );

    if (!isInitialized) {
        return <CircularLoading fullScreen={true} />; // if we don't know yet, whether the user has authenticated, just return a loading and wait
    }

    if (
        isBrowser && // required to only run this code client-side
        !isAuthenticated &&
        isCurrentPathProtected
    ) {
        push('/login');
    }

    if (isBrowser && isAuthenticated && isCurrentPathProtectedNoLogin) {
        push('/');
    }

    if (!isAuthenticated && isCurrentPathProtected) {
        return <CircularLoading fullScreen={true} />; // required because both on client-side and both on server-side we must return the same component type
    }

    if (isAuthenticated && isCurrentPathProtectedNoLogin) {
        return <CircularLoading fullScreen={true} />;
    }

    return children;
};
