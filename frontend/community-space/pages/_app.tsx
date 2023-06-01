import '@/styles/globals.scss';
import type { AppProps } from 'next/app';
import Head from 'next/head';
import { Experimental_CssVarsProvider as CssVarsProvider, StyledEngineProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Layout from '@/components/Layout';
import AuthContextProvider from '@/utils/AuthContext';
import { RouteGuard } from '@/utils/RouteGuard';
import CrossContextProvider from '@/utils/CrossContext';
import { CookiesProvider } from 'react-cookie';
import SnackbarProvider from '@/utils/SnackbarProvider';

export default function App({ Component, pageProps }: AppProps) {
    return (
        <>
            <Head>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <StyledEngineProvider injectFirst>
                <CssVarsProvider>
                    <CssBaseline enableColorScheme />
                    <CookiesProvider>
                        <SnackbarProvider>
                            <CrossContextProvider>
                                <AuthContextProvider>
                                    <RouteGuard>
                                        <Layout>
                                            <Component {...pageProps} />
                                        </Layout>
                                    </RouteGuard>
                                </AuthContextProvider>
                            </CrossContextProvider>
                        </SnackbarProvider>
                    </CookiesProvider>
                </CssVarsProvider>
            </StyledEngineProvider>
        </>
    );
}
