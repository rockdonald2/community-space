import { Skeleton } from '@mui/material';

const SkeletonLoader = ({ nrOfLayers = 4 }: { nrOfLayers?: number }) => {
    return (
        <>
            {Array(nrOfLayers)
                .fill(0)
                .map((_, idx) => (
                    <Skeleton key={idx} />
                ))}
        </>
    );
};

export default SkeletonLoader;
