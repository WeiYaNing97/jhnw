package com.sgcc.connect.method;

import com.sgcc.connect.util.SshConnect;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年09月29日 14:55
 */
@ResponseBody
@RestController
@RequestMapping("ConnectMethod")
public class SshMethod {

    //ip地址
    private String hostIp;
    //姓名
    private String userName;
    //密码
    private String userPassword;
    //返回信息
    private String ReturnInformation;
    private String EndIdentifier = ">";
    private boolean quit=false;
    public static SshConnect sshConnect;


    /***
    * @method: 连接telnet 或者 ssh
    * @Param: [mode 连接方法, ip, port, name, password, end:telnet结尾标识符]
    * @return: com.sgcc.jhnw.util.Result
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("requestConnect")
    public boolean requestConnect(String ip, int port, String name, String password){
        this.hostIp = ip;
        this.userName = name;
        this.userPassword = password;
        this.ReturnInformation = "";
        //创建连接 ip 端口号：22
        sshConnect = new SshConnect(hostIp, port, null, null);
        //用户名、密码
        String[] cmds = { userName, userPassword};
        //连接方法
        boolean login = sshConnect.login(cmds);
        return login;
    }

    /***
    * @method: 发送命令
    * @Param: [command]
    * @return: com.sgcc.jhnw.util.Result
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("sendCommand")
    public String sendCommand(String command,String notFinished){
        //ssh发送命令
        //将命令放到数组中，满足方法的参数要求
        String[] cmds = {command};

        if (this.ReturnInformation.endsWith(EndIdentifier) && command.equalsIgnoreCase("quit")) {
            quit = true;
        }
        //发送命令
        String string = sshConnect.batchCommand(cmds, notFinished,null,quit);
        if (string.indexOf("遗失对主机的连接")!=-1){
            return string;
        }
        if (string.substring(0, 1).equals("\n")){
            //字符串删除子字符串
            string = trimStr(string, string.substring(0, 1));
        }
        //高亮首乱码
        String substring = string.substring(0, 1);
        String strStar = substring+"[";

        String[] split = string.split("\n");
        int length = split[0].length();
        string = string.substring(length,string.length());

        //如果返回字符串包含 高亮首乱码,是windows返回字符串
        if(string.contains(strStar)){
            //创建一个新的字符串来存储返回信息
            String temporary = string;
            //循环判断是否还有 高亮首乱码
            while (temporary.contains(strStar)){
                //获取第一个高亮乱码首
                Integer integerStart = startChar(temporary, substring);
                //从高亮乱码开始截取
                temporary = temporary.substring(integerStart, temporary.length());
                Integer integerEnd = letterFirst(temporary);
                //高亮乱码
                String substr = temporary.substring(0, integerEnd+1);
                while (temporary.contains(substr)){
                    temporary = trimStr(temporary, substr);
                    string = trimStr(string,substr);
                }
            }
            string = "\n" + string.trim();
        }
        if (notFinished == null && notFinished == ""){
            notFinished = "---- More ----";
        }
        string = string.replace(" "+notFinished+"\n","");
        this.ReturnInformation = string;
        return string;
    }

    /***
    * @method: 关闭连接
    * @Param: []
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("closeConnect")
    public void closeConnect(){
        //ssh关闭连接
        sshConnect.close();
    }


    //删除字符串中 第一个满足条件的 子字符串
    public static String trimStr(String str, String indexStr){
        if(str == null){
            return null;
        }
        StringBuilder newStr = new StringBuilder(str);
        if(newStr.indexOf(indexStr) == 0){
            newStr = new StringBuilder(newStr.substring(indexStr.length()));
        }else if(newStr.indexOf(indexStr) == newStr.length() - indexStr.length()){
            newStr = new StringBuilder(newStr.substring(0,newStr.lastIndexOf(indexStr)));

        }else if(newStr.indexOf(indexStr) < (newStr.length() - indexStr.length())){
            newStr =  new StringBuilder(newStr.substring(0,newStr.indexOf(indexStr))
                    +newStr.substring(newStr.indexOf(indexStr)+indexStr.length(),newStr.length()));

        }
        return newStr.toString();
    }

    //查看字符串中是否存在某一字符
    //boolean status = str.contains("a");

    //获取字符串中 字符的 位置
    public static Integer startChar(String str,String specialChar){
        Matcher matcher= Pattern.compile(specialChar).matcher(str);
        if(matcher.find()){
            Integer start = matcher.start();
            return start;
        }
        return null;
    }

    public static Integer letterFirst(String temporary){
        char[] charArr = temporary.toCharArray();
        for (int i=0;i<charArr.length;i++){
            if(charArr[i] >='a' && charArr[i]<='z' ||  charArr[i] >='A' && charArr[i]<='Z'){
                String str = charArr[i]+"";
                Integer integer = startChar(temporary, str);
                return integer;
            }
        }
        return 0;
    }
}