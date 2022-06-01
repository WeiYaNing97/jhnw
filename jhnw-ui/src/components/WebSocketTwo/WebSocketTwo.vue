<template>
  <div>
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
                     v-show="scope.row.ifQuestion==='无问题'"
                     @click="xiufu(scope.row)">修复</el-button>
          <el-button style="margin-left: 0" size="mini" type="text"
                     v-show="scope.row.hasOwnProperty('switchIp')&&!scope.row.hasOwnProperty('typeProblem')"
                     @click="xiuall(scope.row)">一键修复</el-button>
        </template>
      </el-table-column>
      <!--      <el-table-column prop="planQuantity"  label="用户名"></el-table-column>-->
    </el-table>

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
    export default {
        name: "WebSocketTwo",
        props:{
            queryParams:''
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
                        switchIp:'192.168.1.1',
                        hproblemId:1,
                        children:[
                            {
                            typeProblem: '安全配置',
                            hproblemId:11,
                            children: [
                                {
                                    problemName:'密码明文存储',
                                    ifQuestion:'无问题',
                                    hproblemId:111
                                },
                                {
                                    problemName:'telnet开启',
                                    ifQuestion:'有问题',
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
                                        ifQuestion:'有问题',
                                        hproblemId:112
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        switchIp:'192.168.1.100',
                        hproblemId:2,
                        children:[{
                            typeProblem: '安全配置',
                            hproblemId:22,
                            children: [
                                {
                                    problemName:'密码明文存储',
                                    ifQuestion:'无问题',
                                    hproblemId:222,
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
        methods: {
            //一键修复
            xiuall(row){
              const listAll = row.children
                // const b = row.children[0].children[0].problemName
                const list1 = []
                const list2 = []
                for (let i=0;i<listAll.length;i++){
                    for (let g=0;g<listAll[i].children.length;g++){
                        // console.log(listAll[i].children[g].problemName)
                        const listC = {}
                        this.$set(listC,'valueId',listAll[i].children[g].valueId)
                        this.$set(listC,'comId',listAll[i].children[g].comId)
                        list2.push(listC)
                    }
                }
                list1.push(this.queryParams)
                const userinformation = list1.map(x=>JSON.stringify(x))
                const commandValueList = list2.map(x=>JSON.stringify(x))
                axios({
                    method:'post',
                    url:'http://192.168.1.98/dev-api/sql/SolveProblemController/batchSolution/'+userinformation+'/'+commandValueList,
                    // url:'/dev-api/sql/ConnectController/definitionProblem1/'+userinformation+'/'+commandValueList,
                    headers:{
                        "Content-Type": "application/json"
                    },
                    data:{
                        "userinformation":userinformation,
                        "commandValueList":commandValueList
                    }
                }).then(res=>{
                    console.log("成功")
                })
                console.log(list2)
            },
            // 修复问题
            xiufu(row){
              // this.dislogopen = true
                // alert(row)
                const list1 = []
                const list2 = []
                const listv = {}
                this.$set(listv,'valueId',row.valueId)
                this.$set(listv,'comId',row.comId)
                list1.push(this.queryParams)
                list2.push(listv)
                const userinformation = list1.map(x=>JSON.stringify(x))
                const commandValueList = list2.map(x=>JSON.stringify(x))
                axios({
                    method:'post',
                    url:'http://192.168.1.98/dev-api/sql/SolveProblemController/batchSolution/'+userinformation+'/'+commandValueList,
                    // url:'/dev-api/sql/ConnectController/definitionProblem1/'+userinformation+'/'+commandValueList,
                    headers:{
                        "Content-Type": "application/json"
                    },
                    data:{
                        "userinformation":userinformation,
                        "commandValueList":commandValueList
                    }
                }).then(res=>{
                    console.log("成功")
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
                const wsuri = 'ws://192.168.1.98/dev-api/websocket/loophole'
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
                console.log(JSON.parse(e.data))

                function changeTreeDate(arrayJsonObj,oldKey,newKey) {
                    let strtest = JSON.stringify(arrayJsonObj);
                    let reg = new RegExp(oldKey,'g');
                    let newStr = strtest.replace(reg,newKey);
                    return JSON.parse(newStr);
                }
                let newJson = changeTreeDate(JSON.parse(e.data),'switchProblemVOList','children');
                let newJson1 = changeTreeDate(newJson,'switchProblemCOList','children')
                // this.testData = newJson1
                // if (newJson1[0].children[0] != null){
                //     delete newJson1[0].children[0]
                // }
                // for (i=0;i<newJson1[0].children.length;i++){
                //     delete newJson1[0].children[i].switchIp
                // }

                // var i = 0
                // for(i;i<newJson1.length;i++){
                //     for (i;i<newJson1[i].children.length;i++){
                //
                //     }
                // }
                this.testData = newJson1
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

