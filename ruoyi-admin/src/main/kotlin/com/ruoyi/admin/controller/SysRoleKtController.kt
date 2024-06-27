package com.ruoyi.admin.controller

import com.ruoyi.system.entity.master.SysRoleService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/system/role")
class SysRoleKtController(
    private val sysRoleService: SysRoleService
) {

    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @GetMapping("/table/{id}")
    fun table(@PathVariable id: Long): Any {
        return sysRoleService.findById(id) ?: error("not found")
    }

    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PostMapping("/table/")
    fun saveTable(@RequestBody @Valid params: SysRoleSaveParams): Any {
        val sysRoleEntity = sysRoleService.findById(params.roleId) ?: error("not found role")
        sysRoleEntity.tables = params.tables
        return sysRoleService.save(sysRoleEntity)
    }


}

data class SysRoleSaveParams(@NotEmpty val tables: List<String>, @NotNull val roleId: Long)