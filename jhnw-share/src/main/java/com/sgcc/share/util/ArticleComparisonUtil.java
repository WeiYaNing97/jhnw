package com.sgcc.share.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: jhnw
 * @description: 文章比较 文本比较 文本对比
 * @author:
 * @create: 2023-11-29 11:56
 **/
@RestController
@RequestMapping("/share/ArticleComparisonUtil")
public class ArticleComparisonUtil {

    /* 原来 */
    static String theOriginal = "#\n" +
            " version 5.20.99, Release 1106\n" +
            "#\n" +
            " sysname H3C-S2152-1\n" +
            "#\n" +
            " domain default enable system\n" +
            "#\n" +
            " ipv6\n" +
            "#\n" +
            " telnet server enable\n" +
            "#\n" +
            " password-recovery enable\n" +
            "#\n" +
            "vlan 1\n" +
            "#\n" +
            "domain system\n" +
            " access-limit disable\n" +
            " state active\n" +
            " idle-cut disable\n" +
            " self-service-url disable\n" +
            "#\n" +
            "user-group system\n" +
            " group-attribute allow-guest\n" +
            "#\n" +
            "local-user admin\n" +
            " password cipher $c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g\n" +
            " authorization-attribute level 3\n" +
            " service-type ssh telnet\n" +
            "local-user user1\n" +
            " password cipher $c$3$OY0X1/eznU7U82j2WUcCwjfhsCh25Nqoeg==\n" +
            " authorization-attribute level 3\n" +
            " service-type ssh telnet\n" +
            "local-user user2\n" +
            " password cipher $c$3$fWohTnscKZVRlfAhH7KwKK+ZA4+Jaw==\n" +
            " authorization-attribute level 3\n" +
            " service-type ssh telnet\n" +
            "#\n" +
            "interface NULL0\n" +
            "#\n" +
            "interface Vlan-interface1\n" +
            " ip address 192.168.1.100 255.255.255.0\n" +
            "#\n" +
            "interface Ethernet1/0/1\n" +
            "#\n" +
            "interface Ethernet1/0/2\n" +
            "#\n" +
            "interface Ethernet1/0/3\n" +
            "#\n" +
            "interface Ethernet1/0/4\n" +
            "#\n" +
            "interface Ethernet1/0/5\n" +
            "#\n" +
            "interface Ethernet1/0/6\n" +
            "#\n" +
            "interface Ethernet1/0/7\n" +
            "#\n" +
            "interface Ethernet1/0/8\n" +
            "#\n" +
            "interface Ethernet1/0/9\n" +
            "#\n" +
            "interface Ethernet1/0/10\n" +
            "#\n" +
            "interface Ethernet1/0/11\n" +
            "#\n" +
            "interface Ethernet1/0/12\n" +
            "#\n" +
            "interface Ethernet1/0/13\n" +
            "#\n" +
            "interface Ethernet1/0/14\n" +
            "#\n" +
            "interface Ethernet1/0/15\n" +
            "#\n" +
            "interface Ethernet1/0/16\n" +
            "#\n" +
            "interface Ethernet1/0/17\n" +
            "#\n" +
            "interface Ethernet1/0/18\n" +
            "#\n" +
            "interface Ethernet1/0/19\n" +
            "#\n" +
            "interface Ethernet1/0/20\n" +
            "#\n" +
            "interface Ethernet1/0/21\n" +
            "#\n" +
            "interface Ethernet1/0/22\n" +
            "#\n" +
            "interface Ethernet1/0/23\n" +
            "#\n" +
            "interface Ethernet1/0/24\n" +
            "#\n" +
            "interface Ethernet1/0/25\n" +
            "#\n" +
            "interface Ethernet1/0/26\n" +
            "#\n" +
            "interface Ethernet1/0/27\n" +
            "#\n" +
            "interface Ethernet1/0/28\n" +
            "#\n" +
            "interface Ethernet1/0/29\n" +
            "#\n" +
            "interface Ethernet1/0/30\n" +
            "#\n" +
            "interface Ethernet1/0/31\n" +
            "#\n" +
            "interface Ethernet1/0/32\n" +
            "#\n" +
            "interface Ethernet1/0/33\n" +
            "#\n" +
            "interface Ethernet1/0/34\n" +
            "#\n" +
            "interface Ethernet1/0/35\n" +
            "#\n" +
            "interface Ethernet1/0/36\n" +
            "#\n" +
            "interface Ethernet1/0/37\n" +
            "#\n" +
            "interface Ethernet1/0/38\n" +
            "#\n" +
            "interface Ethernet1/0/39\n" +
            "#\n" +
            "interface Ethernet1/0/40\n" +
            "#\n" +
            "interface Ethernet1/0/41\n" +
            "#\n" +
            "interface Ethernet1/0/42\n" +
            "#\n" +
            "interface Ethernet1/0/43\n" +
            "#\n" +
            "interface Ethernet1/0/44\n" +
            "#\n" +
            "interface Ethernet1/0/45\n" +
            "#\n" +
            "interface Ethernet1/0/46\n" +
            "#\n" +
            "interface Ethernet1/0/47\n" +
            "#\n" +
            "interface Ethernet1/0/48\n" +
            "#\n" +
            "interface GigabitEthernet1/0/49\n" +
            "#\n" +
            "interface GigabitEthernet1/0/50\n" +
            "#\n" +
            "interface GigabitEthernet1/0/51\n" +
            "#\n" +
            "interface GigabitEthernet1/0/52\n" +
            "#\n" +
            " undo info-center logfile enable\n" +
            "#\n" +
            " ssh server enable\n" +
            " ssh user admin service-type stelnet authentication-type password\n" +
            "#\n" +
            " load xml-configuration\n" +
            "#\n" +
            " load tr069-configuration\n" +
            "#\n" +
            "user-interface aux 0\n" +
            "user-interface vty 0 4\n" +
            " authentication-mode scheme\n" +
            " user privilege level 3\n" +
            " set authentication password cipher $c$3$sh7XRFVfwCOzWj8YhTw3f7lXKjY8yKhuIQ==\n" +
            "user-interface vty 5 15\n" +
            " authentication-mode scheme\n" +
            "#\n" +
            "return";
    /* 现在 */
    static String newarticle = "#\n" +
            " version 5.20.99, Release 1106\n" +
            "#\n" +
            " sysname H3C-S2152-1\n" +
            "#\n" +
            " domain default enable system\n" +
            "#\n" +
            " ipv6\n" +
            "#\n" +
            " telnet server enable\n" +
            "#\n" +
            " password-recovery enable\n" +
            "#\n" +
            "vlan 1\n" +
            "#\n" +
            "domain system\n" +
            " access-limit disable\n" +
            " state active\n" +
            " idle-cut disable\n" +
            " self-service-url disable\n" +
            "#\n" +
            "user-group system\n" +
            " group-attribute allow-guest\n" +
            "#\n" +
            "local-user admin\n" +
            " password cipher $c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g\n" +
            " authorization-attribute level 3\n" +
            " service-type ssh telnet\n" +
            "local-user test1\n" +
            " password cipher $c$3$mqJiTUkb52XrTxIHDpIhz9Mw/T7PerRL\n" +
            "local-user test2\n" +
            " password cipher $c$3$K9jfMXEqmhnWb8/LAjwe53WxtARUxACh\n" +
            "local-user user1\n" +
            " password cipher $c$3$OY0X1/eznU7U82j2WUcCwjfhsCh25Nqoeg==\n" +
            " authorization-attribute level 3\n" +
            " service-type ssh telnet\n" +
            "local-user user2\n" +
            " password cipher $c$3$fWohTnscKZVRlfAhH7KwKK+ZA4+Jaw==\n" +
            " authorization-attribute level 3\n" +
            " service-type ssh telnet\n" +
            "#\n" +
            "interface NULL0\n" +
            "#\n" +
            "interface Vlan-interface1\n" +
            " ip address 192.168.1.100 255.255.255.0\n" +
            "#\n" +
            "interface Ethernet1/0/1\n" +
            "#\n" +
            "interface Ethernet1/0/2\n" +
            "#\n" +
            "interface Ethernet1/0/3\n" +
            "#\n" +
            "interface Ethernet1/0/4\n" +
            "#\n" +
            "interface Ethernet1/0/5\n" +
            "#\n" +
            "interface Ethernet1/0/6\n" +
            "#\n" +
            "interface Ethernet1/0/7\n" +
            "#\n" +
            "interface Ethernet1/0/8\n" +
            "#\n" +
            "interface Ethernet1/0/9\n" +
            "#\n" +
            "interface Ethernet1/0/10\n" +
            "#\n" +
            "interface Ethernet1/0/11\n" +
            "#\n" +
            "interface Ethernet1/0/12\n" +
            "#\n" +
            "interface Ethernet1/0/13\n" +
            "#\n" +
            "interface Ethernet1/0/14\n" +
            "#\n" +
            "interface Ethernet1/0/15\n" +
            "#\n" +
            "interface Ethernet1/0/16\n" +
            "#\n" +
            "interface Ethernet1/0/17\n" +
            "#\n" +
            "interface Ethernet1/0/18\n" +
            "#\n" +
            "interface Ethernet1/0/19\n" +
            "#\n" +
            "interface Ethernet1/0/20\n" +
            "#\n" +
            "interface Ethernet1/0/21\n" +
            "#\n" +
            "interface Ethernet1/0/22\n" +
            "#\n" +
            "interface Ethernet1/0/23\n" +
            "#\n" +
            "interface Ethernet1/0/24\n" +
            "#\n" +
            "interface Ethernet1/0/25\n" +
            "#\n" +
            "interface Ethernet1/0/26\n" +
            "#\n" +
            "interface Ethernet1/0/27\n" +
            "#\n" +
            "interface Ethernet1/0/28\n" +
            "#\n" +
            "interface Ethernet1/0/29\n" +
            "#\n" +
            "interface Ethernet1/0/30\n" +
            "#\n" +
            "interface Ethernet1/0/31\n" +
            "#\n" +
            "interface Ethernet1/0/32\n" +
            "#\n" +
            "interface Ethernet1/0/33\n" +
            "#\n" +
            "interface Ethernet1/0/34\n" +
            "#\n" +
            "interface Ethernet1/0/35\n" +
            "#\n" +
            "interface Ethernet1/0/36\n" +
            "#\n" +
            "interface Ethernet1/0/37\n" +
            "#\n" +
            "interface Ethernet1/0/38\n" +
            "#\n" +
            "interface Ethernet1/0/39\n" +
            "#\n" +
            "interface Ethernet1/0/40\n" +
            "#\n" +
            "interface Ethernet1/0/41\n" +
            "#\n" +
            "interface Ethernet1/0/42\n" +
            "#\n" +
            "interface Ethernet1/0/43\n" +
            "#\n" +
            "interface Ethernet1/0/44\n" +
            "#\n" +
            "interface Ethernet1/0/45\n" +
            "#\n" +
            "interface Ethernet1/0/46\n" +
            "#\n" +
            "interface Ethernet1/0/47\n" +
            "#\n" +
            "interface Ethernet1/0/48\n" +
            "#\n" +
            "interface GigabitEthernet1/0/49\n" +
            "#\n" +
            "interface GigabitEthernet1/0/50\n" +
            "#\n" +
            "interface GigabitEthernet1/0/51\n" +
            "#\n" +
            "interface GigabitEthernet1/0/52\n" +
            "#\n" +
            " undo info-center logfile enable\n" +
            "#\n" +
            " ssh server enable\n" +
            " ssh user admin service-type stelnet authentication-type password\n" +
            "#\n" +
            " load xml-configuration\n" +
            "#\n" +
            " load tr069-configuration\n" +
            "#\n" +
            "user-interface aux 0\n" +
            "user-interface vty 0 4\n" +
            " authentication-mode scheme\n" +
            " user privilege level 3\n" +
            " set authentication password cipher $c$3$sh7XRFVfwCOzWj8YhTw3f7lXKjY8yKhuIQ==\n" +
            "user-interface vty 5 15\n" +
            " authentication-mode scheme\n" +
            "#\n" +
            "return";

