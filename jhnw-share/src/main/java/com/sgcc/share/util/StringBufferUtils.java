package com.sgcc.share.util;

import java.util.ArrayList;
import java.util.List;

public class StringBufferUtils {

    /**
     * 判断 StringBuffer 对象是否为空
     *
     * @param stringBuffer 需要判断的 StringBuffer 对象
     * @return 如果 stringBuffer 为空（长度为0或为空字符串）或为 null，则返回 true；否则返回 false
     */
    public static boolean isEmpty(StringBuffer stringBuffer) {
        // 判断 StringBuffer 对象的长度是否为0
        if (stringBuffer.length() == 0 ||
            // 判断 StringBuffer 对象是否为 null
            stringBuffer == null ||
            // 判断 StringBuffer 对象转换为字符串后是否为空字符串
            stringBuffer.toString()=="") {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 从 StringBuffer 对象中截取指定范围内的子串，并将截取结果输出到控制台。
     *
     * @param original 目标 StringBuffer 对象
     * @param start    截取起始位置（包含此位置），索引从0开始
     * @param end      截取结束位置（不包含此位置），索引从0开始
     */
    public static StringBuffer substring(StringBuffer original,int start,int end) {
        // 目标 StringBuffer
        StringBuffer result = new StringBuffer();

        // 如果 start 和 end 是有效的
        if (start >= 0 && end <= original.length() && start <= end) {
            // 创建一个临时的字符数组来存储截取的部分
            char[] temp = new char[end - start];

            // 从 original 获取字符并放入 temp 数组
            original.getChars(start, end, temp, 0);

            // 将 temp 数组中的字符添加到 result
            result.append(temp);
        }

        // 输出结果
        return result;  // 输出: world
    }

    /**
     * 对给定的 StringBuffer 内容进行格式化处理。
     *
     * @param stringBuffer 需要格式化的 StringBuffer 对象
     * @return 格式化后的 StringBuffer 对象
     *
     * <p>该方法将给定的 StringBuffer 对象中的内容进行格式化处理，处理规则如下：</p>
     *
     * <ul>
     *     <li>将连续的换行符统一转换为标准的 "\r\n" 换行符。</li>
     *     <li>将连续的空白字符（空格、制表符等）统一替换为一个空格。</li>
     *     <li>非换行符和空白字符的其他字符保持不变。</li>
     * </ul>
     *
     * <p>注意：该方法不会修改原 StringBuffer 对象，而是返回一个新的格式化后的 StringBuffer 对象。</p>
     */
    public static StringBuffer arrange(StringBuffer stringBuffer) {
        StringBuffer article = new StringBuffer(stringBuffer.toString());

        // 用于标记是否遇到过换行符或空格
        boolean lastWasNewline = false;
        boolean lastWasSpace = false;

        // 创建一个新的StringBuffer对象来存放处理后的结果
        StringBuffer formattedArticle = new StringBuffer();

        for (int i = 0; i < article.length(); i++) {
            char c = article.charAt(i);

            // 检查当前字符是否是换行符
            if (c == '\r' || c == '\n') {
                // 如果前一个字符不是换行符，则添加标准换行符
                if (!lastWasNewline) {
                    formattedArticle.append("\r\n");
                    lastWasNewline = true; // 标记已经遇到了换行符
                }
                // 跳过其他换行符
            } else if (Character.isWhitespace(c)) { // 检查当前字符是否是空白字符
                // 如果前一个字符不是空格也不是换行符
                if (!lastWasSpace && !lastWasNewline) {
                    formattedArticle.append(' ');
                    lastWasSpace = true; // 标记已经遇到了空格
                }
                // 跳过多余的空格
            } else { // 当前字符既不是换行符也不是空白字符
                // 添加当前字符到formattedArticle中
                formattedArticle.append(c);
                // 重置lastWasNewline和lastWasSpace标记
                lastWasNewline = false;
                lastWasSpace = false;
            }
        }

        // 输出格式化后的文章
        return formattedArticle;
    }


    /**
     * 使用指定的分隔符将 StringBuffer 字符串分割成多个字符串列表。
     *
     * @param buffer 需要被分割的 StringBuffer 对象
     * @param delimiter 分隔符字符串
     * @return 包含所有分割后的字符串的列表
     */
    public static List<String> stringBufferSplit(StringBuffer buffer, String delimiter) {
        StringBuffer sb = new StringBuffer(buffer);
        List<String> result = new ArrayList<>();
        int start = 0;  // 分割开始的位置
        int index;  // 分隔符出现的位置

        // 循环遍历 StringBuffer 字符串，查找分隔符并分割
        while ((index = sb.indexOf(delimiter, start)) != -1) {
            // 从start到index（不包括）创建一个新的StringBuffer并添加到结果列表
            result.add(sb.substring(start, index).toString());
            // 更新start为当前分隔符之后的位置
            start = index + delimiter.length();
        }

        // 添加最后一个部分
        // 如果start小于StringBuffer的长度，说明还有未处理的字符串部分
        if (start < sb.length()) {
            // 添加剩余部分的StringBuffer到结果列表
            result.add(sb.substring(start, sb.length()).toString());
        }

        return result;
    }


    /**
     * 将传入的 StringBuffer 对象中的所有大写字母转换为小写字母，并返回新的 StringBuffer 对象
     *
     * @param buffer 需要转换的 StringBuffer 对象
     * @return 包含所有小写字母的新 StringBuffer 对象
     */
    public static StringBuffer stringBufferToLowerCase(StringBuffer buffer) {
        StringBuffer sb = new StringBuffer(buffer);
        StringBuffer lower = new StringBuffer();

        // 遍历 StringBuffer 中的每个字符
        for (int i = 0; i < sb.length(); i++) {
            // 获取当前位置的字符
            char c = sb.charAt(i);
            // 判断当前字符是否为大写字母
            if (Character.isUpperCase(c)) {
                // 将当前字符转为小写
                lower.append(Character.toLowerCase(c));
            }else {
                lower.append(c);
            }
        }

        return lower;
    }

    /**
     * 判断 StringBuffer 是否包含指定的字符串（不区分大小写）
     *
     * @param buffer 待判断的 StringBuffer 对象
     * @param delimiter 需要查找的字符串
     * @return 如果 StringBuffer 包含指定的字符串，则返回 true；否则返回 false
     */
    public static boolean stringBufferContainString(StringBuffer buffer, String delimiter) {
        StringBuffer sb = new StringBuffer(buffer);
        // 将 StringBuffer 转换为小写，并查找 delimiter（也转换为小写）的位置
        int i = stringBufferToLowerCase(sb).indexOf(delimiter.toLowerCase());
        // 如果找到 delimiter，则返回 true
        if (i != -1) {
            return true;
        }else {
            // 如果没有找到 delimiter，则返回 false
            return false;
        }
    }
}
