export type User = {
    email: string;
    firstName: string;
    lastName: string;
    token?: string;
};

export type UserShort = {
    email: string;
};

export type Memo = {
    id?: string;
    author: string;
    content?: string;
    createdOn: Date;
    visibility: 'PUBLIC' | 'PRIVATE';
};

export type MemoShort = {
    id?: string;
    author: string;
    createdOn: Date;
    visibility: 'PUBLIC' | 'PRIVATE';
};
