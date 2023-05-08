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
      <el-form-item label="品牌" prop="brand">
        <el-input
          v-model="queryParams.brand"
          placeholder="请输入品牌"
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
      <el-form-item label="参数数量" prop="numberParameters">
        <el-input
          v-model="queryParams.numberParameters"
          placeholder="请输入参数数量"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="端口号" prop="port">
        <el-input
          v-model="queryParams.port"
          placeholder="请输入端口号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="TX平均值" prop="txAverageValue">
        <el-input
          v-model="queryParams.txAverageValue"
          placeholder="请输入TX平均值"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="TX最新参数" prop="txLatestNumber">
        <el-input
          v-model="queryParams.txLatestNumber"
          placeholder="请输入TX最新参数"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="RX平均值" prop="rxAverageValue">
        <el-input
          v-model="queryParams.rxAverageValue"
          placeholder="请输入RX平均值"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="RX最新参数" prop="rxLatestNumber">
        <el-input
          v-model="queryParams.rxLatestNumber"
          placeholder="请输入RX最新参数"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="TX起始值(基准)" prop="txStartValue">
        <el-input
          v-model="queryParams.txStartValue"
          placeholder="请输入TX起始值(基准)"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="RX起始值(基准)" prop="rxStartValue">
        <el-input
          v-model="queryParams.rxStartValue"
          placeholder="请输入RX起始值(基准)"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="额定偏差" prop="ratedDeviation">
        <el-input
          v-model="queryParams.ratedDeviation"
          placeholder="请输入额定偏差"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="保留字段一" prop="valueOne">
        <el-input
          v-model="queryParams.valueOne"
          placeholder="请输入保留字段一"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="保留字段二" prop="valueTwo">
        <el-input
          v-model="queryParams.valueTwo"
          placeholder="请输入保留字段二"
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
          v-hasPermi="['sql:comparison:add']"
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
          v-hasPermi="['sql:comparison:edit']"
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
          v-hasPermi="['sql:comparison:remove']"
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
          v-hasPermi="['sql:comparison:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="comparisonList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="交换机ip" align="center" prop="switchIp" />
      <el-table-column label="品牌" align="center" prop="brand" />
      <el-table-column label="型号" align="center" prop="switchType" />
      <el-table-column label="内部固件版本" align="center" prop="firewareVersion" />
      <el-table-column label="子版本号" align="center" prop="subVersion" />
      <el-table-column label="参数数量" align="center" prop="numberParameters" />
      <el-table-column label="端口号" align="center" prop="port" />
      <el-table-column label="TX平均值" align="center" prop="txAverageValue" />
      <el-table-column label="TX最新参数" align="center" prop="txLatestNumber" />
      <el-table-column label="RX平均值" align="center" prop="rxAverageValue" />
      <el-table-column label="RX最新参数" align="center" prop="rxLatestNumber" />
      <el-table-column label="TX起始值(基准)" align="center" prop="txStartValue" />
      <el-table-column label="RX起始值(基准)" align="center" prop="rxStartValue" />
      <el-table-column label="额定偏差" align="center" prop="ratedDeviation" />
      <el-table-column label="保留字段一" align="center" prop="valueOne" />
      <el-table-column label="保留字段二" align="center" prop="valueTwo" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['sql:comparison:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['sql:comparison:remove']"
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

    <!-- 添加或修改光衰平均值比较对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交换机ip" prop="switchIp">
          <el-input v-model="form.switchIp" placeholder="请输入交换机ip" />
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
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
        <el-form-item label="参数数量" prop="numberParameters">
          <el-input v-model="form.numberParameters" placeholder="请输入参数数量" />
        </el-form-item>
        <el-form-item label="端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="TX平均值" prop="txAverageValue">
          <el-input v-model="form.txAverageValue" placeholder="请输入TX平均值" />
        </el-form-item>
        <el-form-item label="TX最新参数" prop="txLatestNumber">
          <el-input v-model="form.txLatestNumber" placeholder="请输入TX最新参数" />
        </el-form-item>
        <el-form-item label="RX平均值" prop="rxAverageValue">
          <el-input v-model="form.rxAverageValue" placeholder="请输入RX平均值" />
        </el-form-item>
        <el-form-item label="RX最新参数" prop="rxLatestNumber">
          <el-input v-model="form.rxLatestNumber" placeholder="请输入RX最新参数" />
        </el-form-item>
        <el-form-item label="TX起始值(基准)" prop="txStartValue">
          <el-input v-model="form.txStartValue" placeholder="请输入TX起始值(基准)" />
        </el-form-item>
        <el-form-item label="RX起始值(基准)" prop="rxStartValue">
          <el-input v-model="form.rxStartValue" placeholder="请输入RX起始值(基准)" />
        </el-form-item>
        <el-form-item label="额定偏差" prop="ratedDeviation">
          <el-input v-model="form.ratedDeviation" placeholder="请输入额定偏差" />
        </el-form-item>
        <el-form-item label="保留字段一" prop="valueOne">
          <el-input v-model="form.valueOne" placeholder="请输入保留字段一" />
        </el-form-item>
        <el-form-item label="保留字段二" prop="valueTwo">
          <el-input v-model="form.valueTwo" placeholder="请输入保留字段二" />
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
import { listComparison, getComparison, delComparison, addComparison, updateComparison, exportComparison } from "@/api/sql/comparison";

export default {
  name: "Comparison",
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
      // 光衰平均值比较表格数据
      comparisonList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        switchIp: null,
        brand: null,
        switchType: null,
        firewareVersion: null,
        subVersion: null,
        numberParameters: null,
        port: null,
        txAverageValue: null,
        txLatestNumber: null,
        rxAverageValue: null,
        rxLatestNumber: null,
        txStartValue: null,
        rxStartValue: null,
        ratedDeviation: null,
        valueOne: null,
        valueTwo: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        switchIp: [
          { required: true, message: "交换机ip不能为空", trigger: "blur" }
        ],
        brand: [
          { required: true, message: "品牌不能为空", trigger: "blur" }
        ],
        switchType: [
          { required: true, message: "型号不能为空", trigger: "change" }
        ],
        firewareVersion: [
          { required: true, message: "内部固件版本不能为空", trigger: "blur" }
        ],
        numberParameters: [
          { required: true, message: "参数数量不能为空", trigger: "blur" }
        ],
        port: [
          { required: true, message: "端口号不能为空", trigger: "blur" }
        ],
        txAverageValue: [
          { required: true, message: "TX平均值不能为空", trigger: "blur" }
        ],
        txLatestNumber: [
          { required: true, message: "TX最新参数不能为空", trigger: "blur" }
        ],
        rxAverageValue: [
          { required: true, message: "RX平均值不能为空", trigger: "blur" }
        ],
        rxLatestNumber: [
          { required: true, message: "RX最新参数不能为空", trigger: "blur" }
        ],
        txStartValue: [
          { required: true, message: "TX起始值(基准)不能为空", trigger: "blur" }
        ],
        rxStartValue: [
          { required: true, message: "RX起始值(基准)不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询光衰平均值比较列表 */
    getList() {
      this.loading = true;
      listComparison(this.queryParams).then(response => {
        this.comparisonList = response.rows;
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
        brand: null,
        switchType: null,
        firewareVersion: null,
        subVersion: null,
        numberParameters: null,
        port: null,
        txAverageValue: null,
        txLatestNumber: null,
        rxAverageValue: null,
        rxLatestNumber: null,
        txStartValue: null,
        rxStartValue: null,
        ratedDeviation: null,
        valueOne: null,
        valueTwo: null
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
      this.title = "添加光衰平均值比较";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getComparison(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改光衰平均值比较";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateComparison(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addComparison(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除光衰平均值比较编号为"' + ids + '"的数据项？').then(function() {
        return delComparison(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有光衰平均值比较数据项？').then(() => {
        this.exportLoading = true;
        return exportComparison(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>
