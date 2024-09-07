import { HttpStatusCode, isAxiosError } from 'axios';
import clsx from 'clsx';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import axios from '../api/axios';
import useAuth from '../hooks/useAuth';
import { ENDPOINTS } from '../util/constants';
import { EMAIL_REGEX } from '../util/registerValidator';
import Icons from './Icons';
import { spaceEnterHandler } from './Nav';

type FormValues = {
    email: string;
    password: string
}

const Login = () => {
    const { setJwtAccessToken } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || '/';
    const [isPasswordHidden, setIsPasswordHidden] = useState(true);
    const {
        register,
        handleSubmit,
        setError,
        formState: { errors, isSubmitting }
    } = useForm<FormValues>({ defaultValues: { email: '', password: '' } });

    const onSubmit = async ({ email, password }: FormValues) => {
        try {
            const response = await axios.post(
                ENDPOINTS.login,
                JSON.stringify({ email, password }),
                {
                    headers: { 'Content-Type': 'application/json' }
                }
            );
            setJwtAccessToken(response.data);
            navigate(from, { replace: true });
        } catch (error) {
            if (isAxiosError(error) && error.response?.status === HttpStatusCode.BadRequest) {
                const { errorMessage } = error.response.data;
                setError('root', { message: errorMessage });
                return;
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

    const password = {
        error: errors.password?.message,
        register: register('password', { required: 'Password required' })
    };

    return (<>
        <div className={'form-container'}>
            <div className="form-wrapper">
                <h1> Welcome to<br />OurQuiz </h1>

                <form onSubmit={handleSubmit(onSubmit)}>
                    {
                        errors.root &&
                        <div data-cy={'submit-error'} className={'form-error'}>
                            {errors.root.message!}
                            <Icons.Error />
                        </div>
                    }

                    <div className="fields-wrapper">
                        <div className={clsx('field-container', email.error && 'field-error')}>
                            <label htmlFor={'email'}>Email address</label>
                            <input data-cy={'email-input'} id={'email'} type={'email'} {...email.register} />
                            {email.error && <span data-cy={'email-error'}>{email.error}</span>}
                        </div>
                        <div className={clsx('field-container', password.error && 'field-error')}>
                            <label htmlFor={'password'}>Password</label>
                            <div className="password-input-wrapper">
                                <input
                                    data-cy={'password-input'}
                                    id={'password'}
                                    type={isPasswordHidden ? 'password' : 'text'}
                                    {...password.register}
                                />
                                <ShowHidePasswordIcons
                                    isHidden={isPasswordHidden}
                                    handler={toggleIsPasswordHidden}
                                />
                            </div>
                            {password.error && <span data-cy={'password-error'}>{password.error}</span>}
                        </div>
                    </div>

                    <button className={'primary'} data-cy={'submit-button'} disabled={isSubmitting}>
                        {isSubmitting ? <LoadingSpinner /> : 'Sign in'}
                    </button>
                </form>
            </div>
            <span>
                Don&apos;t have an account?&nbsp;
                <Link className={'link'} to='/register'>Sign up</Link>
            </span>
        </div>
    </>
    );
};

type ShowHidePasswordIconsProps = {
    isHidden: boolean,
    handler: () => void
}

export const ShowHidePasswordIcons = ({ isHidden, handler }: ShowHidePasswordIconsProps) => {
    return (<>
        <Icons.OpenEye
            className={clsx(!isHidden && 'hidden')}
            focusable={true}
            tabIndex={0}
            onClick={handler}
            onKeyDown={e => spaceEnterHandler(e, handler)}
        />
        <Icons.ClosedEye
            className={clsx(isHidden && 'hidden')}
            focusable={true}
            tabIndex={0}
            onClick={handler}
            onKeyDown={e => spaceEnterHandler(e, handler)}
        />
    </>
    );
};

export const LoadingSpinner = () => {
    return (<>
        <div data-cy={'loading-spinner'}></div>
    </>
    );
};

export default Login;