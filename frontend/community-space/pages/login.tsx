/* eslint-disable react/no-unescaped-entities */
import { Alert, AlertTitle, Avatar, Button, Chip, Divider, Stack, Typography } from '@mui/material';
import Head from 'next/head';
import { useState, useCallback } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/router';
import styles from '@/styles/Login.module.scss';
import { useAuthContext } from '@/utils/AuthContext';
import PasswordField from '@/components/PasswordField';
import TextField from '@/components/TextField';
import { useSnackbar } from 'notistack';
import { handleInput } from '@/utils/Utility';

function Login() {
    const [emailInput, setEmailInput] = useState<string>(null);
    const [passwordInput, setPasswordInput] = useState<string>(null);
    const [isBadLogin, setIsBadLogin] = useState<boolean>(false);
    const { push } = useRouter();
    const { signIn } = useAuthContext();
    const { enqueueSnackbar } = useSnackbar();

    const handleSubmit = useCallback(
        async (e: React.FormEvent<HTMLFormElement>) => {
            e.preventDefault();

            const { user: _, error } = await signIn({
                email: emailInput,
                password: passwordInput,
            });

            if (error) {
                setIsBadLogin(true);
                enqueueSnackbar('An error occurred, please try again!', { variant: 'error' });
                return console.debug('Error happened while trying to log in', error);
            }

            setIsBadLogin(false);
            push('/');
        },
        [emailInput, enqueueSnackbar, passwordInput, push, signIn]
    );

    return (
        <Stack className={styles.wrapper}>
            <Head>
                <title>Community Space | Login</title>
            </Head>
            <Stack className={styles['avatar-wrapper']}>
                <Avatar className={styles['avatar-wrapper__avatar']}>U</Avatar>
                <Divider variant='middle' orientation='vertical' flexItem />
                <Typography variant='h5' component='h5' className={styles['avatar-wrapper__msg']}>
                    Login with your account
                </Typography>
            </Stack>
            {isBadLogin && (
                <div className={styles.alert}>
                    <Alert severity='error'>
                        <AlertTitle>Oops!</AlertTitle>
                        There has been a problem, either wrong <strong>username</strong> or <strong>password</strong>,
                        please try again!
                    </Alert>
                </div>
            )}
            <form className={styles.form} onSubmit={handleSubmit}>
                <Stack className={styles['form__wrapper']}>
                    <TextField
                        isError={isBadLogin}
                        label='E-mail'
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setEmailInput)}
                    />
                    <PasswordField
                        isError={isBadLogin}
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setPasswordInput)}
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
            <Divider flexItem className={styles['form__divider']}>
                <Chip label='OR' />
            </Divider>
            <Button href='/register' LinkComponent={Link} variant='text' className={styles['login__button']}>
                You don't have an account yet? Register now!
            </Button>
        </Stack>
    );
}

export default Login;
