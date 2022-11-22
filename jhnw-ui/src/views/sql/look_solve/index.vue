<template>
  <div class="app-container">
    <el-row>
      <el-col :span="18">
        <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
          <el-form-item label="设备基本信息:"></el-form-item>
          <el-form-item label="品牌" prop="brand">
            <el-select v-model="queryParams.brand" placeholder="品牌"
                       name="brand" @focus="general($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.brand" :value="item.brand"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="型号" prop="type">
            <el-select v-model="queryParams.type" placeholder="型号"
                       name="type" @focus="general($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.type" :value="item.type"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="固件版本" prop="firewareVersion">
            <el-select v-model="queryParams.firewareVersion" placeholder="固件版本"
                       name="firewareVersion" @focus="general($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.firewareVersion" :value="item.firewareVersion"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="子版本" prop="subVersion">
            <el-select v-model="queryParams.subVersion" placeholder="子版本"
                       name="subVersion" @focus="general($event)" style="width: 150px">
              <el-option v-for="(item,index) in genList"
                         :key="index" :label="item.subVersion" :value="item.subVersion"></el-option>
            </el-select>
          </el-form-item>
          <br/>
          <el-form-item label="问题概要:"></el-form-item>
          <el-form-item label="问题类型" prop="typeProblem">
            <el-select v-model="queryParams.typeProblem" placeholder="问题类型"
                       name="typeProblem" @focus="general($event)">
              <el-option v-for="(item,index) in genList" :key="index"
                         :label="item.typeProblem" :value="item.typeProblem"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="范式名称">
            <el-select v-model="queryParams.temProName" placeholder="请选择范式名称"
                       name="temProName" @focus="general($event)">
              <el-option v-for="(item,index) in genList" :key="index"
                         :label="item.temProName" :value="item.temProName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="自定义名称">
            <el-select v-model="queryParams.problemName" placeholder="自定义名称"
                       name="problemName" @focus="general($event)">
              <el-option v-for="(item,index) in genList" :key="index"
                         :label="item.problemName" :value="item.problemName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="chakan">查看修复命令</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="xiugai" icon="el-icon-edit">修改</el-button>
          </el-form-item>
        </el-form>
        <hr style='border:1px inset #D2E9FF;'>
        <el-form ref="forms" :inline="true" :model="forms" v-show="showNo" :disabled="zhidu">
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
          <el-form-item>
            <el-button @click="delRepair" type="primary">删除</el-button>
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
import { listLook_solve, getLook_solve, delLook_solve, addLook_solve, updateLook_solve, exportLook_solve } from "@/api/sql/look_solve";
import axios from 'axios'
import {MessageBox} from "element-ui"
import request from '@/utils/request'

