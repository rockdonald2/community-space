import { Hub } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { swrExploreHubFetcherWithAuth } from '@/utils/Utility';
import { Grid } from '@mui/material';
import useSWR from 'swr';
import HubExploreCard from './HubExploreCard';
import Alerter from './Alerter';

interface HubTabPanelProps {
    index: number;
    value: number;
    role: 'OWNER' | 'MEMBER';
}

const HubTabPanel = ({ value, index, role, ...other }: HubTabPanelProps) => {
    const { user } = useAuthContext();

    const {
        data: hubs,
        error: hubsError,
        isLoading: hubsIsLoading,
        isValidating: hubsIsValidating,
    } = useSWR<Hub[]>({ key: 'explore', token: user.token, role: role }, swrExploreHubFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    return (
        <>
            {!hubsIsLoading && !hubsIsValidating && !hubsError ? (
                <div
                    role='tabpanel'
                    hidden={value !== index}
                    id={`simple-tabpanel-${index}`}
                    aria-labelledby={`simple-tab-${index}`}
                    {...other}
                >
                    {value === index && (
                        <Grid container spacing={2} sx={{ mt: 3 }}>
                            {hubs.map((hub) => (
                                <Grid item key={hub.id} xs={12} sm={8} md={6}>
                                    <HubExploreCard hub={hub} />
                                </Grid>
                            ))}
                        </Grid>
                    )}
                </div>
            ) : (
                <Alerter
                    isValidating={hubsIsValidating}
                    isLoading={hubsIsLoading}
                    data={hubs}
                    error={hubsError}
                    nrOfLayersInSkeleton={2}
                />
            )}
        </>
    );
};

const a11yProps = (index: number) => {
    return {
        id: `simple-tab-${index}`,
        'aria-controls': `simple-tabpanel-${index}`,
    };
};

export { HubTabPanel as default, a11yProps };
