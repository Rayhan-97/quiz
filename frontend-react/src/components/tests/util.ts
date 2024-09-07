import { HttpStatusCode, AxiosError, InternalAxiosRequestConfig } from 'axios';


export function buildAxiosError(statusCode: HttpStatusCode, errorMessage: string): AxiosError<unknown> {
    return {
        response: {
            status: statusCode,
            data: { errorMessage: errorMessage },
            config: {} as InternalAxiosRequestConfig,
            headers: {},
            statusText: ''
        },
        isAxiosError: true,
        toJSON: () => { throw new Error('Function not implemented.'); },
        name: '',
        message: ''
    };
}
