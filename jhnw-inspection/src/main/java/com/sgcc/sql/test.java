package com.sgcc.sql;

import com.alibaba.fastjson.JSON;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.sql.domain.AnalyzeConvertJson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2024-01-05 17:11
 **/
public class test {
    public static void main(String[] args) {

        String string = "{\"onlyIndex\":\"1697591400802\",\"trueFalse\":\"\",\"pageIndex\":1,\"command\":\"dis cu\",\"para\":\"\",\"resultCheckId\":\"0\",\"nextIndex\":\"1697591408064\",\"targetType\":\"command\"}\n" +
                "{\"onlyIndex\":\"1697591408064\",\"trueFalse\":\"成功\",\"matched\":\"精确匹配\",\"relative\":\"present&full\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"local-user\",\"action\":\"null\",\"tNextId\":\"null\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591425120\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":2,\"targetType\":\"match\",\"relativeTest\":\"present\",\"relativeType\":\"full\"}\n" +
                "{\"onlyIndex\":\"1697591425120\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"local-user\",\"action\":\"取词\",\"tNextId\":\"null\",\"rPosition\":\"1\",\"length\":\"1W\",\"exhibit\":\"显示\",\"wordName\":\"用户名\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591450760\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":3,\"targetType\":\"takeword\",\"classify\":\"W\",\"length1\":\"1\"}\n" +
                "{\"onlyIndex\":\"1697591450760\",\"trueFalse\":\"成功\",\"matched\":\"精确匹配\",\"relative\":\"1&present\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"simple\",\"action\":\"null\",\"tNextId\":\"null\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591497888\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":4,\"targetType\":\"match\",\"relativeTest\":\"1\",\"relativeType\":\"present\"}\n" +
                "{\"onlyIndex\":\"1697591497888\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"simple\",\"action\":\"取词\",\"tNextId\":\"null\",\"rPosition\":\"1\",\"length\":\"1W\",\"exhibit\":\"不显示\",\"wordName\":\"密码\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591524136\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":5,\"targetType\":\"takeword\",\"classify\":\"W\",\"length1\":\"1\"}\n" +
                "{\"onlyIndex\":\"1697591524136\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"比较\",\"tNextId\":\"null\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"密码 == admin\",\"content\":\"null\",\"nextIndex\":\"1697591552473\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":6,\"targetType\":\"analyse\"}\n" +
                "{\"onlyIndex\":\"1697591552473\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"问题\",\"tNextId\":\"有问题\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591569665\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":7,\"targetType\":\"prodes\"}\n" +
                "{\"onlyIndex\":\"1697591569665\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"循环\",\"tNextId\":\"null\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591524136\",\"problemId\":\"null\",\"cycleStartId\":\"1697591408064\",\"pageIndex\":8,\"targetType\":\"wloop\"}\n" +
                "{\"onlyIndex\":\"1697591524136\",\"trueFalse\":\"失败\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"null\",\"tNextId\":\"null\",\"rPosition\":\"null\",\"length\":\"null\",\"exhibit\":\"null\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591575057\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":9,\"targetType\":\"failedB\"}\n" +
                "{\"onlyIndex\":\"1697591575057\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"问题\",\"tNextId\":\"无问题\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591592449\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":10,\"targetType\":\"prodes\"}\n" +
                "{\"onlyIndex\":\"1697591592449\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"循环\",\"tNextId\":\"null\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591450760\",\"problemId\":\"null\",\"cycleStartId\":\"1697591408064\",\"pageIndex\":11,\"targetType\":\"wloop\"}\n" +
                "{\"onlyIndex\":\"1697591450760\",\"trueFalse\":\"失败\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"null\",\"tNextId\":\"null\",\"rPosition\":\"null\",\"length\":\"null\",\"exhibit\":\"null\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591597713\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":12,\"targetType\":\"failed\"}\n" +
                "{\"onlyIndex\":\"1697591597713\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"问题\",\"tNextId\":\"无问题\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591617633\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":13,\"targetType\":\"prodes\"}\n" +
                "{\"onlyIndex\":\"1697591617633\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"循环\",\"tNextId\":\"null\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591408064\",\"problemId\":\"null\",\"cycleStartId\":\"1697591408064\",\"pageIndex\":14,\"targetType\":\"wloop\"}\n" +
                "{\"onlyIndex\":\"1697591408064\",\"trueFalse\":\"失败\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"null\",\"tNextId\":\"null\",\"rPosition\":\"null\",\"length\":\"null\",\"exhibit\":\"null\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"1697591620186\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":15,\"targetType\":\"failed\"}\n" +
                "{\"onlyIndex\":\"1697591620186\",\"trueFalse\":\"\",\"matched\":\"null\",\"relative\":\"0\",\"position\":\"0\",\"cursorRegion\":\"0\",\"matchContent\":\"null\",\"action\":\"问题\",\"tNextId\":\"完成\",\"rPosition\":\"0\",\"length\":\"0\",\"exhibit\":\"不显示\",\"wordName\":\"null\",\"compare\":\"null\",\"content\":\"null\",\"nextIndex\":\"null\",\"problemId\":\"null\",\"cycleStartId\":\"null\",\"pageIndex\":16,\"targetType\":\"prodes\"}\n";

        String[] split = string.split("\n");


    }
}
