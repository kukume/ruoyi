package com.ruoyi.system.entity.master

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "sys_post")
class SysPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var postId: Long? = null
    var postCode: String = ""
    var postName: String = ""
    var postSort: String = ""
    var status: Int = 0
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createTime: LocalDateTime = LocalDateTime.now()
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var updateTime: LocalDateTime? = null
    var remark: String = ""
    var createBy: String = ""
    var updateBy: String? = null
}


interface SysPostRepository: JpaRepository<SysPostEntity, Long>, JpaSpecificationExecutor<SysPostEntity>

@Service
class SysPostService(
    private val sysPostRepository: SysPostRepository
) {


}
