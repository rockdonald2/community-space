import useSWR from 'swr';
import { Activity } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { longDateShortTimeDateFormatter, swrActivitiesFetcherWithAuth } from '@/utils/Utility';
import Head from 'next/head';
import Item from '@/components/Item';
import Alerter from '@/components/Alerter';
import { Chip, Typography, Link as MaterialLink, Pagination } from '@mui/material';
import Link from 'next/link';
import { useState } from 'react';
import Breadcrumbs from '@/components/Breadcrumbs';

const Activity = () => {
    const { user } = useAuthContext();

    const [currPage, setCurrPage] = useState<number>(1);

    const {
        data: activities,
        error: activitiesError,
        isLoading: activitiesIsLoading,
        isValidating: activitiesIsValidating,
    } = useSWR<{ totalPages: number; totalCount: number; content: Activity[] }>(
        { key: 'activities', token: user.token, page: currPage - 1 },
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
                                    alignItems: { xs: 'flex-start', md: 'center' },
                                    justifyContent: 'space-between',
                                    flexDirection: {
                                        xs: 'column',
                                        md: 'row',
                                    },
                                }}
                                key={idx}
                            >
                                <Typography>
                                    <strong>{activity.userName}</strong> ({activity.user}){' '}
                                    {activity.type.toLocaleLowerCase().split('_').reverse().join(' a ')} (
                                    {activity.type.includes('MEMO') && `${activity.memoTitle} in `}
                                    <MaterialLink component={Link} href={`/hubs/${activity.hubId}`} sx={{ mx: 0.5 }}>
                                        {activity.hubName}
                                    </MaterialLink>
                                    )
                                </Typography>
                                <Chip
                                    label={longDateShortTimeDateFormatter.format(new Date(activity.date))}
                                    variant='filled'
                                    sx={{ mt: { xs: 1, md: 0 } }}
                                />
                            </Item>
                        ))
                        .reverse()}
                    <Pagination
                        count={activities?.totalPages}
                        page={currPage}
                        onChange={(_e, page) => setCurrPage(page)}
                        sx={{ display: 'flex', alignContent: 'center', justifyContent: 'center', mt: 1 }}
                        shape='rounded'
                        variant='outlined'
                        showFirstButton
                        showLastButton
                    />
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
