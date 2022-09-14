<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :rules="rules" :inline="true" v-show="showSearch" label-width="40px" :show-message="false">
      <el-form-item style="margin-left: 15px">
        <el-button type="primary" icon="el-icon-search" size="small" @click="saomiao">一键扫描</el-button>
        <el-button type="primary" @click="xinzeng" icon="el-icon-plus" size="small">新增设备</el-button>
        <el-button type="primary" icon="el-icon-d-arrow-right"
                   size="small" style="margin-left: 10px" @click="dialogVisible = true">批量导入</el-button>
        <el-button type="primary" @click="zhuanall" icon="el-icon-search" size="small">专项扫描</el-button>
        <el-dialog
          title="交换机信息导入"
          :visible.sync="dialogVisible"
          width="50%"
          :before-close="handleClose">
          <input type="file" id="importBtn" @click="handleClick" @change="handleImport"
                 style="height: 30px;cursor: pointer">
          <br/>
          <span style="font-size: 12px">仅允许导入xls、xlsx格式文件</span>
          <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="daorusure">确 定</el-button>
          </span>
        </el-dialog>
        <el-button type="primary" size="small" icon="el-icon-download"
                   @click="xiazai" style="margin-left: 10px">下载模板</el-button>
        <el-input-number size="small" style="margin-left: 10px" v-model="num" controls-position="right"
                         @change="handleChange" :min="1" :max="5"></el-input-number>
