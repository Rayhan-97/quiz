# Rayhan Quiz Project



## About

A quiz web application allowing users to create quizzes, share and join quizzes and view past quizzes they have made and/or taken.

### Tech stack

| **Backend**   	| **Frontend**  	| **DevOps**     	| **Management** 	|
|---------------	|---------------	|----------------	|----------------	|
| Java          	| React         	| Git            	| GitHub         	|
| Spring Boot   	| TypeScript    	| Docker         	| Jira           	|
| JUnit         	| Jest          	| Bash           	|                	|
| Cypress (e2e) 	| Cypress (e2e) 	| GitHub Actions 	|                	|

## Local deployment

After cloning the repository, run the backend server (Java 17 required) followed by the frontend server:

```bash
[...]$ https://github.com/Rayhan-97/quiz.git
quiz/$ cd quiz
quiz/$ ./gradlew bootRun &      # build backend and run the server
quiz/$ cd frontend-react
frontend-react/$ npm ci         # install dependencies
frontend-react/$ npm start      # run frontend server
```

The backend is built and run via gradle, while the frontend uses create-react-app.
The web app is accessed on `http://localhost:3000` by default.

The backend runs on port 8080, so if these two ports are in use, this method won't work out of the box.
Alternatively, the servers can be run in docker.

### Docker local deployment

Dockerfiles exist for the two services. They can be built using gradle tasks and deployed with `docker-compose`.

```bash
quiz/$ ./gradlew buildDockerImages
quiz/$ (cd backend && docker-compose up -d)
quiz/$ (cd frontend-react && docker-compose up -d)
```

You can view the running containers with `docker ps`. In the event that you are using a later Docker Engine version and `docker-compose` is unsupported. You can instead run `docker compose` without the hyphen.

## Testing

Backend tests can be run with `./gradlew test` in the root.
This will also run Spring integration tests in the [tests-backend-integration](./tests-backend-integration/) directory.
Frontend tests can be run with `npm test` in the [frontend-react](./frontend-react/) directory.

### E2E Cypress tests

Cypress tests exist in the [tests-cypress](./tests-cypress/) directory. A helper script exists to run the tests.

```bash
quiz/$ bash scripts/runCypressTests.sh
quiz/$ bash scripts/runCypressTests.sh --headless=true
```

The second method runs Cypress in headless mode without a GUI. The first method will open the interactive Cypress test runner.
This will run Cypress in docker and has been made with an Ubuntu local development environment in mind. The GUI works by forwarding the display to an X11 server running on the host machine. Note that this may not work on your machine. In fact, headed mode isn't working currently on the author's machine after upgrading Ubuntu, currently on 24.04.1 LTS, the test runner opens as a white screen and is unresponsive. Headless still runs correctly.

The workaround is to run Cypress on the local host machine with `npx cypress open` in the [tests-cypress](./tests-cypress/) directory. This may require changing the base url in the Cypress [configuration](./tests-cypress/cypress.json).

### GitHub Actions

The pipeline uses GitHub Actions to run tests automatically whenever a push is made to remote. The workflows are found in [.github/workflows](./.github/workflows/). The current pipeline consists of running frontend and backend tests in parallel followed by Cypress tests.

## Current stage of development

- [x] Ideation
- [x] Initial user stories
- [x] User registration / login user story writing
- [x] User registration
  - [x] frontend
  - [x] backend
  - [x] Cypress e2e tests
- [x] User login
  - [x] backend
  - [x] frontend
  - [x] Cypress e2e tests
- [ ] UI designs in Figma
  - [x] register
  - [x] login
  - [ ] homepage
- [ ] UI frontend styling
  - [x] registration
  - [x] login
  - [ ] homepage

Current work is focused on working on the UI for the homepage, including designing layout and implementation.

## Future stages of development

### Application use cases and content

Following planned pieces of work involve:
- quiz maker use case
    - a user is able to make quizzes, with different questions, options, time limit and scoring system
- quiz taker use case
    - a user can join live quizzes activated by a quiz maker and answer questions accordingly
- quiz hoster use case
    - a quiz hoster can monitor live responses and manage the quiz
- quiz history use case
    - a quiz maker can see past quizzes they have made, and review details of a quiz
    can also view past quizzes participated in
- a how it works section
    - detailing deployment and development details of the quiz application itself

### Public deployment and hosting

Implementation of hosting yet to be determined. Initial plans consist of running the servers in docker on an AWS EC2 instance, and a Postgres RDS instance for persistence. There is a need to configure routing and certificates via Route53. Overall, the idea seems like a good way to explore AWS (further than the knowledge I already have).

## GitHub external tooling

Tools used to progress the project's development that exist outside the visibility of this GitHub repostiory includes Figma, used for designing web application UI, and Jira, used for issue tracking. The commit message prefixes refer to Jira issue numbers. There may be further investigation into integrating Jira and GitHub or migrating Jira issues into GitHub issues.

