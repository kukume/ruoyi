plugins {
    val kotlinVersion = "1.9.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.lombok") version kotlinVersion
    id("io.freefair.lombok") version "8.4"
}

group = "com.ruoyi"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        maven("https://nexus.kuku.me/repository/maven-public/")
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.jetbrains.kotlin.kapt")
        plugin("org.jetbrains.kotlin.plugin.lombok")
        plugin("io.freefair.lombok")
    }

    val druidVersion = "1.2.20"
    val bitwalkerVersion = "1.21"
    val kaptchaVersion = "2.3.3"
    val pagehelperVersion = "1.4.7"
    val fastjsonVersion = "2.0.39"
    val oshiVersion = "6.4.4"
    val commonIoVersion = "2.13.0"
    val commonsCollectionsVersion = "3.2.2"
    val poiVersion = "4.1.2"
    val velocityVersion = "2.3"
    val jwtVersion = "0.9.1"
    val swaggerVersion = "3.0.0"
    val dynamicDatasource = "4.1.3"
    val hibernateVersion = "6.3.1.Final"
    val springDataJpaVersion = "3.1.5"
    val querydslVersion = "5.0.0"
    val entityGraphVersion = "3.1.0"
    val jacksonVersion = "2.15.3"
    val okhttpVersion = "4.11.0"


    dependencyManagement {
        dependencies {
            dependency("com.alibaba:druid-spring-boot-starter:${druidVersion}")
            dependency("eu.bitwalker:UserAgentUtils:${bitwalkerVersion}")
            dependency("com.github.pagehelper:pagehelper-spring-boot-starter:${pagehelperVersion}")
            dependency("com.github.oshi:oshi-core:${oshiVersion}")
            dependency("io.springfox:springfox-boot-starter:${swaggerVersion}") {
                exclude("io.swagger:swagger-models")
            }
            dependency("commons-io:commons-io:${commonIoVersion}")
            dependency("org.apache.poi:poi-ooxml:${poiVersion}")
            dependency("org.apache.velocity:velocity-engine-core:${velocityVersion}")
            dependency("commons-collections:commons-collections:${commonsCollectionsVersion}")
            dependency("commons-collections:commons-collections:${commonsCollectionsVersion}")
            dependency("com.alibaba.fastjson2:fastjson2:${fastjsonVersion}")
            dependency("com.alibaba.fastjson2:fastjson2:${fastjsonVersion}")
            dependency("io.jsonwebtoken:jjwt:${jwtVersion}")
            dependency("pro.fessional:kaptcha:${kaptchaVersion}")
            dependency("com.baomidou:dynamic-datasource-spring-boot-starter:$dynamicDatasource")
            dependency("com.cosium.spring.data:spring-data-jpa-entity-graph:$entityGraphVersion")
            dependency("org.springframework.data:spring-data-jpa:$springDataJpaVersion")
            dependency("org.hibernate.orm:hibernate-core:$hibernateVersion")
            dependency("com.querydsl:querydsl-core:$querydslVersion")
            dependency("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6:$jacksonVersion")
            dependency("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
            dependency("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
            dependency("com.squareup.okhttp3:okhttp:$okhttpVersion")
        }
    }

    dependencies {
        kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
        kapt("org.springframework.boot:spring-boot-configuration-processor")
        implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.compileKotlin {
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
    }

    tasks.compileJava {
        options.encoding = "utf-8"
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    kotlin {
        jvmToolchain(17)
    }

    kapt {
        keepJavacAnnotationProcessors = true
    }


}

kotlinLombok {
    lombokConfigurationFile(file("lombok.config"))
}