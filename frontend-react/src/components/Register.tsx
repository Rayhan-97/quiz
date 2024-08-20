/* eslint-disable @typescript-eslint/no-explicit-any */
import { Formik, FormikProps, useField } from 'formik';
import { ChangeEvent, useEffect, useRef, useState } from 'react';
import { Button, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import * as Yup from 'yup';
import { BACKEND_URL } from '../util/constants';
import { isValidEmail, isValidPassword, isValidUsername } from '../util/registerValidator';
import capitalize from '../util/stringUtils';

const REGISTER_ENDPOINT = `${BACKEND_URL}/register`;

interface RegisterFormValues {
  username: string,
  email: string,
  password: string,
  confirmPassword: string
}

interface RegisterFormErrorValidation {
  username?: string,
  email?: string,
  password?: string,
  confirmPassword?: string
}

interface RegisterFormSubmissionResponseError {
  errorCode: number,
  errorMessage: string
}

const registerFormValidationSchema = Yup.object().shape({
  username: Yup.string()
    .min(6, 'Username must be at least 6 characters')
    .max(32, 'Username must be less than 33 characters')
    .test('isValidUsername', 'Username can only contain values a-zA-Z0-9_-', isValidUsername)
    .required('Username required'),
  email: Yup.string()
    .test('isValidEmail', 'Invalid email', isValidEmail)
    .required('Email required'),
  password: Yup.string()
    .min(8, 'Password must be at least 8 characters')
    .max(127, 'Password must be less than 128 characters')
    .test('isValidPassword', 'Invalid password', isValidPassword)
    .required('Password required'),
  confirmPassword: Yup.string()
    .min(8, ' ')
    .max(127, ' ')
    .test('isValidPassword', ' ', isValidPassword)
    .required('Confirm password required')
});

export default function Register() {
  const navigate = useNavigate();
  const formikRef = useRef<FormikProps<RegisterFormValues>>(null);
  const [submissionError, setSubmissionError] = useState<string | undefined>(undefined);
  const [formSubmitting, setFormSubmitting] = useState(false);

  useEffect(() => {
    formikRef.current?.validateForm();
  }, [submissionError]);

  useEffect(() => {
    // trigger re-render to add/remove loading spinner
  }, [formSubmitting]);

  const handleSubmissionError = (errorJson?: RegisterFormSubmissionResponseError) => {
    let message: string = 'Server error. Try again';

    if (errorJson) {
      const { errorCode, errorMessage } = errorJson;
      message = `Error code <${errorCode}>. ${errorMessage}`;
    }

    setSubmissionError(message);
  };

  const submitPayload = async ({ username, email, password }: RegisterFormValues): Promise<void> => {
    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username,
        email: email,
        password: password
      })
    };

    const response = await fetch(REGISTER_ENDPOINT, requestOptions);

    if (response.ok) {
      navigate('/login');
      return;
    }

    const errorJson: RegisterFormSubmissionResponseError = await response.json();
    handleSubmissionError(errorJson);
  };

  const handleFormSubmission = async (values: RegisterFormValues): Promise<void> => {
    clearSubmissionError();
    setFormSubmitting(true);

    try {
      await submitPayload(values);
    }
    catch {
      handleSubmissionError();
    }

    setFormSubmitting(false);
  };


  const validateForm = (values: RegisterFormValues): RegisterFormErrorValidation => {
    if (submissionError) {
      return {
        username: ' ',
        email: ' ',
        password: ' ',
        confirmPassword: ' ',
      };
    }

    const errors: RegisterFormErrorValidation = {};
    if (values.password !== values.confirmPassword) {
      errors.password = 'Passwords do not match';
      errors.confirmPassword = 'Passwords do not match';
    }

    return errors;
  };

  const clearSubmissionError = (): void => {
    setSubmissionError(undefined);
  };

  return (
    <>
      <div style={styles.container}>
        <h2 style={styles.title}>Register</h2>

        <Formik
          innerRef={formikRef}
          initialValues={{ username: '', email: '', password: '', confirmPassword: '' }}
          validate={validateForm}
          validationSchema={registerFormValidationSchema}
          onSubmit={handleFormSubmission}
        >
          {({ handleChange, handleSubmit, isSubmitting }) => (
            <form onSubmit={handleSubmit} data-cy="register-form">

              <FormField
                name="username"
                onChange={value => {
                  clearSubmissionError();
                  handleChange('username')(value);
                }}
                props={{ type: 'text' }}
              />

              <FormField
                name="email"
                onChange={value => {
                  clearSubmissionError();
                  handleChange('email')(value);
                }}
                props={{ type: 'email' }}
              />

              <FormField
                name="password"
                onChange={value => {
                  clearSubmissionError();
                  handleChange('password')(value);
                }}
                props={{ type: 'password' }}
              />

              <FormField
                label="Confirm password"
                name="confirmPassword"
                onChange={value => {
                  clearSubmissionError();
                  handleChange('confirmPassword')(value);
                }}
                props={{ type: 'password' }}
              />

              {submissionError && <ErrorText name='submission' errorMessage={submissionError} />}

              <Button data-cy="register-submit-button" type="submit" disabled={isSubmitting}>
                {isSubmitting
                  ? <LoadingSpinner /> // TODO: fixme
                  : 'Submit'}
              </Button>

              <Spinner animation="border" />

            </form>
          )}
        </Formik>
      </div>
    </>
  );
}

