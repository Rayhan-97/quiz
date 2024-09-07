import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { HttpStatusCode } from 'axios';
import axiosInstance from '../../api/axios';
import Register from '../Register';
import { buildAxiosError } from './util';
import { MemoryRouter } from 'react-router';

const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

jest.mock('../../api/axios');

describe('Registration component test', () => {
  beforeEach(() => {
    render(<Register />, {
      wrapper: ({ children }) => (
        <MemoryRouter>
          {children}
        </MemoryRouter>
      )
    });
  });

  describe('Successful register path', () => {
    it('registers and navigates to /login', async () => {
      (axiosInstance.post as jest.Mock).mockResolvedValue({ status: 200 });

      inputValidData();
      submit();

      await waitFor(() => {
        expect(axiosInstance.post).toHaveBeenCalled();
        expect(mockNavigate).toHaveBeenCalledWith('/login');
      });
    });
  });

  describe('Bad backend API response', () => {
    it('shows error message when bad request response from server', async () => {
      inputValidData();
      submit();

      const errorMessage = 'Something has gone terribly wrong';
      (axiosInstance.post as jest.Mock)
        .mockRejectedValueOnce(
          buildAxiosError(HttpStatusCode.BadRequest, errorMessage)
        );

      await waitFor(() => expect(axiosInstance.post).toHaveBeenCalled());

      const errorElement = await screen.findByText(errorMessage);
      expect(errorElement).toHaveAttribute('data-cy', 'submit-error');
    });
  });

  describe('UI validation', () => {
    it('shows required errors when register without data', async () => {
      submit();

      const allErrors = await Promise.all([
        screen.findByTestId('username-error'),
        screen.findByTestId('email-error'),
        screen.findByTestId('password-error'),
        screen.findByTestId('confirmPassword-error'),
      ]);
      allErrors.forEach(error => expect(error).toBeInTheDocument());
      expect(screen.queryAllByText(/required/i)).toHaveLength(4);
    });

    it.each([
      ['short', /username must be at least 6 characters/i],
      ['a'.repeat(32 + 1), /username must be less than 33 characters/i],
      ['*********', /username can only contain values a-zA-Z0-9_-/i],
    ])(
      'shows invalid username errors when submitting bad usernames',
      async (invalidUsername, expectedError) => {
        userEvent.type(screen.getByTestId('username-input'), invalidUsername);
        submit();

        const usernameError = await screen.findByTestId('username-error');
        expect(usernameError).toBeInTheDocument();
        expect(usernameError).toHaveTextContent(expectedError);
      }
    );

    it('shows invalid email error when submitting bad email', async () => {
      const invalidEmail = 'not an email';
      userEvent.type(screen.getByTestId('email-input'), invalidEmail);

      submit();

      const emailError = await screen.findByTestId('email-error');

      expect(emailError).toBeInTheDocument();
      expect(emailError).toHaveTextContent(/invalid email/i);

    });

    it('shows passwords do not match when submitting mismatching passwords', async () => {
      userEvent.type(screen.getByTestId('password-input'), 'password 1');
      userEvent.type(screen.getByTestId('confirmPassword-input'), 'different password');
      submit();

      const passwordErrors = await Promise.all([
        screen.findByTestId('password-error'),
        screen.findByTestId('confirmPassword-error')
      ]);

      passwordErrors.forEach(error => {
        expect(error).toBeInTheDocument();
        expect(error).toHaveTextContent(/passwords do not match/i);
      });
    });

    it.each([
      ['short', /password must be at least 8 characters/i],
      ['a'.repeat(128), /password must be less than 128 characters/i],
    ])(
      'shows invalid password error when submit bad password',
      async (invalidPassword, expectedError) => {
        userEvent.type(screen.getByTestId('password-input'), invalidPassword);
        submit();

        const passwordError = await screen.findByTestId('password-error');
        expect(passwordError).toBeInTheDocument();
        expect(passwordError).toHaveTextContent(expectedError);
      }
    );
  });

  describe('Loading spinner for slow submission', () => {
    it('shows loading spinner and disabled submit button for slow submissions', async () => {
      inputValidData();

      // Create a promise that will resolve when the condition is met
      // to stop axios.post from resolving straight away
      let responseResolve: ({ status }: { status: number }) => void;
      (axiosInstance.post as jest.Mock)
        .mockImplementation(
          () => new Promise(resolve => responseResolve = resolve)
        );

      const submitButton = screen.getByTestId('submit-button');
      expect(submitButton).toBeEnabled();

      submit();

      const spinner = await screen.findByTestId('loading-spinner');
      expect(spinner).toBeInTheDocument();
      expect(submitButton).toBeDisabled();
      expect(submitButton).toHaveTextContent('');

      expect(mockNavigate).toHaveBeenCalledTimes(0);
      // Resolve the promise to continue with the test
      responseResolve!({ status: 200 });

      await waitFor(() => {
        expect(axiosInstance.post).toHaveBeenCalled();
        expect(mockNavigate).toHaveBeenCalledWith('/login');
      });
    });
  });

  const inputValidData = () => {
    userEvent.type(screen.getByTestId('username-input'), 'validUsername');
    userEvent.type(screen.getByTestId('email-input'), 'a@a.aa');
    userEvent.type(screen.getByTestId('password-input'), 'validPassword');
    userEvent.type(screen.getByTestId('confirmPassword-input'), 'validPassword');
  };

  const submit = () => userEvent.click(screen.getByTestId('submit-button'));
});
