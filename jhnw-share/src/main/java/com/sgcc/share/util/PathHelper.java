package com.sgcc.share.util;
import com.sgcc.share.domain.Constant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PathHelper {
    /*日志路径*/
    public static String logPath = "D:\\jhnwadminlog" ;//Configuration.logPath;
    public static String logTime = MyUtils.getDate("yyyy-MM-dd");

    private final Object lock = new Object();
    /**
     * 将字符串写入文件中
     * @param str
     * @throws IOException
     */
    public void writeDataToFile(String str) throws IOException {
        // 获取当前时间并格式化为指定格式的字符串
        String logPresent = MyUtils.getDate("yyyy-MM-dd HH:mm:ss");
        // 添加时间前缀
        logPresent = "["+logPresent+"] ";
        // 拼接时间前缀和原始字符串
        str = logPresent + str ;

        /* 获取 设备是 路由器的标志*/
        // 获取日志路径配置值
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());
        String path = "";
        if (logPath != null && !logPath.equals("")){
            // 如果日志路径不为空，则拼接完整路径
            path = logPath+  "\\" + logTime + "\\" +logTime +"log.txt";
        }else {
            // 如果日志路径为空，则使用默认路径
            path = logPath+  "\\" + logTime + "\\" +logTime +"log.txt";
        }

        // 创建文件对象
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

                // 创建新文件
                writefile.createNewFile();
                // 重新指定文件对象
                writefile = new File(path);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bw;
        synchronized (lock){
            boolean append = true;  //  是否追加
            // 创建文件写入流
            FileWriter fw = new FileWriter(writefile, append);
            try {
                // 创建缓冲写入流
                bw = new BufferedWriter(fw);
                // 写入字符串到文件
                fw.write(str);
                // 刷新缓冲区
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 关闭文件写入流
                fw.close();
            }
        }
    }
    /**
     * 将字符串写入文件中
     * @param name 文件名
     * @param str 要写入的字符串
     * @throws IOException
     */
    public void writeDataToFileByName(String name,String str) throws IOException {
        // 获取当前时间并格式化
        String logPresent = MyUtils.getDate("yyyy-MM-dd HH:mm:ss");
        logPresent = "["+logPresent+"] ";
        // 添加时间戳到字符串开头
        str = logPresent + str ;

        String path = "";
        // 从配置中获取日志路径
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());
        if (logPath != null && !logPath.equals("")){
            // 如果日志路径不为空，则拼接文件路径
            path = logPath + "\\" + logTime + "\\" + logTime +"log" + name + ".txt";
        } else {
            // 如果日志路径为空，则使用默认路径
            path = logPath + "\\" + logTime + "\\" + logTime +"log" + name + ".txt";
        }

        // 创建文件对象
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

                // 创建文件
                writefile.createNewFile();
                // 重新获取文件对象
                writefile = new File(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter bw;
        synchronized (lock){
            boolean append = true;  // 是否追加到文件末尾
            // 创建FileWriter对象，并设置是否追加
            FileWriter fw = new FileWriter(writefile, append);
            try {
                // 创建BufferedWriter对象，用于缓冲写入操作
                bw = new BufferedWriter(fw);
                // 写入字符串到文件
                fw.write(str);
                // 刷新缓冲区，确保数据写入文件
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 关闭流
                fw.close();
            }
        }
    }

    /**
     * 将字符串写入文件中
     * @param name 文件名
     * @param str 要写入的字符串
     * @throws IOException
     */
    public void writeDataToFileByAdvancedFeatureName(String name,String str) throws IOException {
        // 获取当前时间并格式化
        String logPresent = MyUtils.getDate("yyyy-MM-dd HH:mm:ss");
        logPresent = "["+logPresent+"] ";
        // 添加时间戳到字符串开头
        str = logPresent + str ;

        String path = "";
        // 从配置中获取日志路径
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());
        if (logPath != null && !logPath.equals("")){
            // 如果日志路径不为空，则拼接文件路径
            path = logPath + "\\" + name + ".txt";
        } else {
            // 如果日志路径为空，则使用默认路径
            path = this.logPath + "\\" + name + ".txt";
        }

        // 创建文件对象
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

                // 创建文件
                writefile.createNewFile();
                // 重新获取文件对象
                writefile = new File(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter bw;
        synchronized (lock){
            boolean append = true;  // 是否追加到文件末尾
            // 创建FileWriter对象，并设置是否追加
            FileWriter fw = new FileWriter(writefile, append);
            try {
                // 创建BufferedWriter对象，用于缓冲写入操作
                bw = new BufferedWriter(fw);
                // 写入字符串到文件
                fw.write(str);
                // 刷新缓冲区，确保数据写入文件
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 关闭流
                fw.close();
            }
        }
    }

    /**
    * @Description 读取文本内容
    * @author charles
    * @createTime 2024/1/19 14:43
    * @desc
    * @param path 文件路径
     * @return 文本内容的列表
    */
    public static List<String> ReadFileContent(String path) {
        // 创建一个ArrayList用于存储读取到的文本内容
        List<String> lines = new ArrayList<>();
        try (
                FileReader fileReader = new FileReader(path);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            // 循环读取文件中的每一行内容
            while ((line = bufferedReader.readLine()) != null) {
                // 将读取到的每一行内容添加到列表中
                lines.add(line);
            }
        } catch (IOException e) {
            // 获取异常类型
            String exceptionType = e.getClass().getSimpleName();
            if (exceptionType.equals("FileNotFoundException")) {
                System.out.println("文件不存在！");
            } else {
                // 捕获IO异常，并打印堆栈信息和错误信息
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        // 返回存储文本内容的列表
        return lines;
    }
}
