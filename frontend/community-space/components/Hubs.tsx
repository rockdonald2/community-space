import { Hub } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { swrHubsFetcherWithAuth } from '@/utils/Utility';
import useSWR from 'swr';
import { Button, Grid, Typography } from '@mui/material';
import HubCard from './HubCard';
import Alerter from './Alerter';
import { useCallback } from 'react';
import Link from 'next/link';

const Hubs = () => {
    const { user } = useAuthContext();
    const {
        data: hubs,
        error,
        isLoading,
        isValidating,
        mutate,
    } = useSWR<Hub[]>({ key: 'hubs', token: user.token }, swrHubsFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    const mutateCallback = useCallback(
        (hub: Hub) => {
            let newHubs = [...hubs];
            newHubs[newHubs.findIndex((h) => h.id === hub.id)] = hub;
            mutate([...newHubs], false);
        },
        [hubs, mutate]
    );

    return (
        <>
            {!isLoading && !isValidating && !error ? (
                <Grid container spacing={2}>
                    {hubs.length > 0 ? (
                        hubs.map((hub: Hub) => (
                            <Grid item key={hub.id} xs={12} sm={8} md={6}>
                                <HubCard hub={hub} mutateCallback={mutateCallback} />
                            </Grid>
                        ))
                    ) : (
                        <Typography variant='body1' sx={{ mt: 2 }}>
                            No hubs yet,{' '}
                            <Button href='/hubs/create' LinkComponent={Link} variant='text'>
                                create one
                            </Button>
                            .
                        </Typography>
                    )}
                </Grid>
            ) : (
                <Alerter isLoading={isLoading} isValidating={isValidating} data={hubs} error={error} />
            )}
        </>
    );
};

export default Hubs;
