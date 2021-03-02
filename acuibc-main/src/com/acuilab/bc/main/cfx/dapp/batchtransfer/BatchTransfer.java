package com.acuilab.bc.main.cfx.dapp.batchtransfer;

/**
 *
 * @author acuilab
 */
public class BatchTransfer {
    private String address;
    private String value;
    private String remark;
    private String result;

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

    public String getResult() {
	return result;
    }

    public void setResult(String result) {
	this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
