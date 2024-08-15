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
        Map<String, String> map = new HashMap<>();
        Set<String> strings = map.keySet();
        for (String string : strings) {
            System.out.println(string);
        }

    }
}