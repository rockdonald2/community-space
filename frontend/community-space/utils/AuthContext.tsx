import { User } from '@/types/db.types';
import {
    IUserContext,
    UserSignIn,
    UserSignInResponse,
    UserSignUp,
    UserSignUpResponse,
} from '@/types/types';
import {
    createContext,
    useCallback,
    useContext,
    useEffect,
    useMemo,
    useState,
} from 'react';

const GATEWAY_URL = process.env.CS_GATEWAY_URL || 'http://localhost:8080';

const AuthContext = createContext(null);
const useAuthContext = () => useContext<IUserContext>(AuthContext);

const AuthContextProvider = ({ children }) => {
    const [user, setUser] = useState<User>(null);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

    useEffect(() => {
        if (user) {
            localStorage.setItem('user-data', JSON.stringify(user));
        }
    }, [user]);

    useEffect(() => {
        const rawUser = JSON.parse(localStorage.getItem('user-data')) as User;
        setUser(rawUser);
        setIsAuthenticated(rawUser !== null && rawUser.token !== null);
    }, []);

    const signOut = useCallback(() => {
        setUser(null);
        setIsAuthenticated(false);
        localStorage.setItem('user-data', null);
    }, []);

    const signIn = useCallback(
        async (
            signInUser: UserSignIn
        ): Promise<{ user?: User; error?: { code: number; msg: string } }> => {
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

                return {
                    user,
                    error: null,
                };
            } catch (err: any) {
                return {
                    user: null,
                    error: {
                        code: ((err as Error).cause as { res: Response }).res
                            ?.status,
                        msg: (err as Error).message,
                    },
                };
            }
        },
        [user]
    );

    const signUp = useCallback(
        async (
            signUpUser: UserSignUp
        ): Promise<{ user?: User; error?: { code: number; msg: string } }> => {
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

                return {
                    user,
                };
            } catch (err) {
                return {
                    user: null,
                    error: {
                        code: ((err as Error).cause as { res: Response }).res
                            ?.status,
                        msg: (err as Error).message,
                    },
                };
            }
        },
        [user]
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

    return (
        <AuthContext.Provider value={provided}>{children}</AuthContext.Provider>
    );
};

export { AuthContext, useAuthContext, AuthContextProvider as default };
