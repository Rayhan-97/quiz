dependencies {
	implementation(project(":backend:core-domain"))
	implementation(project(":backend:infrastructure"))
	implementation(project(":backend:interface"))

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	runtimeOnly("com.h2database:h2")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
}