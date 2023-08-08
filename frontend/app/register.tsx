/* eslint-disable @typescript-eslint/no-explicit-any */
import { GestureResponderEvent, StyleSheet, TouchableOpacity, View, Text } from "react-native";

import { Formik, FormikProps, useField } from "formik";
import { ChangeEvent, useEffect, useRef, useState } from "react";
import { ActivityIndicator, TextInput, TextInputProps } from "react-native-paper";
import * as Yup from "yup";
import { isValidEmail, isValidPassword, isValidUsername } from "../util/registerValidator";
import capitalize from "../util/stringUtils";
import { useNavigation } from "@react-navigation/native";

const REGISTER_ENDPOINT = "http://localhost:8080/register";

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
    .min(6, "Username must be at least 6 characters")
    .max(32, "Username must be less than 33 characters")
    .test("isValidUsername", "Username can only contain values a-zA-Z0-9_-", isValidUsername)
    .required("Username required"),
  email: Yup.string()
    .test("isValidEmail", "Invalid email", isValidEmail)
    .required("Email required"),
  password: Yup.string()
    .min(8, "Password must be at least 8 characters")
    .max(127, "Password must be less than 128 characters")
    .test("isValidPassword", "Invalid password", isValidPassword)
    .required("Password required"),
  confirmPassword: Yup.string()
    .min(8, " ")
    .max(127, " ")
    .test("isValidPassword", " ", isValidPassword)
    .required("Confirm password required")
});

export default function Register() {
  const navigation = useNavigation();
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
    let message: string = "Server error. Try again";

    if (errorJson)
    {
      const {errorCode, errorMessage} = errorJson;
      message = `Error code <${errorCode}>. ${errorMessage}`;
    }

    setSubmissionError(message);
  };

  const submitPayload = async ({username, email, password} : RegisterFormValues) : Promise<void> => {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: username, //todo
        email: email,
        password: password
      })
    };

    const response = await fetch(REGISTER_ENDPOINT, requestOptions);
    
    if (response.ok) {
      navigation.navigate("Login");
      return;
    }

    const errorJson: RegisterFormSubmissionResponseError = await response.json();

    handleSubmissionError(errorJson);
  };

  const handleFormSubmission = async (values: RegisterFormValues): Promise<void> => {
    setSubmissionError(undefined);
    setFormSubmitting(true);

    try {
      await submitPayload(values);
    } 
    catch {
      handleSubmissionError();
    }

    setFormSubmitting(false);
  };


  const validateForm = (values: RegisterFormValues) : RegisterFormErrorValidation => {
    if (submissionError) {
      return {
        username: "bad ",
        email: " ",
        password: " ",
        confirmPassword: " ",
      };
    }

    const errors: RegisterFormErrorValidation = {};
    if (values.password !== values.confirmPassword) {
      errors.password = "Passwords do not match";
      errors.confirmPassword = "Passwords do not match";
    }
  
    return errors;
  };

  return (
    <>
      <View style={styles.container}>
        <Text style={styles.title}>Register</Text>

        <Formik
        innerRef={formikRef}
          initialValues={{ username: "", email: "", password: "", confirmPassword: "" }}
          validate={validateForm}
          validationSchema={registerFormValidationSchema}
          onSubmit={handleFormSubmission}
        >
          {({ handleChange, handleBlur, handleSubmit, isValid, isSubmitting }) => (
            <View>

              <FormField
                name="username"
                onChange={value => {
                  setSubmissionError(undefined);
                  handleChange("username")(value);
                }}
                onBlur={handleBlur("username")}
              />

              <FormField
                name="email"
                onChange={value => {
                  setSubmissionError(undefined);
                  handleChange("email")(value);
                }}
                onBlur={handleBlur("email")}
              />

              <FormField
                name="password"
                onChange={value => {
                  setSubmissionError(undefined);
                  handleChange("password")(value);
                }}
                onBlur={handleBlur("password")}
                {...{ secureTextEntry: true }}
              />

              <FormField
                label="Confirm password"
                name="confirmPassword"
                onChange={value => {
                  setSubmissionError(undefined);
                  handleChange("confirmPassword")(value);
                }}
                onBlur={handleBlur("confirmPassword")}
                {...{ secureTextEntry: true }}
              />

              {submissionError && <ErrorText name='submission' errorMessage={submissionError} />}

              <SubmitButton
                isDisabled={isSubmitting}
                isSubmitting={formSubmitting}
                isValid={isValid}
                handleSubmit={handleSubmit as any}
              />
            </View>
          )}
        </Formik>
      </View>
    </>
  );
}

