<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="formworkName">
        <el-input
          v-model="queryParams.formworkName"
          placeholder="请输入模板名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板索引" prop="formworkIndex">
        <el-input
          v-model="queryParams.formworkIndex"
          placeholder="请输入模板索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预留1" prop="reserve">
        <el-input
          v-model="queryParams.reserve"
          placeholder="请输入预留1"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预留2" prop="reservetwo">
        <el-input
          v-model="queryParams.reservetwo"
          placeholder="请输入预留2"
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
          v-hasPermi="['sql:formwork:add']"
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
          v-hasPermi="['sql:formwork:edit']"
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
          v-hasPermi="['sql:formwork:remove']"
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
          v-hasPermi="['sql:formwork:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="formworkList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预留2" align="center" prop="id" />
      <el-table-column label="模板名称" align="center" prop="formworkName" />
      <el-table-column label="模板索引" align="center" prop="formworkIndex" />
      <el-table-column label="预留1" align="center" prop="reserve" />
      <el-table-column label="预留2" align="center" prop="reservetwo" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['sql:formwork:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['sql:formwork:remove']"
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

    <!-- 添加或修改问题模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="模板名称" prop="formworkName">
          <el-input v-model="form.formworkName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板索引" prop="formworkIndex">
          <el-input v-model="form.formworkIndex" placeholder="请输入模板索引" />
        </el-form-item>
        <el-form-item label="预留1" prop="reserve">
          <el-input v-model="form.reserve" placeholder="请输入预留1" />
        </el-form-item>
        <el-form-item label="预留2" prop="reservetwo">
          <el-input v-model="form.reservetwo" placeholder="请输入预留2" />
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
import { listFormwork, getFormwork, delFormwork, addFormwork, updateFormwork, exportFormwork } from "@/api/sql/formwork";

export default {
  name: "Formwork",
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
      // 问题模板表格数据
      formworkList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        formworkName: null,
        formworkIndex: null,
        reserve: null,
        reservetwo: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        formworkName: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        formworkIndex: [
          { required: true, message: "模板索引不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询问题模板列表 */
    getList() {
      this.loading = true;
      listFormwork(this.queryParams).then(response => {
        this.formworkList = response.rows;
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
        formworkName: null,
        formworkIndex: null,
        reserve: null,
        reservetwo: null
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
      this.title = "添加问题模板";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getFormwork(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改问题模板";
      });
    },
    /** 提交按钮 */
    submitForm() {
        console.log(this.form.formworkIndex.length)
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
              console.log(this.form)
            updateFormwork(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addFormwork(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除问题模板编号为"' + ids + '"的数据项？').then(function() {
        return delFormwork(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有问题模板数据项？').then(() => {
        this.exportLoading = true;
        return exportFormwork(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>
