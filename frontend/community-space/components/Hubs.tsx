import { Hub } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import { useAuthContext } from '@/utils/AuthContext';
import { checkIfError, swrHubsFetcherWithAuth } from '@/utils/Utility';
import useSWR from 'swr';
import { Grid } from '@mui/material';
import HubCard from './HubCard';
import Alerter from './Alerter';
import { useCallback } from 'react';

const Hubs = () => {
    const { user } = useAuthContext();
    const {
        data: hubs,
        error,
        isLoading,
        isValidating,
        mutate,
    } = useSWR<Hub[] | ErrorResponse>({ key: 'hubs', token: user.token }, swrHubsFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    const mutateCallback = useCallback(
        (hub: Hub) => {
            let newHubs = [...(hubs as Hub[])];
            newHubs[(newHubs as Hub[]).findIndex((h) => h.id === hub.id)] = hub;
            mutate([...(newHubs as Hub[])], false);
        },
        [hubs, mutate]
    );

    return (
        <>
            <Alerter isLoading={isLoading} isValidating={isValidating} data={hubs} error={error} />
            <Grid container spacing={2}>
                {!isLoading &&
                    !isValidating &&
                    !checkIfError(hubs) &&
                    (hubs as Hub[]).map((hub: Hub) => (
                        <Grid item key={hub.id} xs={12} sm={8} md={6}>
                            <HubCard hub={hub} mutateCallback={mutateCallback} />
                        </Grid>
                    ))}
            </Grid>
        </>
    );
};

export default Hubs;
