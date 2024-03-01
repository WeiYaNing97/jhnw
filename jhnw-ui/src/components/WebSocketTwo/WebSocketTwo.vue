<template>
  <div>
<!--    lishiData  nowData  tableDataqq-->
<!--    <el-button type="success" size="small" @click="allxiu" v-show="chuci">一键修复</el-button>-->
<!--    <el-button type="success" size="small" @click="testOne">测试</el-button>-->
<!--    <el-button @click="closeWebSocket">关闭WebSocket</el-button>-->
<!--    <el-button type="primary" size="small" @click="lishi">历史扫描</el-button>-->
<!--    <p>我是：{{ endIp }}</p>-->
<!--    <el-button type="primary" size="small" @click="wenben">测试按钮</el-button>-->
<!--    <el-button @click="closeWeb">关闭webscoket</el-button>-->
<!--    <el-button type="primary" size="small" @click="exportDocx">生成报告</el-button>-->
<!--    <el-input type="textarea" v-model="wenbenben"></el-input>-->
<!--    当前扫描-->
    <el-table v-loading="loading"
              :data="nowData"
              ref="treenow"
              v-show="chuci"
              @row-click="expandChangeone"
              style="width: 100%"
              row-key="hproblemId"
              :cell-style="hongse"
              default-expand-all
              :tree-props="{children: 'children',hasChildren: 'hasChildren'}"
              :cell-class-name="lookDom"
              :span-method="arraySpanMethodOne">
