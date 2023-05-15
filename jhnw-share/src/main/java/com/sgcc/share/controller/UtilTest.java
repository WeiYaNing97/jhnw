package com.sgcc.share.controller;

import com.sgcc.share.util.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class UtilTest {
    public static void main(String[] args) {
        List<String> totalQuestionTableId = new ArrayList<>();
        totalQuestionTableId.add("123456789");
        totalQuestionTableId.add("fsdvxczcvzxcv");
        totalQuestionTableId.add("456");
        totalQuestionTableId.add("vsdzxczvs");
        totalQuestionTableId.add("789");
        totalQuestionTableId.add("741852963");
        totalQuestionTableId.add("852963741");
        totalQuestionTableId.add("963741852");
        List<Long> idScan = new ArrayList<>();
        List<String> advancedName = new ArrayList<>();
        for (String id:totalQuestionTableId){
            if (MyUtils.allIsNumeric(id)){
                idScan.add(Long.valueOf(id).longValue());
            }else {
                advancedName.add(id);
            }
        }
        idScan.stream().forEach(x -> System.out.println("Long:"+x));

        System.out.println("===============");

        advancedName.stream().forEach(x -> System.out.println("String:"+x));
    }
}
