<template>
  <div class="app-container">
    <el-row>
      <el-col :span="18">
        <el-form :model="queryParams" ref="queryForm" :inline="true">
          <el-form-item label="基本信息:"></el-form-item>
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
          <!--      <el-form-item>-->
          <!--        <el-select v-model="queryParams.commandId" style="width: 120px">-->
          <!--          <el-option label="所有问题" value="1"></el-option>-->
          <!--          <el-option label="未定义问题" value="0"></el-option>-->
          <!--        </el-select>-->
          <!--      </el-form-item>-->
          <el-form-item label="分类概要:"></el-form-item>
          <el-form-item label="范式分类" prop="typeProblem">
            <el-select v-model="queryParams.typeProblem" placeholder="范式分类"
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
<!--              @change="cproId"-->
              <el-option v-for="(item,index) in genList" :key="index"
                         :label="item.problemName" :value="item.problemName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="chaxun">查看定义</el-button>
            <!--        :disabled="!isNull"-->
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="xiugai" icon="el-icon-edit" :disabled="isUse">修改</el-button>
          </el-form-item>
          <!--      <el-form-item>-->
          <!--        <el-button type="primary" @click="gaibian">改变</el-button>-->
          <!--      </el-form-item>-->
        </el-form>

        <hr style='border:1px inset #D2E9FF;'>

        <el-form ref="forms" :inline="true" :model="forms" :disabled="zhidu" v-show="showNo">
          <el-form-item label="检测方法:"></el-form-item>
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
            <div v-if="item.targetType === 'command'" :key="index"
                 style="display: inline-block">
              <el-form-item label="命令" :prop="'dynamicItem.' + index + '.command'">
                <el-input v-model="item.command"></el-input>
              </el-form-item>
              <el-form-item label="命令校验">
                <el-select v-model="item.resultCheckId" placeholder="校验方式">
                  <el-option label="常规校验" value="1"></el-option>
                  <el-option label="自定义校验" value="0"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'match'" :key="index"
                 style="display: inline-block">
              <el-form-item label="全文精确匹配" :prop="'dynamicItem.' + index + '.matchContent'">
                <el-input v-model="item.matchContent"></el-input>
              </el-form-item>
              <el-form-item label="成功"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'failed'" style="display: inline-block;padding-left:308px">
              <el-form-item label="失败"></el-form-item>
              <el-form-item style="visibility: hidden">
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'dimmatch'" :key="index" style="display: inline-block">
              <el-form-item label="全文模糊匹配" :prop="'dynamicItem.' + index + '.matchContent'">
                <el-input v-model="item.matchContent"></el-input>
              </el-form-item>
              <el-form-item label="成功"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'wloop'" :key="index"
                 style="display: inline-block">
              <el-form-item label="循环" :prop="'dynamicItem.' + index + '.cycleStartId'">
                <el-input v-model="item.cycleStartId" style="width: 150px"></el-input>
              </el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'lipre'" :key="index" style="display:inline-block">
              <el-form-item label="按行精确匹配" :prop="'dynamicItem.' + index + '.relative'">
                <el-input v-model="item.relative" placeholder="下几行" style="width: 80px"></el-input>
              </el-form-item>
              <el-form-item label="匹配内容">
                <el-input v-model="item.matchContent" aria-placeholder="填写匹配内容"></el-input>
              </el-form-item>
              <el-form-item label="成功"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'failedH'" style="display: inline-block;padding-left: 466px">
              <el-form-item label="失败"></el-form-item>
              <el-form-item style="visibility: hidden">
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'dimpre'" :key="index" style="display: inline-block">
              <el-form-item label="按行模糊匹配" :prop="'dynamicItem.' + index + '.relative'">
                <el-input v-model="item.relative" placeholder="下几行" style="width: 80px"></el-input>
              </el-form-item>
              <el-form-item label="匹配内容">
                <el-input v-model="item.matchContent" aria-placeholder="填写匹配内容"></el-input>
              </el-form-item>
              <el-form-item label="成功"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'takeword'" :key="index" style="display: inline-block">
              <el-form-item label="取词" :prop="'dynamicItem.' + index + '.takeword'">
                <el-input v-model="item.rPosition" style="width: 80px" placeholder="第几个"></el-input> --
                <el-input v-model="item.length1" style="width: 80px" placeholder="几个词"></el-input>
                <el-select v-model="item.classify" placeholder="单词/行" style="width: 80px">
                  <el-option label="单词" value="W"></el-option>
                  <el-option label="字母" value="L"></el-option>
                  <el-option label="字符串" value="S"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-radio-group v-model="item.exhibit">
                  <el-radio label="显示" style="margin-right: 5px"></el-radio>
                  <el-input style="width: 120px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='显示'"></el-input>
                  <el-radio label="不显示" style="margin-right: 5px"></el-radio>
                  <el-input style="width: 120px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='不显示'"></el-input>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'analyse'" :key="index" style="display:inline-block">
              <el-form-item label="比较" v-show="bizui">
                <el-input v-model="item.compare" style="width: 217px" v-show="bizui" @input="bihou"></el-input>
              </el-form-item>
              <el-form-item label="比较" v-show="bixiala">
                <el-select v-model="item.bi" @change="bibi"
                           v-show="bixiala" placeholder="例如:品牌<5.20.99">
                  <el-option v-for="(item,index) in biList"
                             :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="成功"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'failedB'" style="display: inline-block;padding-left: 268px">
              <el-form-item label="失败"></el-form-item>
              <el-form-item style="visibility: hidden">
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>

            <div v-else-if="item.targetType === 'prodes'" :key="index" style="display:inline-block">
              <el-form-item label="有无问题">
                <el-select v-model="item.tNextId" placeholder="有无问题、完成">
                  <el-option label="异常" value="有问题"></el-option>
                  <el-option label="安全" value="无问题"></el-option>
                  <el-option label="完成" value="完成"></el-option>
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
                    <el-button @click="addItem('match',item)" type="primary">全文精确匹配</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('dimmatch',item)" type="primary">全文模糊匹配</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('lipre',item)" type="primary">按行精确匹配</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('dimpre',item)" type="primary">按行模糊匹配</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('takeword',item)" type="primary">取词</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('analyse',item)" type="primary">分析比较</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('wloop',item)" type="primary">循环位置</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button @click="addItem('prodes',item)" type="primary">问题名称</el-button>
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
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listLook_test, getLook_test, delLook_test, addLook_test, updateLook_test, exportLook_test } from "@/api/sql/look_test";
import axios from 'axios'
import  {MessageBox} from "element-ui";
import request from '@/utils/request'
import log from "../../monitor/job/log";

