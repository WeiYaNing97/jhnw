<template>
  <div class="cpu">
    <el-form>
      <el-form-item>
<!--        v-trigger-->
        <el-button type="primary" @click="lunxun">轮询</el-button>
        <el-input v-model="cpus" readonly></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
    import axios from 'axios'
    export default {
        name: "cpu",
        data(){
            return{
                cpus:''
            }
        },
        //自动触发事件
        // directives:{
        //     // trigger:{
        //     //     inserted(el,binging){
        //     //         el.click()
        //     //     }
        //     // }
        // },
        methods:{
            lun(){
                axios({
                    method:'post',
                    url:'http://192.168.1.98/dev-api/sql/get_Memory_CPU',
                }).then(res=>{
                    this.cpus = res.data
                })
                console.log(this.cpus)
            },
            lunxun(){
                // window.setInterval(this.lun,3000)
                setInterval(this.lun,3000)
            },
        }
    }
</script>

<style scoped>

</style>
