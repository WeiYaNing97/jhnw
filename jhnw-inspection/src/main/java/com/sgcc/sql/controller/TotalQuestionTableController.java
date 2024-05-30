package com.sgcc.sql.controller;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.entity.SysRole;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.domain.Information;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.service.IInformationService;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.IFormworkService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

/**
 * 问题及命令Controller
 */
@RestController
@RequestMapping("/sql/total_question_table")
//事务
@Transactional(rollbackFor = Exception.class)
@Api("交换机问题管理")
public class TotalQuestionTableController extends BaseController
{
    @Autowired
    private IFormworkService formworkService;
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IInformationService informationService;
    /**
     * 导出问题及命令列表
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:export')")
    @MyLog(title = "问题及命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TotalQuestionTable totalQuestionTable)
    {
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        ExcelUtil<TotalQuestionTable> util = new ExcelUtil<TotalQuestionTable>(TotalQuestionTable.class);
        return util.exportExcel(list, "问题及命令数据");
    }


    /**
     * @method: 根据交换机信息查询扫描问题的命令ID
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     */
    @ApiOperation("根据交换机信息查询扫描问题的命令ID")
    @GetMapping(value = "/commandIdByInformation")
    public List<String> commandIdByInformation(String brand,String type,String firewareVersion,String subversionNumber) {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);
        totalQuestionTable.setSubVersion(subversionNumber);

        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        List<String> longList = new ArrayList<>();
        if (totalQuestionTables.size() != 0){
            for (TotalQuestionTable pojo:totalQuestionTables){
                longList.add(pojo.getLogicalID());
            }
        }

