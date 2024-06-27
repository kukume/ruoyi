package com.ruoyi.common.entity.master

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type

@Entity
@Table(name = "sys_role")
class SysRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var roleId: Long? = null
    @Type(JsonType::class)
    var tables: List<String> = listOf()
}