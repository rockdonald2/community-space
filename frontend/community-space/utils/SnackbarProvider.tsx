import { IconButton } from '@mui/material';
import { styled } from '@mui/material/styles';
import { SnackbarProvider as SnackbarProviderInternal, MaterialDesignContent, useSnackbar } from 'notistack';
import CloseIcon from '@mui/icons-material/Close';

const SnackbarCloseButton = ({ snackbarKey }) => {
    const { closeSnackbar } = useSnackbar();

    return (
        <IconButton onClick={() => closeSnackbar(snackbarKey)}>
            <CloseIcon fontSize='small' />
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
            maxSnack={10}
            anchorOrigin={{ vertical: 'top', horizontal: 'left' }}
            Components={{
                success: StyledMaterialDesignContent,
                error: StyledMaterialDesignContent,
                info: StyledMaterialDesignContent,
                warning: StyledMaterialDesignContent,
                default: StyledMaterialDesignContent,
            }}
            action={(snackbarKey) => <SnackbarCloseButton snackbarKey={snackbarKey} />}
        >
            {children}
        </SnackbarProviderInternal>
    );
};

export default SnackbarProvider;
