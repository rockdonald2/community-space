import HubTabPanel, { a11yProps } from '@/components/HubTabPanel';
import { Box, Tab, Tabs } from '@mui/material';
import Head from 'next/head';
import { useState } from 'react';

const Explore = () => {
    const [currentTab, setCurrentTab] = useState<number>(0);

    const handleChange = (event: React.SyntheticEvent, newTabValue: number) => {
        setCurrentTab(newTabValue);
    };

    return (
        <>
            <Head>
                <title>Community Space | Explore</title>
            </Head>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                <Tabs value={currentTab} onChange={handleChange} aria-label='basic tabs example'>
                    <Tab label='Owned Hubs' {...a11yProps(0)} />
                    <Tab label='Member Hubs' {...a11yProps(1)} />
                </Tabs>
            </Box>
            <HubTabPanel value={currentTab} index={0} role='OWNER' />
            <HubTabPanel value={currentTab} index={1} role='MEMBER' />
        </>
    );
};

export default Explore;
