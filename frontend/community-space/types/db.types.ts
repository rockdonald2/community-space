export type User = {
    email: string;
    firstName: string;
    lastName: string;
    token?: string;
};

export type Memo = {
    id?: string;
    author: string;
    content?: string;
    createdOn: Date;
    visibility: 'PUBLIC' | 'PRIVATE';
};
