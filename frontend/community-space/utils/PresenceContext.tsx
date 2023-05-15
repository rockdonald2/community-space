import { IPresenceContext, UserPresence } from '@/types/types';
import { useContext, createContext, useState, useMemo, useEffect, useCallback } from 'react';
import { User } from '@/types/db.types';
import { useStompClient, useSubscription } from 'react-stomp-hooks';
import { useBeforeunload } from 'react-beforeunload';

const PresenceContext = createContext(null);
const usePresenceContext = () => useContext<IPresenceContext>(PresenceContext);

const PresenceContextProvider = ({ children, user }: { children: React.ReactNode; user: User }) => {
    const [presence, setPresence] = useState<UserPresence[]>(null);
    const stompClient = useStompClient();

    useSubscription('/topic/status-broadcast', (msg) => setPresence(JSON.parse(msg.body) as UserPresence[]));

    const ping = useCallback(
        (status: 'ONLINE' | 'OFFLINE') => {
            try {
                stompClient?.publish({
                    destination: '/exchange/status-notify',
                    body: JSON.stringify({
                        email: user.email,
                        status,
                    } as UserPresence),
                });
            } catch (err) {
                console.debug('Failed to publish message to status socket', err);
            }
        },
        [stompClient, user.email]
    );

    const pingActive = useCallback(() => ping('ONLINE'), [ping]);
    const pingInactive = useCallback(() => ping('OFFLINE'), [ping]);

    useEffect(() => {
        const presenceIntervalId = setInterval(() => {
            pingActive();
        }, 1000 * 15); // every 15 seconds

        return () => {
            clearInterval(presenceIntervalId);
        };
    }, [pingActive, stompClient]);

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
