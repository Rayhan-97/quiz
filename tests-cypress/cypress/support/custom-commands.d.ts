// tests-cypress/support/custom-commands.d.ts
declare namespace Cypress {
    interface Chainable {
      getByTestId(testId: string): Chainable<Element>;
      // Add other custom command declarations here, if needed
    }
  }
  