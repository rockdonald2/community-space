import useSWR from 'swr';
import { Activity } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { longDateShortTimeDateFormatter, swrActivitiesFetcherWithAuth } from '@/utils/Utility';
import Head from 'next/head';
import Item from '@/components/Item';
import Alerter from '@/components/Alerter';
import { Button, Chip, Typography } from '@mui/material';
import Link from 'next/link';
import { useState } from 'react';
import Pagination from '@/components/Pagination';
import Breadcrumbs from '@/components/Breadcrumbs';

const Activity = () => {
    const { user } = useAuthContext();

    const [currPage, setPage] = useState<number>(0);

    const {
        data: activities,
        error: activitiesError,
        isLoading: activitiesIsLoading,
        isValidating: activitiesIsValidating,
    } = useSWR<{ totalPages: number; totalCount: number; content: Activity[] }>(
        { key: 'activities', token: user.token, page: currPage },
        swrActivitiesFetcherWithAuth
    );

    return (
        <>
            <Head>
                <title>Community Space | Activity</title>
            </Head>
            <Breadcrumbs currRoute={{ name: 'Activity' }} />
            <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                Activity from the last week
            </Typography>
            {!activitiesIsLoading && !activitiesIsValidating && !activitiesError ? (
                <>
                    {activities?.content
                        ?.map((activity, idx) => (
                            <Item
                                sx={{
                                    mb: 1,
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'space-between',
                                    flexDirection: {
                                        xs: 'column',
                                        md: 'row',
                                    },
                                }}
                                key={idx}
                            >
                                <Typography>
                                    <strong>{activity.user}</strong>{' '}
                                    {activity.type.toLocaleLowerCase().split('_').reverse().join(' a ')} (
                                    {activity.type.includes('MEMO') && `${activity.memoTitle} in `}
                                    <Button
                                        type='button'
                                        LinkComponent={Link}
                                        size='small'
                                        href={`/hubs/${activity.hubId}`}
                                    >
                                        {activity.hubName}
                                    </Button>
                                    )
                                </Typography>
                                <Chip
                                    label={longDateShortTimeDateFormatter.format(new Date(activity.date))}
                                    variant='filled'
                                />
                            </Item>
                        ))
                        .reverse()}
                    <Pagination currPage={currPage} setPage={setPage} totalPages={activities?.totalPages} />
                </>
            ) : (
                <Alerter
                    isValidating={activitiesIsValidating}
                    isLoading={activitiesIsLoading}
                    data={activities}
                    error={activitiesError}
                    nrOfLayersInSkeleton={2}
                />
            )}
        </>
    );
};

export default Activity;
