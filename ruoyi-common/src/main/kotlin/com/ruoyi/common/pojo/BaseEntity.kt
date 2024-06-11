package com.ruoyi.common.pojo

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    var createTime: LocalDateTime = LocalDateTime.now()
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    var updateTime: LocalDateTime = LocalDateTime.now()
    @CreatedBy
    var createBy: String = ""
    @LastModifiedBy
    var updateBy: String = ""
}