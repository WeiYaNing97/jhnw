package com.sgcc.sql.domain;

import java.util.List;

public class AdvancedFunctionalParameters {

    private TotalQuestionTable totalQuestionTable;
    private List<CommandLogic> commandLogics;
    private List<MethodTable> methodTables;

    public TotalQuestionTable getTotalQuestionTable() {
        return totalQuestionTable;
    }

    public void setTotalQuestionTable(TotalQuestionTable totalQuestionTable) {
        this.totalQuestionTable = totalQuestionTable;
    }

    public List<CommandLogic> getCommandLogics() {
        return commandLogics;
    }

    public void setCommandLogics(List<CommandLogic> commandLogics) {
        this.commandLogics = commandLogics;
    }

    public List<MethodTable> getMethodTables() {
        return methodTables;
    }

    public void setMethodTables(List<MethodTable> methodTables) {
        this.methodTables = methodTables;
    }

    @Override
    public String toString() {
        return "AdvancedFunctionalParameters{" +
                "totalQuestionTable=" + totalQuestionTable +
                ", commandLogics=" + commandLogics +
                ", methodTables=" + methodTables +
                '}';
    }
}
