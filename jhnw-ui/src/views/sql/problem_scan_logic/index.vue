<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="逻辑" prop="logic">
        <el-input
          v-model="queryParams.logic"
          placeholder="请输入逻辑"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="匹配" prop="matched">
        <el-input
          v-model="queryParams.matched"
          placeholder="请输入匹配"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="相对位置" prop="relativePosition">
        <el-input
          v-model="queryParams.relativePosition"
          placeholder="请输入相对位置"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="动作" prop="action">
        <el-input
          v-model="queryParams.action"
          placeholder="请输入动作"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="位置" prop="rPosition">
        <el-input
          v-model="queryParams.rPosition"
          placeholder="请输入位置"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="长度" prop="length">
        <el-input
          v-model="queryParams.length"
          placeholder="请输入长度"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否显示" prop="exhibit">
        <el-input
          v-model="queryParams.exhibit"
          placeholder="请输入是否显示"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="取词名称" prop="wordName">
        <el-input
          v-model="queryParams.wordName"
          placeholder="请输入取词名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="比较" prop="compare">
        <el-input
          v-model="queryParams.compare"
          placeholder="请输入比较"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="true下一条分析索引" prop="tNextId">
        <el-input
          v-model="queryParams.tNextId"
          placeholder="请输入true下一条分析索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="true下一条命令索引" prop="tComId">
        <el-input
          v-model="queryParams.tComId"
          placeholder="请输入true下一条命令索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="true问题索引" prop="tProblemId">
        <el-input
          v-model="queryParams.tProblemId"
          placeholder="请输入true问题索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="false下一条分析索引" prop="fNextId">
        <el-input
          v-model="queryParams.fNextId"
          placeholder="请输入false下一条分析索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="false下一条命令索引" prop="fComId">
        <el-input
          v-model="queryParams.fComId"
          placeholder="请输入false下一条命令索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="false问题索引" prop="fProblemId">
        <el-input
          v-model="queryParams.fProblemId"
          placeholder="请输入false问题索引"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="返回命令" prop="returnCmdId">
        <el-input
          v-model="queryParams.returnCmdId"
          placeholder="请输入返回命令"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="循环起始ID" prop="cycleStartId">
        <el-input
          v-model="queryParams.cycleStartId"
          placeholder="请输入循环起始ID"
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
          v-hasPermi="['sql:problem_scan_logic:add']"
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
          v-hasPermi="['sql:problem_scan_logic:edit']"
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
          v-hasPermi="['sql:problem_scan_logic:remove']"
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
          v-hasPermi="['sql:problem_scan_logic:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="problem_scan_logicList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键索引" align="center" prop="id" />
      <el-table-column label="逻辑" align="center" prop="logic" />
      <el-table-column label="匹配" align="center" prop="matched" />
      <el-table-column label="相对位置" align="center" prop="relativePosition" />
      <el-table-column label="匹配内容" align="center" prop="matchContent" />
      <el-table-column label="动作" align="center" prop="action" />
      <el-table-column label="位置" align="center" prop="rPosition" />
      <el-table-column label="长度" align="center" prop="length" />
      <el-table-column label="是否显示" align="center" prop="exhibit" />
      <el-table-column label="取词名称" align="center" prop="wordName" />
      <el-table-column label="比较" align="center" prop="compare" />
      <el-table-column label="内容" align="center" prop="content" />
      <el-table-column label="true下一条分析索引" align="center" prop="tNextId" />
      <el-table-column label="true下一条命令索引" align="center" prop="tComId" />
      <el-table-column label="true问题索引" align="center" prop="tProblemId" />
      <el-table-column label="false下一条分析索引" align="center" prop="fNextId" />
      <el-table-column label="false下一条命令索引" align="center" prop="fComId" />
      <el-table-column label="false问题索引" align="center" prop="fProblemId" />
      <el-table-column label="返回命令" align="center" prop="returnCmdId" />
      <el-table-column label="循环起始ID" align="center" prop="cycleStartId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['sql:problem_scan_logic:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['sql:problem_scan_logic:remove']"
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

    <!-- 添加或修改问题扫描逻辑对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="逻辑" prop="logic">
          <el-input v-model="form.logic" placeholder="请输入逻辑" />
        </el-form-item>
        <el-form-item label="匹配" prop="matched">
          <el-input v-model="form.matched" placeholder="请输入匹配" />
        </el-form-item>
        <el-form-item label="相对位置" prop="relativePosition">
          <el-input v-model="form.relativePosition" placeholder="请输入相对位置" />
        </el-form-item>
        <el-form-item label="匹配内容">
          <editor v-model="form.matchContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="动作" prop="action">
          <el-input v-model="form.action" placeholder="请输入动作" />
        </el-form-item>
        <el-form-item label="位置" prop="rPosition">
          <el-input v-model="form.rPosition" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="长度" prop="length">
          <el-input v-model="form.length" placeholder="请输入长度" />
        </el-form-item>
        <el-form-item label="是否显示" prop="exhibit">
          <el-input v-model="form.exhibit" placeholder="请输入是否显示" />
        </el-form-item>
        <el-form-item label="取词名称" prop="wordName">
          <el-input v-model="form.wordName" placeholder="请输入取词名称" />
        </el-form-item>
        <el-form-item label="比较" prop="compare">
          <el-input v-model="form.compare" placeholder="请输入比较" />
        </el-form-item>
        <el-form-item label="内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="true下一条分析索引" prop="tNextId">
          <el-input v-model="form.tNextId" placeholder="请输入true下一条分析索引" />
        </el-form-item>
        <el-form-item label="true下一条命令索引" prop="tComId">
          <el-input v-model="form.tComId" placeholder="请输入true下一条命令索引" />
        </el-form-item>
        <el-form-item label="true问题索引" prop="tProblemId">
          <el-input v-model="form.tProblemId" placeholder="请输入true问题索引" />
        </el-form-item>
        <el-form-item label="false下一条分析索引" prop="fNextId">
          <el-input v-model="form.fNextId" placeholder="请输入false下一条分析索引" />
        </el-form-item>
        <el-form-item label="false下一条命令索引" prop="fComId">
          <el-input v-model="form.fComId" placeholder="请输入false下一条命令索引" />
        </el-form-item>
        <el-form-item label="false问题索引" prop="fProblemId">
          <el-input v-model="form.fProblemId" placeholder="请输入false问题索引" />
        </el-form-item>
        <el-form-item label="返回命令" prop="returnCmdId">
          <el-input v-model="form.returnCmdId" placeholder="请输入返回命令" />
        </el-form-item>
        <el-form-item label="循环起始ID" prop="cycleStartId">
          <el-input v-model="form.cycleStartId" placeholder="请输入循环起始ID" />
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
import { listProblem_scan_logic, getProblem_scan_logic, delProblem_scan_logic, addProblem_scan_logic, updateProblem_scan_logic, exportProblem_scan_logic } from "@/api/sql/problem_scan_logic";

