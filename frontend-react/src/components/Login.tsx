import { HttpStatusCode, isAxiosError } from 'axios';
import clsx from 'clsx';
import { useForm, UseFormRegisterReturn } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import axios from '../api/axios';
import useAuth from '../hooks/useAuth';
import { EMAIL_REGEX } from '../util/registerValidator';
import capitalize from '../util/stringUtils';
import { Spinner } from 'react-bootstrap';

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
        formState: { errors, isSubmitting }
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
            );
            const token: string = response.data;
            setJwtAccessToken(token);
            navigate(from, { replace: true });
        } catch (error) {
            if (isAxiosError(error) && error.response?.status === HttpStatusCode.BadRequest) {
                const { errorMessage }: SubmissionResponseError = error.response.data;
                setError('root', { message: errorMessage });
                return;
            }

            if (!isAxiosError(error)) {
                console.error(error);
            }

            setError('root', { message: 'Server error' });
        }
    };

    return (
        <>
            <div className={'form-container'}>
                <form className={'form'} onSubmit={handleSubmit(onSubmit)}>
                    <FormField
                        name={'email'} error={errors.email?.message}
                        register={register('email', {
                            required: 'Email required',
                            pattern: { value: EMAIL_REGEX, message: 'Invalid email' }
                        })}
                    />
                    <FormField
                        name={'password'} error={errors.password?.message}
                        register={register('password', { required: 'Password required' })}
                    />

                    {errors.root && <ErrorText name='submit' errorMessage={errors.root.message!} />}

                    <button data-cy={'submit-button'} disabled={isSubmitting}>
                        {isSubmitting ? <LoadingSpinner /> : 'Sign in'}
                    </button>
                </form>
                <Link to='/register'>Sign Up</Link>
            </div>
        </>
    );
};

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
};

const ErrorText = ({ name, errorMessage }: { name: string, errorMessage: string }): JSX.Element => {
    return (
        <>
            <span data-cy={`${name}-error`} className={'errortext'}>{errorMessage}</span>
        </>
    );
};

const LoadingSpinner = ({ dataCyPrefix }: { dataCyPrefix?: string }) => {
    dataCyPrefix = dataCyPrefix ? `${dataCyPrefix}-` : '';

    return (
        <>
            <Spinner
                data-cy={`${dataCyPrefix}loading-spinner`}
                animation="border"
            />
        </>

    );
};

export default Login;