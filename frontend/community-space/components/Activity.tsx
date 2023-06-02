import {
    getCurrentDay,
    getCurrentMonthName,
    getDaysInCurrentMonth,
    swrActivitiesGroupedFetcherWithAuth,
} from '@/utils/Utility';
import { Box, Container, Tooltip, Typography } from '@mui/material';
import styles from '@/styles/Activity.module.scss';
import { useAuthContext } from '@/utils/AuthContext';
import useSWR from 'swr';
import { ActivityGrouped } from '@/types/db.types';
import Alerter from './Alerter';
import { useEffect, useState } from 'react';

const currDaysInMonth = getDaysInCurrentMonth();
const currDay = getCurrentDay();
const currMonthName = getCurrentMonthName();

const Activity = () => {
    const { user } = useAuthContext();

    const {
        data: rawActivities,
        error: activitiesError,
        isLoading: isLoadingActivities,
        isValidating: isValidatingActivities,
    } = useSWR<ActivityGrouped[]>(
        { key: 'activities-grouped', token: user.token },
        swrActivitiesGroupedFetcherWithAuth
    );

    const [activities, setActivities] = useState<Map<number, number>>(new Map<number, number>());

    useEffect(() => {
        if (!rawActivities) return;

        // we need to group and count the activities by days
        const activitiesMap = new Map<number, number>();
        rawActivities.forEach((activity) => {
            activitiesMap.set(activity.groupNumber, activity.count);
        });

        setActivities(activitiesMap);
    }, [rawActivities]);

    return (
        <Container style={{ paddingRight: '.5rem' }}>
            <Typography mb={2} color='text.secondary' variant='h6'>
                Activity
            </Typography>
            {!isLoadingActivities && !isValidatingActivities && !activitiesError ? (
                <>
                    <Container className={styles.wrapper} sx={{ mb: 2 }}>
                        {new Array(currDaysInMonth).fill(0).map((_day, idx) => (
                            <Tooltip
                                key={idx}
                                arrow
                                title={`${currMonthName} ${idx + 1} (${
                                    activities.has(idx + 1) ? activities.get(idx + 1) : 0
                                } activities)`}
                                enterTouchDelay={0}
                            >
                                <Box
                                    className={`${styles.block} ${idx + 1 === currDay && styles['block--today']}`}
                                    sx={{
                                        backgroundColor:
                                            activities?.has(idx + 1) &&
                                            `rgba(21, 101, 192, ${0.03 * activities.get(idx + 1)}) !important`,
                                    }}
                                />
                            </Tooltip>
                        ))}
                    </Container>
                </>
            ) : (
                <Alerter
                    isLoading={isLoadingActivities}
                    isValidating={isValidatingActivities}
                    error={activitiesError}
                    data={rawActivities}
                    nrOfLayersInSkeleton={1}
                />
            )}
            <Typography color='text.secondary' variant='caption'>
                Activities include both hub and memo activity
            </Typography>
        </Container>
    );
};

export default Activity;
