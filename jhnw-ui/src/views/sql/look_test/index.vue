<template>
  <div class="app-container">
    <el-row>
      <el-col :span="18">
        <el-form :model="queryParams" ref="queryForm" :inline="true">
          <el-form-item label="基本信息:"></el-form-item>
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
          <!--      <el-form-item>-->
          <!--        <el-select v-model="queryParams.commandId" style="width: 120px">-->
          <!--          <el-option label="所有问题" value="1"></el-option>-->
          <!--          <el-option label="未定义问题" value="0"></el-option>-->
          <!--        </el-select>-->
          <!--      </el-form-item>-->
          <el-form-item label="分类概要:"></el-form-item>
          <el-form-item label="范式分类" prop="typeProblem">
            <el-select v-model="queryParams.typeProblem" placeholder="范式分类"
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
          <el-form-item label="自定义名称">
<!--            <el-select v-model="queryParams.problemName" placeholder="自定义名称"-->
<!--                       name="problemName" @change="xuanChange" @focus="generalOne($event)">-->
<!--&lt;!&ndash;              @change="cproId"&ndash;&gt;-->
<!--              <el-option v-for="(item,index) in genList" :key="index"-->
<!--                         :label="item.problemName" :value="item.problemName"></el-option>-->
<!--            </el-select>-->
            <el-input v-model="queryParams.problemName" :disabled="true" placeholder="自定义问题名称"></el-input>
          </el-form-item>
<!--          <el-form-item>-->
<!--            <el-button type="primary" @click="chaxun" icon="el-icon-search" size="small">查看</el-button>-->
<!--            &lt;!&ndash;        :disabled="!isNull"&ndash;&gt;-->
<!--          </el-form-item>-->
          <el-form-item>
            <el-button type="primary" @click="addNew" icon="el-icon-plus" size="small" plain>新增</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="xiugai" icon="el-icon-edit" :disabled="isUse" size="small" plain>修改</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="danger" @click="shanchutest" icon="el-icon-delete" size="small" plain>删除</el-button>
          </el-form-item>
<!--          <el-form-item>-->
<!--            <el-button type="primary" @click="exportData">导出</el-button>-->
<!--          </el-form-item>-->
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
            <el-form-item v-if="index!=0" :label="numToStr(item.onlyIndex)" @click.native="wcycle(item,$event)"></el-form-item>
            <div v-if="item.targetType === 'command'" :key="index"
                 style="display: inline-block">
              <el-form-item label="命令：" class="strongW" :prop="'dynamicItem.' + index + '.command'">
                <el-input v-model="item.command"></el-input>
              </el-form-item>
              <el-form-item label="命令校验">
                <el-select v-model="item.resultCheckId" placeholder="校验方式">
                  <el-option label="自定义校验" value="0"></el-option>
                  <el-option label="常规校验" value="1"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'match'" :key="index" style="display: inline-block">
              <el-form-item label="匹配:" class="strongW"></el-form-item>
              <el-form-item label="位置">
                <el-select v-model="item.relativeTest" @change="relType(item)" filterable allow-create placeholder="当前行" style="width: 110px">
                  <el-option label="当前位置" value="present"></el-option>
                  <el-option label="全文起始" value="full"></el-option>
                  <el-option label="自定义行" value="" disabled></el-option>
                </el-select>
              </el-form-item>
              <el-form-item class="blockW">
                <el-radio v-model="item.relativeType" label="present">按行匹配</el-radio>
                <el-radio v-model="item.relativeType" label="full">全文匹配</el-radio>
              </el-form-item>
              <el-form-item label="内容" :prop="'dynamicItem.' + index + '.matchContent'">
                <el-input style="width:150px" v-model="item.matchContent"></el-input>
              </el-form-item>
<!--              <el-form-item label="光标位置">-->
<!--                <el-select v-model="item.cursorRegion" placeholder="当前行" style="width: 130px">-->
<!--                  <el-option label="当前行" value="0"></el-option>-->
<!--                  <el-option label="第一行" value="1"></el-option>-->
<!--                </el-select>-->
<!--              </el-form-item>-->
<!--              <el-form-item label="类型">-->
<!--                <el-select v-model="item.matched" filterable allow-create placeholder="类型" style="width: 130px">-->
<!--                  <el-option label="精确匹配" value="精确匹配"></el-option>-->
<!--                  <el-option label="模糊匹配" value="模糊匹配"></el-option>-->
<!--                </el-select>-->
<!--              </el-form-item>-->
              <el-form-item label="True"></el-form-item>
