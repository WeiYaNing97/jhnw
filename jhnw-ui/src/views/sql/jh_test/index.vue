<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :rules="rules" :inline="true" v-show="showSearch" label-width="50px" :show-message="false">
      <el-form-item label="ip" prop="ip">
        <el-input
          v-model="queryParams.ip"
          placeholder="请输入ip"
          clearable
          size="small"
          style="width: 150px;"
        />
      </el-form-item>
      <el-form-item label="用户" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入用户名"
          clearable
          size="small"
          style="width: 150px;"
        />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="queryParams.password"
          placeholder="请输入密码"
          clearable
          size="small"
          style="width: 150px;"
        />
      </el-form-item>
      <el-form-item label="方式" prop="mode">
        <el-select style="width: 100px;" v-model="queryParams.mode" placeholder="请选择" clearable size="small" @change="chooseT">
          <el-option label="telnet" value="telnet"></el-option>
          <el-option label="ssh" value="ssh"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="端口" prop="port">
        <el-input style="width: 50px;" v-model="queryParams.port" size="small" :disabled="true" clearable>
        </el-input>
      </el-form-item>
      <el-form-item>
<!--   @keyup.enter.native="handleQuery"-- 回车事件 >
<el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
        <el-button type="primary" icon="el-icon-search" size="mini" @click="requestConnect">开始扫描</el-button>
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
<!--        <el-button icon="el-icon-refresh" size="mini" @click="gettest">获取数据</el-button>-->
      </el-form-item>
    </el-form>

    <el-divider></el-divider>
<!--    <input url="file:///D:/HBuilderX-test/first-test/index.html" />-->
    <WebSocketOne></WebSocketOne>

<!--    <el-upload-->
<!--      class="upload-demo"-->
<!--      action=""-->
<!--      :on-change="handleChange"-->
<!--      :on-exceed="handleExceed"-->
<!--      :on-remove="handleRemove"-->
<!--      :file-list="fileListUpload"-->
<!--      :limit="limitUpload"-->
<!--      accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"-->
<!--      :auto-upload="false">-->
<!--      <el-button size="small" type="primary">点击上传</el-button>-->
<!--      <div slot="tip" class="el-upload__tip">只 能 上 传 xlsx / xls 文 件</div>-->
<!--    </el-upload>-->

    <WebSocketTwo :queryParams="queryParams"></WebSocketTwo>

    <div class="app-container home">
      <el-row :gutter="20">
        <WebSocket></WebSocket>
      </el-row>
    </div>

