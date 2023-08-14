plugins {
	java
	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

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
}

dependencies {
	implementation(project("core-domain"))
	implementation(project("infrastructure"))
	implementation(project("interface"))

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	compileOnly("org.projectlombok:lombok")

	runtimeOnly("com.h2database:h2")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.mockito:mockito-core:5.4.0")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	
	useJUnitPlatform()
}
