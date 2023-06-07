import { Card, CardActions, Typography, IconButton, Collapse, Stack, Paper } from '@mui/material';
import { CustomContentProps, SnackbarContent, useSnackbar } from 'notistack';
import { forwardRef, useCallback, useState } from 'react';
import CloseIcon from '@mui/icons-material/Close';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Notification } from '@/types/db.types';
import { calculateRelativeTimeFromNow } from '@/utils/Utility';
import styles from '@/styles/NotificationSnackbar.module.scss';

export interface NotificationSnackbarProps extends CustomContentProps {
    notification: Notification;
}

const NotificationSnackbar = forwardRef<HTMLDivElement, NotificationSnackbarProps>(({ id, ...props }, ref) => {
    const { closeSnackbar } = useSnackbar();
    const [isExpanded, setExpanded] = useState(false);

    const handleExpandClick = useCallback(() => {
        setExpanded((oldExpanded) => !oldExpanded);
    }, []);

    const handleDismiss = useCallback(() => {
        closeSnackbar(id);
    }, [id, closeSnackbar]);

    return (
        <SnackbarContent ref={ref} className={styles.wrapper}>
            <Card sx={{ width: '100%' }}>
                <CardActions sx={{px: 2}}>
                    <Typography variant='body2'>{props.message}</Typography>
                    <div style={{ marginLeft: 'auto' }}>
                        <IconButton
                            aria-label='Show more'
                            size='small'
                            onClick={handleExpandClick}
                            sx={{
                                transform: isExpanded ? 'rotate(180deg)' : 'unset',
                                transition: 'all .2s ease-in-out',
                            }}
                        >
                            <ExpandMoreIcon />
                        </IconButton>
                        <IconButton size='small' onClick={handleDismiss}>
                            <CloseIcon fontSize='small' />
                        </IconButton>
                    </div>
                </CardActions>
                <Collapse in={isExpanded} timeout='auto' unmountOnExit>
                    <Paper sx={{ py: 1, px: 3 }}>
                        <Typography gutterBottom variant='caption' sx={{ display: 'block', color: 'text.primary' }}>
                            {props.notification.msg}
                        </Typography>
                        <Typography
                            variant='caption'
                            color='text.secondary'
                            sx={{
                                paddingTop: 0.5,
                            }}
                        >
                            {calculateRelativeTimeFromNow(new Date(props.notification.createdAt))}
                        </Typography>
                    </Paper>
                </Collapse>
            </Card>
        </SnackbarContent>
    );
});

NotificationSnackbar.displayName = 'NotificationSnackbar';

export default NotificationSnackbar;
