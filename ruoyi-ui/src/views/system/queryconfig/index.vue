<template>
  <div class="app-container">
    <DynamicSpecification entity="SysQueryConfig" :template-config="templateConfig" :page="queryParams.pageNum - 1"
                          :size="queryParams.pageSize" ref="query" @callback="queryCallback" @before="before"
                          :table-show="tableShow" :table-ref="this.$refs.table">
    </DynamicSpecification>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <right-toolbar :search="false" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="data" ref="table">
      <el-table-column label="实体类前缀" align="center" prop="entity" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="模板配置" align="center" prop="templateConfig" />
      <el-table-column label="表格显示" align="center" prop="tableShow" />
      <el-table-column label="类型" align="center" prop="type" />
      <el-table-column label="from" align="center" prop="from" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="新增/修改" :visible.sync="open" append-to-body :close-on-click-modal="false">
      <el-form size="small">
        <el-form-item label="实体类前缀" prop="entity">
          <el-input v-model="change.entity" type="text" placeholder="请输入实体类前缀" />
        </el-form-item>
        <el-form-item label="查询配置备注" prop="remark">
          <el-input v-model="change.remark" type="text" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="editTemplateConfig">编辑模板配置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="editTableShow">编辑表格显示</el-button>
        </el-form-item>
        <el-form-item v-if="change.type === 'Querydsl'">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="editFrom">编辑FROM</el-button>
        </el-form-item>
        <el-form-item label="配置类型">
          <el-radio-group v-model="change.type">
            <el-radio label="Specification"></el-radio>
            <el-radio label="Querydsl"></el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
          <el-button @click="open = false" size="small">取 消</el-button>
          <el-button type="primary" @click="save" size="small">确 定</el-button>
      </span>

      <el-dialog title="模板配置" :visible.sync="templateConfigOpen" append-to-body :close-on-click-modal="false">
        <el-form size="small" :inline="true">
          <el-form-item>
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="addTemplateConfigSave">添加</el-button>
          </el-form-item>
        </el-form>
        <el-row :gutter="20" class="dynamicLine">
          <el-col :span="5" class="text-center">实体类的属性名</el-col>
          <el-col :span="5" class="text-center">标签</el-col>
          <el-col :span="5" class="text-center">输入类型</el-col>
          <el-col :span="5" class="text-center">复杂表达式</el-col>
          <el-col :span="4" class="text-center">操作</el-col>
        </el-row>
        <el-row :gutter="20" v-for="it in templateConfigSave" class="dynamicLine" :key="it.key">
          <el-col :span="5">
            <el-input v-model="it.value" size="small"></el-input>
          </el-col>
          <el-col :span="5">
            <el-input v-model="it.label" size="small"></el-input>
          </el-col>
          <el-col :span="5">
            <el-select v-model="it.inputType" clearable placeholder="请选择" size="small">
              <el-option
                v-for="item in inputTypes"
                :key="item.value"
                :label="item.label"
                clearable
                :value="item.value">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="5">
            <el-input type="textarea" v-model="it.complex" size="small"></el-input>
          </el-col>
          <el-col :span="4">
            <el-button type="danger" size="small" @click="deleteTemplateConfigSave(it.key)">删除</el-button>
          </el-col>
        </el-row>
        <span slot="footer" class="dialog-footer">
          <el-button @click="templateConfigOpen = false" size="small">取 消</el-button>
          <el-button type="primary" @click="saveTemplateConfigSave" size="small">确 定</el-button>
        </span>
      </el-dialog>

      <el-dialog title="表格显示" :visible.sync="tableShowOpen" append-to-body :close-on-click-modal="false">
        <el-form size="small" :inline="true">
          <el-form-item>
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="addTableShowSave">添加</el-button>
          </el-form-item>
        </el-form>
        <el-row :gutter="20" class="dynamicLine">
          <el-col :span="7" class="text-center">实体类的属性名</el-col>
          <el-col :span="7" class="text-center">标签</el-col>
          <el-col :span="7" class="text-center">规则</el-col>
          <el-col :span="3" class="text-center">操作</el-col>
        </el-row>
        <el-row :gutter="20" v-for="it in tableShowSave" class="dynamicLine" :key="it.vueKey">
          <el-col :span="7">
            <el-input v-model="it.key" size="small"></el-input>
          </el-col>
          <el-col :span="7">
            <el-input v-model="it.label" size="small"></el-input>
          </el-col>
          <el-col :span="7">
            <el-input type="textarea" v-model="it.rules" size="small"></el-input>
          </el-col>
          <el-col :span="3">
            <el-button type="danger" size="small" @click="deleteTableShowSave(it.key)">删除</el-button>
          </el-col>
        </el-row>
        <span slot="footer" class="dialog-footer">
          <el-button @click="tableShowOpen = false" size="small">取 消</el-button>
          <el-button type="primary" @click="saveTableShowOpenSave" size="small">确 定</el-button>
        </span>
      </el-dialog>

      <el-dialog title="FROM" :visible.sync="fromOpen" append-to-body :close-on-click-modal="false">

      </el-dialog>

    </el-dialog>
  </div>
