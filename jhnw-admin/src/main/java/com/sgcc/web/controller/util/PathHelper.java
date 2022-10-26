package com.sgcc.web.controller.util;


import com.sgcc.web.controller.sql.Configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PathHelper {
    static String logPath = "D:\\IdeaProjects\\github\\beifen\\jhnwadminlog" ;//Configuration.logPath;
    static String logTime = MyUtils.getDate("yyyy-MM-dd");

    /**
     * 将字符串写入文件中
     * @param str
     * @throws IOException
     */
    public static void writeDataToFile(String str) throws IOException {
        //文件目录
        File writefile;
        BufferedWriter bw;
        boolean append = true;  //  是否追加
        String path = logPath + "\\" +logTime +"log.txt";
        writefile = new File(path);
        if (writefile.exists() == false)   // 判断文件是否存在，不存在则生成
        {
            try {
                writefile.createNewFile();
                writefile = new File(path);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        /*else {        // 存在先删除，再创建
            writefile.delete();
            try {
                writefile.createNewFile();
                writefile = new File(path);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }*/
        try {
            FileWriter fw = new FileWriter(writefile, append);
            bw = new BufferedWriter(fw);
            fw.write(str);
            fw.flush();
            fw.close();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

}
