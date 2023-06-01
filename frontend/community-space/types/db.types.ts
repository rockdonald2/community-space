export type User = {
    email: string;
    firstName: string;
    lastName: string;
    token?: string;
};

export type UserShort = {
    email: string;
};

export type Visibility = 'PUBLIC' | 'PRIVATE' | '';
export const visibilities: Visibility[] = ['PRIVATE', 'PUBLIC'];

export type Urgency = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT' | '';
export const urgencies: Urgency[] = ['URGENT', 'HIGH', 'MEDIUM', 'LOW'];

export type Memo = {
    id?: string;
    title: string;
    author: string;
    content?: string;
    createdOn: Date;
    visibility: Visibility;
    urgency: Urgency;
    hubId: string;
    completed?: boolean;
};

export type MemoShort = {
    id?: string;
    title: string;
    author: string;
    createdOn: Date;
    visibility: Visibility;
    urgency: Urgency;
    hubId: string;
};

export type Hub = {
    id?: string;
    name: string;
    description: string;
    createdOn: Date;
    owner: string;
    members?: string[];
    waiters?: string[];
    role?: 'OWNER' | 'MEMBER' | 'PENDING' | 'NONE';
};

export type HubShort = {
    id?: string;
    name: string;
    description: string;
    createdOn: Date;
    owner: string;
};

export type Activity = {
    id?: string;
    type: 'HUB_CREATED' | 'MEMO_CREATED' | 'MEMO_COMPLETED';
    hubId: string;
    hubName: string;
    memoId?: string;
    memoTitle?: string;
    user: string;
    date: Date;
};

export type ActivityGrouped = {
    groupNumber: number;
    count: number;
};

export type Completion = {
    memoId: string;
    hubId: string;
    user: string;
    completed: boolean;
};

export type Notification = {
    id: string;
    msg: string;
    createdAt: Date;
    isRead?: boolean;
};
