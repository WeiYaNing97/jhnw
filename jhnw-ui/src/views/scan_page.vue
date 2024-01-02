<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :rules="rules" :inline="true"
             v-show="showSearch" label-width="40px" :show-message="false">

      <el-form-item style="margin-left: 15px;width: 100%">

        <el-button type="primary" @click="guangStart">光衰开始</el-button>
        <el-button type="primary" @click="guangEnd">光衰结束</el-button>
        <el-button type="primary" @click="errorStart">错误包开始</el-button>
        <el-button type="primary" @click="errotEnd">错误包结束</el-button>

<!--        2023.12.22-->
        <el-dropdown trigger="click" size="small" split-button type="primary" @command="addHandleCommand" @click="addScanIp">
          <i class="el-icon-plus"></i>  添加设备
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-d-arrow-right" command="bulkImport">批量导入设备</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

<!--        <el-button type="primary" @click="xinzeng" v-if="this.scanShow == true"-->
<!--                   icon="el-icon-plus" size="small">添加设备</el-button>-->

<!--        <el-button type="primary" icon="el-icon-d-arrow-right"-->
<!--                   size="small" v-if="this.scanShow == true" @click="dialogVisible = true">批量导入</el-button>-->
        <el-dropdown style="padding-left: 20px" trigger="click" size="small"
                     split-button type="success" v-show="this.scanButtonShow" @command="scanHandleCommand" @click="fullScan">
          <i class="el-icon-search"></i>  全面扫描
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-search" command="specialSearch">专项扫描</el-dropdown-item>
            <el-dropdown-item icon="el-icon-search" command="specialSearchAdv">高级功能扫描</el-dropdown-item>
            <el-dropdown-item icon="el-icon-search" command="modelScan">模板扫描</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

<!--        <el-button type="success" icon="el-icon-search" size="small" @click="fullScans"-->
<!--                   v-if="this.scanShow == true" :disabled="this.scanUse == false" round>全面扫描</el-button>-->

        <el-button type="warning" size="small" @click="cancelScan"
                   v-if="this.cancelShow == true" icon="el-icon-circle-close" round>取消扫描</el-button>

<!--        <el-button type="success" @click="specialSearch" v-if="this.scanShow == true"-->
<!--                   :disabled="this.scanUse == false" icon="el-icon-search" size="small" round>专项扫描</el-button>-->

<!--        <el-button type="success" @click="modelScan" v-if="this.scanShow == true"-->
<!--                   :disabled="this.scanUse == false" icon="el-icon-search" size="small" round>下发模板</el-button>-->

<!--        <el-button type="success" @click="specialSearchAdv" v-if="this.scanShow == true"-->
<!--                   :disabled="this.scanUse == false" icon="el-icon-search" size="small" round>高级功能</el-button>-->

        <el-button type="primary" icon="el-icon-refresh-left" v-if="this.rStartShow == true"
                   @click="rStart" size="small">返  回</el-button>

        <el-button type="warning" @click="repairAll" v-if="this.rStartShow == true"
                   icon="el-icon-search" size="small">一键修复</el-button>
<!--        <el-button type="success" v-if="this.rStartShow == true && this.allNormal == true"-->
<!--                   icon="el-icon-search" size="small">全部正常</el-button>-->
<!--        <el-button type="primary" @click="xinzeng" icon="el-icon-plus" size="small">新增设备</el-button>-->
<!--        <el-button type="primary" icon="el-icon-d-arrow-right"-->
<!--                   size="small" style="margin-left: 10px" @click="dialogVisible = true">批量导入</el-button>-->
<!--        批量导入-->
        <el-dialog
          title="交换机信息导入"
          :visible.sync="dialogVisible"
          width="50%">
          <input type="file" id="importBtn" @click="handleClick" @change="handleImport"
                 style="height: 30px;cursor: pointer">
