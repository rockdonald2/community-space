import { Container, Divider, Typography } from "@mui/material";
import { Hub } from "@/types/db.types";

const HubsTopbar = ({ hub }: { hub: Hub }) => {
    return (
        <>
            <Container sx={{ marginBottom: '1rem' }}>
                <Typography variant='h5' align='center' color='text.secondary' mb={2}>
                    Welcome to {hub.name} Hub!
                </Typography>
                <Typography variant='body1' align='left' color='text.secondary'>
                    {hub.description}
                </Typography>
            </Container>
            <Divider sx={{ mb: 1.5 }}/>
        </>
    )
}

export default HubsTopbar;