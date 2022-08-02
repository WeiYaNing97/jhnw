<template>
  <div>
    <el-form :model="xinxi" :ref="xinxi" label-width="60px" :inline="true" readonly>
      <el-form-item label="品牌" prop="pinpai">
        <el-input
          v-model="xinxi.jiben[0]"
          placeholder="品牌"
          size="small" readonly
        />
      </el-form-item>
      <el-form-item label="型号" prop="xinghao">
        <el-input
          v-model="xinxi.jiben[1]"
          placeholder="型号"
          size="small" readonly
        />
      </el-form-item>
      <el-form-item label="版本" prop="banben">
        <el-input
          v-model="xinxi.jiben[2]"
          placeholder="版本"
          size="small" readonly
        />
      </el-form-item>
      <el-form-item label="子版本" prop="zibanben">
        <el-input
          v-model="xinxi.jiben[3]"
          placeholder="子版本"
          size="small" readonly
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
    import Cookies from "js-cookie"
    export default {
        name: "WebSocketOne",
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
                textarea:'',
                xinxi:{
                    jiben:[]
                },
            }
        },
        async mounted() {
            this.wsIsRun = true
            this.wsInit()
        },
        methods: {
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
                // const wsuri = 'ws://192.168.1.98/dev-api/websocket/basicinformation'
                const wsuri = `ws://localhost/dev-api/websocket/basicinformation${Cookies.get('usName')}`
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
            wsMessageHanler(a) {
                console.log('wsMessageHanler')
                this.xinxi.jiben = JSON.parse(a.data)
                console.log(this.xinxi.jiben)
                //const redata = JSON.parse(e.data)
                //console.log(redata)
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
