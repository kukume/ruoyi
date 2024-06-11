<template>
  <span>
    <el-table-column v-for="item in filterTableShow" :label="item.label" align="center" :prop="item.key" :width="item.width" />
  </span>
</template>

<script>

export default {
  name: "ConfigDynamicTableColumn",
  props: {
    tableShow: {
      type: Array
    }
  },
  data() {
    return {
    }
  },
  computed: {
    filterTableShow() {
      return this.tableShow.filter(item => {
        let show = item.show
        if (show === undefined) show = true
        return show
      })
    }
  }
}
</script>

<style scoped lang="scss">

</style>
