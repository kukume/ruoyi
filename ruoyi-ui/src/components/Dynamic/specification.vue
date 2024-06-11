<template>
  <span>
    <DynamicBase ref="base" queryType="Specification" :template-config="templateConfig" :page="remark"
                 :table-show="tableShow" :tableRef="tableRef" :templateQueryCondition="templateQueryCondition"
                 :tableRefName="tableRefName"
    >
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
import { dynamicExport, dynamicQuery } from '@/api/dynamic'
import { downloadFile } from '@/utils/download'

export default {
  name: 'dynamic-specification',
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
    'tableRef': {
      type: Object
    },
    'remark': {
      type: String,
      default: undefined
    },
    'entityGraph': {
      type: Object,
      default: undefined
    },
    'tableRefName': {
      type: String,
      required: false
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
        { value: 'notLike', label: '不包含' },
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
        pageSize: 10
      },
      total: 0
    }
  },
  mounted() {
      this.query()
  },
  methods: {
    buildParams() {
      const templateQueryContent = this.$refs.base.templateQueryContent
      const query = []
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
          query.push(item)
        }
      })
      const defaultCondition = this.defaultCondition
      const queryKeyList = query.filter(it => it.key).map(it => it.key)
      defaultCondition.forEach(it => {
        if (queryKeyList.indexOf(it.key) !== -1) {
          return
        }
        if (it.field && it.type && (it.inputValue || it.inputValueList)) {
          query.push(it)
        }
      })
      const templateSortContent = this.$refs.base.templateSortContent
      const sort = []
      templateSortContent.forEach(it => {
        if (it.order) {
          const item = {}
          item.field = it.value
          item.order = it.order
          sort.push(item)
        }
      })
      const defaultSort = this.defaultSort
      const sortKeyList = sort.filter(it => it.key).map(it => it.key)
      defaultSort.forEach(it => {
        if (sortKeyList.indexOf(it.key) !== -1) {
          return
        }
        if (it.field && it.order) {
          sort.push(it)
        }
      })
      const pageable = {
        page: this.queryParams.pageNum - 1,
        size: this.queryParams.pageSize,
        sort
      }
      const data = {
        name: this.entity,
        query,
        pageable,
        method: this.method,
        entityGraph: this.entityGraph
      }
      this.$emit('queryParams', data)
      return data
    },
    query() {
      this.$emit('before')
      dynamicQuery(this.buildParams()).then(response => {
        this.total = response.totalElements
        this.$emit('callback', response)
        this.$emit('after', response)
      })
    },
    buildExportParams(async) {
      const dynamicParam = this.buildParams()
      const tableShow = this.$refs.base.tableShowSort
      const headers = []
      const hidden = this.$refs.base.tableHidden
      tableShow.filter(it => hidden.indexOf(it.key) === -1).forEach(it => {
        const inner = {
          title: it.label,
          value: it.key
        }
        if (it.rules) {
          inner['rules'] = it.rules
        }
        headers.push(inner)
      })
      return { headers, dynamicParam, async }
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
        dynamicExport(data).then(obj => {
          downloadFile(obj, `${this.entity}.xlsx`)
        })
        this.$message({
          type: 'success',
          message: '导出成功!'
        })
      }).catch(action => {
        if (action === 'cancel') {
          const data = this.buildExportParams(true)
          dynamicExport(data).then(() => {
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