        return longList;
    }


    /**
     * 查询交换机问题列表PojoList
     */
    @GetMapping("/selectPojoList")
    @ApiOperation("查询交换机问题列表")
    public List<TotalQuestionTable> selectPojoList(TotalQuestionTable totalQuestionTable)
    {
        /** 品牌 型号 版本 子版本 */
        if (totalQuestionTable.getBrand()!=null){
            totalQuestionTable.setBrand(totalQuestionTable.getBrand().equals("")?null:totalQuestionTable.getBrand());
        }
        if (totalQuestionTable.getType()!=null){
            totalQuestionTable.setType(totalQuestionTable.getType().equals("")?null:totalQuestionTable.getType());
        }
        if (totalQuestionTable.getFirewareVersion()!=null){
            totalQuestionTable.setFirewareVersion(totalQuestionTable.getFirewareVersion().equals("")?null:totalQuestionTable.getFirewareVersion());
        }
        if (totalQuestionTable.getSubVersion()!=null){
            totalQuestionTable.setSubVersion(totalQuestionTable.getSubVersion().equals("")?null:totalQuestionTable.getSubVersion());
        }
        /** 分类 名称 自定义名称*/
        if (totalQuestionTable.getTemProName()!=null){
            totalQuestionTable.setTemProName(totalQuestionTable.getTemProName().equals("")?null:totalQuestionTable.getTemProName());
        }
        if (totalQuestionTable.getTypeProblem()!=null){
            totalQuestionTable.setTypeProblem(totalQuestionTable.getTypeProblem().equals("")?null:totalQuestionTable.getTypeProblem());
        }
        if (totalQuestionTable.getProblemName()!=null){
            totalQuestionTable.setProblemName(totalQuestionTable.getProblemName().equals("")?null:totalQuestionTable.getProblemName());
        }

        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);

        /** 交换机问题回显 右侧显示。
         * 不记得为什么要显示只有 品牌型号的数据 所以先注销*/
        /*Information information = new Information();
        information.setDeviceBrand(totalQuestionTable.getBrand().equals("")?null:totalQuestionTable.getBrand());
        information.setDeviceModel(totalQuestionTable.getType().equals("")?null:totalQuestionTable.getType());
        List<Information> informationlist = informationService.selectInformationList(information);

        if (informationlist.size()!=0){
            for (Information pojo:informationlist){
                TotalQuestionTable totalQuestionTablePjo = new TotalQuestionTable();
                totalQuestionTablePjo.setBrand(pojo.getDeviceBrand());
                totalQuestionTablePjo.setType(pojo.getDeviceModel());
                list.add(totalQuestionTablePjo);
            }
        }*/

        return list;
    }

    /**
     * 查询问题及命令列表 实体类ID
     */
    @ApiOperation("查询交换机问题列表忽略扫描索引CommandId")
    @GetMapping("/select")
    public String select(TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setLogicalID(null);

        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);

        return list.get(0).getId();
    }


    /**
     * 获取交换机问题的详细信息
     */
    @ApiOperation("根据ID获取交换机问题的详细信息")
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(totalQuestionTableService.selectTotalQuestionTableById(id));
    }

    /**
     * 修改问题及命令
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:edit')")
    @MyLog(title = "问题及命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        return toAjax(totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable));
    }

    /**
     * 修改问题及命令 的 循环
     */
    public AjaxResult updateTotalQuestionTable(String id)
    {
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(id);
        return toAjax(totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable));
    }

    /**
     * 根据ID数组查询集合
     */
    //@Log(title = "根据ID数组查询集合", businessType = BusinessType.DELETE)
    @GetMapping("/query/{ids}")//{ids}
    @ApiOperation("根据ID数组查询交换机问题集合")
    public List<TotalQuestionTable> query(@PathVariable Long[] ids)//@PathVariable Long[] ids
    {
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        if (ids.length == 0){
            return totalQuestionTables;
        }
        totalQuestionTables =totalQuestionTableService.selectTotalQuestionTableByIds(ids);
        return totalQuestionTables;
    }

    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/

    /*判断是否为 超级管理员*/
    @GetMapping("/judgeSuperAdministrator")
    @ApiOperation("判断是否为超级管理员")
    public boolean judgeSuperAdministrator() {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        if (userId == 1L){
            return true;
        }else {
            SysUser sysUser = userService.selectUserById(userId);
            List<SysRole> roles = sysUser.getRoles();
            for (SysRole role:roles){
                if (role.getRoleId() == 1L){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 新增问题
     * @param totalQuestionTable
     * @return
     */
    @ApiOperation("新增问题")
    @PostMapping("add")
    @MyLog(title = "新增问题", businessType = BusinessType.INSERT)
    public AjaxResult add(@RequestBody TotalQuestionTable totalQuestionTable) {
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        String brand = null;
        if (!(totalQuestionTable.getBrand().equals(""))){
            brand = totalQuestionTable.getBrand();
        }
        String type = null;
        if (!(totalQuestionTable.getType().equals(""))){
            type = totalQuestionTable.getType();
        }else {type = "*";}
        String firewareVersion = null;
        if (!(totalQuestionTable.getFirewareVersion().equals(""))){
            firewareVersion = totalQuestionTable.getFirewareVersion();
        }else {firewareVersion = "*";}
        String subVersion = null;
        if (!(totalQuestionTable.getSubVersion().equals(""))){
            subVersion = totalQuestionTable.getSubVersion();
        }else {subVersion = "*";}
        String typeProblem = null;
        if (!(totalQuestionTable.getTypeProblem().equals(""))){
            typeProblem = totalQuestionTable.getTypeProblem();
        }
        String temProName = null;
        if (!(totalQuestionTable.getTemProName().equals(""))){
            temProName = totalQuestionTable.getTemProName();
        }

        //先根据六个条件 查询 是否存在 如果存在 则 返回错误 问题已存在
        TotalQuestionTable pojo = new TotalQuestionTable();
        pojo.setBrand(brand);
        pojo.setType(type);
        pojo.setFirewareVersion(firewareVersion);
        pojo.setSubVersion(subVersion);
        pojo.setTypeProblem(typeProblem);/*范式种类*/
        pojo.setTemProName(temProName);/*范式名称*/
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableListInsert(pojo);

        if (totalQuestionTables.size() != 0){
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "风险:交换机问题已存在。\r\n");

            return  AjaxResult.error("问题已存在");
        }

        int insert = 0;
        try{
            if (totalQuestionTable.getType().equals("")){
                totalQuestionTable.setType("*");
            }
            if (totalQuestionTable.getFirewareVersion().equals("")){
                totalQuestionTable.setFirewareVersion("*");
            }
            if (totalQuestionTable.getSubVersion().equals("")){
                totalQuestionTable.setSubVersion("*");
            }
            insert = totalQuestionTableService.insertTotalQuestionTable(totalQuestionTable);
        }catch (Exception e){
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {

                //传输登陆人姓名 及问题简述
                AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                        "风险:"+"SQL唯一约束异常,问题已存在\r\n");

                //返回成功
                return  AjaxResult.error("SQL唯一约束异常,问题已存在");
            }
        }

        if (insert <= 0){
            return AjaxResult.error();
        }

        Information information = new Information();
        information.setDeviceBrand(totalQuestionTable.getBrand());
        information.setDeviceModel(totalQuestionTable.getType());
        List<Information> informationlist = informationService.selectInformationList(information);

        if (informationlist.size() == 0){
            int i = informationService.insertInformation(information);
            if (i>0){

            }else {
                AjaxResult.error("交换机信息表同步失败");
            }
        }

        return AjaxResult.success(totalQuestionTable.getId()+"");
    }


    /**
    * @method: 查询所有问题种类
    * @Param: [totalQuestionTable]
    * @return: java.util.List<java.lang.String>
    */
    @GetMapping("/typeProblemlist")
    @ApiOperation("查询所有问题种类")
    public List<String> typeProblemlist()
    {
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTabletypeProblemList(null);
        if (typeProblemlist.size() == 0){
            return new ArrayList<>();
        }
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getTypeProblem());
        }
        return stringList;
    }

    /**
     * @method: 根据问题种类查询范本问题名称
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     */
    @GetMapping("/temProNamelist")
    @ApiOperation("根据问题种类查询范本问题名称")
    public List<String> temProNamelist(String typeProblem)
    {
        return totalQuestionTableService.selectTemProNamelistBytypeProblem(typeProblem);
    }

    /**
    * @method: 根据问题实体类查询问题名称
    * @Param: [totalQuestionTable]
    * @return: java.util.List<java.lang.String>
    */
    @GetMapping("/problemNameList")
    @ApiOperation("根据问题实体类查询问题名称")
    public List<String> problemNameList(TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setLogicalID(null);
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (list.size() == 0){
            return new ArrayList<>();
        }
        List<String> totalQuestionTables = new ArrayList<>();
        for (TotalQuestionTable pojo:list){
            totalQuestionTables.add(pojo.getProblemName());
        }
        return totalQuestionTables;
    }

    /**
     * @method: 获取 解决问题 命令ID
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     */
    @GetMapping(value = "/totalQuestionTableId")
    public String totalQuestionTableId( String brand, String type, String firewareVersion, String subVersion, String problemName, String typeProblem,String temProName)
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);
        totalQuestionTable.setSubVersion(subVersion);
        totalQuestionTable.setProblemName(problemName);
        totalQuestionTable.setTypeProblem(typeProblem);
        totalQuestionTable.setTemProName(temProName);
        totalQuestionTable.setLogicalID(null);

        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTables.size() != 0){
            return totalQuestionTables.get(0).getId();
        }

        return null;
    }

    /**
     * 查询问题及命令列表
     */
    @RequestMapping("/list")
    @MyLog(title = "查询问题及命令列表", businessType = BusinessType.OTHER)
    public TableDataInfo list(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        String logicalID = totalQuestionTable.getLogicalID();
        totalQuestionTable.setLogicalID(null);
        startPage();
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (list.size() == 0){
            return getDataTable(new ArrayList<>());
        }
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        if (logicalID.equals("0")){//未定义解决问题命令
            for (TotalQuestionTable pojo:list){
                if (pojo.getLogicalID() == null || pojo.getLogicalID().equals("")){
                    totalQuestionTables.add(pojo);
                }
            }
            return getDataTable(totalQuestionTables);
        }
        return getDataTable(list);
    }

    /**
     * 根据实体类 模糊查询 实体类集合
     */
    @GetMapping("/fuzzyQueryListByPojo")
    @ApiOperation("根据实体类模糊查询 实体类集合")
    public List<TotalQuestionTableCO> fuzzyQueryListByPojo(TotalQuestionTable totalQuestionTable)
    {
        List<TotalQuestionTable> totalQuestionTableList = totalQuestionTableService.fuzzyTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTableList.size() == 0){
            return new ArrayList<>();
        }

        HashSet<String> typeProblemHashSet = new HashSet();
        HashSet<String> temProNameHashSet = new HashSet();

        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

        for (TotalQuestionTable totalQuestion:totalQuestionTableList){
            typeProblemHashSet.add(totalQuestion.getTypeProblem());
            temProNameHashSet.add(totalQuestion.getTypeProblem()+ customDelimiter +totalQuestion.getTemProName());
        }

        List<TotalQuestionTableCO> pojoCOList = new ArrayList<>();
        List<TotalQuestionTableVO> pojoVOList = new ArrayList<>();
        for (String typeProblem:typeProblemHashSet){
            TotalQuestionTableCO totalQuestionTableCO = new TotalQuestionTableCO();
            totalQuestionTableCO.setTypeProblem(typeProblem);
            pojoCOList.add(totalQuestionTableCO);
        }

        for (String temProName:temProNameHashSet){
            TotalQuestionTableVO totalQuestionTableVO = new TotalQuestionTableVO();
            /*自定义分隔符*/
            String[] split = temProName.split(customDelimiter );
            totalQuestionTableVO.setTypeProblem(split[0]);
            totalQuestionTableVO.setTemProName(split[1]);
            pojoVOList.add(totalQuestionTableVO);
        }
        for (TotalQuestionTableCO totalQuestionTableCO:pojoCOList){
            List<TotalQuestionTableVO> totalQuestionTableVOList = new ArrayList<>();
            for (TotalQuestionTableVO totalQuestionTableVO:pojoVOList){
                if (totalQuestionTableCO.getTypeProblem().equals(totalQuestionTableVO.getTypeProblem())){
                    totalQuestionTableVOList.add(totalQuestionTableVO);
                }
            }
            totalQuestionTableCO.setTotalQuestionTableVOList(totalQuestionTableVOList);
        }
        for (TotalQuestionTableVO totalQuestionTableVO:pojoVOList){
            List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
            for (TotalQuestionTable totalQuestion:totalQuestionTableList){
                if (totalQuestion.getTypeProblem().equals(totalQuestionTableVO.getTypeProblem())
                        && totalQuestion.getTemProName().equals(totalQuestionTableVO.getTemProName())){
                    totalQuestionTables.add(totalQuestion);
                }
            }
            totalQuestionTableVO.setTotalQuestionTableList(totalQuestionTables);
        }
        return pojoCOList;
    }

    /**
     * 根据实体类 模糊查询 实体类集合 fuzzyQueryListBymybatis
     */
    @ApiOperation("根据实体类模糊查询实体类集合")
    @GetMapping("/fuzzyQueryListByPojoMybatis")
    public List<TotalQuestionTableCO> fuzzyQueryListBymybatis(TotalQuestionTable totalQuestionTable)//@RequestBody TotalQuestionTable totalQuestionTable
    {
        List<TotalQuestionTableVO> totalQuestionTableList = totalQuestionTableService.fuzzyQueryListBymybatis(totalQuestionTable);
        if (totalQuestionTableList.size() == 0){
            return new ArrayList<>();
        }

        HashSet<String> typeProblemHashSet = new HashSet();
        for (TotalQuestionTableVO totalQuestionTableVO:totalQuestionTableList){
            typeProblemHashSet.add(totalQuestionTableVO.getTypeProblem());
        }
        List<TotalQuestionTableCO> totalQuestionTableCOList = new ArrayList<>();
        for (String typeProblem:typeProblemHashSet){
            TotalQuestionTableCO totalQuestionTableCO = new TotalQuestionTableCO();
            totalQuestionTableCO.setTypeProblem(typeProblem);
            totalQuestionTableCOList.add(totalQuestionTableCO);
        }
        for (TotalQuestionTableCO totalQuestionTableCO:totalQuestionTableCOList){
            List<TotalQuestionTableVO> totalQuestionTableVOList = new ArrayList<>();
            for (TotalQuestionTableVO totalQuestionTableVO:totalQuestionTableList){
                if (totalQuestionTableVO.getTypeProblem().equals(totalQuestionTableCO.getTypeProblem())){
                    totalQuestionTableVOList.add(totalQuestionTableVO);
                }
            }
            totalQuestionTableCO.setTotalQuestionTableVOList(totalQuestionTableVOList);
        }
        return totalQuestionTableCOList;
    }


    /**
     * 删除交换机问题
     */
    @ApiOperation("删除交换机问题")
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:remove')")
    @MyLog(title = "删除交换机问题", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteTotalQuestionTable")
    public AjaxResult deleteTotalQuestionTable(@RequestBody String id)
    {
        List<String> formworks = formworkService.selectFormworkByLikeFormworkIndex(id);
        if (formworks.size() != 0){
            return AjaxResult.error("删除失败,该问题加入模板",formworks);
        }

        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(id);
        if (totalQuestionTable.getProblemSolvingId() != null){
            CommandLogicController commandLogicController = new CommandLogicController();
            boolean command = commandLogicController.deleteProblemSolvingCommand(id);
            if (!command){
                return AjaxResult.error();
            }
        }
        if (totalQuestionTable.getLogicalID() != null){
            DefinitionProblemController definitionProblemController = new DefinitionProblemController();
            boolean scan = definitionProblemController.deleteScanningLogic(id);
            if (!scan){
                return AjaxResult.error();
            }
        }
        int i = totalQuestionTableService.deleteTotalQuestionTableById(id);
        if (i<=0){
            return AjaxResult.error();
        }
        return AjaxResult.success();
    }
}
