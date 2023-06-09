import { IconButton } from '@mui/material';
import { styled } from '@mui/material/styles';
import { SnackbarProvider as SnackbarProviderInternal, MaterialDesignContent, useSnackbar } from 'notistack';
import CloseIcon from '@mui/icons-material/Close';
import NotificationSnackbar from '@/components/misc/NotificationSnackbar';
import { Notification } from '@/types/db.types';

const SnackbarCloseButton = ({ snackbarKey }) => {
    const { closeSnackbar } = useSnackbar();

    return (
        <IconButton onClick={() => closeSnackbar(snackbarKey)}>
            <CloseIcon fontSize='small' sx={{ color: 'white' }} />
        </IconButton>
    );
};

const StyledMaterialDesignContent = styled(MaterialDesignContent)(() => ({
    '&.notistack-MuiContent-success': {
        backgroundColor: 'var(--mui-palette-success-dark)',
    },
    '&.notistack-MuiContent-error': {
        backgroundColor: 'var(--mui-palette-error-dark)',
    },
    '&.notistack-MuiContent-warning': {
        backgroundColor: 'var(--mui-palette-warning-dark)',
    },
    '&.notistack-MuiContent-info': {
        backgroundColor: 'var(--mui-palette-primary-dark)',
    },
}));

const SnackbarProvider = ({ children }: { children: JSX.Element }) => {
    return (
        <SnackbarProviderInternal
            preventDuplicate
            maxSnack={15}
            anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
            transitionDuration={{ enter: 300, exit: 300 }}
            autoHideDuration={7000}
            Components={{
                success: StyledMaterialDesignContent,
                error: StyledMaterialDesignContent,
                info: StyledMaterialDesignContent,
                warning: StyledMaterialDesignContent,
                default: StyledMaterialDesignContent,
                notification: NotificationSnackbar,
            }}
            action={(snackbarKey) => <SnackbarCloseButton snackbarKey={snackbarKey} />}
        >
            {children}
        </SnackbarProviderInternal>
    );
};

declare module 'notistack' {
    interface VariantOverrides {
        notification: {
            notification: Notification;
        };
    }
}

export default SnackbarProvider;
