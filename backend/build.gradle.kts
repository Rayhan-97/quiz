import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
	id("com.bmuschko.docker-remote-api") version "6.0.0"
}

group = "com.quiz"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

subprojects {
	// Apply the Java plugin to all subprojects
	plugins.apply("java")

	// Apply the Spring Boot and Spring Dependency Management plugins
	plugins.apply("org.springframework.boot")
	plugins.apply("io.spring.dependency-management")

	java {
		sourceCompatibility = JavaVersion.VERSION_17
	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}

	repositories {
		mavenCentral() // Common repositories
	}

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-validation")

		annotationProcessor("org.projectlombok:lombok")
		compileOnly("org.projectlombok:lombok")

		testImplementation("org.mockito:mockito-core:5.4.0")
		testImplementation("org.assertj:assertj-core:3.24.2")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	tasks.named<BootJar>("bootJar") {
		enabled = false // Disable the bootJar task for subprojects
	}
}

dependencies {
	implementation(project("core-domain"))
	implementation(project("infrastructure"))
	implementation(project("interface"))
	implementation(project("launch"))
}

springBoot {
	mainClass = "com.quiz.launch.Application"
}

tasks.register<Exec>("buildDockerImage") {
	dependsOn(tasks.named("bootJar"))

	// Set the command to build the Docker image
	commandLine("docker-buildx", "build", "-t", "backend", ".")

	// Set the working directory where the Dockerfile is located
	workingDir(file(project.projectDir, PathValidation.DIRECTORY))
}