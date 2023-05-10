package com.sgcc.sql.pojo;

public class DeviceCmd {
    private String id;  //命令自身的id
    private String content;  //命令的具体内容
    private String serial;  //命令对应的设备型号
    private String result;  //命令的执行结果
    private String nextCmd;  //下一条执行命令
    private String SuccesCmd;  //成功后执行的命令的id
    private String FailCmd;  //失败后执行命令的id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getNextCmd() {
        if (this.result==null||this.result==""){
            return "命令执行失败";
        }
        else if(this.result=="200"){
            return this.SuccesCmd;
        }
        else if (this.result=="404"){
            return this.FailCmd;
        }
        return "命令执行失败";
    }

    public void setNextCmd(String nextCmd) {
        this.nextCmd = nextCmd;
    }
}
