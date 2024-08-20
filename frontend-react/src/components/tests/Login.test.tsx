import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AxiosError, HttpStatusCode, InternalAxiosRequestConfig } from "axios";
import { Location, MemoryRouter } from "react-router-dom";
import axiosInstance from '../../api/axios';
import { AuthProvider } from "../../context/AuthContext";
import Login from "../Login";

const mockNavigate = jest.fn();
const mockLocation: Location = {
    pathname: "",
    state: undefined,
    key: "",
    search: "",
    hash: ""
};

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom') as any,
    useNavigate: () => mockNavigate,
    useLocation: () => mockLocation
}));

jest.mock('../../api/axios');

describe('Login component test', () => {
    beforeEach(() => {
        renderLogin();
    })

    describe('Successful login path', () => {
        it('logs in and navigates to homepage when successful login', async () => {
            userEvent.type(screen.getByTestId('email-input'), 'a@a.aa');
            userEvent.type(screen.getByTestId('password-input'), 'myValidPassword');

            (axiosInstance.post as jest.Mock).mockResolvedValue({ data: 'successful-access-token' });

            userEvent.click(screen.getByTestId('submit-button'));

            await waitFor(() => {
                expect(axiosInstance.post).toHaveBeenCalled();
                expect(mockNavigate).toHaveBeenCalledWith('', { replace: true });
            });
        })
    })

    describe('Loading spinner for slow submission', () => {
        it('shows loading spinner for slow submissions', async () => {
            userEvent.type(screen.getByTestId('email-input'), 'a@a.aa');
            userEvent.type(screen.getByTestId('password-input'), 'password');

            // create barrier to stop post resolving
            let barrierRelease: () => void;
            const barrier = new Promise<void>(resolve => barrierRelease = resolve);
            (axiosInstance.post as jest.Mock)
                .mockImplementation(() => Promise.resolve(async () => {
                    await barrier;
                    return { data: 'some-token' };
                }))

            const submitButton = screen.getByTestId('submit-button');
            expect(submitButton).toBeEnabled();

            userEvent.click(submitButton);

            const spinner = await screen.findByTestId('loading-spinner');
            expect(spinner).toBeInTheDocument();
            expect(submitButton).toBeDisabled();
            expect(submitButton).toHaveTextContent('');

            // resolve promise to continue with test
            barrierRelease!();

            await waitFor(() => {
                expect(axiosInstance.post).toHaveBeenCalled();
                expect(mockNavigate).toHaveBeenCalled();
                expect(submitButton).toBeEnabled();
                expect(submitButton).toHaveTextContent('Sign in');
            })
        })
    })

    describe('Bad backend API responses', () => {
        it.each([
            ['Server error', { isAxiosError: true }],
            ['Server error', { isAxiosError: false }],
            ['Invalid email/password', buildAxiosError(HttpStatusCode.BadRequest, 'Invalid email/password')]
        ])
            ('shows error message when bad/no response from server',
                async (errorMessage: string, axiosPostError: any) => {
                    userEvent.type(screen.getByTestId('email-input'), 'a@a.aa');
                    userEvent.type(screen.getByTestId('password-input'), 'badPassword');

                    (axiosInstance.post as jest.Mock).mockRejectedValue(axiosPostError);

                    userEvent.click(screen.getByTestId('submit-button'));

                    await waitFor(() => expect(axiosInstance.post).toHaveBeenCalled());

                    const errorElement = await screen.findByText(errorMessage);
                    expect(errorElement).toHaveAttribute('data-cy', 'submit-error');
                })
    })

    it('shows server error message when no response from server', async () => {
        userEvent.type(screen.getByTestId('email-input'), 'a@a.aa');
        userEvent.type(screen.getByTestId('password-input'), 'badPassword');

        (axiosInstance.post as jest.Mock).mockRejectedValue({ isAxiosError: true });

        userEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => expect(axiosInstance.post).toHaveBeenCalled());

        const errorMessage = 'Server error'
        const errorElement = await screen.findByText(errorMessage);
        expect(errorElement).toHaveAttribute('data-cy', 'submit-error');
    })

    describe('UI validation', () => {
        it('shows required errors when login without entering any data', async () => {
            userEvent.click(screen.getByTestId('submit-button'));

            const [emailError, passwordError] = await Promise.all([
                screen.findByTestId('email-error'),
                screen.findByTestId('password-error')
            ])

            expect(emailError).toBeInTheDocument();
            expect(emailError).toHaveTextContent(/email required/i);

            expect(passwordError).toBeInTheDocument();
            expect(passwordError).toHaveTextContent(/password required/i);
        })

        it('shows invalid email error when submitting bad email', async () => {
            userEvent.type(screen.getByTestId('password-input'), 'validPassword');

            const invalidEmail = 'not an email';
            userEvent.type(screen.getByTestId('email-input'), invalidEmail);

            userEvent.click(screen.getByTestId('submit-button'));

            const emailError = await screen.findByTestId('email-error');

            expect(emailError).toBeInTheDocument();
            expect(emailError).toHaveTextContent(/invalid email/i);
        })
    })
})

const renderLogin = () => {
    return render(<Login />, {
        wrapper: ({ children }) => (<AuthProvider><MemoryRouter>{children}</MemoryRouter></AuthProvider>)
    });
};

function buildAxiosError(statusCode: HttpStatusCode, errorMessage: string): AxiosError<unknown, any> {
    return {
        response: {
            status: statusCode,
            data: { errorMessage: errorMessage },
            config: {} as InternalAxiosRequestConfig,
            headers: {},
            statusText: ''
        },
        isAxiosError: true,
        toJSON: () => { throw new Error("Function not implemented.") },
        name: "",
        message: ""
    };
}
