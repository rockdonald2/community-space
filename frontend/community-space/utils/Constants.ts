export const isBrowser = typeof window !== 'undefined';
export const GATEWAY_USERS_WS = process.env.CS_GATEWAY_USERS_WS || 'http://localhost:8080/stomp/users';
export const GATEWAY_URL = process.env.CS_GATEWAY_URL || 'http://localhost:8080';