import { HttpStatusCode, isAxiosError } from 'axios';
import clsx from 'clsx';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import axios from '../api/axios';
import { ENDPOINTS } from '../util/constants';
import { EMAIL_REGEX, USERNAME_REGEX } from '../util/registerValidator';
import Icons from './Icons';
import { LoadingSpinner, ShowHidePasswordIcons } from './Login';

type FormValues = {
    email: string;
    username: string;
    password: string;
    confirmPassword: string;
}

const Register = () => {
    const navigate = useNavigate();
    const [isPasswordHidden, setIsPasswordHidden] = useState(true);
    const {
        register,
        watch,
        handleSubmit,
        setError,
        formState: { errors, isSubmitting }
    } = useForm<FormValues>();

    const onSubmit = async ({ username, email, password }: FormValues) => {
        try {
            const response = await axios.post(
                ENDPOINTS.register,
                JSON.stringify({ username, email, password }),
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: false
                },
            );
            if (response.status === HttpStatusCode.Ok) {
                navigate('/login');
            }
        } catch (error) {
            if (isAxiosError(error) && error.response?.status === HttpStatusCode.BadRequest) {
                const { errorMessage } = error.response.data;
                setError('root', { message: errorMessage });
                return;
            }

            if (!isAxiosError(error)) {
                console.error(error);
            }

            setError('root', { message: 'Server error' });
        }
    };

    const toggleIsPasswordHidden = () => {
        setIsPasswordHidden(prev => !prev);
    };

    const email = {
        error: errors.email?.message,
        register: register('email', {
            required: 'Email required',
            pattern: { value: EMAIL_REGEX, message: 'Invalid email' }
        })
    };

    const username = {
        error: errors.username?.message,
        register: register('username', {
            required: 'Username required',
            minLength: { value: 6, message: 'Username must be at least 6 characters' },
            maxLength: { value: 32, message: 'Username must be less than 33 characters' },
            pattern: { value: USERNAME_REGEX, message: 'Username can only contain values a-zA-Z0-9_-' }
        })
    };

    const password = {
        error: errors.password?.message,
        register: register('password', {
            required: 'Password required',
            minLength: { value: 8, message: 'Password must be at least 8 characters' },
            maxLength: { value: 127, message: 'Password must be less than 128 characters' },
            validate: value => (watch('confirmPassword') === value || 'Passwords do not match')
        })
    };

    const confirmPassword = {
        error: errors.confirmPassword?.message,
        register: register('confirmPassword', {
            required: 'Password required',
            validate: value => (watch('password') === value || 'Passwords do not match')
        })
    };

    return (<>
        <div className={'form-container'} data-cy={'register-form'}>
            <div className={'form-wrapper'}>
                <h1> Sign up </h1>

                <form onSubmit={handleSubmit(onSubmit)}>
                    {
                        errors.root &&
                        <div data-cy={'submit-error'} className={'form-error'}>
                            {errors.root.message!}
                            <Icons.Error />
                        </div>
                    }

                    <div className={'fields-wrapper'}>
                        <div className={clsx('field-container', email.error && 'field-error')}>
                            <label htmlFor={'email'}>Email address</label>
                            <input data-cy={'email-input'} id={'email'} type={'email'} {...email.register} />
                            {email.error && <span data-cy={'email-error'}>{email.error}</span>}
                        </div>

                        <div className={clsx('field-container', username.error && 'field-error')}>
                            <label htmlFor={'username'}>Username</label>
                            <input data-cy={'username-input'} id={'username'} type={'text'} {...username.register} />
                            {username.error && <span data-cy={'username-error'}>{username.error}</span>}
                        </div>

                        <div className={clsx('field-container', password.error && 'field-error')}>
                            <label htmlFor={'password'}>Password</label>
                            <div className={'password-input-wrapper'}>
                                <input
                                    data-cy={'password-input'}
                                    id={'password'}
                                    type={isPasswordHidden ? 'password' : 'text'}
                                    {...password.register}
                                />
                                <ShowHidePasswordIcons isHidden={isPasswordHidden} handler={toggleIsPasswordHidden} />
                            </div>
                            {password.error && <span data-cy={'password-error'}>{password.error}</span>}
                        </div>

                        <div className={clsx('field-container', confirmPassword.error && 'field-error')}>
                            <label htmlFor={'confirmPassword'}>Confirm password</label>
                            <div className={'password-input-wrapper'}>
                                <input
                                    data-cy={'confirmPassword-input'}
                                    id={'confirmPassword'}
                                    type={isPasswordHidden ? 'password' : 'text'}
                                    {...confirmPassword.register}
                                />
                            </div>
                            {confirmPassword.error && <span data-cy={'confirmPassword-error'}>{confirmPassword.error}</span>}
                        </div>
                    </div>

                    <button className={'primary'} data-cy={'submit-button'} disabled={isSubmitting}>
                        {isSubmitting ? <LoadingSpinner /> : 'Sign Up'}
                    </button>
                </form>
            </div>
            <span>
                Already have an account?&nbsp;
                <Link className={'link'} to={'/login'}>Sign in</Link>
            </span>
        </div>
    </>
    );
};

export default Register;
