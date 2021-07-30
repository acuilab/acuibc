package com.acuilab.bc.main.cfx.dapp.accountinfo;

/**
 *
 * @author acuilab.com
 */
public class Coin {
    private final String symbal;
    private Double balance;

    public Coin(String symbal, Double balance) {
        this.symbal = symbal;
        this.balance = balance;
    }

    public String getSymbal() {
        return symbal;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
}
