<template>
  <div>
    <el-form size="small" :inline="true">
      <el-form-item label="模板选择" prop="postCode">
        <el-select v-model="templateValue" clearable placeholder="请选择" @change="changeTemplate">
          <el-option
            v-for="item in template"
            :key="item.value"
            :label="item.label"
            clearable
            :value="item.value">
            <el-tooltip class="item" effect="dark" :content="tooltip(item)" placement="right">
              <div>{{item.label}}</div>
            </el-tooltip>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-edit" size="mini" @click="showTemplateDialog">编辑</el-button>
      </el-form-item>
      <slot name='form'></slot>
    </el-form>

    <el-dialog title="查询模板修改" :visible.sync="templateDialog" width="70%" :close-on-click-modal="false" append-to-body>
      <el-form size="small" :inline="true">
        <el-form-item label="模板名字" prop="templateName">
          <el-input v-model="templateName" type="text" placeholder="请输入模板名字" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteTemplate">删除</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-copy-document" size="mini" @click="copyTemplate">复制</el-button>
        </el-form-item>
      </el-form>
      <el-tabs v-model="templateTab">
        <el-tab-pane label="查询参数" name="query">
          <el-form size="small" :inline="true">
            <el-form-item label="字段选择">
              <el-select v-model="templateQueryFieldValue" clearable placeholder="请选择">
                <el-option
                  v-for="item in templateQueryField"
                  :key="item.value"
                  :label="item.label"
                  clearable
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-plus" size="mini" @click="addQuery">添加</el-button>
            </el-form-item>
          </el-form>

          <el-row :gutter="20" v-for="it in templateQueryContent" class="dynamicLine" :key="it.key">
            <el-col :span="6">
              <el-input :value="it.label" disabled size="small"></el-input>
            </el-col>
            <el-col :span="4">
              <el-select v-model="it.queryConditionValue" clearable placeholder="请选择" size="small">
                <el-option
                  v-for="item in filterQueryCondition(it)"
                  :key="item.value"
                  :label="item.label"
                  clearable
                  :value="item.value">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="12">
              <span v-if="it.inputType === 'text'">
                <el-input v-if="it.queryConditionValue !== 'between'" v-model="it.inputValue" placeholder="请输入内容" size="small"></el-input>
                <el-form size="small" :inline="true" v-else>
                  <el-form-item>
                    <el-input v-model="it.inputValueBefore" placeholder="请输入内容"></el-input>
                  </el-form-item>
                  <el-form-item>
                    -
                  </el-form-item>
                  <el-form-item>
                    <el-input v-model="it.inputValueAfter" placeholder="请输入内容"></el-input>
                  </el-form-item>
                </el-form>
              </span>
              <span v-else-if="it.inputType === 'select'">
                  <el-select v-model="it.inputValue" clearable placeholder="请选择">
                    <el-option
                      v-for="item in it.complex"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                </el-select>
              </span>
              <span v-else-if="it.inputType === 'remoteSelect'">
                <el-select
                  v-model="it.inputValue"
                  filterable
                  remote
                  reserve-keyword
                  placeholder="请输入关键词"
                  :remote-method="(query) => remoteQuery(query, it.complex)"
                  :loading="remoteSelect.loading">
                  <el-option
                    v-for="item in remoteSelect.options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
              </span>
              <span v-else-if="it.inputType === 'number'">
                  <el-input-number v-model="it.inputValue" label="请输入数字"></el-input-number>
                </span>
              <span v-else-if="it.inputType === 'time'">
                  <el-time-picker
                    v-model="it.inputValue"
                    format="HH:mm:ss"
                    value-format="HH:mm:ss"
                    placeholder="选择时间">
                  </el-time-picker>
              </span>
              <span v-else-if="it.inputType === 'date'">
                  <el-date-picker
                    v-model="it.inputValue"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    placeholder="选择日期">
                  </el-date-picker>
                </span>
              <span v-else-if="it.inputType === 'datetime'">
                  <el-date-picker
                    v-if="it.queryConditionValue !== 'between'"
                    v-model="it.inputValue"
                    type="datetime"
                    format="yyyy-MM-dd HH:mm:ss"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="选择日期时间">
                  </el-date-picker>
                  <el-date-picker
                    v-else
                    v-model="it.inputValueList"
                    type="datetimerange"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期">
                  </el-date-picker>
              </span>
            </el-col>
            <el-col :span="2">
              <el-button @click="deleteQuery(it.key)" type="danger" size="small">删除</el-button>
            </el-col>
          </el-row>

        </el-tab-pane>
        <el-tab-pane label="排序参数" name="second">
          <el-form size="small" :inline="true">
            <el-form-item label="字段选择">
              <el-select v-model="templateQueryFieldValue" clearable placeholder="请选择">
                <el-option
                  v-for="item in templateQueryField"
                  :key="item.value"
                  :label="item.label"
                  clearable
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-plus" size="mini" @click="addSort">添加</el-button>
            </el-form-item>
          </el-form>

          <el-row :gutter="20" v-for="(it, index) in templateSortContent" class="dynamicLine" :key="index">
            <el-col :span="8">
              <el-input :value="it.label" disabled></el-input>
            </el-col>
            <el-col :span="8">
              <el-select v-model="it.order" clearable placeholder="请选择">
                <el-option
                  key="desc"
                  label="desc"
                  value="desc">
                </el-option>
                <el-option
                  key="asc"
                  label="asc"
                  value="asc">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-button @click="moveUp(index)" type="primary" size="small">上移</el-button>
              <el-button @click="moveDown(index)" type="primary" size="small">下移</el-button>
              <el-button @click="deleteSort(it.key)" type="danger" size="small">删除</el-button>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="表格展示" name="third" v-if="tableShow.length > 0">
          <el-row>
            <el-col :span="4">
              <div>
                <el-button type="primary" size="small" @click="tableMoveUp">上移</el-button>
                <el-button type="primary" size="small" @click="tableMoveDown">下移</el-button>
              </div>
            </el-col>
            <el-col :span="20">
              <el-transfer
                :titles="['显示列', '隐藏列']"
                v-model="tableHidden"
                target-order="push"
                @left-check-change="leftCheckChange"
                :data="tableShowSort">
                <template v-slot:left-footer>
                  <el-button class="transfer-footer" size="mini" @click="tableMoveUp">上移</el-button>
                  <el-button class="transfer-footer" size="mini" @click="tableMoveDown">下移</el-button>
                </template>
              </el-transfer>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
      <span slot="footer" class="dialog-footer">
          <el-button @click="templateDialog = false" size="small">取 消</el-button>
          <el-button type="primary" @click="saveTemplate" size="small">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import { copyCondition, deleteCondition, queryCondition, saveCondition } from '@/api/dynamic'
