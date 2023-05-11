import { StompSessionProvider } from 'react-stomp-hooks';
import { useAuthContext } from './AuthContext';

const MessagingWrapper = ({
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
            {children}
        </StompSessionProvider>
    );
};

export { MessagingWrapper as default };
