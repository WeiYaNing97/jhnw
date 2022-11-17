package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.annotation.Log;
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
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.domain.TotalQuestionTableCO;
import com.sgcc.sql.domain.TotalQuestionTableVO;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.system.service.ISysUserService;
import com.sgcc.web.controller.util.PathHelper;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 问题及命令Controller
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/sql/total_question_table")
public class TotalQuestionTableController extends BaseController
{
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;

    @Autowired
    private ISysUserService userService;


    /**
     * @method: 根据交换机信息查询 扫描问题的 命令ID
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping(value = "/commandIdByInformation")
    public List<String> commandIdByInformation(String brand,
                                               String type,
                                               String firewareVersion,
                                               String subversionNumber)
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();

        totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);
        totalQuestionTable.setSubVersion(subversionNumber);

        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTables!=null){
            List<String> longList = new ArrayList<>();
            for (TotalQuestionTable pojo:totalQuestionTables){
                longList.add(pojo.getCommandId());
            }
            //this.longList=longList;
            return longList;
        }else {
            return null;
        }
    }


    /**
     *
     * a
     *
     * 查询交换机问题列表PojoList
     */
    @RequestMapping("/selectPojoList")
    public List<TotalQuestionTable> selectPojoList(@RequestBody TotalQuestionTable totalQuestionTable)
    {

        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        return list;
    }

    /**
     * 查询问题及命令列表 实体类ID
     */
    @RequestMapping("/select")
    public Long select(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        return list.get(0).getId();
    }


