package com.acuilab.bc.main.wallet;

import java.util.Date;

/**
 *
 * @author admin
 */
public class TransferRecord {
    
    private String status;
    private String SendAddress;
    private String recvAddress;
    private String remark;
    private String hash;
    private Date created;

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
