import { Hub, Hub as HubType } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { GATEWAY_URL } from '@/utils/Constants';
import { mediumDateWithNoTimeFormatter } from '@/utils/Utility';
import { Avatar, Box, Button, Card, CardContent, Chip, Divider, Typography } from '@mui/material';
import { teal } from '@mui/material/colors';
import Link from 'next/link';
import { useSnackbar } from 'notistack';
import { useCallback } from 'react';

const HubCard = ({ hub, mutateCallback }: { hub: HubType; mutateCallback: (hub: Hub) => void }) => {
    const { user, signOut } = useAuthContext();
    const { enqueueSnackbar } = useSnackbar();

    const handleJoinHub = useCallback(async () => {
        try {
            const resp = await fetch(`${GATEWAY_URL}/api/v1/hubs/${hub.id}/waiters`, {
                method: 'POST',
                body: JSON.stringify({ email: user.email }),
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${user.token}`,
                },
            });

            if (resp.ok) {
                mutateCallback({ ...hub, role: 'PENDING' });
            } else {
                throw new Error('Failed to add user to waiters list', {
                    cause: {
                        res: resp,
                    },
                });
            }
        } catch (err) {
            console.debug('Failed to add user to waiters list', err);
            if (err instanceof Error) {
                if ('res' in (err.cause as any)) {
                    const res = (err.cause as any).res;
                    if (res.status === 409) {
                        enqueueSnackbar('You are already in the waiters list', { variant: 'info' });
                    } else if (res.status === 401) {
                        enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                        signOut();
                    } else {
                        enqueueSnackbar('Failed to add you to the waiters list', { variant: 'error' });
                    }
                }
            }
        }
    }, [enqueueSnackbar, hub, mutateCallback, signOut, user.email, user.token]);

    return (
        <Card variant='elevation' elevation={1}>
            <>
                <CardContent>
                    <Typography variant='h5' component='div' align='center'>
                        {hub.name}
                    </Typography>
                    <Typography color='text.secondary' align='center'>
                        {hub.ownerName}
                    </Typography>
                    <Typography sx={{ mb: 1.5 }} color='text.secondary' variant='body2' align='center'>
                        ({hub.owner})
                    </Typography>
                    <Box alignItems={'center'} justifyContent={'center'} display={'flex'}>
                        <Avatar
                            sx={{
                                bgcolor: teal[Object.keys(teal).at(Math.random() * Object.keys(teal).length)],
                                width: 56,
                                height: 56,
                            }}
                            variant='square'
                        >
                            {hub.name.substring(0, 3).toUpperCase()}
                        </Avatar>
                    </Box>
                    <Typography sx={{ mt: 1.5 }} color='text.secondary' align='center'>
                        Since {mediumDateWithNoTimeFormatter.format(new Date(hub.createdOn))}
                    </Typography>
                </CardContent>
                <Divider />
                <Typography sx={{ mt: 1.5, mb: 1.5 }} color='text.secondary' align='center' variant='subtitle2'>
                    <Chip label={hub.role} variant='filled' />
                </Typography>
                <Divider />
                <Button
                    fullWidth
                    type='button'
                    variant='text'
                    href={hub.role !== 'NONE' && hub.role !== 'PENDING' ? `/hubs/${hub.id}` : null}
                    LinkComponent={Link}
                    disabled={hub.role === 'PENDING' ? true : false}
                    onClick={hub.role !== 'NONE' ? null : handleJoinHub}
                >
                    {hub.role === 'PENDING' ? 'Waiting...' : hub.role !== 'NONE' ? 'Enter' : 'Join'}
                </Button>
            </>
        </Card>
    );
};

export default HubCard;
