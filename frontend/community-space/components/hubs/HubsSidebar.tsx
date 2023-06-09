import Members from "@/components/hubs/Members";
import { Divider } from "@mui/material";
import { Hub } from "@/types/db.types";

const HubsSidebar = ({ hub }: { hub: Hub }) => {
    return (
        <>
            <Members hubId={hub?.id} hubRole={hub?.role}/>
            <Divider sx={{ mt: 2, mb: 2 }}/>
        </>
    )
}

export default HubsSidebar;