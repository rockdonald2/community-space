export const isBrowser = typeof window !== 'undefined';
export const GATEWAY_WS = process.env.CS_GATEWAY_WS || 'http://localhost:8080/stomp';
export const GATEWAY_URL = process.env.CS_GATEWAY_URL || 'http://localhost:8080';