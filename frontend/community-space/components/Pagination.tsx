import { Container, Button } from '@mui/material';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

const Pagination = ({
    currPage,
    setPage,
    totalPages,
}: {
    currPage: number;
    setPage: any;
    totalPages: number | null;
}) => {
    return (
        <Container sx={{ justifyContent: 'center', alignItems: 'center', display: 'flex', mt: 4 }}>
            <Button
                startIcon={<ArrowBackIosNewIcon />}
                sx={{ mr: 1 }}
                disabled={currPage === 0}
                onClick={() => setPage(currPage - 1)}
            >
                Prev
            </Button>
            <Button
                endIcon={<ArrowForwardIosIcon />}
                onClick={() => setPage(currPage + 1)}
                disabled={currPage + 1 === totalPages || totalPages === 0}
            >
                Next
            </Button>
        </Container>
    );
};

export default Pagination;
