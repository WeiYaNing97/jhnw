<template>
  <div id="app">
    <router-view v-if="isRouterAlive" />
  </div>
</template>

<script>
    import Cookies from 'js-cookie'
export default  {
  name:  'App',
    provide(){
      return {
          reload:this.reload
      }
    },
    data(){
      return {
          isRouterAlive:true,
          gap_time: 0,
          beforeUnload_time: 0
      }
    },
    metaInfo() {
        return {
            title: this.$store.state.settings.dynamicTitle && this.$store.state.settings.title,
            titleTemplate: title => {
                return title ? `${title} - ${process.env.VUE_APP_TITLE}` : process.env.VUE_APP_TITLE
            }
        }
    },
    mounted(){
      // window.onbeforeunload = function (e) {
      //     var storage = window.localStorage
      //     storage.clear()
      // }
        window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
        window.addEventListener('unload', e => this.unloadHandler(e))
    },
    methods:{
      reload(){
          this.isRouterAlive = false
          this.$nextTick(function () {
              this.isRouterAlive = true
          })
      },
         beforeunloadHandler(e) {
            this.beforeUnload_time = new Date().getTime();
            console.log(this.beforeUnload_time)
            console.log('关闭前')
             this.$store.dispatch('LogOut').then(() => {
                 console.log('执行了退出')
             })
            // if (e) {
            //     e = e || window.event;
            //     console.log(e);
            //     if (e) {
            //         e.returnValue = "关闭提示";
            //     }
            //     return "关闭提示";
            // }
        },
        unloadHandler(e){
            this.gap_time = new Date().getTime() - this.beforeUnload_time;
            if (this.gap_time <= 5) {
                localStorage.clear()
                Cookies.remove('Admin-Token')
                Cookies.remove('usName')
                this.$store.dispatch('LogOut').then(() => {
                    console.log('执行了退出')
                })
            } else {
                console.log('刷新页面!')
            }
        }
    },
    destroyed() {
        window.removeEventListener('beforeunload', (e) => this.beforeunloadHandler(e));
        window.removeEventListener('unload', (e) => this.unloadHandler(e));
    },
}
</script>
