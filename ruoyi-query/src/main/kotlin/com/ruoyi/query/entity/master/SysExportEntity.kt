package com.ruoyi.query.entity.master

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Service
import jakarta.persistence.*

@Entity
@Table(name = "sys_export")
class SysExportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var sysExportId: Long? = null
    var sysUserId: Long = 0
    @Column(name = "page_name")
    var page: String = ""
    var error: String = "" // 导出异常原因
    var path: String = "" // 文件路径
}

interface SysExportRepository: JpaRepository<SysExportEntity, Long>, JpaSpecificationExecutor<SysExportEntity> {

    fun findBySysUserId(userid: Long): List<SysExportEntity>

}

@Service
class SysExportService(
    private val sysExportRepository: SysExportRepository
) {
    fun findBySysUserId(userid: Long) = sysExportRepository.findBySysUserId(userid)

    fun save(entity: SysExportEntity): SysExportEntity = sysExportRepository.save(entity)

    fun findById(id: Long): SysExportEntity? = sysExportRepository.findById(id).orElse(null)
}
