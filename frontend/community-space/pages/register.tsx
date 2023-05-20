import { Alert, Avatar, Button, Divider, Stack, Typography } from '@mui/material';
import Head from 'next/head';
import { useState, useCallback, Dispatch, SetStateAction } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import styles from '@/styles/Register.module.scss';
import { useAuthContext } from '@/utils/AuthContext';
import PasswordField from '@/components/PasswordField';
import TextField from '@/components/TextField';
import { useSnackbar } from 'notistack';
import { handleInput } from '@/utils/Utility';

const EMAIL_REGEXP: RegExp = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

function Register() {
    const [emailInput, setEmailInput] = useState<string>(null);
    const [passwordInput, setPasswordInput] = useState<string>(null);
    const [confirmPasswordInput, setConfirmPasswordInput] = useState<string>(null);
    const [firstNameInput, setFirstNameInput] = useState<string>(null);
    const [lastNameInput, setLastNameInput] = useState<string>(null);
    const [isMalformedEmail, setIsMalformedEmail] = useState<boolean>(false);
    const [isMalformedPassword, setIsMalformedPassword] = useState<boolean>(false);
    const [isError, setIsError] = useState<boolean>(false);
    const [passwordsMatch, setPasswordsMatch] = useState<boolean>(true);
    const [namesMissing, setNamesMissing] = useState<boolean>(false);
    const { push } = useRouter();
    const { signUp } = useAuthContext();
    const { enqueueSnackbar } = useSnackbar();

    const handleSubmit = useCallback(
        async (e: React.FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            setIsError(false);

            const isPasswordOK: boolean = passwordInput && passwordInput.length >= 6;
            const isEmailOK: boolean = emailInput && EMAIL_REGEXP.test(emailInput);
            const doPasswordsMatch: boolean = confirmPasswordInput && passwordInput === confirmPasswordInput;
            const doNamesMissing: boolean = !firstNameInput || !lastNameInput;

            setIsMalformedPassword(!isPasswordOK);
            setIsMalformedEmail(!isEmailOK);
            setPasswordsMatch(doPasswordsMatch);
            setNamesMissing(doNamesMissing);

            if (!isPasswordOK || !isEmailOK || !doPasswordsMatch || doNamesMissing) return;

            const { error } = await signUp({
                email: emailInput,
                password: passwordInput,
                firstName: firstNameInput,
                lastName: lastNameInput,
            });

            if (error) {
                setIsError(true);
                enqueueSnackbar('An error occurred, please try again!', { variant: 'error' });
                return console.debug('Error happened while trying to sign up', error);
            }

            push('/');
        },
        [passwordInput, emailInput, confirmPasswordInput, firstNameInput, lastNameInput, signUp, push, enqueueSnackbar]
    );

    return (
        <Stack className={styles.wrapper}>
            <Head>
                <title>Community Space | Register</title>
            </Head>
            <Stack className={styles['avatar-wrapper']}>
                <Avatar className={styles['avatar-wrapper__avatar']}>U</Avatar>
                <Divider variant='middle' orientation='vertical' flexItem />
                <Typography variant='h5' component='h5' className={styles['avatar-wrapper__msg']}>
                    Register your account
                </Typography>
            </Stack>
            {isError && (
                <Alert severity='error' className={styles.alert}>
                    Something went wrong!
                </Alert>
            )}
            <form className={styles.form} onSubmit={handleSubmit}>
                <Stack className={styles['form__wrapper']}>
                    <TextField
                        helperText='Should be in format of smth@domain.ex'
                        isError={isMalformedEmail}
                        label='E-mail'
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setEmailInput)}
                    />
                    <PasswordField
                        isError={isMalformedPassword}
                        helperText='Should contain at least 6 characters'
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setPasswordInput)}
                        label='Password'
                    />
                    <PasswordField
                        isError={!passwordsMatch}
                        helperText='Should be the same as the previous password'
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) =>
                            handleInput(e, setConfirmPasswordInput)
                        }
                        label='Confirm Password'
                    />
                    <Stack
                        direction={'row'}
                        alignItems={'center'}
                        justifyContent={'flex-start'}
                        className={styles['form__wrapper--inner']}
                    >
                        <TextField
                            isError={namesMissing}
                            label='First Name'
                            inner
                            handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setFirstNameInput)}
                        />
                        <TextField
                            isError={namesMissing}
                            label='Last Name'
                            inner
                            handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setLastNameInput)}
                        />
                    </Stack>
                    <Button
                        variant='outlined'
                        aria-label='register'
                        type='submit'
                        size='large'
                        className={styles['form__button']}
                    >
                        Register
                    </Button>
                </Stack>
            </form>
            {(isMalformedPassword || isMalformedEmail || !passwordsMatch || namesMissing) && (
                <Alert severity='warning' className={styles.alert}>
                    Something went wrong — some fields are not filled or contain invalid data!
                </Alert>
            )}
            <Divider flexItem className={styles['form__divider']} />
            <Button href='/login' LinkComponent={Link} variant='text' className={styles['login__button']}>
                Already have an account? Login!
            </Button>
        </Stack>
    );
}

export default Register;
