import { TextField as MaterialTextField } from '@mui/material';
import React from 'react';
import styles from '@/styles/TextField.module.scss';

const TextField = ({
    isError,
    helperText,
    handleInput,
    label,
    inner = false,
}: {
    isError: boolean;
    helperText?: string;
    handleInput: (e: React.ChangeEvent<HTMLInputElement>) => any;
    label: string;
    inner?: boolean;
}) => {
    return (
        <>
            <MaterialTextField
                label={label}
                type='text'
                className={`${styles.field} ${inner ? styles['field--inner']: ''}`}
                onChange={handleInput}
                helperText={helperText}
                error={isError}
            />
        </>
    );
};

export default TextField;
