<template>
  <!--  <div class="app-container" @contextmenu="showMenu">-->
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" :inline="true">
      <el-form-item label="ip" prop="ip">
        <el-input v-model="form.ip" style="width: 150px" size="small" placeholder="请输入ip" />
      </el-form-item>
      <el-form-item label="用户名" prop="name">
        <el-input v-model="form.name" style="width: 120px" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" style="width: 150px" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="方式" prop="mode">
        <el-select v-model="form.mode" placeholder="方式" style="width: 100px">
          <el-option label="ssh" value="ssh"></el-option>
          <el-option label="telnet" value="telnet"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="端口号" prop="port">
        <el-input v-model="form.port" style="width: 66px" placeholder="" />
      </el-form-item>
      <el-form-item label="配置密码" prop="configureCiphers">
        <el-input v-model="form.configureCiphers" style="width: 150px" placeholder="请输入配置密码" />
      </el-form-item>
      <br/>
      <el-button type="primary" size="small" @click="ceshi1" style="margin-top: 11px">预执行</el-button>
      <div style="display: inline-block;margin-left: 20px">
        <el-input v-model="returnInfo" readonly placeholder="展示获取基本信息" style="width:500px"></el-input>
      </div>
    </el-form>
    <hr style='border:1px inset #D2E9FF;'>
    <el-form ref="forms" :inline="true" :model="forms" v-show="chuxian">
      <el-form-item label="定义获取基本信息命令">

        <el-input v-model="custom" placeholder="品牌" style="width: 110px;margin-bottom: 10px"></el-input>
          <div v-for="(item,index) in forms.testsss" :key="item.key">
            <el-input v-model="item.value" placeholder="命令" style="width: 300px;margin-bottom: 5px"></el-input>
            <el-button @click="removeItem(index)" style="display: inline-block">删除</el-button>
          </div>

<!--        <el-input v-model="basicCom" type="text" style="width:260px" placeholder="命令用逗号分隔"></el-input>-->
        <el-button @click="tianyi">添加命令</el-button>
<!--        <el-button @click="kantest">查看</el-button>-->
      </el-form-item>
      <br/>
      <el-form-item label="命令分析逻辑:"></el-form-item>
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
          <el-form-item label="True"></el-form-item>
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
          <el-form-item label="True"></el-form-item>
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
          <el-form-item label="True"></el-form-item>
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
            <el-input v-model="item.relative" style="width: 80px" placeholder="第几行"></el-input> --

            <el-input v-model="item.rPosition" style="width: 80px" placeholder="第几个"></el-input> --
            <el-input v-model="item.length1" style="width: 80px" placeholder="几个"></el-input>
            <el-select v-model="item.classify" @change="reloadv" placeholder="单词/行" style="width: 80px">
              <el-option label="单词" value="W"></el-option>
              <el-option label="字母" value="L"></el-option>
              <el-option label="字符串" value="S"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-radio-group v-model="item.exhibit">
              <el-radio label="显示" style="margin-right: 5px"></el-radio>

                <el-select v-model="item.wordName" placeholder="请选择"
                           v-if="item.exhibit==='显示'" style="margin-top:0;width: 100px;margin-right: 10px">
                  <el-option label="设备品牌" value="设备品牌"></el-option>
                  <el-option label="设备型号" value="设备型号"></el-option>
                  <el-option label="内部固件版本" value="内部固件版本"></el-option>
                  <el-option label="子版本号" value="子版本号"></el-option>
                </el-select>

<!--              <el-input style="width: 120px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='显示'"></el-input>-->
              <el-radio label="不显示" style="margin-right: 5px"></el-radio>

                <el-select v-model="item.wordName" placeholder="请选择"
                           v-if="item.exhibit==='不显示'" style="margin-top:0;width: 100px">
                  <el-option label="设备品牌" value="设备品牌"></el-option>
                  <el-option label="设备型号" value="设备型号"></el-option>
                  <el-option label="内部固件版本" value="内部固件版本"></el-option>
                  <el-option label="子版本号" value="子版本号"></el-option>
                </el-select>

<!--              <el-input style="width: 120px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='不显示'"></el-input>-->
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
          <el-form-item label="True"></el-form-item>
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
<!--              <el-dropdown-item>-->
<!--                <el-button @click="addItem('command',item)" type="primary">命令</el-button>-->
<!--              </el-dropdown-item>-->
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
      </el-form-item>

    </el-form>

    <vue-context-menu style="width: 172px;background: #eee;margin-left: auto"
                      :contextMenuData="contextMenuData" @deletedata="deletedata" @showhelp="showhelp">
    </vue-context-menu>

  </div>
