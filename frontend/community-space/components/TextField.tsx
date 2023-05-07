import { TextField as MaterialTextField } from '@mui/material';
import React from 'react';
import styles from '@/styles/TextField.module.scss';

const TextField = ({
    isError = false,
    helperText,
    handleInput,
    label,
    inner = false,
    multiline = false,
}: {
    isError?: boolean;
    helperText?: string;
    handleInput: (e: React.ChangeEvent<HTMLInputElement>) => any;
    label: string;
    inner?: boolean;
    multiline?: boolean;
}) => {
    return (
        <>
            <MaterialTextField
                label={label}
                type='text'
                className={`${styles.field} ${inner ? styles['field--inner'] : ''}`}
                onChange={handleInput}
                helperText={helperText}
                error={isError}
                multiline={multiline}
                rows={multiline ? 4 : 1}
            />
        </>
    );
};

export default TextField;
