import { UserShort } from '@/types/db.types';
import { checkIfError, swrMembersFetcherWithAuth } from '@/utils/Utility';
import { Typography, IconButton, Menu, Divider, MenuItem, ListItemIcon } from '@mui/material';
import Alerter from './Alerter';
import SkeletonLoader from './SkeletonLoader';
import Avatar from './Avatar';
import { ErrorResponse } from '@/types/types';
import { useState } from 'react';
import { useAuthContext } from '@/utils/AuthContext';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import { usePresenceContext } from '@/utils/PresenceContext';
import useSWR from 'swr';

const Members = ({ hubId, hubRole }: { hubId: string; hubRole: 'OWNER' | 'MEMBER' | 'PENDING' | 'NONE' }) => {
    const { presence } = usePresenceContext();
    const { user } = useAuthContext();

    const {
        data: hubMembers,
        error: hubMembersError,
        isLoading: hubMembersIsLoading,
        isValidating: hubMembersIsValidating,
    } = useSWR<UserShort[] | ErrorResponse>(
        { key: 'members', token: user.token, hubId: hubId },
        swrMembersFetcherWithAuth,
        { revalidateOnFocus: false, refreshWhenHidden: false, refreshWhenOffline: false }
    );

    const [menuAnchorEl, setMenuAnchorEl] = useState<null | HTMLElement>(null);
    const open = Boolean(menuAnchorEl);

    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setMenuAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setMenuAnchorEl(null);
    };

    return (
        <>
            <Typography mb={2} color='text.secondary' variant='h6'>
                Members
            </Typography>
            <Alerter
                isValidating={hubMembersIsValidating}
                isLoading={hubMembersIsLoading}
                data={hubMembers}
                error={hubMembersError}
                nrOfLayersInSkeleton={1}
            />
            {!hubMembersIsLoading &&
                !hubMembersIsValidating &&
                !checkIfError(hubMembers) &&
                (hubMembers as UserShort[]).map((user, idx) => (
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
                ))}
            <>
                <Menu
                    anchorEl={menuAnchorEl}
                    open={open}
                    onClose={handleClose}
                    transitionDuration={0}
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
                        <MenuItem onClick={handleClose}>
                            <ListItemIcon>
                                <PersonRemoveIcon fontSize='small' />
                            </ListItemIcon>
                            Remove from hub
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
