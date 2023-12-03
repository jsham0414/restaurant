plugins {}

version = "0.0.1"

allprojects {
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-security")
        // jjwt
        implementation("io.jsonwebtoken", "jjwt", "0.9.1")

        // com.sun.xml.bind, jjwt signWith
        implementation("com.sun.xml.bind:jaxb-impl:4.0.1")
        implementation("com.sun.xml.bind:jaxb-core:4.0.1")
        // javax.xml.bind jjwt signWith
        implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")

        implementation(project(":database"))

        // Swagger
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    }
}