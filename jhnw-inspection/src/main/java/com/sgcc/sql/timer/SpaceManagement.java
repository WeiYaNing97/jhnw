package com.sgcc.sql.timer;

import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.domain.DatabaseUsage;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.service.*;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.system.service.ISwitchOperLogService;
import com.sgcc.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

/**
 * @program: jhnw
 * @description: 空间管理
 * @author:
 * @create: 2024-06-17 15:41
 **/
public class SpaceManagement extends TimerTask {

    @Autowired
    private static IAbnormalAlarmInformationService abnormalAlarmInformationService;
    @Autowired
    private static IReturnRecordService returnRecordService;
    @Autowired
    private static ISwitchScanResultService switchScanResultService;
    @Autowired
    private static ISwitchOperLogService switchOperLogService;

    @Autowired
    private static DatabaseUsageService databaseUsageService;


    @Override
    public void run() {
        // 获取数据保留时长(年)
        Object dataRetentionTime = (Object) CustomConfigurationUtil.getValue("configuration.spaceManagement.dataRetentionTime", Constant.getProfileInformation());

        Double dataRetentionTimeDouble = null;
        if (dataRetentionTime == null) {
            return;
        }else if (dataRetentionTime instanceof Double) {
            dataRetentionTimeDouble = (Double) dataRetentionTime;
        }else if (dataRetentionTime instanceof Integer) {
            Integer dataRetentionTimeInteger = (Integer) dataRetentionTime;
            dataRetentionTimeDouble = (double) dataRetentionTimeInteger;
        }

        // 调用硬盘管理方法，传入数据保留时长
        hardDiskManagement(dataRetentionTimeDouble);

        // 调用数据库管理方法，传入数据保留时长，并获取过期时间
        String expirationTime = databaseManagement(dataRetentionTimeDouble);

        // 根据过期时间删除数据
        Integer deleteDataBasedOnTime = deleteDataBasedOnTime(expirationTime);

        boolean obtainProportionOfMemoryUsage = obtainProportionOfMemoryUsage("D:\\");

        // 获取数据库使用率
        boolean obtainDatabaseUsageRatio = obtainDatabaseUsageRatio();
    }

    /**
     * 对硬盘进行管理，根据给定的数据保留时间删除指定目录下的过期文件和文件夹
     *
     * @param dataRetentionTime 数据保留时间，单位：年
     */
    public static void hardDiskManagement(Double dataRetentionTime) {

        // 从配置中获取日志路径
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());

