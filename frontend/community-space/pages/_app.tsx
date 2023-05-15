import '@/styles/globals.scss';
import type { AppProps } from 'next/app';
import Head from 'next/head';
import { Experimental_CssVarsProvider as CssVarsProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Layout from '@/components/Layout';
import AuthContextProvider from '@/utils/AuthContext';
import { RouteGuard } from '@/utils/RouteGuard';
import CrossContextProvider from '@/utils/CrossContext';
import { CookiesProvider } from 'react-cookie';
import PresenceMessagingWrapper from '@/utils/PresenceMessagingWrapper';
import { GATEWAY_ACCOUNT_WS } from '@/utils/Constants';

export default function App({ Component, pageProps }: AppProps) {
    return (
        <>
            <Head>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <CssVarsProvider>
                <CssBaseline enableColorScheme />
                <CookiesProvider>
                    <CrossContextProvider>
                        <AuthContextProvider>
                            <RouteGuard>
                                <PresenceMessagingWrapper url={GATEWAY_ACCOUNT_WS}>
                                    <Layout>
                                        <Component {...pageProps} />
                                    </Layout>
                                </PresenceMessagingWrapper>
                            </RouteGuard>
                        </AuthContextProvider>
                    </CrossContextProvider>
                </CookiesProvider>
            </CssVarsProvider>
        </>
    );
}
