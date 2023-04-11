import { Box, CircularProgress } from '@mui/material';
import styles from '@/styles/CircularLoading.module.scss';

const CircularLoading = ({ fullScreen }: { fullScreen?: boolean }) => {
    return (
        <Box
            className={`${styles.loader} ${
                fullScreen ? styles['loader--full'] : ''
            }`}
        >
            <CircularProgress />
        </Box>
    );
};

export default CircularLoading;
