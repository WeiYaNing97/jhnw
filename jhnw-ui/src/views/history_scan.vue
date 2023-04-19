<template>
  <div class="app-container">
<!--    <el-button type="primary" size="small" style="margin-bottom: 10px" @click="lishi">历史扫描</el-button>-->
<!--    <el-button type="primary" size="small" @click="exportDocx">生成报告</el-button>-->
<!--    <el-button type="primary" size="small" @click="printDel">打印报告</el-button>-->
    <!--    历史扫描-->
    <el-table v-loading="loading"
              :data="lishiData"
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
                     @click="xiufuone(scope.row)">修复</el-button>
          <el-button style="margin-left: 0" size="mini" type="text"
                     v-show="scope.row.switchIp != undefined && scope.row.noPro"
                     @click.stop="xiuallone(scope.row)">单台修复</el-button>
          <el-button style="margin-left: 0" type="warning" plain round
                     size="small" v-show="scope.row.createTime != undefined && scope.row.noPro"
                     @click.stop="huitimeyijian(scope.row)">一键修复</el-button>
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
                //

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
                lishiData:[],
                printData:[],
                loading:false,
                huisao:true,
                newArr:[],
                //详情内容
                particular:'',
                isExpansion:true,
            }
        },
        mounted:function(){
          this.lishi()
          this.getCook()
        },
        created(){
            // this.expandChange()
        },
        methods:{
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
            //回显历史扫描某次时间一键修复
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
            //历史单台一键修复
            xiuallone(row){
                // setInterval(this.lishi,5000)
                const thisip = row.switchIp
                const listAll = row.children
                console.log(row)
                const list1 = []
                const errIpList = []
                const problemIdList = []
                for(let i = 0;i<listAll.length;i++){
                    for (let g = 0;g<listAll[i].children.length;g++){
                        // problemIdList.push(listAll[i].children[g].questionId)
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
                    // if(chaip['ip'] === thisip){
                    //     for (let g = 0;g<problemIdList.length;g++){
                    //         list1.push(chaip)
                    //     }
                    // }
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
            //历史修复单个问题
            xiufuone(row){
                console.log(row)
                const thisid = row.hproblemId
                let thisparip = ''
                const allwenti = this.lishiData
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
            //历史扫描
            lishi(){
                this.lishifu = true
                this.chuci = false
                this.huisao = true
                return request({
                    url:'/sql/SolveProblemController/getUnresolvedProblemInformationByUserName',
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
                    this.lishiData = response
                    const jiaid = this.lishiData
                    const jiaid1 = this.lishiData
                    //合并信息
                    for(let i = 0;i<jiaid.length;i++){
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            // var beforeJieData = jiaid[i].children[g].switchIp
                            // var plcaeJie =  beforeJieData.indexOf(':')
                            // var afterJie = beforeJieData.substring(0,plcaeJie)
                            var hebingInfo = jiaid[i].children[g].switchIp + ' ' + jiaid[i].children[g].showBasicInfo
                            this.$set(jiaid[i].children[g],'hebing',hebingInfo)
                        }
                    }
                    //修改ip

                    // console.log(row)
                    // const thisid = row.hproblemId
                    // let thisparip = ''
                    // const allwenti = this.lishiData
                    // for(let i = 0;i<allwenti.length;i++){
                    //     for (let g = 0;g<allwenti[i].children.length;g++){
                    //         for (let m = 0;m<allwenti[i].children[g].children.length;m++){
                    //             for (let n = 0;n<allwenti[i].children[g].children[m].children.length;n++){
                    //                 console.log(allwenti[i].children[g].children[m].children[n].hproblemId)
                    //                 if (allwenti[i].children[g].children[m].children[n].hproblemId === thisid){
                    //                     thisparip = allwenti[i].children[g].switchIp

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
