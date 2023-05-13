import { useAuthContext } from '@/utils/AuthContext';
import { useRouter } from 'next/router';
import useSWR, { useSWRConfig } from 'swr';
import { Hub as HubType, UserShort } from '@/types/db.types';
import { ErrorResponse } from '@/types/types';
import {
    checkIfError,
    swrHubFetcherWithAuth,
    swrMembersFetcherWithAuth,
    swrWaitersFetcherWithAuth,
} from '@/utils/Utility';
import { Container, Divider, Grid, Typography } from '@mui/material';
import Head from 'next/head';
import Memos from '@/components/Memos';
import Alerter from '@/components/Alerter';
import Avatar from '@/components/Avatar';
import { useCallback } from 'react';
import DoneIcon from '@mui/icons-material/Done';
import { GATEWAY_URL } from '@/utils/Constants';
import { usePresenceContext } from '@/utils/PresenceContext';

const Hub = () => {
    const { query } = useRouter();
    const { id: hubId } = query;
    const { mutate } = useSWRConfig();

    const { user } = useAuthContext();
    const { presence } = usePresenceContext();

    const {
        data: hub,
        error: hubError,
        isLoading: hubIsLoading,
        isValidating: hubIsValidating,
    } = useSWR<HubType | ErrorResponse>({ key: 'hub', token: user.token, hubId: hubId }, swrHubFetcherWithAuth, {
        revalidateOnFocus: false,
        refreshWhenHidden: false,
        refreshWhenOffline: false,
    });

    const {
        data: hubPendings,
        error: hubPendingsError,
        isLoading: hubPendingsIsLoading,
        isValidating: hubPendingsIsValidating,
    } = useSWR<UserShort[] | ErrorResponse>(
        (hub as HubType)?.role === 'OWNER' ? { key: 'pendings', token: user.token, hubId: hubId } : null,
        swrWaitersFetcherWithAuth,
        { revalidateOnFocus: false, refreshWhenHidden: false, refreshWhenOffline: false }
    );

    const {
        data: hubMembers,
        error: hubMembersError,
        isLoading: hubMembersIsLoading,
        isValidating: hubMembersIsValidating,
    } = useSWR<UserShort[] | ErrorResponse>(
        { key: 'members', token: user.token, hubId: hubId },
        swrMembersFetcherWithAuth,
        { revalidateOnFocus: false, refreshWhenHidden: false, refreshWhenOffline: false }
    );

    const handleJoinHub = useCallback(
        async ({ pendingMember }: { pendingMember: UserShort }) => {
            // we need to make 2 calls, one to delete it from the waiters club and add it to the members of a hub
            try {
                const waiterResp = await fetch(`${GATEWAY_URL}/api/v1/hubs/${hubId}/waiters/${pendingMember.email}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                    },
                });

                if (!waiterResp.ok) {
                    throw new Error('Failed to delete user from waiters list', {
                        cause: waiterResp,
                    });
                }

                const membersResp = await fetch(`${GATEWAY_URL}/api/v1/hubs/${hubId}/members`, {
                    method: 'POST',
                    headers: {
                        Authorization: `Bearer ${user.token}`,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email: pendingMember.email }),
                });

                if (!membersResp.ok) {
                    throw new Error('Failed to add user to members list', {
                        cause: membersResp,
                    });
                }

                mutate({ key: 'pendings', token: user.token, hubId: hubId });
                mutate({ key: 'members', token: user.token, hubId: hubId });
            } catch (err) {
                console.debug('error', err);
            }
        },
        [hubId, mutate, user.token]
    );

    return (
        <>
            <Head>
                <title>Community Space {!checkIfError(hub) ? `| ${(hub as HubType).name}` : ''}</title>
            </Head>
            <Grid container spacing={2}>
                <Grid item xs={4}>
                    <Container>
                        {(hub as HubType)?.role === 'OWNER' && (
                            <>
                                <Typography mb={2} color='text.secondary' variant='h6'>
                                    Waiting members
                                </Typography>
                                <Alerter
                                    isValidating={hubPendingsIsValidating}
                                    isLoading={hubIsLoading}
                                    data={hubPendings}
                                    error={hubPendingsError}
                                    nrOfLayersInSkeleton={1}
                                />
                                {!hubPendingsIsLoading &&
                                    !hubPendingsIsValidating &&
                                    !checkIfError(hubPendings) &&
                                    ((hubPendings as UserShort[]).length !== 0 ? (
                                        (hubPendings as UserShort[]).map((user, idx) => (
                                            <Avatar
                                                style={{ marginRight: '.5rem' }}
                                                user={user}
                                                key={idx}
                                                generateRandomColor
                                                cursor='pointer'
                                                onClick={() => handleJoinHub({ pendingMember: user })}
                                                hoverText={<DoneIcon color='success' />}
                                            />
                                        ))
                                    ) : (
                                        <Typography variant='body2' color='text.secondary'>
                                            No pending members
                                        </Typography>
                                    ))}
                                <Divider sx={{ mt: 2, mb: 2 }} />
                            </>
                        )}
                        <>
                            <Typography mb={2} color='text.secondary' variant='h6'>
                                Members
                            </Typography>
                            <Alerter
                                isValidating={hubMembersIsValidating}
                                isLoading={hubIsLoading}
                                data={hubMembers}
                                error={hubMembersError}
                                nrOfLayersInSkeleton={1}
                            />
                            {!hubMembersIsLoading &&
                                !hubMembersIsValidating &&
                                !checkIfError(hubMembers) &&
                                (hubMembers as UserShort[]).map((user, idx) => (
                                    <div key={idx} style={{ marginRight: '.5rem', display: 'inline-block' }}>
                                        <Avatar
                                            user={user}
                                            generateRandomColor
                                            isOnline={presence.filter((p) => p.email === user.email).length > 0}
                                        />
                                    </div>
                                ))}
                        </>
                    </Container>
                </Grid>
                <Grid item xs={8}>
                    <Alerter isValidating={hubIsValidating} isLoading={hubIsLoading} data={hub} error={hubError} />
                    {!hubIsLoading && !hubIsValidating && !checkIfError(hub) && (
                        <>
                            <Container sx={{ marginBottom: '1rem' }}>
                                <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                                    Welcome to {(hub as HubType).name} Hub!
                                </Typography>
                            </Container>
                            <Divider sx={{ mb: 1.5 }} />
                            <Memos hubId={hubId} />
                        </>
                    )}
                </Grid>
            </Grid>
        </>
    );
};

export default Hub;
