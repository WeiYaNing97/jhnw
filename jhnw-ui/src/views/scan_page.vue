<template>
  <div class="app-container">

    <div>
      <el-button type="primary" @click="startSnapshot">快照开始</el-button>
      <el-button type="primary" @click="closeSnapshot">快照竣工</el-button>
      <el-button type="primary" @click="analysis">解析</el-button>
      <el-dialog
        title=" 快照竣工 "
        :visible.sync="completionWindow"><!-- width="50%" -->
        <h6>是否删除快照扫描数据</h6>
        <el-button type="primary" @click="deleteData">删除快照扫描数据</el-button>
        <!--<el-button type="primary" @click="preserveData">保留快照扫描数据</el-button>-->
      </el-dialog>

    </div>

    <el-form :model="queryParams" ref="queryForm" :rules="rules" :inline="true"
             v-show="showSearch" label-width="40px" :show-message="false">

      <el-form-item style="margin-left: 15px;width: 100%">

<!--        2023.12.22-->

        <el-dropdown trigger="click" size="small" v-show="this.addButtonShow"
                     split-button type="primary" @command="addHandleCommand" @click="addScanIp">
          <i class="el-icon-plus"></i>  添加设备
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-d-arrow-right" command="bulkImport">批量导入设备</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

<!--        <el-button type="primary" @click="changeIpPass" size="small" style="margin-left: 20px">修改ip密码</el-button>-->

        <el-dropdown style="padding-left: 20px" trigger="click" size="small"
                     split-button type="success" v-show="this.scanButtonShow" @command="scanHandleCommand" @click="fullScan">
          <i class="el-icon-search"></i>  全面扫描
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-search" command="specialSearch">专项扫描</el-dropdown-item>
            <el-dropdown-item icon="el-icon-search" command="specialSearchAdv">运行分析扫描</el-dropdown-item>
            <el-dropdown-item icon="el-icon-search" command="modelScan">模板扫描</el-dropdown-item>
            <el-dropdown-item icon="el-icon-edit" command="changeIpPass">修改IP密码</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

<!--        <el-button type="success" icon="el-icon-search" size="small" @click="fullScans"-->
<!--                   v-if="this.scanShow == true" :disabled="this.scanUse == false" round>全面扫描</el-button>-->

        <el-button type="warning" size="small" @click="cancelScan" v-show="this.cancelScanShow"
                   icon="el-icon-circle-close" round>取消扫描</el-button>

<!--        <el-button type="success" @click="specialSearch" v-if="this.scanShow == true"-->
<!--                   :disabled="this.scanUse == false" icon="el-icon-search" size="small" round>专项扫描</el-button>-->

<!--        <el-button type="success" @click="modelScan" v-if="this.scanShow == true"-->
<!--                   :disabled="this.scanUse == false" icon="el-icon-search" size="small" round>下发模板</el-button>-->

<!--        <el-button type="success" @click="specialSearchAdv" v-if="this.scanShow == true"-->
<!--                   :disabled="this.scanUse == false" icon="el-icon-search" size="small" round>高级功能</el-button>-->

        <el-button type="primary" icon="el-icon-refresh-left" v-if="this.rStartShow == true"
                   @click="rStart" size="small">返  回</el-button>

