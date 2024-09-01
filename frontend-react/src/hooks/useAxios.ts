import { HttpStatusCode, InternalAxiosRequestConfig, isAxiosError } from 'axios';
import { useEffect } from 'react';
import axios from '../api/axios';
import { ENDPOINTS } from '../util/constants';
import useAuth from './useAuth';

type CustomRequestConfig = InternalAxiosRequestConfig & {
    sent?: boolean
};

const useAxios = () => {
    const refresh = useRefreshToken();
    const { authToken } = useAuth();

    useEffect(() => {
        const requestInterceptor = axios.interceptors.request.use(config => {
            if (!config.headers.Authorization) {
                console.log('Setting new bearer token');
                config.headers.setAuthorization(`Bearer ${authToken}`);
            }
            return config;
        },
            error => Promise.reject(error)
        );

        const responseInterceptor = axios.interceptors.response.use(
            response => response,
            async error => {
                if (!isAxiosError(error)) {
                    return Promise.reject(error);
                }

                const prevRequestConfig = error.config as CustomRequestConfig;
                console.log('url: ' + prevRequestConfig.url);

                if (prevRequestConfig.sent || error.response?.status !== HttpStatusCode.Forbidden) {
                    return Promise.reject(error);
                }

                prevRequestConfig.sent = true;
                const newAccessToken = await refresh();
                prevRequestConfig.headers.setAuthorization(`Bearer ${newAccessToken}`);
                return axios(prevRequestConfig);
            }
        );

        return () => {
            axios.interceptors.request.eject(requestInterceptor);
            axios.interceptors.response.eject(responseInterceptor);
        };
    }, [authToken, refresh]);
    
    return axios;
};

export const useRefreshToken = () => {
    const { setJwtAccessToken } = useAuth();

    const refresh = async (signal?: AbortSignal) => {

        console.log('Trying request to /refresh...');

        const response = await axios.get(ENDPOINTS.refresh, {signal: signal});
        const token: string = response.data;

        console.log('Hit /refresh and acquired new access token: ' + token);
        setJwtAccessToken(token);

        return token;
    };
    return refresh;
};

export default useAxios;