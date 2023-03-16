<template>
  <div class="app-container">
    <el-form ref="form" :model="form">
      <el-form-item label="定义模板" class="inline_one">

        <el-select v-model="form.model_name" placeholder="自定义模板"
                   filterable allow-create @blur="modelName" @change="getList" @focus="general($event)"
                   name="model_name" style="width: 200px">
          <el-option v-for="(item,index) in genList"
                     :key="index" :label="item" :value="item"></el-option>
        </el-select>

      </el-form-item>
      <el-form-item style="display:inline-block;margin-left: 10px">
<!--        <el-button @click="specialSearch">查看列表</el-button>-->
<!--        <el-button @click="test_All">测试按钮</el-button>-->
        <el-button @click="submit_Form" type="primary" round>提交</el-button>
<!--        <el-button @click="scanModel" type="primary" round>扫描模板</el-button>-->
      </el-form-item>
      <div>
        <el-input
          v-model="deptName"
          placeholder="请输入查找内容"
          clearable
          size="small"
          prefix-icon="el-icon-search"
          style="margin-bottom: 20px"
        />
        <el-tree show-checkbox
                 :data="fenxiang" :props="defaultProps" :filter-node-method="filterNode"
                 @node-click="handleNodeClick"
                 ref="treeone" node-key="id" :default-expand-all="true" :default-checked-keys="check_list"></el-tree>
      </div>
    </el-form>

<!--    <el-dialog title="收货地址" :visible.sync="dialogFormVisible">-->
<!--      <el-form :model="formScan">-->
<!--        <el-form-item label="ip" label-width="80px">-->
<!--          <el-input v-model="formScan.ip"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="用户名" label-width="80px">-->
<!--          <el-input v-model="formScan.name"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="密码" label-width="80px">-->
<!--          <el-input v-model="formScan.password"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="连接方式" label-width="80px">-->
<!--          <el-input v-model="formScan.mode"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="端口号" label-width="80px">-->
<!--          <el-input v-model="formScan.port"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="配置密码" label-width="80px">-->
<!--          <el-input v-model="formScan.configureCiphers"></el-input>-->
<!--        </el-form-item>-->
<!--      </el-form>-->
<!--      <div slot="footer" class="dialog-footer">-->
<!--        <el-button @click="dialogFormVisible = false">取 消</el-button>-->
<!--        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>-->
<!--      </div>-->
<!--    </el-dialog>-->

  </div>
</template>

