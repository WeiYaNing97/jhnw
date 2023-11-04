package com.sgcc.share.util;
import com.sgcc.share.domain.Constant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class PathHelper {
    /* todo  日志路径*/
    static String logPath = "D:\\jhnwadminlog" ;//Configuration.logPath;
    static String logTime = MyUtils.getDate("yyyy-MM-dd");
    /**
     * 将字符串写入文件中
     * @param str
     * @throws IOException
     */
    public static void writeDataToFile(String str) throws IOException {
        String logPresent = MyUtils.getDate("yyyy-MM-dd HH:mm:ss");
        logPresent = "["+logPresent+"] ";
        str = logPresent + str ;

        /* 获取 设备是 路由器的标志*/
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());
        String path = "";
        if (logPath != null && !logPath.equals("")){
            path = logPath+  "\\" + logTime + "\\" +logTime +"log.txt";
        }else {
            path = logPath+  "\\" + logTime + "\\" +logTime +"log.txt";
        }

        //文件目录
        File writefile = new File(path);
        if (writefile.exists() == false)   // 判断文件是否存在，不存在则生成
        {
            try {
                // 获取文件所在的文件夹路径
                String folderPath = writefile.getParent();
                // 创建文件夹
                File folder = new File(folderPath);
                folder.mkdirs();

                writefile.createNewFile();
                writefile = new File(path);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bw;
        try {
            boolean append = true;  //  是否追加
            FileWriter fw = new FileWriter(writefile, append);
            bw = new BufferedWriter(fw);
            fw.write(str);
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将字符串写入文件中
     * @param str
     * @throws IOException
     */
    public static void writeDataToFileByName(String str,String name) throws IOException {
        String logPresent = MyUtils.getDate("yyyy-MM-dd HH:mm:ss");
        logPresent = "["+logPresent+"] ";
        str = logPresent + str ;
        String path = "";
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());
        if (logPath != null && !logPath.equals("")){
            path = logPath+  "\\"  + logTime +  "\\" +logTime +"log"+name+".txt";
        }else {
            path = logPath+  "\\"  + logTime +  "\\" +logTime +"log"+name+".txt";
        }
        //文件目录
        File writefile = new File(path);
        if (writefile.exists() == false)   // 判断文件是否存在，不存在则生成
        {
            try {
                // 获取文件所在的文件夹路径
                String folderPath = writefile.getParent();
                // 创建文件夹
                File folder = new File(folderPath);
                folder.mkdirs();

                writefile.createNewFile();
                writefile = new File(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bw;
        try {
            boolean append = true;  //  是否追加
            FileWriter fw = new FileWriter(writefile, append);
            bw = new BufferedWriter(fw);
            fw.write(str);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
