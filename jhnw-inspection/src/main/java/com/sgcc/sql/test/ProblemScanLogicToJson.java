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


    public static void main(String[] args) {

        String string1 = "5.20";
        String string2 = "5.20.99";
        String string3 = "5.20.99";
        String string4 = "5.020.99";
        String string5 = "5.21";

        String string6 = "29";
        String string7 = "029";

        int i = compareByName(string1, string2);
        int i1 = compareByName(string2, string3);
        int i2 = compareByName(string3, string4);
        int i3 = compareByName(string4, string5);
        int i4 = compareByName(string6, string7);
        /*System.err.println(i);
        System.err.println(i1);
        System.err.println(i2);
        System.err.println(i3);*/
        System.err.println(i4);

    }

    private static int compareByName(String name1, String name2) {
        final byte[] bytes1 = name1.getBytes();
        final byte[] bytes2 = name2.getBytes();
        int i = 0;
        byte b1, b2;
        int numCompare = 0;//如果都是数字, 那么需要比较连续数字的大小, 只要高位大, 这个数字就大
        for (; i < Math.min(bytes1.length, bytes2.length); i++) {
            b1 = bytes1[i];
            b2 = bytes2[i];
            if (b1 != b2) {//只有ascii不相等时才比较
                if (numCompare != 0
                        && !(b1 >= 48 && b1 <= 57)
                        && !(b2 >= 48 && b2 <= 57)) {//已经出现过不等的数字，并且这个循环都是字符的情况
                    return numCompare;
                }
                if (b1 >= 48 && b1 <= 57
                        && b2 >= 48 && b2 <= 57) {//只有都是数字才会进入
                    if (numCompare == 0)
                        numCompare = Byte.compare(b1, b2);
                } else {//其中一个是数字，或者都是字符
                    if (numCompare != 0) {//已经出现过数字，那么本次循环哪个是数字，说明哪个数字位数多，那么这个数字就大
                        if (b1 >= 48 && b1 <= 57)
                            return 1;
                        if (b2 >= 48 && b2 <= 57)
                            return -1;
                    }
                    return Byte.compare(b1, b2);
                }
            }
        }
        if (numCompare == 0)//说明长度较小的部分完全一样，比较哪个长度大
            return Integer.compare(bytes1.length, bytes2.length);
        else {
            if (bytes1.length > bytes2.length) {
                if (bytes1[i] >= 48 && bytes1[i] <= 57)
                    return 1;
                else
                    return numCompare;
            }
            if (bytes1.length < bytes2.length) {
                if (bytes2[i] >= 48 && bytes2[i] <= 57)
                    return -1;
                else
                    return numCompare;
            }
            return numCompare;//出现过数字且不相同，并且最后一位不是字符
        }
    }

}
