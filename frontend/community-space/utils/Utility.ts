import { MemoShort } from '@/types/db.types';
import { SelectChangeEvent, Theme } from '@mui/material';
import { GATEWAY_URL } from './Constants';
import { ErrorResponse } from '@/types/types';
import { Dispatch, SetStateAction } from 'react';

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
export const swrRecentMemosFetcherWithAuth = async (args: {
    key: string;
    token: string;
    hubId?: string;
    page: number;
}) => {
    const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
    let url = `${GATEWAY_URL}/api/v1/memos?createdAfter=${yesterday}&page=${args.page}`;

    if (args.hubId) {
        url = url.concat(`&hubId=${args.hubId}`);
    }

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

    let totalCount = -1;
    let totalPages = -1;

    if (res.headers.get('X-TOTAL-COUNT')) {
        totalCount = parseInt(res.headers.get('X-TOTAL-COUNT'));
    }

    if (res.headers.get('X-TOTAL-PAGES')) {
        totalPages = parseInt(res.headers.get('X-TOTAL-PAGES'));
    }

    const content = await res.json();

    return {
        content,
        totalCount,
        totalPages,
    };
};

export const swrMemosFetcherWithAuth = async (args: { key: string; token: string; hubId?: string; page: number }) => {
    let url = `${GATEWAY_URL}/api/v1/memos?page=${args.page}`;

    if (args.hubId) {
        url = url.concat(`&hubId=${args.hubId}`);
    }

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

    let totalCount = -1;
    let totalPages = -1;

    if (res.headers.get('X-TOTAL-COUNT')) {
        totalCount = parseInt(res.headers.get('X-TOTAL-COUNT'));
    }

    if (res.headers.get('X-TOTAL-PAGES')) {
        totalPages = parseInt(res.headers.get('X-TOTAL-PAGES'));
    }

    const content = await res.json();

    return {
        content,
        totalCount,
        totalPages,
    };
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

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

    return await res.json();
};

/**
 * Retrieve the hub from the repository with the given token.
 * @param args the user's token and the hub's id
 * @returns the hub from the repository
 */
export const swrHubFetcherWithAuth = async (args: { key: string; token: string; hubId: string }) => {
    if (!args.hubId) {
        return {};
    }

    const url = `${GATEWAY_URL}/api/v1/hubs/${args.hubId}`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

    return await res.json();
};

/**
 * Retrieves the pending members of a hub.
 * @param args the user's token and the hub's id
 * @returns the pending members of a hub
 */
export const swrWaitersFetcherWithAuth = async (args: { key: string; token: string; hubId: string }) => {
    if (!args.hubId) {
        return [];
    }

    const url = `${GATEWAY_URL}/api/v1/hubs/${args.hubId}/waiters`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

    return await res.json();
};

/**
 * Retrieves the members of a hub.
 * @param args the user's token and the hub's id
 * @returns the members of a hub
 */
export const swrMembersFetcherWithAuth = async (args: { key: string; token: string; hubId: string }) => {
    if (!args.hubId) {
        return [];
    }

    const url = `${GATEWAY_URL}/api/v1/hubs/${args.hubId}/members`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

    return await res.json();
};

/**
 * Retrieves a memo of a hub.
 * @param args the user's token and the memo's id
 * @returns the memo of a hub
 * @throws ErrorResponse if the request fails
 */
export const swrMemoFetcherWithAuth = async (args: { key: string; token: string; memoId: string }) => {
    if (!args.memoId) {
        return {};
    }

    const res = await fetch(`${GATEWAY_URL}/api/v1/memos/${args.memoId}`, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

    return await res.json();
};

export const swrExploreHubFetcherWithAuth = async (args: { key: string; token: string; role: 'OWNER' | 'MEMBER' }) => {
    const res = await fetch(`${GATEWAY_URL}/api/v1/hubs?role=${args.role}`, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    if (!res.ok) {
        throw {
            status: res.status,
            path: res.url,
            message: res.statusText,
            error: 'error',
        } satisfies ErrorResponse;
    }

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

export const handleMultiSelectChange = (event: SelectChangeEvent<any>, setState: Dispatch<SetStateAction<any>>) => {
    const {
        target: { value },
    } = event;

    setState(typeof value === 'string' ? value.split(',') : value);
};

export const handleInput = (e: React.ChangeEvent<HTMLInputElement>, setState: Dispatch<SetStateAction<string>>) => {
    setState(e.target.value);
};

export const getDaysInCurrentMonth = () => {
    var now = new Date();
    return new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
};
