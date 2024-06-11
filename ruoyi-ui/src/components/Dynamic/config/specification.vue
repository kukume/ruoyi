<template>
  <div>
    <DynamicSpecification
      v-if="flag"
      :entity="entity"
      :template-config="templateConfig"
      :table-show="tableShow"
      :remark="remark"
      :table-ref="tableRef"
      ref="dynamicSpecification"
      v-on="$listeners"
      :table-ref-name="tableRefName"
      :default-condition="defaultCondition"
      :default-sort="defaultSort"
    >
      <template v-slot:table>
        <slot name="table"></slot>
      </template>
    </DynamicSpecification>
  </div>
</template>

<script>
import DynamicSpecification from '@/components/Dynamic/specification.vue'
import { getQueryConfigByRemarkAndType } from '@/api/system/queryconfig'

export default {
  name: 'config-dynamic-specification',
  components: { DynamicSpecification },
  props: {
    'tableRef': {
      type: Object,
      default: undefined
    },
    'tableRefName': {
      type: String,
      required: false
    },
    'remark': {
      type: String
    },
    'entityGraph': {
      type: Object,
      default: undefined
    }
  },
  data() {
    return {
      templateConfig: [],
      tableShow: [],
      entity: '',
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
      getQueryConfigByRemarkAndType(this.remark, 'Specification').then((data) => {
        if (data) {
          this.templateConfig = JSON.parse(data.templateConfig)
          this.tableShow = JSON.parse(data.tableShow)
          this.entity = data.entity
          this.flag = true
          this.defaultCondition = JSON.parse(data.defaultCondition)
          this.defaultSort = JSON.parse(data.defaultSort)
        } else {
          this.$message.warning('没有与其绑定的查询配置')
        }
      })
    },
    query() {
      this.$refs.dynamicSpecification.query()
    }
  }
}

</script>

<style scoped lang="scss">

</style>
