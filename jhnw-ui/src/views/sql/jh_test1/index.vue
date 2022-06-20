<template>
  <div class="app-container" @contextmenu="showMenu">
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="设备基本信息:"></el-form-item>
      <el-form-item label="品牌" prop="brand">
        <el-select v-model="queryParams.brand" placeholder="品牌"
                   filterable allow-create @blur="brandShu" @focus.once="brandLi" style="width: 150px">
          <el-option v-for="(item,index) in brandList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="型号" prop="type">
        <el-select v-model="queryParams.type" placeholder="型号"
                   filterable allow-create @blur="typeShu" @focus.once="typeLi" style="width: 150px">
          <el-option v-for="(item,index) in typeList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="固件版本" prop="firewareVersion">
        <el-select v-model="queryParams.firewareVersion" placeholder="固件版本"
                   filterable allow-create @blur="fireShu" @focus.once="fireLi" style="width: 150px">
          <el-option v-for="(item,index) in fireList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="子版本" prop="subVersion">
        <el-select v-model="queryParams.subVersion" placeholder="子版本"
                   filterable allow-create @blur="subShu" @focus.once="subLi" style="width: 150px">
          <el-option v-for="(item,index) in subList"
                     :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <br/>
<!--      <el-form-item>-->
<!--        <el-select v-model="queryParams.commandId" style="width: 120px">-->
<!--          <el-option label="所有问题" value="1"></el-option>-->
<!--          <el-option label="未定义问题" value="0"></el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
      <el-form-item label="问题概要:"></el-form-item>
      <el-form-item label="问题类型" prop="typeProblem">
        <el-select v-model="queryParams.typeProblem" placeholder="问题类型"
                   filterable allow-create @focus.once="proType" @blur="typeProShu">
          <el-option v-for="(item,index) in typeProList" :key="index"
                     :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="问题名称">
        <el-select v-model="queryParams.problemName" placeholder="请选择问题"
                   filterable allow-create @focus.once="chawenti" @blur="proSelect">
          <el-option v-for="(item,index) in proNameList" :key="index"
                     :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="标识符">
        <el-input v-model="queryParams.notFinished"
                  clearable
                  @contextmenu="ssss"
        ></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="huoquid">提交问题</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="hand">出现</el-button>
      </el-form-item>

    </el-form>
    <hr style='border:1px inset #D2E9FF;'>

    <el-form ref="forms" :inline="true" :model="forms" :disabled="zhidu">
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
            <el-select v-model="jiaoyan" placeholder="校验方式" value="常规校验">
              <el-option label="常规校验" value="常规校验" selected></el-option>
              <el-option label="自定义校验" value="自定义校验"></el-option>
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
        <div v-else-if="item.targetType === 'matchfal'"
             style="display: inline-block;padding-left:308px">
          <el-form-item label="失败"></el-form-item>
        </div>

        <div v-else-if="item.targetType === 'wloop'" :key="index"
             style="display: inline-block">
          <el-form-item label="循环" :prop="'dynamicItem.' + index + '.cycleStartId'">
            <el-input v-model="item.cycleStartId" style="width: 150px"
                      clearable @focus="shaxun"></el-input>
<!--              disabled="true"-->
          </el-form-item>
          <el-form-item>
            <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
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
        <div v-else-if="item.targetType === 'dimmatchfal'" style="display: inline-block;padding-left: 308px">
          <el-form-item label="失败"></el-form-item>
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
        <div v-else-if="item.targetType === 'liprefal'" style="display: inline-block;padding-left: 470px">
          <el-form-item label="失败"></el-form-item>
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
        <div v-else-if="item.targetType === 'dimprefal'" style="display: inline-block;padding-left: 470px">
          <el-form-item label="失败"></el-form-item>
        </div>

        <div v-else-if="item.targetType === 'takeword'" :key="index" style="display: inline-block">
          <el-form-item label="取词" :prop="'dynamicItem.' + index + '.takeword'">
            <el-input v-model="item.rPosition" style="width: 80px" placeholder="第几个"></el-input> --
            <el-input v-model="item.length" style="width: 80px" placeholder="几个词"></el-input>
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
          <el-form-item label="比较">
            <el-input v-model="item.compare" v-show="bizui" @input="bihou"></el-input>
          </el-form-item>
          <el-form-item>
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
        <div v-else-if="item.targetType === 'analysefal'" style="display: inline-block;padding-left: 278px">
          <el-form-item label="失败"></el-form-item>
        </div>

        <div v-else-if="item.targetType === 'prodes'" :key="index" style="display:inline-block">
          <el-form-item label="有无问题">
<!--            prop="'dynamicItem.' + index + '.prodes'"-->
<!--            <el-input v-model="item.prodes"></el-input>-->
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
        <el-button @click="tijiao" type="primary">提交</el-button>
