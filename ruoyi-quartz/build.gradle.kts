dependencies {
    api("org.quartz-scheduler:quartz") {
        exclude(group = "com.mchange", module = "c3p0")
    }
    api(project(":ruoyi-common"))
}