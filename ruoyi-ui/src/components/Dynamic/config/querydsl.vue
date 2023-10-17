<template>
  <div>
    <DynamicQuerydsl v-if="flag" :entity="entity" :template-config="templateConfig" :page="page"
                     :size="size" ref="dynamicQuerydsl" :table-show="tableShow" :from="from">
    </DynamicQuerydsl>
  </div>
</template>

<script>
import DynamicQuerydsl from "@/components/Dynamic/querydsl.vue";
import {getQueryConfigByRemarkAndType} from "@/api/system/queryconfig";

export default {
  name: 'config-dynamic-querydsl',
  components: {DynamicQuerydsl},
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
      flag: false
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
        } else {
          this.$message.warning('没有与其绑定的查询配置')
        }
      })
    }
  }
}

</script>

<style scoped lang="scss">

</style>
