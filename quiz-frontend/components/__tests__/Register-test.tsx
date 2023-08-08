import { render, screen, fireEvent, waitFor } from '@testing-library/react-native';

import Register from '../../app/register';
import { useRouter } from 'expo-router';
import { TextMatch, TextMatchOptions } from '@testing-library/react-native/build/matches';
import { FindByQuery, GetByQuery, QueryAllByQuery } from '@testing-library/react-native/build/queries/makeQueries';
import { CommonQueryOptions } from '@testing-library/react-native/build/queries/options';

jest.mock('expo-router', () => ({
  useRouter: jest.fn(),
}));

describe('registration component test', () => {
  let getByTestId: GetByQuery<TextMatch, CommonQueryOptions & TextMatchOptions>;
  let findByTestId: FindByQuery<TextMatch, CommonQueryOptions & TextMatchOptions>;
  let queryAllByText: QueryAllByQuery<TextMatch, CommonQueryOptions & TextMatchOptions>;

  beforeEach(() => {
    ({ getByTestId, findByTestId, queryAllByText } = render(<Register />));
  });

  it.each(['username-input', 'email-input', 'password-input', 'confirmPassword-input', 'register-submit-button'])(
    'register form elements should be rendered',
    elementTestId => {
      const registerFormElement = getByTestId(elementTestId);

      expect(registerFormElement).toBeTruthy();
    },
  );

  it('given no inputs filled out when submit then required errors for all input fields', async () => {
    submitForm();

    expect(await findByTestId('username-error')).toBeTruthy();
    expect(await findByTestId('email-error')).toBeTruthy();
    expect(await findByTestId('password-error')).toBeTruthy();
    expect(await findByTestId('confirmPassword-error')).toBeTruthy();
    expect(queryAllByText(/required/i)).toHaveLength(4);
  });

  it.each([
    ['short', /username must be at least 6 characters/i],
    ['a'.repeat(32 + 1), /username must be less than 33 characters/i],
    ['*********', /username can only contain values a-zA-Z0-9_-/i],
  ])('given invalid username when submit then errors', async (invalidUsername, expectedErrorMessageMatcher) => {
    const usernameInput = getByTestId('username-input');
    fireEvent.changeText(usernameInput, invalidUsername);

    submitForm();

    const usernameError = await findByTestId('username-error');
    expect(usernameError).toBeTruthy();
    expect(usernameError.children[0]).toMatch(expectedErrorMessageMatcher);
  });

  it('given invalid email when submit then errors', async () => {
    const emailInput = getByTestId('email-input');
    fireEvent.changeText(emailInput, 'invalid email');

    submitForm();

    const emailError = await findByTestId('email-error');
    expect(emailError).toBeTruthy();
    expect(emailError.children[0]).toMatch(/invalid email/i);
  });

  it('given mismatching passwords when submit then errors', async () => {
    const passwordInput = getByTestId('password-input');
    const confirmPasswordInput = getByTestId('confirmPassword-input');

    fireEvent.changeText(passwordInput, 'password 1');
    fireEvent.changeText(confirmPasswordInput, 'different password');

    submitForm();

    const passwordError = await findByTestId('password-error');
    expect(passwordError).toBeTruthy();
    expect(passwordError.children[0]).toMatch(/passwords do not match/i);
    const confirmPasswordError = await findByTestId('confirmPassword-error');
    expect(confirmPasswordError).toBeTruthy();
    expect(confirmPasswordError.children[0]).toMatch(/passwords do not match/i);
  });

  it.each([
    ['short', /password must be at least 8 characters/i],
    ['a'.repeat(128), /password must be less than 128 characters/i],
  ])('given invalid password when submit then errors', async (invalidPassword, expectedErrorMessageMatcher) => {
    const passwordInput = getByTestId('password-input');
    const confirmPasswordInput = getByTestId('confirmPassword-input');

    fireEvent.changeText(passwordInput, invalidPassword);
    fireEvent.changeText(confirmPasswordInput, invalidPassword);

    submitForm();

    const passwordError = await findByTestId('password-error');
    expect(passwordError).toBeTruthy();
    expect(passwordError.children[0]).toMatch(expectedErrorMessageMatcher);
    const confirmPasswordError = await findByTestId('confirmPassword-error');
    expect(confirmPasswordError).toBeTruthy();
  });

  it('given valid response when submit form then redirect to /login', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
      }),
    ) as jest.Mock;

    const mockPush = jest.fn();
    (useRouter as jest.Mock).mockReturnValue({ push: mockPush });

    render(<Register />);

    inputValidData();
    submitForm();

    await waitFor(() => expect(mockPush).toHaveBeenCalledWith('/login'));
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
      }),
    ) as jest.Mock;

    const { findByTestId } = render(<Register />);

    inputValidData();
    submitForm();

    const submissionError = await findByTestId('submission-error');
    expect(submissionError).toBeTruthy();
    expect(submissionError.children[0]).toMatch('Error code <35>. Something has gone terribly wrong');

    expect(await findByTestId('username-error')).toBeTruthy();
    expect(await findByTestId('email-error')).toBeTruthy();
    expect(await findByTestId('password-error')).toBeTruthy();
    expect(await findByTestId('confirmPassword-error')).toBeTruthy();
  });

  it('given server error when submit form then display error message', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        json: () => Promise.reject(),
      }),
    ) as jest.Mock;

    const { findByTestId } = render(<Register />);

    inputValidData();
    submitForm();

    const submissionError = await findByTestId('submission-error');
    expect(submissionError).toBeTruthy();
    expect(submissionError.children[0]).toMatch('Server error. Try again');

    expect(await findByTestId('username-error')).toBeTruthy();
    expect(await findByTestId('email-error')).toBeTruthy();
    expect(await findByTestId('password-error')).toBeTruthy();
    expect(await findByTestId('confirmPassword-error')).toBeTruthy();
  });

  it('given submitted form when waiting for response then loading spinner and button is disabled', async () => {
    // Create a promise that will resolve when the condition is met
    let resolvePromise: () => void;
    const promise = new Promise<void>(resolve => (resolvePromise = resolve));

    global.fetch = jest.fn(async () => {
      await promise;
      return {
        ok: false,
        json: () => Promise.reject(),
      };
    }) as jest.Mock;

    const { getByTestId, findByTestId, queryAllByText } = render(<Register />);

    inputValidData();

    const submitButton = getByTestId('register-submit-button');
    expect(submitButton.props.accessibilityState.disabled).toStrictEqual(false);

    fireEvent.press(submitButton);

    expect(submitButton.props.accessibilityState.disabled).toStrictEqual(true);

    const loadingSpinner = await findByTestId('submission-pending');
    expect(loadingSpinner).toBeTruthy();

    // Resolve the promise to continue with the test
    resolvePromise!();

    const submissionError = await findByTestId('submission-error');
    expect(submissionError).toBeTruthy();
    expect(queryAllByText(/submit/i)).toHaveLength(1);
    expect(submitButton.props.accessibilityState.disabled).toStrictEqual(false);
  });

  const submitForm = (): void => {
    const submitButton = screen.getByTestId('register-submit-button');
    fireEvent.press(submitButton);
  };

  const inputValidData = (): void => {
    fireEvent.changeText(screen.getByTestId('username-input'), 'validUsername');
    fireEvent.changeText(screen.getByTestId('email-input'), 'a@a.aa');
    fireEvent.changeText(screen.getByTestId('password-input'), 'validPassword');
    fireEvent.changeText(screen.getByTestId('confirmPassword-input'), 'validPassword');
  };
});