    /**
     * @Description 后端测试方法
     * @desc
     * @return
     */
    @GetMapping("/fuwenbentest")
    public static List<String> fuwenbentest() {

        // 去除字符串两端的空格
        theOriginal = MyUtils.trimString(theOriginal);
        newarticle = MyUtils.trimString(newarticle);

        // 获取包含差异行号的HashMap
        HashMap<String, List<Integer>> stringListHashMap = GetProblemLines(theOriginal, newarticle);

        // 获取newarticle的差异行号列表
        List<Integer> newarticleRowlist = stringListHashMap.get("newarticle");
        // 获取theOriginal的差异行号列表
        List<Integer> theOriginalRowlist = stringListHashMap.get("theOriginal");

        // 创建StringBuilder对象用于拼接newarticle的内容
        StringBuilder stringBuildernew = new StringBuilder();

        // 将newarticle按换行符分割成字符串数组
        String[] newarticlesplit = newarticle.split("\r\n");
        for (int i = 0; i < newarticlesplit.length; i++) {
            // 如果当前行号是差异行号之一
            if (newarticleRowlist.contains(i)) {
                // 将当前行以黄色背景显示并添加到stringBuildernew中
                stringBuildernew.append("<p style=\"background-color: yellow;\">"+newarticlesplit[i]+"</p>\r");
            } else {
                // 将当前行正常显示并添加到stringBuildernew中
                stringBuildernew.append("<p>"+newarticlesplit[i]+"</p>\r");
            }
        }

        // 创建StringBuilder对象用于拼接theOriginal的内容
        StringBuilder stringBuilderthe = new StringBuilder();

        // 将theOriginal按换行符分割成字符串数组
        String[] theOriginalsplit = theOriginal.split("\r\n");
        for (int i = 0; i < theOriginalsplit.length; i++) {
            // 如果当前行号是差异行号之一
            if (theOriginalRowlist.contains(i)) {
                // 将当前行以黄色背景显示并添加到stringBuilderthe中
                stringBuilderthe.append("<p style=\"background-color: yellow;\">"+theOriginalsplit[i]+"</p>\r");
            } else {
                // 将当前行正常显示并添加到stringBuilderthe中
                stringBuilderthe.append("<p>"+theOriginalsplit[i]+"</p>\r");
            }
        }

        // 创建一个字符串列表用于存储结果
        List<String> strings = new ArrayList<>();

        // 将stringBuildernew转换为字符串并添加到strings列表中，表示现在文章内容
        strings.add(stringBuildernew.toString());

        // 将stringBuilderthe转换为字符串并添加到strings列表中，表示原来文章内容
        strings.add(stringBuilderthe.toString());

        // 返回结果列表
        return strings;
    }


