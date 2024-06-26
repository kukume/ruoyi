package com.ruoyi.query.controller.query

import com.querydsl.core.Tuple
import com.querydsl.core.types.Expression
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.ruoyi.common.pojo.JpaPackageName
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.Method

@RestController
class DynamicQuerydslController(
    private val jpaQueryFactory: JPAQueryFactory
) {

    @PostMapping("/dynamic/query/querydsl")
    fun query(@RequestBody sql: Sql): Any {
        val from = sql.from
        val primary = from.value
        val qPrimary = qEntity(primary)
        val jpaQuery = jpaQueryFactory.from(qPrimary)
        sql.from(jpaQuery)
        sql.where(jpaQuery)
        sql.orderBy(jpaQuery)
        val total = jpaQuery.fetch().size
        sql.offset?.let { jpaQuery.offset(it) }
        sql.limit?.let { jpaQuery.limit(it) }
        val selectQArray = sql.select.map { convert(it) }.toTypedArray()
        if (selectQArray.isNotEmpty()) jpaQuery.select(*selectQArray)
        val fetch = jpaQuery.fetch()
        val list = mutableListOf<MutableMap<String, Any>>()
        for (f in fetch) {
            if (f is Tuple) {
                val map = mutableMapOf<String, Any>()
                for ((i, path) in selectQArray.withIndex()) {
                    f.get(path)?.let {
                        map[sql.select[i]] = it
                    }
                }
                list.add(map)
            } else return fetch
        }
        return QuerydslResult(list, total, sql.offset, sql.limit)
//        return list
    }


}

open class BaseSql {
    var from: From = From()
    var where: MutableList<Where> = mutableListOf()
    var orderBy: MutableList<OrderBy> = mutableListOf()

    fun from(jpaQuery: JPAQuery<*>) {
        for (leftJoin in from.leftJoin) {
            val leftJoinValue = leftJoin.value
            jpaQuery.leftJoin(qEntity(leftJoinValue))
            for (on in leftJoin.on) {
                on.on(jpaQuery)
            }
        }
        for (rightJoin in from.rightJoin) {
            val rightJoinValue = rightJoin.value
            jpaQuery.rightJoin(qEntity(rightJoinValue))
            for (on in rightJoin.on) {
                on.on(jpaQuery)
            }
        }
    }

    fun where(jpaQuery: JPAQuery<*>) {
        for (where in where) {
            val field = where.field
            val path = convert(field)
            if (where.value.toString().isNotEmpty()) {
//                val method = path::class.java.getMethod(where.type, Any::class.java)
                val method = method(path::class.java, where.type, where.value::class.java)
                val predicate = method.invoke(path, where.value) as Predicate
                jpaQuery.where(predicate)
            } else if (where.valueList.size == 2) {
                val method = path::class.java.getMethod(where.type, Comparable::class.java, Comparable::class.java)
                val predicate = method.invoke(path, *where.valueList.toTypedArray()) as Predicate
                jpaQuery.where(predicate)
            }
        }
    }

    fun orderBy(jpaQuery: JPAQuery<*>) {
        for (orderBy in orderBy) {
            val path = convert(orderBy.value)
            val orderSpecifier = path::class.java.getMethod(orderBy.type.lowercase()).invoke(path) as OrderSpecifier<*>
            jpaQuery.orderBy(orderSpecifier)
        }
    }

}

data class QuerydslResult<T> (val data: T, val total: Int, val offset: Long?, val limit: Long?)

// 实体类的包名

fun qEntity(str: String): EntityPathBase<*> {
    for (qPrefix in JpaPackageName.entityPackageNames) {
        runCatching {
            return Class.forName("$qPrefix.Q${firstUpper(str)}")
                .getDeclaredField(firstLower(str)).get(null) as EntityPathBase<*>
        }
    }
    error("没有找到q类")
}

private fun firstUpper(str: String): String {
    return str.substring(0, 1).uppercase() + str.substring(1)
}

private fun firstLower(str: String): String {
    return str.substring(0, 1).lowercase() + str.substring(1)
}

private fun method(clazz: Class<*>, name: String, paramType: Class<*>): Method {
    val methods = clazz.methods.filter { it.name == name }
    if (methods.isEmpty()) error("没有${name}的方法")
    for (method in methods) {
        if (method.parameterTypes.last() == paramType) return method
    }
    return methods.find { it.parameterTypes.last() == Any::class.java } ?: error("没有${name}的方法")
}


fun convert(str: String): Path<*> {
    val arr = str.split(".")
    var qEntity = qEntity(arr[0]) as Path<*>
    for (s in arr.stream().skip(1)) {
        qEntity = qEntity::class.java.getDeclaredField(firstLower(s)).get(qEntity) as Path<*>
    }
    return qEntity
}


class Sql: BaseSql() {
    var select: MutableList<String> = mutableListOf()
    var offset: Long? = null
    var limit: Long? = null
}

class From {
    var value: String = ""
    var leftJoin: MutableList<LeftJoin> = mutableListOf()
    var rightJoin: MutableList<RightJoin> = mutableListOf()

    @Suppress("MemberVisibilityCanBePrivate")
    class On {
        var left: String = ""
        var right: String = ""

        fun on(jpaQuery: JPAQuery<*>) {
            val pathLeft = convert(left)
            val pathRight = convert(right)
            jpaQuery.on(pathLeft::class.java.getMethod("eq", Expression::class.java).invoke(pathLeft, pathRight) as Predicate)
        }
    }

    class LeftJoin {
        var value: String = ""
        var on: List<On> = mutableListOf()
    }

    class RightJoin {
        var value: String = ""
        var on: List<On> = mutableListOf()
    }
}

class Where {
    var field: String = ""
    var value: Any = ""
    var valueList: List<Any> = mutableListOf()
    // querydsl 的比较方法名
    var type: String = "eq"
}

class OrderBy {
    var value: String = ""
    var type: String = ""
}
