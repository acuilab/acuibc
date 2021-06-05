package com.acuilab.bc.main.cfx;

import java.math.BigInteger;

/**
 * 全新的 "FC" 矿池，存入FC获得超高年化收益
 * @author acuilab.com
 */
public interface IFCExchange {

    UserInfo userInfos(String address);
    
    BigInteger accCfxPerFc();
    
    BigInteger fcSupply();
    
    BigInteger lastStakingAmount();
    
    String withdrawPendingProfit(String privateKey) throws Exception;
    
    class UserInfo {
        // 当前质押的fc数量
        private final BigInteger amount;
        // 提取的cfx数量
        private final BigInteger profitDebt;
        // 累积质押的fc数量*
        private final BigInteger accumulateAmount;
        // 是否获得nft
        private final boolean nftGranted;
        // 获得的nft token id
        private final BigInteger grantedTokenId;
        // 累计提取的CFX数量*
        private final BigInteger accProfit;

        public UserInfo(BigInteger amount, BigInteger profitDebt, BigInteger accumulateAmount, boolean nftGranted, BigInteger grantedTokenId, BigInteger accProfit) {
            this.amount = amount;
            this.profitDebt = profitDebt;
            this.accumulateAmount = accumulateAmount;
            this.nftGranted = nftGranted;
            this.grantedTokenId = grantedTokenId;
            this.accProfit = accProfit;
        }

        public BigInteger getAmount() {
            return amount;
        }

        public BigInteger getProfitDebt() {
            return profitDebt;
        }

        public BigInteger getAccumulateAmount() {
            return accumulateAmount;
        }

        public boolean isNftGranted() {
            return nftGranted;
        }

        public BigInteger getGrantedTokenId() {
            return grantedTokenId;
        }

        public BigInteger getAccProfit() {
            return accProfit;
        }
        
    }
}
