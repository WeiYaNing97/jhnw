<template>
  <div>
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="品牌" prop="brand">
        <el-input
          v-model="queryParams.brand"
          placeholder="请输入品牌"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
<!--      <el-form-item label="型号" prop="switchType">-->
<!--        <el-input-->
<!--          v-model="queryParams.switchType"-->
<!--          placeholder="请输入型号"-->
<!--          clearable-->
<!--          size="small"-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="内部固件版本" prop="firewareVersion">-->
<!--        <el-input-->
<!--          v-model="queryParams.firewareVersion"-->
<!--          placeholder="请输入内部固件版本"-->
<!--          clearable-->
<!--          size="small"-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="子版本号" prop="subVersion">-->
<!--        <el-input-->
<!--          v-model="queryParams.subVersion"-->
<!--          placeholder="请输入子版本号"-->
<!--          clearable-->
<!--          size="small"-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="获取up端口号命令" prop="getPortCommand">-->
<!--        <el-input-->
<!--          v-model="queryParams.getPortCommand"-->
<!--          placeholder="请输入获取up端口号命令"-->
<!--          clearable-->
<!--          size="small"-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="获取光衰参数命令" prop="getParameterCommand">-->
<!--        <el-input-->
<!--          v-model="queryParams.getParameterCommand"-->
<!--          placeholder="请输入获取光衰参数命令"-->
<!--          clearable-->
<!--          size="small"-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['advanced:error_rate_command:add']"
        >新增</el-button>
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
      </el-form-item>
    </el-form>

<!--    <el-row :gutter="10" class="mb8">-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-plus"-->
<!--          size="mini"-->
<!--          @click="handleAdd"-->
<!--          v-hasPermi="['advanced:error_rate_command:add']"-->
<!--        >新增</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['advanced:error_rate_command:edit']"-->
<!--        >修改</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="danger"-->
<!--          plain-->
<!--          icon="el-icon-delete"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleDelete"-->
<!--          v-hasPermi="['advanced:error_rate_command:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="warning"-->
<!--          plain-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          :loading="exportLoading"-->
<!--          @click="handleExport"-->
<!--          v-hasPermi="['advanced:error_rate_command:export']"-->
<!--        >导出</el-button>-->
<!--      </el-col>-->
<!--      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
<!--    </el-row>-->

    <el-table v-loading="loading" :data="error_rate_commandList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
<!--      <el-table-column label="主键" align="center" prop="id" />-->
      <el-table-column label="品牌" align="center" prop="brand" width="120px" />
      <el-table-column label="型号" align="center" prop="switchType" width="120px" />
      <el-table-column label="内部固件版本" align="center" prop="firewareVersion" width="120px" />
      <el-table-column label="子版本号" align="center" prop="subVersion" width="120px" />
      <el-table-column label="获取up端口号命令" align="center" prop="getPortCommand" />
      <el-table-column label="获取误码率命令" align="center" prop="getParameterCommand" />
      <el-table-column label="转译字符" align="center" prop="conversion" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width"  width="180px">
        <template slot-scope="scope">
          <el-button
            type="text"
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['advanced:error_rate_command:add']"
          >新增</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['advanced:error_rate_command:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['advanced:error_rate_command:remove']"
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

    <!-- 添加或修改误码率命令对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="型号" prop="switchType">
          <el-input v-model="form.switchType" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="内部固件版本" prop="firewareVersion">
          <el-input v-model="form.firewareVersion" placeholder="请输入内部固件版本" />
        </el-form-item>
        <el-form-item label="子版本号" prop="subVersion">
          <el-input v-model="form.subVersion" placeholder="请输入子版本号" />
        </el-form-item>
        <el-form-item label="获取up端口号命令" prop="getPortCommand">
          <el-input v-model="form.getPortCommand" placeholder="请输入获取up端口号命令" />
        </el-form-item>
        <el-form-item label="获取误码率命令" prop="getParameterCommand">
          <el-input v-model="form.getParameterCommand" placeholder="请输入获取误码率命令" />
        </el-form-item>
        <el-form-item label="转译字符" prop="conversion">
          <el-input v-model="form.conversion" placeholder="请输入转译字符" />
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
import { listError_rate_command, getError_rate_command, delError_rate_command, addError_rate_command, updateError_rate_command, exportError_rate_command } from "@/api/advanced/error_rate_command";

export default {
  name: "Error_rate_command",
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
      // 误码率命令表格数据
      error_rate_commandList: [],
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
        getPortCommand: null,
        getParameterCommand: null,
        conversion : null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        brand: [
          { required: true, message: "品牌不能为空", trigger: "blur" }
        ],
          getPortCommand: [
              { required: true, message: "获取up端口号命令不能为空", trigger: "blur" }
          ],
          getParameterCommand: [
              { required: true, message: "获取光衰参数命令不能为空", trigger: "blur" }
          ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询误码率命令列表 */
    getList() {
      this.loading = true;
      listError_rate_command(this.queryParams).then(response => {
        this.error_rate_commandList = response.rows;
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
        brand: null,
        switchType: null,
        firewareVersion: null,
        subVersion: null,
        getPortCommand: null,
        getParameterCommand: null,
        conversion : 'GE:GigabitEthernet'
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
      this.title = "添加误码率命令";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getError_rate_command(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改误码率命令";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateError_rate_command(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addError_rate_command(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除误码率命令编号为"' + ids + '"的数据项？').then(function() {
        return delError_rate_command(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有误码率命令数据项？').then(() => {
        this.exportLoading = true;
        return exportError_rate_command(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>