<!--                    <span style="font-size: 12px">仅允许导入xls、xlsx格式文件</span>-->
          <br/>
          <span>请下载模板:</span>
          <el-button type="text" size="small" icon="el-icon-download"
                     @click="downloadTemp" style="margin-left: 10px">下载模板</el-button>
          <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="importIp">确 定</el-button>
          </span>
        </el-dialog>
<!--        扫描线程数-->
        <div style="display: inline-block;float: right;margin-right: 100px">
<!--          <el-select v-model="advcancedChoose" clearable placeholder="高级功能定义"-->
<!--                     @focus="advacedNull" @change="openAdvanced" style="padding-right: 10px">-->
<!--            <el-option label="OSPF" value="OSPF"></el-option>-->
<!--            <el-option label="光纤衰耗" value="光纤衰耗"></el-option>-->
<!--            <el-option label="误码率" value="误码率"></el-option>-->
<!--          </el-select>-->
          <el-button @click="avTest" type="primary" style="margin-right: 10px">高级配置</el-button>
          <p style="display: inline-block;margin: 0">扫描线程数:</p>
          <el-input-number size="small" style="width:100px" v-model="num" controls-position="right"
                           @change="handleChange" :min="1" :max="maxValue"></el-input-number>
        </div>
        <!--        <el-button type="primary" @click="testall" icon="el-icon-search" size="small">测试按钮</el-button>-->
      </el-form-item>

<!--      专项扫描、高级扫描-->
      <el-dialog
        title="扫描项目选择"
        :visible.sync="dialogVisibleSpecial"
        width="50%"
        :before-close="handleClose">
        <div style="overflow: auto;height: 340px">
          <el-input
            v-model="deptName"
            placeholder="请输入查找内容"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            style="margin-bottom: 20px"
          />
<!--          <el-scrollbar style="height:100%">-->
            <el-tree show-checkbox
                     :data="specialItems" :props="defaultProps" :filter-node-method="filterNode"
                     @node-click="handleNodeClick" :default-expand-all="true" ref="treeone" node-key="id"></el-tree>
<!--          </el-scrollbar>-->
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="specialSearchStart" v-show="!showButton">开始扫描专项</el-button>
          <el-button type="primary" @click="specialSearchStartAdv" v-show="showButton">开始扫描高级</el-button>
          <el-button @click="dialogVisibleSpecial = false">取 消</el-button>
<!--          <el-button type="primary" @click="dialogVisibleSpecial = false">确 定</el-button>-->
        </span>
      </el-dialog>

      <el-divider></el-divider>
