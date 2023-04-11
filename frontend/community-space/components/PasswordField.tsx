import { TextField } from '@mui/material';
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

    return (
        <>
            <TextField
                label={label}
                type={showPassword ? 'text' : 'password'}
                className={styles.field}
                onChange={handleInput}
                helperText={helperText}
                error={isError}
            />
        </>
    );
};

export default PasswordField;