<!--        有用-->
<!--        <el-button @click="jiejue" type="primary">解决问题</el-button>-->
<!--        <el-button @click="proname" type="primary">问题名</el-button>-->
      </el-form-item>
<!--      <el-form-item>-->
<!--        <el-button type="primary" @click="onSubmit">提交</el-button>-->
<!--      </el-form-item>-->

    </el-form>

    <TinymceEditor :proId="proId" v-show="showha"></TinymceEditor>

    <vue-context-menu style="width: 172px;background: #eee;margin-left: auto"
                      :contextMenuData="contextMenuData" @deletedata="deletedata" @showhelp="showhelp">
    </vue-context-menu>

  </div>
</template>

<script>
import { listJh_test1, getJh_test1, delJh_test1, addJh_test1, updateJh_test1, exportJh_test1 } from "@/api/sql/jh_test1";
import axios from 'axios';
import TinymceEditor from "@/components/Tinymce/TinymceEditor";

export default {
  name: "Jh_test1",
    components:{
        TinymceEditor,
    },
  data() {
    return {
        //右键
        contextMenuData:{
          menuName:"demo",
            axis:{
              x:null,
                y:null
            },
            menulists:[
                {
                    fnHandler:'deletedata',
                    btnName:'删除当前数据'
                },
                {
                    fnHandler:'showhelp',
                    btnName:'帮助'
                }
            ]
        },
        showha:false,
        jiaoyan:'自定义校验',
        bizui:false,
        bixiala:true,
        radio:'1',
        proNameList:[],
        typeProList:[],
        brandList:[],
        fireList:[],
        typeList:[],
        subList:[],
        biList:['品牌','型号','固件版本','子版本'],
        checkedQ:false,
        // checked:false,
        proId:'',
        zhidu:false,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 是否显示弹出层
      open: false,
        position:0,
      // 查询参数
      queryParams: {
          brand: '',
          type: '',
          firewareVersion: '',
          subVersion: '',
          commandId:'1',
          problemName:'',
          notFinished:'---- More ----',
          typeProblem:''
      },
      // 表单参数
        form:{},
        forms: {
          dynamicItem:[
              {
                  test:'test',
                  onlyIndex:''
              }
          ],
      },
        flagchange:0,
      // 表单校验
      rules: {
      }
    };
  },
  created() {

  },
  methods: {
      //出现
      hand(){
          this.showha = true
      },
      //右键
      showMenu(){
        event.preventDefault();
        var x = event.clientX;
        var y = event.clientY;
        this.contextMenuData.axis = {
            x,
            y,
        }
      },
      //帮助
      deletedata(){
          console.log('delete!')
      },
      showhelp(){
        alert('sss')
      },

      shaxun(){
          if (item.cycleStartId.length == 0){
              this.onfocus('clear')
          }
      },
      ssss(row,column,event){
          event.preventDefault()
        alert('sss')
      },
      //基本信息获取列表
      subLi(){
          const subOne = {}
          const brandO = this.queryParams.brand
          const typeO = this.queryParams.type
          const fireO = this.queryParams.firewareVersion
          this.$set(subOne,'brand',brandO)
          this.$set(subOne,'type',typeO)
          this.$set(subOne,'firewareVersion',fireO)
          // alert(JSON.stringify(subOne))
          axios({
              method:'post',
              url:'http://192.168.1.98/dev-api/sql/total_question_table/subVersionlist',
              headers:{
                  "Content-Type": "application/json"
              },
              data:JSON.stringify(subOne)
          }).then(res=>{
              this.subList = res.data
          })
      },
      fireLi(){
          const fireOne = {}
          const brandO = this.queryParams.brand
          const typeO = this.queryParams.type
          this.$set(fireOne,'brand',brandO)
          this.$set(fireOne,'type',typeO)
          // alert(JSON.stringify(fireOne))
          axios({
              method:'post',
              url:'http://192.168.1.98/dev-api/sql/total_question_table/firewareVersionlist',
              headers:{
                  "Content-Type": "application/json"
              },
              data:JSON.stringify(fireOne)
          }).then(res=>{
              this.fireList = res.data
          })
      },
      typeLi(){
          // const fff = JSON.parse(JSON.stringify(this.queryParams))
          const typeOne = {}
          const brandO = this.queryParams.brand
          this.$set(typeOne,'brand',brandO)
          // alert(JSON.stringify(typeOne))
          axios({
              method:'post',
              url:'http://192.168.1.98/dev-api/sql/total_question_table/typelist',
              headers:{
                  "Content-Type": "application/json"
              },
              data:JSON.stringify(typeOne)
          }).then(res=>{
              this.typeList = res.data
          })
      },
      brandLi(){
          axios({
              method:'post',
              url:'http://192.168.1.98/dev-api/sql/total_question_table/brandlist',
          }).then(res=>{
              this.brandList = res.data
          })
      },
      proType(){
          alert(JSON.stringify(this.queryParams))
          axios({
              method:'post',
              url:'http://192.168.1.98/dev-api/sql/total_question_table/typeProblemlist',
              headers:{
                  "Content-Type": "application/json"
              },
              data:JSON.stringify(this.queryParams)
          }).then(res=>{
              this.typeProList = res.data
          })
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
      //下拉列表输入
      subShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.subVersion = value
          }
      },
      brandShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.brand = value
          }
      },
      biShu(e){
          let value = e.target.value
          if(value){
              // this.forms.compareThree = value
              this.forms.dynamicItem.compareThree = value
          }
      },
      fireShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.firewareVersion = value
          }
      },
      typeShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.type = value
          }
      },
      typeProShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.typeProblem = value
          }
      },
      proSelect(e){
          let value = e.target.value
          if(value){
              this.queryParams.problemName = value
          }
      },
      //提交问题 返回问题id
      huoquid(){
          var shasha = JSON.parse(JSON.stringify(this.queryParams))
          this.$delete(shasha,'commandId')
          alert(JSON.stringify(shasha))
          // alert(JSON.stringify(this.queryParams))
          axios({
              method:'post',
              url:'http://192.168.1.98/dev-api/sql/total_question_table/add',
              headers:{
                  "Content-Type": "application/json"
              },
              data:JSON.stringify(shasha)
          }).then(res=>{
              alert(res.data)
              this.proId = res.data
          })
      },
      chawenti(){
          alert(JSON.stringify(this.queryParams))
          axios({
              method:'post',
              url:'http://192.168.1.98/dev-api/sql/total_question_table/problemNameList',
              headers:{
                  "Content-Type": "application/json"
              },
              data:JSON.stringify(this.queryParams)
          }).then(res=>{
              console.log(res.data)
              this.proNameList = res.data
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
      numToStr(num){
          num = num.toString()
          return num
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
                  targetType:'matchfal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'dimmatch'){
              this.$set(item1,'matched','全文模糊匹配')
              this.$set(item1,'trueFalse','成功')
              const item2 = {
                  targetType:'dimmatchfal',
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
                  targetType:'liprefal',
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
                  targetType:'dimprefal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if (type == 'analyse'){
              this.$set(item1,'action','比较')
              this.$set(item1,'trueFalse','成功')
              const item2 = {
                  targetType:'analysefal',
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

      //删除
      deleteItem (item, index) {
          this.forms.dynamicItem.splice(index,1)
      },
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
      proname(){
          // console.log(item.prodes)
          var proNamess = ""
          this.forms.dynamicItem.map((item)=>{
              proNamess = item.prodes
          })
          alert(proNamess)
      },
      //解决问题
      jiejue(){
        this.$router.push('solve_question')
          var arr = []
          function getJson(key,jsonObj){
              for(var index in jsonObj){
                  getJson1(key,jsonObj[index]);
              }
          }
          function getJson1(key,jsonObj){
              for (var p1 in jsonObj) {
                  if(p1 === key){
                      console.log(jsonObj[key]);
                      arr.push(jsonObj[key])
                  }else if(jsonObj[p1] instanceof Array) {
                      getJson(key,jsonObj[p1]);
                  }
              }
          }
          getJson('wordName',this.forms.dynamicItem);
          alert(arr)
      },

      //初次提交定义问题
      tijiao(){
          // this.forms.dynamicItem.shift(); hasOwnProperty(存在) 删除数组第一个元素
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
              if (this.jiaoyan == '常规校验'){
                  this.$set(eeee,'resultCheckId','1')
              }else {
                  this.$set(eeee,'resultCheckId','0')
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
          alert(handForm)
          axios({
              method:'post',
              // url:'/dev-api/sql/ConnectController/definitionProblem',
              url:'http://192.168.1.98/dev-api/sql/DefinitionProblemController/definitionProblemJsonPojo',
              headers:{
                  "Content-Type": "application/json"
              },
              data:handForm
              // data:[JSON.stringify(oneOne),JSON.stringify(twoTwo)]
          }).then(res=>{
              console.log("成功")
          })
          // window.location.reload()   刷新页面
          // this.$router.go(0)   刷新页面
      },

    // 表单重置
    reset() {
      this.form = {
        id: null,
        ip: null,
        name: null,
        type: null,
        password: null,
        end: null,
        port: null
      };
      this.resetForm("form");
    },


    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateJh_test1(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addJh_test1(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除多个扫描编号为"' + ids + '"的数据项？').then(function() {
        return delJh_test1(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有多个扫描数据项？').then(() => {
        this.exportLoading = true;
        return exportJh_test1(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>

<style>
  .el-form-item{
    margin-top: 10px;
    margin-bottom: 10px;
  }
</style>
