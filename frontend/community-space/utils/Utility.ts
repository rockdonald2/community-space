import { MemoShort } from '@/types/db.types';

export const isBrowser = typeof window !== 'undefined';
export const GATEWAY_WS = process.env.CS_GATEWAY_WS || 'http://localhost:8080/stomp';
export const GATEWAY_URL = process.env.CS_GATEWAY_URL || 'http://localhost:8080';

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
