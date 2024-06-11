package com.ruoyi.query.controller

import com.ruoyi.common.utils.SecurityUtils
import com.ruoyi.query.entity.master.QueryType
import com.ruoyi.query.entity.master.SysQueryConditionEntity
import com.ruoyi.query.entity.master.SysQueryConditionService
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/query/condition")
class SysQueryConditionController(
    private val sysQueryConditionService: SysQueryConditionService
) {

    @GetMapping
    fun query(page: String, type: QueryType): List<SysQueryConditionEntity> {
        val sysUserId = SecurityUtils.getUserId()
        return sysQueryConditionService.findBySysUserIdAndPageAndType(sysUserId, page, type)
    }

    @PostMapping
    fun save(@RequestBody entity: SysQueryConditionEntity): SysQueryConditionEntity {
        entity.sysUserId = SecurityUtils.getUserId()
        if (entity.sysQueryConditionId == null) {
            val remark = entity.remark
            val queryList =
                sysQueryConditionService.findBySysUserIdAndPageAndTypeAndRemark(
                    entity.sysUserId,
                    entity.page, entity.type, remark
                )
            if (queryList.isNotEmpty()) error("模板名重复，请更新模板名")
        }
        return sysQueryConditionService.save(entity)
    }

    @PostMapping("copy")
    fun copy(@RequestBody params: SysQueryConditionCopyParams): SysQueryConditionEntity {
        if (params.id == 0L) error("id不存在")
        if (params.remark.isEmpty()) error("模板名不存在")
        val entity = sysQueryConditionService.findById(params.id).getOrNull()
            ?: error("不存在复制前的模板")
        val queryList = sysQueryConditionService.findBySysUserIdAndPageAndTypeAndRemark(entity.sysUserId, entity.page,
            entity.type, params.remark)
        if (queryList.isNotEmpty()) error("模板名重复，请更新模板名")
        val newEntity = entity.copy()
        newEntity.remark = params.remark
        sysQueryConditionService.save(newEntity)
        return newEntity
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) {
        sysQueryConditionService.deleteById(id)
    }

}

class SysQueryConditionCopyParams {
    var id: Long = 0
    var remark: String = ""
}
