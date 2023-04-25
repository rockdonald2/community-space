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
export const swrRecentMemosFetcherWithAuth = async (args: { token: string }) => {
    const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
    const url = `${GATEWAY_URL}/api/v1/memos?createdAfter=${yesterday}`;

    const res = await fetch(url, {
        headers: { Authorization: `Bearer ${args.token}` },
    });

    return await res.json();
};
