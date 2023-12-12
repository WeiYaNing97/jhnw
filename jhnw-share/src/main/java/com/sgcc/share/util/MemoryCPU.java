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
@Component
@EnableScheduling   // 1.开启定时任务
public class MemoryCPU {

    private static String MemorySize = null;//内存大小
    private static String MemoryUsage = null;//内存使用率
    private static int TotalCPUs;//CPU总数
    private static String CPUUtilization = null;//CPU利用率

    @ApiOperation("获取服务器CPU、内存大小及使用率")
    //@GetMapping("/get_Memory_CPU")
    public String get_Memory_CPU() {
        String Memory_CPU = "内存大小 : "+MemorySize+"\r\n"+
                "内存使用率 : "+MemoryUsage+"\r\n"+
                "CPU总数 : "+TotalCPUs+"\r\n"+
                "CPU利用率 : "+CPUUtilization+"\r\n";
        //WebSocketService.sendMessageAll(Memory_CPU);
        return Memory_CPU;
    }

    /*系统自带参数*/
    private void systemParamsNameAndValue() {
        Properties properties = System.getProperties();
        Set<Map.Entry<Object, Object>> params = properties.entrySet();
        for (Iterator<Map.Entry<Object, Object>> iterator = params.iterator(); iterator.hasNext();) {
            Map.Entry<Object, Object> systemParam = iterator.next();
            Object key = systemParam.getKey();
            Object value = systemParam.getValue();
            System.out.println("系统属性名: [" + key + "]  ---------系统属性名字对应值: " + value);
        }

    }

    /***
     * @author Benjamin
     * @date 2022/12/8 10:02:50
     * @version 1.0.0
     * @description 获取系统详细参数信息
     * @param
     * @return void
     */
    @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
    public void initSystemInfo() {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                SystemInfo systemInfo = new SystemInfo();
                OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                // 椎内存使用情况
                MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
                // 初始的总内存
                long initTotalMemorySize = memoryUsage.getInit();
                // 最大可用内存
                long maxMemorySize = memoryUsage.getMax();
                // 已使用的内存
                long usedMemorySize = memoryUsage.getUsed();
                // 操作系统
                String osName = System.getProperty("os.name");
                // 总的物理内存
                String totalMemorySize = new DecimalFormat("#.##")
                        .format(osmxb.getTotalPhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G";
                // 剩余的物理内存
                String freePhysicalMemorySize = new DecimalFormat("#.##")
                        .format(osmxb.getFreePhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G";
                // 已使用的物理内存
                String usedMemory = new DecimalFormat("#.##").format(
                        (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / 1024.0 / 1024 / 1024)
                        + "G";
                // 获得线程总数
                ThreadGroup parentThread;
                for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                        .getParent() != null; parentThread = parentThread.getParent()) {

                }
                int totalThread = parentThread.activeCount();
                // 磁盘使用情况
                File[] files = File.listRoots();
                for (File file : files) {
                    String total = new DecimalFormat("#.#").format(file.getTotalSpace() * 1.0 / 1024 / 1024 / 1024)
                            + "G";
                    String free = new DecimalFormat("#.#").format(file.getFreeSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
                    String un = new DecimalFormat("#.#").format(file.getUsableSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
                    String path = file.getPath();
                    /*System.err.println(path + "总:" + total + ",可用空间:" + un + ",空闲空间:" + free);
                    System.err.println("=============================================");*/
                }
                /*System.err.println("操作系统:" + osName);
                System.err.println("程序启动时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(ManagementFactory.getRuntimeMXBean().getStartTime())));
                System.err.println("pid:" + System.getProperty("PID"));
                System.err.println("cpu核数:" + Runtime.getRuntime().availableProcessors());*/
                printlnCpuInfo(systemInfo);
                /*System.err.println("JAVA_HOME:" + System.getProperty("java.home"));
                System.err.println("JAVA_VERSION:" + System.getProperty("java.version"));
                System.err.println("USER_HOME:" + System.getProperty("user.home"));
                System.err.println("USER_NAME:" + System.getProperty("user.name"));
                System.err.println("初始的总内存(JVM):"
                        + new DecimalFormat("#.#").format(initTotalMemorySize * 1.0 / 1024 / 1024) + "M");
                System.err.println(
                        "最大可用内存(JVM):" + new DecimalFormat("#.#").format(maxMemorySize * 1.0 / 1024 / 1024) + "M");
                System.err.println(
                        "已使用的内存(JVM):" + new DecimalFormat("#.#").format(usedMemorySize * 1.0 / 1024 / 1024) + "M");
                System.err.println("总的物理内存:" + totalMemorySize);
                System.out
                        .println("总的物理内存:"
                                + new DecimalFormat("#.##").format(
                                systemInfo.getHardware().getMemory().getTotal() * 1.0 / 1024 / 1024 / 1024)
                                + "M");
                System.err.println("剩余的物理内存:" + freePhysicalMemorySize);
                System.out
                        .println("剩余的物理内存:"
                                + new DecimalFormat("#.##").format(
                                systemInfo.getHardware().getMemory().getAvailable() * 1.0 / 1024 / 1024 / 1024)
                                + "M");
                System.err.println("已使用的物理内存:" + usedMemory);
                System.out.println("已使用的物理内存:"
                        + new DecimalFormat("#.##").format((systemInfo.getHardware().getMemory().getTotal()
                        - systemInfo.getHardware().getMemory().getAvailable()) * 1.0 / 1024 / 1024 / 1024)
                        + "M");
                System.err.println("总线程数:" + totalThread);
                System.err.println("===========================");*/
                MemorySize = totalMemorySize;
                //System.err.println(MemorySize);
                MemoryUsage = keepTwoDecimalPlaces(Float.valueOf(usedMemory.substring(0,usedMemory.length()-1)).floatValue()/Float.valueOf(totalMemorySize.substring(0,totalMemorySize.length()-1)).floatValue()*100)+"%";
                //System.err.println(MemoryUsage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 60, TimeUnit.SECONDS);
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
        /*System.err.println("cpu核数:" + processor.getLogicalProcessorCount());
        System.err.println("cpu系统使用率:" + new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
        System.err.println("cpu用户使用率:" + new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
        System.err.println("cpu当前等待率:" + new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));
        System.err.println("cpu当前空闲率:" + new DecimalFormat("#.##%").format(idle * 1.0 / totalCpu));*/

        TotalCPUs = processor.getLogicalProcessorCount();
        //System.err.println(TotalCPUs);
        CPUUtilization = new DecimalFormat("#.##%").format(idle * 1.0 / totalCpu);
        CPUUtilization = keepTwoDecimalPlaces((100.00 - Double.valueOf(CPUUtilization.substring(0,CPUUtilization.length()-1)).doubleValue()))+"%";
        //System.err.println(CPUUtilization);

        /*long[] ticksArray = {1,2,3,4,5,6,7,8};
        System.err.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks(ticksArray) * 100);
        System.err.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad(1L) * 100);*/
    }

    public static String keepTwoDecimalPlaces(double value) {

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }

}