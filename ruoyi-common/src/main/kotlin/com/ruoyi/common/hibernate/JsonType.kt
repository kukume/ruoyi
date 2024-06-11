package com.ruoyi.common.hibernate

import com.fasterxml.jackson.databind.ObjectMapper
import com.ruoyi.common.utils.SpringUtils
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.descriptor.java.ObjectJavaType
import org.hibernate.usertype.DynamicParameterizedType
import org.hibernate.usertype.UserType
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.util.*

class JsonType: UserType<Any>, DynamicParameterizedType {

    private lateinit var className: String
    private lateinit var propertyName: String

    override fun setParameterValues(parameters: Properties) {
        className = parameters.getProperty("org.hibernate.type.ParameterType.returnedClass")
        propertyName = parameters.getProperty("org.hibernate.type.ParameterType.propertyName")
    }

    override fun equals(x: Any?, y: Any?): Boolean {
        return x == y
    }

    override fun hashCode(x: Any?): Int {
        return x?.hashCode() ?: 0
    }

    override fun returnedClass(): Class<Any> {
        return javaType.javaTypeClass
    }

    private val javaType = ObjectJavaType.INSTANCE

    override fun getSqlType(): Int {
        return Types.VARCHAR
    }


    override fun nullSafeGet(
        rs: ResultSet,
        position: Int,
        session: SharedSessionContractImplementor?,
        owner: Any?
    ): Any {
        val value = rs.getObject(position)
        return SpringUtils.getBean<ObjectMapper>().readValue(value?.toString() ?: "{}", Class.forName(className))
    }

    override fun nullSafeSet(
        st: PreparedStatement,
        value: Any?,
        index: Int,
        session: SharedSessionContractImplementor?
    ) {
        if (value == null) {
            st.setObject(index, "{}")
        } else {
            val bean = SpringUtils.getBean<ObjectMapper>()
            st.setObject(index, bean.writeValueAsString(value))
        }
    }

    override fun deepCopy(value: Any?): Any? {
        return value
    }

    override fun isMutable(): Boolean {
        return true
    }

    override fun disassemble(value: Any?): Serializable {
        TODO("Not yet implemented")
    }

    override fun assemble(cached: Serializable?, owner: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun replace(detached: Any, managed: Any, owner: Any): Any {
        return detached
    }
}
