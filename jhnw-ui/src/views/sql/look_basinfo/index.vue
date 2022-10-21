<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="基本信息命令" prop="brand">
        <el-select v-model="basicCom" placeholder="基本信息命令"
                   filterable allow-create @focus="brandLi" style="width: 250px">
          <el-option v-for="(item,index) in comsss"
                     :value-key="index" :label="item.command" :value="item.problemId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="chakan" type="primary">查看</el-button>
      </el-form-item>
      <el-form-item>
        <el-button @click="xiugai" type="primary">编辑</el-button>
      </el-form-item>
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
        <div v-else-if="item.targetType === 'wloop'" :key="index"
             style="display: inline-block">
          <el-form-item label="循环" :prop="'dynamicItem.' + index + '.cycleStartId'">
            <el-input v-model="item.cycleStartId" style="width: 150px"></el-input>
          </el-form-item>
          <el-form-item>
            <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
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
  </div>
</template>

<script>
    import { listLook_test, getLook_test, delLook_test, addLook_test, updateLook_test, exportLook_test } from "@/api/sql/look_test";
    import axios from 'axios'
    import  {MessageBox} from "element-ui"
    import request from '@/utils/request'
    import log from "../../monitor/job/log";

    export default {
        name: "Look_test",
        data() {
            return {
                //全文、按行、比较
                allOne:[],
                allTwo:[],
                //获取基本信息命令
                basicCom:'',
                //比较隐藏
                bizui:false,
                bixiala:true,
                biList:['品牌','型号','固件版本','子版本'],
                //新添加
                checkedQ:false,
                zhidu:true,
                showNo:true,
                checked:false,
                comsss:[],
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
                    brand:'',
                },
                // 表单参数
                form: {},
                isChange:false,
                huicha:[],
                forms: {
                    dynamicItem:[
                        {
                            test:'test',
                            onlyIndex:''
                        }
                    ],
                },
                formss:{
                    dynamicItemss:[
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
            }
        },
        computed:{
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
                        if (eeee.classify === '单词'){
                            eeee.length = `${eeee.length1}W`
                        }else if (eeee.classify === '字母'){
                            eeee.length = `${eeee.length1}L`
                        }else if (eeee.classify === '字符串'){
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
                console.log(this.proId)
                form1.append('basicInformationId',this.proId)
                form1.append('jsonPojoList',handForm)
                console.log(handForm)
                this.newValue = JSON.parse(JSON.stringify(this.forms.dynamicItem))
                return request({
                    url:`/sql/DefinitionProblemController/updatebasicAnalysis?basicInformationId=${this.proId}`,
                    method:'post',
                    data:handForm
                }).then(response=>{
                    this.$message.success('提交修改成功!')
                })
            },
            //下拉框获取后台数据
            brandLi(){
                return request({
                    url:'/sql/basic_information/getPojolist',
                    method:'get',
                }).then(response=>{
                    this.comsss = response
                    console.log(this.comsss)
                })
            },
            //查看获取基本信息
            chakan(){
                console.log(this.basicCom)
                this.comsss.forEach(e=>{
                    if (this.basicCom === e.problemId){
                        this.proId = e.id
                        console.log(this.proId)
                    }
                })
                this.forms.dynamicItem = this.formss.dynamicItemss
                return request({
                    url:'/sql/DefinitionProblemController/getBasicInformationProblemScanLogic',
                    method:'post',
                    data:this.basicCom
                }).then(response=>{
                    console.log(response)
                    this.fDa = []
                    response.data.forEach(l=>{
                        if (l != null){
                            const wei = l.replace(/"=/g,'":')
                            this.fDa.push(JSON.parse(wei))
                        }
                    })
                    this.fDa.forEach(chae=>{
                        if (chae.hasOwnProperty('command') == true){
                            this.$set(chae,'targetType','command')
                            if (chae.resultCheckId === 0){
                                this.$set(chae,'resultCheckId','自定义校验')
                            }else  if (chae.resultCheckId === 1){
                                this.$set(chae,'resultCheckId','常规校验')
                            }
                            this.huicha.push(chae)
                        }else if (chae.matched === '全文精确匹配'){
                            this.$set(chae,'targetType','match')
                            this.huicha.push(chae)
                            // this.hangjf = chae.onlyIndex
                            this.allOne.push(chae.onlyIndex)
                        }else if (chae.matched === '全文模糊匹配'){
                            this.$set(chae,'targetType','dimmatch')
                            this.huicha.push(chae)
                            this.allOne.push(chae.onlyIndex)
                        }else if (chae.matched === '按行精确匹配'){
                            this.$set(chae,'targetType','lipre')
                            this.huicha.push(chae)
                            this.allTwo.push(chae.onlyIndex)
                        }else if (chae.matched === '按行模糊匹配'){
                            this.$set(chae,'targetType','dimpre')
                            this.huicha.push(chae)
                            this.allTwo.push(chae.onlyIndex)
                        }else if (chae.action === '比较'){
                            this.$set(chae,'targetType','analyse')
                            // bixiala bizui
                            this.bizui = true
                            this.bixiala = false
                            this.huicha.push(chae)
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
                            console.log(chae.length.slice(chae.length.length-1))
                            if (chae.length.slice(chae.length.length-1) === 'W'){
                                this.$set(chae,'classify','单词')
                            }else  if (chae.length.slice(chae.length.length-1) === 'S'){
                                this.$set(chae,'classify','字符串')
                            }else if (chae.length.slice(chae.length.length-1) === 'L'){
                                this.$set(chae,'classify','字母')
                            }
                            chae.length1 = chae.length.slice(0,chae.length.length-1)
                            console.log(chae.length)
                            this.huicha.push(chae)
                        }else if (chae.action === '问题'){
                            this.$set(chae,'targetType','prodes')
                            if (chae.tNextId === '异常'){
                                this.$set(chae,'tNextId','有问题')
                            }else if (chae.tNextId === '安全'){
                                this.$set(chae,'tNextId','无问题')
                            }
                            this.huicha.push(chae)
                        }else if (chae.action === '循环'){
                            this.$set(chae,'targetType','wloop')
                            this.huicha.push(chae)
                        }
                    })
                    this.huicha.sort(function (a, b) { return a.pageIndex - b.pageIndex; })
                    this.forms.dynamicItem = this.forms.dynamicItem.concat(this.huicha)
                    this.huicha = []
                    this.oldValue = JSON.parse(JSON.stringify(this.forms.dynamicItem))
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
                    this.$set(item1,'problemId',this.wentiid)
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
    margin-top: 10px;
    margin-bottom: 10px;
  }
</style>
