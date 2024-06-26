package com.ruoyi.common.entity.master

import jakarta.persistence.*

@Entity
@Table(name = "sys_role")
class SysRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var roleId: String? = null
}