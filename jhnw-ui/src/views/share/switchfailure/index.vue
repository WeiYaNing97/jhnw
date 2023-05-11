<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="交换机品牌" prop="brand">
        <el-input
          v-model="queryParams.brand"
          placeholder="请输入交换机品牌"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="型号" prop="switchType">
        <el-select v-model="queryParams.switchType" placeholder="请选择型号" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="内部固件版本" prop="firewareVersion">
        <el-input
          v-model="queryParams.firewareVersion"
          placeholder="请输入内部固件版本"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="子版本号" prop="subVersion">
        <el-input
          v-model="queryParams.subVersion"
          placeholder="请输入子版本号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="故障关键字" prop="failureKeyword">
        <el-input
          v-model="queryParams.failureKeyword"
          placeholder="请输入故障关键字"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="故障名称" prop="failureName">
        <el-input
          v-model="queryParams.failureName"
          placeholder="请输入故障名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['share:switchfailure:add']"
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
          v-hasPermi="['share:switchfailure:edit']"
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
          v-hasPermi="['share:switchfailure:remove']"
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
          v-hasPermi="['share:switchfailure:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="switchfailureList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="故障主键" align="center" prop="failureId" />
      <el-table-column label="交换机品牌" align="center" prop="brand" />
      <el-table-column label="型号" align="center" prop="switchType" />
      <el-table-column label="内部固件版本" align="center" prop="firewareVersion" />
      <el-table-column label="子版本号" align="center" prop="subVersion" />
      <el-table-column label="故障关键字" align="center" prop="failureKeyword" />
      <el-table-column label="故障名称" align="center" prop="failureName" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['share:switchfailure:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['share:switchfailure:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改交换机故障对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交换机品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入交换机品牌" />
        </el-form-item>
        <el-form-item label="型号" prop="switchType">
          <el-select v-model="form.switchType" placeholder="请选择型号">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="内部固件版本" prop="firewareVersion">
          <el-input v-model="form.firewareVersion" placeholder="请输入内部固件版本" />
        </el-form-item>
        <el-form-item label="子版本号" prop="subVersion">
          <el-input v-model="form.subVersion" placeholder="请输入子版本号" />
        </el-form-item>
        <el-form-item label="故障关键字" prop="failureKeyword">
          <el-input v-model="form.failureKeyword" placeholder="请输入故障关键字" />
        </el-form-item>
        <el-form-item label="故障名称" prop="failureName">
          <el-input v-model="form.failureName" placeholder="请输入故障名称" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSwitchfailure, getSwitchfailure, delSwitchfailure, addSwitchfailure, updateSwitchfailure, exportSwitchfailure } from "@/api/share/switchfailure";

export default {
  name: "Switchfailure",
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
      // 交换机故障表格数据
      switchfailureList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        brand: null,
        switchType: null,
        firewareVersion: null,
        subVersion: null,
        failureKeyword: null,
        failureName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询交换机故障列表 */
    getList() {
      this.loading = true;
      listSwitchfailure(this.queryParams).then(response => {
        this.switchfailureList = response.rows;
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
        failureId: null,
        brand: null,
        switchType: null,
        firewareVersion: null,
        subVersion: null,
        failureKeyword: null,
        failureName: null,
        createTime: null
      };
      this.resetForm("form");
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
      this.ids = selection.map(item => item.failureId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加交换机故障";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const failureId = row.failureId || this.ids
      getSwitchfailure(failureId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改交换机故障";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.failureId != null) {
            updateSwitchfailure(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSwitchfailure(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const failureIds = row.failureId || this.ids;
      this.$modal.confirm('是否确认删除交换机故障编号为"' + failureIds + '"的数据项？').then(function() {
        return delSwitchfailure(failureIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有交换机故障数据项？').then(() => {
        this.exportLoading = true;
        return exportSwitchfailure(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>
