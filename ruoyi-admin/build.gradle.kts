dependencies {
//    implementation("org.springframework.boot:spring-boot-devtools")
    api("io.springfox:springfox-boot-starter:3.0.0")
    api("io.swagger:swagger-models:1.6.2")
    api("com.mysql:mysql-connector-j:8.1.0")
    api(project(":ruoyi-framework"))
    api(project(":ruoyi-quartz"))
    api(project(":ruoyi-generator"))
}