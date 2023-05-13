import { Alert, AlertTitle } from '@mui/material';
import SkeletonLoader from './SkeletonLoader';

const Alerter = ({
    isLoading,
    isValidating,
    error,
    data,
    nrOfLayersInSkeleton = 2,
}: {
    isLoading: boolean;
    isValidating: boolean;
    error: any;
    data: any;
    nrOfLayersInSkeleton?: number;
}) => {
    return (
        <>
            {(isLoading || isValidating) && <SkeletonLoader nrOfLayers={nrOfLayersInSkeleton} />}
            {error && (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    Unexpected error has occurred.
                </Alert>
            )}
            {data && 'status' in data && data?.status === 404 ? (
                <Alert severity='error'>
                    <AlertTitle>Oops!</AlertTitle>
                    The requested content cannot be found.
                </Alert>
            ) : (
                data &&
                'status' in data && (
                    <Alert severity='error'>
                        <AlertTitle>Oops!</AlertTitle>
                        Unexpected error has occurred.
                    </Alert>
                )
            )}
        </>
    );
};

export default Alerter;
