import { MemoShort } from '@/types/db.types';
import { Theme } from '@mui/material';
import { GATEWAY_URL } from './Constants';

export const sortByUrgency = (m1: MemoShort, m2: MemoShort) => {
    if (m1.urgency === 'URGENT') return -1; // if any of them is urgent, be it first
    if (m2.urgency === 'URGENT') return 1;

    if (m1.urgency === 'HIGH') return -1;
    if (m2.urgency === 'HIGH') return 1;

    if (m1.urgency === 'MEDIUM') return -1;
    if (m2.urgency === 'MEDIUM') return 1;

    return 0;
};

export const sortByCreationDate = (m1: MemoShort, m2: MemoShort) => {
    if (m1.createdOn < m2.createdOn) return 1;
    if (m1.createdOn > m2.createdOn) return -1;

    return 0;
};

/**
 * Create bold style based on the condition that the element is present inside a list.
 * @param elem
 * @param container
 * @param theme
 * @returns bold style if the elem is present inside of the container
 */
export const boldSelectedElementStyle = (elem: string, container: readonly string[], theme: Theme) => {
    return {
        fontWeight:
            container.indexOf(elem) === -1 ? theme.typography.fontWeightRegular : theme.typography.fontWeightBold,
    };
};

/**
 * Retrieve the recent memos from the repository with the given token. Recent memos are those that were created after yesterday.
 * @param args the user's token
 * @returns the recent memos from the repository
 */
export const swrRecentMemosFetcherWithAuth = async (args: { key: string; token: string; hubId?: string }) => {
    const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
    let url = `${GATEWAY_URL}/api/v1/memos?createdAfter=${yesterday}`;

    if (args.hubId) {
        url = url.concat(`&hubId=${args.hubId}`);
    }

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    return await res.json();
};

/**
 * Retrieve the hubs from the repository with the given token.
 * @param args the user's token
 * @returns the hubs from the repository
 */
export const swrHubsFetcherWithAuth = async (args: { key: string; token: string }) => {
    const url = `${GATEWAY_URL}/api/v1/hubs`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    return await res.json();
};

/**
 * Retrieve the hub from the repository with the given token.
 * @param args the user's token and the hub's id
 * @returns the hub from the repository
 */
export const swrHubFetcherWithAuth = async (args: { key: string; token: string; hubId: string }) => {
    const url = `${GATEWAY_URL}/api/v1/hubs/${args.hubId}`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    return await res.json();
};

/**
 * Retrieves the pending members of a hub.
 * @param args the user's token and the hub's id
 * @returns the pending members of a hub
 */
export const swrWaitersFetcherWithAuth = async (args: { key: string; token: string; hubId: string }) => {
    const url = `${GATEWAY_URL}/api/v1/hubs/${args.hubId}/waiters`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    return await res.json();
};

/**
 * Retrieves the members of a hub.
 * @param args the user's token and the hub's id
 * @returns the members of a hub
 */
export const swrMembersFetcherWithAuth = async (args: { key: string; token: string; hubId: string }) => {
    const url = `${GATEWAY_URL}/api/v1/hubs/${args.hubId}/members`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    return await res.json();
};

export const mediumDateWithNoTimeFormatter = new Intl.DateTimeFormat('en-gb', {
    formatMatcher: 'best fit',
    dateStyle: 'medium',
});

/**
 * Checks if a returned payload is of type ErrorResponse
 * @param data
 * @returns
 */
export const checkIfError = (data: any): boolean => {
    return data === undefined || (data && typeof data === 'object' && 'status' in data);
};
