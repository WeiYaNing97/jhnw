<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="info"
              plain
              icon="el-icon-upload2"
              @click="handleImport"
              v-hasPermi="['system:user:import']"
            >导入</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="el-icon-download"
              :loading="exportLoading"
              @click="exportData"
              v-hasPermi="['system:user:export']"
            >导出</el-button>
          </el-col>
          <el-col :span="1.5">
<!--            <el-button-->
<!--              type="warning"-->
<!--              plain-->
<!--              icon="el-icon-download"-->
<!--              size="mini"-->
<!--              :loading="exportLoading"-->
<!--              @click="exportData"-->
<!--              v-hasPermi="['system:user:export']"-->
<!--            >导出</el-button>-->
            <el-button  type="danger"
                        plain icon="el-icon-delete"
                        @click="delDatabase">清空数据库</el-button>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
<!--          <div class="el-upload__tip" slot="tip">-->
<!--            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的用户数据-->
<!--          </div>-->
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
    import { listUser, getUser, delUser, addUser, updateUser, exportUser, resetUserPwd, changeUserStatus, importTemplate } from "@/api/system/user";
    import { getToken } from "@/utils/auth";
    import { treeselect } from "@/api/system/dept";
    import Treeselect from "@riophae/vue-treeselect";
    import "@riophae/vue-treeselect/dist/vue-treeselect.css";
    import request from '@/utils/request'
    import  {MessageBox} from "element-ui"
    import log from "../../monitor/job/log";

    export default {
        name: "User",
        dicts: ['sys_normal_disable', 'sys_user_sex'],
        components: { Treeselect },
        data() {
            return {
                // proId:126,
                // 遮罩层
                loading: true,
                // 导出遮罩层
                exportLoading: false,
                // 选中数组
                ids: [],
                // 弹出层标题
                title: "",
                // 是否显示弹出层
                open: false,
                // 默认密码
                initPassword: undefined,
                // 表单参数
                form: {},
                // 用户导入参数
                upload: {
                    // 是否显示弹出层（用户导入）
                    open: false,
                    // 弹出层标题（用户导入）
                    title: "",
                    // 是否禁用上传
                    isUploading: false,
                    // 是否更新已经存在的用户数据
                    updateSupport: 0,
                    // 设置上传的请求头部
                    headers: { Authorization: "Bearer " + getToken() },
                    // 上传的地址
                    // url: process.env.VUE_APP_BASE_API + "/system/user/importData"
                        // /sql/DefinitionProblemController/importData
                    url:process.env.VUE_APP_BASE_API + "/sql/DefinitionProblemController/importData"
                },
                // 查询参数
                queryParams: {
                    pageNum: 1,
                    pageSize: 10,
                    userName: undefined,
                    phonenumber: undefined,
                    status: undefined,
                    deptId: undefined
                }
            };
        },
        watch: {

        },
        created() {
            this.getConfigKey("sys.user.initPassword").then(response => {
                this.initPassword = response.msg;
            });
        },
        methods: {
            // 取消按钮
            cancel() {
                this.open = false;
            },
            //清空数据库
            delDatabase(){
                MessageBox.confirm('确定清空数据库吗？','提示').then(c=>{
                    console.log('sssss')
                    return request({
                        url:'/sql/DefinitionProblemController/deleteAllTable',
                        method:'post'
                    }).then(response=>{
                        console.log(response)
                    })
                }).catch(ee=>{
                    this.$message.warning('清空操作已取消!')
                })
            },
            //导出数据库数据
            exportData(){
                // console.log(this.proId)
                this.$modal.confirm('是否确认导出？').then(() => {
                    this.exportLoading = true
                    return request({
                        url:'/sql/DefinitionProblemController/scanningSQL',
                        method:'post',
                        // data:this.proId
                    }).then(response=>{
                        console.log(response)
                        console.log(response.data.length)
                        for (let i = 0;i<response.data.length;i++){
                            this.$download.name(response.data[i])
                        }
                        this.exportLoading = false
                    })
                }).catch(() => {})
            },
            /** 导出按钮操作 */
            handleExport() {
                const queryParams = this.queryParams;
                console.log(queryParams)
                this.$modal.confirm('是否确认导出所有用户数据项？').then(() => {
                    this.exportLoading = true;
                    console.log(exportUser(queryParams))
                    return exportUser(queryParams);
                }).then(response => {
                    console.log(response)
                    this.$download.name(response.msg);
                    console.log(response.msg)
                    this.exportLoading = false;
                }).catch(() => {});
            },
            /** 导入按钮操作 */
            handleImport() {
                this.upload.title = "信息导入";
                this.upload.open = true;
            },
            /** 下载模板操作 */
            importTemplate() {
                importTemplate().then(response => {
                    this.$download.name(response.msg);
                });
            },
            // 文件上传中处理
            handleFileUploadProgress(event, file, fileList) {
                this.upload.isUploading = true;
            },
            // 文件上传成功处理
            handleFileSuccess(response, file, fileList) {
                this.upload.open = false;
                this.upload.isUploading = false;
                this.$refs.upload.clearFiles();
                this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
            },
            // 提交上传文件
            submitFileForm() {
                this.$refs.upload.submit();
            }
        }
    };
</script>
