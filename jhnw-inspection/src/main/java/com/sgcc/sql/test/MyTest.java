package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.junit.Test;
import org.springframework.scheduling.annotation.Scheduled;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import javax.lang.model.element.NestingKind;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class MyTest {
    public static void main(String[] args) {
        String list = "poo l-16-thread-1";
        boolean b = filterStrings(list);
        System.err.println(b);
    }

    public static boolean filterStrings(String input) {
        Pattern pattern = Pattern.compile("pool-\\d+-thread-\\d+");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