    /**
     * 获取两篇文章的问题行
     *
     * @param theOriginal 第一篇文章内容
     * @param newarticle  第二篇文章内容
     * @return 包含两篇文章问题行号的HashMap，键为"newarticle"和"theOriginal"，值为对应的行号列表
     */
    public static HashMap<String, List<Integer>> GetProblemLines(String theOriginal , String newarticle ) {
        // 获取两篇文章 每行数据出现行数与次数的map集合
        HashMap<String, HashMap<String, String>> hashMapHashMap = GetMapCollection(theOriginal , newarticle );
        // 比较map集合，获取不同和相同的数据
        HashMap<String, List<String>> listHashMap = CompareMaps(hashMapHashMap);

        // 获取只存在于theOriginal中的数据
        //获取两文单独即共同出现问题的数据
        List<String> theOriginalList = listHashMap.get("只存在theOriginalMap中的key");
        // 获取只存在于newarticle中的数据
        List<String> newarticleList = listHashMap.get("只存在newarticleMap中的key");
        // 获取在theOriginal和newarticle中都存在但不同的数据
        List<String> ExistingAndDifferent = listHashMap.get("都存在且不相同key");

        // 将存在于theOriginal和newarticle中都不同但存在的数据加入到对应列表中
        //获取两文出现问题的数据
        theOriginalList.addAll(ExistingAndDifferent);
        newarticleList.addAll(ExistingAndDifferent);

        // 获取两篇文章出现问题数据的行号列表
        List<Integer> theOriginalRowlist = new ArrayList<>();
        HashMap<String, String> theOriginalhashMap = hashMapHashMap.get("theOriginalHashMap");
        for (String theOriginals:theOriginalList){
            // 将行号字符串转换为整数列表并添加到theOriginalRowlist中
            theOriginalRowlist.addAll(Arrays.stream(theOriginalhashMap.get(theOriginals).split(";")).map(Integer::parseInt).collect(Collectors.toList()));
        }
        List<Integer> newarticleRowlist = new ArrayList<>();
        HashMap<String, String> newarticlehashMap = hashMapHashMap.get("newarticleHashMap");
        for (String newarticles:newarticleList){
            // 将行号字符串转换为整数列表并添加到newarticleRowlist中
            newarticleRowlist.addAll(Arrays.stream(newarticlehashMap.get(newarticles).split(";")).map(Integer::parseInt).collect(Collectors.toList()));
        }

        // 创建并返回包含问题行号的HashMap
        HashMap<String, List<Integer>> hashMap = new HashMap<>();
        hashMap.put("newarticle",newarticleRowlist);
        hashMap.put("theOriginal",theOriginalRowlist);
        return hashMap;
    }

