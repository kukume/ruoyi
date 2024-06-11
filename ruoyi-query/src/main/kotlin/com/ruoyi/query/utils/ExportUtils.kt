package com.ruoyi.query.utils

import com.ruoyi.common.utils.SecurityUtils
import com.ruoyi.common.utils.spring.SpringUtils
import com.ruoyi.query.entity.master.SysExportEntity
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File
import java.io.FileOutputStream

object ExportUtils {

    private const val PREFIX_PATH = "tmp"   // 保存导出文件的前缀

    fun async(workbook: XSSFWorkbook, fileName: String, page: String, userid: Long? = null): SysExportEntity {
        val folder = File(PREFIX_PATH)
        if (!folder.exists()) folder.mkdir()
        val newFileName = if (fileName.endsWith(".xlsx")) fileName
        else "$fileName.xlsx"
        val path = "$PREFIX_PATH${File.separator}$newFileName"
        FileOutputStream(path).use {
            workbook.write(it)
        }
        var newUserId = userid
        if (newUserId == null) newUserId = SecurityUtils.getUserId()
        val jdbcTemplate = SpringUtils.getBean(JdbcTemplate::class.java)
        jdbcTemplate.execute("""
            insert into sys_export(sys_user_id, error, path, page_name)
            values ($newUserId, '', '${path.replace(File.separator, File.separator + File.separator)}', '$page')
        """.trimIndent())
        val entity = SysExportEntity().also {
            it.sysUserId = newUserId!!
            it.path = path
            it.page = page
        }
        return entity
    }
}
