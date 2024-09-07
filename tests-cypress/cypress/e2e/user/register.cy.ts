describe('register flow', () => {

  it('can register a new user', () => {
    const backendUrl = Cypress.env('BACKEND_URL') || 'http://localhost:8080';
    const backendRegisterEndpoint = `${backendUrl}/register`;

    cy.intercept('POST', backendRegisterEndpoint, (req) => {
      req.on('response', async (res) => {
        res.setDelay(300);
      });
    }).as('delayedResponse');

    cy.visit('/register');
    cy.dataCy('register-form').should('exist');

    const username = `user-${randomString(5)}`;

    cy.dataCy('username-input').type(username);
    cy.dataCy('email-input').type(`${username}@email.com`);
    cy.dataCy('password-input').type('password');
    cy.dataCy('confirmPassword-input').type('password');

    cy.dataCy('submit-button').click();

    cy.dataCy('loading-spinner').should('exist');

    cy.wait('@delayedResponse');
    cy.url().should('contain', '/login');
  });

  /**
   * Return a random string with characters from the set [a-zA-Z0-9]
   * @example const username = `user-${randomString(5)}`
   */
  const randomString = (length: number) => {
    return (Math.random().toString(36) + '00000000000000000').slice(2, length + 2);
  };
});
