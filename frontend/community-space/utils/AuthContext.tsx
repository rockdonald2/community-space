import { User } from '@/types/db.types';
import { IUserContext, UserSignIn, UserSignInResponse, UserSignUp, UserSignUpResponse } from '@/types/types';
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { GATEWAY_URL } from './Constants';
import { useCrossContext } from './CrossContext';
import { useCookies } from 'react-cookie';
import { useSnackbar } from 'notistack';

const USER_DATA_LS = 'user-data';

const AuthContext = createContext(null);
const useAuthContext = () => useContext<IUserContext>(AuthContext);

const AuthContextProvider = ({ children }) => {
    const [user, setUser] = useState<User>(null);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [isInitialized, setInitialized] = useState<boolean>(false);
    const { triggerReload } = useCrossContext();
    const [cookies, setCookie, removeCookie] = useCookies([USER_DATA_LS]);
    const { enqueueSnackbar } = useSnackbar();

    useEffect(() => {
        // on every user state change, save the current state to LS
        if (user) {
            setCookie(USER_DATA_LS, JSON.stringify(user), {
                path: '/',
                maxAge: 1440,
                sameSite: 'lax',
            });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user]);

    const signOut = useCallback(() => {
        const token = user?.token ?? null;
        const handleAsync = async () => {
            try {
                await fetch(`${GATEWAY_URL}/api/v1/sessions`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            } catch (err) {
                console.debug('Failed to sign out user', err);
                enqueueSnackbar('Failed to sign out', { variant: 'error' });
            }
        };

        if (token) {
            handleAsync();
        }

        setUser(null);
        setIsAuthenticated(false);
        removeCookie(USER_DATA_LS);
        triggerReload();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [triggerReload, user?.token]);

    useEffect(() => {
        // on component load read the user state from LS, validate it and set it if it is not yet expired
        const rawCookie = cookies[USER_DATA_LS] || null;
        let rawUser = null;

        if (typeof rawCookie === 'string') {
            rawUser = JSON.parse(cookies[USER_DATA_LS] || null) as User;
        } else if (typeof rawCookie === 'object') {
            rawUser = rawCookie as User;
        }

        // validate using the /api/v1/sessions/** endpoint the found token

        const handleAsync = async () => {
            try {
                const validationRes = await fetch(`${GATEWAY_URL}/api/v1/sessions`, {
                    headers: {
                        Authorization: `Bearer ${rawUser.token}`,
                    },
                });

                if (validationRes.ok) {
                    setUser(rawUser);
                    setIsAuthenticated(true);
                    enqueueSnackbar('Welcome back', { variant: 'success' });
                } else {
                    enqueueSnackbar('Your session has expired, please sign in', { variant: 'warning' });
                    signOut();
                }
            } catch (err) {
                console.debug('Failed to send validation for user token', err);
                enqueueSnackbar('Unexpected issue, please sign in', { variant: 'error' });
                signOut();
            } finally {
                setInitialized(true);
            }
        };

        // if there is any captured user
        if (rawUser?.token) {
            handleAsync();
        } else {
            setInitialized(true);
        }

        return () => {
            // on component unload, clear the user state
            setUser(null);
            setIsAuthenticated(false);
            setInitialized(false);
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const signIn = useCallback(
        async (signInUser: UserSignIn): Promise<{ user?: User; error?: { code: number; msg: string } }> => {
            try {
                const loginRes = await fetch(`${GATEWAY_URL}/api/v1/sessions`, {
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
                        code: ((err as Error)?.cause as { res: Response })?.res?.status ?? null,
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
                    if (signUpRes.status === 409) {
                        throw new Error('User with the same e-mail address already exists', {
                            cause: {
                                res: signUpRes,
                            },
                        });
                    } else {
                        throw new Error('Failed to sign up due to bad response', {
                            cause: {
                                res: signUpRes,
                            },
                        });
                    }
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
                        code: ((err as Error)?.cause as { res: Response })?.res?.status ?? null,
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
            isInitialized,
            signOut,
            signIn,
            signUp,
        }),
        [isAuthenticated, signIn, signOut, signUp, user, isInitialized]
    );

    return <AuthContext.Provider value={provided}>{children}</AuthContext.Provider>;
};

export { AuthContext, useAuthContext, AuthContextProvider as default };