<!--      下一页-->
<!--    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList"-->
<!--    />-->

    <!-- 添加或修改嘉豪测试对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="ip" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入ip" />
        </el-form-item>
        <el-form-item label="名字" prop="name">
          <el-input v-model="form.name" placeholder="请输入名字" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" />
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
import { listJh_test, getJh_test, delJh_test, addJh_test, updateJh_test, exportJh_test } from "@/api/sql/jh_test";
import WebSocket from '@/components/WebSocket/WebSocket';
import WebSocketOne from "@/components/WebSocketOne/WebSocketOne";
import WebSocketTwo from "@/components/WebSocketTwo/WebSocketTwo";
import axios from 'axios'
import log from "../../monitor/job/log";
export default {
  name: "Jh_test",
    components:{
        WebSocket,
        WebSocketOne,
        WebSocketTwo
    },
  data() {
    return {
      // 遮罩层
      loading: false,
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
      // 嘉豪测试表格数据
      jh_testList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
        aaa:'1',
      // 查询参数
      queryParams: {
          ip: '192.168.1.100',
          name: 'admin',
          password: 'admin',
          mode: null,
          port: 22,
      },
        //新添加的
        textarea:'',
        testData:[],
        tableData:[],
        // switchIp typeProblem problemName ifQuestion problemId
        tableDataq: [
            {
                switchIp:'192.168.1.1',
                hproblemId:1,
                children:[{
                    typeProblem: '安全配置',
                    hproblemId:11,
                    children: [
                        {
                            problemName:'密码明文存储',
                            ifQuestion:'noproblem_endmark',
                            hproblemId:111
                        },
                        {
                            problemName:'telnet开启',
                            ifQuestion:'haveproblem_endmark',
                            hproblemId:1111
                        }
                    ]
                },
                    {
                        typeProblem: '设备缺陷',
                        hproblemId:12,
                        children: [
                            {
                                problemName:'没有配置管理地址',
                                ifQuestion:'haveproblem_endmark',
                                hproblemId:112
                            }
                        ]
                    }
                ]
            },
            {
                switchIp:'192.168.1.100',
                problemId:2,
                children:[{
                    typeProblem: '安全配置',
                    problemId:22,
                    children: [
                        {
                            problemName:'密码明文存储',
                            ifQuestion:'noproblem_endmark',
                            problemId:222,
                        }
                    ]
                },
                ]
            }
        ],

      // 表单参数
      form: {},
      // 表单校验
      rules: {
          ip:[
              { required: true, trigger: "blur",message:null }
          ],
          name:[
              { required: true, trigger: "blur", message: "请输入您的账号" }
          ],
          password:[
              { required: true, trigger: "blur", message: "请输入您的密码" }
          ],
          mode:[
              { required: true, trigger: "change", message: "请选择连接方式" }
          ],
          port:[
              { required: true, trigger: "change", message: "端口号" }
          ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询嘉豪测试列表 */
    getList() {
      this.loading = true;
      listJh_test(this.queryParams).then(response => {
        this.jh_testList = response.rows;
        // console.log(this.jh_testList);
        this.total = response.total;
        this.loading = false;
      });
    },
      startsao(){
        ///dev-api/sql/ConnectController/testget
        axios.get('http://192.168.1.98/dev-api/sql/switch_problem/getUnresolvedProblemInformationByData').then(res=>{
           // console.log(res.data);
           function changeTreeDate(arrayJsonObj,oldKey,newKey) {
              let strtest = JSON.stringify(arrayJsonObj);
              let reg = new RegExp(oldKey,'g');
              let newStr = strtest.replace(reg,newKey);
              return JSON.parse(newStr);
           }
            let newJson = changeTreeDate(res.data,'switchProblemVOList','children');
           let newJson1 = changeTreeDate(newJson,'switchProblemCOList','children')
            // console.log(newJson);
            this.tableData = newJson1;
           // console.log(this.tableData);
            // console.log(this.tableData);
            // this.tableData.forEach((value,index) =>{
            //   value["proid"] = 0;
            // });
            this.tableData[0].children.forEach((value,index) =>{
                value["problemId"] = 111;
            });
            this.tableData[0].children[0].children.forEach((value,index) =>{
                value["problemId"] = 22;
            });
            // for (let i=0;i<this.tableData.length;i++){
            //   this.tableData[i].children.forEach((value,index) =>{
            //         value["problemId"] = 111;
            //       }
            //   )
            // };
            // this.tableData[0].children[0].children.forEach((value,index) =>{
            //     value["proid"] = 2;
            // });
            // this.tableData[0].children.children.forEach((value,index) =>{
            //     value["proid"]=1;
            // });
            console.log(this.tableData);
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
          ip: null,
          name: null,
          password: null,
          mode: null,
          port: 22,
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
      //连接交换机
      requestConnect(){
          this.$refs.queryForm.validate(valid => {
            if (valid){
                alert(JSON.stringify(this.queryParams));
                // axios.post('/dev-api/sql/ConnectController/requestConnect',JSON.stringify(this.queryParams)).then(
                //     this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
                // );
                let form = new FormData();
                for (var key in this.queryParams){
                    form.append(key,this.queryParams[key]);
                }
                console.log(JSON.stringify(this.queryParams))
                axios({
                    method:'post',
                    // http://192.168.1.98/dev-api/sql/SwitchTest/totalMethod
                    // http://192.168.1.98/dev-api/sql/SwitchInteraction/logInToGetBasicInformation
                // /dev-api/sql/ConnectController/requestConnect
                //     url:'/dev-api/sql/ConnectController/requestConnect',
                    url:'http://192.168.1.98/dev-api/sql/SwitchInteraction/logInToGetBasicInformation',
                    headers:{
                        "Content-Type": "multipart/form-data"
                    },
                    data:form
                }).then(res=>{
                        console.log('成功'),
                        console.log(res.data),
                        this.xinxi = res.data.data;
                        console.log(this.xinxi);
                },
                )
            }
          }
          );
      },

      //指定端口号
      chooseT(){
          var val=this.queryParams.mode;
          if (val=='telnet'){
              this.queryParams.port=23;
          }else if (val=='ssh'){
              this.queryParams.port=22;
          }else {
              this.queryParams.port=22;
          }
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
      this.title = "添加嘉豪测试";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getJh_test(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改嘉豪测试";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateJh_test(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addJh_test(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除嘉豪测试编号为"' + ids + '"的数据项？').then(function() {
        return delJh_test(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有嘉豪测试数据项？').then(() => {
        this.exportLoading = true;
        return exportJh_test(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>


<style>
  .el-input--suffix .el-input__inner{
    padding-right: 10px;
  }
  .el-form-item{
    margin-bottom: 10px;
  }
  .el-table th.el-table__cell.is-leaf, .el-table td.el-table__cell{
    border-bottom: none;
  }
  .el-divider--horizontal{
    margin: 10px 0;
  }
</style>
