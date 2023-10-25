package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题扫描逻辑对象 problem_scan_logic
 * @author ruoyi
 * @date 2022-02-14
 */
public class ProblemScanLogicCO {

    private String targetType;
    private String onlyIndex;
    private String trueFalse;
    private String checked;
    private String nextIndex;
    private String matched;
    private String action;
    private String relativeTest;
    private String position;
    private String problemId;
    private String bi;
    private String cycleStartId;
    private String tNextId;
    private String relativeType;
    private String cursorRegion;
    private String relative;
    private String compare;
    private String pageIndex;
    private String length;
    private String matchContent;
    private String wordName;
    private String rPosition;
    private String exhibit;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
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

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(String nextIndex) {
        this.nextIndex = nextIndex;
    }

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        this.matched = matched;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRelativeTest() {
        return relativeTest;
    }

    public void setRelativeTest(String relativeTest) {
        this.relativeTest = relativeTest;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getBi() {
        return bi;
    }

    public void setBi(String bi) {
        this.bi = bi;
    }

    public String getCycleStartId() {
        return cycleStartId;
    }

    public void setCycleStartId(String cycleStartId) {
        this.cycleStartId = cycleStartId;
    }

    public String gettNextId() {
        return tNextId;
    }

    public void settNextId(String tNextId) {
        this.tNextId = tNextId;
    }

    public String getRelativeType() {
        return relativeType;
    }

    public void setRelativeType(String relativeType) {
        this.relativeType = relativeType;
    }

    public String getCursorRegion() {
        return cursorRegion;
    }

    public void setCursorRegion(String cursorRegion) {
        this.cursorRegion = cursorRegion;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }

    public String getCompare() {
        return compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getMatchContent() {
        return matchContent;
    }

    public void setMatchContent(String matchContent) {
        this.matchContent = matchContent;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getrPosition() {
        return rPosition;
    }

    public void setrPosition(String rPosition) {
        this.rPosition = rPosition;
    }

    public String getExhibit() {
        return exhibit;
    }

    public void setExhibit(String exhibit) {
        this.exhibit = exhibit;
    }

    @Override
    public String toString() {
        return "{" +
                "targetType='" + targetType + '\'' +
                ", onlyIndex='" + onlyIndex + '\'' +
                ", trueFalse='" + trueFalse + '\'' +
                ", checked='" + checked + '\'' +
                ", nextIndex='" + nextIndex + '\'' +
                ", matched='" + matched + '\'' +
                ", action='" + action + '\'' +
                ", relativeTest='" + relativeTest + '\'' +
                ", position='" + position + '\'' +
                ", problemId='" + problemId + '\'' +
                ", bi='" + bi + '\'' +
                ", cycleStartId='" + cycleStartId + '\'' +
                ", tNextId='" + tNextId + '\'' +
                ", relativeType='" + relativeType + '\'' +
                ", cursorRegion='" + cursorRegion + '\'' +
                ", relative='" + relative + '\'' +
                ", compare='" + compare + '\'' +
                ", pageIndex='" + pageIndex + '\'' +
                ", length='" + length + '\'' +
                ", matchContent='" + matchContent + '\'' +
                ", wordName='" + wordName + '\'' +
                ", rPosition='" + rPosition + '\'' +
                ", exhibit='" + exhibit + '\'' +
                '}';
    }
}
