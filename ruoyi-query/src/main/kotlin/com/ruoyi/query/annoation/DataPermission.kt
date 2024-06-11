package com.ruoyi.query.annoation

import com.ruoyi.common.core.domain.TreeSelect
import com.ruoyi.common.core.domain.entity.SysDept
import com.ruoyi.common.utils.SecurityUtils
import com.ruoyi.common.utils.spring.SpringUtils
import org.springframework.data.jpa.domain.Specification
import org.springframework.jdbc.core.JdbcTemplate
import java.lang.reflect.Method

annotation class DeptId

private val clazz: Class<*> by lazy {
    Class.forName("com.ruoyi.system.service.ISysDeptService")
}

private val sysDeptService: Any by lazy {
    SpringUtils.getBean(clazz)
}

private val buildDeptTreeSelectMethod: Method by lazy {
    clazz.getDeclaredMethod("buildDeptTreeSelect", List::class.java)
}

private val selectDeptByIdMethod: Method by lazy {
    clazz.getDeclaredMethod("selectDeptById", Long::class.javaObjectType)
}

private val jdbcTemplate: JdbcTemplate by lazy {
    SpringUtils.getBean(JdbcTemplate::class.java)
}

@Suppress("UNCHECKED_CAST")
fun depIdsById(): List<Long> {
    val deptId = SecurityUtils.getDeptId()
    val sysDept = selectDeptByIdMethod.invoke(sysDeptService, deptId) as SysDept
    val trees = buildDeptTreeSelectMethod.invoke(sysDeptService, listOf(sysDept)) as List<TreeSelect>
    val ids = mutableListOf<Long>()
    trees.forEach {
        ids.add(it.id)
        ids.addAll(it.children.map { child -> child.id })
    }
    return ids
}

@Suppress("UNCHECKED_CAST")
fun depTreeById(): List<TreeSelect> {
    val deptId = SecurityUtils.getDeptId()
    val sysDept = selectDeptByIdMethod.invoke(sysDeptService, deptId) as SysDept
    return buildDeptTreeSelectMethod.invoke(sysDeptService, listOf(sysDept)) as List<TreeSelect>
}

@Suppress("SqlDialectInspection", "SqlNoDataSourceInspection")
fun depIdsByRole(): List<Long> {
    val roles = SecurityUtils.getLoginUser().user.roles
    val list = jdbcTemplate.queryForList("""
        select dept_id
		from sys_role_dept
		where role_id in (${roles.joinToString(",")})
    """.trimIndent())
    return list.map { it["dept_id"] as Long }
}

private fun Class<*>.depIdFiledName(): String {
    val fields = this.declaredFields
    var fieldName = ""
    for (field in fields) {
        if (field.isAnnotationPresent(DeptId::class.java)) {
            fieldName = field.name
            break
        }
    }
    return fieldName
}

private fun specification(ids: List<Long>, fieldName: String): Specification<Any> {
    return Specification<Any> { root, _, criteriaBuilder ->
        if (ids.isNotEmpty()) {
            val cbi = criteriaBuilder.`in`(root.get<Any>(fieldName))
            ids.forEach { cbi.value(it) }
            criteriaBuilder.and(cbi)
        } else {
            criteriaBuilder.and(criteriaBuilder.equal(root.get<Any>(fieldName), -1))
        }
    }
}

fun Specification<Any>.appendDepIdsById(clazz: Class<*>): Specification<Any> {
    val fieldName = clazz.depIdFiledName()
    if (fieldName.isEmpty()) return this
    val ids = depIdsById()
    val newSpecification = specification(ids, fieldName)
    return this.and(newSpecification)
}

fun Specification<Any>.appendDepIdsByRole(clazz: Class<*>): Specification<Any> {
    val fieldName = clazz.depIdFiledName()
    if (fieldName.isEmpty()) return this
    val ids = depIdsByRole()
    val newSpecification = specification(ids, fieldName)
    return this.and(newSpecification)
}