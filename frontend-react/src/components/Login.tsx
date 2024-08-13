import { HttpStatusCode, isAxiosError } from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "../api/axios";
import useAuth from "../hooks/useAuth";

const LOGIN_ENDPOINT = '/login';

type FormValues = {
    email: string;
    password: string
}

const Login = () => {
    const { setJwtAccessToken } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || '';

    const handleSubmit = async ({ email, password }: FormValues) => {
        try {
            const response = await axios.post(
                LOGIN_ENDPOINT,
                JSON.stringify({ email, password }),
                {
                    headers: { 'Content-Type': 'application/json' },
                    // withCredentials: true
                }
            )
            const token = response.data;
            setJwtAccessToken(token);
            navigate(from, { replace: true })
        } catch (error) {
            if (!isAxiosError(error)) {
                throw error;
            }

            const response = error.response;
            if (!response) {
                console.log('Server error');
            }
            if (response?.status === HttpStatusCode.BadRequest) {
                console.log('Invalid email / password');
                console.log(response.data);
            }
        }
    }

    return (
        <>
            <div>Login page! Welcome!</div>
            <button onClick={() => handleSubmit({ email: 'a@a.aabc', password: 'abcdefgh' })}>Submit</button>
        </>
    )
}

export default Login;