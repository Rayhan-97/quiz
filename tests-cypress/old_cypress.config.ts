const { defineConfig } = require("cypress");

module.exports = defineConfig({
  e2e: {
    baseUrl: "http://host.docker.internal:3000",
    specPattern: "cypress/e2e/**/*.cy.{js,jsx,ts,tsx}",
    supportFile: "cypress/support/custom-commands.d.ts",
  },
  screenshotsFolder: "cypress/screenshots",
  videosFolder: "cypress/videos",
  fixturesFolder: "cypress/fixtures",
});
