module.exports = {
  // ... other Jest config options ...
  preset: "jest-expo",
  setupFilesAfterEnv: ["@testing-library/jest-native/extend-expect"],
  transform: {
    "^.+\\.(js|jsx|ts|tsx)$": "babel-jest",
    "\\.(ts|tsx)$": "ts-jest",
  },
};

// "jest": {
//   "preset": "jest-expo",
//   "setupFilesAfterEnv": [
//     "@testing-library/jest-native/extend-expect"
//   ],
//   "testEnvironment": "node",
//   "transform": {
//       "^.+\\.(js|jsx|ts|tsx)$": "babel-jest"
//     },
//   "transformIgnorePatterns": [],
//   "resolver": "jest-esm-resolver"
// },
