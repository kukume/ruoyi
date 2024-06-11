<template>
  <div class="app-container">
    <DynamicSpecification :entity="page" :template-config="templateConfig" :page="queryParams.pageNum - 1"
                          :size="queryParams.pageSize" ref="query" @callback="queryCallback" @before="before"
                          :table-show="tableShow" :table-ref="this.$refs.table" @queryParams="params">
      <template v-slot:table>
        <el-table v-loading="loading" :data="dataList" ref="table">
          <el-table-column label="页面" align="center" prop="page" />
          <el-table-column label="异常原因" align="center" prop="error" />
          <el-table-column label="路径" align="center" prop="path" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-download"
                @click="download(scope.row)"
              >下载</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </DynamicSpecification>
  </div>
</template>

<script>
import DynamicSpecification from '@/components/Dynamic/specification.vue'
import { downloadExport } from '@/api/system/export'
import { downloadFile } from '@/utils/download'

export default {
  name: "Export",
  components: { DynamicSpecification },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 岗位表格数据
      dataList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postCode: undefined,
        postName: undefined,
        status: undefined
      },
      templateConfig: [
        {
          value: "page",
          label: "页面",
          inputType: "text",
          complex: [
          ]
        },
        {
          value: "error",
          label: "异常原因",
          inputType: "text",
          complex: [
          ]
        },
        {
          value: "path",
          label: "路径",
          inputType: "text",
          complex: [
          ]
        }
      ],
      page: 'sysExport',
      tableShow: [
        { key: 'page', label: '页面' },
        { key: 'error', label: '异常原因' },
        { key: 'path', label: '路径' }
      ]
    };
  },
  computed: {
  },
  methods: {
    before() {
      this.loading = true
    },
    queryCallback(response) {
      this.dataList = response.content
      this.total = response.totalElements
      this.loading = false
    },
    getList() {
      this.$refs.query.query()
    },
    params(data) {
      const userid = this.$store.state.user.id
      data.query.push({ field: 'sysUserId', type: 'eq', value: userid })
    },
    download(row) {
      const id = row.sysExportId
      downloadExport(id).then(obj => {
        downloadFile(obj, `${row.page}.xlsx`)
      })
    }
  }
}
</script>
