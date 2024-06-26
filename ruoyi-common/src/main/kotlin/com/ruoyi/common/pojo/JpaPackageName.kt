package com.ruoyi.common.pojo

object JpaPackageName {

    val entityPackageNames = listOf(
        "com.ruoyi.query.entity.master",
        "com.ruoyi.common.entity.master"
    )

    @JvmStatic
    val entityPackageNameArray = entityPackageNames.toTypedArray()

    val repositoryPackageNames = listOf(
        "com.ruoyi.query.entity.master",
        "com.ruoyi.common.entity.master"
    )

}