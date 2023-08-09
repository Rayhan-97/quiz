import {
  isValidEmail,
  isValidUsername,
  isValidPassword,
} from "../../src/util/registerValidator";

describe("username validator test", () => {
  it.each(["username", "a123-_"])(
    "given valid username when isValidUsername then true",
    validUsername => {
      const result = isValidUsername(validUsername);

      expect(result).toStrictEqual(true);
    },
  );

  it.each([
    null,
    "",
    "*",
    "+",
    "=",
    "@",
    "£",
    "#",
    "$",
    "%",
    "12345",
    "a".repeat(32 + 1),
  ])(
    "given invalid username when isValidUsername then false",
    invalidUsername => {
      const result = isValidUsername(invalidUsername);

      expect(result).toStrictEqual(false);
    },
  );
});

describe("email validator test", () => {
  it.each([
    "username@domain.com",
    "user.name@domain.com",
    "user-name@domain.com",
    "username@domain.co.in",
    "user_name@domain.com",
  ])("given valid email when isValidEmail then true", validEmail => {
    const result = isValidEmail(validEmail);

    expect(result).toStrictEqual(true);
  });

  it.each([
    null,
    "",
    "@",
    "@.",
    "@.co",
    "@a.co",
    "a@.co",
    "a@a.a",
    "*@a.co",
    "a@*.co",
    "username.@domain.com",
    ".user.name@domain.com",
    "user-name@domain.com.",
    "username@.com",
  ])("given invalid email when isValidEmail then false", invalidEmail => {
    const result = isValidEmail(invalidEmail);

    expect(result).toStrictEqual(false);
  });
});

describe("password validator test", () => {
  it("given valid password when isValidPassword then true", () => {
    const validPassword = "password";
    const result = isValidPassword(validPassword);

    expect(result).toStrictEqual(true);
  });

  it.each([
    null,
    "",
    "1",
    "22",
    "333",
    "4444",
    "55555",
    "666666",
    "7777777",
    "a".repeat(128),
  ])(
    "given invalid password when isValidPassword then false",
    invalidPassword => {
      const result = isValidPassword(invalidPassword);

      expect(result).toStrictEqual(false);
    },
  );
});
