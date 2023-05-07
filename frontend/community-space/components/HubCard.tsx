import { Hub as HubType } from '@/types/db.types';
import { mediumDateWithNoTimeFormatter } from '@/utils/Utility';
import { Avatar, Box, Button, Card, CardContent, Divider, Link, Typography } from '@mui/material';
import { teal } from '@mui/material/colors';

const HubCard = ({ hub }: { hub: HubType }) => {
    return (
        <Card variant='elevation' elevation={1}>
            <>
                <CardContent>
                    <Typography variant='h5' component='div' align='center'>
                        {hub.name}
                    </Typography>
                    <Typography sx={{ mb: 1.5 }} color='text.secondary' align='center'>
                        {hub.owner}
                    </Typography>
                    <Box alignItems={'center'} justifyContent={'center'} display={'flex'}>
                        <Avatar
                            sx={{
                                bgcolor: teal[Object.keys(teal).at(Math.random() * Object.keys(teal).length)],
                                width: 56,
                                height: 56,
                            }}
                            variant='square'
                        >
                            {hub.name.substring(0, 3).toUpperCase()}
                        </Avatar>
                    </Box>
                    <Typography sx={{ mt: 1.5 }} color='text.secondary' align='center'>
                        Since {mediumDateWithNoTimeFormatter.format(new Date(hub.createdOn))}
                    </Typography>
                </CardContent>
                <Divider />
                <Typography sx={{ mt: 1.5, mb: 1.5 }} color='text.secondary' align='center' variant='subtitle2'>
                    {hub.role}
                </Typography>
                <Divider />
                <Button
                    size='small'
                    fullWidth
                    type='button'
                    variant='text'
                    href={`/hubs/${hub.id}`}
                    LinkComponent={Link}
                    disabled={hub.role === 'WAITER' ? true : false}
                >
                    {hub.role === 'WAITER' ? 'Waiting...' : hub.role !== 'NONE' ? 'Enter' : 'Join'}
                </Button>
            </>
        </Card>
    );
};

export default HubCard;
