package com.sgcc.sql.test;

import com.sgcc.sql.domain.Constant;
import com.sgcc.sql.util.CustomConfigurationUtil;

import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String[][] strings = new String[5][6];
        for (int i=0 ;i<5;i++){
            for (int j =0;j<6;j++){
                strings[i][j] = i+j+"";
            }
        }

        for (int i=0 ;i<5;i++){
            for (int j =0;j<6;j++){
                System.err.print("["+i +","+ j +"]=" +strings[i][j]+ " ");
            }
            System.err.println();
        }
    }
}
