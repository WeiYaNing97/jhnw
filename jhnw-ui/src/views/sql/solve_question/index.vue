<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="设备基本信息:"></el-form-item>
      <el-form-item label="品牌" prop="brand">
        <el-select v-model="queryParams.brand" placeholder="品牌"
                   filterable allow-create @blur="brandShu" @focus="brandLi" style="width: 150px">
          <el-option v-for="(item,index) in brandList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="型号" prop="type">
        <el-select v-model="queryParams.type" placeholder="型号"
                   filterable allow-create @blur="typeShu" @focus="typeLi" style="width: 150px">
          <el-option v-for="(item,index) in typeList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="固件版本" prop="firewareVersion">
        <el-select v-model="queryParams.firewareVersion" placeholder="固件版本"
                   filterable allow-create @blur="fireShu" @focus="fireLi" style="width: 150px">
          <el-option v-for="(item,index) in fireList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="子版本" prop="subVersion">
        <el-select v-model="queryParams.subVersion" placeholder="子版本"
                   filterable allow-create @blur="subShu" @focus="subLi" style="width: 150px">
          <el-option v-for="(item,index) in subList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="问题概要:"></el-form-item>
      <el-form-item label="问题类型" prop="typeProblem">
        <el-select v-model="queryParams.typeProblem" placeholder="问题类型"
                   filterable allow-create @focus="proType" @blur="typeProShu">
          <el-option v-for="(item,index) in typeProList" :key="index"
                     :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="问题名称">
        <el-select v-model="queryParams.problemName" placeholder="请选择问题"
                   filterable allow-create @focus="chawenti" @blur="proSelect">
          <el-option v-for="(item,index) in proNameList" :key="index"
                     :label="item.problemName" :value="item.problemName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="chakan">定义解决命令</el-button>
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
                       filterable allow-create @focus="paraLi" @blur="paraShu" style="width: 150px">
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
import { listSolve_question, getSolve_question, delSolve_question, addSolve_question, updateSolve_question, exportSolve_question } from "@/api/sql/solve_question";
import axios from 'axios'
import request from '@/utils/request'

export default {
  name: "Solve_question",
    components:{

    },
  data() {
    return {
      proNameList:[],
      typeProList:[],
      brandList:[],
      fireList:[],
      typeList:[],
      subList:[],
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
          commandId:'1'
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
    this.getList();
  },
    mounted:function(){

    },
  methods: {
      //点击末尾删除图标
      deleteItem (item, index) {
          this.forms.dynamicItem.splice(index,1)
      },
      //获取该问题ID
      chakan(){
          this.showNo = true
          let form = new FormData();
          for (var key in this.queryParams){
              form.append(key,this.queryParams[key]);
          }
          return request({
              url:'/sql/total_question_table/totalQuestionTableId',
              method:'post',
              data:form
          }).then(response=>{
              console.log(this.proId)
              this.proId = response
          })
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
          alert(handForm)
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
              console.log("成功")
          })
          //     // url:'/dev-api/sql/ConnectController/definitionProblem1/\'+handForm+\'/\'+handForm',
          //     // url:'/dev-api/sql/ConnectController/definitionProblem?totalQuestionTableId=23&aaa=33',
          //     // url:`/dev-api/sql/ConnectController/definitionProblem?totalQuestionTableId=${this.queryParams.brand}&aaa=${this.proId}`,
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
      //下拉框输入
      brandShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.brand = value
          }
      },
      typeShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.type = value
          }
      },
      fireShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.firewareVersion = value
          }
      },
      subShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.subVersion = value
          }
      },
      proSelect(e){
          let value = e.target.value
          if(value){
              this.queryParams.problemName = value
          }
      },
      typeProShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.typeProblem = value
          }
      },
      paraShu(){
          let value = e.target.value
          if(value){
              this.queryParams.para = value
          }
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
              method:'post',
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
              data:JSON.stringify(this.queryParams)
          }).then(response=>{
              this.typeProList = response
          })
      },
      //下拉框问题
      chawenti(){
          return request({
              url:'/sql/total_question_table/list',
              method:'post',
              data:JSON.stringify(this.queryParams)
          }).then(response=>{
              this.proNameList = response.rows
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