<!--              <el-form-item label="True">{{ "\xa0" }}</el-form-item>-->
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'failed'" style="display: inline-block;padding-left:493px">
              <el-form-item label=" False"></el-form-item>
              <el-form-item style="visibility: hidden">
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'dimmatch'" :key="index" style="display: inline-block">
              <el-form-item label="全文模糊匹配" :prop="'dynamicItem.' + index + '.matchContent'">
                <el-input v-model="item.matchContent"></el-input>
              </el-form-item>
              <el-form-item label="光标位置">
                <el-select v-model="item.cursorRegion" placeholder="当前行" style="width: 130px">
                  <el-option label="当前行" value="0"></el-option>
                  <el-option label="第一行" value="1"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="True"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'wloop'" :key="index"
                 style="display: inline-block">
              <el-form-item label="循环：" class="strongW" :prop="'dynamicItem.' + index + '.cycleStartId'">
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
              <el-form-item label="True"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'failedH'" style="display: inline-block;padding-left: 466px">
              <el-form-item label="False"></el-form-item>
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
              <el-form-item label="True"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'takeword'" :key="index" style="display: inline-block">
              <el-form-item label="取参:" class="strongW"></el-form-item>
              <el-form-item label="位置">
                <el-select v-model="item.cursorRegion" placeholder="当前位置" style="width: 110px">
                  <el-option label="当前位置" value="0"></el-option>
                  <el-option label="全文起始" value="1"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item :prop="'dynamicItem.' + index + '.takeword'">
                <el-input v-model="item.relative" style="width: 55px;padding: 0 5px" placeholder="行偏移"></el-input> --
                <el-input v-model="item.rPosition" style="width: 55px;padding: 0 5px" placeholder="列偏移"></el-input> --
                <el-input v-model="item.length1" style="width: 55px;padding: 0 5px" placeholder="取几个"></el-input>
                <el-select v-model="item.classify" placeholder="单词/行" style="width: 80px">
                  <el-option label="词汇" value="W"></el-option>
                  <el-option label="单字" value="L"></el-option>
                  <el-option label="字符串" value="S"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-radio-group v-model="item.exhibit">
                  <el-radio label="显示" style="margin-right: 5px"></el-radio>
                  <el-input style="width: 80px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='显示'"></el-input>
                  <el-radio label="不显示" style="margin-right: 5px"></el-radio>
                  <el-input style="width: 80px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='不显示'"></el-input>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'analyse'" :key="index" style="display:inline-block">
              <el-form-item label="比较：" class="strongW" v-show="bizui">
                <el-input v-model="item.compare" style="width: 217px" v-show="bizui" @input="bihou"></el-input>
              </el-form-item>
              <el-form-item label="比较" v-show="bixiala">
                <el-select v-model="item.bi" @change="bibi"
                           v-show="bixiala" placeholder="例如:品牌<5.20.99">
                  <el-option v-for="(item,index) in biList"
                             :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="True"></el-form-item>
              <el-form-item>
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>
            <div v-else-if="item.targetType === 'failedB'" style="display: inline-block;padding-left: 268px">
              <el-form-item label="False"></el-form-item>
              <el-form-item style="visibility: hidden">
                <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
              </el-form-item>
            </div>

            <div v-else-if="item.targetType === 'prodes'" :key="index" style="display:inline-block">
              <el-form-item label="异常：" class="strongW">
                <el-select v-model="item.tNextId" filterable allow-create placeholder="异常、安全、完成、自定义">
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
                    <el-button @click="addItem('match',item)" type="primary">匹配</el-button>
                  </el-dropdown-item>
<!--                  <el-dropdown-item>-->
<!--                    <el-button @click="addItem('dimmatch',item)" type="primary">全文模糊匹配</el-button>-->
<!--                  </el-dropdown-item>-->
<!--                  <el-dropdown-item>-->
<!--                    <el-button @click="addItem('lipre',item)" type="primary">按行精确匹配</el-button>-->
<!--                  </el-dropdown-item>-->
<!--                  <el-dropdown-item>-->
<!--                    <el-button @click="addItem('dimpre',item)" type="primary">按行模糊匹配</el-button>-->
<!--                  </el-dropdown-item>-->
                  <el-dropdown-item>
                    <el-button @click="addItem('takeword',item)" type="primary">取参</el-button>
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
          <el-form-item>
            <el-button @click="delAnalyse" type="primary">删除</el-button>
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
import { listLook_test, getLook_test, delLook_test, addLook_test, updateLook_test, exportLook_test, test_export } from "@/api/sql/look_test";
import axios from 'axios'
import  {MessageBox} from "element-ui"
import request from '@/utils/request'
import router from '@/router/index'
import log from "../../monitor/job/log";

