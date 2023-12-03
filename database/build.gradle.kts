plugins {
    kotlin("plugin.jpa")
}

version = "0.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.6")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-aop:2.7.6")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // jjwt
    implementation("io.jsonwebtoken", "jjwt", "0.12.3")

}