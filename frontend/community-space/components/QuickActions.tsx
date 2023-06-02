import LogoutIcon from '@mui/icons-material/Logout';
import HomeIcon from '@mui/icons-material/Home';
import GroupIcon from '@mui/icons-material/Group';
import ExploreIcon from '@mui/icons-material/Explore';
import { Box, SpeedDial, SpeedDialAction, SpeedDialIcon } from '@mui/material';
import { useRouter } from 'next/router';
import { useState, useCallback } from 'react';
import { QuickActionActionType, QuickActionType } from '@/types/types';
import { useAuthContext } from '@/utils/AuthContext';
import LocalActivityIcon from '@mui/icons-material/LocalActivity';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';

const quickActions: QuickActionType[] = [
    { icon: <LogoutIcon />, name: 'Logout', action: 'signout' },
    { icon: <HomeIcon />, name: 'Back to home', action: 'backToHome' },
    { icon: <GroupIcon />, name: 'Create Hub', action: 'createHub' },
    { icon: <ExploreIcon />, name: 'Explore Hubs', action: 'explore' },
    { icon: <LocalActivityIcon />, name: 'Activity', action: 'activity' },
];

const QuickActions = () => {
    const { push } = useRouter();
    const [open, setOpen] = useState<boolean>(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const { signOut } = useAuthContext();

    const handleAction = useCallback(
        async (_e: React.MouseEvent<HTMLDivElement>, action: QuickActionActionType) => {
            switch (action) {
                case 'signout': {
                    signOut();
                    push('/login');
                    break;
                }
                case 'backToHome': {
                    push('/');
                    break;
                }
                case 'createHub': {
                    push('/hubs/create');
                    break;
                }
                case 'explore': {
                    push('/hubs/explore');
                    break;
                }
                case 'activity': {
                    push('/activity');
                    break;
                }
            }
        },
        [push, signOut]
    );

    return (
        <Box sx={{ flexGrow: 1, position: 'fixed', bottom: '5%', right: '10%' }}>
            <SpeedDial
                ariaLabel='Quick actions dial'
                icon={<SpeedDialIcon icon={<MenuIcon />} openIcon={<CloseIcon />} />}
                onClose={handleClose}
                onOpen={handleOpen}
                open={open}
            >
                {quickActions.map((action) => (
                    <SpeedDialAction
                        key={action.name}
                        icon={action.icon}
                        tooltipTitle={action.name}
                        onClick={(e: React.MouseEvent<HTMLDivElement>) => handleAction(e, action.action)}
                    />
                ))}
            </SpeedDial>
        </Box>
    );
};

export default QuickActions;
