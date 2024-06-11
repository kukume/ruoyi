package com.ruoyi.query.entity.master

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.*

@Entity
@Table(name = "sys_query_config")
class SysQueryConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var sysQueryConfigId: Long? = null
    var remark: String = ""
    var templateConfig: String = ""
    var tableShow: String = ""
    var entity: String = ""
    @Column(name = "from_")
    var from: String = ""
    var entityGraph: String = "{}"
    var type: QueryType = QueryType.Specification
//    @Column(name = "default_")
//    var default: Boolean = false

    @Column(name = "default_query_condition")
    var defaultCondition: String = "[]" //  查询条件，存json？
    @Column(name = "default_query_sort")
    var defaultSort: String = "[]" // 排序条件
}


interface SysQueryConfigRepository: JpaRepository<SysQueryConfigEntity, Long>, JpaSpecificationExecutor<SysQueryConfigEntity> {
    fun findByRemarkAndType(remark: String, type: QueryType): SysQueryConfigEntity?
    fun findBySysQueryConfigIdAndRemarkAndType(id: Long, remark: String, type: QueryType): List<SysQueryConfigEntity>
}

@Service
class SysQueryConfigService(
    private val sysQueryConfigRepository: SysQueryConfigRepository
) {

    fun save(entity: SysQueryConfigEntity): SysQueryConfigEntity = sysQueryConfigRepository.save(entity)

    fun findAll(pageable: Pageable): Page<SysQueryConfigEntity> = sysQueryConfigRepository.findAll(pageable)

    @Transactional
    fun deleteById(id: Long) = sysQueryConfigRepository.deleteById(id)

    fun findByRemarkAndType(remark: String, type: QueryType) =
        sysQueryConfigRepository.findByRemarkAndType(remark, type)

    fun findBySysQueryConfigIdAndRemarkAndType(id: Long, remark: String, type: QueryType) =
            sysQueryConfigRepository.findBySysQueryConfigIdAndRemarkAndType(id, remark, type)

}
