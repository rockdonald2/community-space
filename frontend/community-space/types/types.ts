import { Notification, User } from './db.types';

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
    isInitialized: boolean;
    signOut: () => void;
    signUp: (user: UserSignUp) => Promise<{ user?: User; error?: { code: number; msg: string } }>;
    signIn: (user: UserSignIn) => Promise<{ user?: User; error?: { code: number; msg: string } }>;
}

export type QuickActionActionType = 'signout' | 'backToHome' | 'createHub' | 'explore' | 'activity';

export type QuickActionType = {
    icon: any;
    name: string;
    action: QuickActionActionType;
};

export type UserPresence = {
    email: string;
    lastSeen?: Date;
    status?: 'ONLINE' | 'OFFLINE';
    firstName?: string;
    lastName?: string;
};

export interface IPresenceContext {
    presence: UserPresence[];
    pingInactive: () => void;
    pingActive: () => void;
}

export interface ICrossContext {
    triggerReload: () => void;
}

export type ErrorResponse = {
    error: string;
    status: number;
    path: string;
    message?: string;
};

export interface INotificationContext {
    notifications: Notification[];
    markAsRead: (notification: Notification) => Promise<void>;
    isLoading: boolean;
    isValidating: boolean;
    error: any;
}
