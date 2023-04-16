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
export type Urgency = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT' | '';

export type Memo = {
    id?: string;
    title: string;
    author: string;
    content?: string;
    createdOn: Date;
    visibility: Visibility;
    urgency: Urgency;
};

export type MemoShort = {
    id?: string;
    title: string;
    author: string;
    createdOn: Date;
    visibility: Visibility;
    urgency: Urgency;
};
