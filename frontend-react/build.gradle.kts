plugins {
    id("com.bmuschko.docker-remote-api") version "6.0.0"
}

tasks.register<Exec>("buildDockerImage") {
    // Set the command to build the Docker image
    commandLine("docker", "buildx", "build", "-t", "frontend", ".", "--load")

    // Set the working directory where the Dockerfile is located
    workingDir(file(project.projectDir, PathValidation.DIRECTORY))
}