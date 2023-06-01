import { UserShort } from '@/types/db.types';
import { swrMembersFetcherWithAuth } from '@/utils/Utility';
import { Typography, IconButton, Menu, Divider, MenuItem, ListItemIcon } from '@mui/material';
import Alerter from './Alerter';
import SkeletonLoader from './SkeletonLoader';
import Avatar from './Avatar';
import { useCallback, useMemo, useState } from 'react';
import { useAuthContext } from '@/utils/AuthContext';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import { usePresenceContext } from '@/utils/PresenceContext';
import useSWR, { useSWRConfig } from 'swr';
import { GATEWAY_URL } from '@/utils/Constants';
import { useSnackbar } from 'notistack';

const Members = ({ hubId, hubRole }: { hubId: string; hubRole: 'OWNER' | 'MEMBER' | 'PENDING' | 'NONE' }) => {
    const { presence } = usePresenceContext();
    const { user, signOut } = useAuthContext();
    const { mutate } = useSWRConfig();
    const { enqueueSnackbar } = useSnackbar();

    const {
        data: hubMembers,
        error: hubMembersError,
        isLoading: hubMembersIsLoading,
        isValidating: hubMembersIsValidating,
    } = useSWR<UserShort[]>({ key: 'members', token: user.token, hubId: hubId }, swrMembersFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    const [menuAnchorEl, setMenuAnchorEl] = useState<null | HTMLElement>(null);
    const open = useMemo(() => Boolean(menuAnchorEl), [menuAnchorEl]);
    const handleClick = useCallback((event: React.MouseEvent<HTMLElement>) => {
        setMenuAnchorEl(event.currentTarget);
    }, []);
    const handleClose = useCallback(() => {
        setMenuAnchorEl(null);
    }, []);

    const handleRemoveMember = useCallback(
        async (memberUser: UserShort) => {
            try {
                const res = await fetch(`${GATEWAY_URL}/api/v1/hubs/${hubId}/members/${memberUser.email}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                    },
                });

                if (!res.ok) {
                    throw new Error('Failed to delete member', { cause: res });
                }

                mutate({ key: 'members', token: user.token, hubId: hubId });
            } catch (err) {
                console.debug('Failed to delete member', err);
                if (err instanceof Error) {
                    if ('res' in (err.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to delete member', { variant: 'error' });
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
                Members {hubMembers?.length ? `(${hubMembers?.length})` : '(0)'}
            </Typography>
            {!hubMembersIsLoading && !hubMembersIsValidating && !hubMembersError ? (
                hubMembers.map((user, idx) => (
                    <IconButton
                        key={idx}
                        aria-haspopup='true'
                        size='small'
                        onClick={handleClick}
                        sx={{ cursor: 'pointer' }}
                        data-user={user.email}
                    >
                        <Avatar
                            style={{ cursor: 'pointer' }}
                            user={user}
                            generateRandomColor
                            isOnline={presence?.filter((p) => p.email === user.email).length > 0}
                        />
                    </IconButton>
                ))
            ) : (
                <Alerter
                    isValidating={hubMembersIsValidating}
                    isLoading={hubMembersIsLoading}
                    data={hubMembers}
                    error={hubMembersError}
                    nrOfLayersInSkeleton={1}
                />
            )}
            <>
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
                    <Typography sx={{ padding: 1, textAlign: 'left', mb: 0.5 }} variant='subtitle1' color='text.secondary'>
                        {menuAnchorEl?.dataset.user || <SkeletonLoader nrOfLayers={1} />}
                    </Typography>
                    <Divider sx={{ mb: 0.5 }} />
                    {menuAnchorEl?.dataset.user && menuAnchorEl?.dataset.user !== user.email && hubRole === 'OWNER' ? (
                        <MenuItem
                            onClick={() => {
                                handleClose();
                                handleRemoveMember({ email: menuAnchorEl?.dataset.user });
                            }}
                        >
                            <ListItemIcon>
                                <PersonRemoveIcon fontSize='small' />
                            </ListItemIcon>
                            <Typography variant='body2'>Remove member</Typography>
                        </MenuItem>
                    ) : (
                        <Typography
                            sx={{ padding: 1, textAlign: 'left', mb: 0.5 }}
                            variant='body2'
                            color='text.secondary'
                        >
                            No available actions
                        </Typography>
                    )}
                </Menu>
            </>
        </>
    );
};

export default Members;
