import { Divider, Stack, Typography } from '@mui/material';

const Header = () => {
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
            </Stack>
            <Divider sx={{ marginBottom: '2rem' }} />
        </>
    );
};

export default Header;
