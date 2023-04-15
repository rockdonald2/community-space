import { StompSessionProvider } from 'react-stomp-hooks';
import { GATEWAY_WS } from './Utility';

const MessagingWrapper = ({ children }) => {
    return (
        <StompSessionProvider url={GATEWAY_WS}>{children}</StompSessionProvider>
    );
};

export { MessagingWrapper as default };
