import { useRouter } from 'next/router';
import { useAuthContext } from './AuthContext';
import CircularLoading from '@/components/CircularLoading';
import { isBrowser } from './Utility';

const pathsThatRequireLogin = ['/'];
const pathsThatRequireNoLogin = ['/login', '/register'];

export const RouteGuard = ({ children }) => {
    const { push, asPath } = useRouter();
    const { isAuthenticated } = useAuthContext();

    const currentPath = asPath.split('?')[0];

    if (
        isBrowser && // required to only run this code client-side
        !isAuthenticated &&
        pathsThatRequireLogin.includes(currentPath)
    ) {
        push('/login');
    }

    if (isBrowser && isAuthenticated && pathsThatRequireNoLogin.includes(currentPath)) {
        push('/');
    }

    if (!isAuthenticated && pathsThatRequireLogin.includes(currentPath)) {
        return <CircularLoading fullScreen={true} />; // required because both on client-side and both on server-side we must return the same component type
    }

    if (isAuthenticated && pathsThatRequireNoLogin.includes(currentPath)) {
        return <CircularLoading fullScreen={true} />;
    }

    return children;
};