</template>

<script>
    import { listJh_test1, getJh_test1, delJh_test1, addJh_test1, updateJh_test1, exportJh_test1 } from "@/api/sql/jh_test1";
    import TinymceEditor from "@/components/Tinymce/TinymceEditor"
    import request from '@/utils/request'
    import  {MessageBox} from "element-ui"
    import { JSEncrypt } from 'jsencrypt'

    export default {
        name: "Basic_information",
        components:{
            // TinymceEditor
        },
        data() {
            return {
                //
                returnInfo:'',
                //问题详情
                particular:'',
                partShow:true,
                custom:'',
                //必选项
                //隐藏定义问题
                chuxian:true,
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
                form:{
                    ip:'',
                    name: '',
                    password:'',
                    mode:'ssh',
                    port:'',
                    configureCiphers:null
                },
                forms: {
                    dynamicItem:[
                        {
                            test:'test',
                            onlyIndex:''
                        }
                    ],
                    testsss:[
                        {
                            value:''
                        }
                    ]
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
        methods: {
            //添加一行
            tianyi(){
                // addDomain() {
                //     this.dynamicValidateForm.domains.push({
                //         value: '',
                //         key: Date.now()
                //     });
                // }
                this.forms.testsss.push({
                    value:''
                })
            },
            kantest(){
                // console.log(this.forms)
                console.log(this.forms.testsss)
                console.log(this.custom)
            },
            removeItem(index){
                this.forms.testsss.splice(index,1)
            },
            //重新赋值
            reloadv(){

            },
            //单步执行
            ceshi1(){
                console.log(this.form)
                // var encrypt = new JSEncrypt()
                // encrypt.setPublicKey('MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLLvjNPfoEjbIUyGFcIFI25Aqhjgazq0dabk/w1DUiUiREmMLRbWY4lEukZjK04e2VWPvKjb1K6LWpKTMS0dOs5WbFZioYsgx+OHD/DV7L40PHLjDYkd4ZWV2EDlS8qcpx6DYw1eXr6nHYZS1e9EoEBWojDUcolzyBXU3r+LDjUQIDAQAB')
                // var pass = encrypt.encrypt(this.form.password)
                // this.form.password = pass
                // console.log(this.form.password)
                let ip = this.form.ip
                let name = this.form.name
                let password = this.form.password
                let mode = this.form.mode
                let port = this.form.port
                let configureCiphers = this.form.configureCiphers
                var command = []
                this.forms.testsss.forEach(e=>{
                    command.push(e.value)
                })
                // let zuihouall = this.deviceInfo.map(x=>JSON.stringify(x))
                // console.log(zuihouall)
                // var qqq = JSON.stringify(zuihouall)
                // console.log(typeof zuihouall)
                //
                // let form11 = new FormData();
                // for (var key in this.form){
                //     form11.append(key,this.form[key])
                // }
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
                console.log(handForm)
                return request({
                    url:'/sql/SwitchInteraction/testToObtainBasicInformation/'+ip+'/'+name+'/'+password+'/'+port+'/'+mode+'/'+configureCiphers+'/'+command,
                    method:'post',
                    data:handForm
                }).then(response=>{
                    // this.$message.success('提交成功!')
                    console.log(response)
                    this.returnInfo  = response
                })
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
                    this.helpT = '请输入标识符'
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
                this.$alert(this.helpT, '标题名称', {
                    confirmButtonText: '确定'
                });
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
                // console.log(thisIndex)
                //动态添加的样式空格问题
                // console.log(this.$refs.btn[thisIndex])
                // console.log(item)
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
                    this.$set(item1,'position',0)
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
            //提交获取基本信息命令
            tijiao(){
                // window.location.reload()   刷新页面
                // this.$router.go(0)   刷新页面
                // this.zhidu = false
                //基本信息命令
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
                console.log(handForm)
                var command = []
                this.forms.testsss.forEach(e=>{
                    command.push(e.value)
                })
                var custom = this.custom
                if(handForm.length == 0){
                    alert('提交失败,分析逻辑不能为空!')
                }else {
                    return request({
                        url:'/sql/DefinitionProblemController/insertInformationAnalysis/'+command+'/'+custom,
                        method:'post',
                        data:handForm
                    }).then(response=>{
                        this.$message.success('提交成功!')
                    })
                }

                // MessageBox.confirm('确定提交吗？','提示').then(c=>{
                //
                // }).catch(ee=>{
                //     this.$message.warning('取消提交!')
                // })
                // this.forms.dynamicItem.shift(); hasOwnProperty(存在) 删除数组第一个元素
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
