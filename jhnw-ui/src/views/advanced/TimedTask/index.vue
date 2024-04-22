<template>
  <div class="app-container">

    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="任务名称" prop="timedTaskName">
        <el-input
          v-model="queryParams.timedTaskName"
          placeholder="请输入任务名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板" prop="timedTaskParameters">

        <el-select v-model="queryParams.timedTaskParameters" filterable placeholder="请选择模板"  @focus ="handleClick(queryParams)" @keyup.enter.native="handleQuery">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.label">
          </el-option>
        </el-select>

      </el-form-item>
      <el-form-item label="开始时间" prop="timedTaskStartTime">
        <el-date-picker clearable size="small"
                        v-model="queryParams.timedTaskStartTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="选择开始时间">
        </el-date-picker>
      </el-form-item>

      <el-form-item label="间隔时间" prop="timedTaskIntervalTime">
        <el-input
          v-model="queryParams.timedTaskIntervalTime"
          placeholder="请输入间隔时间"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务状态" prop="timedTaskStatus">

        <el-select v-model="queryParams.timedTaskStatus" placeholder="请选择任务状态">
          <el-option
            v-for="item in status"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>

      </el-form-item>
      <el-form-item label="创建人姓名" prop="creatorName">
        <el-input
          v-model="queryParams.creatorName"
          placeholder="请输入创建人姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!--<el-form-item label="创建时间" prop="createdOn">
        <el-date-picker clearable size="small"
          v-model="queryParams.createdOn"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="创建时间">
        </el-date-picker>
      </el-form-item>-->
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
          v-hasPermi="['advanced:TimedTask:add']"
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
          v-hasPermi="['advanced:TimedTask:edit']"
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
          v-hasPermi="['advanced:TimedTask:remove']"
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
          v-hasPermi="['advanced:TimedTask:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表展示数据表信息-->
    <el-table v-loading="loading" :data="TimedTaskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务编号" align="center" prop="id"/>
      <el-table-column label="任务名称" align="center" prop="timedTaskName" />
      <el-table-column label="模板" align="center" prop="timedTaskParameters" />
      <el-table-column label="开始时间" align="center" prop="timedTaskStartTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.timedTaskStartTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="间隔时间" align="center" prop="timedTaskIntervalTime" />
      <el-table-column label="功能" align="center" prop="function" width="200">
        <template slot-scope="scope">
          <el-select v-model="scope.row.function" multiple >
            <el-option
              v-for="item in functionvalue"
              :key="item.value"
              :label="item.label"
              :value="item.label">
            </el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="timedTaskStatus" >
        <template slot-scope="scope">
          <!--<el-switch
            v-model="scope.row.timedTaskStatus"
            active-value="0"
            inactive-value="1"
            @change="handleChange(scope.row)"
          >
          </el-switch>-->


          <el-select v-model="scope.row.timedTaskStatus" @change="handleChange(scope.row)">
            <el-option
              v-for="(item, index) in status"
              :key="index"
              :label="item.label"
              :value="item.value"
            />
          </el-select>


        </template>
      </el-table-column>
      <el-table-column label="创建人姓名" align="center" prop="creatorName" />
      <el-table-column label="创建时间" align="center" prop="createdOn">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdOn, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['advanced:TimedTask:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['advanced:TimedTask:remove']"
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

    <!-- 添加或修改定时任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务名称" prop="timedTaskName">
          <el-input v-model="form.timedTaskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="模板" prop="timedTaskParameters">
          <!--<el-input v-model="form.timedTaskParameters" placeholder="请输入定时任务参数" />-->
          <el-select v-model="form.timedTaskParameters" filterable placeholder="请选择模板"  @focus ="handleClick(form)" @keyup.enter.native="handleQuery">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.label">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="开始时间" prop="timedTaskStartTime">
          <!--<el-date-picker clearable size="small"
                          v-model="form.timedTaskStartTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="选择定时开始时间">
          </el-date-picker>-->
          <el-date-picker
            v-model="form.timedTaskStartTime"
            type="datetime"
            placeholder="选择开始时间">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="间隔时间" prop="timedTaskIntervalTime">
          <el-input v-model="form.timedTaskIntervalTime" placeholder="请选择间隔时间" />
        </el-form-item>

        <el-form-item label="功能" prop="function">
          <el-select v-model="form.function" multiple placeholder="请选择功能">
            <el-option
              v-for="item in functionvalue"
              :key="item.value"
              :label="item.label"
              :value="item.label">
            </el-option>
          </el-select>
        </el-form-item>

        <!--<el-form-item label="任务状态">
          <el-switch
            v-model="form.timedTaskStatus"
            active-value="0"
            inactive-value="1"
          >
          </el-switch>
        </el-form-item>-->

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>



    <template>
      <el-row type="flex">
        <el-col :span="24">
          <div v-if="theOriginal!=''" style="font-weight: bold; margin-bottom: 8px;">原文章</div>
          <div v-html="theOriginal"  style="font-size: 15px;"></div>
        </el-col>
        <el-col :span="24">
          <div v-if="newarticle!=''" style="font-weight: bold; margin-bottom: 8px;">新文章</div>
          <div v-html="newarticle"  style="font-size: 15px;"></div>
        </el-col>
      </el-row>
      <div>
        <button @click="handleButtonClick">我的按钮</button>
      </div>
    </template>


  </div>
</template>

<script>
import { listTimedTask, getTimedTask, delTimedTask, addTimedTask, updateTimedTask, exportTimedTask } from "@/api/advanced/TimedTask";
import request from '@/utils/request'

export default {
  name: "TimedTask",
  data() {
    return {
        /* 富文本 */
        theOriginal: '',
        newarticle: '',

        //查询到的结果
        options: [],
        status: [
            {value: 0, label: '开启'},
            {value: 1, label: '关闭'}],
        /*value: null,*/
        functionvalue:[
            {value: 'OSPF', label: 'OSPF'},
            {value: '光衰', label: '光衰'},
            {value: '错误包', label: '错误包'},
        ],

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
      // 定时任务表格数据
      TimedTaskList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        timedTaskName: null,
        timedTaskParameters: null,
        timedTaskStartTime: '',
        timedTaskIntervalTime: null,
          function:[],
        timedTaskStatus: null,
        creatorName: null,
        createdOn: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        timedTaskName: [
          { required: true, message: "定时任务名称不能为空", trigger: "blur" }
        ],
        timedTaskParameters: [
          { required: true, message: "定时任务参数不能为空", trigger: "blur" }
        ],
        function: [
            { required: true, message: "定时任务功能不能为空", trigger: "blur" }
        ],
        timedTaskIntervalTime: [
          { required: true, message: "定时任务间隔时间不能为空", trigger: "blur" }
        ],
        timedTaskStatus: [
          { required: true, message: "定时任务开启状态不能为空", trigger: "blur" }
        ],
        creatorName: [
          { required: true, message: "定时任务创建人姓名不能为空", trigger: "blur" }
        ],
        createdOn: [
          { required: true, message: "定时任务创建时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询定时任务列表 */
    getList() {
      this.loading = true;
      listTimedTask(this.queryParams).then(response => {
        this.TimedTaskList = response.rows;
        for (let i = 0; i < this.TimedTaskList.length; i++){

        }
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
        timedTaskName: null,
        timedTaskParameters: null,
        timedTaskStartTime: '',
        timedTaskIntervalTime: null,
        function: [],
        timedTaskStatus: null,
        creatorName: null,
        createdOn: null
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
      this.title = "添加定时任务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getTimedTask(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改定时任务";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.form.timedTaskStartTime = this.convertDateFormat(this.form.timedTaskStartTime)

      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTimedTask(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTimedTask(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
      convertDateFormat(dateString) {
          const date = new Date(dateString);
          const year = date.getFullYear();
          const month = (date.getMonth() + 1).toString().padStart(2, '0');
          const day = date.getDate().toString().padStart(2, '0');
          const hours = date.getHours().toString().padStart(2, '0');
          const minutes = date.getMinutes().toString().padStart(2, '0');
          const seconds = date.getSeconds().toString().padStart(2, '0');
          return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      },

      /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除定时任务编号为"' + ids + '"的数据项？').then(function() {
        return delTimedTask(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有定时任务数据项？').then(() => {
        this.exportLoading = true;
        return exportTimedTask(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    },

      /** 关于查询定时任务登录交换机信息表的方法 */
      handleClick(queryParams) {
          // 获取用户输入的值
          let keyword = queryParams.timedTaskParameters;
          // 使用keyword去查询数据，这里假设我们有一个名为queryData的方法可以完成这个任务
          this.queryData(keyword).then(result => {
              this.options = []
              for (let i = 0 ; i <result.length;i++){
                  this.options.push({
                      value: i,
                      label: result[i]
                  })
              }

          });
      },
      queryData(keyword){
          return request({
              url:'/advanced/timedTaskRetrievalFile/getFileNames',
              method:'get',
          })
      },

      handleChange(row){

          let text = row.timedTaskStatus === "1" ? "关闭" : "开启";
          this.$modal.confirm('确认要"' + text + '""' + row.timedTaskName + '"任务吗？').then(function() {

              /*JSON.parse(JSON.stringify( ))*/
              console.log(JSON.stringify(row))
              console.log(JSON.parse(JSON.stringify(row)))

              return request({
                  url:'/advanced/TimedTask/performScheduledTasks',
                  method:'put',
                  data: row
              })

          }).then(() => {
              this.$modal.msgSuccess(text + "成功");
          }).catch(function() {
              row.timedTaskStatus = row.timedTaskStatus === "1" ? "0" : "1";
          });
      },

      handleButtonClick() {
          console.log('按钮被点击了！');
          // 在这里添加按钮点击事件的具体逻辑
          return request({
              url:'/share/ArticleComparisonUtil/fuwenbentest',
              method:'get',
          }).then(response => {
              let list = response;
              this.newarticle = list[0];
              this.theOriginal = list[1];
          })
      },
  }
};
</script>
