package com.sgcc.share.switchboard;

import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.ScanResultsVO;
import com.sgcc.share.domain.SwitchProblemCO;
import com.sgcc.share.domain.SwitchProblemVO;
import com.sgcc.share.domain.ValueInformationVO;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.share.util.EncryptUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SwitchIssueEcho {

    @Autowired
    private ISwitchScanResultService switchScanResultService;

    /**
     * @method: 查询扫描出的问题表 放入 websocket
     * @Param: []
     * @return: java.util.List<com.sgcc.sql.domain.SwitchProblem>
     */
    @GetMapping("getSwitchScanResultListByData")
    @ApiOperation("查询当前扫描出的问题表放入websocket")
    public void getSwitchScanResultListByData(String username,Long longId){

        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        SwitchProblemVO pojpVO = switchScanResultService.selectSwitchScanResultListById(longId);
        if (pojpVO == null){
            return;
        }

        List<SwitchProblemCO> switchProblemCOList = pojpVO.getSwitchProblemCOList();

        for (SwitchProblemCO switchProblemCO:switchProblemCOList){
            /*赋值随机数 前端需要*/
            switchProblemCO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            /*定义 参数集合 */
            List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
            /*根据 结构数据中的 交换机扫描结果ID 在交换机扫描结果数据 hashmap中 取出 *//*
                SwitchScanResult switchScanResult = hashMap.get(switchProblemCO.getQuestionId());*/
            //提取信息 如果不为空 则有参数
            if (switchProblemCO.getDynamicInformation()!=null && !switchProblemCO.getDynamicInformation().equals("")){//switchScanResult.getDynamicInformation()!=null && !switchScanResult.getDynamicInformation().equals("")

                //String dynamicInformation = switchScanResult.getDynamicInformation();
                String dynamicInformation = switchProblemCO.getDynamicInformation();
                //几个参数中间的 参数是 以  "=:=" 来分割的
                //设备型号=:=是=:=S3600-28P-EI=:=设备品牌=:=是=:=H3C=:=内部固件版本=:=是=:=3.10,=:=子版本号=:=是=:=1510P09=:=
                String[] dynamicInformationsplit = dynamicInformation.split("=:=");
                //判断提取参数 是否为空
                if (dynamicInformationsplit.length>0){

                    //考虑到 需要获取 参数 的ID 所以要从参数组中获取第一个参数的 ID
                    //所以 参数组 要倒序插入
                    for (int number=dynamicInformationsplit.length-1;number>0;number--){
                        //创建 参数 实体类
                        ValueInformationVO valueInformationVO = new ValueInformationVO();
                        //插入参数
                        //用户名=:=是=:=admin=:=密码=:=否=:=$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g=:=
                        String setDynamicInformation=dynamicInformationsplit[number];
                        valueInformationVO.setDynamicInformation(setDynamicInformation);
                        --number;
                        String setExhibit=dynamicInformationsplit[number];
                        valueInformationVO.setExhibit(setExhibit);//是否显示
                        if (setExhibit.equals("否")){
                            String setDynamicInformationMD5 = EncryptUtil.densificationAndSalt(setDynamicInformation);
                            valueInformationVO.setDynamicInformation(setDynamicInformationMD5);//动态信息
                        }
                        --number;
                        valueInformationVO.setDynamicVname(dynamicInformationsplit[number]);//动态信息名称
                        valueInformationVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                        valueInformationVOList.add(valueInformationVO);
                    }

                }
            }
            switchProblemCO.setValueInformationVOList(valueInformationVOList);
        }


        //将ip存入回显实体类
        List<ScanResultsVO> scanResultsVOList = new ArrayList<>();
        ScanResultsVO scanResultsVO = new ScanResultsVO();
        scanResultsVO.setSwitchIp(pojpVO.getSwitchIp());
        scanResultsVO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue();
        scanResultsVO.setShowBasicInfo("("+pojpVO.getBrand()+" "+pojpVO.getSwitchType()+" "
                +pojpVO.getFirewareVersion()+" "+pojpVO.getSubVersion()+")");
        List<SwitchProblemVO> switchProblemVOList = new ArrayList<>();
        switchProblemVOList.add(pojpVO);
        scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
        scanResultsVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
        String switchIp = scanResultsVO.getSwitchIp();
        //String[] split = switchIp.split(":");
        scanResultsVO.setSwitchIp(switchIp);
        List<SwitchProblemVO> pojoVOlist = scanResultsVO.getSwitchProblemVOList();
        for (SwitchProblemVO switchProblemVO:pojoVOlist){
            switchProblemVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            switchProblemVO.setSwitchIp(null);
        }
        scanResultsVOList.add(scanResultsVO);
        WebSocketService.sendMessage("loophole"+username,scanResultsVOList);
    }
}
