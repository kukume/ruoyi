pluginManagement {
    repositories {
        maven { url = uri("https://nexus.kuku.me/repository/maven-public/") }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "ruoyi"
include("ruoyi-common")
include("ruoyi-generator")
include("ruoyi-quartz")
include("ruoyi-system")
include("ruoyi-framework")
include("ruoyi-admin")
include("ruoyi-query")
