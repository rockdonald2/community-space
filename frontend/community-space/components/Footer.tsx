import { Grid, Typography, Link as MaterialLink, Divider } from '@mui/material';
import Link from 'next/link';

const links: { name: string; href: string }[] = [
    { name: 'Explore Hubs', href: '/hubs/explore' },
    { name: 'Activity', href: '/activity' },
    { name: 'Create Hub', href: '/hubs/create' },
];

export default function Footer() {
    return (
        <footer>
            <Divider sx={{ mt: 2, mb: 2 }} />
            <Typography variant='h6' color='text.secondary' mb={2}>
                Useful links
            </Typography>
            <Grid container spacing={2}>
                {links.map((link, idx) => (
                    <Grid key={idx} item xs={12} md={2}>
                        <MaterialLink href={link.href} underline='hover' component={Link}>
                            {link.name}
                        </MaterialLink>
                    </Grid>
                ))}
            </Grid>
        </footer>
    );
}
