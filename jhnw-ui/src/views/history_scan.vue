<template>
  <div class="app-container">
<!--    <el-button type="primary" size="small" style="margin-bottom: 10px" @click="lishi">历史扫描</el-button>-->
<!--    <el-button type="primary" size="small" @click="exportDocx">生成报告</el-button>-->
<!--    <el-button type="primary" size="small" @click="printDel">打印报告</el-button>-->
    <div class="block">
      <el-pagination
        layout="prev, pager, next"
        :total="allPage" @current-change="goPage">
      </el-pagination>
    </div>
    <!--    历史扫描-->
    <el-table v-loading="loading"
              :data="historyData"
              ref="tree"
              v-show="huisao"
              style="width: 100%"
              row-key="hproblemId"
              :cell-style="hongse"
              @row-click='expandChange'
              :tree-props="{children: 'children',hasChildren: 'hasChildren'}"
              :span-method="arraySpanMethodTwo">
      <el-table-column prop="createTime" label="扫描时间" width="120"></el-table-column>
      <!--      <el-table-column prop="switchIp" label="主机" width="130"></el-table-column>-->
      <!--      <el-table-column prop="showBasicInfo" label="基本信息" width="200"></el-table-column>-->
      <el-table-column prop="hebing" label="主机(基本信息)" width="200"></el-table-column>
      <el-table-column prop="typeProblem" label="分类" width="120"></el-table-column>
      <el-table-column prop="problemName" label="问题" width="300"></el-table-column>
      <el-table-column prop="ifQuestion" label="是否异常"></el-table-column>
      <el-table-column prop="solve" label="操作">
        <template slot-scope="scope">
          <el-button size="mini"
                     type="text"
                     icon="el-icon-edit"
                     v-show="scope.row.ifQuestion==='异常'"
                     @click="repairSwitch(scope.row,'proRepair')">修复</el-button>
          <el-button style="margin-left: 0" size="mini" type="text"
                     v-show="scope.row.switchIp != undefined && scope.row.noPro"
                     @click.stop="repairSwitch(scope.row,'singleRepair')">单台修复</el-button>
          <el-button style="margin-left: 0" type="warning" plain round
                     size="small" v-show="scope.row.createTime != undefined && scope.row.noPro"
                     @click.stop="repairSwitch(scope.row,'allRepair')">一键修复</el-button>
          <el-button style="margin-left: 0" type="success" plain round
                     size="small" v-show="scope.row.createTime != undefined && !scope.row.noPro"
                     @click.stop="allTrue(scope.row)">全部正常</el-button>
          <el-button style="margin-left: 10px" type="info" plain round
                     size="small" v-show="scope.row.createTime != undefined"
                     @click.stop="printBaogao(scope.row)" v-print="printTxt">打印报告</el-button>
          <el-button size="mini" type="text" icon="el-icon-view"
                     v-show="scope.row.hasOwnProperty('problemDescribeId')"
                     @click="xiangqing(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

<!--    <div id="hisTxt" style="opacity: 0;position:absolute">-->
      <div id="hisTxt" ref="pdf" v-if="printShow" style="margin: 0">
<!--          <div style="text-align:center"><h4>扫描报告</h4></div>-->
<!--          <span>扫描人:{{ this.usName }}</span>-->
<!--          <span style="padding-left: 10px">打印时间:{{ this.nowTime }}</span>-->
          <el-table v-loading="loading"
                    :data="printData"
                    ref="tree"
                    v-show="huisao"
                    style="width: 100%"
                    row-key="hproblemId"
                    :cell-style="hongse"
                    @row-click='expandChange'
                    :default-expand-all='isExpansion'
                    :tree-props="{children: 'children',hasChildren: 'hasChildren'}"
                    :span-method="arraySpanMethodTwo">
            <el-table-column prop="createTime" label="扫描时间" width="120"></el-table-column>
            <!--      <el-table-column prop="switchIp" label="主机" width="130"></el-table-column>-->
            <!--      <el-table-column prop="showBasicInfo" label="基本信息" width="200"></el-table-column>-->
            <el-table-column prop="hebing" label="主机(基本信息)" width="200"></el-table-column>
            <el-table-column prop="typeProblem" label="分类" width="120"></el-table-column>
            <el-table-column prop="problemName" label="问题" width="300"></el-table-column>
            <el-table-column prop="ifQuestion" label="是否异常"></el-table-column>
          </el-table>
      </div>
    </div>
