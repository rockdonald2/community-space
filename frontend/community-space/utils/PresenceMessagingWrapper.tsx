import { StompSessionProvider } from 'react-stomp-hooks';
import { useAuthContext } from './AuthContext';
import PresenceContextProvider from './PresenceContext';
import { GATEWAY_ACCOUNT_WS } from './Constants';

const PresenceMessagingWrapper = ({ children }: { children: React.ReactNode }) => {
    const { user } = useAuthContext();

    if (!user) {
        return <>{children}</>;
    }

    return (
        <StompSessionProvider
            url={GATEWAY_ACCOUNT_WS}
            connectHeaders={{ Authorization: `Bearer ${user.token}` }}
            disconnectHeaders={{ Authorization: `Bearer ${user.token}` }}
        >
            <PresenceContextProvider user={user}>{children}</PresenceContextProvider>
        </StompSessionProvider>
    );
};

export { PresenceMessagingWrapper as default };
