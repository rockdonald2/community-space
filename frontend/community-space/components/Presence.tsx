import { usePresenceContext } from '@/utils/PresenceContext';
import { Container, Skeleton, Stack, Tooltip, Typography } from '@mui/material';
import Avatar from './Avatar';
import { useAuthContext } from '@/utils/AuthContext';
import CircularLoading from './CircularLoading';
import styles from '@/styles/Presence.module.scss';

const Presence = () => {
    const { presence } = usePresenceContext();
    const { user } = useAuthContext();

    return (
        <Container>
            <Typography mb={2} color='text.secondary' variant='h6'>
                Others online
            </Typography>
            <Stack alignItems={'flex-start'} direction={'row'} flexWrap={'wrap'} sx={{ mb: 2 }}>
                {presence ? (
                    presence.length !== 1 ? (
                        presence
                            .filter((present) => present.email !== user.email)
                            .map((present, idx) => {
                                return (
                                    <div key={idx} className={styles['avatar-wrapper']}>
                                        <Avatar user={present} isOnline generateRandomColor />
                                    </div>
                                );
                            })
                    ) : (
                        <Tooltip title={'Looks like none is here'} arrow>
                            <Skeleton variant='circular' width={42} height={42} />
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