<!--        <el-button type="primary" @click="testall" icon="el-icon-search" size="small">测试按钮</el-button>-->
      </el-form-item>
      <el-divider></el-divider>
      <el-row>
        <el-col :span="13">
          <!--      表格展示列表-->
          <p style="margin: 0;text-align: center">扫描设备信息</p>
          <el-table :data="tableData" style="width: 100%"
                    max-height="300" @select="xuanze">
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column type="selection" width="45"></el-table-column>
            <el-table-column prop="ip" label="设备IP" width="130">
              <template slot-scope="{ row }">
                <el-input v-if="row.$isEdit" v-model="row.ip"
                          placeholder="请输入ip" size="small" style="width: 120px"></el-input>
                <span v-else>{{ row.ip }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="用户名" width="130">
              <template slot-scope="{ row }">
                <el-input v-if="row.$isEdit" v-model="row.name"
                          placeholder="请输入用户名" size="small" style="width: 120px"></el-input>
                <span v-else>{{ row.name }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="password" label="密码" width="80">
              <template slot-scope="{ row }">
                <el-input v-if="row.$isEdit" v-model="row.password" type="password"
                          placeholder="请输入密码" size="small" style="width: 110px"></el-input>
                <span v-else>{{ row.passmi }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="mode" label="连接方式" width="80">
              <template slot-scope="{ row }">
                <el-input v-if="row.$isEdit" v-model="row.mode"
                          placeholder="连接方式" size="small" style="width: 70px"></el-input>
                <span v-else>{{ row.mode }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="port" label="端口号" width="80">
              <template slot-scope="{ row }">
                <el-input v-if="row.$isEdit" v-model="row.port"
                          placeholder="端口" size="small" style="width: 50px"></el-input>
                <span v-else>{{ row.port }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="{ row }">
                <el-button type="text" v-if="row.$isEdit" @click.stop="queding(row)" size="small">确定</el-button>
                <el-button type="text" v-else @click.stop="queding(row)" size="small">编辑</el-button>
                <el-button @click.native.prevent="deleteRow(row.$index, tableData)" type="text" size="small">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
        <el-col :span="11" v-show="showxiang">
          <p style="margin: 0;text-align: center">扫描项目选择</p>
          <div>
            <el-input
              v-model="deptName"
              placeholder="请输入查找内容"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px;width: 80%;margin-left: 50px"
            />
          </div>
          <div style="max-height: 300px">
            <el-scrollbar style="height:100%">
              <el-tree style="max-height: 300px;width: 95%;margin-left:50px" show-checkbox
                       :data="fenxiang" :props="defaultProps" :filter-node-method="filterNode"
                       @node-click="handleNodeClick" ref="treeone" node-key="id"></el-tree>
            </el-scrollbar>
          </div>
        </el-col>
      </el-row>

      <el-dialog
        title="提示"
        :visible.sync="dialogVisibleXiu"
        width="30%"
        :before-close="handleClose">
        <span>修复已结束!</span>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="lishiend">确 定</el-button>
        </span>
      </el-dialog>

<!--   @keyup.enter.native="handleQuery"-- 回车事件 >
<el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
    </el-form>

    <el-divider></el-divider>
<!--    <input url="file:///D:/HBuilderX-test/first-test/index.html" />-->

    <WebSocketTwo :queryParams="queryParams" ref="webtwo" :saowanend="saowanend" :xiufuend="xiufuend" :num="num"></WebSocketTwo>

    <div class="app-container home">
      <el-row :gutter="20">
        <WebSocket ref="webone"></WebSocket>
      </el-row>
    </div>

    <WebSocketOne></WebSocketOne>

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
import log from "../../monitor/job/log"
import * as XLSX from 'xlsx'
import request from '@/utils/request'
import { JSEncrypt } from 'jsencrypt'
export default {
  name: "Jh_test",
    components:{
        WebSocket,
        WebSocketOne,
        WebSocketTwo
    },
  data() {
    return {
        //扫描项目选择是否显示
        showxiang:true,
        //定时接收true或者false
        torf:false,
        //是否扫描完成
        saowanend:false,
        //修复是否完成
        xiufuend:false,
        xiuendson:false,
        //查询树
        deptName:undefined,
        shuru:false,
        tableData: [{
            ip: '',
            name: '',
            password:'',
            passmi:'********',
            mode:'ssh',
            port:22,
            $isEdit:false
        }],
        xuanzhong:[

        ],
        fenxiang: [

        ],
        defaultProps: {
            children: 'children',
            label: 'label'
        },
        //计数器
        num:1,
        //表格上传的设备信息
        importData:[],
        dialogVisible:false,
        dialogVisibleXiu:false,
        //多台隐身
        duoShow:true,
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
        //动态增加交换机
        // forms:{
        //     dynamicItem:[
        //         {
        //             ip: '192.168.1.100',
        //             name: 'admin',
        //             password:'admin',
        //             mode:'ssh',
        //             port:22
        //         }
        //     ],
        //     rules:{
        //         ip:[
        //             { required: true, trigger: "blur",message:null }
        //         ]
        //     }
        // },
        // 查询参数
      queryParams: {
          ip: '192.168.1.1',
          name: 'admin',
          password: 'admin',
          mode: 'telnet',
          port: 23,
      },
        //新添加的
        textarea:'',
        // switchIp typeProblem problemName ifQuestion problemId
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
    watch:{
        // 根据输入筛选专项
        deptName(val) {
            this.$refs.treeone.filter(val);
        },
    },
  created() {
      this.xunhuanxiufu()
      // let us = Cookies.get("userInfo")
      // console.log(us)
      this.getList();
  },
  methods: {
      // 筛选节点
      filterNode(value, data) {
          if (!value) return true;
          return data.label.indexOf(value) !== -1;
      },
      //专项所有
      zhuanall(){
          this.showxiang = true
          var ce = {}
          return request({
              url:'/sql/total_question_table/fuzzyQueryListByPojoMybatis',
              method:'post',
              data:ce
          }).then(response=>{
              console.log(response)
              function changeTreeDate(arrayJsonObj,oldKey,newKey) {
                  let strtest = JSON.stringify(arrayJsonObj);
                  let reg = new RegExp(oldKey,'g');
                  let newStr = strtest.replace(reg,newKey);
                  return JSON.parse(newStr);
              }
              response = changeTreeDate(response,'totalQuestionTableVOList','children')
              response = changeTreeDate(response,'totalQuestionTableList','children')
              for (let i = 0;i<response.length;i++){
                  for (let g = 0;g<response[i].children.length;g++){
                      this.$delete(response[i].children[g],'typeProblem')
                      for (let m = 0;m<response[i].children[g].children.length;m++){
                          this.$delete(response[i].children[g].children[m],'typeProblem')
                          this.$delete(response[i].children[g].children[m],'temProName')
                          let pinjie = response[i].children[g].children[m].problemName+' '+'('+
                              response[i].children[g].children[m].brand+' '+
                              response[i].children[g].children[m].type+' '+
                              response[i].children[g].children[m].firewareVersion+' '+
                              response[i].children[g].children[m].subVersion+')'
                          this.$set(response[i].children[g].children[m],'problemName',pinjie)
                      }
                  }
              }
              response = changeTreeDate(response,'typeProblem','label')
              response = changeTreeDate(response,'temProName','label')
              response = changeTreeDate(response,'problemName','label')
              this.fenxiang = response
              console.log(this.fenxiang)
          })
      },
      //选择设备
      xuanze(row){
          this.xuanzhong = JSON.parse(JSON.stringify(row))
      },
      //新增设备
      xinzeng(){
          this.tableData.push({
              ip: '',
              name: '',
              password:'',
              passmi:'********',
              mode:'ssh',
              port:22,
              $isEdit:true
          })
      },
      //删除扫描设备
      deleteRow(index, rows) {
          rows.splice(index, 1);
      },
      //确定
      queding(row){
          console.log(row)
          row.$isEdit = !row.$isEdit
      },
      //专项扫描
      handleNodeClick(fenxiang) {

      },
      //扫描完成弹窗,后面定时执行
      saowan(){
          this.saowanend = this.$refs.webone.geifuone()
          if (this.saowanend === true){
              clearInterval(this.torf)
              alert('扫描已结束!')
          }
      },
      //定时获取是否修复结束
      cirxiufu(){
          this.xiufuend = this.$refs.webone.geifurepaired()
          console.log(this.xiufuend)
          if (this.xiufuend === true){
              clearInterval(this.xiuendson)
              // alert('修复已结束!')
              this.dialogVisibleXiu = true
          }
      },
      //修复结束确定按钮
      lishiend(){
          const lishixiuend = this.$refs.webtwo.geifu()
          if(lishixiuend === true){
              this.$refs.webtwo.lishi()
          }
          this.dialogVisibleXiu = false
      },
      xunhuanxiufu(){
          this.xiuendson = setInterval(this.cirxiufu,5000)
      },
      //测试
      testall(){
          // console.log(this.$refs.webtwo.geifu())
          // console.log(this.$refs.treeone.getCheckedKeys())
      },
      //导入弹窗确定
      daorusure(){
          console.log(this.importData)
          this.dialogVisible = false
          if (this.importData.length != 0){
              for (let i = 0;i<this.importData.length;i++){
                  this.$set(this.importData[i],'passmi','********')
                  this.$set(this.importData[i],'$isEdit',false)
                  if (this.tableData[0].ip === '' && this.tableData[0].name === '' && this.tableData[0].password === ''){
                      this.$delete(this.tableData,0)
                  }
                  this.tableData.push(this.importData[i])
              }
              this.$message.success('批量导入成功!')
          }else {
              this.$message.warning('批量导入失败，请查看导入数据或者下载模板!')
          }
      },
      //计数器
      handleChange(value) {
          console.log(value);
      },
      //关闭导入弹窗
      handleClose(done) {
          this.$confirm('确认关闭？')
              .then(_ => {
                  done();
              })
              .catch(_ => {});
      },
      //下载模板
      xiazai(){
          window.location.href = '/交换机信息模板.xlsx'
      },
      //批量导入
      handleClick() {
          let dom = document.getElementById("importBtn")
          if (dom) {
              dom.value = ""
          }
      },
      handleImport(event) {
          let fileReader = new FileReader()
          var file = event.currentTarget.files[0]
          if (!file) {
              return
          }
          // 成功回调函数
          fileReader.onload = async (ev) => {
              try {
                  let datas = ev.target.result
                  let workbook = XLSX.read(datas, {
                      type: "binary"
                  });
                  // excelData为excel读取出的数据,可以用来制定校验条件,如数据长度等
                  let excelData = XLSX.utils.sheet_to_json(
                      workbook.Sheets[workbook.SheetNames[0]]
                  );
                  // 将上面数据转换成需要的数据
                  let arr = []
                  //item[]中的内容为Excel中数据的表头,上传的数据表头必须根据标题填写,否则无法读取
                  excelData.forEach((item,index) => {
                      if(item.ip && item.用户名 && item.密码 && item.登录方式 && item.端口号){
                          let obj = {}
                          obj.ip= item["ip"]
                          obj.name= item["用户名"]
                          obj.password= item["密码"]
                          obj.mode=item["登录方式"]
                          obj.port=item["端口号"]
                          arr.push(obj)
                      }
                  });
                  this.importData = [...arr]
                  if (this.importData.length === 0){
                      this.$message.warning('批量导入失败，请重新导入或者下载模板!')
                  }
                  // this.importData则为最终获取到的数据
                  console.log(this.importData)
              } catch (e) {
                  window.alert("文件类型不正确!请下载模板!")
                  return false
              }
          };
          // 读取文件 成功后执行上面的回调函数
          fileReader.readAsBinaryString(file)
      },
      //一键扫描
      saomiao(){
          //定时获取是否扫描结束
           this.torf = setInterval(this.saowan,3000)
          //最终扫描设备
          let zuihou = []
          if (this.xuanzhong.length>0){
              zuihou = this.xuanzhong
          }else {
              zuihou = JSON.parse(JSON.stringify(this.tableData))
          }
          var encrypt = new JSEncrypt();
          encrypt.setPublicKey('MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLLvjNPfoEjbIUyGFcIFI25Aqhjgazq0dabk/w1DUiUiREmMLRbWY4lEukZjK04e2VWPvKjb1K6LWpKTMS0dOs5WbFZioYsgx+OHD/DV7L40PHLjDYkd4ZWV2EDlS8qcpx6DYw1eXr6nHYZS1e9EoEBWojDUcolzyBXU3r+LDjUQIDAQAB')
          for (let i = 0;i<zuihou.length;i++){
              this.$delete(zuihou[i],'$isEdit')
              this.$delete(zuihou[i],'passmi')
              //给用户密码加密
              var pass = encrypt.encrypt(zuihou[i].password)
              this.$set(zuihou[i],'password',pass)
          }
          //传输几个线程
          const scanNum = this.num
          //专项扫描的所有id
          var zhuanid = this.$refs.treeone.getCheckedKeys()
          const totalQuestionTableId = []
          for(let i = 0;i<=zhuanid.length;i++){
              if (typeof(zhuanid[i])!='undefined'){
                  totalQuestionTableId.push(zhuanid[i])
              }
          }
          console.log(totalQuestionTableId)
          console.log(zuihou)
          let zuihouall = zuihou.map(x=>JSON.stringify(x))
          console.log(zuihouall)
          if(totalQuestionTableId.length == 0){
              console.log('全部扫描')
              return request({
                  url:'/sql/SwitchInteraction/multipleScans/'+scanNum,
                  method:'post',
                  data:zuihouall
              }).then(response=>{
                  this.$message.success('扫描请求以提交!')
              })
          }else {
              console.log('专项扫描')
              return request({
                  url:'/sql/SwitchInteraction/directionalScann/'+totalQuestionTableId+'/'+scanNum,
                  method:'post',
                  data:zuihouall
              }).then(response=>{
                  this.$message.success('扫描请求以提交!')
              })
          }
      },
      // //新增表单项
      // addItem(length) {
      //     if (length >= 100) {
      //         this.$message({
      //             type: 'warning',
      //             message: '最多可存在5行!'
      //         })
      //     } else {
      //         this.forms.dynamicItem.push({
      //             ip: '',
      //             name: '',
      //             password:'',
      //             mode:'',
      //             port:''
      //         })
      //     }
      // },
      // //删减表单项
      // deleteItem(item, index) {
      //     this.forms.dynamicItem.splice(index, 1)
      // },
      //新增指定端口号
      chooseTT(item){
          var val = item.mode;
          if (val == 'telnet'){
              item.port=23;
          }else if (val == 'ssh'){
              item.port=22;
          }else {
              item.port=22;
          }
      },
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
