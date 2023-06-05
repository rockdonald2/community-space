import { Hub, Memo, UserShortCombined } from '@/types/db.types';
import { useAuthContext } from '@/utils/AuthContext';
import {
    mediumDateWithNoTimeFormatter,
    swrMembersFetcherWithAuth,
    swrRecentMemosFetcherWithAuth,
    swrWaitersFetcherWithAuth,
} from '@/utils/Utility';
import { Card, CardContent, Typography, Box, Avatar as MaterialAvatar, Divider, Chip, Button } from '@mui/material';
import { blue } from '@mui/material/colors';
import Link from 'next/link';
import useSWR from 'swr';
import Alerter from './Alerter';
import { usePresenceContext } from '@/utils/PresenceContext';
import Avatar from './Avatar';

const HubExploreCard = ({ hub }: { hub: Hub }) => {
    const { user } = useAuthContext();
    const { presence } = usePresenceContext();

    const {
        data: memos,
        error: memosError,
        isLoading: memosIsLoading,
        isValidating: memosIsValidating,
    } = useSWR<{ totalCount: number; totalPages: number; content: Memo[] }>(
        { key: 'memos', token: user.token, hubId: hub?.id, page: 0 },
        swrRecentMemosFetcherWithAuth
    );

    const {
        data: hubPendings,
        error: hubPendingsError,
        isLoading: hubPendingsIsLoading,
        isValidating: hubPendingsIsValidating,
    } = useSWR<UserShortCombined[]>(
        hub.role === 'OWNER' ? { key: 'pendings', token: user.token, hubId: hub.id } : null,
        swrWaitersFetcherWithAuth
    );

    const {
        data: hubMembers,
        error: hubMembersError,
        isLoading: hubMembersIsLoading,
        isValidating: hubMembersIsValidating,
    } = useSWR<UserShortCombined[]>({ key: 'members', token: user.token, hubId: hub.id }, swrMembersFetcherWithAuth);

    return (
        <>
            <Card variant='elevation' elevation={1}>
                <>
                    <CardContent>
                        <Typography variant='h5' component='div' align='center'>
                            {hub.name}
                        </Typography>
                        <Typography color='text.secondary' align='center'>
                            <Chip
                                avatar={
                                    <Avatar
                                        generateRandomColor
                                        style={{ width: '26px', height: '26px', margin: '.25rem', fontSize: '14px' }}
                                        user={{ email: hub.owner }}
                                    />
                                }
                                label={hub.ownerName}
                                sx={{ my: 1.5 }}
                            />
                        </Typography>
                        <Box alignItems={'center'} justifyContent={'center'} display={'flex'}>
                            <MaterialAvatar
                                sx={{
                                    bgcolor: blue[Object.keys(blue).at(Math.random() * Object.keys(blue).length)],
                                    width: 56,
                                    height: 56,
                                }}
                                variant='square'
                            >
                                {hub.name.substring(0, 3).toUpperCase()}
                            </MaterialAvatar>
                        </Box>
                        <Typography sx={{ mt: 1.5 }} color='text.secondary' align='center'>
                            Since {mediumDateWithNoTimeFormatter.format(new Date(hub.createdOn))}
                        </Typography>
                    </CardContent>
                    <Divider />
                    <Typography
                        sx={{ mt: 1.5, mb: 1.5, p: 1 }}
                        color='text.secondary'
                        align='center'
                        variant='subtitle2'
                    >
                        {!memosIsLoading && !memosIsValidating && !memosError ? (
                            <Chip label={`${memos.totalCount} memos since Yesterday`} variant='filled' />
                        ) : (
                            <Alerter
                                isValidating={memosIsValidating}
                                isLoading={memosIsLoading}
                                data={memos}
                                error={memosError}
                                nrOfLayersInSkeleton={1}
                            />
                        )}
                    </Typography>
                    <Divider />
                    <Typography
                        sx={{ mt: 1.5, mb: 1.5, p: 1 }}
                        color='text.secondary'
                        align='center'
                        variant='subtitle2'
                    >
                        {!hubMembersIsLoading && !hubMembersIsValidating && !hubMembersError ? (
                            <Chip label={`${hubMembers.length} members`} variant='filled' sx={{ mr: 1 }} />
                        ) : (
                            <Alerter
                                isValidating={hubMembersIsValidating}
                                isLoading={hubMembersIsLoading}
                                data={hubMembers}
                                error={hubMembersError}
                                nrOfLayersInSkeleton={1}
                            />
                        )}

                        {hub.role === 'OWNER' && (
                            <>
                                {!hubPendingsIsLoading && !hubPendingsIsValidating && !hubPendingsError ? (
                                    <Chip label={`${hubPendings.length} pending members`} variant='filled' />
                                ) : (
                                    <Alerter
                                        isValidating={hubPendingsIsValidating}
                                        isLoading={hubPendingsIsLoading}
                                        data={hubPendings}
                                        error={hubPendingsError}
                                        nrOfLayersInSkeleton={1}
                                    />
                                )}
                            </>
                        )}
                    </Typography>
                    <Divider />
                    <Typography
                        sx={{ mt: 1.5, mb: 1.5, p: 1 }}
                        color='text.secondary'
                        align='center'
                        variant='subtitle2'
                    >
                        {!hubMembersIsLoading && !hubMembersIsValidating && !hubMembersError ? (
                            <Chip
                                label={`${presence
                                    .map((present) =>
                                        hubMembers.filter((member) => member.email === present.email) ? 1 : 0
                                    )
                                    .reduce((acc, curr) => acc + curr, 0)} active members at the moment`}
                                variant='filled'
                            />
                        ) : (
                            <Alerter
                                isValidating={hubMembersIsValidating}
                                isLoading={hubMembersIsLoading}
                                data={hubMembers}
                                error={hubMembersError}
                                nrOfLayersInSkeleton={1}
                            />
                        )}
                    </Typography>
                    <Divider />
                    <Button
                        fullWidth
                        type='button'
                        variant='text'
                        href={`/hubs/${hub.id}`}
                        LinkComponent={Link}
                        disabled={hub.role === 'PENDING' ? true : false}
                    >
                        Go To Hub
                    </Button>
                </>
            </Card>
        </>
    );
};

export default HubExploreCard;
