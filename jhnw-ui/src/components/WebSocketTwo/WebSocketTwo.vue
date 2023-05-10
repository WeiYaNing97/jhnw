<template>
  <div>
<!--    lishiData  nowData  tableDataqq-->
<!--    <el-button type="success" size="small" @click="allxiu" v-show="chuci">一键修复</el-button>-->

<!--    <el-button type="success" size="small" @click="testOne">测试</el-button>-->
<!--    <el-button type="primary" size="small" @click="lishi">历史扫描</el-button>-->
<!--    <p>我是：{{ endIp }}</p>-->
<!--    <el-button type="primary" size="small" @click="wenben">测试按钮</el-button>-->
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
                     icon="el-icon-edit"
                     v-show="scope.row.ifQuestion==='异常'"
                     @click="xiufu(scope.row)">修复</el-button>
          <el-button style="margin-left: 0" size="mini" type="text"
                     v-show="scope.row.hasOwnProperty('switchIp')&&!scope.row.hasOwnProperty('typeProblem')"
                     @click.stop="xiuall(scope.row)">单台修复</el-button>
          <el-button size="mini" type="text" icon="el-icon-view"
                     v-show="scope.row.hasOwnProperty('problemDescribeId')"
                     @click="xiangqing(scope.row)">详情</el-button>
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
            saowanend: false,
            xiufuend: false,
            queryParams: '',
            num: '',
            endIp:''
        },
        data() {
            return {
                //
                noPro:true,
                //报告
                docxData:{
                    tableData:[],
                    year:'',
                    month:''
                },
                //
                endIpCopy: '',
                //
                lishifu: false,
                //详情显示
                particular: '',
                proxiang: '',
                //当前扫描所有交换机登录信息
                alljiao: {
                    allInfo: []
                },
                //
                wenbenben: '',
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
                //
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
            saowanend() {
                if (this.saowanend === true) {
                    for (let i = 0; i < this.nowData.length; i++) {
                        this.$set(this.nowData[i], 'loading', false)
                        for (let g = 0; g < this.nowData[i].children.length; g++){
                            for (let m = 0; m < this.nowData[i].children[g].children.length;m++){
                                //查找是否有问题
                                if (this.nowData[i].children[g].children[m].ifQuestion.includes('异常')){
                                    this.noPro = false
                                }
                            }
                            }
                        }
                    }
                },
            //监听扫描完成IP变化
            endIp(newVal){
                this.endIpCopy = newVal
                console.log('eeeeeeeeeeeeeeeeeeeeeeeeeee')
                console.log(this.endIpCopy)
                for (let i = 0; i < this.nowData.length; i++) {
                    if (this.endIp == this.nowData[i].switchIp) {
                        this.$set(this.nowData[i], 'loading', false)
                    }
                }
            },
            noPro(){
                this.postNoPro()
            }
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
            //全部正常
            postNoPro(){
                this.$emit('allNoPro',this.noPro)
            },
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
                if (this.endIpCopy == row.switchIp) {
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
            handleClose(done) {
                this.dialogVisible = false
            },
            //历史扫描
            lishi() {
                this.lishifu = true
                this.chuci = false
                this.huisao = true
                return request({
                    url: '/sql/SolveProblemController/getUnresolvedProblemInformationByUserName',
                    method: 'post',
                }).then(response => {
                    function changeTreeDate(arrayJsonObj, oldKey, newKey) {
                        let strtest = JSON.stringify(arrayJsonObj);
                        let reg = new RegExp(oldKey, 'g');
                        let newStr = strtest.replace(reg, newKey);
                        return JSON.parse(newStr);
                    }

                    response = changeTreeDate(response, 'scanResultsVOList', 'children')
                    response = changeTreeDate(response, 'switchProblemVOList', 'children')
                    response = changeTreeDate(response, 'switchProblemCOList', 'children')
                    this.lishiData = response
                    const jiaid = this.lishiData
                    //合并信息
                    for (let i = 0; i < jiaid.length; i++) {
                        for (let g = 0; g < jiaid[i].children.length; g++) {
                            var hebingInfo = jiaid[i].children[g].switchIp + ' ' + jiaid[i].children[g].showBasicInfo
                            this.$set(jiaid[i].children[g], 'hebing', hebingInfo)
                        }
                    }
                    //返回数据添加hproblemId
                    for (let i = 0; i < jiaid.length; i++) {
                        this.$set(jiaid[i], 'hproblemId', Math.floor(Math.random() * (999999999999999 - 1) + 1))
                        for (let g = 0; g < jiaid[i].children.length; g++) {
                            this.$set(jiaid[i].children[g], 'hproblemId', Math.floor(Math.random() * (999999999999999 - 1) + 1))
                            for (let m = 0; m < jiaid[i].children[g].children.length; m++) {
                                for (let n = 0; n < jiaid[i].children[g].children[m].children.length; n++) {
                                    this.$set(jiaid[i].children[g].children[m].children[n], 'createTime', null)
                                }
                            }
                        }
                    }
                    //历史扫描问题添加用户名
                    for (let i = 0; i < jiaid.length; i++) {
                        for (let g = 0; g < jiaid[i].children.length; g++) {
                            for (let m = 0; m < jiaid[i].children[g].children.length; m++) {
                                for (let n = 0; n < jiaid[i].children[g].children[m].children.length; n++) {
                                    if (jiaid[i].children[g].children[m].children[n].valueInformationVOList.length > 0) {
                                        let yongone = ''
                                        let endyong = ''
                                        for (let k = 0; k < jiaid[i].children[g].children[m].children[n].valueInformationVOList.length; k++) {
                                            if (jiaid[i].children[g].children[m].children[n].valueInformationVOList[k].exhibit === '是') {
                                                yongone = jiaid[i].children[g].children[m].children[n].valueInformationVOList[k].dynamicInformation
                                                endyong = endyong + ' ' + yongone
                                            }
                                        }
                                        const wenti = jiaid[i].children[g].children[m].children[n].problemName
                                        const zuihou = endyong + " " + wenti
                                        this.$set(jiaid[i].children[g].children[m].children[n], 'problemName', zuihou)
                                    }
                                }
                            }
                        }
                    }
                    //获取返回ip、用户名、密码五条信息,添加了配置密码
                    const allxinxi = []
                    for (let i = 0; i < jiaid.length; i++) {
                        for (let g = 0; g < jiaid[i].children.length; g++) {
                            for (let m = 0; m < jiaid[i].children[g].children.length; m++) {
                                const allinfo = {}
                                if (jiaid[i].children[g].children[m].switchName != undefined) {
                                    this.$set(allinfo, 'ip', jiaid[i].children[g].switchIp)
                                    this.$set(allinfo, 'name', jiaid[i].children[g].children[m].switchName)
                                    this.$set(allinfo, 'password', jiaid[i].children[g].children[m].switchPassword)
                                    this.$set(allinfo, 'mode', jiaid[i].children[g].children[m].loginMethod)
                                    this.$set(allinfo, 'port', jiaid[i].children[g].children[m].portNumber)
                                    this.$set(allinfo, 'configureCiphers', jiaid[i].children[g].children[m].configureCiphers)
                                    allxinxi.push(allinfo)
                                    break
                                }
                            }
                        }
                    }
                    //所有交换机信息去重
                    let lishiobj = {}
                    for (let i = 0; i < allxinxi.length; i++) {
                        if (!lishiobj[allxinxi[i].ip]) {
                            this.newArr.push(allxinxi[i])
                            lishiobj[allxinxi[i].ip] = true
                        }
                    }
                    console.log(this.newArr)
                    console.log(jiaid)
                })
            },
            //点击历史扫描给父
            geifu() {
                return this.lishifu
            },
            //回显历史扫描某次时间一键修复
            huitimeyijian(row) {
                const problemIdList = []
                const iplist = []
                const yijian = row.children
                console.log(yijian)
                for (let i = 0; i < yijian.length; i++) {
                    for (let g = 0; g < yijian[i].children.length; g++) {
                        for (let m = 0; m < yijian[i].children[g].children.length; m++) {
                            iplist.push(yijian[i]['switchIp'])
                            problemIdList.push(yijian[i].children[g].children[m].questionId)
                        }
                    }
                }
                const list1 = []
                for (let i = 0; i < this.newArr.length; i++) {
                    const chaip = this.newArr[i]
                    for (let g = 0; g < iplist.length; g++) {
                        if (chaip['ip'] === iplist[g]) {
                            list1.push(chaip)
                        }
                    }
                }
                const allProIdList = []
                const userinformation = list1.map(x => JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                return request({
                    url: '/sql/SolveProblemController/batchSolutionMultithreading/' + problemIdList + '/' + scanNum + '/' + allProIdList,
                    method: 'post',
                    data: userinformation
                }).then(response => {
                    this.$message.success('修复请求以提交!')
                    console.log('成功')
                })
            },
            //测试按钮
            testOne() {
                console.log('awdawadaw')
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
            //历史单台一键修复
            xiuallone(row) {
                // setInterval(this.lishi,5000)
                const thisip = row.switchIp
                const listAll = row.children
                const list1 = []
                const problemIdList = []
                for (let i = 0; i < listAll.length; i++) {
                    for (let g = 0; g < listAll[i].children.length; g++) {
                        problemIdList.push(listAll[i].children[g].questionId)
                    }
                }
                for (let i = 0; i < this.newArr.length; i++) {
                    const chaip = this.newArr[i]
                    if (chaip['ip'] === thisip) {
                        for (let g = 0; g < problemIdList.length; g++) {
                            list1.push(chaip)
                        }
                    }
                }
                const allProIdList = []
                const userinformation = list1.map(x => JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                return request({
                    url: '/sql/SolveProblemController/batchSolutionMultithreading/' + problemIdList + '/' + scanNum + '/' + allProIdList,
                    method: 'post',
                    data: userinformation
                }).then(response => {
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
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
            //历史修复单个问题
            xiufuone(row) {
                console.log(row)
                const thisid = row.hproblemId
                let thisparip = ''
                const allwenti = this.lishiData
                for (let i = 0; i < allwenti.length; i++) {
                    for (let g = 0; g < allwenti[i].children.length; g++) {
                        for (let m = 0; m < allwenti[i].children[g].children.length; m++) {
                            for (let n = 0; n < allwenti[i].children[g].children[m].children.length; n++) {
                                if (allwenti[i].children[g].children[m].children[n].hproblemId === thisid) {
                                    thisparip = allwenti[i].children[g].switchIp
                                }
                            }
                        }
                    }
                }
                const list1 = []
                const problemIdList = []
                const allProIdList = []
                problemIdList.push(row.questionId)
                for (let i = 0; i < this.newArr.length; i++) {
                    const chaip = this.newArr[i]
                    if (chaip['ip'] === thisparip) {
                        for (let g = 0; g < problemIdList.length; g++) {
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x => JSON.stringify(x))
                const scanNum = this.num
                console.log(problemIdList)
                console.log(userinformation)
                return request({
                    url: '/sql/SolveProblemController/batchSolutionMultithreading/' + problemIdList + '/' + scanNum + '/' + allProIdList,
                    method: 'post',
                    data: userinformation
                }).then(response => {
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
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
            /**
             * 初始化ws
             */
            wsInit() {
                // var lockReconnect = false
                // var ws = null
                // var wsUrl = serverConfig.socketUrl
                // createWebSocket(wsUrl)

                // const wsuri = 'ws://192.168.1.98/dev-api/websocket/loophole'
                // const wsuri = `wss://${location.host}/dev-api/websocket/loophole${Cookies.get('usName')}`
                const wsuri = `ws://${location.host}/prod-api/websocket/loophole${Cookies.get('usName')}`
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
                    console.log('22222'+e.data)
                }else {
                    console.log(JSON.parse(e.data))
                    let newJson = this.changeTreeDate(JSON.parse(e.data), 'switchProblemVOList', 'children')
                    let newJson1 = this.changeTreeDate(newJson, 'switchProblemCOList', 'children')
                    console.log(newJson1)
                    //备份完整线程IP
                    const copyIpThred = newJson1[0].switchIp
                    console.log('线程ip' + copyIpThred)
                    //截取IP
                    const splitIp = newJson1[0].switchIp.split(':')[0]
                    console.log('截取' + splitIp)
                    newJson1[0].copyIpThred = copyIpThred
                    newJson1[0].switchIp = splitIp
                    console.log('==============================')
                    console.log(newJson1)
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
                        // this.newData.push(newJson1[0])
                    }
                    this.nowData = JSON.parse(JSON.stringify(this.newData))

                    const shu = this.nowData
                    //给ip添加loading
                    for (let i = 0; i < shu.length; i++) {
                        this.$set(shu[i], 'loading', true)
                        console.log(shu[i])
                    }
                    //合并IP、四条基本信息
                    for (let i = 0; i < shu.length; i++) {
                        var hebingInfo = '　' + shu[i].switchIp + ' ' + shu[i].showBasicInfo
                        // var hebingInfo = shu[i].switchIp + ' ' + shu[i].showBasicInfo
                        this.$set(shu[i], 'hebing', hebingInfo)
                    }
                    //参数+问题名 拼接展示
                    for (let i = 0; i < shu.length; i++) {
                        for (let g = 0; g < shu[i].children.length; g++) {
                            for (let m = 0; m < shu[i].children[g].children.length; m++) {
                                if (shu[i].children[g].children[m].valueInformationVOList.length > 0) {
                                    let mi1 = ''
                                    let mi2 = ''
                                    for (let n = 0; n < shu[i].children[g].children[m].valueInformationVOList.length; n++) {
                                        if (shu[i].children[g].children[m].valueInformationVOList[n].exhibit === '是') {
                                            mi1 = shu[i].children[g].children[m].valueInformationVOList[n].dynamicInformation
                                            mi2 = mi2 + ' ' + mi1
                                            console.log(mi2)
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
                    console.log(this.alljiao.allInfo)
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
            /**
             * ws关闭
             */
            wsCloseHanler(event) {
                console.log('正常的' + "+" + event.code + "+" + event.reason + "+" + event.wasClean)
                console.log(event, 'ws关闭')
                this.wsInit()
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

