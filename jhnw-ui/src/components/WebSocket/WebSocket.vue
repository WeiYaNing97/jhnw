<template>
  <div>
<!--    <el-button @click="sendDataToServer" >给后台发送消息</el-button>-->
    <el-input
      id="webt"
      type="textarea"
      style="white-space: pre-wrap;"
      v-model="textarea" :rows="10" readonly></el-input>
  </div>
</template>

<script>
    import Cookies from "js-cookie"
    export default {
        name: "WebSocket",
        data() {
            return {
                //扫描结束
                saoend:false,
                // ws是否启动
                wsIsRun: false,
                // 定义ws对象
                webSocket: null,
                // ws请求链接（类似于ws后台地址）
                ws: '',
                // ws定时器
                wsTimer: null,
                textarea:''
            }
        },
        async mounted() {
            this.wsIsRun = true
            this.wsInit()
        },
        watch:{
            //检测扫描结束
            textarea(){
                if (this.textarea.includes('扫描结束')){
                    this.saoend = true
                }
            }
        },
        created(){
            const usname = Cookies.get('usName')
        },
        methods: {
            //给父组件显示
            geifuone(){
              return this.saoend
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
                const wsuri = `ws://localhost/dev-api/websocket/badao${Cookies.get('usName')}`
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
                this.textarea = this.textarea + e.data;
                this.$nextTick(()=>{
                    const textarea = document.getElementById('webt')
                    textarea.scrollTop = textarea.scrollHeight
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
