import { BroadcastChannel, LeaderElector, createLeaderElection } from 'broadcast-channel';
import { useCallback, useEffect, useMemo, useRef } from 'react';
import { isBrowser } from './Utility';

/**
 * This hook is currently unused.
 * @returns vars and methods to wait and check for leadership, in case of multiple tabs open.
 */
export function useLeader() {
    const channel = useRef<BroadcastChannel<any>>();
    const leader = useRef<boolean>();
    const elector = useRef<LeaderElector>();

    const waitForLeader = useCallback(async () => {
        await elector.current.awaitLeadership();
    }, [channel]);
    const isLeader = useMemo(() => elector.current?.isLeader, [elector]);

    useEffect(() => {
        if (!isBrowser) return; // only run on the client-side

        channel.current = new BroadcastChannel('__useLeader__');
        elector.current = createLeaderElection(channel.current);

        elector.current.awaitLeadership().then(() => {
            // this tab is the leader now
            leader.current = true;
        });

        return () => {
            channel.current.close();
        };
    }, []);

    return {
        isLeader,
        waitForLeader,
    };
}
