import useSWR from 'swr';
import { Activity } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import { longDateShortTimeDateFormatter, swrActivitiesFetcherWithAuth } from '@/utils/Utility';
import Head from 'next/head';
import Item from '@/components/Item';
import Alerter from '@/components/Alerter';
import { Button, Chip, Container, Typography } from '@mui/material';
import Link from 'next/link';
import { useState } from 'react';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

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
            <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                Activity from the last week
            </Typography>
            <Alerter
                isValidating={activitiesIsValidating}
                isLoading={activitiesIsLoading}
                data={activities}
                error={activitiesError}
                nrOfLayersInSkeleton={3}
            />
            {!activitiesIsLoading && !activitiesIsValidating && !activitiesError && (
                <>
                    {activities?.content
                        ?.map((activity, idx) => (
                            <Item
                                sx={{ mb: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
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
                    <Container sx={{ justifyContent: 'center', alignItems: 'center', display: 'flex', mt: 4 }}>
                        <Button
                            startIcon={<ArrowBackIosNewIcon />}
                            sx={{ mr: 1 }}
                            disabled={currPage === 0}
                            onClick={() => setPage(currPage - 1)}
                        >
                            Prev
                        </Button>
                        <Button
                            endIcon={<ArrowForwardIosIcon />}
                            onClick={() => setPage(currPage + 1)}
                            disabled={currPage + 1 === activities?.totalPages || activities?.totalPages === 0}
                        >
                            Next
                        </Button>
                    </Container>
                </>
            )}
        </>
    );
};

export default Activity;
