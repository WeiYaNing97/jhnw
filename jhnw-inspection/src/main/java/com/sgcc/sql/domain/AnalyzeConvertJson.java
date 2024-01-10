package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;

/**
 * @program: jhnw
 * @description: 分析解析类
 * @author:
 * @create: 2024-01-05 16:37
 **/
public class AnalyzeConvertJson {
    private String onlyIndex;/*ID*/
    private String trueFalse;/*成功 失败*/
    private String pageIndex;/*行号*/
    private String command;/*命令*/
    private String nextIndex;/*下一ID*/
    private String para;/*参数名*/
    private String resultCheckId;/*返回结果验证id*/
    private String matched;/*匹配*/

    private String relative;/*相对位置*/
    private String position;
    private String relativePosition;
    private String cursorRegion;


    private String matchContent;/*匹配内容*/
    private String action;/*动作*/
    private String tNextId;/*true下一条分析索引*/
    private Integer rPosition;/*位置*/
    private String length;/*长度*/
    private String exhibit;/*是否显示*/
    private String wordName;/*取词名称*/
    private String compare;/*比较*/
    private String content;
    private String problemId;/*问题索引*/
    private String cycleStartId;/*循环起始索引*/
    private String targetType;
    private String relativeTest;
    private String relativeType;
    private String length1;
    private String classify;

    private String id;
    private String tComId;
    private String fLine;
    private String tLine;
    private String fNextId;
    private String fComId;
    private Long returnCmdId;



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

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(String nextIndex) {
        this.nextIndex = nextIndex;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getResultCheckId() {
        return resultCheckId;
    }

    public void setResultCheckId(String resultCheckId) {
        this.resultCheckId = resultCheckId;
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

    public String getCursorRegion() {
        return cursorRegion;
    }

    public void setCursorRegion(String cursorRegion) {
        this.cursorRegion = cursorRegion;
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

    public String gettNextId() {
        return tNextId;
    }

    public void settNextId(String tNextId) {
        this.tNextId = tNextId;
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

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getRelativeTest() {
        return relativeTest;
    }

    public void setRelativeTest(String relativeTest) {
        this.relativeTest = relativeTest;
    }

    public String getRelativeType() {
        return relativeType;
    }

    public void setRelativeType(String relativeType) {
        this.relativeType = relativeType;
    }

    public String getLength1() {
        return length1;
    }

    public void setLength1(String length1) {
        this.length1 = length1;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getRelativePosition() {
        return relativePosition;
    }

    public void setRelativePosition(String relativePosition) {
        this.relativePosition = relativePosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String gettComId() {
        return tComId;
    }

    public void settComId(String tComId) {
        this.tComId = tComId;
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

    @Override
    public String toString() {
        return "AnalyzeConvertJson{" +
                "onlyIndex='" + onlyIndex + '\'' +
                ", trueFalse='" + trueFalse + '\'' +
                ", pageIndex='" + pageIndex + '\'' +
                ", command='" + command + '\'' +
                ", nextIndex='" + nextIndex + '\'' +
                ", para='" + para + '\'' +
                ", resultCheckId='" + resultCheckId + '\'' +
                ", matched='" + matched + '\'' +
                ", relative='" + relative + '\'' +
                ", position='" + position + '\'' +
                ", relativePosition='" + relativePosition + '\'' +
                ", cursorRegion='" + cursorRegion + '\'' +
                ", matchContent='" + matchContent + '\'' +
                ", action='" + action + '\'' +
                ", tNextId='" + tNextId + '\'' +
                ", rPosition=" + rPosition +
                ", length='" + length + '\'' +
                ", exhibit='" + exhibit + '\'' +
                ", wordName='" + wordName + '\'' +
                ", compare='" + compare + '\'' +
                ", content='" + content + '\'' +
                ", problemId='" + problemId + '\'' +
                ", cycleStartId='" + cycleStartId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", relativeTest='" + relativeTest + '\'' +
                ", relativeType='" + relativeType + '\'' +
                ", length1='" + length1 + '\'' +
                ", classify='" + classify + '\'' +
                ", id='" + id + '\'' +
                ", tComId='" + tComId + '\'' +
                ", fLine='" + fLine + '\'' +
                ", tLine='" + tLine + '\'' +
                ", fNextId='" + fNextId + '\'' +
                ", fComId='" + fComId + '\'' +
                ", returnCmdId=" + returnCmdId +
                '}';
    }
}