export default {
  name: "Problem_scan_logic",
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
      // 问题扫描逻辑表格数据
      problem_scan_logicList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        logic: null,
        matched: null,
        relativePosition: null,
        matchContent: null,
        action: null,
        rPosition: null,
        length: null,
        exhibit: null,
        wordName: null,
        compare: null,
        content: null,
        tNextId: null,
        tComId: null,
        tProblemId: null,
        fNextId: null,
        fComId: null,
        fProblemId: null,
        returnCmdId: null,
        cycleStartId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        relativePosition: [
          { required: true, message: "相对位置不能为空", trigger: "blur" }
        ],
        rPosition: [
          { required: true, message: "位置不能为空", trigger: "blur" }
        ],
        length: [
          { required: true, message: "长度不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询问题扫描逻辑列表 */
    getList() {
      this.loading = true;
      listProblem_scan_logic(this.queryParams).then(response => {
        this.problem_scan_logicList = response.rows;
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
        logic: null,
        matched: null,
        relativePosition: null,
        matchContent: null,
        action: null,
        rPosition: null,
        length: null,
        exhibit: null,
        wordName: null,
        compare: null,
        content: null,
        tNextId: null,
        tComId: null,
        tProblemId: null,
        fNextId: null,
        fComId: null,
        fProblemId: null,
        returnCmdId: null,
        cycleStartId: null
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
      this.title = "添加问题扫描逻辑";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProblem_scan_logic(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改问题扫描逻辑";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProblem_scan_logic(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProblem_scan_logic(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除问题扫描逻辑编号为"' + ids + '"的数据项？').then(function() {
        return delProblem_scan_logic(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有问题扫描逻辑数据项？').then(() => {
        this.exportLoading = true;
        return exportProblem_scan_logic(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>
