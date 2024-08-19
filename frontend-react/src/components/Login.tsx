import { HttpStatusCode, isAxiosError } from "axios";
import clsx from 'clsx';
import { useForm, UseFormRegisterReturn } from 'react-hook-form';
import { Link, useLocation, useNavigate } from "react-router-dom";
import axios from "../api/axios";
import useAuth from "../hooks/useAuth";
import capitalize from "../util/stringUtils";

const LOGIN_ENDPOINT = '/login';

type FormValues = {
    email: string;
    password: string
}

type SubmissionResponseError = {
    errorCode: number,
    errorMessage: string
}

const Login = () => {
    const { setJwtAccessToken } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || '';

    const {
        register,
        handleSubmit,
        setError,
        formState: { errors }
    } = useForm<FormValues>({ defaultValues: { email: '', password: '' } });

    const onSubmit = async ({ email, password }: FormValues) => {
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
                throw error; // FIXME: add fallback or handle here?
            }

            const response = error.response;
            if (!response) {
                setError('root', { message: 'Server error' });
            }
            else if (response?.status === HttpStatusCode.BadRequest) {
                const { errorMessage }: SubmissionResponseError = response.data;
                setError('root', { message: errorMessage });
            }


        }
    }

    return (
        <>
            <div className={'login-form-container'}>
                <form className={'form'} onSubmit={handleSubmit(onSubmit)}>
                    <FormField
                        name={'email'} error={errors.email?.message}
                        register={register('email', { required: 'Email required' })}
                    />
                    <FormField
                        name={'password'} error={errors.password?.message}
                        register={register('password', { required: 'Password required' })}
                    />

                    {errors.root && <ErrorText name='submit' errorMessage={errors.root.message!} />}

                    <button>Sign in</button>
                </form>
                <Link to='/register'>Sign Up</Link>
            </div>
        </>
    )
}

type FormFieldType = {
    name: string;
    fieldLabel?: string;
    error?: string;
    register: UseFormRegisterReturn;
}

const FormField = ({ name, fieldLabel = capitalize(name), error, register }: FormFieldType) => {
    return (
        <div className={clsx('form-field-container', error && 'form-error')}>
            <div className={'form-field-name-container'}>
                <label className={'form-label'} htmlFor={name}>{fieldLabel}</label>
                {error && <ErrorText name={name} errorMessage={error} />
                }
            </div>

            <input data-cy={`${name}-input`} id={name} {...register} />
        </div>
    );
}

const ErrorText = ({ name, errorMessage }: { name: string, errorMessage: string }): JSX.Element => {
    return (
        <>
            <span data-cy={`${name}-error`} className={'errortext'}>{errorMessage}</span>
        </>
    );
};

export default Login;