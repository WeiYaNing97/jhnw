package com.sgcc.web.controller.sql;

import java.util.Map;

public class Configuration {

    private static String bootstrap_file = "classpath:configurationFile.yml";

    /** 最小超时时间 */
    public static Long minimumTimeout;
    /** 最大超时时间 */
    public static Long maximumTimeout;


    public static void getConfiguration(){

        Map<String, String> ymlByFileName = YmlUtils.getYmlByFileName(bootstrap_file,"configuration");
        minimumTimeout = Long.valueOf(ymlByFileName.get("configuration.minimumTimeout")).longValue();
        maximumTimeout = Long.valueOf(ymlByFileName.get("configuration.maximumTimeout")).longValue();

    }

}