    /**
     * 导出问题及命令列表
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:export')")
    @Log(title = "问题及命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TotalQuestionTable totalQuestionTable)
    {
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        ExcelUtil<TotalQuestionTable> util = new ExcelUtil<TotalQuestionTable>(TotalQuestionTable.class);
        return util.exportExcel(list, "问题及命令数据");
    }

    /**
     *
     * a
     *
     * 获取交换机问题的详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(totalQuestionTableService.selectTotalQuestionTableById(id));
    }

    /**
     * 修改问题及命令
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:edit')")
    @Log(title = "问题及命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        return toAjax(totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable));
    }

    /**
     * 修改问题及命令 的 循环
     */
    public AjaxResult updateTotalQuestionTable(Long id)
    {
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(id);
        return toAjax(totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable));
    }

    /**
     * 删除问题及命令
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:remove')")
    @Log(title = "问题及命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(totalQuestionTableService.deleteTotalQuestionTableByIds(ids));
    }

    /**
     * 根据ID数组查询集合
     */
    //@Log(title = "根据ID数组查询集合", businessType = BusinessType.DELETE)
    @RequestMapping("/query/{ids}")//{ids}
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
    @RequestMapping("/judgeSuperAdministrator")
    public boolean judgeSuperAdministrator() {
        LoginUser login = SecurityUtils.getLoginUser();
        Long userId = login.getUserId();
        if (userId == 1l){
            return true;
        }else {
            SysUser sysUser = userService.selectUserById(userId);
            List<SysRole> roles = sysUser.getRoles();
            for (SysRole role:roles){
                if (role.getRoleId() == 1l){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     *
     * a
     *
     *
     * 新增问题
     * @param totalQuestionTable
     * @return
     */
    @RequestMapping("add")
    @MyLog(title = "新增问题", businessType = BusinessType.INSERT)
    public AjaxResult add(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        TotalQuestionTable pojo = new TotalQuestionTable();
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
        pojo.setBrand(brand);
        pojo.setType(type);
        pojo.setFirewareVersion(firewareVersion);
        pojo.setSubVersion(subVersion);
        pojo.setTypeProblem(typeProblem);
        pojo.setTemProName(temProName);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableListInsert(pojo);
        if (totalQuestionTables.size() != 0){

            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"交换机问题已存在\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"交换机问题已存在\r\n"
                        +"方法com.sgcc.web.controller.sql.total_question_table.add");
            } catch ( IOException e) {
                e.printStackTrace();
            }

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
                WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"SQL唯一约束异常,问题已存在\r\n");
                try {
                    //插入问题简述及问题路径
                    PathHelper.writeDataToFile("风险："+"SQL唯一约束异常,问题已存在\r\n"
                            +"方法com.sgcc.web.controller.sql.total_question_table.add");
                } catch ( IOException e1) {
                    e1.printStackTrace();
                }
                //返回成功
                return  AjaxResult.error("SQL唯一约束异常,问题已存在");
            }
        }
        if (insert <= 0){
            return AjaxResult.error();
        }
        return AjaxResult.success(totalQuestionTable.getId()+"");
    }

    /**
     * @method: 查询所有品牌
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/brandlist")
    public List<String> brandlist()
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTablebrandList(totalQuestionTable);
        if (typeProblemlist.size() == 0){
            return null;
        }
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getBrand());
        }
        return stringList;
    }

    /**
     * @method: 根据所选品牌 查询所有型号
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/typelist")
    public List<String> typelist(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTabletypelist(totalQuestionTable);
        if (typeProblemlist.size() == 0){
            return null;
        }
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getType());
        }
        return stringList;
    }

    /**
     * @method: 根据所选品牌、型号   查询所有内部固件版本
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/firewareVersionlist")
    public List<String> firewareVersionlist(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTablefirewareVersionlist(totalQuestionTable);
        if (typeProblemlist.size() == 0){
        return null;
        }
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getFirewareVersion());
        }
        return stringList;
    }

    /**
     * @method: 根据所选品牌、型号、内部固件版本   查询所有子版本号
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/subVersionlist")
    public List<String> subVersionlist(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTablesubVersionlist(totalQuestionTable);
        if (typeProblemlist.size() == 0){
        return null;
        }
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getSubVersion());
        }
        return stringList;
    }

    /**
    * @method: 查询所有问题种类
    * @Param: [totalQuestionTable]
    * @return: java.util.List<java.lang.String>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("/typeProblemlist")
    public List<String> typeProblemlist()
    {
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTabletypeProblemList(null);
        if (typeProblemlist.size() == 0){
            return null;
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
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/temProNamelist")
    public List<String> temProNamelist(@RequestBody String typeProblem)
    {
        List<String> typeProblemlist = totalQuestionTableService.selectTemProNamelistBytypeProblem(typeProblem);
        if (typeProblemlist.size() == 0){
            return null;
        }
        return typeProblemlist;
    }

    /**
    * @method: 根据问题实体类查询问题名称
    * @Param: [totalQuestionTable]
    * @return: java.util.List<java.lang.String>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("/problemNameList")
    public List<String> problemNameList(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (list.size() == 0){
            return null;
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
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping(value = "/totalQuestionTableId")
    public Long totalQuestionTableId( String brand, String type, String firewareVersion, String subVersion, String problemName, String typeProblem,String temProName)
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);
        totalQuestionTable.setSubVersion(subVersion);
        totalQuestionTable.setProblemName(problemName);
        totalQuestionTable.setTypeProblem(typeProblem);
        totalQuestionTable.setTemProName(temProName);
        totalQuestionTable.setCommandId(null);
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
        String selectCommandId = totalQuestionTable.getCommandId();
        totalQuestionTable.setCommandId(null);
        startPage();
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (list.size() == 0){
            return null;
        }
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        if (selectCommandId.equals("0")){//未定义解决问题命令
            for (TotalQuestionTable pojo:list){
                if (pojo.getCommandId() == null || pojo.getCommandId().equals("")){
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
    @RequestMapping("/fuzzyQueryListByPojo")
    public List<TotalQuestionTableCO> fuzzyQueryListByPojo(@RequestBody TotalQuestionTable totalQuestionTable)//@RequestBody TotalQuestionTable totalQuestionTable
    {
        List<TotalQuestionTable> totalQuestionTableList = totalQuestionTableService.fuzzyTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTableList.size() == 0){
            return null;
        }
        HashSet<String> typeProblemHashSet = new HashSet();
        HashSet<String> temProNameHashSet = new HashSet();

        for (TotalQuestionTable totalQuestion:totalQuestionTableList){
            typeProblemHashSet.add(totalQuestion.getTypeProblem());
            temProNameHashSet.add(totalQuestion.getTypeProblem()+"=:="+totalQuestion.getTemProName());
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
            String[] split = temProName.split("=:=");
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
    @RequestMapping("/fuzzyQueryListByPojoMybatis")
    public List<TotalQuestionTableCO> fuzzyQueryListBymybatis(@RequestBody TotalQuestionTable totalQuestionTable)//@RequestBody TotalQuestionTable totalQuestionTable
    {
        List<TotalQuestionTableVO> totalQuestionTableList = totalQuestionTableService.fuzzyQueryListBymybatis(totalQuestionTable);
        if (totalQuestionTableList.size() == 0){
            return null;
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

}
