rootProject.name = "quiz"

include(
        "backend",
        "backend:core-domain",
        "backend:infrastructure",
        "backend:interface",
        "backend:launch",
        "frontend-react",
        "tests-cypress",
        "tests-backend-integration",
)
