import { IPresenceContext, UserPresence } from '@/types/types';
import { useContext, createContext, useState, useMemo, useEffect, useCallback } from 'react';
import { User } from '@/types/db.types';
import { useStompClient, useSubscription } from 'react-stomp-hooks';
import { useBeforeunload } from 'react-beforeunload';

const PresenceContext = createContext(null);
const usePresenceContext = () => useContext<IPresenceContext>(PresenceContext);

const PresenceContextProvider = ({ children, user }: { children: React.ReactNode; user: User }) => {
    const [presence, setPresence] = useState<UserPresence[]>(null);

    useSubscription('/wb/status-broadcast', (msg) => setPresence(JSON.parse(msg.body) as UserPresence[]));

    const stompClient = useStompClient();

    const ping = useCallback(
        (status: 'ONLINE' | 'OFFLINE') => {
            try {
                stompClient?.publish({
                    destination: '/ws/status-notify',
                    body: JSON.stringify({
                        email: user.email,
                        status,
                    } as UserPresence),
                });
            } catch (err) {
                console.debug('error', err);
            }
        },
        [stompClient, user.email]
    );

    const pingActive = useCallback(() => ping('ONLINE'), [ping]);
    const pingInactive = useCallback(() => ping('OFFLINE'), [ping]);

    useEffect(() => {
        pingActive();

        const presenceIntervalId = setInterval(() => {
            pingActive();
        }, 1000 * 15); // every 15 seconds

        return () => {
            pingInactive();
            clearInterval(presenceIntervalId);
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [stompClient]);

    useBeforeunload(() => pingInactive());

    const provided = useMemo<IPresenceContext>(
        () => ({
            presence,
        }),
        [presence]
    );

    return <PresenceContext.Provider value={provided}>{children}</PresenceContext.Provider>;
};

export { PresenceContext, usePresenceContext, PresenceContextProvider as default };
