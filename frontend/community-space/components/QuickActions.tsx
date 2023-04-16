import LogoutIcon from '@mui/icons-material/Logout';
import HomeIcon from '@mui/icons-material/Home';
import { Box, SpeedDial, SpeedDialAction, SpeedDialIcon } from '@mui/material';
import { useRouter } from 'next/router';
import { useState, useCallback } from 'react';
import { QuickActionActionType, QuickActionType } from '@/types/types';
import { useAuthContext } from '@/utils/AuthContext';

const quickActions: QuickActionType[] = [
    { icon: <LogoutIcon />, name: 'Logout', action: 'signout' },
    { icon: <HomeIcon />, name: 'Back to home', action: 'backToHome' },
];

const QuickActions = () => {
    const { push } = useRouter();
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const { signOut } = useAuthContext();

    const handleAction = useCallback(async (_e: React.MouseEvent<HTMLDivElement>, action: QuickActionActionType) => {
        switch (action) {
            case 'signout': {
                await signOut();
                push('/login');
                break;
            }
            case 'backToHome': {
                push('/');
                break;
            }
        }
    }, []);

    return (
        <Box sx={{ transform: 'translateZ(0px)', flexGrow: 1 }}>
            <SpeedDial
                ariaLabel='Quick actions dial'
                sx={{ position: 'absolute', bottom: 0, right: '-10%' }}
                icon={<SpeedDialIcon />}
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
