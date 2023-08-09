const USERNAME_REGEX = /^[_A-Za-z0-9-]{6,32}$/;
const EMAIL_REGEX =
  /^[_A-Za-z0-9-+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;

export const isValidUsername = (username: string = ""): boolean => {
  return USERNAME_REGEX.test(username);
};

export const isValidEmail = (email: string = ""): boolean => {
  return EMAIL_REGEX.test(email);
};

export const isValidPassword = (password: string = ""): boolean => {
  return password !== null && password.length >= 8 && password.length < 128;
};

export default { isValidUsername, isValidEmail, isValidPassword };
