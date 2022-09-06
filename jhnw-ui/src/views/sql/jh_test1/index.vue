<template>
<!--  <div class="app-container" @contextmenu="showMenu">-->
  <div class="app-container" @contextmenu="showMenu">
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="设备基本信息:"></el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-select v-model="queryParams.brand" placeholder="品牌"
                     filterable allow-create @blur="brandShu" @focus="brandLi($event)"
                     name="brand" style="width: 150px">
            <el-option v-for="(item,index) in brandList"
                       :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="型号" prop="type">
          <el-select v-model="queryParams.type" placeholder="型号"
                     filterable allow-create @blur="typeShu"
                     @focus="typeLi($event)" name="type" style="width: 150px">
            <el-option v-for="(item,index) in typeList"
                       :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="固件版本" prop="firewareVersion">
          <el-select v-model="queryParams.firewareVersion" placeholder="固件版本"
                     filterable allow-create @blur="fireShu" @focus="fireLi($event)"
                     name="firewareVersion" style="width: 150px">
            <el-option v-for="(item,index) in fireList"
                       :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="子版本" prop="subVersion">
          <el-select v-model="queryParams.subVersion" placeholder="子版本"
                     filterable allow-create @blur="subShu"
                     @focus="subLi($event)" name="subVersion" style="width: 150px">
            <el-option v-for="(item,index) in subList"
                       :key="index" :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
          </el-select>
        </el-form-item>
<!--        <el-form-item>-->
<!--          <el-button type="primary" @click="tianjia(item)"><i class="el-icon-plus"></i></el-button>-->
<!--        </el-form-item>-->
      <br/>
<!--      <el-form-item>-->
<!--        <el-select v-model="queryParams.commandId" style="width: 120px">-->
<!--          <el-option label="所有问题" value="1"></el-option>-->
<!--          <el-option label="未定义问题" value="0"></el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
      <el-form-item label="问题概要:"></el-form-item>
      <el-form-item label="范式分类" prop="typeProblem">
        <el-select v-model="queryParams.typeProblem" placeholder="范式分类" name="typeProblem"
                   filterable allow-create @focus="proType($event)" @blur="typeProShu">
          <el-option v-for="(item,index) in typeProList" :key="index"
                     :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="范式名称">
        <el-select v-model="queryParams.temProName" placeholder="请选择范式名称"
                   filterable allow-create @focus="temPro($event)" @blur="temProShu">
          <el-option v-for="(item,index) in temProNameList" :key="index"
                     :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="自定义名称">
        <el-select v-model="queryParams.problemName" placeholder="请输入问题名称"
                   filterable allow-create @focus="chawenti($event)" @blur="proSelect">
          <el-option v-for="(item,index) in proNameList" :key="index"
                     :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
        </el-select>
      </el-form-item>
      <br/>
      <el-form-item label="其它:"></el-form-item>
      <el-form-item>
        <el-checkbox v-model="queryParams.requiredItems">必扫问题</el-checkbox>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="queryParams.remarks"></el-input>
      </el-form-item>
      <el-form-item label="标识符">
        <el-input v-model="queryParams.notFinished"
                  clearable
                  @focus="biaoshi($event)" name="biaoshi"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="tiwenti">提交问题</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="huoquid">定义问题命令</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="xiangqing">定义问题详情</el-button>
      </el-form-item>
