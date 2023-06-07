import { Alert, AlertTitle, Avatar, Button, Chip, Divider, Stack, Typography } from '@mui/material';
import TextField from '@/components/TextField';
import Head from 'next/head';
import styles from '@/styles/CreateHub.module.scss';
import { useCallback, useState } from 'react';
import { GATEWAY_URL } from '@/utils/Constants';
import { useAuthContext } from '@/utils/AuthContext';
import { useRouter } from 'next/router';
import { useSnackbar } from 'notistack';
import { handleInput } from '@/utils/Utility';
import Breadcrumbs from '@/components/Breadcrumbs';
import Link from 'next/link';

function CreateHub() {
    const { user, signOut } = useAuthContext();
    const { push } = useRouter();
    const { enqueueSnackbar } = useSnackbar();

    const [name, setNameInput] = useState<string>(null);
    const [description, setDescriptionInput] = useState<string>(null);

    const [error, setError] = useState<{ msg: string }>(null);

    const handleSubmit = useCallback(
        async (e: React.FormEvent<HTMLFormElement>) => {
            e.preventDefault();

            try {
                if (name == null || name?.length < 3) {
                    throw new Error('Name must be at least 3 characters long', {
                        cause: {},
                    });
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
                setError({ msg: err.message });
                console.debug('Failed to create hub', err);
                if (err instanceof Error) {
                    if ('res' in (err?.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 409) {
                            enqueueSnackbar('Hub with the same name already exists', { variant: 'warning' });
                        } else if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to create hub', { variant: 'error' });
                        }
                    }
                }
            }
        },
        [description, enqueueSnackbar, name, push, signOut, user?.token]
    );

    return (
        <>
            <Head>
                <title>Community Space | Create Hub</title>
            </Head>
            <Breadcrumbs currRoute={{ name: 'Create' }} />
            <Stack className={styles.wrapper}>
                <Stack className={styles['avatar-wrapper']}>
                    <Avatar className={styles['avatar-wrapper__avatar']}>H</Avatar>
                    <Divider variant='middle' orientation='vertical' flexItem />
                    <Typography variant='h5' component='h5' className={styles['avatar-wrapper__msg']}>
                        Create your own Hub
                    </Typography>
                </Stack>
                <Stack>
                    <Typography
                        variant='body1'
                        component='p'
                        className={styles['avatar-wrapper__msg']}
                        color='text.secondary'
                    >
                        Create a Hub to share your memos with others
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
                            handleInput={(e: React.ChangeEvent<HTMLInputElement>) =>
                                handleInput(e, setDescriptionInput)
                            }
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
                <Divider flexItem className={styles['form__divider']}>
                    <Chip label='OR' />
                </Divider>
                <Button href='/' LinkComponent={Link} variant='text' className={styles['create__button']}>
                    See the list of existing Hubs
                </Button>
            </Stack>
        </>
    );
}

export default CreateHub;
