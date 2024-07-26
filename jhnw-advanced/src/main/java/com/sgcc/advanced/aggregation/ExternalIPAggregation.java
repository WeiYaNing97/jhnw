package com.sgcc.advanced.aggregation;

import com.sgcc.advanced.domain.ExternalIP;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalIPAggregation {
    public static void main(String[] args){

        String returnInformation = "O E2  10.122.117.208/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.216/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.224/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.232/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.240/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.248/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O     10.122.119.4/30 [110/3] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.8/30 [110/3] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.12/30 [110/4] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.16/30 [110/102] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.20/30 [110/103] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.24/29 [110/2] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.32/29 [110/3] via 10.122.114.86, 127d,14:44:44, GigabitEthernet 9/1\n" +
                "                       [110/3] via 10.122.114.90, 127d,14:44:44, GigabitEthernet 9/2\n" +
                "O     10.122.119.48/29 [110/12] via 10.122.114.86, 116d,00:06:03, GigabitEthernet 9/1\n" +
                "                       [110/12] via 10.122.114.90, 116d,00:06:03, GigabitEthernet 9/2\n" +
                "O     10.122.119.56/29 [110/2] via 10.122.114.86, 202d,17:28:24, GigabitEthernet 9/1\n" +
                "O     10.122.119.64/30 [110/3] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.68/30 [110/4] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.80/29 [110/4] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1";

        String text = "The IP addresses are 192.168.1.1/24 and 2001:db8::1/32, but not 192.168.1.256/24 or 2001:db8::g/128.";
        //String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());

        String deviceVersion ="GigabitEthernet GE FastEthernet Ten-GigabitEthernet Ethernet Eth-Trunk XGigabitEthernet Trunking BAGG Eth FastEthernet SFP USB InLoop";

        String[] deviceVersion_split = deviceVersion.split(" ");

        String[] returnInformation_split = returnInformation.split("\n");

        List<ExternalIP> externalIPList = new ArrayList<>();
        for (int i = 0; i < returnInformation_split.length; i++) {
            String returnInformation_split_i = returnInformation_split[i];

            List<String> ipcidRs = MyUtils.findIPCIDRs(returnInformation_split_i);

            String port = includePortNumberKeywords(returnInformation_split_i, deviceVersion_split);
            if (ipcidRs.size() == 2 &&  port != null){
                ExternalIP externalIP = new ExternalIP();
                for (String ipcidR : ipcidRs) {
                    if (ipcidR.contains("/")) {
                        externalIP.setDestinationMask(ipcidR);
                    }else {
                        externalIP.setNextHop(ipcidR);
                    }
                }
                externalIP.setInterface(port);
                externalIPList.add(externalIP);
            }
        }
        externalIPList.stream().forEach(System.out::println);
    }

    /**
     * 在给定的行数据中查找并返回第一个同时包含设备端口号关键字且包含数字的单词（按空格分隔）。
     *
     * <p>该方法接收两个参数：一个是要搜索的行数据（{@code lineData}），另一个是包含设备端口号关键字的字符串数组（{@code deviceVersion_split}）。
     * 它首先遍历设备版本关键字数组，对于每个关键字，检查行数据是否包含该关键字（不区分大小写）。
     * 如果行数据包含某个关键字，则进一步将行数据按空格拆分为单词数组，并遍历这些单词。
     * 对于每个单词，如果它同时包含给定的设备端口号关键字（不区分大小写）且包含至少一个数字，则立即返回该单词。</p>
     *
     * <p>如果行数据中不包含任何给定的设备端口号关键字，或者没有找到同时满足条件的单词，则返回{@code null}。</p>
     *
     * @param lineData 要搜索的行数据字符串。
     * @param deviceVersion_split 包含设备端口号关键字的字符串数组。
     * @return 第一个同时包含设备端口号关键字且包含数字的单词（按空格分隔），如果未找到则返回{@code null}。
     */
    public static String includePortNumberKeywords(String lineData,String[] deviceVersion_split) {
        for (String deviceVersion : deviceVersion_split) {
            if (MyUtils.containIgnoreCase(lineData,deviceVersion)) {

                lineData = caseInsensitiveReplace(lineData, deviceVersion+" ", deviceVersion);

                String[] lineData_split = lineData.split(" ");
                for (int i = 0; i < lineData_split.length; i++) {
                    String split_i = lineData_split[i];
                    if (MyUtils.containIgnoreCase(split_i,deviceVersion) && MyUtils.containDigit(split_i)) {
                        return split_i;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 不区分大小写地替换字符串中的指定子串。
     *
     * <p>此方法接收三个字符串参数：输入字符串（{@code input}），要查找的子串（{@code find}），以及用于替换的子串（{@code replacement}）。
     * 它遍历输入字符串，查找所有与要查找的子串（不区分大小写）匹配的实例，并将它们替换为指定的替换子串。
     * 替换时，会考虑原始字符串中被替换部分的大小写情况，以确保替换后的字符串尽可能保持原始字符串的大小写特征。</p>
     *
     * <p>如果输入参数中的任何一个为{@code null}，则抛出{@link IllegalArgumentException}异常。</p>
     *
     * @param input 输入的原始字符串。
     * @param find 要在输入字符串中查找的子串（不区分大小写）。
     * @param replacement 替换找到的子串的字符串。
     * @return 替换后的字符串。
     * @throws IllegalArgumentException 如果输入参数中的任何一个为{@code null}。
     */
    public static String caseInsensitiveReplace(String input, String find, String replacement) {
        // 检查输入参数是否为null，如果是，则抛出异常
        if (input == null || find == null || replacement == null) {
            throw new IllegalArgumentException("Arguments cannot be null"); // 抛出异常，提示参数不能为空
        }

        int start = 0; // 初始化查找起始位置
        int end = 0; // 初始化当前查找结束位置
        StringBuilder builder = new StringBuilder(); // 使用StringBuilder来构建最终的字符串

        // 循环查找并替换
        while ((end = input.toLowerCase().indexOf(find.toLowerCase(), start)) != -1) {
            // 将从起始位置到当前查找结束位置之前的子字符串添加到StringBuilder中
            builder.append(input.substring(start, end));
            // 调用capitalizeReplacement方法处理替换逻辑，考虑原始字符串中find部分的字母大小写
            builder.append(capitalizeReplacement(input.substring(end, end + find.length()), replacement));
            // 更新下一次查找的起始位置
            start = end + find.length();
        }
        // 将最后一部分（可能没有被替换的部分）添加到StringBuilder中
        builder.append(input.substring(start));

        // 返回构建好的字符串
        return builder.toString();
    }



    /**
     * 根据原始字符串的首尾字符的大小写状态，调整替换字符串的首尾字符的大小写。
     *
     * <p>此方法首先检查原始字符串的首字符是否为大写，如果是，则将替换字符串的首字符也转换为大写。
     * 接着，如果原始字符串的末字符存在且为小写（且替换字符串长度大于1），则将替换字符串的末字符也转换为小写。
     * 这样做可以确保替换后的字符串在大小写方面与原始字符串的某些部分保持一致。</p>
     *
     * @param original 原始字符串，用于判断其首尾字符的大小写状态。
     * @param replacement 需要被调整大小写的替换字符串。
     * @return 调整后的替换字符串，其首尾字符的大小写状态可能与原始字符串的首尾字符保持一致。
     */
    private static String capitalizeReplacement(String original, String replacement) {
        // 检查原始字符串的第一个字符是否为大写
        boolean firstCharUpper = Character.isUpperCase(original.charAt(0));
        // 检查原始字符串的最后一个字符（如果存在）是否为小写
        boolean lastCharLower = original.length() > 1 && Character.isLowerCase(original.charAt(original.length() - 1));

        String result = replacement; // 初始化结果为替换字符串

        // 如果原始字符串的第一个字符是大写，则将替换字符串的第一个字符也转换为大写
        if (firstCharUpper) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }

        // 如果原始字符串的最后一个字符是小写，并且替换字符串的长度大于1，
        // 则将替换字符串的最后一个字符也转换为小写
        if (lastCharLower && result.length() > 1) {
            result = result.substring(0, result.length() - 1) + Character.toLowerCase(result.charAt(result.length() - 1));
        }

        return result; // 返回处理后的结果字符串
    }

}
