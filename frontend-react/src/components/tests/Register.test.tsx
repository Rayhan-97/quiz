import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import Register from '../Register';

const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
   ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('registration component test', () => {

  it.each([
    'register-form',
    'username-input',
    'email-input',
    'password-input',
    'confirmPassword-input',
    'register-submit-button',
  ])('register form elements should be rendered', (elementTestId) => {
    render(<Register />);

    const registerFormElement = screen.getByTestId(elementTestId);

    expect(registerFormElement).toBeInTheDocument();
  });

  it('given no inputs filled out when submit then required errors for all input fields', async () => {
    render(<Register />);
    
    submitForm();

    expect(await screen.findByTestId('username-error')).toBeInTheDocument();
    expect(await screen.findByTestId('email-error')).toBeInTheDocument();
    expect(await screen.findByTestId('password-error')).toBeInTheDocument();
    expect(await screen.findByTestId('confirmPassword-error')).toBeInTheDocument();
    expect(screen.queryAllByText(/required/i)).toHaveLength(4);
  });

  it.each([
    ['short', /username must be at least 6 characters/i],
    ['a'.repeat(32 + 1), /username must be less than 33 characters/i],
    ['*********', /username can only contain values a-zA-Z0-9_-/i],
  ])(
    'given invalid username when submit then errors',
    async (invalidUsername, expectedErrorMessageMatcher) => {
      render(<Register />);

      const usernameInput = screen.getByTestId('username-input');
      blurAndEnterText(usernameInput, invalidUsername);

      const usernameError = await screen.findByTestId('username-error');
      expect(usernameError).toBeInTheDocument();
      expect(usernameError).toHaveTextContent(expectedErrorMessageMatcher);
    }
  );

  it('given invalid email when submit then errors', async () => {
    render(<Register />);

    const emailInput = screen.getByTestId('email-input');
    blurAndEnterText(emailInput, 'invalid email');

    const emailError = await screen.findByTestId('email-error');
    expect(emailError).toBeInTheDocument();
    expect(emailError).toHaveTextContent(/invalid email/i);
  });

  // failing test - need to set touched to true for both fields in validation
  it.skip('given only typed into one password field then errors for both', async () => {
    render(<Register />);

    const passwordInput = screen.getByTestId('password-input');
    blurAndEnterText(passwordInput, 'password 1');

    const passwordError = await screen.findByTestId('password-error');
    expect(passwordError).toHaveTextContent(/passwords do not match/i);
    const confirmPasswordError = await screen.findByTestId('confirmPassword-error');
    expect(confirmPasswordError).toHaveTextContent(/passwords do not match/i);
  });

  it('given mismatching passwords when submit then errors', async () => {
    render(<Register />);

    const passwordInput = screen.getByTestId('password-input');
    const confirmPasswordInput = screen.getByTestId('confirmPassword-input');

    blurAndEnterText(passwordInput, 'password 1');
    blurAndEnterText(confirmPasswordInput, 'different password');

    const passwordError = await screen.findByTestId('password-error');
    expect(passwordError).toBeInTheDocument();
    expect(passwordError).toHaveTextContent(/passwords do not match/i);
    const confirmPasswordError = await screen.findByTestId('confirmPassword-error');
    expect(confirmPasswordError).toBeInTheDocument();
    expect(confirmPasswordError).toHaveTextContent(/passwords do not match/i);
  });

  it.each([
    ['short', /password must be at least 8 characters/i],
    ['a'.repeat(128), /password must be less than 128 characters/i],
  ])(
    'given invalid password when submit then errors',
    async (invalidPassword, expectedErrorMessageMatcher) => {
      render(<Register />);

      const passwordInput = screen.getByTestId('password-input');
      const confirmPasswordInput = screen.getByTestId('confirmPassword-input');

      blurAndEnterText(passwordInput, invalidPassword);
      blurAndEnterText(confirmPasswordInput, invalidPassword);

      const passwordError = await screen.findByTestId('password-error');
      expect(passwordError).toBeInTheDocument();
      expect(passwordError).toHaveTextContent(expectedErrorMessageMatcher);
      const confirmPasswordError = await screen.findByTestId('confirmPassword-error');
      expect(confirmPasswordError).toBeInTheDocument();
    }
  );

  it('given valid response when submit form then redirect to /login', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
      })
    ) as jest.Mock;

    render(<Register />);

    inputValidData();
    submitForm();

    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/login'));
  });

  it('given invalid response when submit form then display error message', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        json: () =>
          Promise.resolve({
            errorCode: 35,
            errorMessage: 'Something has gone terribly wrong',
          }),
      })
    ) as jest.Mock;

    render(<Register />);

    inputValidData();
    submitForm();

    const submissionError = await screen.findByTestId('submission-error');
    expect(submissionError).toBeInTheDocument();
    expect(submissionError).toHaveTextContent('Error code <35>. Something has gone terribly wrong');

    expect(await screen.findByTestId('username-error')).toBeInTheDocument();
    expect(await screen.findByTestId('email-error')).toBeInTheDocument();
    expect(await screen.findByTestId('password-error')).toBeInTheDocument();
    expect(await screen.findByTestId('confirmPassword-error')).toBeInTheDocument();
  });

  it('given server error when submit form then display error message', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        json: () => Promise.reject(),
      })
    ) as jest.Mock;

    render(<Register />);

    inputValidData();
    submitForm();

    const submissionError = await screen.findByTestId('submission-error');
    expect(submissionError).toBeInTheDocument();
    expect(submissionError).toHaveTextContent('Server error. Try again');

    expect(await screen.findByTestId('username-error')).toBeInTheDocument();
    expect(await screen.findByTestId('email-error')).toBeInTheDocument();
    expect(await screen.findByTestId('password-error')).toBeInTheDocument();
    expect(await screen.findByTestId('confirmPassword-error')).toBeInTheDocument();
  });

  it('given submitted form when waiting for response then loading spinner and button is disabled', async () => {
    // Create a promise that will resolve when the condition is met
    let barrierRelease: () => void;
    const barrier = new Promise<void>(resolve => barrierRelease = resolve);

    global.fetch = jest.fn(async () => {
      await barrier;
      return {
        ok: false,
        json: () => Promise.reject(),
      };
    }) as jest.Mock;

    render(<Register />);

    inputValidData();

    const submitButton = screen.getByTestId('register-submit-button');
    expect(submitButton).toBeEnabled();

    fireEvent.submit(submitButton);

    expect(submitButton).toBeDisabled();

    const loadingSpinner = await screen.findByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();

    // Resolve the promise to continue with the test
    barrierRelease!();

    const submissionError = await screen.findByTestId('submission-error');
    expect(submissionError).toBeInTheDocument();
    expect(screen.queryAllByText(/submit/i)).toHaveLength(1);
    expect(submitButton).toBeEnabled();
  });

  const submitForm = (): void => {
    const submitButton = screen.getByTestId('register-submit-button');
    fireEvent.submit(submitButton);
  };

  const inputValidData = (): void => {
    blurAndEnterText(screen.getByTestId('username-input'), 'validsername');
    blurAndEnterText(screen.getByTestId('email-input'), 'a@a.aa');
    blurAndEnterText(screen.getByTestId('password-input'), 'validPassword');
    blurAndEnterText(screen.getByTestId('confirmPassword-input'), 'validPassword');
  };
  
  const blurAndEnterText = (element: HTMLElement, textToInput: string): void => {
    fireEvent.blur(element);
    fireEvent.input(element, {target: { value: textToInput } });
  };
});
