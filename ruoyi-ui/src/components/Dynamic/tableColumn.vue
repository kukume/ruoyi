<template>
  <span v-for="item in filterTableShow">
    <el-table-column :label="item.label" align="center" :prop="item.key" :width="item.width" />
  </span>
</template>

<script>
export default {
  name: "DynamicTableColumn",
  props: {
    tableShow: {
      type: Array,
      default: []
    },
    remark: {
      type: String
    }
  },
  computed: {
    filterTableShow() {
      return this.tableShow.filter(item => item.show ? item.show : true)
    }
  }
}
</script>

<style scoped lang="scss">

</style>
