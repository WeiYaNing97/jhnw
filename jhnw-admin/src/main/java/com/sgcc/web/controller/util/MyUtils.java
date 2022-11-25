package com.sgcc.web.controller.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年08月02日 14:42
 */
public class MyUtils {


    /**
     * @method: 多个连续空格 改为 多个单空格
     * @Param: [original]
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String repaceWhiteSapce(String original){
        StringBuilder sb = new StringBuilder();
        boolean isFirstSpace = false;//标记是否是第一个空格
        // original = original.trim();//如果考虑开头和结尾有空格的情形
        char c;
        for(int i = 0; i < original.length(); i++){
            c = original.charAt(i);
            if(c == ' ' || c == '\t')//遇到空格字符时,先判断是不是第一个空格字符
            {
                if(!isFirstSpace)
                {
                    sb.append(c);
                    isFirstSpace = true;
                }
            }
            else{//遇到非空格字符时
                sb.append(c);
                isFirstSpace = false;
            }
        }
        return sb.toString();
    }

    /*
    Java时间设为二十四小时制和十二小时制的区别：
    1) 二十四小时制： “yyyy-MM-dd HH:mm:ss”
    2)十二小时制： “"yyyy-MM-dd hh:mm:ss"”
    */
    //获取时间 格式自定
    //例如：type  =  "yyyy-MM-dd hh:mm:ss"
    public static String getDate(String type){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        String time = simpleDateFormat.format(new Date());
        return time;
    }

    public static Date getStringtoData(String time){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = null;
        try {
            parse = format.parse(time);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    //时间排序 由大到小
    public static List<Date> sortDate(List<Date> dateList){
        dateList.sort((a1,a2) ->{
            return a1.compareTo(a2);
        });
        return dateList;
    }

    public static void main(String[] args) {
       String str = "2022-08-02 02:52:34";
        Date stringtoData = getStringtoData(str);
        System.err.println(stringtoData);
    }
}