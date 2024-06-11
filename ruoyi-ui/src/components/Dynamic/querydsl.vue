<template>
  <span>
    <DynamicBase ref="base" queryType="Querydsl" :template-config="templateConfig" :page="remark"
                 :table-show="tableShow" :tableRef="tableRef" :templateQueryCondition="templateQueryCondition">
      <template v-slot:form>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="query">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-download" size="mini" @click="exportDocument">导出</el-button>
        </el-form-item>
      </template>
    </DynamicBase>
    <slot name="table"></slot>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="query"
    />
  </span>
</template>

<script>
import DynamicBase from '@/components/Dynamic/base.vue'
import { dynamicExportQuerydsl, dynamicQuerydsl } from '@/api/dynamic'
import { downloadFile } from '@/utils/download'

export default {
  name: 'dynamic-querydsl',
  components: { DynamicBase },
  props: {
    'templateConfig': {
      type: Array
    },
    'entity': {
      type: String
    },
    'method': {
      type: String,
      default: 'findAll'
    },
    'tableShow': {
      type: Array,
      default() {
        return []
      }
    },
    'from': {
      type: Object
    },
    'tableRef': {
      type: Object
    },
    'remark': {
      type: String,
      default: undefined
    },
    'defaultCondition': {
      type: Array,
      required: false,
      default: () => []
    },
    'defaultSort': {
      type: Array,
      required: false,
      default: () => []
    }
  },
  data() {
    return {
      templateQueryCondition: [
        { value: 'eq', label: '等于' },
        { value: 'like', label: '包含' },
        { value: 'between', label: '介于两者之间' },
        { value: 'startWith', label: '以xx开始' },
        { value: 'endWith', label: '以xx结束' },
        { value: 'gt', label: '大于（数字）' },
        { value: 'ge', label: '大于或等于（数字）' },
        { value: 'lt', label: '小于（数字）' },
        { value: 'le', label: '小于或等于（数字）' },
        { value: 'ne', label: '不等于' },
        { value: 'greaterThan', label: '大于（文字）' },
        { value: 'lessThan', label: '小于（文字）' },
        { value: 'greaterThanOrEqualTo', label: '大于或等于（文字）' },
        { value: 'lessThanOrEqualTo', label: '小于或等于（文字）' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 20
      },
      total: 0
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.query()
    })
  },
  methods: {
    buildParams() {
      const select = []
      const tableShow = this.tableShow
      tableShow.forEach(it => select.push(it.key))
      const from = this.from
      const templateQueryContent = this.$refs.base.templateQueryContent
      const where = []
      templateQueryContent.forEach(it => {
        if (it.inputValue || it.inputValueList || (it.inputValueBefore && it.inputValueAfter)) {
          const item = {}
          item.field = it.value
          item.type = it.queryConditionValue
          if (it.inputValue) item.value = it.inputValue
          if (it.inputValueList)  item.valueList = it.inputValueList
          else if (it.inputValueBefore && it.inputValueAfter) {
            item.valueList = [it.inputValueBefore, it.inputValueAfter]
          }
          where.push(item)
        }
      })
      const templateSortContent = this.$refs.base.templateSortContent
      const orderBy = []
      templateSortContent.forEach(it => {
        if (it.order) {
          const item = {}
          item.value = it.value
          item.type = it.order
          orderBy.push(item)
        }
      })
      const offset = this.queryParams.pageNum * this.queryParams.pageSize
      const limit = this.queryParams.pageSize
      return {
        select,
        from,
        where,
        orderBy,
        offset,
        limit
      }
    },
    query() {
      this.$emit('before')
      dynamicQuerydsl(this.buildParams()).then(data => {
        this.$emit('callback', data)
        this.$emit('after', data)
        const newData = data.data
        newData.forEach(item => {
          Object.keys(item).forEach(oldKey => {
            const newKey = oldKey.substring(oldKey.indexOf('.') + 1)
            item[newKey] = item[oldKey]
            delete item[oldKey]
          })
        })
        this.$emit('callbackFormat', data)
        this.$emit('afterFormat', data)
      })
    },
    buildExportParams(async) {
      const dynamicParam = this.buildParams()
      const tableShow = this.$refs.base.tableShowSort
      const hidden = this.$refs.base.tableHidden
      const select = []
      tableShow.filter(it => hidden.indexOf(it.key) === -1).forEach(it => {
        const inner = {
          title: it.label,
          value: it.key
        }
        if (it.rules) {
          inner['rules'] = it.rules
        }
        select.push(inner)
      })
      dynamicParam.select = select
      dynamicParam.async = async
      return dynamicParam
    },
    exportDocument() {
      this.$confirm('请选择同步导出还是异步导出', '提示', {
        cancelButtonClass: 'el-button--primary',
        distinguishCancelAndClose: true,
        confirmButtonText: '同步',
        cancelButtonText: '异步',
        type: 'warning'
      }).then(() => {
        const data = this.buildExportParams(false)
        dynamicExportQuerydsl(data).then(obj => {
          downloadFile(obj, `${this.entity}.xlsx`)
        })
        this.$message({
          type: 'success',
          message: '导出成功!'
        })
      }).catch(action => {
        if (action === 'cancel') {
          const data = this.buildExportParams(true)
          dynamicExportQuerydsl(data).then(() => {
            this.$message({
              type: 'success',
              message: '导出成功，稍后请前往导出页面下载文件'
            })
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">

</style>