<script>
    import request from '@/utils/request'
    export default {
        name: "Model_page",
        data(){
            return {
                // //扫描设备
                // formScan:{
                //         ip: '',
                //         name: '',
                //         password:'',
                //         mode:'ssh',
                //         port:22,
                //         configureCiphers:''
                // },
                // dialogFormVisible: false,
                form:{
                    model_name:''
                },
                defaultProps: {
                    children: 'children',
                    label: 'label'
                },
                fenxiang:[],
                //查询树
                deptName:undefined,
                check_list:[],
                //模板list
                genList:[],
                //选中需要提交的模板id
                choose_modelId:[],
                //模板id
                formworkId:'',
            }
        },
        created(){
          this.specialSearch()
        },
        watch:{
            // 根据输入筛选专项
            deptName(val) {
                this.$refs.treeone.filter(val);
            },
            check_list(){
                this.$refs.treeone.setCheckedKeys(this.check_list)
            }
        },
        methods:{
            //扫描模板
            scanModel(){
                this.dialogFormVisible = true
            },
            //提交模板
            submit_Form(){
                //赋值给chooseTest,无实际作用
                var chooseTest = this.$refs.treeone.getCheckedKeys()
                for(let i = 0;i<chooseTest.length;i++){
                    if (typeof(chooseTest[i]) != 'undefined'){
                        this.choose_modelId.push(chooseTest[i])
                    }
                }
                //创建实体类传给后台
                const formwork = {
                    formworkName:this.form.model_name,
                    formworkIndex:this.choose_modelId.join()
                }
                if(this.form.model_name == ''){
                    this.$message.warning('名称不能为空!')
                }else {
                    if(this.choose_modelId.length>0){
                        return request({
                            url:'/sql/formwork',
                            method:'post',
                            data:JSON.stringify(formwork)
                        }).then(response=>{
                            this.$message.success('提交成功!')
                        })
                    }else {
                        this.$message.warning('请选择问题种类!')
                    }
                }
            },
            //选择模板后回显
            getList(){
                this.check_list = []
                for (let i = 0;i<this.genList.length;i++){
                    if (this.genList[i] == this.form.model_name){
                        return request({
                            url:'/sql/formwork/pojoByformworkName?formworkName=' + this.form.model_name,
                            method:'get',
                        }).then(response=>{
                            console.log(response)
                            this.check_list = response.formworkIndex.split(',')
                            this.formworkId = response.id
                            console.log(this.formworkId)
                            console.log(this.check_list)
                        })
                    }else {
                        console.log('新添加项!')
                        this.check_list = []
                    }
                }
            },
            //自定义模板输入
            modelName(e){
                let value = e.target.value
                if(value){
                    this.form.model_name = value
                }
                this.check_list = []
            },
            //获取模板list
            general(e){
                    this.who = e.target.getAttribute('name')
                    console.log(this.who)
                    return request({
                        url:'/sql/formwork/getNameList',
                        method:'get',
                    }).then(response=>{
                        console.log(response)
                        this.genList = response
                    })
            },
            //测试所有
            test_All(){
                // console.log(this.form.model_name)
                // console.log(this.fenxiang)
                // var zhuanid = this.$refs.treeone.getCheckedKeys()
                // console.log(zhuanid)
                // const totalQuestionTableId = []
                // for(let i = 0;i<=zhuanid.length;i++){
                //     if (typeof(zhuanid[i])!='undefined'){
                //         totalQuestionTableId.push(zhuanid[i])
                //     }
                // }
                // console.log(totalQuestionTableId)
            },
            // 筛选节点
            filterNode(value, data) {
                if (!value) return true;
                return data.label.indexOf(value) !== -1;
            },
            //点击树
            handleNodeClick(fenxiang) {

            },
            //专项扫描
            specialSearch(){
                var ce = {}
                return request({
                    url:'/sql/total_question_table/fuzzyQueryListByPojoMybatis',
                    method:'get',
                    data:ce
                }).then(response=>{
                    function changeTreeDate(arrayJsonObj,oldKey,newKey) {
                        let strtest = JSON.stringify(arrayJsonObj);
                        let reg = new RegExp(oldKey,'g');
                        let newStr = strtest.replace(reg,newKey);
                        return JSON.parse(newStr);
                    }
                    response = changeTreeDate(response,'totalQuestionTableVOList','children')
                    response = changeTreeDate(response,'totalQuestionTableList','children')
                    for (let i = 0;i<response.length;i++){
                        for (let g = 0;g<response[i].children.length;g++){
                            this.$delete(response[i].children[g],'typeProblem')
                            for (let m = 0;m<response[i].children[g].children.length;m++){
                                this.$delete(response[i].children[g].children[m],'typeProblem')
                                this.$delete(response[i].children[g].children[m],'temProName')
                                let pinjie = response[i].children[g].children[m].problemName+' '+'('+
                                    response[i].children[g].children[m].brand+' '+
                                    response[i].children[g].children[m].type+' '+
                                    response[i].children[g].children[m].firewareVersion+' '+
                                    response[i].children[g].children[m].subVersion+')'
                                this.$set(response[i].children[g].children[m],'problemName',pinjie)
                            }
                        }
                    }
                    response = changeTreeDate(response,'typeProblem','label')
                    response = changeTreeDate(response,'temProName','label')
                    response = changeTreeDate(response,'problemName','label')
                    this.fenxiang = response
                })
            }
        }
    }
</script>

<style scoped>
  .inline_one{
    display: inline-block;
  }
>>> div.inline_one>.el-form-item__content{
  display: inline-block;
}
  >>> .el-dialog{
    width: 600px;
  }
</style>
