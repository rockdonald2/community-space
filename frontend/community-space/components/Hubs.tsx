import { Hub } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import { useAuthContext } from '@/utils/AuthContext';
import { checkIfError, swrHubsFetcherWithAuth } from '@/utils/Utility';
import useSWR from 'swr';
import { Grid } from '@mui/material';
import HubCard from './HubCard';
import Alerter from './Alerter';

const Hubs = () => {
    const { user } = useAuthContext();
    const {
        data: hubs,
        error,
        isLoading,
        isValidating,
    } = useSWR<Hub[] | ErrorResponse>({ key: 'hubs', token: user.token }, swrHubsFetcherWithAuth);

    return (
        <>
            <Alerter isLoading={isLoading} isValidating={isValidating} data={hubs} error={error} />
            <Grid container spacing={2}>
                {!isLoading &&
                    !isValidating &&
                    !checkIfError(hubs) &&
                    (hubs as Hub[]).map((hub: Hub) => (
                        <Grid item key={hub.id} xs={12} sm={8} md={6}>
                            <HubCard hub={hub} />
                        </Grid>
                    ))}
            </Grid>
        </>
    );
};

export default Hubs;
