package com.ruoyi.query.entity.master

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import jakarta.persistence.*

//  保存前端的自定义查询条件
@Entity
@Table(name = "sys_query_condition")
class SysQueryConditionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var sysQueryConditionId: Long? = null
//    @OneToOne
//    @JoinColumn(name = "sys_user_id")
//    var sysUserEntity: SysUserEntity = SysUserEntity()  // 用户id
    var sysUserId: Long = 0
    @Column(name = "query_condition")
    var condition: String = "" //  查询条件，存json？
    @Column(name = "query_sort")
    var sort: String = "" // 排序条件
    var page: String = ""    // 哪个页面
    var remark: String = "" // 模板名字
    @Column(name = "column_hidden")
    var hidden: String = "" //  隐藏的列
    @Column(name = "column_show")
    var show: String = "" // 显示的列，可按列排序导出
    var type: QueryType = QueryType.Specification  // 查询类型，

    fun copy() = SysQueryConditionEntity().also {
        it.sysUserId = sysUserId
        it.condition = condition
        it.sort = sort
        it.page = page
        it.remark = remark
        it.hidden = hidden
        it.show = show
        it.type = type
    }

}

enum class QueryType {
    Specification, Querydsl, Other
}

interface SysQueryConditionRepository: JpaRepository<SysQueryConditionEntity, Long> {

    fun findBySysUserIdAndPageAndType(sysUserId: Long, page: String, type: QueryType): List<SysQueryConditionEntity>

    fun findBySysUserIdAndPageAndTypeAndRemark(sysUserId: Long, page: String, type: QueryType, remark: String): List<SysQueryConditionEntity>

}


@Service
class SysQueryConditionService(
    private val sysQueryConditionRepository: SysQueryConditionRepository
) {

    fun save(entity: SysQueryConditionEntity): SysQueryConditionEntity =
        sysQueryConditionRepository.save(entity)

    fun findBySysUserIdAndPageAndType(sysUserId: Long, page: String, type: QueryType) =
        sysQueryConditionRepository.findBySysUserIdAndPageAndType(sysUserId, page, type)

    @Transactional
    fun deleteById(id: Long) = sysQueryConditionRepository.deleteById(id)

    fun findBySysUserIdAndPageAndTypeAndRemark(sysUserId: Long, page: String, type: QueryType, remark: String) =
        sysQueryConditionRepository.findBySysUserIdAndPageAndTypeAndRemark(sysUserId, page, type, remark)

    fun findById(id: Long): Optional<SysQueryConditionEntity> = sysQueryConditionRepository.findById(id)
}
