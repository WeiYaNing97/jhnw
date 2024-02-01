package com.sgcc.advanced.test;

import com.sgcc.advanced.thread.TimedTaskRetrievalFile;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.util.EncryptUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2023-12-29 11:37
 **/
public class Test {
    public static void main(String[] args) {
        /*String string = "D:\\jhnwadminlog\\1706085922379角色数据.xlsx";
        String string1 = "D:\\jhnwadminlog\\1706086856513用户数据.xlsx";
        try {
            FileInputStream file = new FileInputStream(new File(string));
            Workbook workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        String excelName = "交换机信息模板";

        List<SwitchLoginInformation> switchLoginInformations = TimedTaskRetrievalFile.readCiphertextExcel(MyUtils.getProjectPath()+"\\jobExcel\\"+ excelName +".txt");

        System.err.println();
    }
}
