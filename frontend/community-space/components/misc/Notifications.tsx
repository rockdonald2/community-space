import { Badge, Divider, Grid, IconButton, Menu, Paper, Stack, Tooltip, Typography, styled } from '@mui/material';
import { useState, useMemo, useCallback } from 'react';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useAuthContext } from '@/utils/AuthContext';
import { Notification } from '@/types/db.types';
import CheckIcon from '@mui/icons-material/Check';
import Alerter from '../layout/Alerter';
import { useSnackbar } from 'notistack';
import { useNotifications } from '@/utils/UseNotifications';
import { calculateRelativeTimeFromNow } from '@/utils/Utility';
import Avatar from './Avatar';

const Notifications = () => {
    const { signOut } = useAuthContext();
    const { enqueueSnackbar } = useSnackbar();

    const [menuAnchorEl, setMenuAnchorEl] = useState<null | HTMLElement>(null);
    const open = useMemo(() => Boolean(menuAnchorEl), [menuAnchorEl]);
    const handleClick = useCallback((event: React.MouseEvent<HTMLElement>) => {
        setMenuAnchorEl(event.currentTarget);
    }, []);
    const handleClose = useCallback(() => {
        setMenuAnchorEl(null);
    }, []);

    const { notifications, error, isLoading, isValidating, markAsRead } = useNotifications();

    const handleRead = useCallback(
        async (notification: Notification) => {
            try {
                await markAsRead(notification);
            } catch (err) {
                console.debug('Failed to mark notification as read', err);
                if (err instanceof Error) {
                    if ('res' in (err.cause as any)) {
                        const res = (err.cause as any).res;
                        if (res.status === 401) {
                            enqueueSnackbar('Your session has expired. Please sign in again', { variant: 'warning' });
                            signOut();
                        } else {
                            enqueueSnackbar('Failed to mark notification as read', { variant: 'error' });
                        }
                    }
                }
            }
        },
        [enqueueSnackbar, markAsRead, signOut]
    );

    return (
        <>
            <Tooltip title='Notifications' enterTouchDelay={0}>
                <IconButton
                    aria-controls={open ? 'notifications-menu' : undefined}
                    aria-haspopup='true'
                    aria-expanded={open ? 'true' : undefined}
                    onClick={handleClick}
                >
                    <Badge
                        variant='standard'
                        color='primary'
                        badgeContent={notifications?.filter((notification) => notification.isRead !== true).length}
                        max={15}
                    >
                        <NotificationsIcon />
                    </Badge>
                </IconButton>
            </Tooltip>
            <Menu
                anchorEl={menuAnchorEl}
                open={open}
                onClose={handleClose}
                PaperProps={{
                    elevation: 0,
                    sx: {
                        overflow: 'auto',
                        boxShadow: 'var(--mui-shadows-1)',
                        mt: 1.5,
                        maxHeight: '500px',
                        px: 1,
                    },
                }}
                transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
                <Paper sx={{ position: 'sticky', top: 0, width: '100%', zIndex: 1 }} elevation={0}>
                    <Typography
                        sx={{ padding: 1, paddingLeft: 2, paddingRight: 2, textAlign: 'left', mb: 0.5 }}
                        variant='subtitle1'
                        color='text.secondary'
                    >
                        Notifications
                    </Typography>
                    <Divider sx={{ mb: 2 }} />
                </Paper>
                {!isLoading && !isValidating && !error && notifications?.length > 0 ? (
                    notifications
                        ?.filter((notification) => notification.isRead !== true)
                        .map((notification, idx) => (
                            <Grid
                                container
                                key={idx}
                                sx={{
                                    maxWidth: {
                                        xs: '85vw',
                                        md: '35vw',
                                    },
                                }}
                                direction='row'
                                alignItems='center'
                            >
                                <Grid item xs={10}>
                                    <Grid container>
                                        <Grid
                                            item
                                            xs={2}
                                            alignSelf={'flex-start'}
                                            justifySelf={'center'}
                                            sx={{ width: '100%', pt: 0.5 }}
                                        >
                                            <div style={{ display: 'flex', justifyContent: 'center' }}>
                                                <Avatar
                                                    generateRandomColor
                                                    style={{
                                                        width: '30px',
                                                        height: '30px',
                                                        fontSize: '14px',
                                                    }}
                                                    user={{ email: notification.taker }}
                                                />
                                            </div>
                                        </Grid>
                                        <Grid item xs={10}>
                                            <Stack>
                                                <Typography
                                                    variant='body2'
                                                    sx={{
                                                        paddingLeft: 0.5,
                                                        paddingRight: 0.5,
                                                        width: '100%',
                                                    }}
                                                >
                                                    {notification.msg}
                                                </Typography>
                                                <Typography
                                                    variant='caption'
                                                    color='text.secondary'
                                                    sx={{
                                                        paddingLeft: 0.5,
                                                        paddingRight: 0.5,
                                                        paddingTop: 0.5,
                                                        textAlign: 'left',
                                                        mb: 1,
                                                    }}
                                                >
                                                    {calculateRelativeTimeFromNow(new Date(notification.createdAt))}
                                                </Typography>
                                            </Stack>
                                        </Grid>
                                    </Grid>
                                </Grid>
                                <Grid
                                    item
                                    xs={2}
                                    sx={{ display: 'flex', alignContent: 'center', justifyContent: 'center' }}
                                >
                                    <Tooltip arrow title='Mark as read' enterTouchDelay={0}>
                                        <IconButton
                                            size='small'
                                            color='primary'
                                            onClick={async () => await handleRead(notification)}
                                        >
                                            <CheckIcon />
                                        </IconButton>
                                    </Tooltip>
                                </Grid>
                            </Grid>
                        ))
                ) : isLoading || isValidating || error ? (
                    <Alerter data={notifications} error={error} isLoading={isLoading} isValidating={isValidating} />
                ) : (
                    <Typography
                        sx={{ padding: 1, paddingLeft: 2, paddingRight: 2, textAlign: 'left', mb: 0.5 }}
                        variant='body2'
                        color='text.secondary'
                    >
                        No new notifications...
                    </Typography>
                )}
            </Menu>
        </>
    );
};

export default Notifications;
