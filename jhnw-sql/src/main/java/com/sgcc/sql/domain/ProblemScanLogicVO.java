package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题扫描逻辑对象 problem_scan_logic
 * 
 * @author ruoyi
 * @date 2022-02-14
 */
public class ProblemScanLogicVO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键索引 */
    private String onlyIndex;

    /** 成功失败 */
    @Excel(name = "成功失败")
    private String trueFalse;

    /** 匹配 */
    @Excel(name = "匹配")
    private String matched;

    /** 相对位置行 */
    @Excel(name = "相对位置行")
    private String relative;

    /** 相对位置列 */
    @Excel(name = "相对位置列")
    private String position;

    /** 匹配内容 */
    @Excel(name = "匹配内容")
    private String matchContent;

    /** 动作 */
    @Excel(name = "动作")
    private String action;

    /** 动作 */
    @Excel(name = "动作")
    private String tNextId;

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

    /**下一条索引 */
    @Excel(name = "下一条索引")
    private String nextIndex;

    /** 问题索引 */
    @Excel(name = "问题索引")
    private String problemId;

    /** 循环 */
    @Excel(name = "循环")
    private String cycleStartId;

    /** 行号 */
    @Excel(name = "行号")
    private String pageIndex;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOnlyIndex() {
        return onlyIndex;
    }

    public void setOnlyIndex(String onlyIndex) {
        this.onlyIndex = onlyIndex;
    }

    public String getTrueFalse() {
        return trueFalse;
    }

    public void setTrueFalse(String trueFalse) {
        this.trueFalse = trueFalse;
    }

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        this.matched = matched;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(String nextIndex) {
        this.nextIndex = nextIndex;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getCycleStartId() {
        return cycleStartId;
    }

    public void setCycleStartId(String cycleStartId) {
        this.cycleStartId = cycleStartId;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String gettNextId() {
        return tNextId;
    }

    public void settNextId(String tNextId) {
        this.tNextId = tNextId;
    }

    @Override
    public String toString() {
        return "ProblemScanLogicVO{" +
                "onlyIndex='" + onlyIndex + '\'' +
                ", trueFalse='" + trueFalse + '\'' +
                ", matched='" + matched + '\'' +
                ", relative='" + relative + '\'' +
                ", position='" + position + '\'' +
                ", matchContent='" + matchContent + '\'' +
                ", action='" + action + '\'' +
                ", tNextId='" + tNextId + '\'' +
                ", rPosition=" + rPosition +
                ", length='" + length + '\'' +
                ", exhibit='" + exhibit + '\'' +
                ", wordName='" + wordName + '\'' +
                ", compare='" + compare + '\'' +
                ", content='" + content + '\'' +
                ", nextIndex='" + nextIndex + '\'' +
                ", problemId='" + problemId + '\'' +
                ", cycleStartId='" + cycleStartId + '\'' +
                ", pageIndex='" + pageIndex + '\'' +
                '}';
    }
}
