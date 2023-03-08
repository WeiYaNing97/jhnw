<template>
  <div>
    <el-dialog
      title="提示"
      :visible.sync="dialogVisibleHelp"
      class="thisblack-bgc"
      style="padding: 10px"
      width="40%">
      <div id="fatherq" class="qqqqq">
        <h3 style="font-weight: bolder" id="brand" ref="brand">品牌</h3>
        <p>请输入品牌：</p>
        <h3 style="font-weight: bolder" id="type" ref="type">型号</h3>
        <p>请输入型号：</p>
        <h3 style="font-weight: bolder" id="firewareVersion" ref="firewareVersion">固件版本</h3>
        <p>请输入固件版本：</p>
        <h3 style="font-weight: bolder" id="subVersion" ref="subVersion">子版本</h3>
        <p>请输入子版本：</p>
        <h3 style="font-weight: bolder" id="typeProblem" ref="typeProblem">范式分类</h3>
        <p>请输入范式类型：</p>
        <h3 style="font-weight: bolder" id="temProName" ref="temProName">范式名称</h3>
        <p>请输入范式名称：</p>
        <h3 style="font-weight: bolder" id="problemName" ref="problemName">自定义名称</h3>
        <p>请输入自定义名称：</p>
        <h3 style="font-weight: bolder" id="beizhu" ref="beizhu">备注</h3>
        <p>请输入备注：</p>
        <h3 style="font-weight: bolder" id="biaoshi" ref="biaoshi">标识符</h3>
        <p>请输入标识符：</p>
        <h3 style="font-weight: bolder" id="comone" ref="comone">命令</h3>
        <p>请输入命令：</p>
        <a :href="whelp" style="display: none" ref="bang"><span>阿斯顿撒大</span></a>
      </div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisibleHelp = false">取 消</el-button>
    <el-button type="primary" @click="dialogVisibleHelp = false">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
    export default {
        name: "help_txt",
        props:['dialogVisibleHelp'],
        data(){
            return {
                dialogVisibleHelp:false,
                whelp:''
            }
        }
    }
</script>

<style scoped>
.thisblack-bgc{
  background-color: rgba(0,0,0,0.5);
}
</style>
