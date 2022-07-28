<template>
  <div>
<!--    tableDataq  testData-->
    <el-table v-loading="loading"
              :data="testData"
              ref="tree"
              style="width: 100%;margin-bottom: 20px;"
              row-key="hproblemId"
              default-expand-all
              :tree-props="{children: 'children',hasChildren: 'hasChildren'}">
      <el-table-column prop="switchIp" label="主机" width="150px"></el-table-column>
      <el-table-column prop="typeProblem" label="问题类型" width="100px"></el-table-column>
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
                     @click="xiuall(scope.row)">一键修复</el-button>
        </template>
      </el-table-column>
      <!--      <el-table-column prop="planQuantity"  label="用户名"></el-table-column>-->
    </el-table>

    <el-button @click="kan">看我</el-button>
    <el-button @click="aaa">第二个</el-button>
    <el-button @click="zizujian">看子组件</el-button>
    <!--  修复问题-->
    <el-dialog
      title="修复问题"
      :visible.sync="dislogopen"
    >
      <el-button type="text">新增</el-button>

      <el-form :model="xiuform">
        <el-row>
          <el-col>
            <el-form-item label="命令" :label-width="formLabelWidth">
              <el-input
                style="width: 150px"
                placeholder="命令"
              ></el-input>
              +
              <el-select v-model="xiuform.xiala" placeholder="参数">
                <el-option v-for="item in depss" :key="item.id" :label="item.name" :value="item.name"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
    import log from "../../views/monitor/job/log";
    import axios from 'axios'
    import request from '@/utils/request'
    import Cookies from "js-cookie"
    export default {
        name: "WebSocketTwo",
        props:{
            queryParams:'',
            alljiao:{
                allInfo:[

                ]
            },
            forms:{

            }
        },
        data() {
            return {
                // ws是否启动
                wsIsRun: false,
                // 定义ws对象
                webSocket: null,
                // ws请求链接（类似于ws后台地址）
                ws: '',
                // ws定时器
                wsTimer: null,
                testData:[],
                // aaa:false,
                tableDataq: [
                    {
                        switchIp:'192.168.1.100',
                        hproblemId:13462523456,
                        children:[
                            {
                            switchIp:null,
                            typeProblem: '安全配置',
                            hproblemId:12345671,
                            children: [
                                {
                                    problemName:'密码明文存储',
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
                dislogopen:false,
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
            zizujian(){
              // alert(JSON.stringify(this.forms.dynamicItem))
                alert(this.alljiao.allInfo)
            },
            //笨方法
            aaa(){
                const shu = this.tableDataq
                for (let i=0;i<shu.length;i++){
                    for (let g=0;g<shu[i].children.length;g++){
                        for (let m=0;m<shu[i].children[g].children.length;m++){
                            if (shu[i].children[g].children[m].hasOwnProperty('valueInformationVOList')===true){
                                const yonghu = shu[i].children[g].children[m].valueInformationVOList[0].dynamicInformation
                                const wenti = shu[i].children[g].children[m].problemName
                                const zuihou = yonghu +" "+ wenti
                                this.$set(shu[i].children[g].children[m],'problemName',zuihou)
                            }
                        }
                    }
                }
            },
            //单台一键修复
            xiuall(row){
                //当前点击一键修复交换机
                const thisip = row.switchIp
                const listAll = row.children
                const list1 = []
                const problemIdList = []
                for (let i=0;i<listAll.length;i++){
                    for (let g=0;g<listAll[i].children.length;g++){
                        // const listC = {}
                        // if(listAll[i].children[g].ifQuestion === '异常'){
                        //     this.$set(listC,'valueId',listAll[i].children[g].valueId)
                        //     this.$set(listC,'comId',listAll[i].children[g].comId)
                        //     list2.push(listC)
                        // }
                        // this.$set(listC,'questionId',listAll[i].children[g].questionId)
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
                console.log(userinformation)
                console.log(problemIdList)
                return request({
                    // url:'/sql/SolveProblemController/batchSolutionMultithreading/'+userinformation+'/'+problemIdList,
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList,
                    method:'post',
                    data:userinformation
                }).then(response=>{
                    console.log('成功')
                    this.$message.success('修复请求以提交!')
                })
            },
            // 修复问题
            xiufu(row){
              // this.dislogopen = true
                console.log(row.hproblemId)
                const thisid = row.hproblemId
                let thisparid = ''
                // tableDataq
                console.log(this.testData)
                const allwenti = this.testData
                for(let i = 0;i<allwenti.length;i++){
                    for (let g = 0;g<allwenti[i].children.length;g++){
                        for (let m = 0;m<allwenti[i].children[g].children.length;m++){
                            if (allwenti[i].children[g].children[m].hproblemId === thisid){
                                thisparid = allwenti[i].switchIp
                                console.log(thisparid)
                            }
                        }
                    }
                }
                const list1 = []
                const problemIdList = []
                // this.$set(listv,'questionId',row.questionId)
                problemIdList.push(row.questionId)
                console.log(problemIdList)
                // list1.push(this.forms.dynamicItem[0])
                // list2.push(listv)
                for (let i = 0;i<this.alljiao.allInfo.length;i++){
                    const chaip = JSON.parse(this.alljiao.allInfo[i])
                    if (chaip['ip'] === thisparid){
                        for (let g=0;g<problemIdList.length;g++){
                            list1.push(chaip)
                        }
                    }
                }
                const userinformation = list1.map(x=>JSON.stringify(x))
                // const problemIdList = list2.map(x=>JSON.stringify(x))
                console.log(userinformation)
                return request({
                    //老的路径 url:'/sql/ConnectController/definitionProblem1/'+userinformation+'/'+problemIdList,
                    url:'/sql/SolveProblemController/batchSolutionMultithreading/'+problemIdList,
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
                // localhost 192.168.1.98
                // const wsuri = 'ws://192.168.1.98/dev-api/websocket/loophole'
                const wsuri = `ws://192.168.1.98/dev-api/websocket/loophole${Cookies.get('usName')}`
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
            //看数据
            kan(){
                console.log(this.tableDataq)
                this.tableDataq.forEach(items=>{
                    // if (item.children.length>0){
                    //     // alert('sss')
                    //     // console.log(item)
                    //     this.digui(item)
                    //     console.log(item,'我们')
                    // }
                    console.log(items)
                    this.has()
                    this.digui(items)
                })
            },
            //实验
            has(){
              console.log('sss')
            },
            //递归
            digui(item){
                if (item.children.length>0){
                    item.children.forEach(ff=>{
                        this.digui(ff)
                        console.log(ff)
                    })
                }else{
                    alert('没了')
                }
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

