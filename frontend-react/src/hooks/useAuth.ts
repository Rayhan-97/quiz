import { useContext } from 'react';
import AuthContext from '../context/AuthContext';

export type AuthData = {
    authToken: string;
    setJwtAccessToken: (token: string) => void;
};

const useAuth = (): AuthData => {
    const authContext = useContext(AuthContext);

    if (authContext === undefined) {
        throw new Error('Expected AuthProvider somewhere in the React tree to set context value');
    }

    return {
        authToken: authContext.auth.accessToken,
        setJwtAccessToken: (token: string) => authContext.setAuth(prev => {
            return ({ ...prev, accessToken: token });
        })
    };
};

export default useAuth;