const FormField = ({ label, name, onChange, onBlur, ...props }:
  {
    label?: string,
    name: string,
    onChange: (e: string | ChangeEvent<any>) => void,
    onBlur: (e: any) => void,
    props?: TextInputProps
  }
): JSX.Element => {
  const [field, meta] = useField(name);

  // Show inline feedback if EITHER
  // - the input is focused AND value is longer than 2 characters
  // - or, the has been visited (touched === true)
  const [didFocus, setDidFocus] = useState(false);
  const handleFocus = () => setDidFocus(true);

  const showFeedback = meta.touched || (!!didFocus && field.value.trim().length > 3);
  const fieldLabel = label ? label : capitalize(name);

  return (
    <View style={styles.formFieldContainer}>
      <View style={styles.horizontalContainer}>
        <Text style={styles.formLabel}>{fieldLabel}</Text>
        {showFeedback ? (meta.error ? (
          <ErrorText name={name} errorMessage={meta.error} />
        ) : <Text style={styles.successText}>✓</Text>
        ) : null}
      </View>

      <TextInput
        testID={`${name}-input`}
        style={showFeedback ? (meta.error ? (
          fieldErrorStyle
        ) : fieldSuccessStyle
        ) : styles.field}
        onFocus={handleFocus}
        onChangeText={onChange}
        onBlur={onBlur}
        value={field.value}
        {...props}
      />
    </View>
  );
};

const ErrorText = ({ name, errorMessage }: { name: string, errorMessage: string }): JSX.Element => {
  return(
  <>
  <Text testID={`${name}-error`} style={styles.errorText}>{errorMessage}</Text>
  </>
  );
};

const SubmitButton = ({ isDisabled, isSubmitting, isValid, handleSubmit } : {isDisabled: boolean, isSubmitting: boolean, isValid: boolean, handleSubmit: ((event: GestureResponderEvent) => void)}): JSX.Element => {

  return (
    <>
      <TouchableOpacity
        testID='register-submit-button'
        style={styles.submitButton}
        disabled={isDisabled}
        activeOpacity={isValid ? 1 : 0.7}
        onPress={handleSubmit as any}
      >
        {isSubmitting
          ? <ActivityIndicator testID='submission-pending' />
          : <Text>Submit</Text>}
      </TouchableOpacity>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  horizontalContainer: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
  },
  separator: {
    marginVertical: 30,
    height: 1,
    width: "80%",
  },
  formFieldContainer: {
    paddingVertical: 10,
    minWidth: "40vw"
  },
  formLabel: {
    fontSize: 16,
    fontWeight: "bold",
    paddingRight: 20
  },
  field: {
    borderWidth: 2,
    borderColor: "#5C5C5C",
    minHeight: 56,
    borderRadius: 4,
    paddingHorizontal: 14,
    marginTop: 4
  },
  fieldError: {
    borderColor: "#EB3F24"
  },
  fieldSuccess: {
    borderColor: "#24E813"
  },
  errorText: {
    color: "#EB3F24"
  },
  successText: {
    color: "#24E813"
  },
  submitButton: {
    flex: 1,
    margin: "auto",
    textAlign: "center",
    minWidth: "40%",
    borderWidth: 2,
    borderColor: "#5C5C5C",
    borderRadius: 4,
    padding:14,
    fontSize: 16,
    fontWeight: "bold",
  },
});

const fieldErrorStyle = StyleSheet.compose(styles.field, styles.fieldError);
const fieldSuccessStyle = StyleSheet.compose(styles.field, styles.fieldSuccess);
