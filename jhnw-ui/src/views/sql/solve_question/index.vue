<template>
  <div class="app-container">
    <el-row>
      <el-col :span="18">
        <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
          <el-form-item label="设备基本信息:"></el-form-item>
          <el-form-item label="品牌" prop="brand">
            <el-select v-model="queryParams.brand" placeholder="品牌"
                       name="brand" @change="xuanChange" @focus="generalOne($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.brand" :value="item.brand"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="型号" prop="type">
            <el-select v-model="queryParams.type" placeholder="型号"
                       name="type" @change="xuanChange" @focus="generalOne($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.type" :value="item.type"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="固件版本" prop="firewareVersion">
            <el-select v-model="queryParams.firewareVersion" placeholder="固件版本"
                       name="firewareVersion" @change="xuanChange" @focus="generalOne($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.firewareVersion" :value="item.firewareVersion"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="子版本" prop="subVersion">
            <el-select v-model="queryParams.subVersion" placeholder="子版本"
                       name="subVersion" @change="xuanChange" @focus="generalOne($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.subVersion" :value="item.subVersion"></el-option>
            </el-select>
          </el-form-item>
          <br/>
          <el-form-item label="问题概要:"></el-form-item>
          <el-form-item label="问题类型" prop="typeProblem">
            <el-select v-model="queryParams.typeProblem" placeholder="问题类型"
                       name="typeProblem" @change="xuanChange" @focus="generalOne($event)">
              <el-option v-for="(item,index) in genList" :key="index"
                         :label="item.typeProblem" :value="item.typeProblem"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="范式名称">
            <el-select v-model="queryParams.temProName" placeholder="请选择范式名称"
                       name="temProName" @change="xuanChange" @focus="generalOne($event)">
              <el-option v-for="(item,index) in genList" :key="index"
                         :label="item.temProName" :value="item.temProName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="问题名称">
            <el-select v-model="queryParams.problemName" placeholder="请选择问题"
                       name="problemName" @change="xuanChange" @focus="generalOne($event)">
              <el-option v-for="(item,index) in genList" :key="index"
                         :label="item.problemName" :value="item.problemName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="repair">定义修复命令</el-button>
          </el-form-item>
        </el-form>
        <hr style='border:1px inset #D2E9FF;'>
        <el-form ref="forms" :inline="true" :model="forms" v-show="showNo">
          <el-form-item label="解决命令:"></el-form-item>
          <el-form-item>
            <el-checkbox v-model="checkedQ" @change="handleCheckAllChange">全选</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button type="text" icon="el-icon-delete" @click="shanchu">删除</el-button>
          </el-form-item>
          <div v-for="(item,index) in forms.dynamicItem" :key="index" :label="index">
            <el-form-item v-if="index!=0">
              <el-checkbox v-model="item.checked"></el-checkbox>
            </el-form-item>
            <el-form-item v-if="index!=0">{{index}}</el-form-item>
            <el-form-item :label="numToStr(item.onlyIndex)" @click.native="wcycle(item,$event)"></el-form-item>
            <div v-if="item.targetType === 'command'" :key="index" style="display: inline-block">
              <el-form-item label="命令" :prop="'dynamicItem.' + index + '.command'">
                <el-input v-model="item.command"></el-input>
              </el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
              </el-form-item>
            </div>
            <div v-if="item.targetType === 'compar'" :key="index" style="display: inline-block">
              <el-form-item label="命令" :prop="'dynamicItem.' + index + '.command'">
                <el-input v-model="item.command"></el-input>
              </el-form-item>
              <el-form-item label="参数">
                <el-select v-model="item.para" placeholder="参数"
                           @focus="paraLi" style="width: 150px">
                  <el-option v-for="(item,index) in paraList"
                             :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
              </el-form-item>
            </div>
            <el-form-item>
              <el-dropdown trigger="click">
                <el-button type="primary"><i class="el-icon-plus"></i></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item>
                    <el-button @click="addItem('command',item)" type="primary">命令</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('compar',item)" type="primary">命令+参数</el-button>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-form-item>
          </div>
          <el-form-item>
            <el-button @click="submitUseForm" type="primary">提交</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="6">
        <div style="border-left: 1px solid #DCDFE6;padding-left: 5px">
          <el-input
            v-model="deptName"
            placeholder="请输入关键字过滤"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            style="margin-bottom: 20px;width: 80%"
          />
          <el-tree :data="lookLists" :default-expand-all="zhankaiAll"
                   :props="defaultProps" :filter-node-method="filterNode"
                   @node-click="handleNodeClick" ref="treeone"></el-tree>
        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import { listSolve_question, getSolve_question, delSolve_question, addSolve_question, updateSolve_question, exportSolve_question } from "@/api/sql/solve_question";
