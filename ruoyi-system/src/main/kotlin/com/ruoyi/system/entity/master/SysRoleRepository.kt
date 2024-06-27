package com.ruoyi.system.entity.master

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository
import com.ruoyi.common.entity.master.SysRoleEntity
import org.springframework.stereotype.Service

interface SysRoleRepository: EntityGraphJpaRepository<SysRoleEntity, Long> {

}

@Service
class SysRoleService(
    private val sysRoleRepository: SysRoleRepository
) {

    fun save(sysRoleEntity: SysRoleEntity): SysRoleEntity = sysRoleRepository.save(sysRoleEntity)

    fun findById(id: Long): SysRoleEntity? = sysRoleRepository.findById(id).orElse(null)

}