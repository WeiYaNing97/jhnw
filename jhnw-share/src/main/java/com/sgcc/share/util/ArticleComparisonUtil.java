package com.sgcc.share.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: jhnw
 * @description: 文章比较
 * @author:
 * @create: 2023-11-29 11:56
 **/
public class ArticleComparisonUtil {

    public static void main(String[] args) {

        String theOriginal = "[1364] 24 Nov 09:22:29.306 * Background saving terminated with success\n" +
                "[1364] 27 Nov 09:29:49.475 * 1 changes in 900 seconds. Saving...\n" +
                "[1364] 27 Nov 09:29:49.655 * Background saving started by pid 29832\n" +
                "[1364] 27 Nov 09:29:55.32 # fork operation complete\n" +
                "[1364] 27 Nov 09:29:55.312 * Background saving terminated with success\n" +
                "[1364] 27 Nov 09:34:56.091 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 09:34:56.104 * Background saving started by pid 26492\n" +
                "[1364] 27 Nov 09:34:56.323 # fork operation complete\n" +
                "[1364] 27 Nov 09:34:56.324 * Background saving terminated with success\n" +
                "[1364] 27 Nov 10:37:37.765 * 1 changes in 900 seconds. Saving...\n" +
                "[1364] 27 Nov 10:37:37.809 * Background saving started by pid 33420\n" +
                "[1364] 27 Nov 10:37:38.254 # fork operation complete\n" +
                "[1364] 27 Nov 10:37:38.254 * Background saving terminated with success\n" +
                "[1364] 27 Nov 10:42:39.069 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 10:42:39.074 * Background saving started by pid 33032\n" +
                "[1364] 27 Nov 10:42:39.178 # fork operation complete\n" +
                "[1364] 27 Nov 10:42:39.185 * Background saving terminated with success\n" +
                "[1364] 27 Nov 10:47:40.088 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 10:47:40.095 * Background saving started by pid 29992\n" +
                "[1364] 27 Nov 10:47:40.745 # fork operation complete\n" +
                "[1364] 27 Nov 10:47:40.746 * Background saving terminated with success\n" +
                "[1364] 27 Nov 12:46:02.101 * 1 changes in 900 seconds. Saving...\n" +
                "[1364] 27 Nov 12:46:02.134 * Background saving started by pid 35836\n" +
                "[1364] 27 Nov 12:46:02.461 # fork operation complete\n" +
                "[1364] 27 Nov 12:46:02.461 * Background saving terminated with success\n" +
                "[1364] 27 Nov 12:51:03.092 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 12:51:03.098 * Background saving started by pid 33128\n" +
                "[1364] 27 Nov 12:51:03.312 # fork operation complete\n" +
                "[1364] 27 Nov 12:51:03.313 * Background saving terminated with success";
        String newarticle = "[1364] 24 Nov 09:22:29.306 * Background saving terminated with success\n" +
                "[1364] 27 Nov 09:29:49.475 * 1 changes in 900 seconds. Saving...\n" +
                "[1364] 27 Nov 09:29:49.655 * Background saving started by pid 29832\n" +
                "[1364] 27 Nov 09:29:55.312 # fork operation complete\n" +
                "[1364] 27 Nov 09:29:55.312 * Background saving terminated with success\n" +
                "[1364] 27 Nov 09:34:56.091 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 09:34:56.091 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 09:34:56.104 * Background saving started by pid 26492\n" +
                "[1364] 27 Nov 09:34:56.323 # fork operation complete\n" +
                "[1364] 27 Nov 09:34:56.324 * Background saving terminated with success\n" +
                "[1364] 27 Nov 10:37:37.765 * 1 changes in 900 seconds. Saving...\n" +
                "[1364] 27 Nov 10:37:37.809 * Background saving started by pid 33420\n" +
                "[1364] 27 Nov 10:37:38.254 # fork operation complete\n" +
                "[1364] 27 Nov 10:37:38.24 # fork operation complete\n" +
                "[1364] 27 Nov 10:37:38.254 * Background saving terminated with success\n" +
                "[1364] 27 Nov 10:42:39.069 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 10:42:39.074 * Background saving started by pid 33032\n" +
                "[1364] 27 Nov 10:42:39.178 # fork operation complete\n" +
                "[1364] 27 Nov 10:42:39.185 * Background saving terminated with success\n" +
                "[1364] 27 Nov 10:47:40.088 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 10:47:40.095 * Background saving started by pid 29992\n" +
                "[1364] 27 Nov 10:47:40.745 # fork operation complete\n" +
                "[1364] 27 Nov 10:47:40.746 * Background saving terminated with success\n" +
                "[1364] 27 Nov 12:46:02.101 * 1 changes in 900 seconds. Saving...\n" +
                "[1364] 27 Nov 12:46:02.134 * Background saving started by pid 35836\n" +
                "[1364] 27 Nov 12:46:02.461 # fork operation complete\n" +
                "[1364] 27 Nov 12:46:02.461 * Background saving terminated with success\n" +
                "[1364] 27 Nov 12:51:03.092 * 10 changes in 300 seconds. Saving...\n" +
                "[1364] 27 Nov 12:51:03.098 * Background saving started by pid 33128\n" +
                "[1364] 27 Nov 12:51:03.312 # fork operation complete\n" +
                "[1364] 27 Nov 12:51:03.313 * Background saving terminated with success\n"+
                "[1364] 27 Nov 12:51:03.313 * Background saving terminated with success";

        HashMap<String, List<Integer>> stringListHashMap = GetProblemLines(theOriginal, newarticle);

        List<Integer> newarticleRowlist = stringListHashMap.get("newarticle");
        List<Integer> theOriginalRowlist = stringListHashMap.get("theOriginal");

        System.err.println("========================================================");
        System.err.println("=====================newarticle========================");
        System.err.println("========================================================");
        String[] newarticlesplit = newarticle.split("\r\n");
        for (int i = 0 ; i< newarticlesplit.length ; i++ ){
            if (newarticleRowlist.contains(i)){
                System.err.println(i +"               "+newarticlesplit[i]);
            }else {
                System.out.println(i +"               "+newarticlesplit[i]);
            }
        }
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println("========================================================");
        System.err.println("=====================theOriginal========================");
        System.err.println("========================================================");
        String[] theOriginalsplit = theOriginal.split("\r\n");
        for (int i = 0 ; i< theOriginalsplit.length ; i++ ){
            if (theOriginalRowlist.contains(i)){
                System.err.println(i +"               "+theOriginalsplit[i]);
            }else {
                System.out.println(i +"               "+theOriginalsplit[i]);
            }
        }
    }

