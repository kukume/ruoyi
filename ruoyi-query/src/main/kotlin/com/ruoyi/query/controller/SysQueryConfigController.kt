package com.ruoyi.query.controller

import com.ruoyi.query.entity.master.QueryType
import com.ruoyi.query.entity.master.SysQueryConfigEntity
import com.ruoyi.query.entity.master.SysQueryConfigService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/system/sysQueryConfig")
class SysQueryConfigController(
    private val sysQueryConfigService: SysQueryConfigService
) {

    @GetMapping
    fun query(pageable: Pageable): Any {
        return sysQueryConfigService.findAll(pageable)
    }

    @GetMapping("/byRemarkAndType")
    fun queryByRemark(remark: String, type: QueryType): SysQueryConfigEntity? {
        return sysQueryConfigService.findByRemarkAndType(remark, type)
    }

    @PostMapping
    fun save(@RequestBody entity: SysQueryConfigEntity): SysQueryConfigEntity {
        val query = sysQueryConfigService.findByRemarkAndType(
                entity.remark, entity.type
        )
        if (entity.sysQueryConfigId != null) {
            if (query != null && query.sysQueryConfigId != entity.sysQueryConfigId) error("备注重复，请更新备注")
        } else {
            if (query != null) error("备注重复，请更新备注")
        }
        entity.entityGraph.ifEmpty { "{}" }
        return sysQueryConfigService.save(entity)
    }

}
