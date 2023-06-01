import { useCallback, useEffect, useRef, useState, useMemo } from 'react';
import { useAuthContext } from './AuthContext';
import { GATEWAY_URL, GATEWAY_WS } from './Constants';
import { Socket, io } from 'socket.io-client';
import { Notification } from '@/types/db.types';
import useSWR from 'swr';
import { swrNotificationsFetcherWithAuth } from '@/utils/Utility';
import { INotificationContext } from '@/types/types';

const Events = {
    CONNECT: 'connect',
    DISCONNECT: 'disconnect',
    NOTIFICATION: 'notification',
};

export function useNotifications() {
    const { user } = useAuthContext();
    const [_isConnected, setConnected] = useState<boolean>(false);
    const socket = useRef<Socket>(null);

    const { data, error, isLoading, isValidating, mutate } = useSWR<Notification[]>( // loading initial data with REST API
        { key: 'notifications', token: user.token },
        swrNotificationsFetcherWithAuth,
        {
            revalidateOnFocus: false,
            revalidateIfStale: false,
        }
    );

    const onConnect = useCallback(() => {
        setConnected(true);
    }, []);

    const onDisconnect = useCallback(() => {
        setConnected(false);
    }, []);

    const onNotification = useCallback(
        (notification: Notification) => {
            mutate([notification, ...data], false);
        },
        [data, mutate]
    );

    useEffect(() => {
        socket.current = io(GATEWAY_WS, {
            transports: ['websocket'],
            autoConnect: true,
            reconnection: true,
            withCredentials: true,
            path: '/ws/notifications',
        });

        socket.current.on(Events.CONNECT, onConnect);
        socket.current.on(Events.DISCONNECT, onDisconnect);
        socket.current.on(Events.NOTIFICATION, onNotification);

        return () => {
            socket.current.off(Events.CONNECT, onConnect);
            socket.current.off(Events.DISCONNECT, onDisconnect);
            socket.current.off(Events.NOTIFICATION, onNotification);
            socket.current.disconnect();
        };
    }, [onNotification, onConnect, onDisconnect, user.token]);

    const markAsRead = useCallback(
        async (notification: Notification) => {
            const res = await fetch(`${GATEWAY_URL}/api/v1/notifications/${notification.id}`, {
                method: 'PATCH',
                headers: { Authorization: `Bearer ${user.token}` },
            });

            if (!res.ok) {
                throw new Error('Failed to delete member', { cause: res });
            }

            let newNotifications = [...data];
            newNotifications[newNotifications.findIndex((n) => n.id === notification.id)] = {
                ...notification,
                isRead: true,
            };

            if (newNotifications.filter((notification) => notification.isRead !== true).length === 0) {
                await mutate([], false);
            } else {
                await mutate([...newNotifications], false); // do not revalidate after mutating the data, just refresh the state
            }
        },
        [data, mutate, user.token]
    );

    const provided = useMemo<INotificationContext>(
        () => ({
            notifications: data ?? [],
            isLoading,
            isValidating,
            error,
            markAsRead,
        }),
        [data, error, isLoading, isValidating, markAsRead]
    );

    return provided;
}