    /**
    * @Description 获取两篇文章 每行数据出现行数与次数的map集合
    * @desc
    * @param theOriginal
     * @param newarticle
     * @return
    */
    public static HashMap<String,HashMap<String,String>> GetMapCollection(String theOriginal,String newarticle){
        /* 按行分割 */
        String[] theOriginalsplit = theOriginal.split("\r\n");
        String[] newarticlesplit = newarticle.split("\r\n");

        /*创建 每行数据出现行数与次数的map集合
        * key   为   行数据
        * value 为   行数据出现的行数，多次出现 行数用";"分隔*/
        HashMap<String, String> theOriginalHashMap =new HashMap<>();
        for (int i = 0 ; i <= theOriginalsplit.length-1 ; i++ ){
            String integerList = theOriginalHashMap.get(theOriginalsplit[i]);
            if (integerList == null){
                theOriginalHashMap.put(theOriginalsplit[i],i+"");
            }else {
                theOriginalHashMap.put(theOriginalsplit[i],theOriginalHashMap.get(theOriginalsplit[i])+";"+i);
            }
        }

        HashMap<String, String> newarticleHashMap =new HashMap<>();
        for (int i = 0 ; i <= newarticlesplit.length-1 ; i++ ){
            String integerList = newarticleHashMap.get(newarticlesplit[i]);
            if (integerList == null){
                newarticleHashMap.put(newarticlesplit[i],i+"");
            }else {
                newarticleHashMap.put(newarticlesplit[i],newarticleHashMap.get(newarticlesplit[i])+";"+i);
            }
        }

        /* 每行数据出现行数与次数的map集合 存入map集合
        * key   为   原文（theOriginalHashMap） 和 新文（newarticleHashMap）
        * value 为   每行数据出现行数与次数的map集合 */
        HashMap<String,HashMap<String,String>> hashMapMap = new HashMap<>();
        hashMapMap.put("theOriginalHashMap",theOriginalHashMap);
        hashMapMap.put("newarticleHashMap",newarticleHashMap);

        return hashMapMap;
    }

