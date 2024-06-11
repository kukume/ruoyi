package com.ruoyi.query.controller.query

import com.ruoyi.common.utils.JobManager
import com.ruoyi.common.utils.SecurityUtils
import com.ruoyi.common.utils.download
import com.ruoyi.query.pojo.DynamicHeader
import com.ruoyi.query.utils.ExportUtils
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class DynamicExportJpaSpecificationController(
    private val applicationContext: ApplicationContext
) {

    @PostMapping("/dynamic/export/specification")
    fun exportSpecification(@RequestBody param: ExportParam, response: HttpServletResponse): Any? {
        return if (param.async) {
            val userId = SecurityUtils.getUserId()
            JobManager.now {
                val workbook = export(param)
                ExportUtils.async(workbook, "${param.dynamicParam.name}_${System.currentTimeMillis()}.xlsx",
                    param.dynamicParam.name, userId)
            }
            "{}"
        } else {
            val workbook = export(param)
            response.download("su.xlsx") {
                workbook.use { workbook ->
                    workbook.write(it)
                }
            }
            null
        }
    }

    private fun export(param: ExportParam): XSSFWorkbook {
        val dynamicParam = param.dynamicParam
        val sp = dynamicParam.toPredicate()
        val sort = dynamicParam.toSort()
        val name = dynamicParam.name
        val prefix = name.substring(0, 1).uppercase() + name.substring(1)
        val repositoryClass = repositoryClass(prefix)
        val repository = applicationContext.getBean(repositoryClass)
        val method =
            repositoryClass.getMethod(param.dynamicParam.method, Specification::class.java, Sort::class.java)
        val list = method.invoke(repository, sp, sort) as List<*>
        val headers = param.headers
        val excel = excelHead(headers)
        excel.excelBody(headers, list)
        return excel.workbook
    }


}

internal data class Excel(val workbook: XSSFWorkbook, val sheet: XSSFSheet)

internal fun excelHead(headers: List<Head>): Excel {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("sheet1")
    val headerRow = sheet.createRow(0)
    for ((index, excelHead) in headers.withIndex()) {
        val cell = headerRow.createCell(index)
        cell.setCellValue(excelHead.title())
    }
    return Excel(workbook, sheet)
}

internal fun Excel.excelBody(headers: List<Head>, list: List<Any?>) {
    for ((i, any) in list.withIndex()) {
        val row = sheet.createRow(i + 1)
        for ((index, excelHead) in headers.withIndex()) {
            val arr = excelHead.value().split(".")
            var temp = any
            arr.forEach { temp = getField(temp, it) }
            val cell = row.createCell(index)
            for (r in excelHead.rules()) {
                if (r.value == temp.toString()) {
                    temp = r.text
                    break
                }
            }
            cell.setCellValue(Objects.toString(temp, ""))
        }
    }
}

internal fun Head.title(): String {
    return when (this) {
        is ExcelHead -> {
            this.title
        }

        is DynamicHeader -> {
            this.title
        }
        else -> {
            error("error")
        }
    }
}

internal fun Head.value(): String {
    return when (this) {
        is ExcelHead -> {
            this.value
        }

        is DynamicHeader -> {
            this.field
        }
        else -> {
            error("error")
        }
    }
}

internal fun Head.rules(): List<Rule> {
    return when (this) {
        is ExcelHead -> {
            this.rules
        }

        is DynamicHeader -> {
            this.rules
        }
        else -> {
            error("error")
        }
    }
}


internal fun getField(any: Any?, name: String): Any? {
    if (any == null) return null
    val clazz = any::class.java
    val getMethodName = "get${name.substring(0, 1).uppercase()}${name.substring(1)}"
    val method = clazz.getMethod(getMethodName)
    return method.invoke(any)
}

class ExportParam {
    var headers = mutableListOf<ExcelHead>()
    var dynamicParam: DynamicParam = DynamicParam()
    var async: Boolean = false
}

internal interface Head

class ExcelHead: Head {
    var title: String = ""
    var value: String = ""
    var rules = mutableListOf<Rule>()
}

class Rule {
    var value: String = ""
    var text: String = ""
}
