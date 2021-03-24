package com.acuilab.bc.main.coin;

import java.math.BigInteger;

/**
 * 全新的 "FC" 矿池，存入FC获得超高年化收益
 * @author acuilab.com
 */
public interface IFCExchange {

    UserInfo userInfos();
    
    class UserInfo {
        // 当前质押的fc数量
        private BigInteger amount;
        // 提取的cfx金额
        private BigInteger profitDebt;
        // 累积质押的fc数量
        private BigInteger accumulateAmount;
        // 是否获得nft
        private boolean nftGranted;
        // 获得的nft token id
        private BigInteger grantedTokenId;
        // 累积的收益
        private BigInteger accProfit;
    }
}
