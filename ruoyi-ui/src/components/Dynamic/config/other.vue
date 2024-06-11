<template>
  <div>
    <dynamic-other
      v-if="flag"
      :template-config="templateConfig"
      ref="dynamicOther"
      :page="page"
      :size="size"
      :table-show="tableShow"
      :remark="remark"
      :table-ref="tableRef"
      :table-ref-name="tableRefName"
      v-on="$listeners"
      :default-condition="defaultCondition"
      :default-sort="defaultSort"
    >
    </dynamic-other>
  </div>
</template>

<script>
import DynamicOther from "@/components/Dynamic/other.vue";
import {getQueryConfigByRemarkAndType} from "@/api/system/queryconfig";
import DynamicSpecification from '@/components/Dynamic/specification.vue'

export default {
  name: "config-dynamic-other",
  components: { DynamicSpecification, DynamicOther},
  props: {
    'page': {
      type: Number,
      default: 0
    },
    'size': {
      type: Number,
      default: 20
    },
    'tableRef': {
      type: Object
    },
    'tableRefName': {
      type: String,
      required: false
    },
    'remark': {
      type: String
    }
  },
  data() {
    return {
      templateConfig: [],
      tableShow: [],
      flag: false,
      defaultCondition: [],
      defaultSort: []
    }
  },
  mounted() {
    this.queryConfig()
  },
  methods: {
    queryConfig() {
      getQueryConfigByRemarkAndType(this.remark, 'Other').then((data) => {
        if (data) {
          this.templateConfig = JSON.parse(data.templateConfig)
          this.tableShow = JSON.parse(data.tableShow)
          this.flag = true
          this.defaultCondition = JSON.parse(data.defaultCondition)
          this.defaultSort = JSON.parse(data.defaultSort)
        } else {
          this.$message.warning('没有与其绑定的查询配置')
        }
      })
    },
    query(params) {
      this.$refs.dynamicOther.query(params)
    }
  }
}
</script>

<style scoped lang="scss">

</style>
