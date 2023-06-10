import useSWR from 'swr';
import { Activity } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { longDateShortTimeDateFormatter, swrActivitiesFetcherWithAuth } from '@/utils/Utility';
import Head from 'next/head';
import Item from '@/components/layout/Item';
import Alerter from '@/components/layout/Alerter';
import { Chip, Typography, Link as MaterialLink, Pagination, Stack } from '@mui/material';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import Breadcrumbs from '@/components/layout/Breadcrumbs';
import Avatar from '@/components/misc/Avatar';
import {
    Timeline,
    TimelineConnector,
    TimelineContent,
    TimelineDot,
    TimelineItem,
    TimelineOppositeContent,
    TimelineSeparator,
    timelineContentClasses,
} from '@mui/lab';

const Activity = () => {
    const { user } = useAuthContext();

    const [currPage, setCurrPage] = useState<number>(1);
    const [activitiesTimeMapped, setActivitiesTimeMapped] = useState<Map<string, Activity[]>>(new Map());

    const {
        data: activities,
        error: activitiesError,
        isLoading: activitiesIsLoading,
        isValidating: activitiesIsValidating,
    } = useSWR<{ totalPages: number; totalCount: number; content: Activity[] }>(
        { key: 'activities', token: user.token, page: currPage - 1 },
        swrActivitiesFetcherWithAuth
    );

    useEffect(() => {
        const newMapping = new Map<string, Activity[]>();

        activities?.content?.forEach((activity) => {
            const currDateString = new Date(activity.date).toDateString();

            if (newMapping.has(currDateString)) {
                newMapping.get(currDateString).push(activity);
            } else {
                newMapping.set(currDateString, [activity]);
            }
        });

        setActivitiesTimeMapped(newMapping);
    }, [activities]);

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
                    <Timeline
                        position='left'
                        sx={{
                            [`& .${timelineContentClasses.root}`]: {
                                flex: 0.2,
                            },
                            p: 0,
                        }}
                    >
                        {Object.keys(Object.fromEntries(activitiesTimeMapped?.entries()))
                            .reverse()
                            .map((date, idx) => (
                                <TimelineItem key={idx}>
                                    <TimelineOppositeContent sx={{ maxWidth: '67vw' }}>
                                        {activitiesTimeMapped
                                            ?.get(date)
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
                                                    <Stack>
                                                        <Typography variant='body1' sx={{ mb: 1.5 }} component={'div'}>
                                                            <Avatar
                                                                user={{ email: activity.takerUser.email }}
                                                                generateRandomColor
                                                            />
                                                            <strong style={{ marginLeft: '.5rem' }}>
                                                                {activity.takerUser.name}
                                                            </strong>
                                                        </Typography>
                                                        <Typography component={'div'}>
                                                            {activity.type
                                                                .toLocaleLowerCase()
                                                                .split('_')
                                                                .reverse()
                                                                .join(' a ')}{' '}
                                                            {activity.affectedUsers?.map((user, idx) => (
                                                                <>
                                                                    <span key={idx}>{user.name}</span>{' '}
                                                                </>
                                                            ))}
                                                            {activity.type.includes('MEMO') && `${activity.memoTitle} `}
                                                            in
                                                            <MaterialLink
                                                                component={Link}
                                                                href={`/hubs/${activity.hubId}`}
                                                                sx={{ mx: 0.5 }}
                                                            >
                                                                {activity.hubName}
                                                            </MaterialLink>
                                                        </Typography>
                                                    </Stack>
                                                    <Chip
                                                        label={longDateShortTimeDateFormatter.format(
                                                            new Date(activity.date)
                                                        )}
                                                        variant='filled'
                                                        sx={{ mt: { xs: 1, md: 0 } }}
                                                    />
                                                </Item>
                                            ))
                                            .reverse()}
                                    </TimelineOppositeContent>
                                    <TimelineSeparator>
                                        <TimelineDot />
                                        <TimelineConnector />
                                    </TimelineSeparator>
                                    <TimelineContent>{date}</TimelineContent>
                                </TimelineItem>
                            ))}
                    </Timeline>
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
