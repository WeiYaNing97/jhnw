package com.sgcc.sql.test;

import com.sgcc.sql.domain.Constant;
import com.sgcc.sql.util.CustomConfigurationUtil;

import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> deviceVersion = (List<String>) CustomConfigurationUtil.getValue("abc.def", Constant.getProfileInformation());
        for (String string:deviceVersion){
            System.err.println(string);
        }
    }
}
