package com.ruoyi.query.utils

import com.ruoyi.common.utils.SecurityUtils

object CheckUtils {

    fun table(table: String) {
        val loginUser = SecurityUtils.getLoginUser()
        if (loginUser.user.isAdmin) return
        val tables = loginUser.tables
        val b = tables.contains(table) || tables.contains("${table}Entity")
        if (!b) error("401")
    }

}