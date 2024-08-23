package com.sgcc.advanced.test;

import java.util.*;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2023-12-29 11:37
 **/

public class Test {
    public static void main(String[] args) {
        List<Integer> substringPositions = getSubstringPositions("123456789123123", "123");
        System.err.println();
    }

    public static List<Integer> getSubstringPositions(String parent, String child) {
        // 创建一个存储位置的列表
        List<Integer> positions = new ArrayList<>();
        // 查找子字符串在父字符串中首次出现的位置
        int index = parent.indexOf(child);
        // 当子字符串在父字符串中存在时
        while (index != -1) {
            // 将子字符串出现的位置添加到列表中
            positions.add(index);
            // 从上一个出现位置的后一个字符开始，继续查找子字符串的下一个出现位置
            index = parent.indexOf(child, index + 1);
        }
        // 返回子字符串在父字符串中出现的所有位置列表
        return positions;
    }
}