import request from '@/utils/request'

export default {
  data() {
    return {
      template: [],
      templateName: '',
      templateId: 0,
      templateValue: '',
      templateDialog: false,
      templateTab: 'query',
      templateQueryFieldValue: '',
      templateQueryConditionValue: '',
      templateQueryContent: [],
      templateDatabase: [],
      templateSortContent: [],
      tableHidden: [],
      selectTableShow: [],
      tableShowSort: [],
      backupTable: undefined,
      finallyTableRef: this.tableRef,
      remoteSelect: {
        loading: false,
        options: []
      }
    }
  },
  name: 'dynamic-base',
  props: ['templateConfig', 'page', 'queryType', 'tableShow', 'tableRef', 'templateQueryCondition', 'tableRefName'],
  created() {
    this.listTemplate()
    this.tableShowSort = this.tableShow
  },
  mounted() {
    if (!this.finallyTableRef) {
      this.finallyTableRef = this.parentRefTable(this)
    }
  },
  computed: {
    templateQueryField() {
      let value = []
      this.templateConfig.forEach(it => {
        value.push({
          value: it.value,
          label: it.label
        })
      })
      return value
    },
    tableShowKey() {
      return this.tableShowSort.map(it => it.key)
    },
    tableShowExcludeHidden() {
      return this.tableShowSort.filter(it =>
        !this.tableHidden.find(hidden => it.key === hidden)
      )
    }
  },
  methods: {
    uuid() {
      let s = [];
      let hexDigits = "0123456789abcdef";
      for (let i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
      }
      s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
      s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
      s[8] = s[13] = s[18] = s[23] = "-";

      return s.join("");
    },
    changeTemplate() {
      let templateValue = this.templateValue
      if (templateValue) {
        const find = this.templateDatabase.find(it => it.remark === templateValue)
        if (find) {
          this.templateName = find.remark
          this.templateQueryContent = find.condition
          this.templateSortContent = find.sort
          this.templateId = find.sysQueryConditionId
          this.tableHidden = find.hidden
          const tableShowKey = find.show
          const tableShowSort = []
          tableShowKey.forEach(it => {
            const find = this.tableShow.find(iit => iit.key === it)
            if (find) {
              tableShowSort.push(find)
            }
          })
          this.tableShowSort = tableShowSort
        }
      } else {
        this.templateName = ''
        this.templateQueryContent = []
        this.templateSortContent = []
        this.templateId = ''
        this.tableHidden = []
        this.tableShowSort = this.tableShow
      }
      this.refreshTable()
    },
    showTemplateDialog() {
      if (this.tableShowSort.length === 0) {
        this.tableShowSort = this.tableShow
      }
      this.templateDialog = true
    },
    addQuery() {
      let templateQueryFieldValue = this.templateQueryFieldValue
      let find = this.templateConfig.find(it => {
        return it.value === templateQueryFieldValue
      })
      if (find) {
        let newFind = JSON.parse(JSON.stringify(find))
        newFind.key = this.uuid()
        this.templateQueryContent.push(newFind)
      }
    },
    deleteQuery(key) {
      let templateQueryContent = this.templateQueryContent
      let newValue = templateQueryContent.filter(it => {
        return it.key !== key
      })
      if (!newValue) { newValue = [] }
      this.templateQueryContent = newValue
    },
    saveTemplate() {
      if (!this.templateName) {
        this.$notify.error({
          title: '失败',
          message: '请输入模板名字'
        })
        return
      }
      if (!this.page) {
        this.$notify.error({
          title: '失败',
          message: '请在vue组件中配置remark属性'
        })
        return
      }
      let templateQueryContent = this.templateQueryContent
      const data = {
        page: this.page,
        remark: this.templateName,
        type: this.queryType,
        condition: JSON.stringify(templateQueryContent),
        sort: JSON.stringify(this.templateSortContent),
        hidden: JSON.stringify(this.tableHidden),
        show: JSON.stringify(this.tableShowKey)
      }
      // const find = this.templateDatabase.find(it => it.remark === this.templateName)
      // if (find) {
      //   data.sysQueryConditionId = find.sysQueryConditionId
      // }
      const templateId = this.templateId
      if (templateId) {
        data.sysQueryConditionId = templateId
      }
      saveCondition(data).then(() => {
        this.$notify({
          title: '成功',
          message: '保存模板成功',
          type: 'success'
        })
        this.listTemplate()
        this.refreshTable()
        this.templateDialog = false
      })
    },
    listTemplate() {
      queryCondition(this.page, this.queryType).then(data => {
        this.template = []
        data.forEach(inner => {
          inner.condition = JSON.parse(inner.condition)
          inner.sort = JSON.parse(inner.sort)
          inner.hidden = JSON.parse(inner.hidden)
          inner.show = JSON.parse(inner.show)
          const template = {
            value: inner.remark,
            label: inner.remark
          }
          this.template.push(template)
        })
        this.templateDatabase = data
        // if (data.length !== 0) {
        //   let findDefault = data.find(it => it.default)
        //   if (!findDefault) {
        //     findDefault = data[0]
        //   }
        //   this.templateValue = findDefault.remark
        //   this.changeTemplate()
        // }
      })
    },
    deleteTemplate() {
      if (this.templateId) {
        deleteCondition(this.templateId).then(() => {
          this.$notify({
            title: '成功',
            message: '删除该模板成功',
            type: 'success'
          })
          this.listTemplate()
          this.templateValue = ''
          this.templateDialog = false
        })
      } else {
        this.$notify.warning({
          title: '提示',
          message: '新建未保存的模板，无法删除'
        })
      }
    },
    addSort() {
      if (!this.templateSortContent) this.templateSortContent = []
      let templateQueryFieldValue = this.templateQueryFieldValue
      let find = this.templateConfig.find(it => {
        return it.value === templateQueryFieldValue
      })
      if (find) {
        let obj = {}
        obj.key = this.uuid()
        obj.label = find.label
        obj.value = find.value
        obj.order = 'asc'
        this.templateSortContent.push(obj)
      }
    },
    moveUp(index) {
      if (index !== 0) {
        // const templateSortContent = this.templateSortContent
        // const temp = templateSortContent[index]
        // templateSortContent[index] = templateSortContent[index - 1]
        // templateSortContent[index - 1] = temp
        // this.templateSortContent = [...templateSortContent]
        this.templateSortContent[index] = this.templateSortContent.splice(index - 1, 1, this.templateSortContent[index])[0]
      }
    },
    moveDown(index) {
      if (index !== this.templateSortContent.length - 1) {
        const templateSortContent = this.templateSortContent
        const temp = templateSortContent[index]
        templateSortContent[index] = templateSortContent[index + 1]
        templateSortContent[index + 1] = temp
        this.templateSortContent = [...templateSortContent]
      }
    },
    deleteSort(key) {
      let templateSortContent = this.templateSortContent
      let newValue = templateSortContent.filter(it => {
        return it.key !== key
      })
      if (!newValue) { newValue = [] }
      this.templateSortContent = newValue
    },
    leftCheckChange(data) {
      this.selectTableShow = data
    },
    tableMoveUp() {
      const selectTableShow = this.selectTableShow
      if (selectTableShow.length !== 1) {
        this.$notify.warning("仅支持选择一个进行上移操作")
        return
      }
      const tableHidden = this.tableHidden
      const showKeys = this.tableShowSort.filter(it => tableHidden.indexOf(it.key) === -1).map(it => it.key)
      const moveKey = selectTableShow[0]
      const index = showKeys.indexOf(moveKey)
      if (index !== 0) {
        showKeys[index] = showKeys.splice(index - 1, 1, showKeys[index])[0]
        console.log(showKeys)
        const tableShow = this.tableShow
        const newTableShow = []
        showKeys.forEach(it => {
          const find = tableShow.find(iit => iit.key === it)
          if (find) {
            newTableShow.push(find)
          }
        })
        tableHidden.forEach(it => {
          const find = tableShow.find(iit => iit.key === it)
          if (find) {
            newTableShow.push(find)
          }
        })
        console.log(newTableShow)
        this.tableShowSort = newTableShow
      }
    },
    tableMoveDown() {
      const selectTableShow = this.selectTableShow
      if (selectTableShow.length !== 1) {
        this.$notify.warning("仅支持选择一个进行下移操作")
        return
      }
      const tableHidden = this.tableHidden
      const showKeys = this.tableShowSort.filter(it => tableHidden.indexOf(it.key) === -1).map(it => it.key)
      const moveKey = selectTableShow[0]
      const index = showKeys.indexOf(moveKey)
      if (index !== showKeys.length - 1) {
        showKeys[index] = showKeys.splice(index + 1, 1, showKeys[index])[0]
        const tableShow = this.tableShow
        const newTableShow = []
        showKeys.forEach(it => {
          const find = tableShow.find(iit => iit.key === it)
          if (find) {
            newTableShow.push(find)
          }
        })
        tableHidden.forEach(it => {
          const find = tableShow.find(iit => iit.key === it)
          if (find) {
            newTableShow.push(find)
          }
        })
        this.tableShowSort = newTableShow
      }
    },
    refreshTable() {
      if (this.finallyTableRef) {
        const originColumns = this.finallyTableRef.store.states._columns
        if (!this.backupTable) {
          this.backupTable = originColumns
        }
        const columns = this.backupTable
        const tableShow = this.tableShow
        const prefix = []
        const suffix = []
        for (let i = 0; i < columns.length; i++) {
          const item = columns[i]
          const prop = item.property
          const find = tableShow.find(it => it.key === prop)
          if (find) break
          prefix.push(item)
        }
        for (let i = columns.length - 1; i > -1; i--) {
          const item = columns[i]
          const prop = item.property
          const find = tableShow.find(it => it.key === prop)
          const prefixFind = prefix.find(it => it.id === item.id)
          if (find || prefixFind) break
          suffix.push(item)
        }
        const remaining = columns.filter(it => {
          const find1 = prefix.find(iit => iit.property === it.property)
          const find2 = suffix.find(iit => iit.property === it.property)
          return !find1 && !find2
        })
        console.log(prefix)
        console.log(suffix)
        console.log(remaining)
        const newRemaining = []
        const tableShowExcludeHidden = this.tableShowExcludeHidden
        tableShowExcludeHidden.forEach(it => {
          const find = columns.find(iit => iit.property === it.key)
          if (find) {
            newRemaining.push(find)
          }
        })
        this.finallyTableRef.store.states._columns = prefix.concat(newRemaining).concat(suffix)
        this.finallyTableRef.store.updateColumns()
      }
    },
    copyTemplate() {
      if (this.templateId) {
        this.$prompt('请输入复制后的新模板的名称', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /.+/,
          inputErrorMessage: '名称格式不正确'
        }).then(({ value }) => {
          copyCondition(this.templateId, value).then((data) => {
            if (!data.code) {
              this.$message.success('复制成功')
              this.listTemplate()
            } else {
              this.$notify.error({
                title: '错误',
                message: data.message
              })
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消输入'
          })
        })
      } else {
        this.$notify.warning({
          title: '提示',
          message: '新建未保存的模板，无法复制'
        })
      }
    },
    filterQueryCondition(content) {
      const value = content.value
      const find = this.templateConfig.find(it => it.value === value)
      if (find && find.only) {
        return this.templateQueryCondition.filter(it => {
          let b = false
          const only = find.only
          only.forEach(iit => {
            if (it.value === iit) {
              b = true
            }
          })
          return b
        })
      } else {
        return this.templateQueryCondition
      }
    },
    getTableShow() {
      const columns = this.finallyTableRef.store.states._columns
      const tableShow = []
      for (let column in columns) {
        const key = column["property"]
        const label = column["label"]
        const find = this.tableShow.find(it => it.key === key)
        if (find) {
          tableShow.push(find)
        } else {
          tableShow.push({key, label})
        }
      }
      return tableShow
    },
    parentRefTable(obj) {
      if (this.tableRefName) {
        const parent = obj.$parent
        const ref = parent.$refs[this.tableRefName]
        console.log('ref' + ref)
        if (ref) {
          return ref
        } else if (parent) {
          return this.parentRefTable(parent)
        } else {
          return undefined
        }
      } else {
        return undefined
      }
    },
    remoteQuery(query, complex) {
      const url = complex.url
      request({
        url: url + "?" + complex.paramName + "=" + encodeURIComponent(query),
        method: 'get'
      }).then(data => {
        this.remoteSelect.options = data
      })
    },
    tooltip(item) {
      const find = this.templateDatabase.find(it => it.remark === item.label)
      const condition = find.condition
      let sb = ''
      condition.forEach(it => {
        let inputValue = it.inputValue
        if (!inputValue) inputValue = '空'
        sb += it.label + ' ' + it.queryConditionValue + ' ' + inputValue + ' && '
      })
      return sb.replace(/&&\s*$/, '')
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
