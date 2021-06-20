package com.acuilab.bc.main.cfx.dapp.guguo;

/**
 *
 * @author acuilab.com
 */
public class UserData {
    private final String userAddress;
    private final double currentBalance;
    private final double currentPendings;
    private final double totalXiang;
    private final double consumed;
    private final double totalConsumed;
    private final double totalCleared;

    public UserData(String userAddress, double currentBalance, double currentPendings, double consumed, double totalConsumed, double totalCleared) {
        this.userAddress = userAddress;
        this.currentBalance = currentBalance;
        this.currentPendings = currentPendings;
        this.totalXiang = currentBalance + currentPendings;
        this.consumed = consumed;
        this.totalConsumed = totalConsumed;
        this.totalCleared = totalCleared;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getCurrentPendings() {
        return currentPendings;
    }

    public double getConsumed() {
        return consumed;
    }
    
    public double getTotalXiang() {
        return totalXiang;
    }

    public double getTotalConsumed() {
        return totalConsumed;
    }

    public double getTotalCleared() {
        return totalCleared;
    }
    
}