</template>

<script>
import {getQueryConfig, saveQueryConfig} from '@/api/system/queryconfig'
import {uuid} from "@/utils/ruoyi";
import DynamicSpecification from "@/components/Dynamic/specification.vue";

export default {
  name: 'QueryConfig',
  components: {DynamicSpecification},
  data() {
    return {
      templateConfig: [
        {
          value: "entity",
          label: "实体类前缀",
          inputType: "text",
          complex: []
        },
        {
          value: "remark",
          label: "备注",
          inputType: "text",
          complex: []
        },
        {
          value: "templateConfig",
          label: "模板配置",
          inputType: "text",
          complex: []
        },
        {
          value: "tableShow",
          label: "表格显示",
          inputType: "text",
          complex: [
          ]
        },
        {
          value: "type",
          label: "类型",
          inputType: "select",
          complex: [
            {
              label: 'Specification',
              value: 'Specification'
            },
            {
              label: 'Querydsl',
              value: 'Querydsl'
            }
          ]
        },
        {
          value: "from",
          label: "from",
          inputType: "text",
          complex: [
          ]
        }
      ],
      tableShow: [
        { key: 'entity', label: '实体类前缀' },
        { key: 'remark', label: '备注' },
        { key: 'templateConfig', label: '模板配置' },
        { key: 'tableShow', label: '表格显示' },
        { key: 'type', label: '类型' },
        { key: 'from', label: 'from' }
      ],
      loading: false,
      data: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 20
      },
      open: false,
      templateConfigOpen: false,
      tableShowOpen: false,
      fromOpen: false,
      change: {
        sysQueryConfigId: undefined,
        entity: '',
        remark: '',
        type: 'Specification',
        templateConfig: [],
        tableShow: [],
        from: '{}'
      },
      templateConfigSave: [],
      inputTypes: [
        { label: 'datetime', value: 'datetime' },
        { label: 'date', value: 'date' },
        { label: 'time', value: 'time' },
        { label: 'number', value: 'number' },
        { label: 'select', value: 'select' },
        { label: 'text', value: 'text' }
      ],
      tableShowSave: []
    }
  },
  methods: {
    queryCallback(response) {
      this.loading = false
      this.data = response.content
      this.total = response.totalElements
    },
    before() {
      this.loading = true
    },
    getList() {
      this.$refs.query.query()
    },
    handleUpdate(row) {
      this.change.sysQueryConfigId = row.sysQueryConfigId
      this.change.entity = row.entity
      this.change.remark = row.remark
      this.change.type = row.type
      const templateConfig = JSON.parse(row.templateConfig)
      templateConfig.forEach(it => {
        it.complex = JSON.stringify(it.complex)
        it.key = uuid()
      })
      this.change.templateConfig = templateConfig
      const tableShow = JSON.parse(row.tableShow)
      tableShow.forEach(it => {
        it.rules = JSON.stringify(it.rules)
        it.vueKey = uuid()
      })
      this.change.tableShow = tableShow
      try {
        this.change.from = row.from
      } catch (e) {
        this.change.from = '{}'
      }
      this.open = true
    },
    handleDelete(row) {

    },
    handleAdd() {
      this.change.sysQueryConfigId = undefined
      this.change.entity = ''
      this.change.remark = ''
      this.change.type = 'Specification'
      this.change.templateConfig = []
      this.change.tableShow = []
      this.change.from = ''
      this.open = true
    },
    editTemplateConfig() {
      this.templateConfigSave = JSON.parse(JSON.stringify(this.change.templateConfig))
      this.templateConfigOpen = true
    },
    editTableShow() {
      this.tableShowSave = JSON.parse(JSON.stringify(this.change.tableShow))
      this.tableShowOpen = true
    },
    editFrom() {
      this.$prompt('请输入From', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputValue: this.change.from.toString(),
        closeOnClickModal: false,
        inputPlaceholder: `{ value: 'SysPostEntity', leftJoin: [ { value: 'LazyEntity', on: { left: 'SysPostEntity.id', right: 'LazyEntity.postId' } } ], rightJoin: [] }`,
      }).then(({ value }) => {
        try {
          const obj = JSON.parse(value)
          if (typeof obj == 'object') {
            this.change.from = value
          } else {
            this.$message.warning('json字符串不符合格式，已替换为空对象')
            this.change.from = '{}'
          }
        } catch (e) {
          this.$message.warning('json字符串不符合格式，已替换为空对象')
          this.change.from = '{}'
        }
      }).catch(() => {

      });
    },
    addTemplateConfigSave() {
      this.templateConfigSave.push({
        value: '',
        label: '',
        inputType: 'text',
        complex: '[]',
        key: uuid()
      })
    },
    deleteTemplateConfigSave(key) {
      this.templateConfigSave = this.templateConfigSave.filter(it => it.key !== key)
    },
    saveTemplateConfigSave() {
      const save = this.templateConfigSave
      const templateConfig = []
      for (let i = 0; i < save.length; i++) {
        const item = save[i]
        if (item.label === '' || item.value === '' || item.inputType === '') {
          this.$message.warning('有必填输入框为空')
          return
        }
      }
      save.forEach(it => {
        if (it.complex !== '[]') {
          try {
            const convert = JSON.parse(it.complex)
            if (typeof convert != 'object') {
              this.$message.warning('复杂表达式格式不正确，已替换成空数组')
              it.complex = '[]'
            }
          } catch (e) {
            this.$message.warning('复杂表达式格式不正确，已替换成空数组')
            it.complex = '[]'
          }
        }
        templateConfig.push(it)
      })
      this.change.templateConfig = templateConfig
      this.templateConfigOpen = false
    },
    addTableShowSave() {
      this.tableShowSave.push({
        key: '',
        label: '',
        rules: '[]',
        vueKey: uuid()
      })
    },
    deleteTableShowSave(key) {
      this.tableShowSave = this.tableShowSave.filter(it => it.key !== key)
    },
    saveTableShowOpenSave() {
      const save = this.tableShowSave
      const tableShow = []
      for (let i = 0; i < save.length; i++) {
        const item = save[i]
        if (item.label === '' || item.value === '') {
          this.$message.warning('有必填输入框为空')
          return
        }
      }
      save.forEach(it => {
        if (it.rules !== '[]') {
          try {
            const convert = JSON.parse(it.rules)
            if (typeof convert != 'object') {
              this.$message.warning('规则格式不正确，已替换成空数组')
              it.rules = '[]'
            }
          } catch (e) {
            this.$message.warning('规则格式不正确，已替换成空数组')
            it.rules = '[]'
          }
        }
        tableShow.push(it)
      })
      this.change.tableShow = tableShow
      this.tableShowOpen = false
    },
    save() {
      const change = this.change
      const data = {}
      data.sysQueryConfigId = change.sysQueryConfigId
      data.entity = change.entity
      data.remark = change.remark
      data.type = change.type
      const tempTemplateConfig = JSON.parse(JSON.stringify(change.templateConfig))
      tempTemplateConfig.forEach(it => {
        it.key = undefined
        it.complex = JSON.parse(it.complex)
      })
      data.templateConfig = JSON.stringify(tempTemplateConfig)
      const tempTableShow = JSON.parse(JSON.stringify(change.tableShow))
      tempTableShow.forEach(it => {
        it.vueKey = undefined
        it.rules = JSON.parse(it.rules)
      })
      data.tableShow = JSON.stringify(tempTableShow)
      data.from = change.from
      if (data.entity === '' || data.remark === '' || data.type === '') {
        this.$notify.warning('必填项为空')
        return
      }
      saveQueryConfig(data).then(data => {
        if (data.code) {
          this.$notify.warning(data.message)
        } else {
          this.$notify.success('保存配置成功')
          this.getList()
          this.open = false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.dynamicLine {
  margin-bottom: 20px;
  &:last-child {
    margin-bottom: 0;
  }
}
</style>
