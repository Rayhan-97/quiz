describe("register flow", () => {
    it("go to register page", () => {
        cy.visit("/");
        cy.get("[data-cy=register-form]").should("exist");
    })
})