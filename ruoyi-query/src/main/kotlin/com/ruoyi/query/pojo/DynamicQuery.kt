package com.ruoyi.query.pojo

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonProperty
import com.ruoyi.query.controller.query.*
import com.ruoyi.query.controller.query.Head
import com.ruoyi.query.controller.query.excelHead
import com.ruoyi.query.controller.query.getField
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import java.util.*

class DynamicQueryParam {
    var query: MutableList<DynamicQuery> = mutableListOf()
    var page: DynamicPage = DynamicPage()
    var sort: List<DynamicSort> = mutableListOf()

    fun filterQuery(field: String): List<DynamicQuery> {
        return query.filter { it.field == field }
    }

    fun findQuery(field: String): DynamicQuery? {
        return filterQuery(field).firstOrNull()
    }

    private fun toDynamicParam(entityName: String): DynamicParam {
        val param = DynamicParam()
        param.name = entityName
        val list = mutableListOf<SpecificationDynamicQuery>()
        for (dynamicQuery in query) {
            list.add(dynamicQuery.toDynamicQuery())
        }
        param.query = list
        return param
    }

    fun toPageable(): Pageable {
        return PageRequest.of(page.page, page.size, toSort())
    }

    fun toSort(): Sort {
        val list = mutableListOf<Sort.Order>()
        for (dynamicSort in sort) {
            val o = if (dynamicSort.order == "asc") {
                Sort.Order.asc(dynamicSort.field)
            } else if (dynamicSort.order == "desc") {
                Sort.Order.desc(dynamicSort.field)
            } else continue
            list.add(o)
        }
        return Sort.by(list)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> toSpecification(entityName: String): Specification<T> {
        val param = toDynamicParam(entityName)
        return param.toPredicate() as Specification<T>
    }

}

typealias SpecificationDynamicQuery =  com.ruoyi.query.controller.query.DynamicQuery
typealias SpecificationDynamicSort =  com.ruoyi.query.controller.query.DynamicSort

class DynamicQuery {
    @JsonProperty("inputValue")
    @JSONField(name = "inputValue")
    var value: String = "" // 输入的值
    @JsonProperty("inputValueList")
    @JSONField(name = "inputValueList")
    var valueList: List<String> = mutableListOf()
    @JsonProperty("queryConditionValue")
    @JSONField(name = "queryConditionValue")
    var condition: String = ""
    @JsonProperty("value")
    @JSONField(name = "value")
    var field: String = ""

    fun toDynamicQuery(): SpecificationDynamicQuery {
        val query = SpecificationDynamicQuery()
        query.field = field
        query.type = condition
        query.value = value
        query.valueList = valueList
        return query
    }
}


class DynamicPage {
    var page: Int = 0
    var size: Int = 0
}

class DynamicSort {
    var order: String = ""
    @JsonProperty("value")
    @JSONField(name = "value")
    var field: String = ""
}

class DynamicExportParam {
    var dynamicHeader: List<DynamicHeader> = mutableListOf()
    var dynamicQuery: DynamicQueryParam = DynamicQueryParam()

    fun export(list: List<*>): XSSFWorkbook {
        val excel = excelHead(dynamicHeader)
        excel.excelBody(dynamicHeader, list)
        return excel.workbook
    }
}

class DynamicHeader: Head {
    var title: String = ""
    @JsonProperty("value")
    @JSONField(name = "value")
    var field: String = ""
    var rules = mutableListOf<Rule>()
}