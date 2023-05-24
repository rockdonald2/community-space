import { Container, Divider, IconButton, ListItemIcon, Menu, MenuItem, Typography } from '@mui/material';
import Alerter from './Alerter';
import { UserShort } from '@/types/db.types';
import { swrWaitersFetcherWithAuth } from '@/utils/Utility';
import Avatar from './Avatar';
import useSWR, { useSWRConfig } from 'swr';
import { useCallback, useMemo, useState } from 'react';
import { useAuthContext } from '@/utils/AuthContext';
import { GATEWAY_URL } from '@/utils/Constants';
import SkeletonLoader from './SkeletonLoader';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import { useSnackbar } from 'notistack';

const Pendings = ({ hubId, hubRole }: { hubId: string; hubRole: 'OWNER' | 'MEMBER' | 'PENDING' | 'NONE' }) => {
    const { user, signOut } = useAuthContext();
    const { mutate } = useSWRConfig();
    const { enqueueSnackbar } = useSnackbar();

    const [menuAnchorEl, setMenuAnchorEl] = useState<null | HTMLElement>(null);
    const open = useMemo(() => Boolean(menuAnchorEl), [menuAnchorEl]);

    const handleClick = useCallback((event: React.MouseEvent<HTMLElement>) => {
        setMenuAnchorEl(event.currentTarget);
    }, []);

    const handleClose = useCallback(() => {
        setMenuAnchorEl(null);
    }, []);

    const {
        data: hubPendings,
        error: hubPendingsError,
        isLoading: hubPendingsIsLoading,
        isValidating: hubPendingsIsValidating,
    } = useSWR<UserShort[]>({ key: 'pendings', token: user.token, hubId: hubId }, swrWaitersFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    const handleJoinHub = useCallback(
        async ({ pendingMember }: { pendingMember: UserShort }) => {
            // we need to make 2 calls, one to delete it from the waiters club and add it to the members of a hub
            try {
                const waiterResp = await fetch(`${GATEWAY_URL}/api/v1/hubs/${hubId}/waiters/${pendingMember.email}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                    },
                });

                if (!waiterResp.ok) {
                    throw new Error('Failed to delete user from waiters list', {
                        cause: waiterResp,
                    });
                }

                const membersResp = await fetch(`${GATEWAY_URL}/api/v1/hubs/${hubId}/members`, {
                    method: 'POST',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email: pendingMember.email }),
                });

                if (!membersResp.ok) {
                    throw new Error('Failed to add user to members list', {
                        cause: membersResp,
                    });
                }

                mutate({ key: 'pendings', token: user.token, hubId: hubId });
                mutate({ key: 'members', token: user.token, hubId: hubId });
            } catch (err) {
                console.debug('Failed to delete user from waiters list or add user to members list', err);
                if (err instanceof Error) {
                    if ('res' in (err.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to delete user from waiters list or add user to members list', {
                                variant: 'error',
                            });
                        }
                    }
                }
            }
        },
        [enqueueSnackbar, hubId, mutate, signOut, user.token]
    );

    const handleDecline = useCallback(
        async ({ pendingMember }: { pendingMember: UserShort }) => {
            try {
                const res = await fetch(`${GATEWAY_URL}/api/v1/hubs/${hubId}/waiters/${pendingMember.email}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                    },
                });

                if (!res.ok) {
                    throw new Error('Failed to delete user from waiters list', {
                        cause: res,
                    });
                }

                mutate({ key: 'pendings', token: user.token, hubId: hubId });
            } catch (err) {
                console.debug('Failed to delete user from waiters list', err);
                if (err instanceof Error) {
                    if ('res' in (err.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to delete user from waiters list', { variant: 'error' });
                        }
                    }
                }
            }
        },
        [enqueueSnackbar, hubId, mutate, signOut, user.token]
    );

    return (
        <>
            <Typography mb={2} color='text.secondary' variant='h6'>
                Waiting members {hubPendings?.length && `(${hubPendings?.length})`}
            </Typography>
            <Alerter
                isValidating={hubPendingsIsValidating}
                isLoading={hubPendingsIsLoading}
                data={hubPendings}
                error={hubPendingsError}
                nrOfLayersInSkeleton={1}
            />
            {!hubPendingsIsLoading &&
                !hubPendingsIsValidating &&
                !hubPendingsError &&
                (hubPendings.length !== 0 ? (
                    <>
                        <Container sx={{ mb: 1.5 }}>
                            {hubPendings.map((user, idx) => (
                                <IconButton
                                    key={idx}
                                    aria-haspopup='true'
                                    size='small'
                                    onClick={handleClick}
                                    sx={{ cursor: 'pointer' }}
                                    data-user={user.email}
                                >
                                    <Avatar user={user} generateRandomColor cursor='pointer' />
                                </IconButton>
                            ))}
                        </Container>
                        <Typography color='text.secondary' variant='caption'>
                            Accept or decline the pending members
                        </Typography>
                    </>
                ) : (
                    <Typography variant='body2' color='text.secondary'>
                        No pending members
                    </Typography>
                ))}
            <Menu
                anchorEl={menuAnchorEl}
                open={open}
                onClose={handleClose}
                PaperProps={{
                    elevation: 0,
                    sx: {
                        overflow: 'visible',
                        filter: 'drop-shadow(0px 2px 6px rgba(0,0,0,0.18))',
                        mt: 1.5,
                        '&:before': {
                            content: '""',
                            display: 'block',
                            position: 'absolute',
                            top: 0,
                            right: 20,
                            width: 12,
                            height: 12,
                            bgcolor: 'background.paper',
                            transform: 'translateY(-45%) rotate(45deg)',
                            zIndex: 0,
                        },
                    },
                }}
                transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
                <Typography sx={{ padding: 1, textAlign: 'left', mb: 0.5 }} variant='body2' color='text.secondary'>
                    {menuAnchorEl?.dataset.user || <SkeletonLoader nrOfLayers={1} />}
                </Typography>
                <Divider sx={{ mb: 0.5 }} />
                {menuAnchorEl?.dataset.user && menuAnchorEl?.dataset.user !== user.email && hubRole === 'OWNER' ? (
                    <div>
                        <MenuItem
                            onClick={() => {
                                handleClose();
                                handleJoinHub({ pendingMember: { email: menuAnchorEl?.dataset.user } });
                            }}
                        >
                            <ListItemIcon>
                                <PersonAddIcon fontSize='small' />
                            </ListItemIcon>
                            <Typography variant='body2'>Accept member</Typography>
                        </MenuItem>
                        <MenuItem
                            onClick={() => {
                                handleClose();
                                handleDecline({ pendingMember: { email: menuAnchorEl?.dataset.user } });
                            }}
                        >
                            <ListItemIcon>
                                <PersonRemoveIcon fontSize='small' />
                            </ListItemIcon>
                            <Typography variant='body2'>Decline member</Typography>
                        </MenuItem>
                    </div>
                ) : (
                    <Typography sx={{ padding: 1, textAlign: 'left', mb: 0.5 }} variant='body2' color='text.secondary'>
                        No available actions
                    </Typography>
                )}
            </Menu>
        </>
    );
};

export default Pendings;
