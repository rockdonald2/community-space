import { StompSessionProvider } from 'react-stomp-hooks';

const MessagingWrapper = ({ children, url }) => {
    return <StompSessionProvider url={url}>{children}</StompSessionProvider>;
};

export { MessagingWrapper as default };
