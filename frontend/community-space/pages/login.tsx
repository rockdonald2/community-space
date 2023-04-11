/* eslint-disable react/no-unescaped-entities */
import {
    Alert,
    AlertTitle,
    Avatar,
    Button,
    Divider,
    Stack,
    Typography,
} from '@mui/material';
import Head from 'next/head';
import { useState, useCallback, SetStateAction, Dispatch } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/router';
import styles from '@/styles/Login.module.scss';
import { useAuthContext } from '@/utils/AuthContext';
import PasswordField from '@/components/PasswordField';
import TextField from '@/components/TextField';

function Login() {
    const [emailInput, setEmailInput] = useState<string>(null);
    const [passwordInput, setPasswordInput] = useState<string>(null);
    const [isBadLogin, setIsBadLogin] = useState<boolean>(false);
    const { push } = useRouter();
    const { signIn } = useAuthContext();

    const handleInput = useCallback(
        (
            e: React.ChangeEvent<HTMLInputElement>,
            setState: Dispatch<SetStateAction<string>>
        ) => {
            setState(e.target.value);
        },
        []
    );

    const handleSubmit = useCallback(
        async (e: React.FormEvent<HTMLFormElement>) => {
            e.preventDefault();

            const { user, error } = await signIn({
                email: emailInput,
                password: passwordInput,
            });

            if (error) {
                setIsBadLogin(true);
                return console.error('error', error);
            }

            setIsBadLogin(false);
            push('/');
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [emailInput, passwordInput]
    );

    return (
        <Stack className={styles.wrapper}>
            <Head>
                <title>Community Space | Login</title>
            </Head>
            <Stack className={styles['avatar-wrapper']}>
                <Avatar className={styles['avatar-wrapper__avatar']}>U</Avatar>
                <Divider variant='middle' orientation='vertical' flexItem />
                <Typography
                    variant='h5'
                    component='h5'
                    className={styles['avatar-wrapper__msg']}
                >
                    Login with your account
                </Typography>
            </Stack>
            {isBadLogin && (
                <div className={styles.alert}>
                    <Alert severity='error'>
                        <AlertTitle>Oops!</AlertTitle>
                        There has been a problem, either wrong{' '}
                        <strong>username</strong> or <strong>password</strong>,
                        please try again!
                    </Alert>
                </div>
            )}
            <form className={styles.form} onSubmit={handleSubmit}>
                <Stack className={styles['form__wrapper']}>
                    <TextField
                        isError={isBadLogin}
                        label='E-mail'
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) =>
                            handleInput(e, setEmailInput)
                        }
                    />
                    <PasswordField
                        isError={isBadLogin}
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) =>
                            handleInput(e, setPasswordInput)
                        }
                        label='Password'
                    />
                    <Button
                        variant='outlined'
                        aria-label='register'
                        type='submit'
                        size='large'
                        className={styles['form__button']}
                    >
                        Login
                    </Button>
                </Stack>
            </form>
            <Divider flexItem className={styles['form__divider']} />
            <Button
                href='/register'
                LinkComponent={Link}
                variant='text'
                className={styles['register__button']}
            >
                You don't have an account yet? Register now!
            </Button>
        </Stack>
    );
}

export default Login;
