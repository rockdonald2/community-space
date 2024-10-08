import { UserShort } from '@/types/db.types';
import { stringToColor } from '@/utils/Utility';
import { Avatar as MaterialAvatar, Badge, styled, Tooltip } from '@mui/material';
import { useState } from 'react';

const StyledBadge = styled(Badge)(({ theme }) => ({
    '& .MuiBadge-badge': {
        backgroundColor: '#44b700',
        color: '#44b700',
        boxShadow: `0 0 0 2px ${theme.palette.background.paper}`,
        '&::after': {
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            borderRadius: '50%',
            border: '1px solid currentColor',
            content: '""',
        },
    },
}));

const defaultColor = 'var(--mui-palette-grey-600)';

const Avatar = ({
    user,
    isOnline = false,
    generateRandomColor = false,
    style,
    cursor = 'default',
    onClick = null,
    hoverText = null,
    innerBody = null,
}: {
    user: UserShort;
    isOnline?: boolean;
    generateRandomColor?: boolean;
    style?: React.CSSProperties;
    cursor?: 'pointer' | 'default';
    hoverText?: string | JSX.Element;
    onClick?: () => void;
    innerBody?: JSX.Element;
}) => {
    const [innerText, setInnerText] = useState<string | JSX.Element>(user.email?.substring(0, 2).toUpperCase());
    const [bgColor, setBgColor] = useState<string>(generateRandomColor ? stringToColor(user.email) : defaultColor);

    return (
        <StyledBadge
            overlap='circular'
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
            }}
            variant={isOnline ? 'dot' : 'standard'}
            style={{ cursor: cursor }}
            onClick={onClick}
            onMouseOver={() => {
                if (hoverText == null) return;

                setInnerText(hoverText);
                setBgColor(defaultColor);
            }}
            onMouseOut={() => {
                if (hoverText == null) return;

                setInnerText(user.email?.substring(0, 2).toUpperCase());
                setBgColor(generateRandomColor ? stringToColor(user.email) : defaultColor);
            }}
        >
            <Tooltip arrow title={innerBody ? innerBody : user.email} enterTouchDelay={0}>
                <MaterialAvatar sx={{ bgcolor: bgColor, transition: '0.15s ease-in-out all' }} style={style}>
                    {innerText}
                </MaterialAvatar>
            </Tooltip>
        </StyledBadge>
    );
};

export default Avatar;
