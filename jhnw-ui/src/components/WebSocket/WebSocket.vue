<template>
  <div>
<!--    <el-button @click="sendDataToServer" >给后台发送消息</el-button>-->
<!--    <el-input-->
<!--      id="webt"-->
<!--      type="textarea"-->
<!--      style="white-space: pre-wrap;"-->
<!--      v-model="textarea" :rows="10" readonly></el-input>-->
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="系统信息" name="first">
        <el-input
          id="webtTwo"
          resize="none"
          type="textarea"
          style="white-space: pre-wrap;"
          v-model="textareaInfo" :rows="10" readonly></el-input>
      </el-tab-pane>
      <el-tab-pane label="发送与接收" name="second">
        <el-input
          resize="none"
          id="webtOne"
          type="textarea"
          style="white-space: pre-wrap;"
          v-model="textareaOne" :rows="10" readonly></el-input>
      </el-tab-pane>
      <el-tab-pane label="风险" name="third">
        <el-input
          id="webtThree"
          resize="none"
          type="textarea"
          style="white-space: pre-wrap;"
          v-model="textareaRisk" :rows="10" readonly></el-input>
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
    export default {
        name: "WebSocket",
        data() {
            return {
                activeName:'second',
                //扫描结束
                saoend:false,
                //修复结束
                repairend:false,
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
            //检测扫描结束、修复结束
            textareaOne(){
                if (this.textareaOne.includes('扫描结束')){
                    this.saoend = true
                }
                if (this.textareaOne.includes('修复结束')){
                    this.repairend = true
                }
            },
            textareaInfo(){
                if (this.textareaInfo.includes('扫描结束')){
                    this.saoend = true
                }
            },
            saoendip(){
                this.postendIp()
            },
            saoend(){
                this.postEnd()
            }
        },
        created(){
            const usname = Cookies.get('usName')
        },
        methods: {
            //扫描结束传给父组件
            postEnd(){
                this.$emit('eventOne',this.saoend)
            },
            //扫描完成ip传给父组件
            postendIp(){
                this.$emit('event',this.ipEnd)
            },
            //
            handleClick(tab, event) {
                console.log(tab, event)
            },
            geifurepaired(){
                return this.repairend
            },
            sendDataToServer() {
                if (this.webSocket.readyState === 1) {
                    this.webSocket.send('来自前ssss端的数据')
                } else {
                    throw Error('服务未连接')
                }
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
                console.log(e)
                if (e.data.indexOf('发送') != -1 || e.data.indexOf('接收') != -1){
                    this.textareaOne = this.textareaOne + e.data
                }else if (e.data.indexOf('系统信息') != -1){
                    this.textareaInfo = this.textareaInfo + e.data
                }else if (e.data.indexOf('风险') != -1){
                    this.textareaRisk = this.textareaRisk + e.data
                }else if (e.data.indexOf('scanThread') != -1){
                    this.textareaEnd = this.textareaEnd + e.data
                    this.saoendip = e.data
                    console.log(this.saoendip)
                }
                //截取字符串
                function getCaption(obj){
                    const index = obj.lastIndexOf(":")
                    const res = obj.substring(index+1,obj.length)
                    return res
                }
                this.ipEnd = getCaption(this.saoendip)
                // this.textarea = this.textarea + e.data;
                this.$nextTick(()=>{
                    // const textarea = document.getElementById('webt')
                    const textareaOne = document.getElementById('webtOne')
                    const textareaTwo = document.getElementById('webtTwo')
                    const textareaThree = document.getElementById('webtThree')
                    // textarea.scrollTop = textarea.scrollHeight
                    textareaOne.scrollTop = textareaOne.scrollHeight
                    textareaTwo.scrollTop = textareaTwo.scrollHeight
                    textareaThree.scrollTop = textareaThree.scrollHeight
                })
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
  textarea{
    background-color: #1e1e1e;
  }
</style>
