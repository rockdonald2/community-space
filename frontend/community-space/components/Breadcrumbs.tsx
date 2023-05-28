import { Link as MaterialLink, Breadcrumbs as MaterialBreadcrumbs, Typography } from '@mui/material';
import HomeIcon from '@mui/icons-material/Home';
import Link from 'next/link';

export type Route = {
    name: string;
    href?: string;
};

const Breadcrumbs = ({ prevRoutes, currRoute }: { prevRoutes?: Route[]; currRoute: Route }) => {
    return (
        <div role='presentation' style={{ marginBottom: '.5rem' }}>
            <MaterialBreadcrumbs aria-label='breadcrumb'>
                <MaterialLink
                    component={Link}
                    underline='hover'
                    sx={{ display: 'flex', alignItems: 'center' }}
                    color='inherit'
                    href='/'
                >
                    <HomeIcon sx={{ mr: 0.5 }} fontSize='inherit' />
                    Home
                </MaterialLink>
                {prevRoutes?.map((route, idx) => (
                    <MaterialLink
                        key={idx}
                        component={Link}
                        underline='hover'
                        sx={{ display: 'flex', alignItems: 'center' }}
                        color='inherit'
                        href={route.href}
                    >
                        {route.name}
                    </MaterialLink>
                ))}
                <Typography sx={{ display: 'flex', alignItems: 'center' }} color='text.primary'>
                    {currRoute.name}
                </Typography>
            </MaterialBreadcrumbs>
        </div>
    );
};

export default Breadcrumbs;
