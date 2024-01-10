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
        List<String> listA = new ArrayList<>();
        listA.add("a");
        listA.add("b");
        listA.add("c");

        List<String> listB = new ArrayList<>();
        listB.add("b");
        listB.add("c");
        listB.add("d");

        List<String> result = findDifference(listA, listB);
        System.out.println(result);
    }

    public static List<String> findDifference(List<String> listA, List<String> listB) {
        HashSet<String> setA = new HashSet<>(listA);
        HashSet<String> setB = new HashSet<>(listB);

        setA.removeAll(setB);

        return new ArrayList<>(setA);
    }
}
