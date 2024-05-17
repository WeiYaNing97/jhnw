package com.sgcc.advanced.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 光衰平均值比较对象 light_attenuation_comparison
 * 
 * @author ruoyi
 * @date 2023-05-06
 */
public class LightAttenuationComparison extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 交换机ip */
    @Excel(name = "交换机ip")
    private String switchIp;

    /** 交换机四项基本信息表ID索引 */
    @Excel(name = "交换机四项基本信息表ID索引")
    private Long switchId;

    /** 参数数量 */
    @Excel(name = "参数数量")
    private Integer numberParameters;

    /** 端口号 */
    @Excel(name = "端口号")
    private String port;

    /** TX平均值 */
    @Excel(name = "TX平均值")
    private String txAverageValue;

    /** TX最新参数 */
    @Excel(name = "TX最新参数")
    private String txLatestNumber;

    /** RX平均值 */
    @Excel(name = "RX平均值")
    private String rxAverageValue;

    /** 收光最新参数 */
    @Excel(name = "RX最新参数")
    private String rxLatestNumber;

    /** TX起始值(基准) */
    @Excel(name = "TX起始值(基准)")
    private String txStartValue;

    /** RX起始值(基准) */
    @Excel(name = "RX起始值(基准)")
    private String rxStartValue;

    /** rx额定偏差 */
    @Excel(name = "rx额定偏差")
    private String rxRatedDeviation;

    /** tx额定偏差 */
    @Excel(name = "tx额定偏差")
    private String txRatedDeviation;

    /** rx 即时偏差 */
    @Excel(name = "rx 即时偏差")
    private String rxImmediateDeviation;

    /** tx 即时偏差 */
    @Excel(name = "tx 即时偏差")
    private String txImmediateDeviation;

    /** 保留字段一 */
    @Excel(name = "保留字段一")
    private String valueOne;

    /** 保留字段二 */
    @Excel(name = "保留字段二")
    private String valueTwo;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSwitchIp(String switchIp) 
    {
        this.switchIp = switchIp;
    }

    public String getSwitchIp() 
    {
        return switchIp;
    }

    public void setNumberParameters(Integer numberParameters) 
    {
        this.numberParameters = numberParameters;
    }

    public Integer getNumberParameters() 
    {
        return numberParameters;
    }
    public void setPort(String port) 
    {
        this.port = port;
    }

    public String getPort() 
    {
        return port;
    }
    public void setTxAverageValue(String txAverageValue) 
    {
        this.txAverageValue = txAverageValue;
    }

    public String getTxAverageValue() 
    {
        return txAverageValue;
    }
    public void setTxLatestNumber(String txLatestNumber) 
    {
        this.txLatestNumber = txLatestNumber;
    }

    public String getTxLatestNumber() 
    {
        return txLatestNumber;
    }
    public void setRxAverageValue(String rxAverageValue) 
    {
        this.rxAverageValue = rxAverageValue;
    }

    public String getRxAverageValue() 
    {
        return rxAverageValue;
    }
    public void setRxLatestNumber(String rxLatestNumber) 
    {
        this.rxLatestNumber = rxLatestNumber;
    }

    public String getRxLatestNumber() 
    {
        return rxLatestNumber;
    }
    public void setTxStartValue(String txStartValue) 
    {
        this.txStartValue = txStartValue;
    }

    public String getTxStartValue() 
    {
        return txStartValue;
    }
    public void setRxStartValue(String rxStartValue) 
    {
        this.rxStartValue = rxStartValue;
    }

    public String getRxStartValue() 
    {
        return rxStartValue;
    }

    public void setValueOne(String valueOne) 
    {
        this.valueOne = valueOne;
    }

    public String getValueOne() 
    {
        return valueOne;
    }
    public void setValueTwo(String valueTwo) 
    {
        this.valueTwo = valueTwo;
    }

    public String getValueTwo() 
    {
        return valueTwo;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getSwitchId() {
        return switchId;
    }

    public void setSwitchId(Long switchId) {
        this.switchId = switchId;
    }

    public String getRxRatedDeviation() {
        return rxRatedDeviation;
    }

    public void setRxRatedDeviation(String rxRatedDeviation) {
        this.rxRatedDeviation = rxRatedDeviation;
    }

    public String getTxRatedDeviation() {
        return txRatedDeviation;
    }

    public void setTxRatedDeviation(String txRatedDeviation) {
        this.txRatedDeviation = txRatedDeviation;
    }


    public String getRxImmediateDeviation() {
        return rxImmediateDeviation;
    }

    public void setRxImmediateDeviation(String rxImmediateDeviation) {
        this.rxImmediateDeviation = rxImmediateDeviation;
    }

    public String getTxImmediateDeviation() {
        return txImmediateDeviation;
    }

    public void setTxImmediateDeviation(String txImmediateDeviation) {
        this.txImmediateDeviation = txImmediateDeviation;
    }

    @Override
    public String toString() {
        return "LightAttenuationComparison{" +
                "id=" + id +
                ", switchIp='" + switchIp + '\'' +
                ", switchId=" + switchId +
                ", numberParameters=" + numberParameters +
                ", port='" + port + '\'' +
                ", txAverageValue='" + txAverageValue + '\'' +
                ", txLatestNumber='" + txLatestNumber + '\'' +
                ", rxAverageValue='" + rxAverageValue + '\'' +
                ", rxLatestNumber='" + rxLatestNumber + '\'' +
                ", txStartValue='" + txStartValue + '\'' +
                ", rxStartValue='" + rxStartValue + '\'' +
                ", rxRatedDeviation='" + rxRatedDeviation + '\'' +
                ", txRatedDeviation='" + txRatedDeviation + '\'' +

                ", rxImmediateDeviation='" + rxImmediateDeviation + '\'' +
                ", txImmediateDeviation='" + txImmediateDeviation + '\'' +

                ", valueOne='" + valueOne + '\'' +
                ", valueTwo='" + valueTwo + '\'' +
                '}';
    }

    public String toJson() {
        return "LightAttenuationComparison{" +
                "id:" + id +
                ", switchIp:'" + switchIp + '\'' +
                ", switchId:" + switchId +
                ", numberParameters:" + numberParameters +
                ", port:'" + port + '\'' +
                ", txAverageValue:'" + txAverageValue + '\'' +
                ", txLatestNumber:'" + txLatestNumber + '\'' +
                ", rxAverageValue:'" + rxAverageValue + '\'' +
                ", rxLatestNumber:'" + rxLatestNumber + '\'' +
                ", txStartValue:'" + txStartValue + '\'' +
                ", rxStartValue:'" + rxStartValue + '\'' +
                ", rxRatedDeviation:'" + rxRatedDeviation + '\'' +
                ", txRatedDeviation:'" + txRatedDeviation + '\'' +

                ", rxImmediateDeviation:'" + rxImmediateDeviation + '\'' +
                ", txImmediateDeviation:'" + txImmediateDeviation + '\'' +

                ", valueOne:'" + valueOne + '\'' +
                ", valueTwo:'" + valueTwo + '\'' +
                '}';
    }

}
