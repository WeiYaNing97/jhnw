package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class ProblemScanLogicToJson {

    @Test
    public List<ProblemScanLogicCO> problemScanLogicCOList() {

        List<String> arryList = new ArrayList<>();
        arryList.add("{\"targetType\":\"match\",\"onlyIndex\":1697591408064,\"trueFalse\":\"成功\",\"checked\":false,\"matched\":\"精确匹配\",\"relativeTest\":\"present\",\"relativeType\":\"full\",\"matchContent\":\"local-user\",\"nextIndex\":1697591425120,\"relative\":\"present&full\",\"pageIndex\":2}");
        arryList.add("{\"targetType\":\"takeword\",\"onlyIndex\":1697591425120,\"trueFalse\":\"\",\"checked\":false,\"action\":\"取词\",\"position\":0,\"cursorRegion\":\"0\",\"relative\":\"0\",\"rPosition\":\"1\",\"length1\":\"1\",\"classify\":\"W\",\"exhibit\":\"显示\",\"wordName\":\"用户名\",\"nextIndex\":1697591450760,\"length\":\"1W\",\"pageIndex\":3,\"matchContent\":\"local-user\"}");
        arryList.add("{\"targetType\":\"match\",\"onlyIndex\":1697591450760,\"trueFalse\":\"成功\",\"checked\":false,\"matched\":\"精确匹配\",\"relativeTest\":\"1\",\"relativeType\":\"present\",\"matchContent\":\"simple\",\"nextIndex\":1697591497888,\"relative\":\"1&present\",\"pageIndex\":4}");
        arryList.add("{\"targetType\":\"takeword\",\"onlyIndex\":1697591497888,\"trueFalse\":\"\",\"checked\":false,\"action\":\"取词\",\"position\":0,\"cursorRegion\":\"0\",\"relative\":\"0\",\"rPosition\":\"1\",\"length1\":\"1\",\"classify\":\"W\",\"exhibit\":\"不显示\",\"wordName\":\"密码\",\"nextIndex\":1697591524136,\"length\":\"1W\",\"pageIndex\":5,\"matchContent\":\"simple\"}");
        arryList.add("{\"targetType\":\"analyse\",\"onlyIndex\":1697591524136,\"trueFalse\":\"成功\",\"checked\":false,\"action\":\"比较\",\"bi\":\"密码\",\"compare\":\"密码 == admin\",\"nextIndex\":1697591552473,\"pageIndex\":6}");
        arryList.add("{\"targetType\":\"prodes\",\"onlyIndex\":1697591552473,\"trueFalse\":\"\",\"checked\":false,\"action\":\"问题\",\"problemId\":\"\",\"tNextId\":\"有问题\",\"nextIndex\":1697591569665,\"pageIndex\":7}");
        arryList.add("{\"targetType\":\"wloop\",\"onlyIndex\":1697591569665,\"trueFalse\":\"\",\"checked\":false,\"action\":\"循环\",\"cycleStartId\":1697591408064,\"nextIndex\":1697591524136,\"pageIndex\":8}");
        arryList.add("{\"targetType\":\"analysefal\",\"onlyIndex\":1697591524136,\"trueFalse\":\"失败\",\"nextIndex\":1697591575057,\"pageIndex\":9}");
        arryList.add("{\"targetType\":\"prodes\",\"onlyIndex\":1697591575057,\"trueFalse\":\"\",\"checked\":false,\"action\":\"问题\",\"problemId\":\"\",\"tNextId\":\"无问题\",\"nextIndex\":1697591592449,\"pageIndex\":10}");
        arryList.add("{\"targetType\":\"wloop\",\"onlyIndex\":1697591592449,\"trueFalse\":\"\",\"checked\":false,\"action\":\"循环\",\"cycleStartId\":1697591408064,\"nextIndex\":1697591450760,\"pageIndex\":11}");
        arryList.add("{\"targetType\":\"matchfal\",\"onlyIndex\":1697591450760,\"trueFalse\":\"失败\",\"nextIndex\":1697591597713,\"pageIndex\":12}");
        arryList.add("{\"targetType\":\"prodes\",\"onlyIndex\":1697591597713,\"trueFalse\":\"\",\"checked\":false,\"action\":\"问题\",\"problemId\":\"\",\"tNextId\":\"无问题\",\"nextIndex\":1697591617633,\"pageIndex\":13}");
        arryList.add("{\"targetType\":\"wloop\",\"onlyIndex\":1697591617633,\"trueFalse\":\"\",\"checked\":false,\"action\":\"循环\",\"cycleStartId\":1697591408064,\"nextIndex\":1697591408064,\"pageIndex\":14}");
        arryList.add("{\"targetType\":\"matchfal\",\"onlyIndex\":1697591408064,\"trueFalse\":\"失败\",\"nextIndex\":1697591620186,\"pageIndex\":15}");
        arryList.add("{\"targetType\":\"prodes\",\"onlyIndex\":1697591620186,\"trueFalse\":\"\",\"checked\":false,\"action\":\"问题\",\"problemId\":\"\",\"tNextId\":\"完成\",\"pageIndex\":16}");

        List<ProblemScanLogicCO> problemScanLogicCOList = new ArrayList<>();
        for (String string:arryList){
            problemScanLogicCOList.add(JSON.parseObject(string, ProblemScanLogicCO.class));
        }

        return problemScanLogicCOList;
    }

}
