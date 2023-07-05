package com.sgcc.sql.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Font;

public class Test {
    public static void main(String[] args) {
        String str = "0123456789";
        int i = str.indexOf("01");
        System.err.println(i);
        String substring = str.substring(i + "01".length() , str.length()-1);
        System.err.println(substring);
        int j = str.indexOf("8");
        String substringj = str.substring(i , j);
        System.err.println(substringj);
    }
}
