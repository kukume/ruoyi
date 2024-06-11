<template>
  <span>
    <DynamicBase ref="base" queryType="Other" :template-config="templateConfig" :page="remark"
                 :table-show="tableShow" :tableRef="tableRef" :templateQueryCondition="templateQueryCondition"
                 :table-ref-name="tableRefName"
    >
      <template v-slot:form>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="query">查询</el-button>
        </el-form-item>
        <el-form-item v-if="showExport && tableShow.length !== 0">
          <el-button type="primary" icon="el-icon-download" size="mini" @click="exportDocument">导出</el-button>
        </el-form-item>
      </template>
    </DynamicBase>
  </span>
</template>

<script>

import DynamicBase from "@/components/Dynamic/base.vue";

export default {
  name: 'dynamic-other',
  components: {DynamicBase},
  props: {
    'templateConfig': {
      type: Array
    },
    'remark': {
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
      type: Object,
      default: undefined
    },
    'tableRefName': {
      type: String,
      required: false
    },
    'showExport': {
      type: Boolean,
      default: true
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
        { value: 'in', label: '在xx之内' },
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
      ]
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.query()
    })
  },
  methods: {
    buildParams(params) {
      const templateQueryContent = this.$refs.base.templateQueryContent
      const templateSortContent = this.$refs.base.templateSortContent
      const page = {}
      if (params && params.page) {
        page.page = params.page - 1
      } else {
        page.page = this.page
      }
      if (params && params.limit) {
        page.size = params.limit
      } else {
        page.size = this.size
      }
      const defaultCondition = this.defaultCondition
      const queryKeyList = templateQueryContent.filter(it => it.key).map(it => it.key)
      defaultCondition.forEach(it => {
        if (queryKeyList.indexOf(it.key) !== -1) {
          return
        }
        if (it.field && it.type && (it.inputValue || it.inputValueList)) {
          templateQueryContent.push(it)
        }
      })
      const defaultSort = this.defaultSort
      const sortKeyList = templateSortContent.filter(it => it.key).map(it => it.key)
      defaultSort.forEach(it => {
        if (sortKeyList.indexOf(it.key) !== -1) {
          return
        }
        if (it.field && it.order) {
          templateSortContent.push(it)
        }
      })
      return {
        query: templateQueryContent,
        sort: templateSortContent,
        page
      }
    },
    buildExportParams() {
      const dynamicParam = this.buildParams()
      const tableShow = this.$refs.base.tableShowSort
      const dynamicHeader = []
      const hidden = this.$refs.base.tableHidden
      tableShow.filter(it => hidden.indexOf(it.key) === -1).forEach(it => {
        const inner = {
          title: it.label,
          value: it.key
        }
        if (it.rules) {
          inner['rules'] = it.rules
        }
        dynamicHeader.push(inner)
      })
      return { dynamicHeader, dynamicParam }
    },
    query(params) {
      this.$emit('query', this.buildParams(params))
    },
    exportDocument() {
      this.$emit('export', this.buildExportParams())
    }
  }
}
</script>

<style scoped lang="scss">

</style>