    /**
    * @Description 获取 两文章 行数据区别
    * @author charles
    * @createTime 2023/11/30 8:33
    * @desc
    * @param hashMapHashMap
     * @return
    */
    public static HashMap<String, List<String>> CompareMaps(HashMap<String, HashMap<String, String>> hashMapHashMap) {
        /*两篇文章的 每行数据出现行数与次数的map集合*/
        HashMap<String, String> theOriginalHashMap = hashMapHashMap.get("theOriginalHashMap");
        HashMap<String, String> newarticleHashMap = hashMapHashMap.get("newarticleHashMap");
        // 获取两个map的key集合 即行数据
        Set<String> theOriginalKey = theOriginalHashMap.keySet();
        Set<String> newarticleKey = newarticleHashMap.keySet();

        // 找出两个集合的相同key , 使用Stream API和Lambda表达式
        Set<String> commonKeys = new HashSet<>(theOriginalHashMap.keySet()); // 获取 theOriginalHashMap 的所有key，也包括相同的key
        commonKeys.retainAll(newarticleHashMap.keySet()); // 保留与 newarticleHashMap 相同的key  得到 两个map集合中 相同的key集合

        //存在并且相等
        List<String> ExistingAndIdentical = new ArrayList<>();
        //存在并且不相等
        List<String> ExistingAndDifferent = new ArrayList<>();
        // 使用Stream API和Lambda表达式，比较两个map中相同key对应的value是否相同
        // 判断是否相等 采用 比较 value的 行数个数(key值出现的次数) 是否相等来确定。 如果两文 key值 出现的次数相等 则 判断相等
        commonKeys.forEach(key -> {
            if ((theOriginalHashMap.get(key).split(";").length) != (newarticleHashMap.get(key).split(";").length)) {
                //不相等
                ExistingAndDifferent.add(key);
            } else {
                //相等
                ExistingAndIdentical.add(key);
            }
        });
        HashMap<String, List<String>> listHashMap = new HashMap<>();
        listHashMap.put("都存在且相同key",ExistingAndIdentical);
        listHashMap.put("都存在且不相同key",ExistingAndDifferent);
        listHashMap.put("只存在theOriginalMap中的key",theOriginalKey.stream().filter(k -> !commonKeys.contains(k)).collect(Collectors.toList()));
        listHashMap.put("只存在newarticleMap中的key",newarticleKey.stream().filter(k -> !commonKeys.contains(k)).collect(Collectors.toList()));
        return listHashMap;
    }

