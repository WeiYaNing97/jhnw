<template>
  <div>
<!--    <el-input-->
<!--      id="webt"-->
<!--      type="textarea"-->
<!--      style="white-space: pre-wrap;"-->
<!--      v-model="textarea" :rows="10" readonly></el-input>-->
    <el-button @click="exportFile" type="primary" plain style="margin-right: 20px">导出</el-button>
<!--    //1.2添加测试-->
    <el-input v-model="inputT" placeholder="请输入内容" style="width:500px" @input="filterText"></el-input>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="系统信息" name="first">
        <el-input id="webtTwo" resize="none" type="textarea"
                  style="white-space: pre-wrap;" v-model="textareaInfo" :rows="10" readonly></el-input>
      </el-tab-pane>
      <el-tab-pane label="发送与接收" name="second">
        <el-input id="webtOne" resize="none" type="textarea"
                  style="white-space: pre-wrap;" v-model="textareaOne" :rows="10" readonly></el-input>
      </el-tab-pane>
      <el-tab-pane label="异常" name="third">
        <el-input id="webtThree" resize="none" type="textarea"
                  style="white-space: pre-wrap;" v-model="textareaRisk" :rows="10" readonly></el-input>
      </el-tab-pane>
<!--      <el-tab-pane label="扫描结束" name="four" v-show="false">-->
<!--        <el-input-->
<!--          id="webtFour"-->
<!--          resize="none"-->
<!--          type="textarea"-->
<!--          style="white-space: pre-wrap;"-->
<!--          v-model="textareaEnd" :rows="10" readonly></el-input>-->
<!--      </el-tab-pane>-->
    </el-tabs>

  </div>
</template>

<script>
    import Cookies from "js-cookie"
    import  {MessageBox} from "element-ui"
    import { saveAs } from 'file-saver'
    export default {
        name: "WebSocket",
        data() {
            return {
                //输入框筛选
                inputT:'',
                activeName:'second',
                // ws是否启动
                wsIsRun: false,
                // 定义ws对象
                webSocket: null,
                // ws请求链接（类似于ws后台地址）
                ws: '',
                // ws定时器
                wsTimer: null,
                textarea:'',
                textareaOne:'',
                textareaOneOneOne:[],
                textareaRisk:'',
                textareaInfo:'',
                textareaEnd:'',
                //扫描完成的ip
                saoendip:'',
                //截取后的ip
                ipEnd:''
            }
        },
        async mounted() {
            this.wsIsRun = true
            this.wsInit()
        },
        watch:{
            saoendip(){
                this.postendIp()
            },
        },
        created(){
            // const usname = Cookies.get('usName')
            let timeXun = setInterval(() => {
                if (this.webSocket.readyState === 1) {
                    this.webSocket.send('ping')
                } else {
                    throw Error('服务未连接')
                }
            },30000)
        },
        computed:{

        },
        methods: {
            //筛选框
            filterText(){
                if (this.inputT){
                    console.log('22222222222')
                    const filteredData = this.textareaOneOneOne
                        .filter(data => data.includes(this.inputT))
                        .join("\n");
                    this.textareaOne = filteredData
                }else {
                    this.textareaOne = this.textareaOneOneOne.join("\n");
                }
              // this.$forceUpdate();
            },
            //导出
            exportFile(){
                MessageBox.confirm('确定导出以下所有文本框内容吗？','提示').then(c=>{
                    const date = new Date()
                    const contentOne = document.getElementById('webtOne').value
                    const contentTwo = document.getElementById('webtTwo').value
                    const contentThree = document.getElementById('webtThree').value
                    const fileNameOne = '发送与接收_' + date.getTime() + '.txt'
                    const fileNameTwo = '系统信息_' + date.getTime() + '.txt'
                    const fileNameThree = '风险_' + date.getTime() + '.txt'
                    const blobOne = new Blob([contentOne], { type: 'text/plain;charset=utf-8' })
                    const blobTwo = new Blob([contentTwo], { type: 'text/plain;charset=utf-8' })
                    const blobThree = new Blob([contentThree], { type: 'text/plain;charset=utf-8' })
                    saveAs(blobOne,fileNameOne)
                    saveAs(blobTwo,fileNameTwo)
                    saveAs(blobThree,fileNameThree)
                }).catch(ee=>{
                    this.$message.warning('导出取消!')
                })
            },
            //扫描完成ip传给父组件
            postendIp(){
                this.$emit('event',this.ipEnd)
            },
            //tab 被选中时触发
            handleClick(tab, event) {
                console.log(tab)
                console.log(event)
            },
            /**
             * 初始化ws
             */
            wsInit() {
                // localhost 192.168.1.98
                // const wsuri = 'ws://192.168.1.98/dev-api/websocket/badao'
                const wsuri = `wss://${location.host}/dev-api/websocket/${Cookies.get('usName')}`
                // const wsuri = `ws://${location.host}/prod-api/websocket/${Cookies.get('usName')}`
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
                if (e.data === 'pong'){
                    console.log('00000'+e.data)
                }else {
                    if (e.data.indexOf('发送') != -1 || e.data.indexOf('接收') != -1){
                        this.textareaOne = this.textareaOne + e.data
                        this.textareaOneOneOne.push(e.data)
                        if (e.data.indexOf('扫描结束') != -1){
                            this.webSocket.send('接收结束')
                            console.log(this.textareaOneOneOne)
                        }
                    }else if (e.data.indexOf('系统信息') != -1){
                        this.textareaInfo = this.textareaInfo + e.data
                    }else if (e.data.indexOf('风险') != -1){
                        this.textareaRisk = this.textareaRisk + e.data
                    }else if (e.data.indexOf('scanThread') != -1){
                        this.textareaEnd = this.textareaEnd + e.data
                        this.saoendip = e.data
                        console.log('后端传输的扫描完成IP:')
                        console.log(this.saoendip)
                    }
                    //截取字符串
                    function getCaption(obj){
                        const result1 = obj.split(':')[1]
                        const result2 = obj.split(':')[2]
                        return result1+':'+result2
                    }
                    this.ipEnd = getCaption(this.saoendip)
                    // this.textarea = this.textarea + e.data;
                    this.$nextTick(()=>{
                        const textareaOne = document.getElementById('webtOne')
                        const textareaTwo = document.getElementById('webtTwo')
                        const textareaThree = document.getElementById('webtThree')
                        if (textareaOne) {
                            textareaOne.scrollTop = textareaOne.scrollHeight
                        }
                        if (textareaTwo) {
                            textareaTwo.scrollTop = textareaTwo.scrollHeight
                        }
                        if (textareaThree) {
                            textareaThree.scrollTop = textareaThree.scrollHeight
                        }
                    })
                }
            },
            /**
             * ws通信发生错误
             */
            wsErrorHanler(event) {
                console.log('错误的+' + event.code + "+" + event.reason + "+" + event.wasClean)
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
            },
        }
    }
</script>

<style scoped>
  textarea{
    background-color: #1e1e1e;
  }
</style>
