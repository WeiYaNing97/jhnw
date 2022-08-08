<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模块标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入模块标题"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务类型" prop="businessType">
        <el-select v-model="queryParams.businessType" placeholder="请选择业务类型" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="方法名称" prop="method">
        <el-input
          v-model="queryParams.method"
          placeholder="请输入方法名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请求方式" prop="requestMethod">
        <el-input
          v-model="queryParams.requestMethod"
          placeholder="请输入请求方式"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作类别" prop="operatorType">
        <el-select v-model="queryParams.operatorType" placeholder="请选择操作类别" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人员" prop="operName">
        <el-input
          v-model="queryParams.operName"
          placeholder="请输入操作人员"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入部门名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请求URL" prop="operUrl">
        <el-input
          v-model="queryParams.operUrl"
          placeholder="请输入请求URL"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主机地址" prop="operIp">
        <el-input
          v-model="queryParams.operIp"
          placeholder="请输入主机地址"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作地点" prop="operLocation">
        <el-input
          v-model="queryParams.operLocation"
          placeholder="请输入操作地点"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择操作状态" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间" prop="operTime">
        <el-date-picker clearable size="small"
          v-model="queryParams.operTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择操作时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:switch_oper_log:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:switch_oper_log:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:switch_oper_log:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          :loading="exportLoading"
          @click="handleExport"
          v-hasPermi="['system:switch_oper_log:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="switch_oper_logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志主键" align="center" prop="operId" />
      <el-table-column label="模块标题" align="center" prop="title" />
      <el-table-column label="业务类型" align="center" prop="businessType" />
<!--      <el-table-column label="方法名称" align="center" prop="method" />-->
      <el-table-column label="请求方式" align="center" prop="requestMethod" />
<!--      <el-table-column label="操作类别" align="center" prop="operatorType" />-->
      <el-table-column label="操作人员" align="center" prop="operName" />
<!--      <el-table-column label="部门名称" align="center" prop="deptName" />-->
<!--      <el-table-column label="请求URL" align="center" prop="operUrl" />-->
      <el-table-column label="主机地址" align="center" prop="operIp" />
      <el-table-column label="操作地点" align="center" prop="operLocation" />
<!--      <el-table-column label="请求参数" align="center" prop="operParam" />-->
<!--      <el-table-column label="返回参数" align="center" prop="jsonResult" />-->
      <el-table-column label="操作状态" align="center" prop="status" />
      <el-table-column label="错误消息" align="center" prop="errorMsg" />
      <el-table-column label="操作时间" align="center" prop="operTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.operTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row,scope.index)"
          >详细</el-button>
<!--          v-hasPermi="['monitor:operlog:query']"-->
        </template>
      </el-table-column>
<!--      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit"-->
<!--            @click="handleUpdate(scope.row)"-->
<!--            v-hasPermi="['system:switch_oper_log:edit']"-->
<!--          >修改</el-button>-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-delete"-->
<!--            @click="handleDelete(scope.row)"-->
<!--            v-hasPermi="['system:switch_oper_log:remove']"-->
<!--          >删除</el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改操作日志记录对话框 -->
    <el-dialog title="操作日志详细" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px" size="mini">
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作模块：">{{ form.title }} / {{ typeFormat(form) }}</el-form-item>
            <el-form-item
              label="登录信息："
            >{{ form.operName }} / {{ form.operIp }} / {{ form.operLocation }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ form.operUrl }}</el-form-item>
            <el-form-item label="请求方式：">{{ form.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作方法：">{{ form.method }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求参数：">{{ form.operParam }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回参数：">{{ form.jsonResult }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <div v-if="form.status === 0">正常</div>
              <div v-else-if="form.status === 1">失败</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间：">{{ parseTime(form.operTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="异常信息：" v-if="form.status === 1">{{ form.errorMsg }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSwitch_oper_log, getSwitch_oper_log, delSwitch_oper_log, addSwitch_oper_log, updateSwitch_oper_log, exportSwitch_oper_log } from "@/api/system/switch_oper_log";

export default {
  name: "Switch_oper_log",
    dicts: ['sys_oper_type', 'sys_common_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 操作日志记录表格数据
      switch_oper_logList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        businessType: null,
        method: null,
        requestMethod: null,
        operatorType: null,
        operName: null,
        deptName: null,
        operUrl: null,
        operIp: null,
        operLocation: null,
        operParam: null,
        jsonResult: null,
        status: null,
        errorMsg: null,
        operTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询操作日志记录列表 */
    getList() {
      this.loading = true;
      listSwitch_oper_log(this.queryParams).then(response => {
          console.log(response.rows)
        this.switch_oper_logList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        operId: null,
        title: null,
        businessType: null,
        method: null,
        requestMethod: null,
        operatorType: null,
        operName: null,
        deptName: null,
        operUrl: null,
        operIp: null,
        operLocation: null,
        operParam: null,
        jsonResult: null,
        status: 0,
        errorMsg: null,
        operTime: null
      };
      this.resetForm("form");
    },
      // 操作日志类型字典翻译
      typeFormat(row, column) {
          return this.selectDictLabel(this.dict.type.sys_oper_type, row.businessType);
      },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.operId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加操作日志记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const operId = row.operId || this.ids
      getSwitch_oper_log(operId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改操作日志记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.operId != null) {
            updateSwitch_oper_log(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSwitch_oper_log(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
      /** 详细按钮操作 */
      handleView(row) {
          this.open = true;
          this.form = row;
      },
    /** 删除按钮操作 */
    handleDelete(row) {
      const operIds = row.operId || this.ids;
      this.$modal.confirm('是否确认删除操作日志记录编号为"' + operIds + '"的数据项？').then(function() {
        return delSwitch_oper_log(operIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有操作日志记录数据项？').then(() => {
        this.exportLoading = true;
        return exportSwitch_oper_log(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>
