import { usePresenceContext } from '@/utils/PresenceContext';
import { Box, Container, Skeleton, Stack, Tooltip, Typography } from '@mui/material';
import Avatar from './Avatar';
import { useAuthContext } from '@/utils/AuthContext';
import CircularLoading from './CircularLoading';
import styles from '@/styles/Presence.module.scss';
import { green } from '@mui/material/colors';
import { shortTimeWithNoDateFormatter } from '@/utils/Utility';

const Presence = () => {
    const { presence } = usePresenceContext();
    const { user } = useAuthContext();

    return (
        <Container>
            <Typography mb={2} color='text.secondary' variant='h6'>
                Others online ({presence ? presence.length - 1 : 0})
            </Typography>
            <Stack alignItems={'flex-start'} direction={'row'} flexWrap={'wrap'} sx={{ mb: 2 }}>
                {presence ? (
                    presence.length > 1 ? (
                        presence
                            .filter((present) => present.email !== user.email)
                            .map((present, idx) => {
                                return (
                                    <div key={idx} className={styles['avatar-wrapper']}>
                                        <Avatar
                                            user={present}
                                            isOnline
                                            generateRandomColor
                                            innerBody={
                                                <>
                                                    <Typography fontSize={12}>{present.email}</Typography>
                                                    <Typography
                                                        fontSize={11}
                                                        sx={{ mb: 0.5 }}
                                                    >{`${present.firstName} ${present.lastName}`}</Typography>
                                                    <Stack direction={'row'} alignItems={'center'}>
                                                        <Box
                                                            sx={{
                                                                borderRadius: '50%',
                                                                width: 12,
                                                                height: 12,
                                                                bgcolor: green[400],
                                                                mr: 1,
                                                            }}
                                                        />
                                                        Last seen at {' '}
                                                        {shortTimeWithNoDateFormatter.format(
                                                            new Date(present?.lastSeen ?? new Date())
                                                        )}
                                                    </Stack>
                                                </>
                                            }
                                        />
                                    </div>
                                );
                            })
                    ) : (
                        <Tooltip title={'Looks like none is here'} arrow enterTouchDelay={0}>
                            <Skeleton variant='circular' width={43} height={43} />
                        </Tooltip>
                    )
                ) : (
                    <CircularLoading />
                )}
            </Stack>
            <Typography color='text.secondary' variant='caption'>
                Join a Hub and start interacting with them
            </Typography>
        </Container>
    );
};

export default Presence;
