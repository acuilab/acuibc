package com.acuilab.bc.main.wallet;

import java.util.Date;

/**
 *
 * @author admin
 */
public class Address {
    private String id;
    private String address; // 地址
    private String remark;  // 备注
    private String blockChainSymbol;    // 区块链简称(其实叫钱包类型更合适一些)
    private Date created;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getBlockChainSymbol() {
        return blockChainSymbol;
    }

    public void setBlockChainSymbol(String blockChainSymbol) {
        this.blockChainSymbol = blockChainSymbol;
    }
    
}
