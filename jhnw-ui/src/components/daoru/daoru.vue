<template>
  <div>
    <div class="flex-display">
      <div class="left-box">表格上传(ElementUI):</div>
<!--      http://localhost:81/daoru/-->
      <el-upload
        action=""
        :on-success="handleChange"
        :file-list="fileList"
        class="el-upload"
      >
        <el-button size="small" type="primary" class="el-btn"
        >点击上传</el-button
        >
        <div slot="tip" class="el-upload-tip">
          只能上传xlsx文件，且不超过5MB
        </div>
      </el-upload>
    </div>
    <el-table v-if="tableHead.length" :data="tableData[0]" style="width: 100%">
      <el-table-column
        v-for="(data, key) in tableHead"
        :prop="data"
        :label="data"
        :key="key"
        width="180"
      >
      </el-table-column>
    </el-table>
    <div class="flex-display">
      <div class="left-box">文件上传(input)：</div>
      <input type="file" v-on:change="onChange" class="file-ipt" />
    </div>
  </div>
</template>

<script>
    import Vue from "vue";
    import ElementUI from "element-ui";
    import "element-ui/lib/theme-chalk/index.css";
    import { read, utils } from "xlsx"; // 注意处理方法引入方式
    Vue.use(ElementUI);
    export default {
        name: "daoru",
        data() {
            return {
                fileList: [], //上传文件列表
                tableHead: [], //表头
                tableData: [] // 表数据
            };
        },
        methods: {
            onChange(e) {
                const file = e.target.files[0];
                const fileReader = new FileReader();

                fileReader.onload = ev => {
                    try {
                        const data = ev.target.result;
                        const workbook = read(data, { type: "binary" });
                        const params = [];
                        // 取对应表生成json表格内容
                        workbook.SheetNames.forEach(item => {
                            params.push({
                                name: item,
                                dataList: utils.sheet_to_json(workbook.Sheets[item])
                            });
                            this.tableData.push(utils.sheet_to_json(workbook.Sheets[item]));
                        });
                        // 该算法仅针对表头无合并的情况
                        if (this.tableData.length > 0) {
                            // 获取excel中第一个表格数据tableData[0][0]，并且将表头提取出来
                            for (const key in this.tableData[0][0]) {
                                this.tableHead.push(key);
                                // console.log(this.tableHead)
                            }
                        }
                        return params;
                        // 重写数据
                    } catch (e) {
                        console.log("error:" + e);
                        return false;
                    }
                };
                fileReader.readAsBinaryString(file);
            },
            handleChange(res, file, fileList) {
                // 将文件放入
                for (let i = 0; i < fileList.length; i++) {
                    if (file.name != fileList[i].name) {
                        this.fileList.push({
                            name: file.name,
                            url: "",
                            uid: file.uid
                        });
                    }
                }

                // this.fileList = fileList.slice(-3);
                const files = { 0: file };
                this.readExcel(files);
            },
            readExcel(file) {
                const fileReader = new FileReader();

                fileReader.onload = ev => {
                    try {
                        const data = ev.target.result;
                        const workbook = read(data, { type: "binary" });
                        const params = [];
                        // 取对应表生成json表格内容
                        workbook.SheetNames.forEach(item => {
                            params.push({
                                name: item,
                                dataList: utils.sheet_to_json(workbook.Sheets[item])
                            });
                            this.tableData.push(utils.sheet_to_json(workbook.Sheets[item]));
                        });
                        // 该算法仅针对表头无合并的情况
                        if (this.tableData.length > 0) {
                            // 获取excel中第一个表格数据tableData[0][0]，并且将表头提取出来
                            for (const key in this.tableData[0][0]) {
                                this.tableHead.push(key);
                            }
                        }
                        return params;
                        // 重写数据
                    } catch (e) {
                        console.log("error:" + e);
                        return false;
                    }
                };
                fileReader.readAsBinaryString(file[0].raw);
            }
        }
    }
</script>

<style scoped>

</style>
