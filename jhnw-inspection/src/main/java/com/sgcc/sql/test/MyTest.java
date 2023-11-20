package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import org.junit.Test;

import javax.lang.model.element.NestingKind;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class MyTest {

    public static void main(String[] args) {
        int[] array = new int[5];
        array[1] = 1;
        array[2] = 2;
        array[3] = 3;
        array[4] = 4;
        array[0] = 1;

        int i = removeElement(array, 1);

    }
    public static int removeElement(int[] nums, int val) {
        int newLength = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[newLength] = nums[i];
                newLength++;
            }
        }
        return newLength;
    }

}
