import { StompSessionProvider } from 'react-stomp-hooks';
import { useAuthContext } from './AuthContext';
import PresenceContextProvider from './PresenceContext';

const PresenceMessagingWrapper = ({
    children,
    url,
    onCloseCallback,
}: {
    children: React.ReactNode;
    url: string;
    onCloseCallback?: (event: any) => void;
}) => {
    const { user } = useAuthContext();

    return (
        <StompSessionProvider
            url={url}
            onWebSocketClose={onCloseCallback}
            connectHeaders={{ Authorization: `Bearer ${user.token}` }}
        >
            <PresenceContextProvider user={user}>{children}</PresenceContextProvider>
        </StompSessionProvider>
    );
};

export { PresenceMessagingWrapper as default };
