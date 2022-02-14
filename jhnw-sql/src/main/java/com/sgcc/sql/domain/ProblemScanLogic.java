package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题扫描逻辑对象 problem_scan_logic
 * 
 * @author ruoyi
 * @date 2022-02-14
 */
public class ProblemScanLogic extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键索引 */
    private Long id;

    /** 逻辑 */
    @Excel(name = "逻辑")
    private String logic;

    /** 匹配 */
    @Excel(name = "匹配")
    private String matched;

    /** 相对位置 */
    @Excel(name = "相对位置")
    private String relativePosition;

    /** 匹配内容 */
    @Excel(name = "匹配内容")
    private String matchContent;

    /** 动作 */
    @Excel(name = "动作")
    private String action;

    /** 位置 */
    @Excel(name = "位置")
    private Integer rPosition;

    /** 长度 */
    @Excel(name = "长度")
    private String length;

    /** 是否显示 */
    @Excel(name = "是否显示")
    private String exhibit;

    /** 取词名称 */
    @Excel(name = "取词名称")
    private String wordName;

    /** 比较 */
    @Excel(name = "比较")
    private String compare;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** true下一条分析索引 */
    @Excel(name = "true下一条分析索引")
    private String tNextId;

    /** true下一条命令索引 */
    @Excel(name = "true下一条命令索引")
    private String tComId;

    /** true问题索引 */
    @Excel(name = "true问题索引")
    private String tProblemId;

    /** false下一条分析索引 */
    @Excel(name = "false下一条分析索引")
    private String fNextId;

    /** false下一条命令索引 */
    @Excel(name = "false下一条命令索引")
    private String fComId;

    /** false问题索引 */
    @Excel(name = "false问题索引")
    private String fProblemId;

    /** 返回命令 */
    @Excel(name = "返回命令")
    private Long returnCmdId;

    /** 循环起始ID */
    @Excel(name = "循环起始ID")
    private Long cycleStartId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setLogic(String logic) 
    {
        this.logic = logic;
    }

    public String getLogic() 
    {
        return logic;
    }
    public void setMatched(String matched) 
    {
        this.matched = matched;
    }

    public String getMatched() 
    {
        return matched;
    }
    public void setRelativePosition(String relativePosition) 
    {
        this.relativePosition = relativePosition;
    }

    public String getRelativePosition() 
    {
        return relativePosition;
    }
    public void setMatchContent(String matchContent) 
    {
        this.matchContent = matchContent;
    }

    public String getMatchContent() 
    {
        return matchContent;
    }
    public void setAction(String action) 
    {
        this.action = action;
    }

    public String getAction() 
    {
        return action;
    }
    public void setrPosition(Integer rPosition) 
    {
        this.rPosition = rPosition;
    }

    public Integer getrPosition() 
    {
        return rPosition;
    }
    public void setLength(String length) 
    {
        this.length = length;
    }

    public String getLength() 
    {
        return length;
    }
    public void setExhibit(String exhibit) 
    {
        this.exhibit = exhibit;
    }

    public String getExhibit() 
    {
        return exhibit;
    }
    public void setWordName(String wordName) 
    {
        this.wordName = wordName;
    }

    public String getWordName() 
    {
        return wordName;
    }
    public void setCompare(String compare) 
    {
        this.compare = compare;
    }

    public String getCompare() 
    {
        return compare;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void settNextId(String tNextId) 
    {
        this.tNextId = tNextId;
    }

    public String gettNextId() 
    {
        return tNextId;
    }
    public void settComId(String tComId) 
    {
        this.tComId = tComId;
    }

    public String gettComId() 
    {
        return tComId;
    }
    public void settProblemId(String tProblemId) 
    {
        this.tProblemId = tProblemId;
    }

    public String gettProblemId() 
    {
        return tProblemId;
    }
    public void setfNextId(String fNextId) 
    {
        this.fNextId = fNextId;
    }

    public String getfNextId() 
    {
        return fNextId;
    }
    public void setfComId(String fComId) 
    {
        this.fComId = fComId;
    }

    public String getfComId() 
    {
        return fComId;
    }
    public void setfProblemId(String fProblemId) 
    {
        this.fProblemId = fProblemId;
    }

    public String getfProblemId() 
    {
        return fProblemId;
    }
    public void setReturnCmdId(Long returnCmdId) 
    {
        this.returnCmdId = returnCmdId;
    }

    public Long getReturnCmdId() 
    {
        return returnCmdId;
    }
    public void setCycleStartId(Long cycleStartId) 
    {
        this.cycleStartId = cycleStartId;
    }

    public Long getCycleStartId() 
    {
        return cycleStartId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("logic", getLogic())
            .append("matched", getMatched())
            .append("relativePosition", getRelativePosition())
            .append("matchContent", getMatchContent())
            .append("action", getAction())
            .append("rPosition", getrPosition())
            .append("length", getLength())
            .append("exhibit", getExhibit())
            .append("wordName", getWordName())
            .append("compare", getCompare())
            .append("content", getContent())
            .append("tNextId", gettNextId())
            .append("tComId", gettComId())
            .append("tProblemId", gettProblemId())
            .append("fNextId", getfNextId())
            .append("fComId", getfComId())
            .append("fProblemId", getfProblemId())
            .append("returnCmdId", getReturnCmdId())
            .append("cycleStartId", getCycleStartId())
            .toString();
    }
}
