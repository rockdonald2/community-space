import useSWR from 'swr';
import { Activity } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { longDateShortTimeDateFormatter, swrActivitiesFetcherWithAuth } from '@/utils/Utility';
import Head from 'next/head';
import Item from '@/components/Item';
import Alerter from '@/components/Alerter';
import { Button, Typography } from '@mui/material';
import Link from 'next/link';

const Activity = () => {
    const { user } = useAuthContext();

    const {
        data: activities,
        error: activitiesError,
        isLoading: activitiesIsLoading,
        isValidating: activitiesIsValidating,
    } = useSWR<Activity[]>({ key: 'activities', token: user.token }, swrActivitiesFetcherWithAuth);

    return (
        <>
            <Head>
                <title>Community Space | Activity</title>
            </Head>
            <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                Activity in the last week
            </Typography>
            <Alerter
                isValidating={activitiesIsValidating}
                isLoading={activitiesIsLoading}
                data={activities}
                error={activitiesError}
                nrOfLayersInSkeleton={3}
            />
            {!activitiesIsLoading &&
                !activitiesIsValidating &&
                !activitiesError &&
                activities?.map((activity, idx) => (
                    <Item sx={{ mb: 1 }} key={idx}>
                        <strong>{activity.user}</strong>{' '}
                        {activity.type.toLocaleLowerCase().split('_').reverse().join(' a ')} (
                        {activity.type.includes('MEMO') && `${activity.memoTitle} in `}
                        <Button type='button' LinkComponent={Link} size='small' href={`/hubs/${activity.hubId}`}>
                            {activity.hubName}
                        </Button>
                        ) on {longDateShortTimeDateFormatter.format(new Date(activity.date))}
                    </Item>
                )).reverse()}
        </>
    );
};

export default Activity;