import axios from 'axios'
import request from '@/utils/request'

export default {
  name: "Solve_question",
    components:{

    },
  data() {
    return {
        deptName:undefined,
        //右侧列表查看问题集合
        lookLists:[],
        //右侧列表全部展开
        zhankaiAll:true,
        //
        defaultProps: {
            children: 'children',
            label: 'label'
        },
        //通用基本信息下拉集合
        genList:[],
      proNameList:[],
      typeProList:[],
      brandList:[],
      fireList:[],
      typeList:[],
      subList:[],
      temProNameList:[],
      paraList:[],
      showNo:false,
      wDa:[],
      checkedQ:false,
      proId:'',
      forms:{
          dynamicItem:[
              {
                  test:'test',
                  onlyIndex:''
              }
          ]
      },
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 解决问题表格数据
      solve_questionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams:{
          brand: '',
          type: '',
          firewareVersion: '',
          subVersion: '',
          // commandId:'1',
          problemName:'',
          // notFinished:'---- More ----',
          typeProblem:'',
          temProName:'',
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        commond: [
          { required: true, message: "命令不能为空", trigger: "blur" }
        ],
      },

    };
  },
  created() {
    // this.getList();
  },
    mounted:function(){

    },
    watch:{
        // 根据输入筛选专项
        deptName(val) {
            this.$refs.treeone.filter(val);
        },
    },
  methods: {
      //筛选条件
      filterNode(value, data){
          if (!value) return true;
          return data.label.indexOf(value) !== -1;
      },
      //点击末尾删除图标
      deleteItem (item, index) {
          this.forms.dynamicItem.splice(index,1)
      },
      //选择值变化时
      xuanChange(){
          let newPar = {}
          for (var key in this.queryParams){
              newPar[key] = this.queryParams[key]
          }
          for (let i in newPar){
              if (newPar[i] === 'null'){
                  newPar[i] = ''
              }
          }
          console.log(newPar)
          return request({
              url:'/sql/total_question_table/selectPojoList',
              method:'get',
              // data:newPar
              params: newPar
          }).then(response=>{
              console.log(response)
              //有歧义
              this.lieNum = response.length
              this.proId = response[0].id
              console.log(this.proId)

              this.lookLists = []
              //转化为树结构
              for (let i = 0;i<response.length;i++){
                  let xinall = response[i].brand + ' ' + response[i].type + ' ' + response[i].firewareVersion + ' ' + response[i].subVersion
                  let loser = {
                      // label:xinall,
                      label:xinall+'>'+response[i].typeProblem+'>'+response[i].temProName,
                      children: [{
                          label:response[i].problemName,
                          id:response[i].id
                          // children:[{
                          //     label:response[i].temProName,
                          //     id:response[i].id
                          // }]
                      }]
                  }
                  this.lookLists.push(loser)
              }
          })
      },
      //新下拉列表
      generalOne(e){
          this.who = e.target.getAttribute('name')
          let newPar = {}
          for (var key in this.queryParams){
              newPar[key] = this.queryParams[key]
          }
          for (let i in newPar){
              if (newPar[i] === 'null'){
                  newPar[i] = ''
              }
          }
          console.log(newPar)
          return request({
              url:'/sql/total_question_table/selectPojoList',
              method:'get',
              // data:newPar
              params:newPar
          }).then(response=>{
              console.log(response)
              this.genList = this.quchong(response,this.who)
              let kong = {
                  [this.who] : 'null'
              }
              this.genList.push(kong)
              console.log(this.genList)
          })
      },
      //下拉列表
      general(e){
          this.who = e.target.getAttribute('name')
          let newPar = {}
          for (var key in this.queryParams){
              newPar[key] = this.queryParams[key]
          }
          for (let i in newPar){
              if (newPar[i] === 'null'){
                  newPar[i] = ''
              }
          }
          console.log(newPar)
          return request({
              url:'/sql/total_question_table/selectPojoList',
              method:'post',
              data:newPar
          }).then(response=>{
              console.log(response)
              this.lookLists = []
              //转化为树结构
              for (let i = 0;i<response.length;i++){
                  let xinall = response[i].brand + ' ' + response[i].type + ' ' + response[i].firewareVersion + ' ' + response[i].subVersion
                  let loser = {
                      label:xinall+'>'+response[i].typeProblem+'>'+response[i].temProName,
                      children: [{
                          label:response[i].problemName,
                          id:response[i].id
                          // children:[{
                          //     label:response[i].temProName,
                          //     id:response[i].id
                          // }]
                      }]
                  }
                  this.lookLists.push(loser)
              }
              this.genList = this.quchong(response,this.who)
              let kong = {
                  [this.who] : 'null'
              }
              this.genList.push(kong)
          })
      },
      //右侧列表点击
      handleNodeClick(lookLists){
          this.proId = lookLists.id
          console.log(this.proId)
          return request({
              url:'/sql/total_question_table/'+ this.proId,
              method:'get',
          }).then(response=>{
              console.log(response)
              this.queryParams.brand = response.data.brand
              this.queryParams.type = response.data.type
              this.queryParams.firewareVersion = response.data.firewareVersion
              this.queryParams.subVersion = response.data.subVersion
              this.queryParams.typeProblem = response.data.typeProblem
              this.queryParams.temProName = response.data.temProName
              this.queryParams.problemName = response.data.problemName
              this.showNo = true
          })
      },
      //定义修复命令(获取该问题ID)
      repair(){
          if (this.lookLists.length != 1 && this.showNo === false){
              alert('条件过于模糊,请完善!')
          }else if (this.showNo === true){
              alert('修复定义已显示，请勿重复点击!')
          } else {
              let form = new FormData()
              console.log(this.queryParams)
              for (var key in this.queryParams){
                  if (key != 'notFinished'&&key != 'commandId'){
                      form.append(key,this.queryParams[key])
                  }
              }
              return request({
                  url:'/sql/total_question_table/totalQuestionTableId',
                  method:'get',
                  // data:form
                  params:form
              }).then(response=>{
                  if (typeof (response) === 'number'){
                      this.showNo = true
                      this.proId = response
                      console.log(this.proId)
                  }else {
                      this.$message.warning('未定义该问题!请先定义问题!')
                  }
              })
          }
      },
      //提交
      submitUseForm(){
          const useForm = []
          const useLess = []
          this.forms.dynamicItem.forEach(e=>{
              if (e.test === "test"){
                  useLess.push(e)
              }else {
                  useForm.push(e)
              }
          })
          useForm.forEach(e=>{
              const thisIndex = useForm.indexOf(e)
              if(useForm.length != thisIndex+1){
                  const thisNext = useForm[thisIndex+1]
                  this.$set(e,'nextIndex',thisNext.onlyIndex)
              }
              this.$set(e,'pageIndex',thisIndex+1)
              this.$set(e,'resultCheckId','1')
          })
          const handForm = useForm.map(x => JSON.stringify(x))
          console.log(handForm)
          console.log(this.proId)
          const aaa = {
              "a1":this.queryParams,
              "a2":handForm
          }
          let form1 = new FormData();
          const a1 = {"ip":"1"}
          form1.append("commandLogicList",handForm)
          form1.append("commandValueList",handForm)
          return request({
              url:`/sql/command_logic/insertModifyProblemCommandSet?totalQuestionTableId=${this.proId}`,
              method:'post',
              data:handForm
          }).then(response=>{
              this.$message.success('提交成功!')
              console.log("成功")
          })
          //     // url:'/dev-api/sql/ConnectController/definitionProblem?totalQuestionTableId=23&aaa=33',
          //     url:`http://192.168.1.98/dev-api/sql/command_logic/insertModifyProblemCommandSet?totalQuestionTableId=${this.proId}`,
          //     // data:{
          //     //     "commandLogicList":handForm,
          //     //     "commandValueList":handForm
          //     // }
      },
      numToStr(num){
          num = num.toString()
          return num
      },
      addItem(type,item){
          const thisData = Date.now()
          const item1 = {
              targetType: type,
              onlyIndex:thisData,
          }
          const thisIndex = this.forms.dynamicItem.indexOf(item)
          this.forms.dynamicItem.splice(thisIndex+1,0,item1)
      },
      //全选
      handleCheckAllChange() {
          const useForm = []
          const useLess = []
          this.forms.dynamicItem.forEach(e=>{
              if (e.test === "test"){
                  useLess.push(e)
              }else {
                  useForm.push(e)
              }
          })
          const checkT = []
          useForm.forEach(te=>{
              if (te.checked === true){
                  checkT.push(te)
              }
          })
          if (checkT.length < useForm.length){
              useForm.forEach(tte=>{
                  this.$set(tte,'checked',true)
              })
              this.checkedQ = true
          }else if (checkT.length === useForm.length){
              useForm.forEach(ttte=>{
                  this.$set(ttte,'checked',false)
              })
              this.checkedQ = false
          }
      },
      shanchu(){
          const shanLiu = this.forms.dynamicItem.filter(shan=>shan.checked != true)
          this.forms.dynamicItem = shanLiu
          this.forms.dynamicItem.forEach(liu=>{
              this.$set(liu,'checked',false)
          })
      },
      //通用数组对象去重
      quchong(arr,key){
          let ret = []
          arr.forEach((item,index,self)=>{
              let compare = []
              ret.forEach((retitem,retindex,retself)=>{
                  compare.push(retitem[key])
              })
              if (compare.indexOf(item[key]) === -1){
                  ret.push(item)
              }
          })
          return ret
      },

      //下拉框获取后台参数
      paraLi(){
          let form = new FormData();
          form.append('totalQuestionTableId',this.proId)
          console.log(this.proId)
          return request({
              url:'/sql/problem_scan_logic/getParameterNameCollection',
              method:'post',
              data:form
          }).then(response=>{
              console.log(response)
              this.paraList = response
          })
      },

    /** 查询解决问题列表 */
    getList() {
      this.loading = true;
      listSolve_question(this.queryParams).then(response => {
        this.solve_questionList = response.rows;
        // alert(JSON.stringify(this.solve_questionList))
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        commond: null,
        comValue: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.commond)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加解决问题";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const commond = row.commond || this.ids
      getSolve_question(commond).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改解决问题";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.commond != null) {
            updateSolve_question(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSolve_question(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const commonds = row.commond || this.ids;
      this.$modal.confirm('是否确认删除解决问题编号为"' + commonds + '"的数据项？').then(function() {
        return delSolve_question(commonds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有解决问题数据项？').then(() => {
        this.exportLoading = true;
        return exportSolve_question(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>

<style>
  /*.el-divider--vertical{*/
  /*  !*display:inline-block;*!*/
  /*  !*width:1px;*!*/
  /*  height:100%;*/
  /*  !*margin:0 8px;*!*/
  /*  !*vertical-align:middle;*!*/
  /*  !*position:relative;*!*/
  /*}*/
</style>
