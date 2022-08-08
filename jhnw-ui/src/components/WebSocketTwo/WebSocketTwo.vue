<template>
  <div>
<!--    tableDataq  testData-->
    <el-button type="success" size="small" @click="allxiu" v-show="chuci">一键修复</el-button>
    <el-button type="primary" size="small" @click="lishi">历史扫描</el-button>
    <el-button type="primary" size="small" @click="wenben">测试按钮</el-button>
    <el-input type="textarea" v-model="wenbenben"></el-input>

<!--    <el-button type="primary" size="small" @click="wutiao">五条</el-button>-->
    <el-table v-loading="loading"
              :data="tableDataqq"
              ref="tree"
              v-show="chuci"
              style="width: 100%;margin-bottom: 20px;"
              row-key="hproblemId"
              :cell-style="hongse"
              default-expand-all
              :tree-props="{children: 'children',hasChildren: 'hasChildren'}">
      <el-table-column prop="switchIp" label="主机"></el-table-column>
      <el-table-column prop="typeProblem" label="问题类型"></el-table-column>
      <el-table-column prop="problemName" label="问题" ></el-table-column>
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
                     @click="xiuall(scope.row)">单台修复</el-button>
          <el-button size="mini" type="text" icon="el-icon-view"
                     v-show="scope.row.hasOwnProperty('problemDescribeId')"
                     @click="xiangqing(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-table v-loading="loading"
              :data="tableDataq"
              ref="tree"
              v-show="huisao"
              style="width: 100%;margin-bottom: 20px;"
              row-key="hproblemId"
              default-expand-all
              :tree-props="{children: 'children',hasChildren: 'hasChildren'}">
      <el-table-column prop="createTime" label="扫描时间" min-width="100px"></el-table-column>
      <el-table-column prop="switchIp" label="主机"></el-table-column>
      <el-table-column prop="typeProblem" label="问题类型"></el-table-column>
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
                     v-show="scope.row.hasOwnProperty('switchIp')&&!scope.row.hasOwnProperty('typeProblem')&&!scope.row.hasOwnProperty('createTime')"
                     @click="xiuallone(scope.row)">单台修复</el-button>
          <el-button style="margin-left: 0" type="success" plain round
            size="small" v-show="scope.row.createTime != undefined"
                     @click="huitimeyijian(scope.row)">一键修复</el-button>
          <el-button size="mini" type="text" icon="el-icon-view"
                     v-show="scope.row.hasOwnProperty('problemDescribeId')"
                     @click="xiangqing(scope.row)">详情</el-button>
        </template>
      </el-table-column>
      <!--      <el-table-column prop="planQuantity"  label="用户名"></el-table-column>-->
    </el-table>

<!--    查看详情-->
    <el-dialog
      title="问题详情以及解决办法"
      :visible.sync="dialogVisible"
      width="50%"
      :before-close="handleClose">
      <TinymceEditor></TinymceEditor>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>

<!--    <el-button @click="aaa">第二个</el-button>-->
<!--    <el-button @click="zizujian">看子组件</el-button>-->
  </div>
</template>

