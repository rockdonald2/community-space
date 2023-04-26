import { StompSessionProvider } from 'react-stomp-hooks';

const MessagingWrapper = ({
    children,
    url,
    onCloseCallback,
}: {
    children: React.ReactNode;
    url: string;
    onCloseCallback?: (event: any) => void;
}) => {
    return (
        <StompSessionProvider url={url} onWebSocketClose={onCloseCallback}>
            {children}
        </StompSessionProvider>
    );
};

export { MessagingWrapper as default };
