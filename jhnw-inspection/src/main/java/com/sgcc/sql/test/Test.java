package com.sgcc.sql.test;

import com.sgcc.share.util.MyUtils;

public class Test {
    public static void main(String[] args) {

        String str = "\r\n                 ^\r\n" +
                " % Unrecognized command found at '^' position.\r\n";

        if (str.indexOf("^")!=-1){
            String trim = str;
            String str1 = "\r\n";
            int length = str1.length();
            str = trim.substring(length,trim.length());
            System.err.println(str);
        }
    }
}
