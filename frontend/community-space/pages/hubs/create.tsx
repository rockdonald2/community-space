import { Alert, AlertTitle, Avatar, Button, Divider, Link, Stack, Typography } from '@mui/material';
import TextField from '@/components/TextField';
import Head from 'next/head';
import styles from '@/styles/CreateHub.module.scss';
import { Dispatch, SetStateAction, useCallback, useState } from 'react';
import { GATEWAY_URL } from '@/utils/Constants';
import { useAuthContext } from '@/utils/AuthContext';
import { useRouter } from 'next/router';

function CreateHub() {
    const { user } = useAuthContext();
    const { push } = useRouter();

    const [name, setNameInput] = useState<string>(null);
    const [description, setDescriptionInput] = useState<string>(null);

    const [error, setError] = useState<{ msg: string }>(null);

    const handleInput = useCallback(
        (e: React.ChangeEvent<HTMLInputElement>, setState: Dispatch<SetStateAction<string>>) => {
            setState(e.target.value);
        },
        []
    );

    const handleSubmit = useCallback(async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        try {
            if (name == null || name?.length < 3) {
                throw new Error('Name must be at least 3 characters long');
            }

            const res = await fetch(`${GATEWAY_URL}/api/v1/hubs`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${user?.token}`,
                },
                body: JSON.stringify({
                    name: name,
                    description: description,
                }),
            });

            if (!res.ok) {
                throw new Error('Failed to create hub due to bad response', {
                    cause: {
                        res,
                    },
                });
            }

            setError(null);
            push('/');
        } catch (err) {
            console.debug('Failed to create hub', err);
            setError({ msg: err.message });
        }
    }, []);

    return (
        <Stack className={styles.wrapper}>
            <Head>
                <title>Community Space | Create Hub</title>
            </Head>
            <Stack className={styles['avatar-wrapper']}>
                <Avatar className={styles['avatar-wrapper__avatar']}>H</Avatar>
                <Divider variant='middle' orientation='vertical' flexItem />
                <Typography variant='h5' component='h5' className={styles['avatar-wrapper__msg']}>
                    Create your own Hub
                </Typography>
            </Stack>
            {error && (
                <div className={styles.alert}>
                    <Alert severity='error'>
                        <AlertTitle>Oops!</AlertTitle>
                        {error.msg}
                    </Alert>
                </div>
            )}
            <form className={styles.form} onSubmit={handleSubmit}>
                <Stack className={styles['form__wrapper']}>
                    <TextField
                        label='Name'
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setNameInput)}
                    />
                    <TextField
                        label='Description'
                        handleInput={(e: React.ChangeEvent<HTMLInputElement>) => handleInput(e, setDescriptionInput)}
                        multiline
                    />
                    <Button
                        variant='outlined'
                        aria-label='create-hub'
                        type='submit'
                        size='large'
                        className={styles['form__button']}
                    >
                        Create Hub
                    </Button>
                </Stack>
            </form>
            <Divider flexItem className={styles['form__divider']} />
            <Button href='/' LinkComponent={Link} variant='text' className={styles['create__button']}>
                See the list of existing Hubs
            </Button>
        </Stack>
    );
}

export default CreateHub;
