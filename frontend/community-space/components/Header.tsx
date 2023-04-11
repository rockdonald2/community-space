import { useAuthContext } from '@/utils/AuthContext';
import { Avatar, Divider, Stack, Typography } from '@mui/material';

const Header = () => {
    const { user } = useAuthContext();

    return (
        <>
            <Stack
                direction={'row'}
                alignItems={'center'}
                justifyContent={'flex-start'}
                mb={'1rem'}
            >
                <Typography variant='h4' component='h1'>
                    Community Space 👋
                </Typography>
                <Divider
                    variant='middle'
                    orientation='vertical'
                    flexItem
                    style={{ margin: '0 1rem' }}
                />
                <Avatar style={{marginRight: '1rem'}}>{user.email?.substring(0, 2).toUpperCase()}</Avatar>
                <Typography color='text.secondary'>
                    logged in as <strong>{`${user.firstName} ${user.lastName}`}</strong>
                </Typography>
            </Stack>
            <Divider sx={{ marginBottom: '1rem' }} />
        </>
    );
};

export default Header;
