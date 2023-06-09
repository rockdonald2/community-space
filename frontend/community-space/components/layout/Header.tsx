import { useAuthContext } from '@/utils/AuthContext';
import { Box, Divider, Stack, Typography } from '@mui/material';
import Avatar from '../misc/Avatar';
import Notifications from '../misc/Notifications';

const Header = () => {
    const { user } = useAuthContext();

    return (
        <>
            <Stack
                direction={{ md: 'row', xs: 'column' }}
                alignItems={'center'}
                justifyContent={'flex-start'}
                mb={'1rem'}
            >
                <Typography variant='h4' component='h1'>
                    Community Space 💬
                </Typography>
                <Divider variant='middle' orientation='vertical' flexItem sx={{ ml: 2, mr: 2 }} />
                <Avatar user={user} generateRandomColor />
                <Typography
                    color='text.secondary'
                    sx={{
                        mt: {
                            xs: 1,
                            md: 0,
                        },
                        ml: {
                            xs: 0,
                            md: 2
                        }
                    }}
                >
                    logged in as <strong>{`${user.firstName} ${user.lastName}`}</strong>
                </Typography>
                <Box
                    sx={{
                        ml: {
                            xs: 'unset',
                            md: 'auto',
                        },
                        mt: {
                            xs: 1,
                            md: 0,
                        },
                    }}
                >
                    <Notifications />
                </Box>
            </Stack>
            <Divider sx={{ marginBottom: '1rem' }} />
        </>
    );
};

export default Header;
