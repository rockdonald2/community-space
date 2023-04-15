import { User } from './db.types';

export type UserSignUp = {
    email: string;
    lastName: string;
    firstName: string;
    password: string;
};

export type UserSignIn = {
    email: string;
    password: string;
};

export type UserSignUpResponse = {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    token: {
        data: string;
    };
};

export type UserSignInResponse = {
    token: {
        data: string;
    };
    firstName: string;
    lastName: string;
    email: string;
};

export interface IUserContext {
    user: User;
    isAuthenticated: boolean;
    signOut: () => void;
    signUp: (user: UserSignUp) => Promise<{ user?: User; error?: { code: number; msg: string } }>;
    signIn: (user: UserSignIn) => Promise<{ user?: User; error?: { code: number; msg: string } }>;
}

export type QuickActionActionType = 'signout' | 'backToHome';

export type QuickActionType = {
    icon: any;
    name: string;
    action: QuickActionActionType;
};

export type UserPresence = {
    email: string;
    lastSeen?: Date;
    status?: 'ONLINE' | 'OFFLINE';
};

export interface IPresenceContext {
    presence: UserPresence[];
}

export interface ICrossContext {
    triggerReload: () => void;
}
