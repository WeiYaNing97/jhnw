package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题扫描逻辑对象 problem_scan_logic
 * 
 * @author ruoyi
 * @date 2021-12-20
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String gettProblemId() {
        return tProblemId;
    }

    public void settProblemId(String tProblemId) {
        this.tProblemId = tProblemId;
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

    public String getfProblemId() {
        return fProblemId;
    }

    public void setfProblemId(String fProblemId) {
        this.fProblemId = fProblemId;
    }

    public Long getReturnCmdId() {
        return returnCmdId;
    }

    public void setReturnCmdId(Long returnCmdId) {
        this.returnCmdId = returnCmdId;
    }

    @Override
    public String toString() {
        return "ProblemScanLogic{" +
                "id=" + id +
                ", logic='" + logic + '\'' +
                ", matched='" + matched + '\'' +
                ", relativePosition=" + relativePosition +
                ", matchContent='" + matchContent + '\'' +
                ", action='" + action + '\'' +
                ", rPosition=" + rPosition +
                ", length='" + length + '\'' +
                ", exhibit='" + exhibit + '\'' +
                ", wordName='" + wordName + '\'' +
                ", compare='" + compare + '\'' +
                ", content='" + content + '\'' +
                ", tNextId='" + tNextId + '\'' +
                ", tComId='" + tComId + '\'' +
                ", tProblemId='" + tProblemId + '\'' +
                ", fNextId='" + fNextId + '\'' +
                ", fComId='" + fComId + '\'' +
                ", fProblemId='" + fProblemId + '\'' +
                ", returnCmdId=" + returnCmdId +
                '}';
    }
}
