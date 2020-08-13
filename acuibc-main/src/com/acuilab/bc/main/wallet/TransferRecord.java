package com.acuilab.bc.main.wallet;

import java.util.Date;

/**
 *
 * @author admin
 */
public class TransferRecord {
    
    private String walletName;      // 钱包名称
    private String coinName;        // 代币名称
    private String status;          // 交易状态(0 代表成功，1 代表发生错误，当交易被跳过或未打包时为null)
    private String SendAddress;     // 发送地址
    private String recvAddress;     // 接收地址
    private String remark;          // 备注
    private String hash;            // 交易哈希(主键)
    private Date timestamp;           // 创建时间
    
    private String value;           // 交易额(以Drip为单位)
    private String gasPrice;        // 发送者提供的 gas 价格（以 Drip 为单位）（这笔交易每笔 gas 的价格）
    private String gas;             // 发送者提供的 gas
    
    private String blockHash;       // 当前交易被打包进的且状态为已执行的区块的哈希值。交易为等待或挂起状态时返回null

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

}
