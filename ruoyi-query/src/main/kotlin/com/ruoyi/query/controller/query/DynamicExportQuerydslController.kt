@file:Suppress("DuplicatedCode")

package com.ruoyi.query.controller.query

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import com.ruoyi.common.utils.JobManager
import com.ruoyi.common.utils.download
import com.ruoyi.query.utils.CheckUtils
import com.ruoyi.query.utils.ExportUtils
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class DynamicExportQuerydslController(
    private val jpaQueryFactory: JPAQueryFactory
) {

    @PostMapping("/dynamic/export/querydsl")
    fun export(@RequestBody sql: ExportSql, response: HttpServletResponse): Any? {
        CheckUtils.table(sql.from.value)
        return if (sql.async) {
            JobManager.now {
                val workbook = export(sql)
                ExportUtils.async(workbook, "${sql.from.value}_${System.currentTimeMillis()}.xlsx",
                    sql.from.value)
            }
            "{}"
        } else {
            val workbook = export(sql)
            response.download("ss.xlsx") {
                workbook.use { xssf ->
                    xssf.write(it)
                }
            }
            null
        }
    }

    private fun export(sql: ExportSql): XSSFWorkbook {
        val from = sql.from
        val primary = from.value
        val qPrimary = qEntity(primary)
        val jpaQuery = jpaQueryFactory.from(qPrimary)
        sql.from(jpaQuery)
        sql.where(jpaQuery)
        sql.orderBy(jpaQuery)
        val select = sql.select
        val selectQArray = select.map { convert(it.value) }.toTypedArray()
        if (selectQArray.isNotEmpty()) jpaQuery.select(*selectQArray)
        val fetch = jpaQuery.fetch()
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("sheet1")
        val headerRow = sheet.createRow(0)
        for ((index, excelHead) in select.withIndex()) {
            val cell = headerRow.createCell(index)
            cell.setCellValue(excelHead.title)
        }
        for ((rowNum, f) in fetch.withIndex()) {
            val row = sheet.createRow(rowNum + 1)
            if (f is Tuple) {
                for ((i, path) in selectQArray.withIndex()) {
                    f.get(path)?.let {
                        var temp = it
                        select.find { se -> se.value.lowercase() == path.toString().lowercase() }?.rules?.forEach { rule ->
                            if (rule.value == temp.toString()) {
                                temp = rule.text
                            }
                        }
                        val cell = row.createCell(i)
                        cell.setCellValue(Objects.toString(temp, ""))
                    }
                }
            }
        }
        return workbook
    }

}

class ExportSql: BaseSql() {
    var select: MutableList<ExcelHead> = mutableListOf()
    var async: Boolean = false
}
