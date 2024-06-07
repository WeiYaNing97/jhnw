package com.sgcc.advanced.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @program: jhnw
 * @description: 创建一个自定义的反序列化器类
 * @author:
 * @create: 2024-04-10 10:02
 **/
public class CustomDateDeserializer extends JsonDeserializer<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 从JsonParser中反序列化日期对象
     *
     * @param jsonParser JSON解析器
     * @param deserializationContext 反序列化上下文
     * @return 反序列化后的日期对象
     * @throws IOException 如果解析日期字符串时出现异常，则抛出IOException
     */
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        // 获取JSON解析器中的文本内容，即日期字符串
        String dateString = jsonParser.getText();
        // 如果日期字符串为空或者为"NaN-NaN-NaN NaN:NaN:NaN"，则返回null
        if (dateString == null || dateString.equals("NaN-NaN-NaN NaN:NaN:NaN")) {
            return null;
        }
        try {
            // 使用dateFormat对象解析日期字符串，并返回解析后的日期对象
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            // 如果解析日期字符串时抛出ParseException异常，则包装成IOException异常并抛出
            throw new IOException("Invalid date format", e);
        }
    }
}
