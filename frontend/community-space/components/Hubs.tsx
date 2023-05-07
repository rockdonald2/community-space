import { Hub } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import { useAuthContext } from '@/utils/AuthContext';
import { swrHubsFetcherWithAuth } from '@/utils/Utility';
import useSWR from 'swr';
import SkeletonLoader from './SkeletonLoader';
import { Alert, AlertTitle, Grid } from '@mui/material';
import HubCard from './HubCard';

const Hubs = () => {
    const { user } = useAuthContext();
    const { data, error, isLoading, isValidating } = useSWR<Hub[] | ErrorResponse>(
        { key: 'hubs', token: user.token },
        swrHubsFetcherWithAuth,
        {
            revalidateOnMount: true,
            revalidateOnReconnect: true,
            refreshWhenHidden: false,
            refreshWhenOffline: false,
        }
    );

    if (isLoading || isValidating) return <SkeletonLoader />;

    if (error) {
        // this a client error handler
        return (
            <Alert severity='error'>
                <AlertTitle>Oops!</AlertTitle>
                Unexpected error has occurred.
            </Alert>
        );
    }

    if ('status' in data) {
        // if it's an error response, handle accordingly; this is coming from the server
        if (data.status === 404) {
            return (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    The requested content cannot be found.
                </Alert>
            );
        } else {
            return (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    Unexpected error has occurred.
                </Alert>
            );
        }
    }

    return (
        <Grid container spacing={2}>
            {data &&
                data.map((hub: Hub) => (
                    <Grid item key={hub.id} xs={12} sm={8} md={6}>
                        <HubCard hub={hub} />
                    </Grid>
                ))}
        </Grid>
    );
};

export default Hubs;
