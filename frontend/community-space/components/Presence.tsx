import { usePresenceContext } from '@/utils/PresenceContext';
import { Container, Skeleton, Stack, Typography } from '@mui/material';
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
            <Stack alignItems={'flex-start'} direction={'row'} flexWrap={'wrap'}>
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
                        <Skeleton variant='circular' width={40} height={40} />
                    )
                ) : (
                    <CircularLoading />
                )}
            </Stack>
        </Container>
    );
};

export default Presence;
