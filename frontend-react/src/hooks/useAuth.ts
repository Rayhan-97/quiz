import { useContext } from 'react';
import AuthContext from '../context/AuthContext';


const useAuth = () => {
    const authContext = useContext(AuthContext);

    if (authContext === undefined) {
        throw new Error('Expected AuthProvider somewhere in the React tree to set context value');
    }

    const setAccessToken = (token: string) => {
        authContext.setAuth({ accessToken: token });
    }

    return {
        authToken: authContext.auth.accessToken,
        setJwtAccessToken: setAccessToken
    };
};

export default useAuth;
