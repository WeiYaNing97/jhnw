package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题扫描逻辑对象 problem_scan_logic
 * @author ruoyi
 * @date 2022-02-14
 */
public class ProblemScanLogic extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键索引 */
    @Excel(name = "主键")
    private String id;

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

    /** true下一条分析索引 */
    @Excel(name = "true下一条分析索引")
    private String tNextId;

    /** true下一条命令索引 */
    @Excel(name = "true下一条命令索引")
    private String tComId;

    /** 问题索引 */
    @Excel(name = "问题索引")
    private String problemId;


    /** false行号 */
    @Excel(name = "false行号")
    private String fLine;
    /** true行号 */
    @Excel(name = "true行号")
    private String tLine;

    /** false下一条分析索引 */
    @Excel(name = "false下一条分析索引")
    private String fNextId;

    /** false下一条命令索引 */
    @Excel(name = "false下一条命令索引")
    private String fComId;

    /** 返回命令 */
    @Excel(name = "返回命令")
    private Long returnCmdId;

    /** 循环起始ID */
    @Excel(name = "循环起始索引")
    private String cycleStartId;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        this.matched = matched;
    }

    public String getRelativePosition() {
        return relativePosition;
    }

    public void setRelativePosition(String relativePosition) {
        this.relativePosition = relativePosition;
    }

    public String getMatchContent() {
        return matchContent;
    }

    public void setMatchContent(String matchContent) {
        this.matchContent = matchContent;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getrPosition() {
        return rPosition;
    }

    public void setrPosition(Integer rPosition) {
        this.rPosition = rPosition;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getExhibit() {
        return exhibit;
    }

    public void setExhibit(String exhibit) {
        this.exhibit = exhibit;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getCompare() {
        return compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    public String gettNextId() {
        return tNextId;
    }

    public void settNextId(String tNextId) {
        this.tNextId = tNextId;
    }

    public String gettComId() {
        return tComId;
    }

    public void settComId(String tComId) {
        this.tComId = tComId;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getfLine() {
        return fLine;
    }

    public void setfLine(String fLine) {
        this.fLine = fLine;
    }

    public String gettLine() {
        return tLine;
    }

    public void settLine(String tLine) {
        this.tLine = tLine;
    }

    public String getfNextId() {
        return fNextId;
    }

    public void setfNextId(String fNextId) {
        this.fNextId = fNextId;
    }

    public String getfComId() {
        return fComId;
    }

    public void setfComId(String fComId) {
        this.fComId = fComId;
    }

    public Long getReturnCmdId() {
        return returnCmdId;
    }

    public void setReturnCmdId(Long returnCmdId) {
        this.returnCmdId = returnCmdId;
    }

    public String getCycleStartId() {
        return cycleStartId;
    }

    public void setCycleStartId(String cycleStartId) {
        this.cycleStartId = cycleStartId;
    }

    @Override
    public String toString() {
        return "ProblemScanLogic{" +
                "id='" + id + '\'' +
                ", matched='" + matched + '\'' +
                ", relativePosition='" + relativePosition + '\'' +
                ", matchContent='" + matchContent + '\'' +
                ", action='" + action + '\'' +
                ", rPosition=" + rPosition +
                ", length='" + length + '\'' +
                ", exhibit='" + exhibit + '\'' +
                ", wordName='" + wordName + '\'' +
                ", compare='" + compare + '\'' +
                ", tNextId='" + tNextId + '\'' +
                ", tComId='" + tComId + '\'' +
                ", problemId='" + problemId + '\'' +
                ", fLine='" + fLine + '\'' +
                ", tLine='" + tLine + '\'' +
                ", fNextId='" + fNextId + '\'' +
                ", fComId='" + fComId + '\'' +
                ", returnCmdId=" + returnCmdId +
                ", cycleStartId='" + cycleStartId + '\'' +
                '}';
    }
}
