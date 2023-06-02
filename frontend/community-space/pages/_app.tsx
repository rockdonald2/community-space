import '@/styles/globals.scss';
import type { AppProps } from 'next/app';
import Head from 'next/head';
import {
    Experimental_CssVarsProvider as CssVarsProvider,
    StyledEngineProvider,
    experimental_extendTheme as extendTheme,
} from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Layout from '@/components/Layout';
import AuthContextProvider from '@/utils/AuthContext';
import { RouteGuard } from '@/utils/RouteGuard';
import CrossContextProvider from '@/utils/CrossContext';
import { CookiesProvider } from 'react-cookie';
import SnackbarProvider from '@/utils/SnackbarProvider';
import { Nunito, Roboto } from 'next/font/google';

const nunito = Nunito({
    subsets: ['latin-ext'],
});
const roboto = Roboto({
    subsets: ['latin-ext'],
    weight: ['300', '400', '500', '700'],
});

const theme = extendTheme({
    typography: {
        fontFamily: `${roboto.style.fontFamily}, ${nunito.style.fontFamily}, sans-serif`,
    },
});

export default function App({ Component, pageProps }: AppProps) {
    return (
        <>
            <Head>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <StyledEngineProvider injectFirst>
                <CssVarsProvider theme={theme}>
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