export default {
  name: "Look_solve",
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
        //返回列表个数
        lieNum:0,
        //返回列表复制
        // returnCopy:[],
        // 遮罩层
        //
        lookCha:[],
        //只读
        zhidu:true,
        //通用基本信息下拉集合
        genList:[],
        proNameList:[],
        typeProList:[],
        temProNameList:[],
        paraList:[],
        wDa:[],
        showNo:false,
        proId:'',
        checkedQ:false,
        forms:{
            dynamicItem:[
                {
                    test:'test',
                    onlyIndex:''
                }
            ]
        },
        formss:{
            dynamicItemss:[
                {
                    test:'test',
                    onlyIndex:''
                }
            ]
        },
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
      // 查看解决表格数据
      look_solveList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
          brand: '',
          type: '',
          firewareVersion: '',
          subVersion: '',
          // commandId:'1',
          problemName:'',
          // notFinished:'---- More ----',
          typeProblem:'',
          temProName:'',
          // requiredItems:false,
          // remarks:''
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
      //筛选条件
      filterNode(value, data){
          if (!value) return true;
          return data.label.indexOf(value) !== -1;
      },
      //右侧列表点击
      handleNodeClick(lookLists){
          this.proId = lookLists.id
          console.log(this.proId)
          this.lieNum = 1
          console.log(this.lieNum)
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
              this.forms.dynamicItem = this.formss.dynamicItemss
              this.lookCha = []
              console.log(this.queryParams)
              let form = new FormData()
              for (var key in this.queryParams){
                  form.append(key,this.queryParams[key]);
              }
              return request({
                  url:'/sql/total_question_table/totalQuestionTableId',
                  method:'post',
                  data:form
              }).then(response=>{
                  this.proId = response
                  let form = new FormData()
                  form.append('totalQuestionTableId',this.proId)
                  return request({
                      url:'/sql/SolveProblemController/queryCommandListBytotalQuestionTableId',
                      method:'post',
                      data:form
                  }).then(response=>{
                      console.log(response)
                      if (response.length != 0){
                          this.showNo = true
                          this.wDa = []
                          response.forEach(ee=>{
                              const wei = ee.replace(/=/g,":")
                              this.wDa.push(JSON.parse(wei))
                          })
                          console.log(this.wDa)
                          this.wDa.forEach(e=>{
                              if (e.para == ''){
                                  this.$set(e,'targetType','command')
                                  this.lookCha.push(e)
                              }else if (e.para != ''){
                                  this.$set(e,'targetType','compar')
                                  this.lookCha.push(e)
                              }
                          })
                          this.lookCha.sort(function (a, b) { return a.pageIndex - b.pageIndex; })
                          this.forms.dynamicItem = this.forms.dynamicItem.concat(this.lookCha)
                      }else {
                          alert('未定义该问题解决逻辑，请定义!')
                      }
                  })
              })
          })
      },
      //编辑
      xiugai(){
          MessageBox.confirm('确定去修改吗？','提示').then(c=>{
              this.zhidu = false
          }).catch(ee=>{
              this.$message.warning('取消修改!')
          })
      },
      //先返回该问题ID，然后回显
      chakan(){
          if (this.lieNum !=1){
              //TODO 做标记用的
              alert('查找条件过于模糊,请完善!')
          }else {
              // this.queryParams.brand = this.returnCopy[0].brand
              // this.queryParams.type = this.returnCopy[0].type
              // this.queryParams.firewareVersion = this.returnCopy[0].firewareVersion
              // this.queryParams.subVersion = this.returnCopy[0].subVersion
              // this.queryParams.typeProblem = this.returnCopy[0].typeProblem
              // this.queryParams.temProName = this.returnCopy[0].temProName
              // this.queryParams.problemName = this.returnCopy[0].problemName
              this.forms.dynamicItem = this.formss.dynamicItemss
              this.lookCha = []
              console.log(this.queryParams)
              let form = new FormData()
              for (var key in this.queryParams){
                  form.append(key,this.queryParams[key]);
              }
              return request({
                  url:'/sql/total_question_table/totalQuestionTableId',
                  method:'post',
                  data:form
              }).then(response=>{
                  this.proId = response
                  let form = new FormData()
                  form.append('totalQuestionTableId',this.proId)
                  return request({
                      url:'/sql/SolveProblemController/queryCommandListBytotalQuestionTableId',
                      method:'post',
                      data:form
                  }).then(response=>{
                      console.log(response)
                      if (response.length != 0){
                          this.showNo = true
                          this.wDa = []
                          response.forEach(ee=>{
                              const wei = ee.replace(/=/g,":")
                              this.wDa.push(JSON.parse(wei))
                          })
                          this.wDa.forEach(e=>{
                              if (e.para == ''){
                                  this.$set(e,'targetType','command')
                                  this.lookCha.push(e)
                              }else if (e.para != ''){
                                  this.$set(e,'targetType','compar')
                                  this.lookCha.push(e)
                              }
                          })
                          this.lookCha.sort(function (a, b) { return a.pageIndex - b.pageIndex; })
                          this.forms.dynamicItem = this.forms.dynamicItem.concat(this.lookCha)
                      }else {
                          alert('未定义该问题解决逻辑，请定义!')
                      }
                  })
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
          return request({
              url:`/sql/command_logic/updateProblemSolvingCommand?totalQuestionTableId=${this.proId}`,
              method:'post',
              data:handForm
          }).then(response=>{
              this.$message.success('提交成功!')
              console.log("成功")
          })
      },
      //删除修复问题
      delRepair(){
          console.log(this.proId)
          MessageBox.confirm('确定删除吗？','提示').then(c=>{
              return request({
                  url:'/sql/command_logic/deleteProblemSolvingCommand',
                  method:'post',
                  data:this.proId
              }).then(response=>{
                  console.log('删除成功')
              })
          }).catch(ee=>{
              this.$message.warning('取消删除!')
          })
      },
      //点击末尾删除图标
      deleteItem (item, index) {
          this.forms.dynamicItem.splice(index,1)
      },
      //下拉框输入
      //数字转换
      numToStr(num){
          num = num.toString()
          return num
      },
      //新增表单项
      addItem(type,item){
          const thisData = Date.now()
          const item1 = {
              targetType: type,
              onlyIndex:thisData,
          }
          const thisIndex = this.forms.dynamicItem.indexOf(item)
          this.forms.dynamicItem.splice(thisIndex+1,0,item1)
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
      //下拉列表通用
      general(e){
          this.who = e.target.getAttribute('name')
          // delete this.queryParams.notFinished
          // delete this.queryParams.remarks
          // delete this.queryParams.requiredItems
          let newPar = {}
          for (var key in this.queryParams){
              newPar[key] = this.queryParams[key]
          }
          for (let i in newPar){
              if (newPar[i] === 'null'){
                  newPar[i] = ''
              }
          }
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

              this.lieNum = response.length
              // this.returnCopy = response
              this.genList = this.quchong(response,this.who)
              let kong = {
                  [this.who] : 'null'
              }
              this.genList.push(kong)
          })
      },
      //下拉框获取后台参数
      paraLi(){
          let form = new FormData();
          form.append('totalQuestionTableId',this.proId)
          return request({
              url:'/sql/problem_scan_logic/getParameterNameCollection',
              method:'post',
              data:form
          }).then(response=>{
              this.paraList = response
          })
      },
      proType(){
          return request({
              url:'/sql/total_question_table/typeProblemlist',
              method:'post',
          }).then(response=>{
              console.log(response)
              this.typeProList = response
          })
      },
      temPro(e){
          var type0 = this.queryParams.typeProblem
          if(type0 != ''){
              return request({
                  url:'/sql/total_question_table/temProNamelist',
                  method:'post',
                  data:type0
              }).then(response=>{
                  console.log(response)
                  this.temProNameList = response
              })
          }else {
              this.$message.warning('问题类型未选择')
          }
      },
      //下拉框问题
      chawenti(){
          const wentilist = {}
          const brandO = this.queryParams.brand
          const typeO = this.queryParams.type
          const firO = this.queryParams.firewareVersion
          const subO = this.queryParams.subVersion
          const protypeO = this.queryParams.typeProblem
          const pronameO = this.queryParams.temProName
          this.$set(wentilist,'brand',brandO)
          this.$set(wentilist,'type',typeO)
          this.$set(wentilist,'firewareVersion',firO)
          this.$set(wentilist,'subVersion',subO)
          this.$set(wentilist,'typeProblem',protypeO)
          this.$set(wentilist,'temProName',pronameO)
          return request({
              url:'/sql/total_question_table/problemNameList',
              method:'post',
              data:JSON.stringify(wentilist)
          }).then(response=>{
              console.log(response)
              this.proNameList = response
          })
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
    /** 查询查看解决列表 */
    getList() {
      this.loading = true;
      listLook_solve(this.queryParams).then(response => {
        this.look_solveList = response.rows;
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
        command: null,
        comvalue: null
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
      this.ids = selection.map(item => item.command)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
  }
};
</script>
