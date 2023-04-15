import { User } from '@/types/db.types';
import { IUserContext, UserSignIn, UserSignInResponse, UserSignUp, UserSignUpResponse } from '@/types/types';
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { GATEWAY_URL } from './Utility';
import { useCrossContext } from './CrossContext';

const USER_DATA_LS = 'user-data';

const AuthContext = createContext(null);
const useAuthContext = () => useContext<IUserContext>(AuthContext);

const AuthContextProvider = ({ children }) => {
    const [user, setUser] = useState<User>(null);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const { triggerReload } = useCrossContext();

    useEffect(() => {
        // on every user state change, save the current state to LS

        if (user) {
            localStorage.setItem(USER_DATA_LS, JSON.stringify(user));
        }
    }, [user]);

    const signOut = useCallback(() => {
        const token = user?.token ?? null;
        const handleAsync = async () => {
            try {
                await fetch(`${GATEWAY_URL}/api/v1/auth/${token}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            } catch (err) {
                console.debug('Failed to sign out user', err);
            }
        };

        if (token) {
            handleAsync();
        }

        setUser(null);
        setIsAuthenticated(false);
        localStorage.removeItem(USER_DATA_LS);
        triggerReload();
    }, [triggerReload, user?.token]);

    useEffect(() => {
        // on component load read the user state from LS, validate it and set it if it is not yet expired

        const rawUser = JSON.parse(localStorage.getItem(USER_DATA_LS)) as User;

        // validate using the /api/v1/auth/** endpoint the found token

        const handleAsync = async () => {
            try {
                const validationRes = await fetch(`${GATEWAY_URL}/api/v1/auth/${rawUser.token}`);

                if (validationRes.ok) {
                    setUser(rawUser);
                    setIsAuthenticated(true);
                } else {
                    signOut();
                }
            } catch (err) {
                console.debug('Failed to send validation for user token', err);
            }
        };

        // if there is any captured user
        if (rawUser && rawUser.token) {
            handleAsync();
        }
    }, []);

    const signIn = useCallback(
        async (signInUser: UserSignIn): Promise<{ user?: User; error?: { code: number; msg: string } }> => {
            try {
                const loginRes = await fetch(`${GATEWAY_URL}/api/v1/auth`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        email: signInUser.email,
                        password: signInUser.password,
                    }),
                });

                if (!loginRes.ok) {
                    throw new Error('Failed to sign in due to bad response', {
                        cause: {
                            res: loginRes,
                        },
                    });
                }

                const resBody = (await loginRes.json()) as UserSignInResponse;

                setUser({
                    email: resBody.email,
                    firstName: resBody.firstName,
                    lastName: resBody.lastName,
                    token: resBody.token.data,
                });
                setIsAuthenticated(true);

                triggerReload();

                return {
                    user,
                    error: null,
                };
            } catch (err: any) {
                return {
                    user: null,
                    error: {
                        code: ((err as Error).cause as { res: Response })?.res?.status ?? null,
                        msg: (err as Error).message,
                    },
                };
            }
        },
        [triggerReload, user]
    );

    const signUp = useCallback(
        async (signUpUser: UserSignUp): Promise<{ user?: User; error?: { code: number; msg: string } }> => {
            try {
                const signUpRes = await fetch(`${GATEWAY_URL}/api/v1/users`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        email: signUpUser.email,
                        password: signUpUser.password,
                        lastName: signUpUser.lastName,
                        firstName: signUpUser.firstName,
                    }),
                });

                if (!signUpRes.ok) {
                    throw new Error('Failed to sign up due to bad response', {
                        cause: {
                            res: signUpRes,
                        },
                    });
                }

                const resBody = (await signUpRes.json()) as UserSignUpResponse;

                setUser({
                    email: resBody.email,
                    firstName: resBody.firstName,
                    lastName: resBody.lastName,
                    token: resBody.token.data,
                });
                setIsAuthenticated(true);

                triggerReload();

                return {
                    user,
                    error: null,
                };
            } catch (err) {
                return {
                    user: null,
                    error: {
                        code: ((err as Error).cause as { res: Response })?.res?.status ?? null,
                        msg: (err as Error).message,
                    },
                };
            }
        },
        [triggerReload, user]
    );

    const provided = useMemo<IUserContext>(
        () => ({
            user,
            isAuthenticated,
            signOut,
            signIn,
            signUp,
        }),
        [isAuthenticated, signIn, signOut, signUp, user]
    );

    return <AuthContext.Provider value={provided}>{children}</AuthContext.Provider>;
};

export { AuthContext, useAuthContext, AuthContextProvider as default };
