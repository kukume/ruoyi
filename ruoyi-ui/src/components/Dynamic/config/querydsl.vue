<template>
  <div>
    <DynamicQuerydsl
      v-if="flag"
      :entity="entity"
      :template-config="templateConfig"
      ref="dynamicQuerydsl"
      :table-show="tableShow"
      :from="from"
      :remark="remark"
      :table-ref="tableRef"
      v-on="$listeners"
      :table-ref-name="tableRefName"
      :default-condition="defaultCondition"
      :default-sort="defaultSort"
    >
      <template v-slot:table>
        <slot name="table"></slot>
      </template>
    </DynamicQuerydsl>
  </div>
</template>

<script>
import DynamicQuerydsl from "@/components/Dynamic/querydsl.vue";
import {getQueryConfigByRemarkAndType} from "@/api/system/queryconfig";
import DynamicSpecification from '@/components/Dynamic/specification.vue'

export default {
  name: 'config-dynamic-querydsl',
  components: { DynamicSpecification, DynamicQuerydsl},
  props: {
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
      from: {},
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
      getQueryConfigByRemarkAndType(this.remark, 'Querydsl').then((data) => {
        if (data) {
          this.templateConfig = JSON.parse(data.templateConfig)
          this.tableShow = JSON.parse(data.tableShow)
          this.entity = data.entity
          this.from = JSON.parse(data.from)
          this.flag = true
          this.defaultCondition = JSON.parse(data.defaultCondition)
          this.defaultSort = JSON.parse(data.defaultSort)
        } else {
          this.$message.warning('没有与其绑定的查询配置')
        }
      })
    },
    query() {
      this.$refs.dynamicQuerydsl.query()
    }
  }
}

</script>

<style scoped lang="scss">

</style>
