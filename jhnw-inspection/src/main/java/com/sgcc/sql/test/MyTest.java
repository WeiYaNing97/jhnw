package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.controller.TotalQuestionTableController;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import com.sgcc.sql.service.ITotalQuestionTableService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class MyTest {
    public static void main(String[] args) {
        TotalQuestionTableController totalQuestionTableController = new TotalQuestionTableController();
        List<String> stringList = totalQuestionTableController.temProNamelist("韦亚宁");

    }

}
