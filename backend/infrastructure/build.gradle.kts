dependencies {
	implementation(project(":backend:core-domain"))

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	runtimeOnly("com.h2database:h2")
}