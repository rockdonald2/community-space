import { Visibility, VisibilityOff } from '@mui/icons-material';
import { IconButton, InputAdornment, TextField } from '@mui/material';
import React, { useState } from 'react';
import styles from '@/styles/PasswordField.module.scss';

const PasswordField = ({
    isError,
    helperText,
    handleInput,
    label,
}: {
    isError: boolean;
    helperText?: string;
    handleInput: (e: React.ChangeEvent<HTMLInputElement>) => any;
    label: string;
}) => {
    const [showPassword, setShowPassword] = useState<boolean>(false);
    const handleClickShowPassword = () => setShowPassword(!showPassword);
    const handleMouseDownPassword = () => setShowPassword(!showPassword);

    return (
        <>
            <TextField
                label={label}
                type={showPassword ? 'text' : 'password'}
                className={styles.field}
                onChange={handleInput}
                helperText={helperText}
                error={isError}
                // InputProps={{
                //     endAdornment: (
                //         <InputAdornment position='end'>
                //             <IconButton
                //                 aria-label='toggle password visibility'
                //                 onClick={handleClickShowPassword}
                //                 onMouseDown={handleMouseDownPassword}
                //             >
                //                 {showPassword ? (
                //                     <Visibility />
                //                 ) : (
                //                     <VisibilityOff />
                //                 )}
                //             </IconButton>
                //         </InputAdornment>
                //     ),
                // }}
            />
        </>
    );
};

export default PasswordField;
