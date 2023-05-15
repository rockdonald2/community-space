import { StompSessionProvider } from 'react-stomp-hooks';
import { useAuthContext } from './AuthContext';
import PresenceContextProvider from './PresenceContext';

const PresenceMessagingWrapper = ({ children, url }: { children: React.ReactNode; url: string }) => {
    const { user } = useAuthContext();

    if (!user) {
        return <>{children}</>;
    }

    return (
        <StompSessionProvider
            url={url}
            connectHeaders={{ Authorization: `Bearer ${user.token}` }}
            disconnectHeaders={{ Authorization: `Bearer ${user.token}` }}
        >
            <PresenceContextProvider user={user}>{children}</PresenceContextProvider>
        </StompSessionProvider>
    );
};

export { PresenceMessagingWrapper as default };
