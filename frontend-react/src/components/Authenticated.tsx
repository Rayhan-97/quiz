import { useQuery } from '@tanstack/react-query';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import { useRefreshToken } from '../hooks/useAxios';

const Authenticated = () => {
    const { authToken } = useAuth();
    const refresh = useRefreshToken();
    const { isPending } = useQuery({
        queryKey: ['refreshToken'],
        queryFn: async () => await refresh(),
        enabled: !authToken,
        retry: 1
    });

    const location = useLocation();

    if (isPending) {
        return <>
            Nothing to see here;
        </>;
    }

    return !!authToken
        ? <Outlet />
        : <Navigate to={'/login'} state={{ from: location }} replace={true} />;
};

export default Authenticated;