<!--      <el-form-item>-->
<!--        <el-button type="primary" @click="kanuser">看用户</el-button>-->
<!--      </el-form-item>-->
<!--      <el-form-item>-->
<!--      </el-form-item>-->

    </el-form>
    <hr style='border:1px inset #D2E9FF;'>

    <el-form ref="forms" :inline="true" :model="forms" v-show="chuxian">
      <el-form-item label="检测方法:"></el-form-item>
      <el-form-item>
        <el-checkbox v-model="checkedQ" @change="handleCheckAllChange">全选</el-checkbox>
      </el-form-item>
      <el-form-item>
        <el-button type="text" icon="el-icon-delete" @click="shanchu">删除</el-button>
      </el-form-item>
      <div v-for="(item,index) in forms.dynamicItem" ref="btn" :key="index" :label="index">
        <el-form-item v-if="index!=0">
          <el-checkbox v-model="item.checked"></el-checkbox>
        </el-form-item>
        <el-form-item v-if="index!=0">{{index}}</el-form-item>
        <el-form-item v-if="index!=0" :label="numToStr(item.onlyIndex)" @click.native="wcycle(item,$event)"></el-form-item>
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
        <div v-else-if="item.targetType === 'match'" :key="index" style="display: inline-block" label="测试">
          <el-form-item label="全文精确匹配" :prop="'dynamicItem.' + index + '.matchContent'">
            <el-input v-model="item.matchContent"></el-input>
          </el-form-item>
          <el-form-item label="True">{{ "\xa0" }}</el-form-item>
          <el-form-item>
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>
        <div v-else-if="item.targetType === 'matchfal'"
             style="display: inline-block;padding-left:308px">
          <el-form-item label="False"></el-form-item>
          <el-form-item style="visibility: hidden">
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
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
          <el-form-item label="True">{{ "\xa0" }}</el-form-item>
          <el-form-item>
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>
        <div v-else-if="item.targetType === 'dimmatchfal'" style="display: inline-block;padding-left: 308px">
          <el-form-item label="False"></el-form-item>
          <el-form-item style="visibility: hidden">
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>
        <div v-else-if="item.targetType === 'lipre'" :key="index" style="display:inline-block">
          <el-form-item label="按行精确匹配" :prop="'dynamicItem.' + index + '.relative'">
            <el-input v-model="item.relative" placeholder="下几行" style="width: 80px"></el-input>
          </el-form-item>
          <el-form-item label="匹配内容">
            <el-input v-model="item.matchContent" aria-placeholder="填写匹配内容"></el-input>
          </el-form-item>
          <el-form-item label="True">{{ "\xa0" }}</el-form-item>
          <el-form-item>
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>
        <div v-else-if="item.targetType === 'liprefal'" style="display: inline-block;padding-left: 466px">
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
          <el-form-item label="True">{{ "\xa0" }}</el-form-item>
          <el-form-item>
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>
        <div v-else-if="item.targetType === 'dimprefal'" style="display: inline-block;padding-left: 466px">
          <el-form-item label="False"></el-form-item>
          <el-form-item style="visibility: hidden">
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>

        <div v-else-if="item.targetType === 'takeword'" :key="index" style="display: inline-block">
          <el-form-item label="取词" :prop="'dynamicItem.' + index + '.takeword'">
            <el-input v-model="item.rPosition" style="width: 80px" placeholder="第几个"></el-input> --
            <el-input v-model="item.length" style="width: 80px" placeholder="几个"></el-input>
            <el-select v-model="item.classify" @change="reloadv" placeholder="单词/行" style="width: 80px">
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
          <el-form-item label="True">{{ "\xa0" }}</el-form-item>
          <el-form-item>
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>
        <div v-else-if="item.targetType === 'analysefal'" style="display: inline-block;padding-left: 268px">
          <el-form-item label="False"></el-form-item>
          <el-form-item style="visibility: hidden">
            <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
          </el-form-item>
        </div>

        <div v-else-if="item.targetType === 'prodes'" :key="index" style="display:inline-block">
          <el-form-item label="有无问题">
<!--            prop="'dynamicItem.' + index + '.prodes'"-->
<!--            <el-input v-model="item.prodes"></el-input>-->
            <el-select v-model="item.tNextId" placeholder="异常安全、完成">
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
                <el-button @click="addItem('match',item,index)" type="primary">全文精确匹配</el-button>
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
        <el-button @click="guanbi" type="primary">关闭</el-button>
<!--        <el-button @click="ceshi" type="primary">测试按钮</el-button>-->
<!--        有用-->
<!--        <el-button @click="jiejue" type="primary">解决问题</el-button>-->
<!--        <el-button @click="proname" type="primary">问题名</el-button>-->
      </el-form-item>

    </el-form>

    <TinymceEditor v-show="showha" ref="fuwenben"></TinymceEditor>
    <el-button @click="yinyin" v-show="showha">隐藏</el-button>
    <el-button @click="look" type="primary" v-show="showha" style="margin-top:20px">提交</el-button>

    <el-input type="textarea" v-show="partShow" v-model="particular" rows="15"></el-input>
    <el-button @click="partsub" type="primary" v-show="partShow" style="margin-top:20px">提交详情</el-button>
    <el-button @click="partclose" type="primary" v-show="partShow" style="margin-top:20px">关闭详情</el-button>

    <vue-context-menu style="width: 172px;background: #eee;margin-left: auto"
                      :contextMenuData="contextMenuData" @deletedata="deletedata" @showhelp="showhelp">
    </vue-context-menu>

