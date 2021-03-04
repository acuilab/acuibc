package com.acuilab.bc.main.cfx.dapp.batchtransfer;

/**
 *
 * @author acuilab
 */
public class BatchTransfer {
    private String address;
    private String value;
    private String remark;
    private String hash;

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public String getHash() {
	return hash;
    }

    public void setHash(String hash) {
	this.hash = hash;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