<script>
    import log from "../../views/monitor/job/log";
    import axios from 'axios'
    import request from '@/utils/request'
    import Cookies from "js-cookie"
    import TinymceEditor from "@/components/Tinymce/TinymceEditor"
    export default {
        name: "WebSocketTwo",
        components:{
            TinymceEditor
        },
        props:{
            queryParams:'',
            num:'',
            alljiao:{
                allInfo:[

                ]
            },
            forms:{

            }
        },
        data() {
            return {
                //
                wenbenben:'',
                dialogVisible:false,
                //后台回显所有交换机信息
                newArr:[],
                chuci:true,
                huisao:false,
                // ws是否启动
                wsIsRun: false,
                // 定义ws对象
                webSocket: null,
                // ws请求链接（类似于ws后台地址）
                ws: '',
                // ws定时器
                wsTimer: null,
                testData:[],
                tableDataq:[],
                tableDataqq: [
                    {
                        switchIp:'192.168.1.100',
                        hproblemId:13462523456,
                        children:[
                            {
                            switchIp:null,
                            switchName:'admin',
                            switchPassword:'admin',
                            typeProblem: '安全配置',
                            hproblemId:12345671,
                            children: [
                                {
                                    problemName:'密码明文存储',
                                    problemDescribeId:2,
                                    ifQuestion:'异常',
                                    hproblemId:12345711,
                                    questionId:1,
                                    comId:'1653277339109',
                                    valueId:1,
                                    valueInformationVOList:[
                                        {
                                            hproblemId:11123462567111,
                                            dynamicVname:'用户名',
                                            dynamicInformation:'admin'
                                        }
                                    ]
                                },
                                {
                                    problemName:'telnet开启',
                                    ifQuestion:'安全',
                                    problemDescribeId:3,
                                    hproblemId:11423511,
                                    questionId:12,
                                    comId:'1653277229109',
                                    valueId:1,
                                    valueInformationVOList:[
                                        {
                                            hproblemId:1157155555461111,
                                            dynamicVname:'用户名',
                                            dynamicInformation:'admin1'
                                        }
                                    ]
                                }
                            ]
                        },
                            {
                                switchIp:null,
                                switchName:'admin',
                                switchPassword:'admin',
                                typeProblem: '设备缺陷',
                                hproblemId:1135242,
                                children: [
                                    {
                                        problemName:'没有配置管理地址',
                                        ifQuestion:'异常',
                                        hproblemId:113456771342,
                                        questionId:112,
                                        comId:'1653277339101',
                                        valueId:1,
                                        valueInformationVOList:[
                                            {
                                                hproblemId:11113456753411,
                                                dynamicVname:'用户名',
                                                dynamicInformation:'admin2'
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        switchIp:'192.168.1.1',
                        hproblemId:212543,
                        children:[{
                            switchIp:null,
                            switchName:'admin11',
                            switchPassword:'admin',
                            typeProblem: '安全配置',
                            hproblemId:22356472,
                            children: [
                                {
                                    problemName:'密码明文存储',
                                    ifQuestion:'无问题',
                                    hproblemId:254676522,
                                    questionId:10,
                                    valueInformationVOList:[
                                        {
                                            hproblemId:1134523541111,
                                            dynamicVname:'用户名',
                                            dynamicInformation:'admin2'
                                        }
                                    ]
                                }
                            ]
                        },
                        ]
                    }
                ],
                loading:false,
                formLabelWidth:'50px',
                xiuform:{
                    xiala:''
                },
                depss:[]
            }
        },
        async mounted() {
            this.wsIsRun = true
            this.wsInit()
        },
        created(){
            const usname = Cookies.get('usName')
        },
        methods: {
            //测试总按钮
            wenben(){
                console.log(this.num)
                console.log(this.wenbenben)
            },
            //异常红色
            hongse(row,column){
                let reds = {
                    'color':'red'
                }
                if(row.column.label === '是否异常'){
                    if (row.row.ifQuestion === '异常'){
                        return reds
                    }
                }
                // if (row.row.ifQuestion === '异常'){
                //     return reds
                // }
            },
            //查看详情
            xiangqing(row){
                const xiangid = row.problemDescribeId
                console.log(xiangid)
                this.dialogVisible = true
                console.log(row)
            },
            handleClose(done) {
                this.$confirm('确认关闭？')
                    .then(_ => {
                        done();
                    })
                    .catch(_ => {});
            },
            //五条信息
            // wutiao(){
            //     //测试获取交换机信息testData
            //     const jiaoxinxi = this.tableDataq
            //     const allwutiao = []
            //     for(let i = 0;i<jiaoxinxi.length;i++){
            //         for (let g = 0;g<jiaoxinxi[i].children.length;g++){
            //             const newwutiao = {}
            //             if(jiaoxinxi[i].children[g].switchName != undefined){
            //                 this.$set(newwutiao,'ip',jiaoxinxi[i].children[g].switchIp)
            //                 this.$set(newwutiao,'name',jiaoxinxi[i].children[g].switchName)
            //                 this.$set(newwutiao,'password',jiaoxinxi[i].children[g].switchPassword)
            //                 allwutiao.push(newwutiao)
            //                 break
            //             }
            //         }
            //     }
            //     console.log(allwutiao)
            // },
            //历史扫描
            lishi(){
                console.log('我是历史')
                this.chuci = false
                this.huisao = true
                return request({
                    url:'/sql/SolveProblemController/getUnresolvedProblemInformationByUserName',
                    method:'post',
                }).then(response=>{
                    console.log(response)
                    function changeTreeDate(arrayJsonObj,oldKey,newKey) {
                        let strtest = JSON.stringify(arrayJsonObj);
                        let reg = new RegExp(oldKey,'g');
                        let newStr = strtest.replace(reg,newKey);
                        return JSON.parse(newStr);
                    }
                    let newJson = changeTreeDate(response,'switchProblemVOList','children');
                    let newJson1 = changeTreeDate(newJson,'switchProblemCOList','children')
                    this.tableDataq = newJson1
                    const jiaid = this.tableDataq
                    //返回数据添加hproblemId
                    for(let i = 0;i<jiaid.length;i++){
                        this.$set(jiaid[i],'hproblemId',Math.floor(Math.random() * (999999999999999 - 1) + 1))
                    }
                    //测试对象加数组
                    for(let i = 0;i<jiaid.length;i++){
                        const waic = []
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            let yic = {}
                            let chi = []
                            chi.push(jiaid[i].children[g])
                            this.$set(yic,'children',chi)
                            this.$set(yic,'switchIp',jiaid[i].children[g].switchIp)
                            this.$set(jiaid[i].children[g],'switchIp',undefined)
                            this.$set(yic,'hproblemId',Math.floor(Math.random() * (999999999999999 - 1) + 1))
                            waic.push(yic)
                        }
                        this.$set(jiaid[i],'children',waic)
                    }
                    //回显问题添加用户名
                    for (let i = 0;i<jiaid.length;i++){
                        for (let g = 0;g<jiaid[i].children.length;g++){
                            for (let m = 0;m<jiaid[i].children[g].children.length;m++){
                                for (let n = 0;n<jiaid[i].children[g].children[m].children.length;n++){
                                    if (jiaid[i].children[g].children[m].children[n].valueInformationVOList.length>0){
                                        const yonghu = jiaid[i].children[g].children[m].children[n].valueInformationVOList[0].dynamicInformation
                                        const wenti = jiaid[i].children[g].children[m].children[n].problemName
                                        const zuihou = yonghu +" "+ wenti
                                        this.$set(jiaid[i].children[g].children[m].children[n],'problemName',zuihou)
                                    }
                                }
                            }
                        }
                    }
                    //获取返回ip、用户名、密码等
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
                                    allxinxi.push(allinfo)
                                    break
                                }
                            }
                        }
                    }
                    //所有交换机信息去重
                    // let newArr = []
                    let newObj = {}
                    for(let i = 0;i<allxinxi.length;i++){
                        if(!newObj[allxinxi[i].ip]){
                            this.newArr.push(allxinxi[i])
                            newObj[allxinxi[i].ip] = true
                        }
                    }
                    console.log(this.newArr)
                    console.log(jiaid)
                })
            },
            // zizujian(){
            //   // alert(JSON.stringify(this.forms.dynamicItem))
            //     alert(this.alljiao.allInfo)
            // },
            //笨方法
            // aaa(){
            //     const shu = this.tableDataq
            //     console.log('点击')
            //     for (let i=0;i<shu.length;i++){
            //         for (let g=0;g<shu[i].children.length;g++){
            //             for (let m=0;m<shu[i].children[g].children.length;m++){
            //                 if (shu[i].children[g].children[m].hasOwnProperty('valueInformationVOList')===true){
            //                     const yonghu = shu[i].children[g].children[m].valueInformationVOList[0].dynamicInformation
            //                     const wenti = shu[i].children[g].children[m].problemName
            //                     const zuihou = yonghu +" "+ wenti
            //                     this.$set(shu[i].children[g].children[m],'problemName',zuihou)
            //                 }
            //             }
            //         }
            //     }
            // },
            //回显历史扫描某次时间一键修复
            huitimeyijian(row){
                const problemIdList = []
                const iplist = []
                const yijian = row.children
                console.log(yijian)
                for (let i = 0;i<yijian.length;i++){
                    for (let g = 0;g<yijian[i].children.length;g++){
                        for (let m = 0;m<yijian[i].children[g].children.length;m++){
                            iplist.push(yijian[i]['switchIp'])
                            problemIdList.push(yijian[i].children[g].children[m].questionId)
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
                const userinformation = list1.map(x=>JSON.stringify(x))
                const historyScan = 'historyscan'
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                console.log(historyScan)
                return request({
                    // url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+historyScan,
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            //当前扫描一键修复所有问题
            allxiu(){
                //问题ID集合
                const problemIdList = []
                const iplist = []
                const yijian = this.testData
                console.log(this.testData)
                for (let i = 0;i<yijian.length;i++){
                    for (let g = 0;g<yijian[i].children.length;g++){
                        for (let m = 0;m<yijian[i].children[g].children.length;m++){
                            iplist.push(yijian[i]['switchIp'])
                            problemIdList.push(yijian[i].children[g].children[m].questionId)
                        }
                    }
                }
                const list1 = []
                for(let i = 0;i<this.alljiao.allInfo.length;i++){
                    const chaip = JSON.parse(this.alljiao.allInfo[i])
                    for(let g = 0;g<iplist.length;g++){
                        if(chaip['ip'] === iplist[g]){
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x=>JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            //历史单台一键修复
            xiuallone(row){
                // setInterval(this.lishi,5000)
                const thisip = row.switchIp
                const listAll = row.children
                const list1 = []
                const problemIdList = []
                for(let i = 0;i<listAll.length;i++){
                    for (let g = 0;g<listAll[i].children.length;g++){
                        problemIdList.push(listAll[i].children[g].questionId)
                    }
                }
                for (let i = 0;i<this.newArr.length;i++){
                    const chaip = this.newArr[i]
                    if(chaip['ip'] === thisip){
                        for (let g = 0;g<problemIdList.length;g++){
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x=>JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            //当前单台一键修复
            xiuall(row){
                const thisip = row.switchIp
                const listAll = row.children
                const list1 = []
                const problemIdList = []
                for (let i=0;i<listAll.length;i++){
                    for (let g=0;g<listAll[i].children.length;g++){
                        problemIdList.push(listAll[i].children[g].questionId)
                    }
                }
                for (let i = 0;i<this.alljiao.allInfo.length;i++){
                    const chaip = JSON.parse(this.alljiao.allInfo[i])
                    if (chaip['ip'] === thisip){
                        for (let g=0;g<problemIdList.length;g++){
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x=>JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                console.log(problemIdList)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            //历史修复单个问题
            xiufuone(row){
                const thisid = row.hproblemId
                let thisparip = ''
                const allwenti = this.tableDataq
                for(let i = 0;i<allwenti.length;i++){
                    for (let g = 0;g<allwenti[i].children.length;g++){
                        for (let m = 0;m<allwenti[i].children[g].children.length;m++){
                            for (let n = 0;n<allwenti[i].children[g].children[m].children.length;n++){
                                if (allwenti[i].children[g].children[m].children[n].hproblemId === thisid){
                                    thisparip = allwenti[i].children[g].switchIp
                                }
                            }
                        }
                    }
                }
                const list1 = []
                const problemIdList = []
                problemIdList.push(row.questionId)
                console.log(problemIdList)
                for (let i = 0;i<this.newArr.length;i++){
                    const chaip = this.newArr[i]
                    if (chaip['ip'] === thisparip){
                        for (let g=0;g<problemIdList.length;g++){
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x=>JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            // 当前修复单个问题
            xiufu(row){
                console.log(row.hproblemId)
                const thisid = row.hproblemId
                let thisparip = ''
                // tableDataq testData
                console.log(this.testData)
                const allwenti = this.testData
                for(let i = 0;i<allwenti.length;i++){
                    for (let g = 0;g<allwenti[i].children.length;g++){
                        for (let m = 0;m<allwenti[i].children[g].children.length;m++){
                            if (allwenti[i].children[g].children[m].hproblemId === thisid){
                                thisparip = allwenti[i].switchIp
                                console.log(thisparip)
                            }
                        }
                    }
                }
                const list1 = []
                const problemIdList = []
                problemIdList.push(row.questionId)
                console.log(problemIdList)
                // list1.push(this.forms.dynamicItem[0])
                // list2.push(listv)
                for (let i = 0;i<this.alljiao.allInfo.length;i++){
                    const chaip = JSON.parse(this.alljiao.allInfo[i])
                    if (chaip['ip'] === thisparip){
                        for (let g=0;g<problemIdList.length;g++){
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x=>JSON.stringify(x))
                // const problemIdList = list2.map(x=>JSON.stringify(x))
                const scanNum = this.num
                console.log(userinformation)
                return request({
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList+'/'+scanNum,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            sendDataToServer() {
                if (this.webSocket.readyState === 1) {
                    this.webSocket.send('来自前端的数据')
                } else {
                    throw Error('服务未连接')
                }
            },
            /**
             * 初始化ws
             */
            wsInit() {
                // const wsuri = 'ws://192.168.1.98/dev-api/websocket/loophole'
                const wsuri = `ws://localhost/dev-api/websocket/loophole${Cookies.get('usName')}`
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
            wsOpenHanler(event) {
                console.log('ws建立连接成功')
            },
            wsMessageHanler(e) {
                console.log('wsMessageHanler')
                function changeTreeDate(arrayJsonObj,oldKey,newKey) {
                    let strtest = JSON.stringify(arrayJsonObj);
                    let reg = new RegExp(oldKey,'g');
                    let newStr = strtest.replace(reg,newKey);
                    return JSON.parse(newStr);
                }
                let newJson = changeTreeDate(JSON.parse(e.data),'switchProblemVOList','children');
                let newJson1 = changeTreeDate(newJson,'switchProblemCOList','children')
                this.testData = newJson1
                const shu = this.testData
                for (let i=0;i<shu.length;i++){
                    for (let g=0;g<shu[i].children.length;g++){
                        for (let m=0;m<shu[i].children[g].children.length;m++){
                            if (shu[i].children[g].children[m].valueInformationVOList.length > 0){
                                const yonghu = shu[i].children[g].children[m].valueInformationVOList[0].dynamicInformation
                                const wenti = shu[i].children[g].children[m].problemName
                                const zuihou = yonghu +" "+ wenti
                                this.$set(shu[i].children[g].children[m],'problemName',zuihou)
                            }
                        }
                    }
                }
                console.log(this.testData)
            },
            /**
             * ws通信发生错误
             */
            wsErrorHanler(event) {
                console.log(event, '通信发生错误')
                this.wsInit()
            },
            /**
             * ws关闭
             */
            wsCloseHanler(event) {
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
            },
        }
    }
</script>

<style scoped>

</style>

