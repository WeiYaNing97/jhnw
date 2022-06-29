package com.sgcc.web.controller.sql;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.web.controller.webSocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import org.hyperic.sigar.SigarException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年11月19日 15:57
 */
@RestController
@RequestMapping("/sql/switch_command")
@Slf4j
public class SwitchController  {

    public static void Memory_CPU() throws InterruptedException, SigarException {

        while(true) {
            SystemInfo systemInfo = new SystemInfo();
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            long totalByte = memory.getTotal();
            long acaliableByte = memory.getAvailable();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            long[] ticks = processor.getSystemCpuLoadTicks();
            long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
            long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
            long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
            long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
            long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
            long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
            log.info("内存大小 = {},内存使用率 ={}",formatByte(totalByte),new DecimalFormat("#.##%").format((totalByte-acaliableByte)*1.0/totalByte));
            log.info("CPU总数 = {},CPU利用率 ={}",processor.getLogicalProcessorCount(),new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu)));
            String Memory_CPU = "内存大小 : "+formatByte(totalByte)+"\r\n"+
                    "内存使用率 : "+new DecimalFormat("#.##%").format((totalByte-acaliableByte)*1.0/totalByte)+"\r\n"+
                    "CPU总数 : "+processor.getLogicalProcessorCount()+"\r\n"+
                    "CPU利用率 : "+new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu));
            WebSocketService.sendMessage("Memory_CPU",Memory_CPU);
            //System.out.println(Memory_CPU);
            Thread.sleep(1000);
        }

    }

    public static String formatByte(long byteNumber){
        double FORMAT = 1024.0;
        double kbNumber = byteNumber/FORMAT;
        if(kbNumber<FORMAT){
            return new DecimalFormat("#.##KB").format(kbNumber);
        }
        double mbNumber = kbNumber/FORMAT;
        if(mbNumber<FORMAT){
            return new DecimalFormat("#.##MB").format(mbNumber);
        }
        double gbNumber = mbNumber/FORMAT;
        if(gbNumber<FORMAT){
            return new DecimalFormat("#.##GB").format(gbNumber);
        }
        double tbNumber = gbNumber/FORMAT;
        return new DecimalFormat("#.##TB").format(tbNumber);
    }



    /*@RequestMapping("/SwitchEntry")
    public List<SwitchEntry> SwitchEntry(String file) {
        file = "C:\\Users\\Administrator\\Desktop\\123.xls";
        List<SwitchEntry> switchEntries = new ArrayList<>();
        try {
            List<Object> excel = excel(file);
            for (Object object:excel){
                List<String> stringList = (List<String>) object;
                if (stringList.get(0) == null || stringList.get(0)==""
                    ||stringList.get(1) == null || stringList.get(1)==""
                        ||stringList.get(2) == null || stringList.get(2)==""){
                    return switchEntries;
                }
                SwitchEntry switchEntry = new SwitchEntry();
                switchEntry.setSwitchIp(stringList.get(0));
                switchEntry.setSwitchName(stringList.get(1));
                switchEntry.setSwitchPassword(stringList.get(2));
                switchEntry.setConnectionMode(stringList.get(3));
                switchEntry.setPortNumber(stringList.get(4));
                switchEntries.add(switchEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return switchEntries;
    }


    public static List<Object> excel(String file) throws Exception {
        //用流的方式先读取到你想要的excel的文件
        FileInputStream fis = new FileInputStream(new File(file));
        //解析excel
        POIFSFileSystem pSystem = new POIFSFileSystem(fis);
        //获取整个excel
        HSSFWorkbook hb = new HSSFWorkbook(pSystem);
        //获取第一个表单sheet
        HSSFSheet sheet = hb.getSheetAt(0);
        //获取第一行
        int firstrow = sheet.getFirstRowNum();
        //获取最后一行
        int lastrow = sheet.getLastRowNum();
        List<Object> objectList = new ArrayList<>();
        //循环行数依次获取列数
        for (int i = firstrow; i < lastrow+2; i++) {
            //获取哪一行i
            Row row = sheet.getRow(i);
            if (row != null) {
                //获取这一行的第一列
                int firstcell = row.getFirstCellNum();
                //获取这一行的最后一列
                int lastcell = row.getLastCellNum();
                //创建一个集合，用处将每一行的每一列数据都存入集合中
                List<String> list = new ArrayList<>();
                for (int j = firstcell; j <lastcell; j++) {
                    //获取第j列
                    Cell cell = row.getCell(j);

                    if (cell != null) {
                        list.add(cell.toString());
                    }else {
                        list.add("");
                    }

                }
                objectList.add(list);
            }
        }
        fis.close();
        return objectList;
    }

    @RequestMapping("/export")
    @ResponseBody
    public AjaxResult export() {

        List<SwitchEntry> list = null;
        ExcelUtil<SwitchEntry> util = new ExcelUtil<SwitchEntry>(SwitchEntry.class);
        return util.exportExcel(list, "交换机登录信息");

    }*/

}