<!--        <el-button type="warning" @click="repairAll" v-if="this.rStartShow == true"-->
<!--                   icon="el-icon-search" size="small">一键修复</el-button>-->
<!--        <el-button type="success" v-if="this.rStartShow == true && this.allNormal == true"-->
<!--                   icon="el-icon-search" size="small">全部正常</el-button>-->
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
<!--            <el-option label="错误包" value="错误包"></el-option>-->
<!--          </el-select>-->
          <el-button @click="avTest" type="primary" style="margin-right: 10px">高级配置</el-button>
          <p style="display: inline-block;margin: 0">扫描线程数:</p>
          <el-input-number size="small" style="width:100px" v-model="num" controls-position="right"
                           @change="handleChange" :min="1" :max="maxValue"></el-input-number>
        </div>
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
          <el-button type="primary" @click="specialSearchStart" v-show="!showButton">专项扫描</el-button>

          <el-button type="primary" @click="specialSearchStartAdv" v-show="showButton">高级扫描</el-button>
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
                  :row-class-name="tableRowClassName" @row-click="currentLine" @selection-change="selectLine" >
          <el-table-column type="index" :index="indexMethod" width="40"></el-table-column>
          <el-table-column type="selection" width="45"></el-table-column>

          <el-table-column prop="ip" label="设备IP">
            <template slot-scope="{ row }">
              <!-- v-if="row.ipIsEdit" -->
              <el-input v-if="row.ipIsEdit" v-model="row.ip" @blur="handleBlur(row)" placeholder="请输入ip" size="small" :style="{ color: row.iPIsExist ? '' : 'red' }"/><!-- :style="{ color: row.iPIsExist ? '' : 'red' }"  -->
              <!-- @mouseenter="queding(row)" -->
              <!-- iPIsExist 没有查询到信息 校验IP地址合法性 -->
              <span v-else :style="{ color: row.iPIsExist ? '' : 'red' }" @click = "obtain(row)">{{ row.ip }}</span><!-- @mouseenter="queding(row)" -->
            </template>
          </el-table-column>
          <el-table-column prop="name" label="用户名">
            <template slot-scope="{ row }">
              <!-- @blur="nameVerification(tableData)" @mouseenter="queding(row),blank(row)" v-on:click="blank(row)"-->
              <el-input v-if="row.isEdit" v-model="row.name" placeholder="请输入用户名" size="small" :style="{ color: row.isExist ? '' : 'red' }"/><!-- :style="{ color: row.isExist ? '' : 'red' }" -->
              <!-- isExist 是否查询到交换机用户信息-->
              <!-- @mouseenter="queding(row),blank(row)" v-on:click="blank(row)" -->
              <span v-else :style="{ color: row.isExist ? '' : 'red' }" @row-click="blank(row)" >{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="password" label="密码">
            <template slot-scope="{ row }">
              <!-- @mouseenter="queding(row)"  @blur="nameVerification(tableData)" -->
              <el-input v-if="row.isEdit" v-model="row.password" type="password" placeholder="请输入密码" size="small" />
              <!-- @mouseenter="queding(row)" -->
              <span v-else >{{ row.passmi }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="mode" label="连接方式">
            <template slot-scope="{ row }">
              <!-- @mouseenter="queding(row)"  @blur="nameVerification(tableData)" -->
              <el-select v-if="row.isEdit" v-model="row.mode" size="small" placeholder="连接方式">
                <el-option label="ssh" value="ssh"/>
                <el-option label="telnet" value="telnet"/>
              </el-select>
              <!-- @mouseenter="queding(row)" -->
              <span v-else>{{ row.mode }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="port" label="端口号">
            <template slot-scope="{ row }">
              <!--  @mouseenter="queding(row)"  @blur="nameVerification(tableData)"  -->
              <el-input v-if="row.isEdit" v-model="row.port" placeholder="端口" size="small" />
              <!-- @mouseenter="queding(row)" -->
              <span v-else>{{ row.port }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="configureCiphers" label="配置密码">
            <template slot-scope="{ row }">
              <!-- @mouseenter="queding(row)"  @blur="nameVerification(tableData)" -->
              <el-input v-if="row.isEdit" v-model="row.configureCiphers" type="password" placeholder="配置密码" size="small" />
              <!--  @mouseenter="queding(row)"  -->
              <span v-else>{{ row.conCip }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="{ row }">
              <!--<el-button type="text" v-if="row.isEdit" @click.stop="queding(row)" size="small"></el-button>
              <el-button type="text" v-else @click.stop="queding(row)" size="small">编辑</el-button>-->
              <el-button @click.native.prevent="deleteRow(row.row_index)" type="text" size="small">删除</el-button>
            </template>
          </el-table-column>

        </el-table>
      </div>
    </el-form>

    <el-divider></el-divider>
    <!--    <input url="file:///D:/HBuilderX-test/first-test/index.html" />-->

<!--    <WebSocketTwo :queryParams="queryParams" ref="webtwo"-->
<!--                  :endIp="endIp" v-show="this.webTwoShow" @allNoPro="postNoPro"-->
<!--                  :saowanend="saowanend" :xiufuend="xiufuend" :num="num"></WebSocketTwo>-->
    <WebSocketTwo :queryParams="queryParams" ref="webtwo"
                  :endIp="endIp" :num="num"
                  :oneClickShow="oneClickShow"></WebSocketTwo>

    <div class="app-container home">
      <el-row :gutter="20">
<!--        <WebSocket ref="webone" @event="getendIp" @eventOne="postEnd"></WebSocket>-->
        <WebSocket ref="webone" @event="getendIp"></WebSocket>
<!--        v-show="this.webTwoShow"-->
      </el-row>
    </div>
<!--    模板扫描-->
    <el-dialog title="模板选择" class="modelDia" :visible.sync="dialogFormVisibleOne">
      <el-form :model="formScan">
        <el-form-item label="选择模板" label-width="80px">
<!--          @focus="general($event)"-->
          <el-select v-model="formScan.model_name" placeholder="选择模板"
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
            WebSocketTwo
        },
        inject:["reload"],
        data() {
            return {

                //竣工弹窗
                completionWindow: false,
                //竣工路径
                completionUrl: '',
                ifDeleteData: false,

                bangding:'',
                //最终扫描设备
                finalScanIps:[],
                //高级功能选择项
                advcancedChoose:'',
                //WebSocketTwo不显示
                webTwoShow:false,
                //取消扫描不显示
                cancelScanShow:false,
                //扫描按钮是否显示
                scanButtonShow:false,
                //添加设备按钮是否显示
                addButtonShow:true,
                //专项还是高级功能
                showButton:true,
                //模板扫描
                formScan:{
                    model_name:''
                },
                //父控制一键修复是否显示
                oneClickShow:false,
                //扫描结束
                scanFinish:false,
                //获取选择的模板ID
                formworkId:'',
                dialogFormVisibleOne:false,
                genList:[],
                //扫描完成ip
                endIp:'',
                //是否圆圈
                loadingOne:true,
                //查询树
                deptName:'',
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
                    label:"运行分析",
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
                            label:"错误包",
                            id:'错误包'
                        },
                        {
                            label:"路由聚合",
                            id:'路由聚合'
                        },
                      {
                        label:"链路捆绑",
                        id:'链路捆绑'
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
                dialogVisibleSpecial:false,
                // 遮罩层
                loading: false,
                // 导出遮罩层
                exportLoading: false,
                // 选中数组
                ids: [],
                // 显示搜索条件
                showSearch: true,
                cancelShow:false,
                //返回按钮显示
                rStartShow:false,
                // 总条数
                total: 0,
                // 弹出层标题
                title: "",
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
            },
            //监听扫描是否结束
            scanFinish(){
                this.$alert('扫描已结束!', '提示', {
                    confirmButtonText: '确定'
                })
                this.rStartShow = true
                this.cancelShow = false
                this.cancelScanShow = false
                this.oneClickShow = true
            }
        },
        created() {
            // let us = Cookies.get("userInfo")
            // console.log(us)
        },
        methods: {
            //无用代码⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇
            //
            advacedNull(){
                console.log('点击了')
                this.advcancedChoose = ''
            },
            //
            openAdvanced(){
                if (this.advcancedChoose == 'OSPF'){
                    console.log('ospf')
                }else if(this.advcancedChoose == '错误包'){
                    console.log('错误包')
                }else if (this.advcancedChoose == '光纤衰耗'){
                    console.log('光纤衰耗')
                }
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
            //无用代码⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆
            ////////////测试按钮⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇
            guangStart(){
                return request({
                    url:'/advanced/LightAttenuationSnapshot/startSnapshot',
                    method:'post'
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
                    method:'post'
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
            //高级配置跳转
            avTest(){
                router.push({
                    path:'/advanced/ospf_command'
                })
            },
            /////////////////
            rStart(){
                this.reload()
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
                    /*ip: '192.168.1.100',
                    name: 'admin',
                    password:'admin',*/
                    ip: '',
                    name: '',
                    password:'',

                    passmi:'********',
                    mode:'ssh',
                    port:'22',

                    /* 普通输入框是否渲染显示*/
                    isEdit:true,
                    /* ip输入框是否渲染显示*/
                    ipIsEdit:true,

                    /*是否异常字体颜色显示*/
                    isExist: true,
                    iPIsExist:true,

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
                // this.webTwoShow = true
                if (command === 'specialSearch'){
                    this.specialSearch()
                }else if (command === 'specialSearchAdv'){
                    this.specialSearchAdv()
                }else if (command === 'modelScan'){
                    this.modelScan()
                }else if (command === 'changeIpPass'){
                    this.changeIpPass()
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
                //扫描设备
                this.finalScanMethod()
                //WebSocketTwo显示
                this.webTwoShow = true
                this.cancelScanShow = true
                this.addButtonShow = false
                this.scanButtonShow = false
                // this.cancelShow = true
                //传输几个线程
                const scanNum = this.num
                return request({
                    url:'/sql/SwitchInteraction/multipleScans/'+scanNum,
                    method:'post',
                    data:this.finalScanIps
                }).then(response=>{
                    console.log(response)
                    if (response == '扫描结束'){
                        this.scanFinish = true
                    }
                })
            },
            //修改ip的密码
            changeIpPass(){
                //扫描设备
                const extractedValues = this.tableData.map(({ ip, name, password }) => ({ ip, name, password }))
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey('MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLLvjNPfoEjbIUyGFcIFI25Aqhjgazq0dabk/w1DUiUiREmMLRbWY4lEukZjK04e2VWPvKjb1K6LWpKTMS0dOs5WbFZioYsgx+OHD/DV7L40PHLjDYkd4ZWV2EDlS8qcpx6DYw1eXr6nHYZS1e9EoEBWojDUcolzyBXU3r+LDjUQIDAQAB')
                for (let i = 0;i<extractedValues.length;i++){
                    //给密码加密
                    let passMi = encrypt.encrypt(extractedValues[i].password)
                    this.$set(extractedValues[i],'password',passMi)
                }
                this.finalScanIps = extractedValues.map(item => JSON.stringify(item))
                console.log(this.finalScanIps)
                return request({
                    url:'/share/switch_scan_result/updateLoginInformation',
                    method:'put',
                    data:this.finalScanIps
                }).then(response=>{
                    console.log('密码修改成功')
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
                    //添加自定义的高级功能(高级功能传递 ：OSPF,错误包)
                    this.specialItems.push(this.advancedScan)
                    console.log('专项扫描分类修改后:')
                    console.log(this.specialItems)
                })
            },

            //专项开始扫描
            specialSearchStart(){
                //WebSocketTwo显示
                this.webTwoShow = true
                this.cancelScanShow = true
                this.addButtonShow = false
                this.scanButtonShow = false
                //扫描设备
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
                        console.log(response)
                        if (response == '扫描结束'){
                            this.scanFinish = true
                        }
                    })
                }
            },

            //高级功能扫描获取tree
            specialSearchAdv(){
                this.showButton = true
                this.dialogVisibleSpecial = true
                this.specialItems = [
                    {
                        label:"运行分析",
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
                                label:"错误包",
                                id:'错误包'
                            },
                            {
                                label:"路由聚合",
                                id:'路由聚合'
                            },
                            {
                              label:"链路捆绑",
                              id:'链路捆绑'
                            }
                        ]
                    }
                ]
            },
            //高级功能开始扫描
            specialSearchStartAdv(){
                //WebSocketTwo显示
                this.webTwoShow = true
                this.cancelScanShow = true
                this.addButtonShow = false
                this.scanButtonShow = false
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
                        console.log(response)
                        if (response == '扫描结束'){
                            this.scanFinish = true
                        }
                    })
                }
            },
            //高级功能开始扫描的备份
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
            //模板扫描获取tree
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
                //WebSocketTwo显示
                this.webTwoShow = true
                this.cancelScanShow = true
                this.addButtonShow = false
                this.scanButtonShow = false
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
                        url:'/sql/SwitchInteraction/formworkScann/' + this.formworkId + '/' + scanNum,
                        method:'post',
                        data:this.finalScanIps
                    }).then(response=>{
                        console.log(response)
                        if (response == '扫描结束'){
                            this.scanFinish = true
                        }
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
            indexMethod(index) {
                return index + 1;
            },


            //点击当前行操作
            currentLine(row){
                /* 鼠标点击行 */
                let lineNum = row.row_index
                /*点击行获得渲染 其余行失去渲染*/
                this.tableData.forEach((row,index)=>{
                    if (lineNum != index){
                        /* true 时 输入框失去光标时，为可改修改状态 */
                        /* false 时 输入框失去光标时，为可改修改状态 */
                        row.isEdit = false
                        this.handleBlur1(row)
                    }else if (lineNum == index){
                        /* 选择当前行添加渲染*/
                        row.isEdit = true
                    }
                })

                this.handleBlur2(row)

                /* 判断数组用户名 */
                this.tableData.forEach((row,index)=>{
                    if (lineNum != index && row.name == "" ){
                        console.log(index + "= 请输入用户名  currentLine方法")
                        row.isExist = false
                        row.name = "请输入用户名"
                    }else if (lineNum == index && row.name == "请输入用户名" ){
                        console.log(index + "= ")
                        row.isExist = true
                        row.name = ''
                    }else if (lineNum == index && row.name != "请输入用户名" && row.name != ""){
                        row.isExist = true
                    }
                })

            },
            /* 将IP输入框渲染移除 */
            handleBlur(row){
                this.handleBlur1(row)
                this.handleBlur2(row)
            },
            /* 将IP输入框渲染移除 */
            handleBlur1(row){
                if (row.ip == ""){
                    row.ip = "请输入IP"
                }
                row.ipIsEdit = false
            },
            /* 查看数据库问题扫描结果表是否有登录信息*/
            handleBlur2(row){
                /* 判断是否输入IP，如果未输入IP则直接返回终止方法 */
                if (row.ip === '') {
                    return;
                }
                let ip = row.ip
                return request({
                    url: '/share/switch_scan_result/getTheLatestData/'+ ip,
                    method: 'get'
                }).then(response=>{

                    /*console.log(ip)
                    console.log((JSON.parse(JSON.stringify(response.data))))*/
                    for (let i =0 ; i < this.tableData.length ; i++){
                        if ( this.tableData[i].ip == ip ){

                            /* 没有查询到信息 校验IP地址合法性 */
                            const regex = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
                            this.tableData[i].iPIsExist = regex.test(row.ip);
                            if(response.data === undefined){
                                /*if (this.tableData[i].name == ""){
                                    console.log(this.tableData[i].row_index + "= 请输入用户名  handleBlur方法")
                                    this.tableData[i].isExist = false
                                    this.tableData[i].name = "请输入用户名"
                                }*/
                            }else {
                                this.tableData[i].isExist = true
                                this.tableData[i].name = JSON.parse(JSON.stringify(response.data.switchName))
                                this.tableData[i].password = JSON.parse(JSON.stringify(response.data.switchPassword))
                                this.tableData[i].passmi = JSON.parse(JSON.stringify(response.data.configureCiphers))
                            }
                        }
                    }

                })
            },


            /*当点击用户名输入框内容时，
            请输入用户名置空 如果用户名为：请输入用户名时置空*/
            blank(row){
                if (row.name == "请输入用户名"){
                    /*console.log(row.row_index + "= ")
                    row.isExist = true*/
                    row.name = ""
                }
            },
            /*当点击IP输入框内容时，
            请输入IP置空 如果IP为：请输入IP时置空*/
            obtain(row){
                if (row.ip == "请输入IP"){
                    row.ip = ""
                }
                row.ipIsEdit = true
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
            //计数器
            handleChange(value) {
                if (this.num > this.maxValue){
                    this.num = this.maxValue
                }
            },
            /////////////////////// 导入模块 /////////////////////////////////////////
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

            /** 开启快照功能 */
            startSnapshot() {
                this.finalScanMethod()
                return request({
                    url:'/advanced/SnapshotFunction/startSnapshot',
                    method:'post',
                    data: this.finalScanIps
                }).then(response=>{

                })
            },

            /** 点击竣工 开启弹窗*/
            closeSnapshot(){
                //竣工弹窗
                this.completionWindow= true
            },


            deleteData(){
                this.ifDeleteData = true,
                this.completion(),

                //竣工弹窗
                this.completionWindow= false,
                //竣工路径
                this.completionUrl= ''
            },
            completion(){
                return request({
                    url: '/advanced/SnapshotFunction/threadInterrupt/' + this.ifDeleteData ,
                    method:'post',
                })
            },
            analysis(){
              return request({
                url: '/GetLogInformation/getOperationalAnalysisLogData/' ,
                method:'get',
              })
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

  .upload-demo {
    width: 300px;
    height: 180px;
    text-align: center;
    line-height: 160px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    background-color: #f5f7fa;
  }
  .upload-demo i {
    font-size: 28px;
    color: #99a9bf;
  }

</style>
