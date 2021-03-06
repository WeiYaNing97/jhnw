package com.sgcc.web.controller.sql;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import org.hyperic.sigar.SigarException;
import java.text.DecimalFormat;
/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年11月19日 15:57
 */
@RestController
@RequestMapping("/sql")
@Slf4j
public class MemoryCPU {

    private static String MemorySize = null;//内存大小
    private static String MemoryUsage = null;//内存使用率
    private static int TotalCPUs;//CPU总数
    private static String CPUUtilization = null;//CPU利用率

    @RequestMapping("/get_Memory_CPU")
    public String get_Memory_CPU() {
        String Memory_CPU = "内存大小 : "+MemorySize+"\r\n"+
                "内存使用率 : "+MemoryUsage+"\r\n"+
                "CPU总数 : "+TotalCPUs+"\r\n"+
                "CPU利用率 : "+CPUUtilization+"\r\n";
        return Memory_CPU;
    }

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

            MemorySize =formatByte(totalByte);
            MemoryUsage = new DecimalFormat("#.##%").format((totalByte-acaliableByte)*1.0/totalByte);
            TotalCPUs = processor.getLogicalProcessorCount();
            CPUUtilization = new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu));

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

}