/**
 * The base backend URL for the API, does not include trailing slash
 * @constant
 */
export const BACKEND_URL = process.env.REACT_APP_BACKEND_URL ?? 'http://localhost:8080';

/**
 * The endpoints for the backend API, does include leading slash
 * @constant
 */
export const ENDPOINTS = {
    login: '/login',
    register: '/register',
    refresh: '/refresh',
};
