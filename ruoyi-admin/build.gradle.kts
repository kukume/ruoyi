dependencies {
//    implementation("org.springframework.boot:spring-boot-devtools")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    api("com.mysql:mysql-connector-j:8.2.0")
    api(project(":ruoyi-framework"))
    api(project(":ruoyi-quartz"))
    api(project(":ruoyi-generator"))
    api(project(":ruoyi-query"))
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}