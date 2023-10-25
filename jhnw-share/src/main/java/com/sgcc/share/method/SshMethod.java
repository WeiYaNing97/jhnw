package com.sgcc.share.method;

import com.sgcc.share.connectutil.SshConnect;
import com.sgcc.share.util.MyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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
    /**
    * @Description  连接ssh
    * @author charles
    * @createTime 2023/10/11 14:02
    * @desc
     * 连接交换机 返回 List<Object> 数组
     * 如果连接成功 则返回 true 和 SshConnect(JSCH 使用方法类)
     * 如果连接失败 则返回 false
    * @param ip
     * @param port
     * @param name
     * @param password
     * @return
    */
    @RequestMapping("requestConnect")
    public List<Object> requestConnect(String ip, int port, String name, String password){

        //(JSCH 使用方法类) 创建连接 ip 端口号：22
        SshConnect sshConnect = new SshConnect(ip, port, null, null);

        //用户名、密码
        String[] cmds = { name, password};

        //连接方法
        List<Object> login = sshConnect.login(ip, cmds);

        /*判断交换机是否连接成功*/
        if ((boolean) login.get(0) == true){
            List<Object> objects = new ArrayList<>();
            objects.add(true);
            objects.add(sshConnect);/* JSCH 使用方法类 */
            return objects;
        }

        return login;
    }

    /**
    * @Description  发送命令
    * @author charles
    * @createTime 2023/10/11 14:14
    * @desc
    * @param ip
     * @param sshConnect   (JSCH 使用方法类)
     * @param command   命令
     * @param notFinished   返回信息未结束标准
     * @return
    */
    @RequestMapping("sendCommand")
    public String sendCommand(String ip,SshConnect sshConnect,String command,String notFinished){
        //ssh发送命令
        //将命令放到数组中，满足封装的JSCH方法的参数要求
        String[] cmds = {command};
        /*if (this.ReturnInformation.endsWith(EndIdentifier) && command.equalsIgnoreCase("quit")) {
            quit = true;
        }*/

        //发送命令 quit:涉及是否断开交换机连接
        String string = sshConnect.batchCommand(ip,cmds, notFinished,null,false);

        if (string.equals("") || string == null){
            return null;
        }
        if (string.indexOf("遗失对主机的连接")!=-1){
            return string;
        }

        if (string.length()>1 && string.substring(0, 1).equals("\n")){
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
        //  todo  --More--
        if (notFinished == null || notFinished == ""){

            string = MyUtils.trimString(string);
            String[] stringSplit = string.split("\r\n");
            for (String stringStr:stringSplit){
                int more = stringStr.toLowerCase().indexOf("more");
                if ( (more ==-1) || more == 0 || (more+4 == stringStr.length())){
                    //没有 more  或者 more开头没有-  或者 more结尾没有-
                }else {
                    //存在 more
                    String  stringStrReplace = stringStr.replace(" ","");
                    if(stringStrReplace.charAt(more+4)=='-' || stringStrReplace.charAt(more-1)=='-'){
                        string = string.replace("\r\n"+stringStr,"");
                    }
                }
            }

        }else {
            string = string.replace(notFinished,"");
        }

       // this.ReturnInformation = string;
        return string;
    }

    /**
    * @Description 关闭连接
    * @author charles
    * @createTime 2023/10/11 14:14
    * @desc
    * @param sshConnect	(JSCH 使用方法类)
     * @return
    */
    @RequestMapping("closeConnect")
    public void closeConnect(SshConnect sshConnect){
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