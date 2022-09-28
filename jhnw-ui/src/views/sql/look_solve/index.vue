<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="设备基本信息:"></el-form-item>
      <el-form-item label="品牌" prop="brand">
        <el-select v-model="queryParams.brand" placeholder="品牌"
                   filterable allow-create name="brand" @focus="general($event)" style="width: 150px">
          <el-option v-for="(item,index) in genList"
                     :key="index" :label="item.brand" :value="item.brand"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="型号" prop="type">
        <el-select v-model="queryParams.type" placeholder="型号"
                   filterable allow-create name="type" @focus="general($event)" style="width: 150px">
          <el-option v-for="(item,index) in genList"
                     :key="index" :label="item.type" :value="item.type"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="固件版本" prop="firewareVersion">
        <el-select v-model="queryParams.firewareVersion" placeholder="固件版本"
                   filterable allow-create name="firewareVersion" @focus="general($event)" style="width: 150px">
          <el-option v-for="(item,index) in genList"
                     :key="index" :label="item.firewareVersion" :value="item.firewareVersion"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="子版本" prop="subVersion">
        <el-select v-model="queryParams.subVersion" placeholder="子版本"
                   filterable allow-create name="subVersion" @focus="general($event)" style="width: 150px">
          <el-option v-for="(item,index) in genList"
                     :key="index" :label="item.subVersion" :value="item.subVersion"></el-option>
        </el-select>
      </el-form-item>
      <br/>
      <el-form-item label="问题概要:"></el-form-item>
      <el-form-item label="问题类型" prop="typeProblem">
        <el-select v-model="queryParams.typeProblem" placeholder="问题类型"
                   filterable allow-create name="typeProblem" @focus="general($event)">
          <el-option v-for="(item,index) in genList" :key="index"
                     :label="item.typeProblem" :value="item.typeProblem"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="范式名称">
        <el-select v-model="queryParams.temProName" placeholder="请选择范式名称"
                   filterable allow-create name="temProName" @focus="general($event)">
          <el-option v-for="(item,index) in genList" :key="index"
                     :label="item.temProName" :value="item.temProName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="自定义名称">
        <el-select v-model="queryParams.problemName" placeholder="自定义名称"
                   filterable allow-create name="problemName" @focus="general($event)">
          <el-option v-for="(item,index) in genList" :key="index"
                     :label="item.problemName" :value="item.problemName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="chakan">查看解决命令</el-button>
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
                       filterable allow-create @focus="paraLi" style="width: 150px">
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
  </div>
</template>

<script>
import { listLook_solve, getLook_solve, delLook_solve, addLook_solve, updateLook_solve, exportLook_solve } from "@/api/sql/look_solve";
import axios from 'axios'
import request from '@/utils/request'

export default {
  name: "Look_solve",
  data() {
    return {
      // 遮罩层
        //通用基本信息下拉集合
        genList:[],
        proNameList:[],
        typeProList:[],
        brandList:[],
        fireList:[],
        typeList:[],
        temProNameList:[],
        subList:[],
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
          notFinished:'---- More ----',
          typeProblem:'',
          temProName:'',
          requiredItems:false,
          remarks:''
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
      //先返回该问题ID，然后回显
      chakan(){
          this.showNo = true
          console.log(this.queryParams)
          let form = new FormData();
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
                  response.forEach(ee=>{
                      const wei = ee.replace(/=/g,":")
                      this.wDa.push(JSON.parse(wei))
                  })
                  const lookCha = []
                  this.wDa.forEach(e=>{
                      if (e.para == ''){
                          this.$set(e,'targetType','command')
                          lookCha.push(e)
                      }else if (e.para != ''){
                          this.$set(e,'targetType','compar')
                          lookCha.push(e)
                      }
                  })
                  lookCha.sort(function (a, b) { return a.pageIndex - b.pageIndex; })
                  this.forms.dynamicItem = this.forms.dynamicItem.concat(lookCha)
              })
          })
          // axios({
          //     url:'http://192.168.1.98/dev-api/sql/total_question_table/totalQuestionTableId',
          //     data:form
          // }).then(res=>{
          //     this.proId = res.data
          //     let form = new FormData();
          //     form.append('totalQuestionTableId',this.proId)
          //     axios({
          //         url:'http://192.168.1.98/dev-api/sql/SolveProblemController/queryCommandListBytotalQuestionTableId',
          //         data:form
          //     }).then(res=>{
          //         res.data.forEach(ee=>{
          //             const wei = ee.replace(/=/g,":")
          //             this.wDa.push(JSON.parse(wei))
          //         })
          //         const lookCha = []
          //         this.wDa.forEach(e=>{
          //             if (e.para == ''){
          //                 this.$set(e,'targetType','command')
          //                 lookCha.push(e)
          //             }else if (e.para != ''){
          //                 this.$set(e,'targetType','compar')
          //                 lookCha.push(e)
          //             }
          //         })
          //         lookCha.sort(function (a, b) { return a.pageIndex - b.pageIndex; })
          //         this.forms.dynamicItem = this.forms.dynamicItem.concat(lookCha)
          //     })
          // })
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
          delete this.queryParams.notFinished
          delete this.queryParams.remarks
          delete this.queryParams.requiredItems
          for (let i in this.queryParams){
              if (this.queryParams[i]==='{空}'){
                  this.queryParams[i]=''
              }
          }
          return request({
              url:'/sql/total_question_table/selectPojoList',
              method:'post',
              data:this.queryParams
          }).then(response=>{
              console.log(response)
              this.genList = this.quchong(response,this.who)
              let kong = {
                  [this.who] : '{空}'
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
      brandLi(){
          return request({
              url:'/sql/total_question_table/brandlist',
              method:'get'
          }).then(response=>{
              this.brandList = response
          })
      },
      typeLi(){
          const typeOne = {}
          const brandO = this.queryParams.brand
          this.$set(typeOne,'brand',brandO)
          return request({
              url:'/sql/total_question_table/typelist',
              method:'post',
              data:JSON.stringify(typeOne)
          }).then(response=>{
              this.typeList = response
          })
      },
      fireLi(){
          const fireOne = {}
          const brandO = this.queryParams.brand
          const typeO = this.queryParams.type
          this.$set(fireOne,'brand',brandO)
          this.$set(fireOne,'type',typeO)
          return request({
              url:'/sql/total_question_table/firewareVersionlist',
              method:'post',
              data:JSON.stringify(fireOne)
          }).then(response=>{
              this.fireList = response
          })
      },
      subLi(){
          const subOne = {}
          const brandO = this.queryParams.brand
          const typeO = this.queryParams.type
          const fireO = this.queryParams.firewareVersion
          this.$set(subOne,'brand',brandO)
          this.$set(subOne,'type',typeO)
          this.$set(subOne,'firewareVersion',fireO)
          return request({
              url:'/sql/total_question_table/subVersionlist',
              method:'post',
              data:JSON.stringify(subOne)
          }).then(response=>{
              this.subList = response
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
