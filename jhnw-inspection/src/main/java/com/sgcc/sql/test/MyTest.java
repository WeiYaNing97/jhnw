package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import org.junit.Test;

import javax.lang.model.element.NestingKind;
import java.util.*;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class MyTest {

    public static int compare(String str1, String str2) {
        int minLength = Math.min(str1.length(), str2.length());
        for(int i = 0; i < minLength; i++) {
            char char1 = str1.charAt(i);
            char char2 = str2.charAt(i);
            if(char1 < char2) {
                return -1;
            } else if(char1 > char2) {
                return 1;
            }
        }
        if(str1.length() < str2.length()) {
            return -1;
        } else if(str1.length() > str2.length()) {
            return 1;
        } else {
            return 0;
        }
    }
    public static void main(String[] args) {
        String str1 = "1234";
        String str2 = "234";
        int result = compare(str1, str2);
        System.out.println(result);
    }

}
