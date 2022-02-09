<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="交换机ip" prop="switchIp">
        <el-input
          v-model="queryParams.switchIp"
          placeholder="请输入交换机ip"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交换机姓名" prop="switchName">
        <el-input
          v-model="queryParams.switchName"
          placeholder="请输入交换机姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交换机密码" prop="switchPassword">
        <el-input
          v-model="queryParams.switchPassword"
          placeholder="请输入交换机密码"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="问题索引" prop="problemId">
        <el-input
          v-model="queryParams.problemId"
          placeholder="请输入问题索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="命令索引" prop="comId">
        <el-input
          v-model="queryParams.comId"
          placeholder="请输入命令索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参数索引" prop="valueId">
        <el-input
          v-model="queryParams.valueId"
          placeholder="请输入参数索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否解决" prop="resolved">
        <el-input
          v-model="queryParams.resolved"
          placeholder="请输入是否解决"
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
          v-hasPermi="['sql:switch_problem:add']"
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
          v-hasPermi="['sql:switch_problem:edit']"
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
          v-hasPermi="['sql:switch_problem:remove']"
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
          v-hasPermi="['sql:switch_problem:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="switch_problemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="交换机ip" align="center" prop="switchIp" />
      <el-table-column label="交换机姓名" align="center" prop="switchName" />
      <el-table-column label="交换机密码" align="center" prop="switchPassword" />
      <el-table-column label="问题索引" align="center" prop="problemId" />
      <el-table-column label="命令索引" align="center" prop="comId" />
      <el-table-column label="参数索引" align="center" prop="valueId" />
      <el-table-column label="是否解决" align="center" prop="resolved" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['sql:switch_problem:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['sql:switch_problem:remove']"
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

    <!-- 添加或修改交换机问题对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交换机ip" prop="switchIp">
          <el-input v-model="form.switchIp" placeholder="请输入交换机ip" />
        </el-form-item>
        <el-form-item label="交换机姓名" prop="switchName">
          <el-input v-model="form.switchName" placeholder="请输入交换机姓名" />
        </el-form-item>
        <el-form-item label="交换机密码" prop="switchPassword">
          <el-input v-model="form.switchPassword" placeholder="请输入交换机密码" />
        </el-form-item>
        <el-form-item label="问题索引" prop="problemId">
          <el-input v-model="form.problemId" placeholder="请输入问题索引" />
        </el-form-item>
        <el-form-item label="命令索引" prop="comId">
          <el-input v-model="form.comId" placeholder="请输入命令索引" />
        </el-form-item>
        <el-form-item label="参数索引" prop="valueId">
          <el-input v-model="form.valueId" placeholder="请输入参数索引" />
        </el-form-item>
        <el-form-item label="是否解决" prop="resolved">
          <el-input v-model="form.resolved" placeholder="请输入是否解决" />
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
import { listSwitch_problem, getSwitch_problem, delSwitch_problem, addSwitch_problem, updateSwitch_problem, exportSwitch_problem } from "@/api/sql/switch_problem";

export default {
  name: "Switch_problem",
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
      // 交换机问题表格数据
      switch_problemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        switchIp: null,
        switchName: null,
        switchPassword: null,
        problemId: null,
        comId: null,
        valueId: null,
        resolved: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        switchIp: [
          { required: true, message: "交换机ip不能为空", trigger: "blur" }
        ],
        switchName: [
          { required: true, message: "交换机姓名不能为空", trigger: "blur" }
        ],
        switchPassword: [
          { required: true, message: "交换机密码不能为空", trigger: "blur" }
        ],
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
    /** 查询交换机问题列表 */
    getList() {
      this.loading = true;
      listSwitch_problem(this.queryParams).then(response => {
        this.switch_problemList = response.rows;
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
        id: null,
        switchIp: null,
        switchName: null,
        switchPassword: null,
        problemId: null,
        comId: null,
        valueId: null,
        resolved: null,
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加交换机问题";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getSwitch_problem(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改交换机问题";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSwitch_problem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSwitch_problem(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除交换机问题编号为"' + ids + '"的数据项？').then(function() {
        return delSwitch_problem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有交换机问题数据项？').then(() => {
        this.exportLoading = true;
        return exportSwitch_problem(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>