<!--                扫描设备列表-->
      <div>
        <p style="margin: 0;text-align: center">扫描设备信息</p>
        <el-table :data="tableData" style="width: 100%"
                  max-height="300" ref="tableData"
                  :row-class-name="tableRowClassName" @row-click="currentLine" @selection-change="selectLine">
          <el-table-column type="index" :index="indexMethod" width="40"></el-table-column>
          <el-table-column type="selection" width="45"></el-table-column>
          <el-table-column prop="ip" label="设备IP">
            <template slot-scope="{ row }">
              <el-input v-if="row.isEdit" v-model="row.ip"
                        placeholder="请输入ip" size="small"></el-input>
              <span v-else>{{ row.ip }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="用户名">
            <template slot-scope="{ row }">
              <el-input v-if="row.isEdit" v-model="row.name"
                        placeholder="请输入用户名" size="small"></el-input>
              <span v-else>{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="password" label="密码">
            <template slot-scope="{ row }">
              <el-input v-if="row.isEdit" v-model="row.password" type="password"
                        placeholder="请输入密码" size="small"></el-input>
              <span v-else>{{ row.passmi }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="mode" label="连接方式">
            <template slot-scope="{ row }">
              <el-select v-if="row.isEdit" v-model="row.mode" size="small" placeholder="连接方式">
                <el-option label="ssh" value="ssh"/>
                <el-option label="telnet" value="telnet"/>
              </el-select>
              <span v-else>{{ row.mode }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="port" label="端口号">
            <template slot-scope="{ row }">
              <el-input v-if="row.isEdit" v-model="row.port"
                        placeholder="端口" size="small"></el-input>
              <span v-else>{{ row.port }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="configureCiphers" label="配置密码">
            <template slot-scope="{ row }">
              <el-input v-if="row.isEdit" v-model="row.configureCiphers" type="password"
                        placeholder="配置密码" size="small"></el-input>
              <span v-else>{{ row.conCip }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="{ row }">
              <el-button type="text" v-if="row.isEdit" @click.stop="queding(row)" size="small"></el-button>
              <el-button type="text" v-else @click.stop="queding(row)" size="small">编辑</el-button>
              <el-button @click.native.prevent="deleteRow(row.row_index)" type="text" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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
    </el-form>

    <el-divider></el-divider>
    <!--    <input url="file:///D:/HBuilderX-test/first-test/index.html" />-->

    <WebSocketTwo :queryParams="queryParams" ref="webtwo"
                  :endIp="endIp" v-show="this.webTwoShow" @allNoPro="postNoPro" :saowanend="saowanend" :xiufuend="xiufuend" :num="num"></WebSocketTwo>

    <div class="app-container home">
      <el-row :gutter="20">
        <WebSocket ref="webone" v-show="this.webTwoShow" @event="getendIp" @eventOne="postEnd"></WebSocket>
      </el-row>
    </div>
    <!-- 添加或修改测试对话框 -->
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

<!--    模板扫描-->
    <el-dialog title="模板选择" class="modelDia" :visible.sync="dialogFormVisibleOne">
      <el-form :model="formScan">
        <el-form-item label="选择模板" label-width="80px">
<!--          @focus="general($event)"-->
          <el-select v-model="formScan.model_name" placeholder="自定义模板"
                     @change="getListModel" name="model_name" style="width: 200px">
            <el-option v-for="(item,index) in genList"
                       :key="index" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" style="margin-top: 150px">
        <el-button type="primary" @click="modelScanStart">模板扫描</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
    // import { listJh_test, getJh_test, delJh_test, addJh_test, updateJh_test, exportJh_test } from "@/api/sql/jh_test"
    import WebSocket from '@/components/WebSocket/WebSocket'
    import WebSocketTwo from "@/components/WebSocketTwo/WebSocketTwo"
    import  {MessageBox} from "element-ui"
    import * as XLSX from 'xlsx'
    import router from '@/router/index'
    import request from '@/utils/request'
    import { JSEncrypt } from 'jsencrypt'
    export default {
        name: "Scan_page",
        components:{
            WebSocket,
            // WebSocketOne,
            WebSocketTwo
        },
        inject:["reload"],
        data() {
            return {
                //最终扫描设备
                finalScanIps:[],
                //
                advcancedChoose:'',
                //WebSocketTwo不显示
                webTwoShow:false,
                //模板扫描
                formScan:{
                    model_name:''
                },
                formworkId:'',
                dialogFormVisibleOne:false,
                genList:[],
                ////////
                allNormal:true,
                //扫描完成ip
                endIp:'',
                //是否圆圈
                loadingOne:true,
                //定时接收true或者false
                torf:false,
                torfOne:false,
                //是否扫描完成
                saowanend:false,
                //修复是否完成
                xiufuend:false,
                xiuendson:false,
                //查询树
                deptName:'',
                shuru:false,
                //设备数组
                tableData: [],
                //表格上传的设备信息
                importData:[],
                //选中设备
                chooseIp:[],
                //专项扫描tree数据
                specialItems: [],
                //高级扫描
                advancedScan:{
                    label:"高级功能",
                    children: [
                        {
                            label:"OSPF",
                            id:'OSPF'
                        },
                        {
                            label:"光衰",
                            id:'光衰'
                        },
                        {
                            label:"误码率",
                            id:'误码率'
                        }
                    ],
                },
                defaultProps: {
                    children: 'children',
                    label: 'label'
                },
                //计数器
                num:1,
                maxValue:20,
                dialogVisible:false,
                dialogVisibleXiu:false,
                dialogVisibleSpecial:false,
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
                //专项还是高级功能
                showButton:true,
                scanButtonShow:false,
                cancelShow:false,
                rStartShow:false,
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
            //根据输入筛选专项
            deptName(val) {
                this.$refs.treeone.filter(val);
            },
            //扫描结束
            saowanend(){
                if (this.saowanend == true){
                    console.log('扫描已结束!')
                    this.$alert('扫描已结束!', '提示', {
                        confirmButtonText: '确定'
                    })
                    this.rStartShow = true
                    this.cancelShow = false
                }
            },
            //监测是否已添加设备
            tableData(){
                console.log(this.tableData.length)
                if (this.tableData.length != 0){
                    this.scanButtonShow = true
                }else {
                    this.scanButtonShow = false
                }
                // this.$nextTick(() => {
                //     for (let i = 0; i < this.tableData.length; i++) {
                //         this.$refs.tableData.toggleRowSelection(
                //             this.tableData[i],
                //             true
                //         )
                //     }
                // })
            }
        },
        created() {
            // this.xunhuanxiufu()
            // let us = Cookies.get("userInfo")
            // console.log(us)
            // this.getList();
        },
        methods: {
            ////////////测试按钮⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇
            guangStart(){
                return request({
                    url:'/advanced/LightAttenuationSnapshot/startSnapshot',
                    method:'get'
                }).then(response=>{
                    console.log('日志')
                })
            },
            guangEnd(){
                return request({
                    url:'/advanced/LightAttenuationSnapshot/threadInterrupt',
                    method:'get'
                }).then(response=>{
                    console.log('日志')
                })
            },
            errorStart(){
                return request({
                    url:'/advanced/ErrorPackageSnapshot/startSnapshot',
                    method:'get'
                }).then(response=>{
                    console.log('日志')
                })
            },
            errotEnd(){
                return request({
                    url:'/advanced/ErrorPackageSnapshot/threadInterrupt',
                    method:'get'
                }).then(response=>{
                    console.log('日志')
                })
            },
            /////////////////////测试按钮⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆
            //
            avTest(){
                router.push({
                    path:'/advanced/ospf_command'
                })
            },
            //
            advacedNull(){
                console.log('点击了')
                this.advcancedChoose = ''
            },
            //
            openAdvanced(){
                if (this.advcancedChoose == 'OSPF'){
                    console.log('ospf')
                }else if(this.advcancedChoose == '误码率'){
                    console.log('误码率')
                }else if (this.advcancedChoose == '光纤衰耗'){
                    console.log('光纤衰耗')
                }
            },
            /////////////////
            indexMethod(index) {
                return index + 1;
            },
            rStart(){
                // this.$router.go(0)
                this.reload()
            },
            //一键修复
            repairAll(){
                this.$refs.webtwo.allxiu()
            },
            //全部正常
            postNoPro(data){
                this.allNormal = data
                console.log(this.allNormal)
            },
            //
            postEnd(data){
                console.log(data)
                this.saowanend = data
            },
            //接收扫描完成ip
            getendIp(data){
                this.endIp = data
                console.log(this.endIp)
            },
            // 筛选节点
            filterNode(value, data) {
                if (!value) return true;
                return data.label.indexOf(value) !== -1;
            },
            ///////////////
            //添加扫描IP
            addScanIp(){
                this.tableData.push({
                    // ip: '192.168.1.100',
                    name: 'admin',
                    password:'admin',
                    ip: '',
                    // name: '',
                    // password:'',
                    passmi:'********',
                    mode:'ssh',
                    port:'22',
                    isEdit:true,
                    conCip:'********',
                    configureCiphers:''
                })
            },
            //添加扫描IP下拉菜单操作：批量导入
            addHandleCommand(command){
                if (command === 'bulkImport'){
                    this.dialogVisible = true
                }
            },
            //扫描 下拉菜单操作
            scanHandleCommand(command){
                //WebSocketTwo显示
                this.webTwoShow = true
                if (command === 'specialSearch'){
                    this.specialSearch()
                }else if (command === 'specialSearchAdv'){
                    this.specialSearchAdv()
                }else if (command === 'modelScan'){
                    this.modelScan()
                }
            },
            /////////////////////扫描块
            //最终扫描设备
            finalScanMethod(){
                this.finalScanIps = []
                let selectScanIps = []
                if(this.chooseIp.length > 0){
                    console.log('扫描选中设备如下')
                    selectScanIps = JSON.parse(JSON.stringify(this.chooseIp))
                }else {
                    selectScanIps = JSON.parse(JSON.stringify(this.tableData))
                }
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey('MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLLvjNPfoEjbIUyGFcIFI25Aqhjgazq0dabk/w1DUiUiREmMLRbWY4lEukZjK04e2VWPvKjb1K6LWpKTMS0dOs5WbFZioYsgx+OHD/DV7L40PHLjDYkd4ZWV2EDlS8qcpx6DYw1eXr6nHYZS1e9EoEBWojDUcolzyBXU3r+LDjUQIDAQAB')
                for (let i = 0;i<selectScanIps.length;i++){
                    this.$delete(selectScanIps[i],'isEdit')
                    this.$delete(selectScanIps[i],'passmi')
                    this.$delete(selectScanIps[i],'conCip')
                    //给密码加密
                    let passMi = encrypt.encrypt(selectScanIps[i].password)
                    this.$set(selectScanIps[i],'password',passMi)
                    //给配置密码加密
                    let passConCip = encrypt.encrypt(selectScanIps[i].configureCiphers)
                    this.$set(selectScanIps[i],'configureCiphers',passConCip)
                }
                this.finalScanIps = selectScanIps.map(item => JSON.stringify(item))
                console.log('最终扫描设备')
                console.log(this.finalScanIps)
            },
            //全面扫描
            fullScan(){
                this.finalScanMethod()
                //WebSocketTwo显示
                this.webTwoShow = true
                // this.cancelShow = true
                //传输几个线程
                const scanNum = this.num
                return request({
                    url:'/sql/SwitchInteraction/multipleScans/'+scanNum,
                    method:'post',
                    data:this.finalScanIps
                }).then(response=>{
                    console.log('日志')
                })
            },
            //专项扫描获取tree
            specialSearch(){
                this.showButton = false
                this.dialogVisibleSpecial = true
                return request({
                    url:'/sql/total_question_table/fuzzyQueryListByPojoMybatis',
                    method:'get'
                }).then(response=>{
                    console.log('专项扫描分类修改前:')
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
                                let jointString = response[i].children[g].children[m].problemName+' '+'('+
                                    response[i].children[g].children[m].brand+' '+
                                    response[i].children[g].children[m].type+' '+
                                    response[i].children[g].children[m].firewareVersion+' '+
                                    response[i].children[g].children[m].subVersion+')'
                                this.$set(response[i].children[g].children[m],'problemName',jointString)
                            }
                        }
                    }
                    response = changeTreeDate(response,'typeProblem','label')
                    response = changeTreeDate(response,'temProName','label')
                    response = changeTreeDate(response,'problemName','label')
                    //删除后台传回的高级功能
                    if (response.length > 0){
                        this.specialItems = response.filter(item => {
                            return item.label != '高级功能'
                        })
                    }
                    //添加自定义的高级功能(高级功能传递 ：OSPF,误码率)
                    this.specialItems.push(this.advancedScan)
                    console.log('专项扫描分类修改后:')
                    console.log(this.specialItems)
                })
            },
            //专项开始扫描
            specialSearchStart(){
                this.finalScanMethod()
                //传输几个线程
                let scanNum = this.num
                //专项扫描选中的id
                let specialChooseIds = this.$refs.treeone.getCheckedKeys()
                let totalQuestionTableId = specialChooseIds.filter(function (item) {
                    return typeof item !== 'undefined'
                })
                console.log('专项扫描选择的问题ID:')
                console.log(totalQuestionTableId)
                if(totalQuestionTableId.length == 0){
                    this.$alert('没有选择要扫描的项,请选择!', '专项扫描', {
                        confirmButtonText: '确定',
                        type:'warning'
                    })
                }else {
                    this.dialogVisibleSpecial = false
                    return request({
                        url:'/sql/SwitchInteraction/directionalScann/'+totalQuestionTableId+'/'+scanNum,
                        method:'post',
                        data:this.finalScanIps
                    }).then(response=>{
                        // this.$message.success('扫描请求以提交!')
                    })
                }
            },
            //高级功能扫描获取tree
            specialSearchAdv(){
                this.showButton = true
                this.dialogVisibleSpecial = true
                this.specialItems = [
                    {
                        label:"高级功能",
                        children: [
                            {
                                label:"OSPF",
                                id:'OSPF'
                            },
                            {
                                label:"光衰",
                                id:'光衰'
                            },
                            {
                                label:"误码率",
                                id:'误码率'
                            }
                        ]
                    }
                ]
            },
            //高级功能开始扫描
            specialSearchStartAdv(){
                //扫描设备
                this.finalScanMethod()
                //传输几个线程
                const scanNum = this.num
                //高级扫描选中的id
                let specialChooseIds = this.$refs.treeone.getCheckedKeys()
                let totalQuestionTableId = specialChooseIds.filter(function (item) {
                    return typeof item !== 'undefined'
                })
                console.log('专项扫描选择的问题ID:')
                console.log(totalQuestionTableId)
                if(totalQuestionTableId.length == 0){
                    this.$alert('没有选择扫描项,请选择!', '高级扫描', {
                        confirmButtonText: '确定',
                        type:'warning'
                    });
                }else {
                    this.dialogVisibleSpecial = false
                    console.log('高级扫描开始')
                    return request({
                        url:'/advanced/AdvancedFeatures/advancedFunction/'+scanNum+'/'+totalQuestionTableId,
                        method:'post',
                        data:this.finalScanIps
                    }).then(response=>{
                        // this.$message.success('扫描请求以提交!')
                    })
                }
            },
            //高级功能开始扫描以前的备份
            specialSearchStartAdvSSSSS(){
                this.cancelShow = true
                //最终扫描设备
                let zuihou = []
                if (this.chooseIp.length>0){
                    zuihou = this.chooseIp
                }else {
                    zuihou = JSON.parse(JSON.stringify(this.tableData))
                }
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey('MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLLvjNPfoEjbIUyGFcIFI25Aqhjgazq0dabk/w1DUiUiREmMLRbWY4lEukZjK04e2VWPvKjb1K6LWpKTMS0dOs5WbFZioYsgx+OHD/DV7L40PHLjDYkd4ZWV2EDlS8qcpx6DYw1eXr6nHYZS1e9EoEBWojDUcolzyBXU3r+LDjUQIDAQAB')
                for (let i = 0;i<zuihou.length;i++){
                    this.$delete(zuihou[i],'isEdit')
                    this.$delete(zuihou[i],'passmi')
                    this.$delete(zuihou[i],'conCip')
                    //给用户密码加密
                    var pass = encrypt.encrypt(zuihou[i].password)
                    this.$set(zuihou[i],'password',pass)

                    var passPei = encrypt.encrypt(zuihou[i].configureCiphers)
                    this.$set(zuihou[i],'configureCiphers',passPei)
                }
                //传输几个线程
                const scanNum = this.num
                //专项扫描的所有id
                var zhuanid = this.$refs.treeone.getCheckedKeys()
                console.log(zhuanid)
                const functionNameT = []
                for(let i = 0;i<=zhuanid.length;i++){
                    if (typeof(zhuanid[i])!='undefined'){
                        functionNameT.push(zhuanid[i])
                    }
                }
                const functionName = functionNameT.filter(item=>{
                    return item != '高级功能'
                })
                console.log(functionName)
                let zuihouall = zuihou.map(x=>JSON.stringify(x))
                console.log(zuihouall)

                if(functionNameT.length == 0){
                    this.$alert('没有选择要扫描的项,请重新选择!', '高级扫描', {
                        confirmButtonText: '确定',
                        type:'warning'
                    });
                }else {
                    this.dialogVisibleSpecial = false
                    console.log('高级扫描')
                    return request({
                        url:'/advanced/AdvancedFeatures/advancedFunction/'+scanNum+'/'+functionName,
                        method:'post',
                        data:zuihouall
                    }).then(response=>{
                        // this.$message.success('扫描请求以提交!')
                    })
                }
            },
            //模板扫描
            modelScan(){
                this.dialogFormVisibleOne = true
                return request({
                    url:'/sql/formwork/getNameList',
                    method:'get',
                }).then(response=>{
                    console.log(response)
                    this.genList = response
                })
            },
            // //模板扫描获取下拉数据
            // general(e){
            //     // this.who = e.target.getAttribute('name')
            //     // return request({
            //     //     url:'/sql/formwork/getNameList',
            //     //     method:'get',
            //     // }).then(response=>{
            //     //     console.log(response)
            //     //     this.genList = response
            //     // })
            // },
            //获取选择的模板的ID
            getListModel(){
                return request({
                    url:'/sql/formwork/pojoByformworkName?formworkName=' + this.formScan.model_name,
                    method:'get',
                }).then(response=>{
                    this.formworkId = response.id
                })
            },
            //模板开始扫描
            modelScanStart(){
                if (this.formworkId == ''){
                    this.$message.warning('请选择模板!')
                }else {
                    this.dialogFormVisibleOne = false
                    // this.cancelShow = true
                    //最终扫描设备
                    this.finalScanMethod()
                    //传输几个线程
                    const scanNum = this.num
                    console.log(this.formworkId)
                    return request({
                        // url:'/sql/SwitchInteraction/formworkScann/' + this.formworkId + '/' + scanNum,
                        method:'post',
                        data:this.finalScanIps
                    }).then(response=>{

                    })
                }
            },
            ///////////////////////////扫描设备
            //选择设备变化时
            selectLine(row){
                this.chooseIp = row
                console.log(this.chooseIp)
            },
            //获取当前表格行index
            tableRowClassName({row, rowIndex}) {
                row.row_index = rowIndex;
            },
            //点击当前行操作
            currentLine(row){
                let lineNum = row.row_index
                this.tableData.forEach((row,index)=>{
                    if (lineNum != index){
                        row.isEdit = false
                    }
                })
            },
            //删除扫描设备
            deleteRow(index) {
                this.tableData.splice(index,1)
            },
            //确定
            queding(row){
                console.log(row)
                row.isEdit = !row.isEdit
            },
            //专项扫描
            handleNodeClick(specialItems) {

            },
            //定时获取是否修复结束
            cirxiufu(){
                this.xiufuend = this.$refs.webone.geifurepaired()
                console.log(this.xiufuend)
                if (this.xiufuend === true){
                    clearInterval(this.xiuendson)
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
            //计数器
            handleChange(value) {
                if (this.num > this.maxValue){
                    this.num = this.maxValue
                }
            },
            ///////////////////////导入模块
            //关闭导入弹窗
            handleClose(done) {
                this.dialogVisibleSpecial = false
            },
            //下载模板
            downloadTemp(){
                window.location.href = '/交换机信息模板.xlsx'
                this.dialogVisible = false
            },
            //批量导入
            handleClick() {
                let dom = document.getElementById("importBtn")
                if (dom) {
                    dom.value = ""
                }
            },
            handleImport(event) {
                this.importData = []
                let fileReader = new FileReader()
                let file = event.currentTarget.files[0]
                if (!file) {
                    return
                }
                // document.getElementById('#textone').innerHTML = document.getElementById('#fileinp').value
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
                            if(item.设备ip && item.用户名 && item.密码 && item.登录方式 && item.端口号){
                                let obj = {}
                                obj.ip = item["设备ip"]
                                obj.name = item["用户名"]
                                obj.password = item["密码"]
                                obj.mode = item["登录方式"]
                                obj.port = item["端口号"]

                                if (item["配置密码"] == undefined){
                                    obj.configureCiphers = ''
                                }else {
                                    obj.configureCiphers = item["配置密码"]
                                }
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
            //导入弹窗确定
            importIp(){
                console.log(this.importData)
                this.dialogVisible = false
                if (this.importData.length != 0){
                    for (let i = 0;i<this.importData.length;i++){
                        this.$set(this.importData[i],'passmi','********')
                        this.$set(this.importData[i],'conCip','********')
                        this.$set(this.importData[i],'isEdit',false)
                        this.tableData.push(this.importData[i])
                    }
                    this.$message.success('批量导入成功!')
                }else {
                    this.$message.warning('批量导入失败，请查看导入数据或者下载模板!')
                }
            },
            //取消扫描
            cancelScan(){
                MessageBox.confirm('确定取消扫描吗？','提示').then(c=>{
                    this.reload()
                }).catch(ee=>{
                    this.$message.warning('已取消扫描!')
                })
            },
            //全面扫描
            saomiao(){
                //最终扫描设备
                let zuihou = []
                if (this.chooseIp.length>0){
                    zuihou = this.chooseIp
                }else {
                    zuihou = JSON.parse(JSON.stringify(this.tableData))
                }
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey('MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLLvjNPfoEjbIUyGFcIFI25Aqhjgazq0dabk/w1DUiUiREmMLRbWY4lEukZjK04e2VWPvKjb1K6LWpKTMS0dOs5WbFZioYsgx+OHD/DV7L40PHLjDYkd4ZWV2EDlS8qcpx6DYw1eXr6nHYZS1e9EoEBWojDUcolzyBXU3r+LDjUQIDAQAB')
                for (let i = 0;i<zuihou.length;i++){
                    this.$delete(zuihou[i],'isEdit')
                    this.$delete(zuihou[i],'passmi')
                    this.$delete(zuihou[i],'conCip')
                    //给用户密码加密
                    var pass = encrypt.encrypt(zuihou[i].password)
                    this.$set(zuihou[i],'password',pass)

                    var passPei = encrypt.encrypt(zuihou[i].configureCiphers)
                    this.$set(zuihou[i],'configureCiphers',passPei)
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
                let zuihouall = zuihou.map(x=>JSON.stringify(x))
                console.log(zuihouall)
                if(totalQuestionTableId.length == 0){
                    console.log('全部扫描')
                    this.$message.success('扫描请求以提交!')
                    return request({
                        url:'/sql/SwitchInteraction/multipleScans/'+scanNum,
                        method:'post',
                        data:zuihouall
                    }).then(response=>{
                        console.log('日志')
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


<style scoped>
  >>> .el-dialog{
    height: 500px;
  }
  >>> .el-form-item__content{
    width: 100% !important;
  }
  .el-input--suffix .el-input__inner{
    padding-right: 10px;
  }
  .el-form-item{
    margin-bottom: 10px;
  }
  .el-divider--horizontal{
    margin: 10px 0;
  }
  >>> .el-dialog__body{
    padding: 20px 20px;
  }
  .el-loading-spinner{
    margin-top: -15px;
    height: 30px;
  }
  /*label{*/
  /*  position: relative;*/
  /*}*/
  /*#fileinp{*/
  /*  position: absolute;*/
  /*  left: 0;*/
  /*  top: 0;*/
  /*  opacity: 0;*/
  /*}*/
  >>> .el-dialog__wrapper.modelDia>.el-dialog{
    width: 40%;
    height: 360px;
  }
  .el-dropdown-menu{
    left:220px;
  }
</style>