        File directory = new File(logPath);
        // 获取该目录下的所有文件和文件夹
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // 判断是否为文件夹
                if (file.isDirectory()) {
                    // 打印文件夹名称
                    /*System.err.println("Folder: " + file.getName());*/
                    // 判断文件夹是否过期
                    if (isItTimeout(file.getName(),dataRetentionTime)) {
                        // 删除文件夹
                        deleteFolder(file);
                    }
                // 判断是否为文件
                } else if (file.isFile()) {
                    /** 文件不用删除，文件为运行分析日志，需要查看数据变化*/

                    getFileInformation(file.getPath() , dataRetentionTime );

                    // 打印文件名称
                    /*System.out.println("File: " + file.getName());*/
                    // 判断文件是否过期
                    /*if (isItTimeout(file.getName(),dataRetentionTime)) {
                        // 删除文件夹
                        deleteFolder(file);
                    }*/
                }
            }
        }else {
            // 如果目录下没有文件或文件夹，则输出提示信息
            /*System.out.println("No files found");*/
        }
    }

    /**
     * 删除文件夹及文件夹下所有文件
     *
     * @param folder 要删除的文件夹
     */
    public static void deleteFolder(File folder) {
        // 判断是否为文件夹
        if (folder.isDirectory()) {
            // 获取文件夹下的所有文件
            File[] files = folder.listFiles();
            // 如果文件不为空
            if (files != null) {
                // 遍历文件夹下的每个文件
                for (File file : files) {
                    // 递归调用删除文件夹方法，删除当前文件
                    deleteFolder(file);
                }
            }
        }
        // 删除文件夹本身
        folder.delete();
    }


    /**
     * 判断给定的日期字符串是否超过了
     *
     * @param dateStr 日期字符串，格式为"yyyy-MM-dd"
     * @return 如果超过半年返回true，否则返回false
     *   如果日期字符串格式不正确，则抛出DateTimeParseException异常
     */
    public static  boolean isItTimeout(String dateStr,Double dataRetentionTime) {
        // 创建一个日期格式化器，用于解析日期字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 将日期字符串解析为LocalDate对象
        LocalDate targetDate = LocalDate.parse(dateStr, formatter);
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 计算当前日期与目标日期之间的差
        long monthsBetween = ChronoUnit.MONTHS.between(targetDate,currentDate);
        // 判断月份差是否大于6，即是否超过半年
        boolean isMoreThanHalfYear = monthsBetween >= (dataRetentionTime * 12) ;
        // 返回判断结果
        return isMoreThanHalfYear;
    }


    /**
     * 获取指定硬盘分区的内存使用情况百分比，并判断是否超过设定的使用空间占比
     *
     * @param partitionPath 硬盘分区路径
     * @return 如果已使用空间占比超过配置文件设置已使用空间占比，返回true；否则返回false
     */
    public static boolean obtainProportionOfMemoryUsage(String partitionPath) {
        // 创建 File 对象
        File partition = new File(partitionPath);

        // 获取总空间
        long totalSpace = partition.getTotalSpace();
        // 获取可用空间
        long freeSpace = partition.getFreeSpace();
        // 计算已使用空间
        long usedSpace = totalSpace - freeSpace;

        // 计算已使用空间的百分比
        double usedSpacePercent = (double) usedSpace / totalSpace;
        /*System.out.println("已使用空间的百分比：" + usedSpacePercent);*/

        // 所用空间占比(%)
        int usedSpaceRateInt = (Integer) CustomConfigurationUtil.getValue("configuration.spaceManagement.usedSpaceRate", Constant.getProfileInformation());

        // 判断是否超过使用空间占比 已使用空间占比>配置文件设置已使用空间占比
        // 返回判断结果 如果超过使用空间占比 返回true
        // 否则返回false

        if (((int) usedSpacePercent * 100) > usedSpaceRateInt) {
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, null, "问题日志",
                    "异常：硬盘已使用空间占比大于配置文件设置的使用空间占比\r\n");
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取数据库使用率
     */
    public static boolean obtainDatabaseUsageRatio() {
        // 获取配置参数
        Map<String, Object> stringObjectMap = new SpaceManagement().ObtainAllConfigurationFileParameters();

        // 从配置参数中获取已使用的空间率
        String usedSpaceRate = (String) CustomConfigurationUtil.getValue("spring.datasource.druid.master.url",stringObjectMap);

        // 根据"/"分割字符串，获取数据库名
        String[] split = usedSpaceRate.split("/");
        String databaseName = split[split.length - 1].split("\\?")[0];

        // 调用方法获取数据库使用率
        DatabaseUsage databaseUsage = getDatabaseUsage(databaseName);

        // 所用空间占比(%)
        Integer usedSpaceRateInt = (Integer) CustomConfigurationUtil.getValue("configuration.spaceManagement.usedSpaceRate", Constant.getProfileInformation());

        // 输出数据库使用率
        /*System.err.println("数据库使用率：" + databaseUsage.getUsedSpacePercent());*/
        if (databaseUsage.getUsedSpacePercent() > usedSpaceRateInt) {
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, null, "问题日志",
                    "异常：数据库已使用空间占比大于配置文件设置的使用空间占比\r\n");
            return false;
        }else {
            return true;
        }
    }

    /**
     * 从配置文件中获取所有参数，返回参数映射表usedSpace = 6.578125
     *
     * @return 包含所有参数的映射表，其中键为参数名称，值为参数值
     * @throws IOException 如果关闭输入流时发生I/O异常
     */
    public Map<String, Object> ObtainAllConfigurationFileParameters() {
        // 定义配置文件路径
        String path3 = "/application-druid.yml";
        // 获取配置文件输入流
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        // 创建Yaml对象
        Yaml yaml = new Yaml();
        // 使用Yaml对象加载配置文件内容，并返回参数映射表
        Map<String, Object>  ProfileInformation = yaml.load(inputStream);

        /*关闭IO流*/
        try {
            // 关闭输入流
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ProfileInformation;
    }

    /**
     * 获取指定数据库的使用情况
     *
     * @param databaseName 数据库名
     * @return 返回指定数据库的使用情况实体对象
     */
    public static DatabaseUsage getDatabaseUsage(String databaseName) {
        // 获取 DatabaseUsageService 的实例
        databaseUsageService = SpringBeanUtil.getBean(DatabaseUsageService.class);

        // 调用 DatabaseUsageService 的 getDatabaseUsage 方法获取数据库使用情况
        return databaseUsageService.getDatabaseUsage(databaseName);
    }

    /**
     * 对数据库进行管理，返回指定数据保留时间前的时间
     *
     * @param dataRetentionTime 数据保留时间，单位：年
     * @return 返回指定数据保留时间前的时间的字符串形式
     */
    public static String databaseManagement(Double dataRetentionTime) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 计算指定数据保留时间前的日期
        // 获取当前日期减去指定天数后的日期
        LocalDate nYearsAgo = currentDate.minusDays( (long)(dataRetentionTime * 365));

        // System.out.println(dataRetentionTime + "年前的时间：" + nYearsAgo);
        // 返回指定数据保留时间前的时间的字符串形式
        return nYearsAgo.toString();
    }

    /**
     * 根据时间删除数据
     *
     * @param data 时间数据
     * @return 无返回值
     */
    public static Integer deleteDataBasedOnTime(String data) {

        // 获取返回记录服务
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        // 获取交换机扫描结果服务
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        // 获取异常报警信息服务
        abnormalAlarmInformationService = SpringBeanUtil.getBean(IAbnormalAlarmInformationService.class);
        // 获取交换机操作日志服务
        switchOperLogService = SpringBeanUtil.getBean(ISwitchOperLogService.class);
        // 获取 DatabaseUsageService 的实例
        databaseUsageService = SpringBeanUtil.getBean(DatabaseUsageService.class);

        // 根据时间删除返回记录
        int deleteReturnRecord = returnRecordService.deleteReturnRecordByTime(data);
        if (deleteReturnRecord < 0) {
            AbnormalAlarmInformationMethod.afferent(null, null, "问题日志",
                    "异常：删除返回记录失败");
            return deleteReturnRecord;
        }

        // 根据时间删除异常报警信息
        int deleteAbnormalAlarmInformation = abnormalAlarmInformationService.deleteAbnormalAlarmInformationByTime(data);
        if (deleteAbnormalAlarmInformation < 0) {
            AbnormalAlarmInformationMethod.afferent(null, null, "问题日志",
                    "异常：删除异常报警信息失败");
            return deleteAbnormalAlarmInformation;
        }

        // 根据时间删除交换机操作日志
        int deleteSwitchOperLog = switchOperLogService.deleteSwitchOperLogByTime(data);
        if (deleteSwitchOperLog < 0) {
            AbnormalAlarmInformationMethod.afferent(null, null, "问题日志",
                    "异常：删除交换机操作日志失败");
            return deleteSwitchOperLog;
        }

        // 根据时间删除交换机扫描结果
        int deleteSwitchScanResult = switchScanResultService.deleteSwitchScanResultByTime(data);
        if (deleteSwitchScanResult < 0) {
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, null, "问题日志",
                    "异常：删除交换机扫描结果失败");
            return deleteSwitchScanResult;
        }
        /* 根据时间删除系统操作日志 */
        int deleteSysOperLog = databaseUsageService.deleteSysOperLogByTime(data);
        if (deleteSysOperLog < 0) {
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, null, "问题日志",
                    "异常：删除系统操作日志失败");

            return deleteSysOperLog;
        }

        return 1;
    }


    /**
     * 根据给定的时间获取文件信息，保留过期时间之后的日志并保存到原文件。
     *
     * @param time 字符串类型，表示过期时间，格式为"yyyy-MM-dd HH:mm:ss"
     * @throws IOException 如果在读取或写入文件时发生错误
     */
    public static void getFileInformation(String logFilePath,Double dataRetentionTime) {


        // 获取当前日期和时间，考虑系统默认时区
        ZonedDateTime now = ZonedDateTime.now();
        // N个月前的日期和时间
        int monthsBefore = (int) (dataRetentionTime * 12) ; // 例如，获取3个月之前的日期和时间
        ZonedDateTime dateBefore = now.minusMonths(monthsBefore);

        // 如果你想以某个特定时区来表示，可以这样设置：
        // ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        // ZonedDateTime dateBeforeInShanghai = now.withZoneSameInstant(zoneId).minusMonths(monthsBefore);

        // 格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateBefore.format(formatter);
        /*System.out.println("三个月前的日期和时间是: " + formattedDateTime);*/


        // 过期时间戳
        long expirationTimeInMillis = calculateExpirationTime(formattedDateTime); // 过期时间戳

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter("temp_log.log", false))) {

            String line;
            boolean isExpirationTime = false;
            // 遍历日志文件，保留过期时间之后的日志
            while ((line = reader.readLine()) != null) {
                // 解析每行的日志时间戳
                //long logTimeInMillis = parseLogTimestamp(line);

                // 如果日志时间戳大于过期时间，保留该日志
                if (isExpirationTime || parseLogTimestamp(line) > expirationTimeInMillis) {
                    /*System.err.println(line);*/
                    isExpirationTime = true;
                    writer.write(line);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 删除原文件并重命名临时文件
        try {
            Files.delete(Paths.get(logFilePath));
            Files.move(Paths.get("temp_log.log"), Paths.get(logFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算过期时间的时间戳
     *
     * @param expireTimeString 过期时间的字符串表示，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 返回过期时间的时间戳（毫秒）
     * @throws RuntimeException 如果解析过期时间字符串失败，则抛出此异常
     */
    private static long calculateExpirationTime(String expireTimeString) {
        try {
            // 创建一个SimpleDateFormat对象，指定日期时间格式为"yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 使用SimpleDateFormat对象的parse方法将字符串解析为Date对象
            Date expireDate = sdf.parse(expireTimeString);
            // 调用Date对象的getTime方法获取时间戳（毫秒）
            return expireDate.getTime();
        } catch (Exception e) {
            // 解析过期时间字符串失败时，抛出RuntimeException异常，并附带异常信息"Failed to parse expiration time"
            throw new RuntimeException("Failed to parse expiration time", e);
        }
    }

    /**
     * 将日志行中的时间戳字符串解析为长整型时间戳。
     *
     * @param logLine 日志行字符串，其中时间戳应包含在 [ ] 之间
     * @return 解析得到的长整型时间戳
     * @throws RuntimeException 如果解析日志时间戳失败，则抛出此异常
     */
    private static long parseLogTimestamp(String logLine) {
        try {
            // 提取时间戳字符串
            // 假设日志时间格式固定为 [yyyy-MM-dd HH:mm:ss]
            String timestampStr = logLine.substring(logLine.indexOf('[') + 1, logLine.indexOf(']'));
            // 创建SimpleDateFormat对象，设置时间格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 将时间戳字符串解析为Date对象
            Date logDate = sdf.parse(timestampStr);
            // 将Date对象转换为长整型时间戳
            return logDate.getTime();
        } catch (Exception e) {
            // 解析失败时抛出RuntimeException异常
            throw new RuntimeException("Failed to parse log timestamp", e);
        }
    }

}