    /**
    * @Description 获取两文的问题行
    * @desc
    * @param theOriginal
     * @param newarticle
     * @return
    */
    public static HashMap<String, List<Integer>> GetProblemLines(String theOriginal , String newarticle ) {
        /*获取两篇文章 每行数据出现行数与次数的map集合*/
        HashMap<String, HashMap<String, String>> hashMapHashMap = GetMapCollection( theOriginal , newarticle );
        HashMap<String, List<String>> listHashMap = CompareMaps(hashMapHashMap);

        //获取两文单独即共同出现问题的数据
        List<String> theOriginalList = listHashMap.get("只存在theOriginalMap中的key");
        List<String> newarticleList = listHashMap.get("只存在newarticleMap中的key");
        List<String> ExistingAndDifferent = listHashMap.get("都存在且不相同key");
        //获取两文出现问题的数据
        theOriginalList.addAll(ExistingAndDifferent);
        newarticleList.addAll(ExistingAndDifferent);

        //获取两文出现问题数据的行号
        List<Integer> theOriginalRowlist = new ArrayList<>();
        HashMap<String, String> theOriginalhashMap = hashMapHashMap.get("theOriginalHashMap");
        for (String theOriginals:theOriginalList){
            theOriginalRowlist.addAll(Arrays.stream(theOriginalhashMap.get(theOriginals).split(";")).map(Integer::parseInt).collect(Collectors.toList()));
        }
        List<Integer> newarticleRowlist = new ArrayList<>();
        HashMap<String, String> newarticlehashMap = hashMapHashMap.get("newarticleHashMap");
        for (String newarticles:newarticleList){
            newarticleRowlist.addAll(Arrays.stream(newarticlehashMap.get(newarticles).split(";")).map(Integer::parseInt).collect(Collectors.toList()));
        }

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

        List<String> theOriginalList = listHashMap.get("只存在theOriginalMap中的key");
        List<String> newarticleList = listHashMap.get("只存在newarticleMap中的key");
        List<String> ExistingAndDifferent = listHashMap.get("都存在且不相同key");


        HashMap<String, String> theOriginalHashMap = hashMapHashMap.get("theOriginalHashMap");
        List<String> theOriginalRowList = new ArrayList<>();
        for (String theOriginalRow:theOriginalList){
            theOriginalRowList.addAll(new ArrayList<>(Arrays.asList(theOriginalHashMap.get(theOriginalRow).split(";"))));
        }


        HashMap<String, String> newarticleHashMap = hashMapHashMap.get("newarticleHashMap");
        List<String> newarticleRowList = new ArrayList<>();
        for (String newarticleRow:newarticleList){
            newarticleRowList.addAll(new ArrayList<>(Arrays.asList(newarticleHashMap.get(newarticleRow).split(";"))));
        }

        String[] newarticlesplit = newarticle.split("\r\n");
        String[] theOriginalsplit = theOriginal.split("\r\n");

        // todo 都存 但是存在的个数不一样
        for (String differentKey:ExistingAndDifferent){
            List<String> theOriginalstrings = new ArrayList<>(Arrays.asList(theOriginalHashMap.get(differentKey).split(";")));
            List<String> newarticleStrings = new ArrayList<>(Arrays.asList(newarticleHashMap.get(differentKey).split(";")));
            /*如果行号为 0 或者 最后一行 ，则 判断是否相等 */
            if (theOriginalstrings.contains("0") && newarticleStrings.contains("0")){
                theOriginalstrings.remove("0");
                newarticleStrings.remove("0");
            }else if (theOriginalstrings.contains("0") && !(newarticleStrings.contains("0"))){
                theOriginalRowList.add("0");
                theOriginalstrings.remove("0");
            }else if (newarticleStrings.contains("0") && !(theOriginalstrings.contains("0"))){
                newarticleRowList.add("0");
                newarticleStrings.remove("0");
            }

            if (theOriginalstrings.contains(theOriginalsplit.length-1+"")
                    && newarticleStrings.contains(newarticlesplit.length-1+"")){
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
