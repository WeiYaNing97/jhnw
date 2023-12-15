package com.sgcc.framework.security.filter;

/**
 * @program: jhnw
 * @description: 全局过滤器
 * @author:
 * @create: 2023-12-13 18:28
 **/
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//@ControllerAdvice
public class GlobalFilter extends HandlerInterceptorAdapter {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 这里是后置处理的逻辑
        System.out.println("请求处理完成，执行后置处理");
    }
    public void asyncMethod() {

        // 获取所有线程
        Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();

        // 遍历并打印线程信息
        for (Map.Entry<Thread, StackTraceElement[]> entry : allThreads.entrySet()) {

            if ( filterStrings(entry.getKey().getName())
                    && (entry.getKey().getState()+"").indexOf("WAITING") != -1 ){

                System.out.println("线程名： " + entry.getKey().getName());
                System.out.println("线程ID: " + entry.getKey().getId());
                System.out.println("线程状态： " + entry.getKey().getState());
                String className = Thread.currentThread().getStackTrace()[2].getClassName();
                String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
                System.out.println("类名："+className);
                System.out.println("方法名："+methodName);
                System.out.println("线程堆栈跟踪： ");
                for (StackTraceElement element : entry.getValue()) {
                    System.out.println("\t" + element);
                }

                System.out.println("\t");

                entry.getKey().start();

            }
        }

        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackTraceElements = entry.getValue();
            for (StackTraceElement element : stackTraceElements) {
                String className = element.getClassName();
                String methodName = element.getMethodName();
                System.out.println("Thread " + thread.getName() + " called " + className + "." + methodName);
            }
            System.err.println("----------------------------");
        }
    }

    public static boolean filterStrings(String input) {
        List<String> stringList = new ArrayList<>();
        stringList.add("schedule-pool-\\d+");
        stringList.add("pool-\\d+-thread-\\d+");

        for (String string:stringList){
            Pattern pattern = Pattern.compile(string);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

}