const LoadingSpinner = ({ dataCyPrefix }: { dataCyPrefix?: string }): JSX.Element => {
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

type InputElementAttributes = React.DetailedHTMLProps<
  React.InputHTMLAttributes<HTMLInputElement>,
  HTMLInputElement
>;

type LabelElementAttributes = React.DetailedHTMLProps<
  React.LabelHTMLAttributes<HTMLLabelElement>,
  HTMLLabelElement
>;

type SpanElementAttributes = React.DetailedHTMLProps<
  React.HTMLAttributes<HTMLSpanElement>,
  HTMLSpanElement
>;

// Define a generic type for the HTML element attributes
// eslint-disable-next-line @typescript-eslint/no-unused-vars
type ElementAttributes<T extends HTMLElement> =
  | InputElementAttributes
  | LabelElementAttributes
  | SpanElementAttributes;

interface FormFieldProps<T extends HTMLElement> {
  label?: string;
  name: string;
  onChange: (e: string | ChangeEvent<any>) => void;
  // Use the generic type for props
  props?: ElementAttributes<T>;
}

const FormField = <T extends HTMLElement>({ label, name, onChange, ...props }: FormFieldProps<T>): JSX.Element => {
  const [field, meta] = useField(name);

  // Show inline feedback if EITHER
  // - the input is focused AND value is longer than 2 characters
  // - or, the has been visited (touched === true)
  const [didFocus, setDidFocus] = useState(false);
  const handleFocus = () => setDidFocus(true);

  const showFeedback = meta.touched || (!!didFocus && field.value.trim().length > 3);
  const fieldLabel = label ? label : capitalize(name);

  return (
    <div className="form-field-container">
      <div className="form-field-name-container">
        <label style={styles.formLabel} htmlFor={name}>{fieldLabel}</label>
        {showFeedback ? (meta.error ? (
          <ErrorText name={name} errorMessage={meta.error} />
        ) : <span style={styles.successText}>✓</span>
        ) : null}
      </div>

      <input data-cy={`${name}-input`} {...field} {...props} onFocus={handleFocus} onChange={onChange} />
    </div>
  );
};

const ErrorText = ({ name, errorMessage }: { name: string, errorMessage: string }): JSX.Element => {
  return (
    <>
      <span data-cy={`${name}-error`} style={styles.errorText}>{errorMessage}</span>
    </>
  );
};

const styles = {
  container: {
    // flex: 1,
    alignItems: 'center',
    // justifyContent: "center",
  },
  horizontalContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between'
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  separator: {
    marginVertical: 30,
    height: 1,
    width: '80%',
  },
  formLabel: {
    fontSize: 16,
    fontWeight: 'bold',
    paddingRight: 20
  },
  field: {
    borderWidth: 2,
    borderColor: '#5C5C5C',
    minHeight: 56,
    borderRadius: 4,
    paddingHorizontal: 14,
    marginTop: 4
  },
  fieldError: {
    borderColor: '#EB3F24'
  },
  fieldSuccess: {
    borderColor: '#24E813'
  },
  errorText: {
    color: '#EB3F24'
  },
  successText: {
    color: '#24E813'
  },
  submitButton: {
    flex: 1,
    margin: 'auto',
    textAlign: 'center',
    minWidth: '40%',
    borderWidth: 2,
    borderColor: '#5C5C5C',
    borderRadius: 4,
    padding: 14,
    fontSize: 16,
    fontWeight: 'bold',
  },
};

// const fieldErrorStyle = {...styles.field, ...styles.fieldError};
// const fieldSuccessStyle = StyleSheet.compose(styles.field, styles.fieldSuccess);
