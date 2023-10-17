<template>
  <div>
    <DynamicSpecification v-if="flag" :entity="entity" :template-config="templateConfig" :page="page"
                          :size="size" :table-show="tableShow" ref="dynamicSpecification">
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
      entity: '',
      flag: false
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
