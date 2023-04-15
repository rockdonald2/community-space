import { UserShort } from '@/types/db.types';
import { Badge, Avatar as MaterialAvatar, Tooltip, styled } from '@mui/material';

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

function stringToColor(string: string) {
    let hash = 0;
    let i;

    /* eslint-disable no-bitwise */
    for (i = 0; i < string.length; i += 1) {
        hash = string.charCodeAt(i) + ((hash << 5) - hash);
    }

    let color = '#';

    for (i = 0; i < 3; i += 1) {
        const value = (hash >> (i * 8)) & 0xff;
        color += `00${value.toString(16)}`.slice(-2);
    }
    /* eslint-enable no-bitwise */

    return color;
}

const Avatar = ({
    user,
    isOnline = false,
    generateRandomColor = false,
    style,
}: {
    user: UserShort;
    isOnline?: boolean;
    generateRandomColor?: boolean;
    style?: React.CSSProperties;
}) => {
    return (
        <StyledBadge
            overlap='circular'
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
            }}
            variant={isOnline ? 'dot' : 'standard'}
        >
            <Tooltip title={user.email}>
                <MaterialAvatar
                    sx={{ bgcolor: generateRandomColor ? stringToColor(user.email) : '--var(--mui-palette-grey-400)' }}
                    style={style}
                >
                    {user.email?.substring(0, 2).toUpperCase()}
                </MaterialAvatar>
            </Tooltip>
        </StyledBadge>
    );
};

export default Avatar;
