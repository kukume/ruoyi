dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("com.alibaba:druid-spring-boot-3-starter")
    api("pro.fessional:kaptcha") {
        exclude(group = "javax.servlet", module = "servlet-api")
    }
    api("com.github.oshi:oshi-core")
    api(project(":ruoyi-system"))
}