<!--    帮助-->
<!--    <el-button @click="bangzhu" style="width: 100px">帮助</el-button>-->
    <el-dialog
      title="提示"
      :visible.sync="dialogVisibleHelp"
      style="padding: 10px;height: 500px"
      width="30%"
      :before-close="handleClose">
      <div>
        <h3 style="font-weight: bolder" id="pinpai" ref="pinpai">品牌</h3>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <p>请输入品牌：</p>
        <a :href="whelp" v-trigger><span>品牌</span></a>
      </div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisibleHelp = false">取 消</el-button>
    <el-button type="primary" @click="dialogVisibleHelp = false">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
import { listJh_test1, getJh_test1, delJh_test1, addJh_test1, updateJh_test1, exportJh_test1 } from "@/api/sql/jh_test1";
import TinymceEditor from "@/components/Tinymce/TinymceEditor"
import request from '@/utils/request'

export default {
  name: "Jh_test1",
    components:{
        TinymceEditor
    },
  data() {
    return {
        //帮助
        whelp:'',
        dialogVisibleHelp:false,
        //问题详情
        particular:'',
        partShow:false,
        //必选项
        //隐藏定义问题
        chuxian:false,
        display:'inline-block',
        paddingLeft:'0px',
        // padqj
        //右键
        cpus:'',
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
        who:'',
        helpT:'',
        showha:false,
        jiaoyan:'常规校验',
        bizui:false,
        bixiala:true,
        radio:'1',
        proNameList:[],
        typeProList:[],
        temProNameList:[],
        brandList:[],
        fireList:[],
        typeList:[],
        subList:[],
        biList:['品牌','型号','固件版本','子版本'],
        checkedQ:false,
        // checked:false,
        //问题ID
        proId:'',
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
          typeProblem:'',
          temProName:'',
          requiredItems:false,
          remarks:''
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
    mounted(){

    },
  created() {
      // this.getList();
      // this.jiazai()
  },
    // 自动触发事件
    directives:{
        trigger:{
            inserted(el,binging){
                el.click()
            }
        }
    },
  methods: {
      //帮助
      bangzhu(){
          this.dialogVisibleHelp = true
      },
      handleClose(done) {
          this.$confirm('确认关闭？')
              .then(_ => {
                  done();
              })
              .catch(_ => {});
      },
      //重新赋值
      reloadv(){

      },
      //测试按钮
      ceshi(){
        // console.log(this.forms.dynamicItem)
        //   alert(JSON.stringify(this.forms.dynamicItem))
      },
      jiazai(){
        alert('加载')
      },
      //添加
      tianjia(item){
          alert('添加')
          this.$set(item)
      },
      //下载
      kanuser(){
          var ip = window.location.host
          console.log(ip)
          console.log("ip",ip.split(":")[0])
          console.log(process.env.VUE_APP_HOST)
      },
      //右键
      biaoshi(e){
          this.who = e.target.getAttribute('name')
          console.log(this.who)
      },
      yinyin(){
        this.showha = false
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
          if (this.who === 'biaoshi'){
              this.dialogVisibleHelp = true
              this.whelp = '#pinpai'
              setTimeout(()=>{
                  this.$refs.pinpai.style.color='red'
              },0)
          }else if (this.who === 'brand'){
              this.helpT = '请输入交换机品牌'
          }else if (this.who === 'type' ){
              this.helpT = '请输入交换机型号'
          }else if (this.who === 'firewareVersion' ){
              this.helpT = '请输入交换机版本'
          }else if (this.who === 'subVersion' ){
              this.helpT = '请输入交换机子版本'
          }else if (this.who === '' ){
              this.helpT = ''
          }else if (this.who === '' ){
              this.helpT = ''
          }
          // this.$alert(this.helpT, '标题名称', {
          //     confirmButtonText: '确定'
          // });
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
      subLi(e){
          this.who = e.target.getAttribute('name')
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
              response.forEach(e=>{
                  if (e === '*'){
                      this.$delete(response,response.indexOf(e))
                  }
              })
              this.subList = response
          })
      },
      fireLi(e){
          this.who = e.target.getAttribute('name')
          const fireOne = {}
          const brandO = this.queryParams.brand
          const typeO = this.queryParams.type
          this.$set(fireOne,'brand',brandO)
          this.$set(fireOne,'type',typeO)
          // alert(JSON.stringify(fireOne))
          return request({
              url:'/sql/total_question_table/firewareVersionlist',
              method:'post',
              data:JSON.stringify(fireOne)
          }).then(response=>{
              response.forEach(e=>{
                  if (e === '*'){
                      this.$delete(response,response.indexOf(e))
                  }
              })
              this.fireList = response
          })
      },
      typeLi(e){
          this.who = e.target.getAttribute('name')
          const typeOne = {}
          const brandO = this.queryParams.brand
          this.$set(typeOne,'brand',brandO)
          return request({
              url:'/sql/total_question_table/typelist',
              method:'post',
              data:JSON.stringify(typeOne)
          }).then(response=>{
              response.forEach(e=>{
                  if (e === '*'){
                      this.$delete(response,response.indexOf(e))
                  }
              })
              this.typeList = response
          })
      },
      brandLi(e){
          this.who = e.target.getAttribute('name')
          return request({
              url:'/sql/total_question_table/brandlist',
              method:'get'
          }).then(response=>{
              response.forEach(e=>{
                  if (e === '*'){
                      this.$delete(response,response.indexOf(e))
                  }
              })
              console.log(response)
              this.brandList = response
          })
      },
      proType(e){
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
      chawenti(e){
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
      temProShu(e){
          let value = e.target.value
          if(value){
              this.queryParams.temProName = value
          }
      },
      proSelect(e){
          let value = e.target.value
          if(value){
              this.queryParams.problemName = value
          }
      },
      //提交问题，返回问题ID
      tiwenti(){
          var shasha = JSON.parse(JSON.stringify(this.queryParams))
          this.$delete(shasha,'commandId')
          if (shasha.requiredItems === true){
              this.$set(shasha,'requiredItems','1')
          }else {
              this.$set(shasha,'requiredItems','0')
          }
          console.log(JSON.stringify(shasha))
          return request({
              url:'/sql/total_question_table/add',
              method:'post',
              data:JSON.stringify(shasha)
          }).then(response=>{
              this.$message.success('提交问题成功!')
              this.proId = response.msg
          })
      },
      //查看是否存在，返回问题ID，定义命令隐藏变为出现
      huoquid(){
          let form = new FormData();
          for (var key in this.queryParams){
              if (key != 'notFinished'&&key != 'requiredItems'&&key != 'commandId'&&key != 'remarks'){
                  form.append(key,this.queryParams[key])
              }
          }
          console.log(form)
          return request({
              url:'/sql/total_question_table/totalQuestionTableId',
              method:'post',
              data:form
          }).then(response=>{
              console.log(response)
              if (typeof(response) === 'number'){
                  this.chuxian = true
                  this.proId = response
              }else {
                  this.$message.error('没有定义该问题,请先定义问题在定义命令！')
              }
          })
      },
      //定义问题详情
      xiangqing(){
          let form = new FormData();
          for (var key in this.queryParams){
              if (key != 'notFinished'&&key != 'requiredItems'&&key != 'commandId'&&key != 'remarks'){
                  form.append(key,this.queryParams[key])
              }
          }
          console.log(form)
          return request({
              url:'/sql/total_question_table/totalQuestionTableId',
              method:'post',
              data:form
          }).then(response=>{
              console.log(response)
              if (typeof(response) === 'number'){
                  this.particular = ''
                  this.partShow = true
                  this.proId = response
              }else {
                  this.$message.error('没有定义该问题,请先定义问题！')
              }
          })
      },
      //富文本提交问题详情
      look(){
          console.log(this.proId)
          console.log(this.$refs.fuwenben.geifu())
          return request({
              // url:`/sql/problem_describe/insertProblemDescribe?totalQuestionTableId=${this.proId}`,
              method:'post',
              data:this.$refs.fuwenben.geifu()
          }).then(response=>{
              this.$message.success('问题详情已提交!')
          })
          //     // url:`/dev-api/sql/ConnectController/ssssss?ass=${this.proId}`,
          //     // url:`http://192.168.1.98/dev-api/sql/problem_describe/insertProblemDescribe?totalQuestionTableId=${this.proId}`,
      },
      //普通文本提交问题详情
      partsub(){
          console.log(this.proId)
          console.log(this.particular)
          return request({
              url:`/sql/problem_describe/insertProblemDescribe?totalQuestionTableId=${this.proId}`,
              method:'post',
              data:this.particular
          }).then(response=>{
              this.$message.success('问题详情已提交!')
              this.partShow = false
          })
      },
      //关闭详情
      partclose(){
          this.particular = ''
          this.partShow = false
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
      addItem(type,item,index){
          const thisData = Date.now()
          const item1 = {
              targetType: type,
              onlyIndex:thisData,
              trueFalse:""
          }
          this.$set(item1,'checked',false)
          const thisIndex = this.forms.dynamicItem.indexOf(item)
          console.log(thisIndex)
          //动态添加的样式空格问题
          console.log(this.$refs.btn[thisIndex])
          console.log(item)
          // this.$set(item,'nextIndex',thisData)
          if(type == 'match'){
              this.$refs.btn[thisIndex].labelss = '测试我'
              this.$set(item1,'matched','全文精确匹配')
              this.$set(item1,'trueFalse','成功')
              // alert(event.target.getAttribute('label'))
              if (item.trueFalse === '成功'){
                  var iii = this.$refs.btn[index].style.paddingLeft
                  console.log(index)
                  var num = iii.replace(/[^0-9]/ig,"")
                  var zuinum = parseInt(num)+20*index
                  // console.log(zuinum)
                  this.$refs.btn[index+1].style.paddingLeft = `${zuinum}px`
                  // console.log(document.querySelector(item).style.paddingLeft)
              }
              const item2 = {
                  targetType:'matchfal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
              // console.log(item2.style.paddingLeft)
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
      // proname(){
      //     // console.log(item.prodes)
      //     var proNamess = ""
      //     this.forms.dynamicItem.map((item)=>{
      //         proNamess = item.prodes
      //     })
      //     alert(proNamess)
      // },
      //解决问题
      // jiejue(){
      //   this.$router.push('solve_question')
      //     var arr = []
      //     function getJson(key,jsonObj){
      //         for(var index in jsonObj){
      //             getJson1(key,jsonObj[index]);
      //         }
      //     }
      //     function getJson1(key,jsonObj){
      //         for (var p1 in jsonObj) {
      //             if(p1 === key){
      //                 console.log(jsonObj[key]);
      //                 arr.push(jsonObj[key])
      //             }else if(jsonObj[p1] instanceof Array) {
      //                 getJson(key,jsonObj[p1]);
      //             }
      //         }
      //     }
      //     getJson('wordName',this.forms.dynamicItem);
      //     alert(arr)
      // },

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
              if (eeee.action === '取词'){
                  eeee.length = `${eeee.length}${eeee.classify}`
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
              //拆分有无问题
              if (eeee.action === '问题'){
                  const neid = eeee.action
                  console.log(neid)
                  const thisData = Date.now()
                  console.log(thisData)
              }

          })
          const handForm = useForm.map(x => JSON.stringify(x))
          console.log(handForm)
          return request({
              url:'/sql/DefinitionProblemController/definitionProblemJsonPojo',
              method:'post',
              data:handForm
          }).then(response=>{
              console.log("成功")
              this.$message.success('提交成功!')
          })
          // window.location.reload()   刷新页面
          // this.$router.go(0)   刷新页面
      },
      //关闭定义问题
      guanbi(){
          this.chuxian = false
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
  .el-dialog__header{
    padding: 10px;
  }
  .el-dialog__body{
    padding: 10px;
  }
</style>
