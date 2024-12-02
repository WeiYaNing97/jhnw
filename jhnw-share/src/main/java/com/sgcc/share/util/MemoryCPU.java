package com.sgcc.share.util;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;
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
        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessageAll(Memory_CPU);
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
        // 获取处理器信息
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        // 获取系统CPU负载的ticks值
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        // 再次获取系统CPU负载的ticks值
        long[] ticks = processor.getSystemCpuLoadTicks();
        // 计算各种CPU状态的ticks差值
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
        // 计算总的CPU ticks
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        // 获取逻辑处理器的数量
        TotalCPUs = processor.getLogicalProcessorCount();
        // 计算CPU的空闲率，并格式化为百分比字符串
        CPUUtilization = new DecimalFormat("#.##%").format(idle * 1.0 / totalCpu);
        // 将CPU使用率转换为百分比字符串，保留两位小数
        CPUUtilization = keepTwoDecimalPlaces((100.00 - Double.valueOf(CPUUtilization.substring(0,CPUUtilization.length()-1)).doubleValue()))+"%";
    }


        /**
         * 保留两位小数的工具方法
         *
         * @param value 需要保留两位小数的浮点数
         * @return 保留两位小数后的字符串
         */
    public static String keepTwoDecimalPlaces(double value) {

        // 将double类型的值转换为BigDecimal类型
        BigDecimal bd = new BigDecimal(value);

        // 设置小数点后保留两位，并采用四舍五入的舍入模式
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        // 将BigDecimal对象转换为字符串并返回
        return bd.toString();
    }

}