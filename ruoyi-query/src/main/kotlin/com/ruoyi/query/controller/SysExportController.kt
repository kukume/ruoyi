package com.ruoyi.query.controller

import com.ruoyi.query.entity.master.SysExportEntity
import com.ruoyi.query.entity.master.SysExportService
import com.ruoyi.common.utils.SecurityUtils
import com.ruoyi.common.utils.download
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream

@RestController
@RequestMapping("/system/sysExport")
class SysExportController(
    private val sysExportService: SysExportService
) {

    @GetMapping("{id}")
    fun get(@PathVariable id: Long, response: HttpServletResponse) {
        val sysExportEntity = sysExportService.findById(id) ?: error("未查询到该数据")
        if (sysExportEntity.sysUserId != SecurityUtils.getUserId()) error("不是您执行的导出，无法下载")
        response.download("ss.xlsx") {
            it.write(FileInputStream(sysExportEntity.path).readBytes())
        }
    }

    @PostMapping
    fun save(@RequestBody entity: SysExportEntity): SysExportEntity {
        return sysExportService.save(entity)
    }

}