</template>

<script>
    import request from '@/utils/request'
    import TinymceEditor from "@/components/Tinymce/TinymceEditor"
    import { ExportBriefDataDocx } from '@/utils/exportBriefDataDocx'
    import Cookies from 'js-cookie'
    import { downloadPDF } from "../utils/pdf"
    export default {
        name: "History_scan",
        inject:["reload"],
        props:{
          title:{
              type:String,
              default:Cookies.get('usName') + " 用户扫描报告",
          }
        },
        data(){
            return{
                //总页数
                allPage:0,
                //当前页数
                pageNumber:1,
                nowTime:'',
                printShow:false,
                printTxt:{
                  id:'hisTxt',
                  // extraHead:'我是head' + this.nowTime,
                  popTitle:this.title,
                  repeatTableHeader:true,
                  targetStyles:['*'],
                  preview:false,
                  closeCallback(){
                      console.log('打印窗口已经关闭')
                  },
                },
                //报告
                docxData:{
                    tableData:[],
                    year:'',
                    month:''
                },
                historyData:[],
                printData:[],
                loading:false,
                huisao:true,
                newArr:[],
                //详情内容
                particular:'',
                isExpansion:true,
            }
        },
        mounted(){
            this.lishi()
            this.getCook()
            this.allPageNums()
        },
        created(){
            // this.expandChange()
        },
        methods:{
            //跳转到第几页
            goPage(e){
                this.pageNumber = e
                console.log(this.pageNumber)
                this.lishi()
            },
            //获取总页数
            allPageNums(){
                return request({
                    url:'/share/switch_scan_result/getPages',
                    method:'get',
                }).then(response=>{
                    this.allPage  = response*10
                })
            },
            noShow(){
                this.printShow = false
            },
            getCook(){
                // this.usName = Cookies.get('usName')
                // console.log(this.usName)
            },
            //打印报告
            printBaogao(row){
                this.printShow = true
                setTimeout(()=>{
                    downloadPDF(this.$refs.pdf)
                },0)
                this.nowTime = new Date().toLocaleDateString()
                this.printData = []
                this.printData.push(row)
                setTimeout(this.noShow,5000)
            },
            //导出
            exportDocx(){
                downloadPDF(this.$refs.pdf)

                // console.log('导出');
                // this.docxData.tableData = this.lishiData
                // this.docxData.year = 2022
                // this.docxData.month = 9
                // // ExportBriefDataDocx 是我导入的一个文件，里边写的是导出文本的核心代码
                // ExportBriefDataDocx('/报告test.docx', this.docxData, '导出的.docx')
                // ExportBriefDataDocx('/text.docx', this.docxData, '文档导出.docx') // text.docx放在了根目录下的public文件夹下
            },
            // sessionStorage.setItem('','ok'),
            // window.sessionStorage.setItem('','ok'),
            // 展开折叠当前列表
            expandChange(row){
                this.$refs.tree.toggleRowExpansion(row)
            },
            //异常红色
            hongse(row,column){
                let reds = {
                    'color':'red'
                }
                let greens = {
                    'color':'#008080'
                }
                if(row.column.label === '是否异常'){
                    if (row.row.ifQuestion == '异常'){
                        return reds
                    }else if (row.row.ifQuestion == '已解决'){
                        return greens
                    }
                }
            },
            //查看详情
            xiangqing(row){
                this.dialogVisible = true
                console.log(row)
                const xiangid = row.problemDescribeId
                console.log(xiangid)
                return request({
                    url:`/sql/problem_describe/selectProblemDescribe?id=${xiangid}`,
                    method:'get',
                }).then(response=>{
                    console.log(response.problemDescribe)
                    this.particular = response.problemDescribe
                    // this.proxiang = response.problemDescribe
                    // this.$refs.abc.geizi()
                })
            },
            //
            allTrue(){
                this.$message.success('此按钮仅供展示，无作用!')
            },
            //修复整合
            repairSwitch(row,type){
                let userinformation = []
                let userinformationCopy = []
                let problemIdList = []
                let allProIdList = []
                let errIpList = []
                let scanNum = 1
                if (type == 'singleRepair'){
                    //单台修复
                    console.log('单台修复')
                    row.children.forEach(item => {
                        item.children.forEach(child => {
                            if (child.ifQuestion == '异常'){
                                problemIdList.push(child.questionId)
                                errIpList.push(row.switchIp)
                            }
                        })
                    })
                }
                else if (type == 'proRepair'){
                    //单个问题修复
                    console.log('单个问题修复')
                    problemIdList.push(row.questionId)
                    this.historyData.forEach(item => {
                        item.children.forEach(child => {
                            child.children.forEach(innerChild =>{
                                if (innerChild.children.find(node => node.hproblemId == row.hproblemId)){
                                    errIpList.push(child.switchIp)
                                }
                            })
                        })
                    })
                }
                else if (type == 'allRepair'){
                    //一键修复
                    console.log('一键修复')
                    row.children.forEach(item => {
                        item.children.forEach(child => {
                            child.children.forEach(innerChild => {
                                if (innerChild.ifQuestion == '异常'){
                                    problemIdList.push(innerChild.questionId)
                                    errIpList.push(item.switchIp)
                                }
                            })
                        })
                    })
                }
                userinformationCopy = errIpList.flatMap(ip => this.newArr
                    .filter(chaip => chaip['ip'] == ip).map(x => JSON.stringify(x)))
                //去掉线程名，只保留ip
                userinformation = userinformationCopy.map(jsonString => {
                    const obj = JSON.parse(jsonString)
                    const ipValue = obj.ip.split(':')[0]
                    obj.ip = ipValue
                    return JSON.stringify(obj)
                })
                console.log(userinformation)
                console.log(problemIdList)
                return request({
                    // url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum+'/'+allProIdList,
                    // url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log(response)
                })
            },
            //历史扫描某次时间一键修复
            huitimeyijian(row){
                const problemIdList = []
                //异常所对应的ip集合
                const iplist = []
                const yijian = row.children
                console.log(yijian)
                for (let i = 0;i<yijian.length;i++){
                    for (let g = 0;g<yijian[i].children.length;g++){
                        for (let m = 0;m<yijian[i].children[g].children.length;m++){
                            //3.16修改逻辑
                            if (yijian[i].children[g].children[m].ifQuestion == '异常'){
                                problemIdList.push(yijian[i].children[g].children[m].questionId)
                                iplist.push(yijian[i]['switchIp'])
                            }
                        }
                    }
                }
                const list1 = []
                for(let i = 0;i<this.newArr.length;i++){
                    const chaip = this.newArr[i]
                    for(let g = 0;g<iplist.length;g++){
                        if(chaip['ip'] === iplist[g]){
                            list1.push(chaip)
                        }
                    }
                }
                const allProIdList = []
                const userinformation = list1.map(x=>JSON.stringify(x))
                const scanNum = this.num
                // const scanNum = 1
                console.log(userinformation)
                console.log(problemIdList)
                console.log(allProIdList)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum+'/'+allProIdList,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    this.$message.success('修复请求以提交!')
                    console.log('成功')
                })
            },
            //历史修复单个问题
            xiufuone(row){
                console.log(row)
                const thisid = row.hproblemId
                let thisparip = ''
                const allwenti = this.historyData
                for(let i = 0;i<allwenti.length;i++){
                    for (let g = 0;g<allwenti[i].children.length;g++){
                        for (let m = 0;m<allwenti[i].children[g].children.length;m++){
                            for (let n = 0;n<allwenti[i].children[g].children[m].children.length;n++){
                                // console.log(allwenti[i].children[g].children[m].children[n].hproblemId)
                                if (allwenti[i].children[g].children[m].children[n].hproblemId === thisid){
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
                for (let i = 0;i<this.newArr.length;i++){
                    const chaip = this.newArr[i]
                    if (chaip['ip'] === thisparip){
                        for (let g=0;g<problemIdList.length;g++){
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x=>JSON.stringify(x))
                // const scanNum = this.num
                const scanNum = 1
                console.log(problemIdList)
                console.log(userinformation)
                console.log(allProIdList)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum+'/'+allProIdList,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            //历史单台一键修复
            xiuallone(row){
                const thisip = row.switchIp
                const listAll = row.children
                console.log(row)
                const list1 = []
                const errIpList = []
                const problemIdList = []
                for(let i = 0;i<listAll.length;i++){
                    for (let g = 0;g<listAll[i].children.length;g++){
                        //3.16修改逻辑
                        if (listAll[i].children[g].ifQuestion == '异常'){
                            problemIdList.push(listAll[i].children[g].questionId)
                            errIpList.push(row.switchIp)
                        }
                    }
                }
                for (let i = 0;i<this.newArr.length;i++){
                    const chaip = this.newArr[i]
                    for(let g = 0;g<errIpList.length;g++){
                        if (chaip['ip'] == errIpList[g]){
                            list1.push(chaip)
                        }
                    }
                }
                const allProIdList = []
                const userinformation = list1.map(x=>JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                console.log(allProIdList)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum+'/'+allProIdList,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            //历史扫描合并列
            arraySpanMethodTwo({ row, column, rowIndex, columnIndex }) {
                if(row.createTime != null){
                    if (columnIndex === 0) {
                        return [1, 2];
                    } else if (columnIndex === 1) {
                        return [0, 0];
                    }
                }
                if (row.hebing != null){
                    if (columnIndex === 1) {
                        return [1, 2];
                    } else if (columnIndex === 2) {
                        return [0, 0];
                    }
                }
            },
            //历史扫描--备份
            lishiNew(){
                this.huisao = true
                return request({
                    url:'/share/switch_scan_result/getUnresolvedProblemInformationByUserName/' + this.pageNumber,
                    method:'get'
                }).then(response=>{
                    function changeTreeDate(arrayJsonObj,oldKey,newKey) {
                        let strtest = JSON.stringify(arrayJsonObj);
                        let reg = new RegExp(oldKey,'g');
                        let newStr = strtest.replace(reg,newKey);
                        return JSON.parse(newStr);
                    }
                    response = changeTreeDate(response,'scanResultsVOList','children')
                    response = changeTreeDate(response,'switchProblemVOList','children')
                    response = changeTreeDate(response,'switchProblemCOList','children')
                    this.historyData = response
                    const jiaid = this.historyData
                    const jiaid1 = this.historyData
                    //合并信息
                    for(let i = 0;i<jiaid.length;i++){
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            // var beforeJieData = jiaid[i].children[g].switchIp
                            // var plcaeJie =  beforeJieData.indexOf(':')
                            // var afterJie = beforeJieData.substring(0,plcaeJie)
                            var hebingInfo = jiaid[i].children[g].switchIp.split(':')[0] + ' ' + jiaid[i].children[g].showBasicInfo
                            this.$set(jiaid[i].children[g],'hebing',hebingInfo)
                        }
                    }

                    //返回数据添加hproblemId
                    for(let i = 0;i<jiaid.length;i++){
                        this.$set(jiaid[i],'hproblemId',Math.floor(Math.random() * (999999999999999 - 1) + 1))
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            this.$set(jiaid[i].children[g],'hproblemId',Math.floor(Math.random() * (999999999999999 - 1) + 1))
                            for (let m = 0;m<jiaid[i].children[g].children.length;m++){
                                this.$set(jiaid[i].children[g].children[m],'hproblemId',Math.floor(Math.random() * (999999999999999 - 1) + 1))
                                for (let n = 0;n<jiaid[i].children[g].children[m].children.length;n++){
                                    this.$set(jiaid[i].children[g].children[m].children[n],'hproblemId',Math.floor(Math.random() * (999999999999999 - 1) + 1))
                                    this.$set(jiaid[i].children[g].children[m].children[n],'createTime',null)
                                    //查找是否有问题
                                    if (jiaid[i].children[g].children[m].children[n].ifQuestion == '异常'){
                                        this.$set(jiaid[i].children[g],'noPro',true)
                                        this.$set(jiaid[i],'noPro',true)
                                    }
                                }
                            }
                        }
                    }
                    //历史扫描问题添加用户名
                    for (let i = 0;i<jiaid.length;i++){
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            for (let m = 0;m<jiaid[i].children[g].children.length;m++){
                                for (let n = 0;n<jiaid[i].children[g].children[m].children.length;n++){
                                    //7.14修改历史扫描
                                    if (jiaid[i].children[g].children[m].typeProblem == "高级功能"){
                                        let str = jiaid[i].children[g].children[m].children[n].dynamicInformation
                                        let startStr = "=:=是=:="
                                        let endStr = "=:="
                                        var startIndex = str.indexOf(startStr) + 1
                                        var endIndex = str.indexOf(endStr)
                                        var result = str.substring(startIndex, endIndex)
                                        console.log(result)
                                    }

                                    if (jiaid[i].children[g].children[m].children[n].valueInformationVOList.length>0){
                                        let yongone = ''
                                        let endyong = ''
                                        for (let k = 0;k<jiaid[i].children[g].children[m].children[n].valueInformationVOList.length;k++){
                                            if (jiaid[i].children[g].children[m].children[n].valueInformationVOList[k].exhibit === '是'){
                                                yongone = jiaid[i].children[g].children[m].children[n].valueInformationVOList[k].dynamicInformation
                                                endyong = endyong + ' ' +yongone
                                            }
                                        }
                                        const wenti = jiaid[i].children[g].children[m].children[n].problemName
                                        const zuihou = endyong +" "+ wenti
                                        this.$set(jiaid[i].children[g].children[m].children[n],'problemName',zuihou)
                                    }
                                }
                            }
                        }
                    }
                    //获取返回ip、用户名、密码五条信息,添加了配置密码
                    const allxinxi = []
                    for(let i = 0;i<jiaid.length;i++){
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            for (let m = 0;m<jiaid[i].children[g].children.length;m++){
                                const allinfo = {}
                                if(jiaid[i].children[g].children[m].switchName != undefined){
                                    this.$set(allinfo,'ip',jiaid[i].children[g].switchIp)
                                    this.$set(allinfo,'name',jiaid[i].children[g].children[m].switchName)
                                    this.$set(allinfo,'password',jiaid[i].children[g].children[m].switchPassword)
                                    this.$set(allinfo,'mode',jiaid[i].children[g].children[m].loginMethod)
                                    this.$set(allinfo,'port',jiaid[i].children[g].children[m].portNumber)
                                    this.$set(allinfo,'configureCiphers',jiaid[i].children[g].children[m].configureCiphers)
                                    allxinxi.push(allinfo)
                                    break
                                }
                            }
                        }
                    }
                    //所有交换机信息去重
                    let lishiobj = {}
                    for(let i = 0;i<allxinxi.length;i++){
                        if (!lishiobj[allxinxi[i].ip]){
                            this.newArr.push(allxinxi[i])
                            lishiobj[allxinxi[i].ip] = true
                        }
                    }
                    console.log(this.newArr)
                    console.log(jiaid)
                })
            },
            //历史扫描
            lishi(){
                this.huisao = true
                return request({
                    url:'/share/switch_scan_result/getUnresolvedProblemInformationByUserName/' + this.pageNumber,
                    method:'get'
                }).then(response=>{
                    // 递归函数，用于替换键名
                    function replaceKeys(obj){
                        if (typeof obj !== 'object' || obj === null){
                            return obj
                        }
                        for (let key in obj){
                            if (key === 'scanResultsVOList' || key === 'switchProblemVOList' || key === 'switchProblemCOList'){
                                obj['children'] = replaceKeys(obj[key])
                                delete obj[key]
                            }else {
                                // 递归处理嵌套对象
                                obj[key] = replaceKeys(obj[key])
                            }
                        }
                        return obj
                    }
                    //替换键名
                    this.historyData = replaceKeys(response)
                    let jiaid = this.historyData
                    //优化后
                    for (let i = 0; i < jiaid.length; i++) {
                        for (let j = 0; j < jiaid[i].children.length; j++) {
                            // 合并信息,使用解构赋值简化代码
                            let { switchIp, showBasicInfo } = jiaid[i].children[j]
                            let hebingInfo = switchIp.split(':')[0] + ' ' + showBasicInfo
                            this.$set(jiaid[i].children[j], 'hebing', hebingInfo)
                            for (let k = 0; k < jiaid[i].children[j].children.length; k++) {
                                for (let l = 0; l < jiaid[i].children[j].children[k].children.length; l++) {
                                    this.$set(jiaid[i].children[j].children[k].children[l],'createTime',null)
                                    //查找是否有问题
                                    if (jiaid[i].children[j].children[k].children[l].ifQuestion == '异常'){
                                        this.$set(jiaid[i].children[j],'noPro',true)
                                        this.$set(jiaid[i],'noPro',true)
                                    }
                                    ///////////历史扫描问题添加用户名
                                    //7.14修改历史扫描
                                    if (jiaid[i].children[j].children[k].typeProblem == "高级功能"){
                                        let str = jiaid[i].children[j].children[k].children[l].dynamicInformation
                                        let startStr = "=:=是=:="
                                        let endStr = "=:="
                                        var startIndex = str.indexOf(startStr) + 1
                                        var endIndex = str.indexOf(endStr)
                                        var result = str.substring(startIndex, endIndex)
                                        console.log(result)
                                    }
                                    if (jiaid[i].children[j].children[k].children[l].valueInformationVOList.length>0){
                                        let yongone = ''
                                        let endyong = ''
                                        for (let m = 0;m<jiaid[i].children[j].children[k].children[l].valueInformationVOList.length;m++){
                                            if (jiaid[i].children[j].children[k].children[l].valueInformationVOList[m].exhibit === '是'){
                                                yongone = jiaid[i].children[j].children[k].children[l].valueInformationVOList[m].dynamicInformation
                                                endyong = endyong + ' ' +yongone
                                            }
                                        }
                                        const wenti = jiaid[i].children[j].children[k].children[l].problemName
                                        const zuihou = endyong +" "+ wenti
                                        this.$set(jiaid[i].children[j].children[k].children[l],'problemName',zuihou)
                                    }
                                }
                            }
                        }
                    }
                    // 递归 给每个节点添加 hproblemId
                    function addHproblemId(node) {
                        this.$set(node, 'hproblemId', Math.floor(Math.random() * (999999999999999 - 1) + 1));
                        // 递归处理子节点
                        if (node.children && node.children.length > 0) {
                            for (let i = 0; i < node.children.length; i++) {
                                addHproblemId.call(this, node.children[i]);
                            }
                        }
                    }
                    // 遍历 jiaid 数组，调用递归函数处理每个节点
                    for (let i = 0; i < jiaid.length; i++) {
                        addHproblemId.call(this, jiaid[i]);
                    }
                    //获取返回ip、用户名、密码五条信息,添加了配置密码
                    const allxinxi = []
                    for(let i = 0;i<jiaid.length;i++){
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            for (let m = 0;m<jiaid[i].children[g].children.length;m++){
                                const allinfo = {}
                                if(jiaid[i].children[g].children[m].switchName != undefined){
                                    this.$set(allinfo,'ip',jiaid[i].children[g].switchIp)
                                    this.$set(allinfo,'name',jiaid[i].children[g].children[m].switchName)
                                    this.$set(allinfo,'password',jiaid[i].children[g].children[m].switchPassword)
                                    this.$set(allinfo,'mode',jiaid[i].children[g].children[m].loginMethod)
                                    this.$set(allinfo,'port',jiaid[i].children[g].children[m].portNumber)
                                    this.$set(allinfo,'configureCiphers',jiaid[i].children[g].children[m].configureCiphers)
                                    allxinxi.push(allinfo)
                                    break
                                }
                            }
                        }
                    }
                    //所有交换机信息去重
                    let lishiobj = {}
                    for(let i = 0;i<allxinxi.length;i++){
                        if (!lishiobj[allxinxi[i].ip]){
                            this.newArr.push(allxinxi[i])
                            lishiobj[allxinxi[i].ip] = true
                        }
                    }
                    console.log(this.newArr)
                    console.log(jiaid)
                })
            },
        }
    }
</script>

<style scoped>
  >>> .el-table td.el-table__cell{
    border-bottom: none !important;
  }
  @page{
    size: A4 portrait;
  }
  #hisTxt{
    page-break-after: avoid;
    page-break-after: avoid;
  }
</style>
<style media="print">
@media print {
  html{
    zoom: 80%;
  }
}
</style>
