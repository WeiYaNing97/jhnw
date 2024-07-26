package com.sgcc.share.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.*;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.share.util.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;
import sun.security.x509.IPAddressName;

/**
 * 交换机扫描结果Controller
 */
@RestController
@RequestMapping("/share/switch_scan_result")
//事务
@Transactional(rollbackFor = Exception.class)
public class SwitchScanResultController extends BaseController
{
    @Autowired
    private ISwitchScanResultService switchScanResultService;

    /**
     * 查询交换机扫描结果列表
     */
    @ApiOperation("查询交换机扫描结果列表")
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchScanResult switchScanResult) {

        switchScanResult.setUserName(SecurityUtils.getLoginUser().getUsername());
        startPage();
        List<SwitchScanResult> list = switchScanResultService.selectSwitchScanResultList(switchScanResult);
        for (SwitchScanResult pojo:list){
            pojo.setDynamicInformation(null);
        }
        return getDataTable(list);
    }

    /**
     * 导出交换机扫描结果列表
     */
    @ApiOperation("导出交换机扫描结果列表")
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:export')")
    @MyLog(title = "交换机扫描结果", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SwitchScanResult switchScanResult) {
        List<SwitchScanResult> list = switchScanResultService.selectSwitchScanResultList(switchScanResult);
        ExcelUtil<SwitchScanResult> util = new ExcelUtil<SwitchScanResult>(SwitchScanResult.class);
        return util.exportExcel(list, "交换机扫描结果数据");
    }

    /**
     * 获取交换机扫描结果详细信息
     */
    @ApiOperation("获取交换机扫描结果详细信息")
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(switchScanResultService.selectSwitchScanResultById(id));
    }


    /**
     * 根据IP查询交换机扫描结果表最新数据
     */
    @GetMapping(value = "/getTheLatestData/{ip}")/*/{ip}*/
    public AjaxResult getTheLatestData(@PathVariable String ip) {/*@PathVariable*/
        SwitchScanResult theLatestDataByIP = switchScanResultService.getTheLatestDataByIP(ip);
        if (theLatestDataByIP == null){
            return AjaxResult.success(theLatestDataByIP);
        }

        theLatestDataByIP.setSwitchPassword(EncryptUtil.desaltingAndDecryption(theLatestDataByIP.getSwitchPassword()));

        theLatestDataByIP.setConfigureCiphers(EncryptUtil.desaltingAndDecryption(theLatestDataByIP.getConfigureCiphers()));

        return AjaxResult.success(theLatestDataByIP);
    }

    /**
     * 新增交换机扫描结果
     */
    @ApiOperation("新增交换机扫描结果")
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:add')")
    @MyLog(title = "交换机扫描结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchScanResult switchScanResult) {
        return toAjax(switchScanResultService.insertSwitchScanResult(switchScanResult));
    }

    /**
     * 修改交换机扫描结果
     */
    @ApiOperation("修改交换机扫描结果")
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:edit')")
    @MyLog(title = "交换机扫描结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchScanResult switchScanResult) {
        return toAjax(switchScanResultService.updateSwitchScanResult(switchScanResult));
    }

    /**
     * 删除交换机扫描结果
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:remove')")
    @MyLog(title = "交换机扫描结果", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(switchScanResultService.deleteSwitchScanResultByIds(ids));
    }

    /**
     * 将高级功能扫描结果插入数据库
     *
     * @param switchParameters 交换机参数对象
     * @param hashMap          包含问题信息的HashMap
     * @return 返回插入数据库的行数
     */
    public Long insertSwitchScanResult (SwitchParameters switchParameters, HashMap<String,String> hashMap){
        SwitchScanResult switchScanResult = new SwitchScanResult();

        // 设置交换机IP
        switchScanResult.setSwitchIp(switchParameters.getIp()+":"+switchParameters.getThreadName()); // ip
        // 设置交换机四项基本信息ID
        switchScanResult.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));/*获取交换机四项基本信息ID*/
        // 设置交换机名称
        switchScanResult.setSwitchName(switchParameters.getName()); //name
        // 设置交换机密码
        switchScanResult.setSwitchPassword( EncryptUtil.densificationAndSalt( switchParameters.getPassword() ) ); //password
        // 设置配置密码
        switchScanResult.setConfigureCiphers( switchParameters.getConfigureCiphers() == null ? null :  EncryptUtil.densificationAndSalt(switchParameters.getConfigureCiphers()));
        // 设置登录方式
        switchScanResult.setLoginMethod(switchParameters.getMode());
        // 设置端口号
        switchScanResult.setPortNumber(switchParameters.getPort());
        // 设置问题类型
        switchScanResult.setTypeProblem("运行分析");
        // 设置临时问题名称
        switchScanResult.setTemProName(hashMap.get("ProblemName"));
        // 设置问题名称
        switchScanResult.setProblemName(hashMap.get("ProblemName"));
        // 设置动态信息
        switchScanResult.setDynamicInformation(hashMap.get("parameterString"));
        // 设置是否有问题
        switchScanResult.setIfQuestion(hashMap.get("IfQuestion")); //是否有问题
        // 设置登录用户名
        switchScanResult.setUserName(switchParameters.getLoginUser().getUsername());//登录名称
        // 设置登录手机号
        switchScanResult.setPhonenumber(switchParameters.getLoginUser().getUser().getPhonenumber()); //登录手机号

        // 插入扫描时间
        // 创建扫描时间对象
        DateTime dateTime = new DateTime(switchParameters.getScanningTime(), "yyyy-MM-dd HH:mm:ss");
        // 设置扫描时间
        switchScanResult.setCreateTime(dateTime);

        // 获取ISwitchScanResultService的Bean实例
        //插入问题
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        // 调用insertSwitchScanResult方法插入问题，并获取插入的行数
        int i = switchScanResultService.insertSwitchScanResult(switchScanResult);
        // 返回插入的行数
        return Long.valueOf(i).longValue();
    };

    /**
     * 获取交换机问题扫描结果页数
     *
     * @return 返回交换机问题扫描结果的页数
     */
    @ApiOperation("获取交换机问题扫描结果页数")
    @GetMapping("/getPages")
    public Integer getPages() {
        String username = SecurityUtils.getLoginUser().getUsername();
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        /*获取交换机问题扫描结果不同时间条数*/
        int number = switchScanResultService.selectCountByName(username);
        Integer ceilResult = (int) Math.ceil((double) number / 10); // 获取小数进一
        return ceilResult;
    }


    /**
     * 分页查询历史扫描信息
     *
     * @param pageNumber 当前页码
     * @return 分页查询结果列表
     */
    @ApiOperation("根据当前登录人获取以往扫描信息")
    @GetMapping("/getUnresolvedProblemInformationByUserName/{pageNumber}")///{pageNumber}
    public List<ScanResultsCO> getSwitchScanResultListByName(@PathVariable String pageNumber) {//@PathVariable String pageNumber
        /*系统登录人姓名*/
        String userName = SecurityUtils.getLoginUser().getUsername();
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        int page = 10 * (Integer.valueOf(pageNumber).intValue()-1);
        List<SwitchScanResult> list = switchScanResultService.selectSwitchScanResultListPages(userName,page);
        /*如果查询结果为空 则直接返回 null*/
        if (MyUtils.isCollectionEmpty(list)){
            return new ArrayList<>();
        }
        /*交换机扫描结果主键集合*/
        Long[] ids = new Long[list.size()];
        /* 交换机扫描结果 map集合  key为主键ID value为结果实体类*/
        HashMap<Long,SwitchScanResult> hashMap = new HashMap<>();
        for (int num = 0;num <list.size();num++){
            ids[num] = list.get(num).getId();
            hashMap.put(list.get(num).getId(),list.get(num));
        }
        /*根据主键数组 查询 带有格式的实体类 集合*/
        List<SwitchProblemVO> switchProblemList = switchScanResultService.selectSwitchProblemVOListByIds(ids);
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                switchProblemCO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
                SwitchScanResult switchScanResult = hashMap.get(switchProblemCO.getQuestionId());
                //提取信息 如果不为空 则有参数
                if (switchScanResult.getDynamicInformation()!=null && !switchScanResult.getDynamicInformation().equals("")){
                    String dynamicInformation = switchScanResult.getDynamicInformation();
                    //几个参数中间的 参数是 以  "=:=" 来分割的
                    //设备型号=:=是=:=S3600-28P-EI=:=设备品牌=:=是=:=H3C=:=内部固件版本=:=是=:=3.10,=:=子版本号=:=是=:=1510P09=:=
                    /*自定义分隔符*/
                    String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

                    String[] dynamicInformationsplit = dynamicInformation.split(customDelimiter);
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
        }
        //将IP地址去重放入set集合中
        HashSet<String> time_hashSet = new HashSet<>();
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            Date createTime = switchProblemVO.getCreateTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(createTime);
            time_hashSet.add(time);
        }
        List<Date> arr = new ArrayList<Date>();
        for (String time:time_hashSet){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                arr.add(format.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<Date> sort = MyUtils.sort(arr);
        List<String> stringtime = new ArrayList<>();
        for (int number = sort.size()-1;number>=0;number--){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = dateFormat.format(sort.get(number));
            stringtime.add(time);
        }
        List<ScanResultsCO> scanResultsCOList = new ArrayList<>();
        for (String time:stringtime){
            ScanResultsCO scanResultsCO = new ScanResultsCO();
            scanResultsCO.setCreateTime(time);
            scanResultsCOList.add(scanResultsCO);
        }
        HashSet<String> hashSet = new HashSet<>();
        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        for (SwitchProblemVO switchProblemVO:switchProblemList){

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(switchProblemVO.getCreateTime());
            hashSet.add(switchProblemVO.getSwitchIp()+customDelimiter+time);
        }
        List<ScanResultsVO> scanResultsVOPojoList = new ArrayList<>();
        for (String hashString:hashSet){
            String[] split = hashString.split(customDelimiter);
            ScanResultsVO scanResultsVO = new ScanResultsVO();
            scanResultsVO.setSwitchIp(split[0]);
            scanResultsVO.setCreateTime(split[1]);
            scanResultsVOPojoList.add(scanResultsVO);
        }
        for (ScanResultsVO scanResultsVO:scanResultsVOPojoList){
            List<SwitchProblemVO> pojoList = new ArrayList<>();
            String pinpai = "*";
            String xinghao = "*";
            String banben = "*";
            String zibanben = "*";
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = format.format(switchProblemVO.getCreateTime());
                if (scanResultsVO.getSwitchIp().equals(switchProblemVO.getSwitchIp())
                        && scanResultsVO.getCreateTime().equals(time)){
                    String brand = switchProblemVO.getBrand();
                    if (!(brand .equals("*"))){
                        pinpai = brand;
                    }
                    String switchType = switchProblemVO.getSwitchType();
                    if (!(switchType .equals("*"))){
                        xinghao = switchType;
                    }
                    String firewareVersion = switchProblemVO.getFirewareVersion();
                    if (!(firewareVersion .equals("*"))){
                        banben = firewareVersion;
                    }
                    String subVersion = switchProblemVO.getSubVersion();
                    if (subVersion !=null && !(subVersion .equals("*"))){
                        zibanben = subVersion;
                    }
                    pojoList.add(switchProblemVO);
                }
            }
            scanResultsVO.setSwitchIp(scanResultsVO.getSwitchIp());
            scanResultsVO.setShowBasicInfo("("+pinpai+" "+xinghao+" "+banben+" "+zibanben+")");
            scanResultsVO.setSwitchProblemVOList(pojoList);
        }
        for (ScanResultsCO scanResultsCO:scanResultsCOList){
            List<ScanResultsVO> scanResultsVOList = new ArrayList<>();
            for (ScanResultsVO scanResultsVO:scanResultsVOPojoList){
                if (scanResultsCO.getCreateTime().equals(scanResultsVO.getCreateTime())){
                    scanResultsVOList.add(scanResultsVO);
                }
            }
            scanResultsCO.setScanResultsVOList(scanResultsVOList);
        }
        for (ScanResultsCO scanResultsCO:scanResultsCOList){
            scanResultsCO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            List<ScanResultsVO> scanResultsVOList = scanResultsCO.getScanResultsVOList();
            for (ScanResultsVO scanResultsVO:scanResultsVOList){
                scanResultsVO.setCreateTime(null);
                String switchIp = scanResultsVO.getSwitchIp();
                //String[] split = switchIp.split(":");
                scanResultsVO.setSwitchIp(switchIp);
                scanResultsVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                List<SwitchProblemVO> switchProblemVOList = scanResultsVO.getSwitchProblemVOList();
                for (SwitchProblemVO switchProblemVO:switchProblemVOList){
                    switchProblemVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                    switchProblemVO.setSwitchIp(null);
                    switchProblemVO.setCreateTime(null);
                }
            }
        }
        return scanResultsCOList;
    }



    /**
     * 更新登录信息
     *
     * @author charles
     * @createTime 2024/1/19 16:18
     * @param switchInformations 包含交换机登录信息的字符串列表
     * @return 无返回值
     */
    @PutMapping("/updateLoginInformation")
    public void updateLoginInformation(@RequestBody List<String> switchInformations) {
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchScanResult> switchScanResults = new ArrayList<>();

        for (String information:switchInformations){
            // 交换机登录信息 转化为 实体类
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);

            SwitchScanResult switchScanResult = new SwitchScanResult();
            // 设置交换机的IP地址
            switchScanResult.setSwitchIp(switchLoginInformation.getIp());
            // 设置交换机的名称
            switchScanResult.setSwitchName(switchLoginInformation.getName());
            // 先解密前端加密的密码，再进行加密处理
            String password = RSAUtils.decryptFrontEndCiphertext(switchLoginInformation.getPassword());
            // 设置交换机的密码，先加密处理
            switchScanResult.setSwitchPassword(EncryptUtil.densificationAndSalt(password));/*EncryptUtil.densificationAndSalt( switchParameters.getPassword() )*/

            if (switchLoginInformation.getConfigureCiphers() != null){
                // 解密前端加密的配置密文
                String ConfigureCiphers = RSAUtils.decryptFrontEndCiphertext(switchLoginInformation.getConfigureCiphers());
                // 设置交换机的配置密文，如果解密后的结果为null，则设置为null，否则进行加密处理
                switchScanResult.setConfigureCiphers(ConfigureCiphers == null? null : EncryptUtil.densificationAndSalt(ConfigureCiphers));
            }

            // 将处理后的交换机扫描结果添加到列表中
            switchScanResults.add(switchScanResult);
        }

        // 获取ISwitchScanResultService接口的实例
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        // 遍历交换机扫描结果列表，更新登录信息
        for (SwitchScanResult scanResult:switchScanResults){
            // 调用服务层方法更新登录信息，并获取更新结果
            int i = switchScanResultService.updateLoginInformationByIP(scanResult);
        }
    }
}
