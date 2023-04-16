import { useAuthContext } from '@/utils/AuthContext';
import { Divider, Stack, Typography } from '@mui/material';
import Avatar from './Avatar';

const Header = () => {
    const { user } = useAuthContext();

    return (
        <>
            <Stack direction={'row'} alignItems={'center'} justifyContent={'flex-start'} mb={'1rem'}>
                <Typography variant='h4' component='h1'>
                    Community Space
                </Typography>
                <Divider variant='middle' orientation='vertical' flexItem style={{ margin: '0 1rem' }} />
                <Avatar user={user} style={{ marginRight: '1rem' }} />
                <Typography color='text.secondary'>
                    logged in as <strong>{`${user.firstName} ${user.lastName}`}</strong> ({user.email})
                </Typography>
            </Stack>
            <Divider sx={{ marginBottom: '1rem' }} />
        </>
    );
};

export default Header;
