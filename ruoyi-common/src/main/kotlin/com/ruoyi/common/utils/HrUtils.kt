package com.ruoyi.common.utils

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 获取hr系统的接口
 */
object HrUtils {

    private lateinit var hrPeopleCache: HrPeopleCache

    @JvmStatic
    fun people(): List<HrPeople> {
        return if (!this::hrPeopleCache.isInitialized || hrPeopleCache.isExpire()) {
            hrPeopleCache = HrPeopleCache()
            val jsonNode = OkHttpUtils.getJson("https://web.fnl56.com/FnlHR/api/getAllStaff.do")
            val data = jsonNode.convertValue<List<HrPeople>>()
            hrPeopleCache.data = data
            hrPeopleCache.get()
        } else {
            hrPeopleCache.get()
        }
    }


    @JvmStatic
    fun getPeople(staffCode : String): StaffCode {
        val jsonNode = OkHttpUtils.getJson("https://web.fnl56.com/FnlHR/api/findByStaffCode.do?staffcode=$staffCode")
        return if(jsonNode["state"].asText()=="y") jsonNode["data"].convertValue()
        else return StaffCode()
    }

    @JvmStatic
    fun findPeopleByStaffCode(staffCode : String): HrPeopleDetail {
        val jsonNode = OkHttpUtils.getJson("https://web.fnl56.com/FnlHR/api/findByStaffCode.do?staffcode=$staffCode")
        if (!jsonNode.has("state")) error(jsonNode["content"].asText() + ":" + jsonNode["data"].asText())
        return if(jsonNode["state"].asText()=="y") jsonNode["data"].convertValue()
        else error("没有找到该员工")
    }

    @JvmStatic
    fun getPeople(returnType: String, timeStart: String, timeEnd: String): List<HrPeopleDetail> {
        val jsonNode = OkHttpUtils.postJson("https://web.fnl56.com/FnlHR/api/getStaffInfo.do",
            OkUtils.json("""
                {"returnType":"$returnType","timeStart":"$timeStart","timeEnd":"$timeEnd"}
            """.trimIndent()))
        return jsonNode.convertValue()
    }

}

open class SimpleCache<T> {
    var data: T? = null
    private var expire: Long = System.currentTimeMillis() + 1000 * 60 * 60

    fun isExpire() = System.currentTimeMillis() > expire

    fun refresh(data: T) {
        this.data = data
        expire = System.currentTimeMillis() + 1000 * 60 * 60
    }

    fun get() = data!!
}

class HrPeopleCache: SimpleCache<List<HrPeople>>()

class HrPeople {
    var address: String = ""
    var departmentName: String = ""
    @JsonProperty("hukou_Address")
    var originAddress: String = ""
    var idCard: String = ""
    var keName: String = ""
    var mobile: String = ""
    var nativePlace: String = ""
    var staffCode: String = ""
    var staffName: String = ""
    var staffType: String = ""
}

class HrPeopleDetail {
    @JsonProperty("departmentname")
    var departmentName: String = ""
    @JsonProperty("kename")
    var keName: String = ""
    var mobile: String = ""
    @JsonProperty("staffcode")
    var staffCode: String = ""
    @JsonProperty("staffname")
    var staffName: String = ""
    var deptGroupName: String = ""
    var sex: String = ""
    var birthday: String = ""
    @JsonProperty("idcard")
    var idCard: String = ""
    @JsonProperty("outtime")
    var outTime: String = ""
}

class StaffCode{
    var staffcode: String ?= ""
    var staffname: String ?= ""
    var departmentcode: String ?= ""
    var departmentname: String ?= ""
    var kename: String ?= ""
    var sectioncode: String ?= ""
}
