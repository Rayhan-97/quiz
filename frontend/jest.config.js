module.exports = {
  // ... other Jest config options ...
  preset: "jest-expo",
  setupFilesAfterEnv: ["@testing-library/jest-native/extend-expect"],
  transform: {
    "^.+\\.(js|jsx|ts|tsx)$": "babel-jest",
    "\\.(ts|tsx)$": "ts-jest",
  },
};