export default {
  name: "Look_test",
  inject:["reload"],
  data() {
    return {
        //联动
        clickLine:'',
        //右侧返回个数
        lieNum:0,
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
        temProNameList:[],
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
        // problemName:'',
        // temProName:'',
        // typeProblem:'',
        // brand:'',
        // type:'',
        // firewareVersion:'',
        // subVersion:''



          problemName:undefined,
          temProName:undefined,
          typeProblem:undefined,
          brand:undefined,
          type:undefined,
          firewareVersion:undefined,
          subVersion:undefined
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
    // this.getList();
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
                  eeee.length = `${eeee.length1}${eeee.classify}`
              }
              if (eeee.targetType == 'match'){
                  this.$set(eeee,'relative',eeee.relativeTest + '&' + eeee.relativeType)
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
              method:'put',
              data:handForm
          }).then(response=>{
              this.$message.success('提交修改成功!')
              this.reload()
          })
      },
      //删除分析问题逻辑
      delAnalyse(){
          MessageBox.confirm('确定删除该问题分析逻辑吗？','提示').then(c=>{
              console.log(this.proId)
              return request({
                  url:'/sql/DefinitionProblemController/deleteScanningLogic',
                  method:'delete',
                  data:this.proId
              }).then(response=>{
                  console.log('删除成功')
                  this.$message.success('删除成功!')
              })
          }).catch(ee=>{
              this.$message.warning('取消删除!')
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
          console.log(typeof newPar)
          console.log(typeof this.queryParams)
          return request({
              url:'/sql/total_question_table/selectPojoList',
              method:'get',
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
      //下拉列表通用事件
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
              params:newPar
          }).then(response=>{
              console.log(response)
              this.genList = this.quchong(response,this.who)
              let kong = {
                  [this.who] : 'null'
              }
              this.genList.push(kong)
              // console.log(this.genList)
          })
      },
      //下拉列表通用（最初）
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
          console.log(item)
          this.forms.dynamicItem.forEach(cy=>{
              if (cy.hasOwnProperty('cycleStartId') != true && cy.targetType == 'wloop'){
                  if (item.relativeTest == 'full'){
                      this.$confirm('此循环行涉及全文起始，应修改为当前位置!', '提示', {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning'
                      }).then(() => {}).catch(() => {});
                  }
                  this.$set(cy,'cycleStartId',cycleId)
              }else if (cy.cycleStartId == ''){
                  if (item.relativeTest == 'full'){
                      this.$confirm('此循环行涉及全文起始，应修改为当前位置!', '提示', {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning'
                      }).then(() => {}).catch(() => {});
                  }
                  this.$set(cy,'cycleStartId',cycleId)
              }
          })
      },
      //回显定义问题
      chaxun(){
          console.log(this.queryParams)
          console.log(this.lookLists)
          if (this.lookLists.length != 1 && this.cdy != true){
              alert('查找条件过于模糊,请完善')
          }else {
              this.forms.dynamicItem = this.formss.dynamicItemss
              this.huichasss = []
              this.showNo= true
              this.zhidu = true
              return request({
                  url:'/sql/DefinitionProblemController/getAnalysisList',
                  method:'get',
                  // data:JSON.stringify(this.queryParams)
                  params:this.queryParams
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
                          this.huichasss.push(chae)
                      }else if (chae.action === '取词'){
                          this.$set(chae,'targetType','takeword')
                          if (chae.length.slice(chae.length.length-1) === 'W'){
                              this.$set(chae,'classify','单词')
                          }else  if (chae.length.slice(chae.length.length-1) === 'S'){
                              this.$set(chae,'classify','字符串')
                          }else if (chae.length.slice(chae.length.length-1) === 'L'){
                              this.$set(chae,'classify','字母')
                          }
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
      //列表展示树结构-回显定义问题
      handleNodeClick(lookLists) {
          this.proId = lookLists.id
          this.lieNum = 1
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
                      method: 'get',
                      // data: response.data
                      params:response.data
                  }).then(response => {
                      console.log(response)
                      if (response.msg === '操作成功'){
                          this.cdy = true
                      }
                      this.fDa = []
                      if (response.data != null){
                          console.log('不为空')
                          response.data.forEach(l => {
                              const wei = l.replace(/"=/g, '":')
                              this.fDa.push(JSON.parse(wei))
                          })
                      }
                      this.fDa.forEach(chae => {
                          if (chae.hasOwnProperty('command') == true) {
                              this.$set(chae, 'targetType', 'command')
                              this.huichasss.push(chae)
                          } else if (chae.matched.indexOf('匹配') != -1 && chae.trueFalse == '成功') {
                              this.$set(chae, 'targetType', 'match')
                              var strList = chae.relative.split('&')
                              this.$set(chae,'relativeTest',strList[0])
                              this.$set(chae,'relativeType',strList[1])
                              this.huichasss.push(chae)
                              this.allOne.push(chae.onlyIndex)
                          } else if (chae.matched === '全文模糊匹配') {
                              this.$set(chae, 'targetType', 'dimmatch')
                              this.huichasss.push(chae)
                              this.allOne.push(chae.onlyIndex)
                          } else if (chae.matched === '按行精确匹配') {
                              this.$set(chae, 'targetType', 'lipre')
                              this.huichasss.push(chae)
                              this.allTwo.push(chae.onlyIndex)
                          } else if (chae.matched === '按行模糊匹配') {
                              this.$set(chae, 'targetType', 'dimpre')
                              this.huichasss.push(chae)
                              this.allTwo.push(chae.onlyIndex)
                          } else if (chae.action === '比较') {
                              this.$set(chae, 'targetType', 'analyse')
                              this.bizui = true
                              this.bixiala = false
                              this.huichasss.push(chae)
                          } else if (chae.action.indexOf('取词') != -1) {
                              this.$set(chae, 'targetType', 'takeword')
                              if (chae.length.slice(chae.length.length - 1) === 'W') {
                                  this.$set(chae, 'classify', 'W')
                              } else if (chae.length.slice(chae.length.length - 1) === 'S') {
                                  this.$set(chae, 'classify', 'S')
                              } else if (chae.length.slice(chae.length.length - 1) === 'L') {
                                  this.$set(chae, 'classify', 'L')
                              }
                              this.$set(chae,'length1',chae.length.slice(0, chae.length.length - 1))
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
                          } else if(chae.trueFalse === '失败'){
                              if (this.allOne.includes(chae.onlyIndex)){
                                  this.$set(chae,'targetType','failed')
                              }else if (this.allTwo.includes(chae.onlyIndex)){
                                  this.$set(chae,'targetType','failedH')
                              }else {
                                  this.$set(chae,'targetType','failedB')
                              }
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
      //编辑
      xiugai(){
          MessageBox.confirm('确定去修改吗？','提示').then(c=>{
              this.zhidu = false
          }).catch(ee=>{
              this.$message.warning('取消修改!')
          })
      },
      //导出数据库
      exportData(){
          console.log(this.proId)
          const idT = this.proId
          this.$modal.confirm('是否确认导出？').then(() => {
              this.exportLoading = true
              return test_export(idT)
              // console.log(return test_export(idT))
          }).then(response => {
              console.log(response)
              console.log(response.data.length)
              // this.$download.name(response.data[0])
              for (let i = 0;i<response.data.length;i++){
                  this.$download.name(response.data[i])
              }
              // console.log(response.msg)
              this.exportLoading = false
          }).catch(() => {})
      },
      //新增
      addNew(){
          router.push({
              path:'/sql/jh_test1'
          })
      },
      //删除
      shanchutest(){
          if (this.lieNum != 1){
              this.$message.error('数据过于模糊,请精准!')
          }else {
              console.log(this.proId)
              MessageBox.confirm('确定删除该问题以及分析逻辑、修改逻辑吗？','提示').then(c=>{
                  return request({
                      // url:'/sql/total_question_table/deleteTotalQuestionTable/' + this.proId,
                      url:'/sql/total_question_table/deleteTotalQuestionTable',
                      method:'delete',
                      data:this.proId
                      // params:this.proId
                  }).then(response=>{
                      this.$message.success('删除成功!')
                      this.reload()
                  })
              }).catch(ee=>{
                  this.$message.warning('取消删除!')
              })
          }
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
      //全文、按行联动
      relType(item){
          this.clickLine = item.onlyIndex
          this.forms.dynamicItem.forEach(e=>{
              if (e.targetType == 'match' && e.onlyIndex == this.clickLine){
                  if (e.relativeTest == 'full'){
                      this.$set(e,'relativeType','full')
                  }else if (e.relative == 'present'){
                      this.$set(e,'relativeType','present')
                  }else {
                      this.$set(e,'relativeType','present')
                  }
              }
          })
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
          if (type == 'command'){
              this.$set(item1,'resultCheckId','0')
          }
          if(type == 'match'){
              this.$set(item1,'matched','精确匹配')
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'relativeTest','present')
              this.$set(item1,'relativeType','present')
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
              this.$set(item1,'cursorRegion','0')
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
              this.$set(item1,'position',0)
              this.$set(item1,'cursorRegion','0')
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

<style scoped>
  .el-form-item{
    margin-top: 5px;
    margin-bottom: 5px;
  }
  >>> label{
    font-weight: normal;
  }
  >>> .strongW label{
    font-weight: 700;
    padding-right: 0;
  }
  >>> .blockW label{
    display: block;
    margin-right: 5px;
  }
</style>
