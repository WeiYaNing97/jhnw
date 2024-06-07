package com.sgcc.share.util;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @date 2021年11月19日 15:57
 *
 */
@Api("获取服务器CPU、内存大小及使用率")
@RestController
@RequestMapping("/sql")
@Slf4j

/**
 * 定时任务的使用
 * @author pan_junbiao
 **/
public class MemoryCPU extends TimerTask {

    @Override
    public void run() {
        get_Memory_CPU();
    }

    private static String MemorySize = null;//内存大小
    private static String MemoryUsage = null;//内存使用率
    private static int TotalCPUs;//CPU总数
    private static String CPUUtilization = null;//CPU利用率

    /**
     * 获取服务器CPU、内存大小及使用率
     */
    @ApiOperation("获取服务器CPU、内存大小及使用率")
    //@GetMapping("/get_Memory_CPU")
    public void get_Memory_CPU() {
        // 初始化系统信息
        initSystemInfo();

        // 拼接内存和CPU信息字符串
        String Memory_CPU = "内存大小 : "+MemorySize+"\r\n"+
                "内存使用率 : "+MemoryUsage+"\r\n"+
                "CPU总数 : "+TotalCPUs+"\r\n"+
                "CPU利用率 : "+CPUUtilization+"\r\n";

        // 将内存和CPU信息通过WebSocket发送给所有客户端
        WebSocketService.sendMessageAll(Memory_CPU);
    }


    /**
     * 获取系统详细参数信息
     *
     * @author Benjamin
     * @date 2022/12/8 10:02:50
     * @version 1.0.0
     */
    public void initSystemInfo() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            // 总的物理内存
            String totalMemorySize = new DecimalFormat("#.##")
                    .format(osmxb.getTotalPhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G";
            // 已使用的物理内存
            String usedMemory = new DecimalFormat("#.##").format(
                    (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / 1024.0 / 1024 / 1024)
                    + "G";
            // 获得线程总数
            ThreadGroup parentThread;
            for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                    .getParent() != null; parentThread = parentThread.getParent()) {

            }
            printlnCpuInfo(systemInfo);
            MemorySize = totalMemorySize;
            MemoryUsage = keepTwoDecimalPlaces(Float.valueOf(usedMemory.substring(0,usedMemory.length()-1)).floatValue()/Float.valueOf(totalMemorySize.substring(0,totalMemorySize.length()-1)).floatValue()*100)+"%";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 CPU 信息
     * @param systemInfo
     */
    private void printlnCpuInfo(SystemInfo systemInfo) throws InterruptedException {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

        TotalCPUs = processor.getLogicalProcessorCount();
        CPUUtilization = new DecimalFormat("#.##%").format(idle * 1.0 / totalCpu);
        CPUUtilization = keepTwoDecimalPlaces((100.00 - Double.valueOf(CPUUtilization.substring(0,CPUUtilization.length()-1)).doubleValue()))+"%";
    }

    public static String keepTwoDecimalPlaces(double value) {

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }
}