plugins {
    id("com.bmuschko.docker-remote-api") version "6.0.0"
}

tasks.register<Exec>("buildDockerImage") {
    enabled = project.findProperty("buildCypressImage")?.toString()?.toBoolean() ?: false
    // Set the command to build the Docker image
    commandLine("docker", "buildx", "build", "-t", "cypress", ".", "--load")

    // Set the working directory where the Dockerfile is located
    workingDir(file(project.projectDir, PathValidation.DIRECTORY))
}