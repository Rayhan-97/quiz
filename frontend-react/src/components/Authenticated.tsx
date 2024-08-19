import { Navigate, Outlet, useLocation } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

const Authenticated = () => {
    const { authToken } = useAuth();
    const location = useLocation();

    return !!authToken
        ? <Outlet />
        : <Navigate to={'/login'} state={{ from: location }} replace={true} />;
};

export default Authenticated;
