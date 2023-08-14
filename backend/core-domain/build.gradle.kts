plugins {
	java
	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
}

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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")

	annotationProcessor("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")

	testImplementation("org.mockito:mockito-core:5.4.0")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
