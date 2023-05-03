export const isBrowser = typeof window !== 'undefined';
export const GATEWAY_ACCOUNT_WS = process.env.CS_GATEWAY_ACCOUNT_WS || 'http://localhost:8080/stomp/account';
export const GATEWAY_URL = process.env.CS_GATEWAY_URL || 'http://localhost:8080';