<!--      <el-table-column prop="switchIp" label="主机" width="150"></el-table-column>-->
<!--      <el-table-column prop="showBasicInfo" label="基本信息" width="200"></el-table-column>-->
      <el-table-column prop="hebing" label="主机(基本信息)" width="230">
        <template slot-scope="scope">
          <span class="el-icon-circle-check" v-loading="scope.row.loading">{{ scope.row.hebing }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="typeProblem" label="分类" width="120"></el-table-column>
      <el-table-column prop="problemName" label="问题"></el-table-column>
      <el-table-column prop="ifQuestion" label="是否异常"></el-table-column>
      <el-table-column prop="solve" label="解决">
        <template slot-scope="scope">
          <el-button size="mini"
                     type="text"
                     icon="el-icon-first-aid-kit"
                     v-show="scope.row.ifQuestion==='异常'"
                     @click="presentRepair(scope.row,'presentOne')">修复</el-button>
          <el-button style="margin-left: 0" size="mini" type="text" icon="el-icon-first-aid-kit"
                     v-show="scope.row.hasOwnProperty('switchIp')&&!scope.row.hasOwnProperty('typeProblem')"
                     @click.stop="presentRepair(scope.row,'presentSingle')">单台修复</el-button>
          <el-button size="mini" type="text" icon="el-icon-view"
                     v-show="scope.row.hasOwnProperty('problemDescribeId')"
                     @click="xiangqing(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit"
                     v-if="scope.row.dynamicInformation && scope.row.dynamicInformation.includes('光衰')"
                     @click="editPara(scope.row)">重设</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="oneClickRepair" label="一键修复" width="120" v-if="oneClickShow">
        <template slot="header" slot-scope="scope">
          <el-button type="success" icon="el-icon-first-aid-kit"
                     size="small" round @click="presentRepair(scope.row,'oneClickRepair')">一键修复</el-button>
        </template>
      </el-table-column>
    </el-table>
<!--    历史扫描-->
    <el-table v-loading="loading"
              :data="lishiData"
              ref="tree"
              v-show="huisao"
              @row-click="expandChange"
              style="width: 100%"
              row-key="hproblemId"
              :cell-style="hongse"
              :default-expand-all="false"
              :tree-props="{children: 'children',hasChildren: 'hasChildren'}"
              :span-method="arraySpanMethodTwo">
      <el-table-column prop="createTime" label="扫描时间" width="180"></el-table-column>
<!--      <el-table-column prop="switchIp" label="主机" width="130"></el-table-column>-->
<!--      <el-table-column prop="showBasicInfo" label="基本信息" width="200"></el-table-column>-->
      <el-table-column prop="hebing" label="主机(基本信息)" width="200"></el-table-column>
      <el-table-column prop="typeProblem" label="分类" width="120"></el-table-column>
      <el-table-column prop="problemName" label="问题" ></el-table-column>
      <el-table-column prop="ifQuestion" label="是否异常"></el-table-column>
      <el-table-column prop="solve" label="解决">
        <template slot-scope="scope">
          <el-button size="mini"
                     type="text"
                     icon="el-icon-edit"
                     v-show="scope.row.ifQuestion==='异常'"
                     @click="xiufuone(scope.row)">修复</el-button>
          <el-button style="margin-left: 0" size="mini" type="text"
                     v-show="scope.row.switchIp != undefined"
                     @click.stop="xiuallone(scope.row)">单台修复</el-button>
          <el-button style="margin-left: 0" type="success" plain round
            size="small" v-show="scope.row.createTime != undefined"
                     @click.stop="huitimeyijian(scope.row)">一键修复</el-button>
          <el-button size="mini" type="text" icon="el-icon-view"
                     v-show="scope.row.hasOwnProperty('problemDescribeId')"
                     @click="xiangqing(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

<!--    查看详情-->
    <el-dialog
      title="问题详情以及解决办法"
      :visible.sync="dialogVisible"
      width="50%"
      :before-close="handleClose">
<!--      <TinymceEditor :proxiang="proxiang" ref="abc"></TinymceEditor>-->
      <el-input type="textarea" v-model="particular" rows="15" readonly></el-input>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>

<!--    修改高级功能参数-->
<!--    <el-dialog-->
<!--      title="修改参数"-->
<!--      :visible.sync="dialogVisibleAdvanced"-->
<!--      width="30%"-->
<!--      :before-close="handleCloseAdvanced">-->
<!--      <div>-->

<!--      </div>-->
<!--      <span slot="footer" class="dialog-footer">-->
<!--    <el-button @click="dialogVisible = false">取 消</el-button>-->
<!--    <el-button type="primary" @click="dialogVisible = false">确 定</el-button>-->
<!--  </span>-->
<!--    </el-dialog>-->

    <!-- 添加或修改光衰平均值比较对话框 -->
    <el-dialog title="修改参数" :visible.sync="dialogVisibleAdvanced" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" :inline="true" label-width="100px">
        <el-form-item label="交换机ip" prop="switchIp">
          <el-input v-model="form.switchIp" :disabled="true" placeholder="请输入交换机ip" />
        </el-form-item>
        <!--        <el-form-item label="交换机四项基本信息表ID索引" prop="switchId">-->
        <!--          <el-input v-model="form.switchId" placeholder="请输入交换机四项基本信息表ID索引" />-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="参数数量" prop="numberParameters">-->
        <!--          <el-input v-model="form.numberParameters" placeholder="请输入参数数量" />-->
        <!--        </el-form-item>-->
        <el-form-item label="端口号" prop="port">
          <el-input v-model="form.port" :disabled="true" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="TX基准功率" prop="txStartValue">
          <el-input v-model="form.txStartValue" placeholder="请输入TX起始值(基准)" />
        </el-form-item>
        <el-form-item label="RX基准功率" prop="rxStartValue">
          <el-input v-model="form.rxStartValue" placeholder="请输入RX起始值(基准)" />
        </el-form-item>

        <el-form-item label="TX额定偏差" prop="txRatedDeviation">
          <el-input v-model="form.txRatedDeviation" placeholder="请输入TX额定偏差" />
        </el-form-item>
        <el-form-item label="RX额定偏差" prop="rxRatedDeviation">
          <el-input v-model="form.rxRatedDeviation" placeholder="请输入RX额定偏差" />
        </el-form-item>
        <el-form-item label="TX平均功率" prop="txAverageValue">
          <el-input v-model="form.txAverageValue" placeholder="请输入TX平均值" />
        </el-form-item>
        <el-form-item label="RX平均功率" prop="rxAverageValue">
          <el-input v-model="form.rxAverageValue" placeholder="请输入RX平均值" />
        </el-form-item>
        <el-form-item label="TX当前功率" prop="txLatestNumber">
          <el-input v-model="form.txLatestNumber" placeholder="请输入TX最新参数" />
        </el-form-item>
        <el-form-item label="RX当前功率" prop="rxLatestNumber">
          <el-input v-model="form.rxLatestNumber" placeholder="请输入RX最新参数" />
        </el-form-item>
<!--        <el-form-item label="额定衰耗偏差" prop="ratedDeviation">-->
<!--          <el-input v-model="form.ratedDeviation" placeholder="请输入额定偏差" />-->
<!--        </el-form-item>-->
        <!--        <el-form-item label="保留字段一" prop="valueOne">-->
        <!--          <el-input v-model="form.valueOne" placeholder="请输入保留字段一" />-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="保留字段二" prop="valueTwo">-->
        <!--          <el-input v-model="form.valueTwo" placeholder="请输入保留字段二" />-->
        <!--        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">提 交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
    import log from "../../views/monitor/job/log"
    import axios from 'axios'
    import request from '@/utils/request'
    import Cookies from "js-cookie"
    import TinymceEditor from "@/components/Tinymce/TinymceEditor"
    import { ExportBriefDataDocx } from '@/utils/exportBriefDataDocx'
    export default {
        name: "WebSocketTwo",
        components: {
            TinymceEditor
        },
        props: {
            queryParams: '',
            num: '',
            endIp:'',
            //父传子是否显示一键修复
            oneClickShow:false
        },
        data() {
            return {
                //报告
                docxData:{
                    tableData:[],
                    year:'',
                    month:''
                },
                //扫描结束IP备份
                endIpCopy: '',
                //扫描结束IP数组
                endIpS:[],
                //详情显示
                particular: '',
                proxiang: '',
                //当前扫描所有交换机登录信息
                alljiao: {
                    allInfo: []
                },
                //
                wenbenben: '',
                //高级修改
                dialogVisibleAdvanced:false,
                //光衰查询IP、port参数
                advancedPort:'',
                advancedIp:'',
                // 表单参数
                form: {},
                // 表单校验
                rules: {
                    switchIp: [
                        { required: true, message: "交换机ip不能为空", trigger: "blur" }
                    ],
                    switchId: [
                        { required: true, message: "交换机四项基本信息表ID索引不能为空", trigger: "blur" }
                    ],
                    numberParameters: [
                        { required: true, message: "参数数量不能为空", trigger: "blur" }
                    ],
                    port: [
                        { required: true, message: "端口号不能为空", trigger: "blur" }
                    ],
                    rxRatedDeviation: [
                        { required: true, message: "RX额定偏差不能为空", trigger: "blur" }
                    ],
                    txRatedDeviation: [
                        { required: true, message: "TX额定偏差不能为空", trigger: "blur" }
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
                },
                dialogVisible: false,
                //后台回显所有交换机信息
                newArr: [],
                //现在返回的交换机信息
                nowArr: [],
                chuci: true,
                huisao: false,
                // ws是否启动
                wsIsRun: false,
                // 定义ws对象
                webSocket: null,
                // ws请求链接（类似于ws后台地址）
                ws: '',
                // ws定时器
                wsTimer: null,
                //当前扫描最终数据
                nowData: [],
                //整合数据
                newData:[],
                //是否执行if外语句
                ifOut:true,
                ifOutFather:true,
                lishiData: [],
                loading: false,
                formLabelWidth: '50px',
                xiuform: {
                    xiala: ''
                },
                depss: []
            }
        },
        // 初始化时将父组件传递的值赋值给子组件的数据属性
        mounted() {
            this.wsIsRun = true
            this.wsInit()
            window.onbeforeunload = function () {
                this.webSocket.close()
            }
        },
        watch: {
            // saowanend() {
            //     if (this.saowanend === true) {
            //         for (let i = 0; i < this.nowData.length; i++) {
            //             this.$set(this.nowData[i], 'loading', false)
            //             for (let g = 0; g < this.nowData[i].children.length; g++){
            //                 for (let m = 0; m < this.nowData[i].children[g].children.length;m++){
            //                     //查找是否有问题
            //                     if (this.nowData[i].children[g].children[m].ifQuestion.includes('异常')){
            //                         this.noPro = false
            //                     }
            //                 }
            //             }
            //         }
            //     }
            // },
            //监听扫描完成IP变化
            endIp(newVal){
                this.endIpCopy = newVal
                this.endIpS.push(newVal)
                for (let i = 0; i < this.nowData.length; i++) {
                    if (this.endIpCopy == this.nowData[i].copyIpThred) {
                        this.$set(this.nowData[i], 'loading', false)
                    }
                }
            },
        },
        created() {
            // const usname = Cookies.get('usName')
            let timeXun = setInterval(() => {
                if (this.webSocket.readyState === 1) {
                    this.webSocket.send('ping')
                } else {
                    throw Error('服务未连接')
                }
            },30000)
        },
        methods: {
            //导出
            exportDocx(){
                console.log('导出');
                this.docxData.tableData = this.nowData
                this.docxData.year = 2022
                this.docxData.month = 9
                // ExportBriefDataDocx 是我导入的一个文件，里边写的是导出文本的核心代码
                ExportBriefDataDocx('/报告.docx', this.docxData, '导出的.docx')
                // ExportBriefDataDocx('/text.docx', this.docxData, '文档导出.docx') // text.docx放在了根目录下的public文件夹下
            },
            //给icon添加class、样式
            lookDom({row, column, rowIndex, columnIndex}) {
                // if (this.endIpCopy == row.copyIpThred) {
                //     return 'table-oneStyle'
                // }
                if (this.endIpS.indexOf(row.copyIpThred) != -1){
                    return 'table-oneStyle'
                }
            },
            //当前扫描信息合并列
            arraySpanMethodOne({row, column, rowIndex, columnIndex}) {
                if (row.hebing != null) {
                    if (columnIndex === 0) {
                        return [1, 2];
                    } else if (columnIndex === 1) {
                        return [0, 0];
                    }
                }
            },
            //历史扫描合并列
            arraySpanMethodTwo({row, column, rowIndex, columnIndex}) {
                if (row.hebing != null) {
                    if (columnIndex === 1) {
                        return [1, 2];
                    } else if (columnIndex === 2) {
                        return [0, 0];
                    }
                }
            },
            //展开折叠当前列表
            expandChange(row) {
                this.$refs.tree.toggleRowExpansion(row)
            },
            expandChangeone(row) {
                this.$refs.treenow.toggleRowExpansion(row)
            },
            //测试总按钮
            wenben() {
                console.log(this.num)
                console.log(this.wenbenben)
            },
            //异常红色
            hongse(row, column) {
                let reds = {
                    'color': 'red'
                }
                let greens = {
                    'color': 'green'
                }
                if (row.column.label === '是否异常') {
                    if (row.row.ifQuestion != '安全') {
                        return reds
                    } else if (row.row.ifQuestion === '已解决') {
                        return greens
                    }
                }
                // if (row.row.ifQuestion === '异常'){
                //     return reds
                // }
            },
            //查看详情
            xiangqing(row) {
                this.dialogVisible = true
                const xiangid = row.problemDescribeId
                console.log(xiangid)
                return request({
                    url: `/sql/problem_describe/selectProblemDescribe?id=${xiangid}`,
                    method: 'get',
                }).then(response => {
                    console.log(response.problemDescribe)
                    this.particular = response.problemDescribe
                    // this.proxiang = response.problemDescribe
                    // this.$refs.abc.geizi()
                })
            },
            //编辑高级功能参数
            editPara(row){
                console.log(row)
                this.advancedIp = row.noUseIp
                for (let i = 0; i < row.valueInformationVOList.length; i++) {
                    if (row.valueInformationVOList[i].dynamicInformation.includes('/')){
                        this.advancedPort = row.valueInformationVOList[i].dynamicInformation
                    }
                }
                const advancedParams = {
                    pageNum: 1,
                    pageSize: 10,
                    switchIp: this.advancedIp,
                    port: this.advancedPort
                }
                return request({
                    url: '/advanced/comparison/reset',
                    method: 'get',
                    params:advancedParams
                }).then(response => {
                    console.log(response)
                    this.form = response.rows[0]
                    this.dialogVisibleAdvanced = true
                })
            },
            handleClose(done) {
                this.dialogVisible = false
            },
            handleCloseAdvanced(done){
                this.dialogVisibleAdvanced = false
            },
            //提交
            submitForm(){
                return request({
                    url: '/advanced/comparison',
                    method: 'put',
                    data: this.form
                }).then(response => {
                    this.dialogVisibleAdvanced = false
                })
            },
            cancel(){
                this.dialogVisibleAdvanced = false
            },
            //测试按钮
            testOne() {
                console.log('awdawadaw')
            },
            //当前修复整合优化
            presentRepair(row,type){
                let allProIdList = []
                let problemIdList = []
                let userinformation = []
                let userinformationCopy = []
                let scanNum = this.num
                if(type == 'presentSingle'){
                    //所有问题id集合
                    this.nowData.forEach(item => {
                        item.children.forEach(child => {
                            child.children.forEach(innerChild => {
                                allProIdList.push(innerChild.questionId)
                            })
                        })
                    })
                    //异常问题id集合
                    row.children.forEach(item => {
                        item.children.forEach(child => {
                            if (child.ifQuestion == '异常'){
                                problemIdList.push(child.questionId)
                            }
                        })
                    })
                    //交换机五条信息
                    userinformationCopy = this.alljiao.allInfo
                        .filter(chaip => chaip['ip'] == row.switchIp)
                        .flatMap(chaip => problemIdList.map(() => JSON.stringify(chaip)))
                }
                else if (type == 'presentOne'){
                    //所有问题ID集合
                    this.nowData.forEach(item => {
                        item.children.forEach(child => {
                            child.children.forEach(innerChild => {
                                allProIdList.push(innerChild.questionId)
                            })
                        })
                    })
                    //当前点击问题ID集合
                    problemIdList.push(row.questionId)
                    //交换机五条信息
                    userinformationCopy = this.alljiao.allInfo
                        .filter(chaip => chaip['ip'] == row.noUseIp)
                        .flatMap(chaip => problemIdList.map(() => JSON.stringify(chaip)))
                }
                else if (type == 'oneClickRepair'){
                    console.log(this.alljiao.allInfo)
                    console.log(this.nowData)
                    let errIpList = []
                    this.nowData.forEach(item => {
                        item.children.forEach(child => {
                            child.children.forEach(innerChild => {
                                if (innerChild.ifQuestion == '异常'){
                                    problemIdList.push(innerChild.questionId)
                                    errIpList.push(item.switchIp)
                                }
                                allProIdList.push(innerChild.questionId)
                            })
                        })
                    })
                    userinformationCopy = errIpList.flatMap(ip => this.alljiao.allInfo
                        .filter(chaip => chaip['ip'] == ip).map(x => JSON.stringify(x)))
                }
                //去掉线程名，只保留ip
                userinformation = userinformationCopy.map(jsonString => {
                    const obj = JSON.parse(jsonString)
                    const ipValue = obj.ip.split(':')[0]
                    obj.ip = ipValue
                    return JSON.stringify(obj)
                })
                console.log(allProIdList)
                console.log(problemIdList)
                console.log(userinformation)
                return request({
                    // url: '/sql/SolveProblemController/batchSolutionMultithreading/' + problemIdList + '/' + scanNum + '/' + allProIdList,
                    method: 'post',
                    data: userinformation
                }).then(response => {
                    console.log(response)
                })
            },
            //当前扫描一键修复所有问题
            allxiu() {
                //问题ID集合
                console.log(this.alljiao.allInfo)
                const problemIdList = []
                //暂时注释，逻辑不对
                // const iplist = []
                //有异常的问题的ip集合
                const errIpList = []
                //页面中所有id集合
                const allProIdList = []
                const yijian = this.nowData
                for (let i = 0; i < yijian.length; i++) {
                    for (let g = 0; g < yijian[i].children.length; g++) {
                        for (let m = 0; m < yijian[i].children[g].children.length; m++) {
                            // iplist.push(yijian[i]['switchIp'])
                            if (yijian[i].children[g].children[m].ifQuestion == '异常'){
                                problemIdList.push(yijian[i].children[g].children[m].questionId)
                                //3.15添加逻辑
                                errIpList.push(yijian[i].switchIp)
                                console.log(errIpList)
                            }
                            allProIdList.push(yijian[i].children[g].children[m].questionId)
                        }
                    }
                }
                // const allProIdList = problemIdList
                const list1 = []
                for (let i = 0; i < this.alljiao.allInfo.length; i++) {
                    const chaip = this.alljiao.allInfo[i]
                    for (let g = 0; g < errIpList.length; g++) {
                        if (chaip['ip'] === errIpList[g]) {
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x => JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                console.log(allProIdList)
                return request({
                    url: '/sql/SolveProblemController/batchSolutionMultithreading/' + problemIdList + '/' + scanNum + '/' + allProIdList,
                    method: 'post',
                    data: userinformation
                }).then(response => {
                    this.$message.success('修复请求以提交!')
                    console.log('成功')
                })
            },
            // 当前修复单个问题
            xiufu(row) {
                //获取所有问题id
                const allProIdList = []
                const alliplist = []
                const allyijian = this.nowData
                for (let i = 0; i < allyijian.length; i++) {
                    for (let g = 0; g < allyijian[i].children.length; g++) {
                        for (let m = 0; m < allyijian[i].children[g].children.length; m++) {
                            alliplist.push(allyijian[i]['switchIp'])
                            allProIdList.push(allyijian[i].children[g].children[m].questionId)
                        }
                    }
                }
                //
                const thisid = row.hproblemId
                let thisparip = ''
                // lishiData nowData
                const allwenti = this.nowData
                for (let i = 0; i < allwenti.length; i++) {
                    for (let g = 0; g < allwenti[i].children.length; g++) {
                        for (let m = 0; m < allwenti[i].children[g].children.length; m++) {
                            if (allwenti[i].children[g].children[m].hproblemId === thisid) {
                                thisparip = allwenti[i].switchIp
                                console.log(thisparip)
                            }
                        }
                    }
                }
                const list1 = []
                const problemIdList = []
                problemIdList.push(row.questionId)
                for (let i = 0; i < this.alljiao.allInfo.length; i++) {
                    const chaip = this.alljiao.allInfo[i]
                    if (chaip['ip'] === thisparip) {
                        for (let g = 0; g < problemIdList.length; g++) {
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x => JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                console.log(allProIdList)
                return request({
                    url: '/sql/SolveProblemController/batchSolutionMultithreading/' + problemIdList + '/' + scanNum + '/' + allProIdList,
                    method: 'post',
                    data: userinformation
                }).then(response => {
                    this.$message.success('修复请求以提交!')
                    console.log('成功')
                })
            },
            //当前扫描单台一键修复
            xiuall(row) {
                //获取所有问题id
                const allProIdList = []
                const alliplist = []
                //
                const allyijian = this.nowData
                for (let i = 0; i < allyijian.length; i++) {
                    for (let g = 0; g < allyijian[i].children.length; g++) {
                        for (let m = 0; m < allyijian[i].children[g].children.length; m++) {
                            alliplist.push(allyijian[i]['switchIp'])
                            allProIdList.push(allyijian[i].children[g].children[m].questionId)
                        }
                    }
                }
                console.log(allProIdList)
                //单台 点击的修复 ip
                const thisip = row.switchIp
                const listAll = row.children
                const list1 = []
                const problemIdList = []
                for (let i = 0; i < listAll.length; i++) {
                    for (let g = 0; g < listAll[i].children.length; g++) {
                        if (listAll[i].children[g].ifQuestion == '异常'){
                            problemIdList.push(listAll[i].children[g].questionId)
                        }
                    }
                }
                for (let i = 0; i < this.alljiao.allInfo.length; i++) {
                    const chaip = this.alljiao.allInfo[i]
                    if (chaip['ip'] === thisip) {
                        for (let g = 0; g < problemIdList.length; g++) {
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x => JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                console.log(allProIdList)
                return request({
                    url: '/sql/SolveProblemController/batchSolutionMultithreading/' + problemIdList + '/' + scanNum + '/' + allProIdList,
                    method: 'post',
                    data: userinformation
                }).then(response => {
                    this.$message.success('修复请求以提交!')
                })
            },
            /**
             * 初始化ws
             */
            wsInit() {
                // var ws = null
                // const wsuri = 'ws://192.168.1.98/dev-api/websocket/loophole'
                const wsuri = `wss://${location.host}/dev-api/websocket/loophole${Cookies.get('usName')}`
                 //const wsuri = `ws://${location.host}/prod-api/websocket/loophole${Cookies.get('usName')}`
                this.ws = wsuri
                if (!this.wsIsRun) return
                // 销毁ws
                this.wsDestroy()
                // 初始化ws
                this.webSocket = new WebSocket(this.ws)
                // ws连接建立时触发
                this.webSocket.addEventListener('open', this.wsOpenHanler)
                // ws服务端给客户端推送消息
                this.webSocket.addEventListener('message', this.wsMessageHanler)
                // ws通信发生错误时触发
                this.webSocket.addEventListener('error', this.wsErrorHanler)
                // ws关闭时触发
                this.webSocket.addEventListener('close', this.wsCloseHanler)

                // 检查ws连接状态,readyState值为0表示尚未连接，1表示建立连接，2正在关闭连接，3已经关闭或无法打开
                clearInterval(this.wsTimer)
                this.wsTimer = setInterval(() => {
                    if (this.webSocket.readyState === 1) {
                        console.log('连接中')
                        clearInterval(this.wsTimer)
                    } else {
                        console.log('ws建立连接失败')
                        this.wsInit()
                    }
                }, 3000)
            },
            // ws连接建立时触发
            wsOpenHanler(event) {
                console.log('ws建立连接成功')
            },
            //修改key->children
            changeTreeDate(arrayJsonObj, oldKey, newKey){
                let strtest = JSON.stringify(arrayJsonObj);
                let reg = new RegExp(oldKey, 'g');
                let newStr = strtest.replace(reg, newKey);
                return JSON.parse(newStr);
            },
            // ws服务端给客户端推送消息
            wsMessageHanler(e) {
                if (e.data === 'pong'){
                    // console.log('22222'+e.data)
                }else if(e.data.includes('内存')){
                    // console.log(e.data)
                }else {
                    console.log('WebTwo传输的单条问题数据====================：')
                    console.log(JSON.parse(e.data))
                    let newJson = this.changeTreeDate(JSON.parse(e.data), 'switchProblemVOList', 'children')
                    let newJson1 = this.changeTreeDate(newJson, 'switchProblemCOList', 'children')
                    //获取 IP+线程
                    const copyIpThred = newJson1[0].switchIp
                    //截取IP
                    const splitIp = newJson1[0].switchIp.split(':')[0]
                    newJson1[0].copyIpThred = copyIpThred
                    newJson1[0].switchIp = splitIp
                    //改变结构后
                    if (this.newData.length == 0){
                        this.newData = newJson1
                    }else {
                        for (let i = 0; i < this.newData.length; i++) {
                            if (this.newData[i].copyIpThred == newJson1[0].copyIpThred){
                                this.ifOutFather = true
                                for (let j = 0; j < this.newData[i].children.length; j++) {
                                    if (this.newData[i].children[j].typeProblem == newJson1[0].children[0].typeProblem
                                        && newJson1[0].children[0].children[0].ifQuestion == '安全'){
                                            this.newData[i].children[j].children.push(newJson1[0].children[0].children[0])
                                            this.ifOut = false
                                            break
                                    }else if (this.newData[i].children[j].typeProblem == newJson1[0].children[0].typeProblem
                                        && newJson1[0].children[0].children[0].ifQuestion == '异常'){
                                        this.newData[i].children[j].children.unshift(newJson1[0].children[0].children[0])
                                        this.ifOut = false
                                        break
                                    }
                                    this.ifOut = true
                                }
                                if (this.ifOut){
                                    this.newData[i].children.push(newJson1[0].children[0])
                                }
                            }else {
                                this.ifOutFather = false
                            }
                        }
                        if (!this.ifOutFather){
                            this.newData.push(newJson1[0])
                        }
                    }
                    //最终问题数据
                    this.nowData = JSON.parse(JSON.stringify(this.newData))
                    const shu = this.nowData
                    //给ip添加loading
                    for (let i = 0; i < shu.length; i++) {
                        if (this.endIpS.indexOf(shu[i].copyIpThred) == -1){
                            this.$set(shu[i], 'loading', true)
                            // console.log(shu[i])
                        }
                    }
                    //合并IP、四条基本信息
                    for (let i = 0; i < shu.length; i++) {
                        if (this.endIpS.indexOf(shu[i].copyIpThred) == -1){
                            var hebingInfo = '　' + shu[i].switchIp + ' ' + shu[i].showBasicInfo
                        }else {
                            var hebingInfo = shu[i].switchIp + ' ' + shu[i].showBasicInfo
                        }
                        this.$set(shu[i], 'hebing', hebingInfo)
                    }
                    //参数+问题名 拼接展示
                    for (let i = 0; i < shu.length; i++) {
                        for (let g = 0; g < shu[i].children.length; g++) {
                            for (let m = 0; m < shu[i].children[g].children.length; m++) {
                                //给第三层数据添加IP
                                const noUseIp = shu[i].switchIp
                                this.$set(shu[i].children[g].children[m],'noUseIp',noUseIp)
                                //参数+问题名 拼接展示
                                if (shu[i].children[g].children[m].valueInformationVOList.length > 0) {
                                    let mi1 = ''
                                    let mi2 = ''
                                    for (let n = 0; n < shu[i].children[g].children[m].valueInformationVOList.length; n++) {
                                        if (shu[i].children[g].children[m].valueInformationVOList[n].exhibit === '是') {
                                            mi1 = shu[i].children[g].children[m].valueInformationVOList[n].dynamicInformation
                                            mi2 = mi2 + ' ' + mi1
                                        }
                                    }
                                    const wenti = shu[i].children[g].children[m].problemName
                                    const zuihou = mi2 + " " + wenti
                                    this.$set(shu[i].children[g].children[m], 'problemName', zuihou)
                                }
                            }
                        }
                    }
                    //获取当前扫描的五条登录信息，添加了配置密码
                    const nowxinxi = []
                    for (let i = 0; i < shu.length; i++) {
                        for (let g = 0; g < shu[i].children.length; g++) {
                            const nowinfo = {}
                            this.$set(nowinfo, 'ip', shu[i].switchIp)
                            this.$set(nowinfo, 'name', shu[i].children[g].switchName)
                            this.$set(nowinfo, 'password', shu[i].children[g].switchPassword)
                            this.$set(nowinfo, 'mode', shu[i].children[g].loginMethod)
                            this.$set(nowinfo, 'port', shu[i].children[g].portNumber)
                            this.$set(nowinfo, 'configureCiphers', shu[i].children[g].configureCiphers)
                            nowxinxi.push(nowinfo)
                            break
                        }
                    }
                    this.alljiao.allInfo = nowxinxi
                    console.log('后端交换机IP数据:')
                    console.log(this.alljiao.allInfo)
                    console.log('展示的问题数据:')
                    console.log(this.nowData)
                }
            },
            /**
             * ws通信发生错误
             */
            wsErrorHanler(event) {
                console.log('错误的' + "+" + event.code + "+" + event.reason + "+" + event.wasClean)
                console.log(event, '通信发生错误')
                this.wsInit()
            },
            closeWeb(event){
                console.log('guanbi')
                this.webSocket.close()
                // this.wsCloseHanler(event)
            },
            /**
             * ws关闭
             */
            wsCloseHanler(event) {
                console.log('正常的' + "+" + event.code + "+" + event.reason + "+" + event.wasClean)
                console.log(event, 'ws关闭')
                this.wsInit()
            },
            /////////////
            closeWebSocket(event){
                this.wsCloseHanler(event)
            },
            /**
             * 销毁ws
             */
            wsDestroy() {
                if (this.webSocket !== null) {
                    this.webSocket.removeEventListener('open', this.wsOpenHanler)
                    this.webSocket.removeEventListener('message', this.wsMessageHanler)
                    this.webSocket.removeEventListener('error', this.wsErrorHanler)
                    this.webSocket.removeEventListener('close', this.wsCloseHanler)
                    this.webSocket.close()
                    this.webSocket = null
                    clearInterval(this.wsTimer)
                }
            }
        }
    }
</script>

<style scoped>
  >>> .el-table td.el-table__cell{
    border-bottom: none !important;
  }
  >>> .el-loading-mask{
    position: inherit;
  }
  >>> .el-loading-spinner{
    width: auto;
    margin-top: -20px ! important;
    height: 20px ! important;
    margin-left: -5px;
  }
  >>> .el-loading-spinner .circular{
    width: 20px;
    height: 20px;
  }
  >>> .el-loading-spinner .path{
    stroke: #13ce66;
  }
  >>> .el-icon-circle-check:before{
    display: none;
  }
  >>> .table-oneStyle .el-icon-circle-check:before{
    display: inline-block;
    color: #00ff80;
    font-size: 18px;
  }
</style>

