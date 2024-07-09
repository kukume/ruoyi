package com.ruoyi.query.controller.query

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph
import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraphType
import com.ruoyi.common.pojo.JpaPackageName
import com.ruoyi.common.utils.DateTimeFormatterUtils
import com.ruoyi.common.utils.SecurityUtils
import com.ruoyi.query.annoation.appendDepIdsById
import com.ruoyi.query.utils.CheckUtils
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
class DynamicJpaSpecificationController(
    private val applicationContext: ApplicationContext
) {

    @PostMapping("/dynamic/query/specification")
    fun test(@RequestBody dynamicParam: DynamicParam): Any {
        CheckUtils.table(dynamicParam.name)
        var sp = dynamicParam.toPredicate()
        val dynamicPageable = dynamicParam.pageable
        val page = dynamicPageable.page
        val size = dynamicPageable.size
        val pageable = PageRequest.of(page, size, dynamicParam.toSort())
        val name = dynamicParam.name
        val prefix = name.substring(0, 1).uppercase() + name.substring(1)
        val repositoryClass = repositoryClass(prefix)
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            val entityClass = entityClass(prefix)
            sp = sp.appendDepIdsById(entityClass)
        }
        val repository = applicationContext.getBean(repositoryClass)
        val entityGraph = dynamicParam.toEntityGraph()
        return if (entityGraph == null) {
            val method =
                repositoryClass.getMethod(dynamicParam.method, Specification::class.java, Pageable::class.java)
            method.invoke(repository, sp, pageable)
        } else {
            val method =
                repositoryClass.getMethod(dynamicParam.method, Specification::class.java, EntityGraph::class.java,
                    Pageable::class.java)
            method.invoke(repository, sp, pageable)
        }
    }

}


class DynamicParam {
    // entity的名字
    var name: String = ""
    var query: List<DynamicQuery> = mutableListOf()
    var pageable: DynamicPageable = DynamicPageable()
    var method: String = "findAll"
    var entityGraph: DynamicEntityGraph? = null

    fun toPredicate(): Specification<Any> {
        return Specification<Any> { root, _, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()
            for (dynamicQuery in query) {
                val field = dynamicQuery.field
                val fieldList = field.split(".")
                val firstField = fieldList[0]
                var path = root.get<Any>(firstField)
                fieldList.stream().skip(1).forEach {
                    path = path.get(it)
                }
                val type = dynamicQuery.type
                val value = dynamicQuery.value
                val convertValue = convertValue(path, value)
                val predicate = when (type) {
                    "eq" -> criteriaBuilder.equal(path, convertValue)
                    "like" -> criteriaBuilder.like(path as Expression<String>, "%$value%")
                    "notLike" -> criteriaBuilder.notLike(path as Expression<String>, "%$value%")
                    "between" -> {
                        val valueList = dynamicQuery.valueList
                        if (valueList.size != 2) error("参数错误，类型为between，valueList数组必须是2个")
                        val first = valueList[0]
                        val second = valueList[1]
                        convert(criteriaBuilder, path, first, second)
                    }
                    "startWith" -> criteriaBuilder.like(path as Expression<String>, "%$value")
                    "endWith" -> criteriaBuilder.like(path as Expression<String>, "$value%")
                    "gt" -> criteriaBuilder.gt(path as Expression<out Number>, convertValue as Number)
                    "ge" -> criteriaBuilder.ge(path as Expression<out Number>, convertValue as Number)
                    "lt" -> criteriaBuilder.lt(path as Expression<out Number>, convertValue as Number)
                    "le" -> criteriaBuilder.le(path as Expression<out Number>, convertValue as Number)
                    "ne" -> criteriaBuilder.notEqual(path, value)
                    "greaterThan" -> criteriaBuilder.greaterThan(path as Expression<String>, value)
                    "lessThan" -> criteriaBuilder.lessThan(path as Expression<String>, value)
                    "greaterThanOrEqualTo" -> criteriaBuilder.greaterThanOrEqualTo(path as Expression<String>, value)
                    "lessThanOrEqualTo" -> criteriaBuilder.lessThanOrEqualTo(path as Expression<String>, value)
                    else -> continue
                }
                predicate?.let {
                    predicates.add(it)
                }
            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun convertValue(path: Path<*>, value: String): Any {
        val javaType = path.javaType
        return if (javaType.isEnum) {
            val method = javaType.getDeclaredMethod("valueOf", String::class.java)
            method.isAccessible = true
            method.invoke(null, value)
        } else if (javaType == Boolean::class.javaObjectType) {
            value.toBoolean()
        } else if (javaType == Int::class.javaObjectType) {
            value.toInt()
        } else if (javaType == Long::class.javaObjectType) {
            value.toLong()
        }
        else value
    }

    fun toSort(): Sort {
        val sortList = pageable.sort
        val sortOrderList = mutableListOf<Sort.Order>()
        for (dynamicSort in sortList) {
            val order = dynamicSort.order
            val field = dynamicSort.field
            if (order.lowercase() == "asc") sortOrderList.add(Sort.Order.asc(field))
            else if (order.lowercase() == "desc") sortOrderList.add(Sort.Order.desc(field))
        }
        return Sort.by(sortOrderList)
    }

    fun toEntityGraph(): EntityGraph? {
        return if (entityGraph == null || entityGraph!!.attributePaths.isEmpty()) return null
        else {
            com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph(entityGraph!!.type, entityGraph!!.attributePaths)
        }
    }

}

class DynamicQuery {
    var field: String = ""
    var value: String = ""
    var valueList: List<String> = mutableListOf()
    // eq、like、between、startWith、endWith、gt、ge、lt、le、ne、greaterThan、lessThan、greaterThanOrEqualTo
    var type: String = "eq"
}


class DynamicSort {
    var field: String = ""
    var order: String = "asc"
}

class DynamicPageable {
    var page: Int = 0
    var size: Int = 20
    var sort: List<DynamicSort> = mutableListOf()
}

class DynamicEntityGraph {
    var type: EntityGraphType = EntityGraphType.FETCH
    var attributePaths: List<String> = arrayListOf()
}


private fun convert(build: CriteriaBuilder, path: Path<*>, text1: String, text2: String): Predicate? {
    return when (path.javaType) {
        LocalDate::class.java -> {
            build.between(path as Expression<LocalDate>,
                DateTimeFormatterUtils.parseToLocalDate(text1, "yyyy-MM-dd"),
                DateTimeFormatterUtils.parseToLocalDate(text2, "yyyy-MM-dd")
            )
        }
        LocalDateTime::class.java -> {
            build.between(path as Expression<LocalDateTime>,
                DateTimeFormatterUtils.parseToLocalDateTime(text1, "yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatterUtils.parseToLocalDateTime(text2, "yyyy-MM-dd HH:mm:ss")
            )
        }
        String::class.java -> {
            build.between(path as Expression<String>, text1, text2)
        }
        else -> null
    }
}


fun repositoryClass(prefix: String): Class<*> {
    for (repositoryPackageName in JpaPackageName.repositoryPackageNames) {
        runCatching {
            return Class.forName("$repositoryPackageName.${prefix}Repository")
        }
    }
    error("找不到repository")
}

fun entityClass(prefix: String): Class<*> {
    for (entityPackageName in JpaPackageName.entityPackageNames) {
        runCatching {
            return Class.forName("$entityPackageName.${prefix}Entity")
        }
    }
    error("找不到entity")
}