export default {
  name: "Look_test",
  data() {
    return {
        //全文、按行、比较
        allOne:[],
        allTwo:[],
        //查看问题集合
        lookLists:[],
        //全部展开
        zhankaiAll:true,
        //
        deptName:undefined,
        defaultProps: {
            children: 'children',
            label: 'label'
        },
        //比较隐藏
        bizui:false,
        bixiala:true,
        biList:['品牌','型号','固件版本','子版本'],
        //查看定义是否可以点击
        cdy:false,
        //新添加
        genList:[],
        checkedQ:false,
        zhidu:true,
        showNo:false,
        checked:false,
        proNameList:[],
        temProNameList:[],
        typeProList:[],
        brandList:[],
        fireList:[],
        typeList:[],
        subList:[],
        proId:'',
        // isLog:true,
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
      // 查看问题表格数据
      look_testList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        problemName:'',
        temProName:'',
        typeProblem:'',
        brand:'',
        type:'',
        firewareVersion:'',
        subVersion:''
        // commandId:'1'
      },
      // 表单参数
      form: {},
      isChange:false,
        huichasss:[],
      formss:{
        dynamicItemss:[
           {
              test:'test',
              onlyIndex:''
           }
        ],
        },
      forms: {
        dynamicItem:[
          {
             test:'test',
             onlyIndex:''
          }
          ],
        },
        fDa:[],
        newValue:[],
        oldValue:[
            {
                test:'test',
                onlyIndex:''
            }
        ],
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  watch:{
      // 根据输入筛选专项
      deptName(val) {
          this.$refs.treeone.filter(val);
      },
      newValue:{
          handler(val,oldVal){
              for (let i in this.newValue){
                  if (val[i] != this.oldValue[i]){
                      this.isChange = true
                      break
                  }else {
                      this.isChange = false
                  }
              }
          },
          deep:true,
          immediate:true
      },
  },
    computed:{
      // isNull(){
      //     return this.queryParams.brand != '' && this.queryParams.type != ''
      //         && this.queryParams.firewareVersion != '' && this.queryParams.subversion != ''
      //         && this.queryParams.problemName != ''
      // },
      isUse(){
          if (this.showNo == true){
              return false
          }
          return true
      }
    },
  methods: {
      a(){
          alert('sss')
      },
      gaibian(){
          this.newValue = JSON.parse(JSON.stringify(this.forms.dynamicItem))
          alert(JSON.stringify(this.newValue))
          alert(JSON.stringify(this.oldValue))
          // this.beifen.dynamicItem = JSON.parse(JSON.stringify(this.forms.dynamicItem))
          if (this.isChange){
              alert('改变')
          }else {
              alert('没变')
          }
      },
      //比较选中触发
      bibi(){
          this.bizui = true
          this.forms.dynamicItem.forEach(e=>{
              if (e.targetType === 'analyse'){
                  const bixuan = e.bi
                  this.$set(e,'compare',bixuan)
              }
          })
          this.bixiala = false
      },
      //比较输入框为空后
      bihou(){
          this.forms.dynamicItem.forEach(e=>{
              if (e.targetType === 'analyse'){
                  if (e.compare === ''){
                      this.bixiala = true
                      this.bizui = false
                  }
              }
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
          useForm.forEach(eeee=>{
              const thisIndex = useForm.indexOf(eeee)
              if(useForm.length != thisIndex+1){
                  const thisNext = useForm[thisIndex+1]
                  this.$set(eeee,'nextIndex',thisNext.onlyIndex)
              }
              if (eeee.action === '取词'){
                  if (eeee.classify === 'W'){
                      eeee.length = `${eeee.length1}W`
                  }else if (eeee.classify === 'L'){
                      eeee.length = `${eeee.length1}L`
                  }else if (eeee.classify === 'S'){
                      eeee.length = `${eeee.length1}S`
                  }
              }
              this.$set(eeee,'pageIndex',thisIndex+1)
              if (eeee.targetType == 'takeword'){
                  const takeWordt = useForm.indexOf(eeee)
                  var quciC = ''
                  useForm.map((e11)=>{
                      const takeWl = useForm.indexOf(e11)
                      if(takeWl == takeWordt-1){
                          quciC = e11.matchContent
                      }
                  })
                  this.$set(eeee,'matchContent',quciC)
              }
          })
          const handForm = useForm.map(x => JSON.stringify(x))
          let form1 = new FormData();
          form1.append('totalQuestionTableId',this.proId)
          form1.append('jsonPojoList',handForm)
          console.log(handForm)
          this.newValue = JSON.parse(JSON.stringify(this.forms.dynamicItem))
          // alert(JSON.stringify(this.newValue))
          // alert(JSON.stringify(this.oldValue))
          return request({
              url:`/sql/DefinitionProblemController/updateAnalysis?totalQuestionTableId=${this.proId}`,
              method:'post',
              data:handForm
          }).then(response=>{
              this.$message.success('提交修改成功!')
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
      //下拉列表通用
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
              this.proId = response[0].id
              console.log(this.proId)
              this.lookLists = []
              //转化为树结构
              for (let i = 0;i<response.length;i++){
                  let xinall = response[i].brand + ' ' + response[i].type + ' ' + response[i].firewareVersion + ' ' + response[i].subVersion
                  let loser = {
                      label:xinall,
                      children: [{
                          label:response[i].typeProblem,
                          children:[{
                              label:response[i].temProName,
                              id:response[i].id
                          }]
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
      //筛选条件
      filterNode(value, data){
          if (!value) return true;
          return data.label.indexOf(value) !== -1;
      },
      //循环项获取index
      wcycle(item,event){
          const cycleId = item.onlyIndex
          this.forms.dynamicItem.forEach(cy=>{
              // if (cy.targetType === 'wloop'){
              //     this.$set(cy,'cycleStartId',cycleId)
              // }
              if (cy.hasOwnProperty('cycleStartId') != true && cy.targetType == 'wloop'){
                  this.$set(cy,'cycleStartId',cycleId)
              }else if (cy.cycleStartId == ''){
                  this.$set(cy,'cycleStartId',cycleId)
              }
          })
      },
      //列表展示树结构
      handleNodeClick(lookLists) {
          this.proId = lookLists.id
          console.log(this.proId)
          let id = this.proId
          if(typeof(id) === 'number'){
              this.forms.dynamicItem = this.formss.dynamicItemss
              this.huichasss = []
              this.showNo= true
              return request({
                  url:'/sql/total_question_table/'+id,
                  method:'get',
              }).then(response=> {
                  console.log(response.data)
                  this.queryParams.brand = response.data.brand
                  this.queryParams.type = response.data.type
                  this.queryParams.firewareVersion = response.data.firewareVersion
                  this.queryParams.subVersion = response.data.subVersion
                  this.queryParams.typeProblem = response.data.typeProblem
                  this.queryParams.temProName = response.data.temProName
                  this.queryParams.problemName = response.data.problemName
                  return request({
                      url: '/sql/DefinitionProblemController/getAnalysisList',
                      method: 'post',
                      data: response.data
                  }).then(response => {
                      console.log(response)
                      if (response.msg === '操作成功'){
                          this.cdy = true
                      }
                      this.fDa = []
                      response.data.forEach(l => {
                          const wei = l.replace(/"=/g, '":')
                          this.fDa.push(JSON.parse(wei))
                          // const weizai = eval(wei)
                          // this.fDa.push(JSON.parse(weizai))
                          // this.fDa.push(eval(wei))
                      })
                      const quanjf = ''
                      const hangjf = ''
                      const quanmf = ''
                      const hangmf = ''
                      const bif = ''
                      this.fDa.forEach(chae => {
                          if (chae.hasOwnProperty('command') == true) {
                              this.$set(chae, 'targetType', 'command')
                              if (chae.resultCheckId === 0) {
                                  this.$set(chae, 'resultCheckId', '自定义校验')
                              } else if (chae.resultCheckId === 1) {
                                  this.$set(chae, 'resultCheckId', '常规校验')
                              }
                              this.huichasss.push(chae)
                          } else if (chae.matched === '全文精确匹配') {
                              this.$set(chae, 'targetType', 'match')
                              this.huichasss.push(chae)
                              this.quanjf = chae.onlyIndex
                          } else if (chae.onlyIndex === this.quanjf && chae.trueFalse === '失败') {
                              this.$set(chae, 'targetType', 'matchfal')
                              this.huichasss.push(chae)
                          } else if (chae.matched === '按行精确匹配') {
                              this.$set(chae, 'targetType', 'lipre')
                              this.huichasss.push(chae)
                              this.hangjf = chae.onlyIndex
                          } else if (chae.onlyIndex === this.hangjf && chae.trueFalse === '失败') {
                              this.$set(chae, 'targetType', 'liprefal')
                              this.huichasss.push(chae)
                          } else if (chae.action === '取词') {
                              this.$set(chae, 'targetType', 'takeword')
                              // this.$set(chae,'classify',chae.length.slice(chae.length.length-1))
                              //伟仔疑问
                              if (chae.length.slice(chae.length.length - 1) === 'W') {
                                  this.$set(chae, 'classify', '单词')
                              } else if (chae.length.slice(chae.length.length - 1) === 'S') {
                                  this.$set(chae, 'classify', '字符串')
                              } else if (chae.length.slice(chae.length.length - 1) === 'L') {
                                  this.$set(chae, 'classify', '字母')
                              }
                              // chae.classify = chae.length.slice(chae.length.length-1)
                              chae.length1 = chae.length.slice(0, chae.length.length - 1)
                              this.huichasss.push(chae)
                          } else if (chae.matched === '全文模糊匹配') {
                              this.$set(chae, 'targetType', 'dimmatch')
                              this.huichasss.push(chae)
                              this.quanmf = chae.onlyIndex
                          } else if (chae.onlyIndex === this.quanmf && chae.trueFalse === '失败') {
                              this.$set(chae, 'targetType', 'dimmatchfal')
                              this.huichasss.push(chae)
                          } else if (chae.matched === '按行模糊匹配') {
                              this.$set(chae, 'targetType', 'dimpre')
                              this.huichasss.push(chae)
                              this.hangmf = chae.onlyIndex
                          } else if (chae.onlyIndex === this.quanmf && chae.trueFalse === '失败') {
                              this.$set(chae, 'targetType', 'dimprefal')
                              this.huichasss.push(chae)
                          } else if (chae.action === '问题') {
                              this.$set(chae, 'targetType', 'prodes')
                              if (chae.tNextId === '异常') {
                                  this.$set(chae, 'tNextId', '有问题')
                              } else if (chae.tNextId === '安全') {
                                  this.$set(chae, 'tNextId', '无问题')
                              }
                              this.huichasss.push(chae)
                          } else if (chae.action === '循环') {
                              this.$set(chae, 'targetType', 'wloop')
                              this.huichasss.push(chae)
                          } else if (chae.action === '比较') {
                              this.$set(chae, 'targetType', 'analyse')
                              this.bizui = true
                              this.bixiala = false
                              this.huichasss.push(chae)
                              this.bif = chae.onlyIndex
                          } else if (chae.onlyIndex === this.bif && chae.trueFalse === '失败') {
                              this.$set(chae, 'targetType', 'analysefal')
                              this.huichasss.push(chae)
                          }
                      })
                      this.huichasss.sort(function (a, b) {
                          return a.pageIndex - b.pageIndex;
                      })
                      this.forms.dynamicItem = this.forms.dynamicItem.concat(this.huichasss)
                      this.oldValue = JSON.parse(JSON.stringify(this.forms.dynamicItem))
                  })
              })
          }
      },
      //下拉框获取后台数据
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
          }).then(response=>{
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
                  this.temProNameList = response
                  console.log(response)
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
      //获取问题ID
      cproId(){
          // this.$delete(this.queryParams,'commandId')
          let form = new FormData()
          console.log(this.queryParams)
          for (var key in this.queryParams){
              form.append(key,this.queryParams[key])
          }
          return request({
              url:'/sql/total_question_table/totalQuestionTableId',
              method:'post',
              data:form
          }).then(response=>{
              console.log(response)
              this.proId = response
          })
      },
      //删除
      deleteItem (item, index) {
          this.forms.dynamicItem.splice(index,1)
      },
      //删除(多选项选中的)
      deleteItemp(item,index){
          this.forms.dynamicItem.splice(index,1)
          const shaIn = item.onlyIndex
          const shaAll = this.forms.dynamicItem.findIndex(it=>{
              if (it.onlyIndex === shaIn){
                  return true
              }
          })
          this.forms.dynamicItem.splice(shaAll,1)
      },
      //回显定义问题
      chaxun(){
          console.log(this.queryParams)
          if (this.lookLists.length != 1 && this.cdy != true){
              alert('查找条件过于模糊,请完善')
          }else {
              this.forms.dynamicItem = this.formss.dynamicItemss
              this.huichasss = []
              this.showNo= true
              return request({
                  url:'/sql/DefinitionProblemController/getAnalysisList',
                  method:'post',
                  data:JSON.stringify(this.queryParams)
              }).then(response=>{
                  console.log(response)
                  this.fDa = []
                  response.data.forEach(l=>{
                      const wei = l.replace(/"=/g,'":')
                      this.fDa.push(JSON.parse(wei))
                      // const weizai = eval(wei)
                      // this.fDa.push(JSON.parse(weizai))
                      // this.fDa.push(eval(wei))
                  })
                  this.fDa.forEach(chae=>{
                      if (chae.hasOwnProperty('command') == true){
                          this.$set(chae,'targetType','command')
                          if (chae.resultCheckId === 0){
                              this.$set(chae,'resultCheckId','自定义校验')
                          }else  if (chae.resultCheckId === 1){
                              this.$set(chae,'resultCheckId','常规校验')
                          }
                          this.huichasss.push(chae)
                      }else if (chae.matched === '全文精确匹配'){
                          this.$set(chae,'targetType','match')
                          this.huichasss.push(chae)
                          this.allOne.push(chae.onlyIndex)
                      }else if (chae.matched === '全文模糊匹配'){
                          this.$set(chae,'targetType','dimmatch')
                          this.huichasss.push(chae)
                          this.allOne.push(chae.onlyIndex)
                      }else if (chae.matched === '按行精确匹配'){
                          this.$set(chae,'targetType','lipre')
                          this.huichasss.push(chae)
                          this.allTwo.push(chae.onlyIndex)
                      }else if (chae.matched === '按行模糊匹配'){
                          this.$set(chae,'targetType','dimpre')
                          this.huichasss.push(chae)
                          this.allTwo.push(chae.onlyIndex)
                      }else if (chae.action === '比较'){
                          this.$set(chae,'targetType','analyse')
                          // bixiala bizui
                          this.bizui = true
                          this.bixiala = false
                          this.huichasss.push(chae)
                      }else if(chae.trueFalse === '失败'){
                          if (this.allOne.includes(chae.onlyIndex)){
                              this.$set(chae,'targetType','failed')
                          }else if (this.allTwo.includes(chae.onlyIndex)){
                              this.$set(chae,'targetType','failedH')
                          }else {
                              this.$set(chae,'targetType','failedB')
                          }
                          this.huicha.push(chae)
                      }else if (chae.action === '取词'){
                          this.$set(chae,'targetType','takeword')
                          // this.$set(chae,'classify',chae.length.slice(chae.length.length-1))
                          if (chae.length.slice(chae.length.length-1) === 'W'){
                              this.$set(chae,'classify','单词')
                          }else  if (chae.length.slice(chae.length.length-1) === 'S'){
                              this.$set(chae,'classify','字符串')
                          }else if (chae.length.slice(chae.length.length-1) === 'L'){
                              this.$set(chae,'classify','字母')
                          }
                          // chae.classify = chae.length.slice(chae.length.length-1)
                          chae.length1 = chae.length.slice(0,chae.length.length-1)
                          this.huichasss.push(chae)
                      }else if (chae.action === '问题'){
                          this.$set(chae,'targetType','prodes')
                          if (chae.tNextId === '异常'){
                              this.$set(chae,'tNextId','有问题')
                          }else if (chae.tNextId === '安全'){
                              this.$set(chae,'tNextId','无问题')
                          }
                          this.huichasss.push(chae)
                      }else if (chae.action === '循环'){
                          this.$set(chae,'targetType','wloop')
                          this.huichasss.push(chae)
                      }
                  })
                  this.huichasss.sort(function (a, b) { return a.pageIndex - b.pageIndex; })
                  this.forms.dynamicItem = this.forms.dynamicItem.concat(this.huichasss)
                  this.oldValue = JSON.parse(JSON.stringify(this.forms.dynamicItem))
              })
          }
      },
      //编辑
      xiugai(){
          MessageBox.confirm('确定去修改吗？','提示').then(c=>{
              this.zhidu = false
          }).catch(ee=>{
              this.$message.warning('取消修改!')
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
      //选择删除
      shanchu(){
          const shanLiu = this.forms.dynamicItem.filter(shan=>shan.checked != true)
          this.forms.dynamicItem = shanLiu
          this.forms.dynamicItem.forEach(liu=>{
              this.$set(liu,'checked',false)
          })
      },
      chadingyi(){
          alert()
      },
      //添加表单项
      addItem(type,item){
          const thisData = Date.now()
          const item1 = {
              targetType: type,
              onlyIndex:thisData,
              trueFalse:""
          }
          this.$set(item1,'checked',false)
          const thisIndex = this.forms.dynamicItem.indexOf(item)
          // this.$set(item,'nextIndex',thisData)
          if(type == 'match'){
              this.$set(item1,'matched','全文精确匹配')
              this.$set(item1,'trueFalse','成功')
              const item2 = {
                  targetType:'failed',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'dimmatch'){
              this.$set(item1,'matched','全文模糊匹配')
              this.$set(item1,'trueFalse','成功')
              const item2 = {
                  targetType:'failed',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'lipre'){
              this.$set(item1,'matched','按行精确匹配')
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'position',0)
              const item2 = {
                  targetType:'failedH',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'dimpre'){
              this.$set(item1,'matched','按行模糊匹配')
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'position',0)
              const item2 = {
                  targetType:'failedH',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if (type == 'analyse'){
              this.$set(item1,'action','比较')
              this.$set(item1,'trueFalse','成功')
              const item2 = {
                  targetType:'failedB',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'takeword'){
              this.$set(item1,'action','取词')
          }
          if (type == 'wloop'){
              this.$set(item1,'action','循环')
          }
          if (type == 'prodes'){
              this.$set(item1,'action','问题')
              this.$set(item1,'problemId',this.proId)
          }
          this.forms.dynamicItem.splice(thisIndex+1,0,item1)
      },
      numToStr(num){
          num = num.toString()
          return num
      },

    /** 查询查看问题列表 */
    getList() {
      this.loading = true;
      listLook_test(this.queryParams).then(response => {
        this.look_testList = response.rows;
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
        id: null,
        brand: null,
        version: null
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
  }
};
</script>

<style>
  .el-form-item{
    margin-top: 5px;
    margin-bottom: 5px;
  }
</style>
