package com.sgcc.advanced.test;

import com.sgcc.advanced.aggregation.IPAddressCalculator;
import com.sgcc.advanced.domain.IPCalculator;

public class PerformanceTest {
    public static void main(String[] args) {
        long totalDuration = 0;
		String valueStr = "";
        for (int i = 0; i <= 50; i++) {
            // 记录开始时间
            long startTime = System.nanoTime();
            long startMemory = getUsedMemory();

            // 执行一些计算
            computeTask();

            // 记录结束时间和内存
            long endTime = System.nanoTime();
            long endMemory = getUsedMemory();

            // 计算本次运行的持续时间和内存消耗
            long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒
            long memoryUsed = endMemory - startMemory;
			if(i>0){
				// 输出结果
				valueStr = valueStr+ "\r\n" + ("Run " + i + ": Time=" + duration + "ms, Memory=" + memoryUsed + " bytes");
				// 累加总时间
				totalDuration += duration;
			}
        }

		System.out.println(valueStr);

        // 输出平均时间
        double averageTime = (double) totalDuration / 50;
        System.out.println("Average Time: " + averageTime + " ms");
    }

    private static void computeTask() {
        // 这里可以放置任何计算密集型的任务
        // 输出九九乘法表
		for(int z = 1; z <= 100; z++){
			for (int i = 1; i <= 9; i++) {
				for (int j = 1; j <= 9; j++) {
					int w = z+i+j;
					/*System.out.println();*/
                    getEncodingID(w+"");
				}
			}
		}
		/*System.out.println();*/
    }

    private static long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

	/**
     * 要在Java中获取字符串第一个数值数字部分，可以使用正则表达式。 */
    public static String getEncodingID(String input) {
        String[] split = input.split(",");
        String str = split[0];
        if (str.startsWith("\'") || str.startsWith("\"")){
            str = str.substring(1,str.length());
        }
        if (str.endsWith("\'") || str.endsWith("\"")){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }
}