    /*根据前后行 判断 筛选一些数据是否有问题*/
    public static HashMap<String,List<String>> GetProblemLines(String theOriginal,String newarticle,
                                       HashMap<String,HashMap<String,String>> hashMapHashMap,
                                       HashMap<String,List<String>> listHashMap){
        // 获取只存在于theOriginal中的数据列表
        List<String> theOriginalList = listHashMap.get("只存在theOriginalMap中的key");
        // 获取只存在于newarticle中的数据列表
        List<String> newarticleList = listHashMap.get("只存在newarticleMap中的key");
        // 获取在theOriginal和newarticle中都存在但不同的数据列表
        List<String> ExistingAndDifferent = listHashMap.get("都存在且不相同key");

        // 获取theOriginal的行号映射
        HashMap<String, String> theOriginalHashMap = hashMapHashMap.get("theOriginalHashMap");
        // 存储theOriginal的行号列表
        List<String> theOriginalRowList = new ArrayList<>();
        for (String theOriginalRow:theOriginalList){
            // 将行号字符串转换为行号列表并添加到theOriginalRowList中
            theOriginalRowList.addAll(new ArrayList<>(Arrays.asList(theOriginalHashMap.get(theOriginalRow).split(";"))));
        }

        // 获取newarticle的行号映射
        HashMap<String, String> newarticleHashMap = hashMapHashMap.get("newarticleHashMap");
        // 存储newarticle的行号列表
        List<String> newarticleRowList = new ArrayList<>();
        for (String newarticleRow:newarticleList){
            // 将行号字符串转换为行号列表并添加到newarticleRowList中
            newarticleRowList.addAll(new ArrayList<>(Arrays.asList(newarticleHashMap.get(newarticleRow).split(";"))));
        }

        // 将newarticle按换行符分割成字符串数组
        String[] newarticlesplit = newarticle.split("\r\n");
        // 将theOriginal按换行符分割成字符串数组
        String[] theOriginalsplit = theOriginal.split("\r\n");

        for (String differentKey:ExistingAndDifferent){

            // 将theOriginal的行号字符串转换为行号列表
            List<String> theOriginalstrings = new ArrayList<>(Arrays.asList(theOriginalHashMap.get(differentKey).split(";")));
            // 将newarticle的行号字符串转换为行号列表
            List<String> newarticleStrings = new ArrayList<>(Arrays.asList(newarticleHashMap.get(differentKey).split(";")));

            /*如果行号为 0 或者 最后一行 ，则 判断是否相等 */
            if (theOriginalstrings.contains("0") && newarticleStrings.contains("0")){
                // 移除行号为0的项
                theOriginalstrings.remove("0");
                newarticleStrings.remove("0");
            }else if (theOriginalstrings.contains("0") && !(newarticleStrings.contains("0"))){
                // 如果theOriginal中存在行号为0，而newarticle中不存在，则将"0"添加到theOriginalRowList中
                theOriginalRowList.add("0");
                // 移除theOriginal中的行号为0的项
                theOriginalstrings.remove("0");
            }else if (newarticleStrings.contains("0") && !(theOriginalstrings.contains("0"))){
                // 如果newarticle中存在行号为0，而theOriginal中不存在，则将"0"添加到newarticleRowList中
                newarticleRowList.add("0");
                // 移除newarticle中的行号为0的项
                newarticleStrings.remove("0");
            }

            if (theOriginalstrings.contains(theOriginalsplit.length-1+"")
                    && newarticleStrings.contains(newarticlesplit.length-1+"")){
                // 移除最后一行的行号
                theOriginalstrings.remove(theOriginalsplit.length-1+"");
                newarticleStrings.remove(newarticlesplit.length-1+"");
            }

            /*排除有集合为kong情况*//*
            if (theOriginalstrings.size() == 0 && newarticleStrings.size() == 0){
                continue;
            }else if (theOriginalstrings.size() == 0){
                newarticleRowList.addAll(newarticleStrings);
            }else if (newarticleStrings.size() == 0){
                theOriginalRowList.addAll(theOriginalstrings);
            }*/

            List<String> newarticles = new ArrayList<>();
            List<String> theOriginals = new ArrayList<>();
            /*判断两文有上下两行相等情况*/
            for (String newarticleRow:newarticleStrings){
                for (String theOriginalRow:theOriginalstrings){
                    if ( newarticlesplit[Integer.valueOf(newarticleRow).intValue()-1].equals(theOriginalsplit[Integer.valueOf(theOriginalRow).intValue()-1])
                            && newarticlesplit[Integer.valueOf(newarticleRow).intValue()+1].equals(theOriginalsplit[Integer.valueOf(theOriginalRow).intValue()+1])){
                        newarticles.add(newarticleRow);
                        theOriginals.add(theOriginalRow);
                        break;
                    }
                }
            }

            newarticleStrings.removeAll(newarticles);
            theOriginalstrings.removeAll(theOriginals);

            newarticleRowList.addAll(newarticleStrings);
            theOriginalRowList.addAll(theOriginalstrings);
        }

        HashMap<String,List<String>> hashMap = new HashMap<>();
        hashMap.put("theOriginalRow",theOriginalRowList);
        hashMap.put("newarticleRow",newarticleRowList);

        return hashMap;
    }

}
