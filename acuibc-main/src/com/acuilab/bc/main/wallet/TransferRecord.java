package com.acuilab.bc.main.wallet;

import java.util.Date;

/**
 *
 * @author admin
 */
public class TransferRecord {
    
    private String coinName;        // 代币名称
    private String status;          // 交易状态
    private String SendAddress;     // 发送地址
    private String recvAddress;     // 接收地址
    private String remark;          // 备注
    private String hash;            // 交易哈希(主键)
    private Date created;           // 创建时间

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendAddress() {
        return SendAddress;
    }

    public void setSendAddress(String SendAddress) {
        this.SendAddress = SendAddress;
    }

    public String getRecvAddress() {
        return recvAddress;
    }

    public void setRecvAddress(String recvAddress) {
        this.recvAddress = recvAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
    
}
