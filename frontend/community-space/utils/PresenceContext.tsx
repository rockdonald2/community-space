import { IPresenceContext, UserPresence } from '@/types/types';
import { useContext, createContext, useState, useMemo, useEffect, useCallback, useRef } from 'react';
import { useBeforeunload } from 'react-beforeunload';
import { Socket, io } from 'socket.io-client';
import { useAuthContext } from './AuthContext';
import { GATEWAY_WS } from './Constants';

const PresenceContext = createContext(null);
const usePresenceContext = () => useContext<IPresenceContext>(PresenceContext);

const Events = {
    CONNECT: 'connect',
    DISCONNECT: 'disconnect',
    STATUS: 'status',
    NOTIFICATION: 'notification'
}

const PresenceContextProvider = ({ children }: { children: React.ReactNode }) => {
    const { user } = useAuthContext();
    const [isConnected, setConnected] = useState<boolean>(false);
    const socket = useRef<Socket>(null);
    const [presence, setPresence] = useState<UserPresence[]>(null);

    const onConnect = useCallback(() => {
        setConnected(true);
    }, []);

    const onDisconnect = useCallback(() => {
        setConnected(false);
    }, []);

    const onStatusBroadcast = useCallback((data: UserPresence[]) => {
        setPresence(data);
    }, []);

    useEffect(() => {
        socket.current = io(GATEWAY_WS, {
            transports: ['websocket'],
            autoConnect: true,
            reconnection: true,
            withCredentials: true,
            path: '/ws/account',
        });

        socket.current.on(Events.CONNECT, onConnect);
        socket.current.on(Events.DISCONNECT, onDisconnect);
        socket.current.on(Events.STATUS, onStatusBroadcast);

        return () => {
            socket.current.off(Events.CONNECT, onConnect);
            socket.current.off(Events.DISCONNECT, onDisconnect);
            socket.current.off(Events.STATUS, onStatusBroadcast);
            socket.current.disconnect();
        };
    }, [onStatusBroadcast, onConnect, onDisconnect, user.token]);

    const ping = useCallback(
        (status: 'ONLINE' | 'OFFLINE') => {
            if (isConnected) {
                socket.current.emit(Events.NOTIFICATION, { email: user.email, status });
            }
        },
        [isConnected, socket, user.email]
    );

    const pingActive = useCallback(() => ping('ONLINE'), [ping]);
    const pingInactive = useCallback(() => ping('OFFLINE'), [ping]);

    useEffect(() => {
        const presenceIntervalId = setInterval(() => {
            pingActive();
        }, 1000 * 15); // every 15 seconds report back as active

        return () => {
            clearInterval(presenceIntervalId);
        };
    }, [pingActive]);

    useBeforeunload(() => pingInactive());

    const provided = useMemo<IPresenceContext>(
        () => ({
            presence,
            pingInactive,
            pingActive,
        }),
        [presence, pingInactive, pingActive]
    );

    return <PresenceContext.Provider value={provided}>{children}</PresenceContext.Provider>;
};

export { PresenceContext, usePresenceContext, PresenceContextProvider as default };
