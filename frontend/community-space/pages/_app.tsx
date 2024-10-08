import '@/styles/globals.scss';
import type { AppProps } from 'next/app';
import Head from 'next/head';
import {
    Experimental_CssVarsProvider as CssVarsProvider,
    StyledEngineProvider,
    experimental_extendTheme as extendTheme,
} from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Layout from '@/components/layout/Layout';
import AuthContextProvider from '@/utils/AuthContext';
import { RouteGuard } from '@/utils/RouteGuard';
import CrossContextProvider from '@/utils/CrossContext';
import { CookiesProvider } from 'react-cookie';
import SnackbarProvider from '@/utils/SnackbarProvider';
import { Nunito } from 'next/font/google';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/en-gb';

const nunito = Nunito({ weight: ['300', '400', '700'], subsets: ['latin'] });

const theme = extendTheme({
    typography: {
        fontFamily: `${nunito.style.fontFamily}, sans-serif`,
    },
});

export default function App({ Component, pageProps }: AppProps) {
    return (
        <>
            <Head>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <StyledEngineProvider injectFirst>
                <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale={'en-gb'}>
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
                </LocalizationProvider>
            </StyledEngineProvider>
        </>
    );
}
