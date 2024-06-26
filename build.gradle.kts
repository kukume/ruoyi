plugins {
    val kotlinVersion = "2.0.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("plugin.lombok") version kotlinVersion
    id("io.freefair.lombok") version "8.6"
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
        plugin("org.jetbrains.kotlin.plugin.jpa")
        plugin("org.jetbrains.kotlin.kapt")
        plugin("org.jetbrains.kotlin.plugin.lombok")
        plugin("io.freefair.lombok")
    }

    val druidVersion = "1.2.23"
    val bitwalkerVersion = "1.21"
    val kaptchaVersion = "2.3.3"
    val pagehelperVersion = "1.4.7"
    val fastjsonVersion = "2.0.39"
    val oshiVersion = "6.6.1"
    val commonIoVersion = "2.13.0"
    val poiVersion = "4.1.2"
    val velocityVersion = "2.3"
    val jwtVersion = "0.9.1"
    val swaggerVersion = "2.2.0"
    val dynamicDatasource = "4.1.3"
    val hibernateVersion = "6.3.1.Final"
    val springDataJpaVersion = "3.1.5"
    val querydslVersion = "5.1.0"
    val entityGraphVersion = "3.1.0"
    val jacksonVersion = "2.15.3"
    val okhttpVersion = "4.11.0"
    val mybatisPlusVersion = "3.5.3.1"


    dependencyManagement {
        dependencies {
            dependency("com.alibaba:druid-spring-boot-3-starter:${druidVersion}")
            dependency("eu.bitwalker:UserAgentUtils:${bitwalkerVersion}")
            dependency("com.github.pagehelper:pagehelper-spring-boot-starter:${pagehelperVersion}")
            dependency("com.github.oshi:oshi-core:${oshiVersion}")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:${swaggerVersion}")
            dependency("commons-io:commons-io:${commonIoVersion}")
            dependency("org.apache.poi:poi-ooxml:${poiVersion}")
            dependency("org.apache.velocity:velocity-engine-core:${velocityVersion}")
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
            dependency("com.baomidou:mybatis-plus-boot-starter:$mybatisPlusVersion")
            dependency("de.codecentric:spring-boot-admin-starter-client:3.1.8")
        }
    }

    dependencies {
        kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
        kapt("org.springframework.boot:spring-boot-configuration-processor")
        implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.compileKotlin {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
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