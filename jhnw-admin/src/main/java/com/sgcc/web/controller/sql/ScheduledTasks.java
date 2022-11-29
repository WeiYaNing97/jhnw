package com.sgcc.web.controller.sql;

import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.SwitchProblem;
import com.sgcc.sql.domain.ValueInformation;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.service.ISwitchProblemService;
import com.sgcc.sql.service.IValueInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年08月12日 14:18
 */
@Component("scheduledTasks")
//事务
@Transactional(rollbackFor = Exception.class)
public class ScheduledTasks {

    @Autowired
    private ISwitchProblemService switchProblemService;

    @Autowired
    private IValueInformationService valueInformationService;

    @Autowired
    private IReturnRecordService returnRecordService;

    @RequestMapping("deleteExpiredData")
    public void deleteExpiredData(){

        Date getStringtoData = getStringtoData("2022-12-20 11:11:11");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        //过去三个月
        c.setTime(getStringtoData);
        c.add(Calendar.MONTH, -3);
        Date data = c.getTime();
        String mon3 = format.format(data);
        System.out.println("过去三个月："+mon3);
        switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
        List<SwitchProblem> switchProblemList = switchProblemService.selectSwitchProblemByDate(mon3);
        Long[] switchProblemId = new Long[switchProblemList.size()];
        List<Long> valueInformationID = new ArrayList<>();
        for (int number = 0 ; number<switchProblemList.size(); number++){
            switchProblemId[number] = switchProblemList.get(number).getId();

            if (switchProblemList.get(number).getValueId() !=0L){
                valueInformationService = SpringBeanUtil.getBean(IValueInformationService.class);
                List<ValueInformation> valueInformationList = valueInformationService.selectValueInformationListByID(switchProblemList.get(number).getValueId());
                for (ValueInformation valueInformation:valueInformationList){
                    valueInformationID.add(valueInformation.getId());
                }
            }
        }
        Long[] valueID = new Long[valueInformationID.size()];
        for (int number = 0; number < valueInformationID.size(); number++){
            valueID[number] = valueInformationID.get(number);
        }
        int i = switchProblemService.deleteSwitchProblemByIds(switchProblemId);
        int i1 = valueInformationService.deleteValueInformationByIds(valueID);
        int i2 = returnRecordService.deleteReturnRecordByDate(mon3);
        System.err.println("\r\n定时任务启动\r\n");
    }

    public static Date getStringtoData(String time){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = null;
        try {
            parse = format.parse(time);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }
}