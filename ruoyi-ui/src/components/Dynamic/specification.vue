<template>
  <span>
    <DynamicBase ref="base" queryType="Specification" :template-config="templateConfig" :page="entity"
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
    'page': {
      type: Number,
      default: 0
    },
    'size': {
      type: Number,
      default: 20
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
    }
  },
  data() {
    return {
      templateQueryCondition: [
        { value: 'eq', label: '=' },
        { value: 'like', label: 'like' },
        { value: 'between', label: 'between' },
        { value: 'startWith', label: 'startWith' },
        { value: 'endWith', label: 'endWith' },
        { value: 'gt', label: 'gt' },
        { value: 'ge', label: 'ge' },
        { value: 'lt', label: 'lt' },
        { value: 'le', label: 'le' },
        { value: 'ne', label: 'ne' },
        { value: 'greaterThan', label: 'greaterThan' },
        { value: 'lessThan', label: 'lessThan' },
        { value: 'greaterThanOrEqualTo', label: 'greaterThanOrEqualTo' },
        { value: 'lessThanOrEqualTo', label: 'lessThanOrEqualTo' }
      ]
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.query()
    })
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
      const pageable = {
        page: this.page,
        size: this.size,
        sort
      }
      const data = {
        name: this.entity,
        query,
        pageable,
        method: this.method
      }
      this.$emit('queryParams', data)
      return data
    },
    query() {
      this.$emit('before')
      dynamicQuery(this.buildParams()).then(response => {
        this.$emit('callback', response)
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
