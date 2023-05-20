import { Hub } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import { useAuthContext } from '@/utils/AuthContext';
import { checkIfError, swrExploreHubFetcherWithAuth } from '@/utils/Utility';
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
    } = useSWR<Hub[] | ErrorResponse>({ key: 'explore', token: user.token, role: role }, swrExploreHubFetcherWithAuth, {
        revalidateOnFocus: false,
    });

    return (
        <>
            <Alerter
                isValidating={hubsIsValidating}
                isLoading={hubsIsLoading}
                data={hubs}
                error={hubsError}
                nrOfLayersInSkeleton={3}
            />
            {!hubsIsLoading && !hubsIsValidating && !checkIfError(hubs) && (
                <div
                    role='tabpanel'
                    hidden={value !== index}
                    id={`simple-tabpanel-${index}`}
                    aria-labelledby={`simple-tab-${index}`}
                    {...other}
                >
                    {value === index && (
                        <Grid container spacing={2} sx={{ mt: 3 }}>
                            {(hubs as Hub[]).map((hub) => (
                                <Grid item key={hub.id} xs={12} sm={8} md={6}>
                                    <HubExploreCard hub={hub} />
                                </Grid>
                            ))}
                        </Grid>
                    )}
                </div>
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
