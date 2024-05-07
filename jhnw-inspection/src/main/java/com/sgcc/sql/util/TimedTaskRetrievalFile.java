package com.sgcc.sql.util;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.util.EncryptUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.sql.domain.TimedTask;
import com.sgcc.sql.service.ITimedTaskService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: jhnw
 * @description: 定时任务获取登录信息
 * @author:
 * @create: 2024-01-15 14:47
 **/
@Api("定时任务文件上传")
@RestController
@RequestMapping("/sql/timedTaskRetrievalFile")
public class TimedTaskRetrievalFile {

    @Autowired
    private ITimedTaskService timedTaskService;

    @GetMapping("/getFileNames")
    public List<String> getFileNames() {
        /*查看文件夹中 是否有该名称文件*/
        return getACollectionOfFileNames(MyUtils.getProjectPath() + "\\jobExcel");
    }

    /**
    * @Description  前端传入明文数据 写入 项目部署文件夹 生成密文
    * @param file
    * @return
    */
    @PostMapping("/localFileImportProjectAddress")
    public static AjaxResult LocalFileImportProjectAddress(@RequestParam("file") MultipartFile file) throws Exception {
        /*查看文件夹中 是否有该名称文件*/
        List<String> aCollectionOfFileNames = getACollectionOfFileNames(MyUtils.getProjectPath() + "\\jobExcel");
        if (aCollectionOfFileNames.indexOf( file.getOriginalFilename().split("\\.")[0] ) != -1){
            return AjaxResult.error("文件名称重复，请更换文件名称");
        }
        List<SwitchLoginInformation> switchLoginInformations = TimedTaskRetrievalFile.readPlaintextExcel(file);
        if (switchLoginInformations.size() == 0){
            return AjaxResult.error("上传文件为空");
        }else if (checkIfAllSetContentsAreNull(switchLoginInformations)){
            return AjaxResult.error("上传文件解析失败");
        }
        TimedTaskRetrievalFile.writeStringArrayToFile(switchLoginInformations, MyUtils.getProjectPath()+"\\jobExcel\\"+ file.getOriginalFilename().split("\\.")[0]+".txt");
        /*获取定时任务 获取交换机登录信息 集合*/
        return AjaxResult.success("文件上传成功");
    }

    public static boolean checkIfAllSetContentsAreNull(List<SwitchLoginInformation> list) {
        for (SwitchLoginInformation element : list) {
            if (element != null) {
                return false ;
            }
        }
        return true;
    }

    /**
    * @Description 读取明文 Excel 表格数据   得到实体类集合
    * @author charles
    * @createTime 2024/1/19 10:39
    * @desc
    * @param file
     * @return
    */
    public static List<SwitchLoginInformation> readPlaintextExcel(MultipartFile file) throws Exception {
        ExcelUtil<SwitchLoginInformation> util = new ExcelUtil<SwitchLoginInformation>(SwitchLoginInformation.class);
        List<SwitchLoginInformation> userList = util.importExcel(file.getInputStream());
        return userList;
    }

    /**
     * @Description 获取文件夹中的文件名称
     * @author charles
     * @createTime 2024/1/18 15:19
     * @desc
     * @param folderPath
     * @return
     */
    @GetMapping("/getACollectionOfFileNames")
    public static List<String> getACollectionOfFileNames(String folderPath) {
        // 指定文件夹路径
        // 创建File对象
        File folder = new File(folderPath);

        // 获取文件夹下的所有文件和文件夹
        File[] files = folder.listFiles();
        List<String> nameList = new ArrayList<>();
        if (files == null){
            return nameList;
        }

        // 遍历文件数组，打印文件名（包含后缀）
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                int lastDotIndex = fileName.lastIndexOf('.');
                if (lastDotIndex != -1) {
                    String fileNameWithoutSuffix = fileName.substring(0, lastDotIndex);
                    nameList.add(fileNameWithoutSuffix);
                } else {
                    nameList.add(fileName);
                }
            }
        }

        return nameList;
    }

    /**
    * @Description
    * @author charles
    * @createTime 2024/1/19 10:59
    * @desc
    * @param switchLoginInformations
     * @param filePath
     * @return
    */
    public static void writeStringArrayToFile(List<SwitchLoginInformation> switchLoginInformations, String filePath) {
        File file = new File(filePath);
        // 创建目录
        File dir = new File(file.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 如果文件存在，删除文件
        if (file.exists()) {
            file.delete();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (SwitchLoginInformation pojo : switchLoginInformations) {
                pojo.setPassword(EncryptUtil.densificationAndSalt(pojo.getPassword()));
                pojo.setConfigureCiphers(EncryptUtil.densificationAndSalt(pojo.getConfigureCiphers()));
                bw.write(pojo.toJson());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 读取密文 Excel 表格数据 并 解密
     * @author charles
     * @createTime 2024/1/12 16:01
     * @desc
     * @param filePath
     * @return
     */
    public static List<SwitchLoginInformation> readCiphertextExcel(String filePath) {
        List<String> pojolist = PathHelper.ReadFileContent(filePath);
        List<SwitchLoginInformation> switchLoginInformationList = new ArrayList<>();
        for (String pojo:pojolist){
            /*解密*/
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(pojo, SwitchLoginInformation.class);
            switchLoginInformation.setPassword(EncryptUtil.desaltingAndDecryption(switchLoginInformation.getPassword()));
            switchLoginInformation.setConfigureCiphers(EncryptUtil.desaltingAndDecryption(switchLoginInformation.getConfigureCiphers()));
            if (switchLoginInformation.getRow_index().equals("null")){
                switchLoginInformation.setRow_index(null);
            }
            switchLoginInformationList.add(switchLoginInformation);
        }
        return switchLoginInformationList;
    }


    @DeleteMapping("/deleteFileBasedOnFileName/{fileName}")
    public AjaxResult deleteFileBasedOnFileName(@PathVariable String fileName) {

        /** 根据定时任务模板 查询定时任务数据*/
        TimedTask timedTask = new TimedTask();
        timedTask.setTimedTaskParameters(fileName);
        List<TimedTask> timedTaskList = timedTaskService.selectTimedTaskList(timedTask);
        if (timedTaskList.size() != 0){
            return  AjaxResult.error(fileName + " 模板正在使用不允许删除");
        }

       String  filePath = MyUtils.getProjectPath() + "\\jobExcel\\"+fileName+".txt";
        // 创建一个文件对象，指定要删除的文件路径
        File file = new File(filePath);

        // 调用delete()方法删除文件
        boolean isDeleted = file.delete();

        // 判断文件是否删除成功
        if (isDeleted) {
            return  AjaxResult.success(fileName + " 模板删除成功");
        } else {
            return  AjaxResult.error(fileName + " 模板删除失败");
        }
    }
}
