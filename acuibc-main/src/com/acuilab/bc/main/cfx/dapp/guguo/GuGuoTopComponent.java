package com.acuilab.bc.main.cfx.dapp.guguo;

import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.Wallet;
import com.acuilab.bc.main.wallet.common.PasswordVerifyDialog;
import com.acuilab.bc.main.wallet.common.SelectWalletDialog;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.examples.complete.Utils;
import net.java.balloontip.utils.TimingUtils;
import org.javatuples.Pair;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.acuilab.bc.main.cfx.dapp.guguo//GuGuo//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "GuGuoTopComponent",
        iconBase="resource/dapp/guguo16.png", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.acuilab.bc.main.cfx.dapp.guguo.GuGuoTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_GuGuoAction",
        preferredID = "GuGuoTopComponent"
)
@Messages({
    "CTL_GuGuoAction=古国序列",
    "CTL_GuGuoTopComponent=古国序列",
    "HINT_GuGuoTopComponent=古国序列 Ancient Chinese Gods是以「中国全系列神话传说」为灵感打造的加密藏品和区块链竞技游戏。"
})
public final class GuGuoTopComponent extends TopComponent {
    
    private Wallet wallet;
    
    private final NftPanel[] guGuoPanels = new NftPanel[] {new NftPanel(3), new NftPanel(3), new NftPanel(3), new NftPanel(3), new NftPanel(3)};
    private final NftPanel[] kaoZiPanels = new NftPanel[] {new NftPanel(0), new NftPanel(0), new NftPanel(0), new NftPanel(0), new NftPanel(0)};
    private final NftPanel[] moonPanels = new NftPanel[] {new NftPanel(1), new NftPanel(1), new NftPanel(1), new NftPanel(1), new NftPanel(1)};
    private final NftPanel[] fluxPanels = new NftPanel[] {new NftPanel(2), new NftPanel(2), new NftPanel(2), new NftPanel(2), new NftPanel(2)};

    public GuGuoTopComponent() {
        initComponents();
        setName(Bundle.CTL_GuGuoTopComponent());
        setToolTipText(Bundle.HINT_GuGuoTopComponent());

        for(NftPanel nftPanel : guGuoPanels) {
            guGuoContainer.add(nftPanel);
        }
        for(NftPanel nftPanel : kaoZiPanels) {
            kaoZiContainer.add(nftPanel);
        }
        for(NftPanel nftPanel : moonPanels) {
            moonContainer.add(nftPanel);
        }
        for(NftPanel nftPanel : fluxPanels) {
            fluxContainer.add(nftPanel);
        }
        
        PromptSupport.init("请输入钱包密码", null, null, pwdFld);
        pwdFld.setVisible(true);
        needWithdrawXiangCheckBox.setVisible(true);
        pickCardBtn.setVisible(true);
        peekUserDataBtn.setVisible(true);
    }
    
    private void myInit() {
        if(wallet != null) {
            setBtnEnabled(false);
            final ProgressHandle ph = ProgressHandle.createHandle("正在初始化，请稍候...");
            SwingWorker<Pair<List<BigInteger>, Map>, Void> worker = new SwingWorker<Pair<List<BigInteger>, Map>, Void>() {
                @Override
                protected Pair<List<BigInteger>, Map> doInBackground() throws Exception {
                    ph.start();
                    
                    IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                    IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                    
                    List<BigInteger> list = Lists.newArrayList();
                    // YAO
                    list.add(yaoContract.yaoBalance(wallet.getAddress()));
                    
                    // XIANG
                    list.add(xiangContract.xiangBalance(wallet.getAddress()));
                    
                    // totalReleased
                    list.add(yaoContract.totalReleased());
                    
                    // pendingToken
                    BigInteger pendingToken = yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_KAOZI_PID);
                    pendingToken = pendingToken.add(yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_MOON_PID));
                    pendingToken = pendingToken.add(yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_FLUX_PID));
                    pendingToken = pendingToken.add(yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_GUGUO_PID));
                    list.add(pendingToken);
                    
                    // 当前质押的烤仔nft编号及数量
                    Pair<BigInteger[], BigInteger[]> kaoZiPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_KAOZI_PID);
                    // 当前质押的月亮创世nft编号及数量
                    Pair<BigInteger[], BigInteger[]> moonPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_MOON_PID);
                    // 当前质押的flux nft编号及数量
                    Pair<BigInteger[], BigInteger[]> fluxPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_FLUX_PID);
                    // 当前质押的古国nft编号及数量
                    Pair<BigInteger[], BigInteger[]> guGuoPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_GUGUO_PID);
                    
                    Map<Integer, Pair<BigInteger[], BigInteger[]>> map = Maps.newHashMap();
                    map.put(Constants.GUGUO_KAOZI_PID, kaoZiPledged);
                    map.put(Constants.GUGUO_MOON_PID, moonPledged);
                    map.put(Constants.GUGUO_FLUX_PID, fluxPledged);
                    map.put(Constants.GUGUO_GUGUO_PID, guGuoPledged);
                    
                    // 获得池内质押总量爻
                    list.add(xiangContract.poolPledged(BigInteger.ZERO));
                    // 我的质押
                    list.add(xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ZERO));
                    // 当前收益
                    list.add(xiangContract.pendingToken(wallet.getAddress(), BigInteger.ZERO));
                    
                    // 获得lp池内质押总量爻
                    list.add(xiangContract.poolPledged(BigInteger.ONE));
                    // lp池我的质押
                    list.add(xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ONE));
                    // lp池当前收益
                    list.add(xiangContract.pendingToken(wallet.getAddress(), BigInteger.ONE));
                    
                    return new Pair<>(list, map);
                }

                @Override
                protected void done() {
                    try {
                        for(NftPanel nftPanel : guGuoPanels) {
                            nftPanel.setWallet(wallet);
                        }
                        for(NftPanel nftPanel : kaoZiPanels) {
                            nftPanel.setWallet(wallet);
                        }
                        for(NftPanel nftPanel : moonPanels) {
                            nftPanel.setWallet(wallet);
                        }
                        for(NftPanel nftPanel : fluxPanels) {
                            nftPanel.setWallet(wallet);
                        }
                        
                        Pair<List<BigInteger>, Map> result = get();
                        List<BigInteger> list = result.getValue0();
                        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                        yaoBalanceFld.setText(yaoCoin.minUnit2MainUint(list.get(0)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        xiangBalanceFld.setText(yaoCoin.minUnit2MainUint(list.get(1)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        
                        totalReleasedFld.setText(yaoCoin.minUnit2MainUint(list.get(2)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        pendingTokenFld.setText(yaoCoin.minUnit2MainUint(list.get(3)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        
                        poolPledgedFld.setText(yaoCoin.minUnit2MainUint(list.get(4)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        poolPledgedFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(4)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        pledgedAmountFld.setText(yaoCoin.minUnit2MainUint(list.get(5)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        pledgedAmountFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(5)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        xiangPendingTokenFld.setText(yaoCoin.minUnit2MainUint(list.get(6)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        xiangPendingTokenFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(6)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        
                        lpPoolPledgedFld.setText(yaoCoin.minUnit2MainUint(list.get(7)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        lpPoolPledgedFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(7)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        lpPledgedAmountFld.setText(yaoCoin.minUnit2MainUint(list.get(8)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        lpPledgedAmountFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(8)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        lpXiangPendingTokenFld.setText(yaoCoin.minUnit2MainUint(list.get(9)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        lpXiangPendingTokenFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(9)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        
                        Map<Integer, Pair<BigInteger[], BigInteger[]>> map = result.getValue1();
                        initPanels(map.get(Constants.GUGUO_KAOZI_PID), kaoZiPanels);
                        initPanels(map.get(Constants.GUGUO_MOON_PID), moonPanels);
                        initPanels(map.get(Constants.GUGUO_FLUX_PID), fluxPanels);
                        initPanels(map.get(Constants.GUGUO_GUGUO_PID), guGuoPanels);
                        
                        stakeNumberFld.setText("");
                        depositNumberFld.setText("");
                        lpStakeNumberFld.setText("");
                        lpDepositNumberFld.setText("");
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        ph.finish();
                        setBtnEnabled(true);
                    }
                }
            };
            worker.execute();
        } else {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("请选择钱包");
                BalloonTip balloonTip = new BalloonTip(selectWalletBtn, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private void initPanels(Pair<BigInteger[], BigInteger[]> pledged, NftPanel[] panels) {
        BigInteger[] tockenIds = pledged.getValue0();
        BigInteger[] amounts = pledged.getValue1();
        int count = 0;
        for(int i=0; i<tockenIds.length; i++) {
             for(int j=0; j<amounts[i].intValueExact(); j++) {
                 panels[count].setPledged(tockenIds[i]);
                 count++;
            }
        }
         
        // 处理剩余的panel
        for(int i=count; i<panels.length; i++) {
            panels[i].setUnpledged();
        }
    }
    
    private void setBtnEnabled(boolean enabled) {
        reloadBtn.setEnabled(enabled);
        withdrawPoolAllBtn.setEnabled(enabled);
        refreshBtn1.setEnabled(enabled);
        refreshBtn2.setEnabled(enabled);
        refreshBtn3.setEnabled(enabled);
        refreshBtn4.setEnabled(enabled);
        refreshBtn5.setEnabled(enabled);
        
        withdrawBtn.setEnabled(enabled);
        stakeYAOBtn.setEnabled(enabled);
        depositYAOBtn.setEnabled(enabled);
        maxShakeBtn.setEnabled(enabled);
        maxDepositBtn.setEnabled(enabled);
        
        lpWithdrawBtn.setEnabled(enabled);
        lpStakeYAOBtn.setEnabled(enabled);
        lpDepositYAOBtn.setEnabled(enabled);
        lpMaxShakeBtn.setEnabled(enabled);
        lpMaxDepositBtn.setEnabled(enabled);
        
        withdrawAllBtn.setEnabled(enabled);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        walletFld = new org.jdesktop.swingx.JXTextField();
        selectWalletBtn = new org.jdesktop.swingx.JXButton();
        jSeparator1 = new javax.swing.JSeparator();
        jXLabel2 = new org.jdesktop.swingx.JXLabel();
        yaoBalanceFld = new org.jdesktop.swingx.JXTextField();
        jXLabel3 = new org.jdesktop.swingx.JXLabel();
        xiangBalanceFld = new org.jdesktop.swingx.JXTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        guGuoContainer = new org.jdesktop.swingx.JXPanel();
        kaoZiContainer = new org.jdesktop.swingx.JXPanel();
        moonContainer = new org.jdesktop.swingx.JXPanel();
        fluxContainer = new org.jdesktop.swingx.JXPanel();
        jXLabel4 = new org.jdesktop.swingx.JXLabel();
        totalReleasedFld = new org.jdesktop.swingx.JXTextField();
        jXLabel5 = new org.jdesktop.swingx.JXLabel();
        pendingTokenFld = new org.jdesktop.swingx.JXTextField();
        jSeparator2 = new javax.swing.JSeparator();
        withdrawPoolAllBtn = new org.jdesktop.swingx.JXButton();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        jXLabel6 = new org.jdesktop.swingx.JXLabel();
        jXLabel7 = new org.jdesktop.swingx.JXLabel();
        jXLabel8 = new org.jdesktop.swingx.JXLabel();
        poolPledgedFld = new org.jdesktop.swingx.JXTextField();
        pledgedAmountFld = new org.jdesktop.swingx.JXTextField();
        xiangPendingTokenFld = new org.jdesktop.swingx.JXTextField();
        withdrawBtn = new org.jdesktop.swingx.JXButton();
        stakeYAOBtn = new org.jdesktop.swingx.JXButton();
        depositYAOBtn = new org.jdesktop.swingx.JXButton();
        stakeNumberFld = new org.jdesktop.swingx.JXTextField();
        depositNumberFld = new org.jdesktop.swingx.JXTextField();
        maxShakeBtn = new org.jdesktop.swingx.JXButton();
        maxDepositBtn = new org.jdesktop.swingx.JXButton();
        refreshBtn3 = new org.jdesktop.swingx.JXButton();
        jXPanel5 = new org.jdesktop.swingx.JXPanel();
        jXLabel9 = new org.jdesktop.swingx.JXLabel();
        jXLabel10 = new org.jdesktop.swingx.JXLabel();
        jXLabel11 = new org.jdesktop.swingx.JXLabel();
        lpXiangPendingTokenFld = new org.jdesktop.swingx.JXTextField();
        lpPledgedAmountFld = new org.jdesktop.swingx.JXTextField();
        lpPoolPledgedFld = new org.jdesktop.swingx.JXTextField();
        refreshBtn5 = new org.jdesktop.swingx.JXButton();
        lpWithdrawBtn = new org.jdesktop.swingx.JXButton();
        lpStakeNumberFld = new org.jdesktop.swingx.JXTextField();
        lpDepositNumberFld = new org.jdesktop.swingx.JXTextField();
        lpMaxShakeBtn = new org.jdesktop.swingx.JXButton();
        lpMaxDepositBtn = new org.jdesktop.swingx.JXButton();
        lpStakeYAOBtn = new org.jdesktop.swingx.JXButton();
        lpDepositYAOBtn = new org.jdesktop.swingx.JXButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        refreshBtn2 = new org.jdesktop.swingx.JXButton();
        refreshBtn4 = new org.jdesktop.swingx.JXButton();
        jSeparator6 = new javax.swing.JSeparator();
        withdrawAllBtn = new org.jdesktop.swingx.JXButton();
        pickCardBtn = new org.jdesktop.swingx.JXButton();
        pwdFld = new javax.swing.JPasswordField();
        jSeparator7 = new javax.swing.JSeparator();
        peekUserDataBtn = new org.jdesktop.swingx.JXButton();
        needWithdrawXiangCheckBox = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JSeparator();
        reloadBtn = new org.jdesktop.swingx.JXButton();
        refreshBtn1 = new org.jdesktop.swingx.JXButton();

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel1, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel1.text")); // NOI18N

        walletFld.setEditable(false);
        walletFld.setToolTipText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.walletFld.toolTipText")); // NOI18N
        walletFld.setPrompt(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.walletFld.prompt")); // NOI18N
        walletFld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                walletFldMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(selectWalletBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.selectWalletBtn.text")); // NOI18N
        selectWalletBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectWalletBtnActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel2, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel2.text")); // NOI18N

        yaoBalanceFld.setEditable(false);
        yaoBalanceFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.yaoBalanceFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel3, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel3.text")); // NOI18N

        xiangBalanceFld.setEditable(false);
        xiangBalanceFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.xiangBalanceFld.text")); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jXPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jXPanel1.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        jXPanel1.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        guGuoContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.guGuoContainer.border.title"))); // NOI18N
        guGuoContainer.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        guGuoContainer.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        kaoZiContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.kaoZiContainer.border.title"))); // NOI18N
        kaoZiContainer.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        kaoZiContainer.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        moonContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.moonContainer.border.title"))); // NOI18N
        moonContainer.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        moonContainer.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        fluxContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.fluxContainer.border.title"))); // NOI18N
        fluxContainer.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        fluxContainer.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        jXLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/acuilab/bc/main/cfx/dapp/guguo/yao16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jXLabel4, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel4.text")); // NOI18N

        totalReleasedFld.setEditable(false);
        totalReleasedFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.totalReleasedFld.text")); // NOI18N

        jXLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/acuilab/bc/main/cfx/dapp/guguo/yao16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jXLabel5, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel5.text")); // NOI18N

        pendingTokenFld.setEditable(false);
        pendingTokenFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.pendingTokenFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(withdrawPoolAllBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.withdrawPoolAllBtn.text")); // NOI18N
        withdrawPoolAllBtn.setEnabled(false);
        withdrawPoolAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdrawPoolAllBtnActionPerformed(evt);
            }
        });

        jXPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel6, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel7, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel8, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel8.text")); // NOI18N

        poolPledgedFld.setEditable(false);
        poolPledgedFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.poolPledgedFld.text")); // NOI18N

        pledgedAmountFld.setEditable(false);
        pledgedAmountFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.pledgedAmountFld.text")); // NOI18N

        xiangPendingTokenFld.setEditable(false);
        xiangPendingTokenFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.xiangPendingTokenFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(withdrawBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.withdrawBtn.text")); // NOI18N
        withdrawBtn.setEnabled(false);
        withdrawBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdrawBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(stakeYAOBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.stakeYAOBtn.text")); // NOI18N
        stakeYAOBtn.setEnabled(false);
        stakeYAOBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stakeYAOBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(depositYAOBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.depositYAOBtn.text")); // NOI18N
        depositYAOBtn.setEnabled(false);
        depositYAOBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depositYAOBtnActionPerformed(evt);
            }
        });

        stakeNumberFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.stakeNumberFld.text")); // NOI18N
        stakeNumberFld.setPrompt(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.stakeNumberFld.prompt")); // NOI18N

        depositNumberFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.depositNumberFld.text")); // NOI18N
        depositNumberFld.setPrompt(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.depositNumberFld.prompt")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(maxShakeBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.maxShakeBtn.text")); // NOI18N
        maxShakeBtn.setEnabled(false);
        maxShakeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxShakeBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(maxDepositBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.maxDepositBtn.text")); // NOI18N
        maxDepositBtn.setEnabled(false);
        maxDepositBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxDepositBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn3, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.refreshBtn3.text")); // NOI18N
        refreshBtn3.setEnabled(false);
        refreshBtn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtn3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXPanel2Layout = new javax.swing.GroupLayout(jXPanel2);
        jXPanel2.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel2Layout.createSequentialGroup()
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pledgedAmountFld, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(xiangPendingTokenFld, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(poolPledgedFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(withdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(refreshBtn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jXPanel2Layout.createSequentialGroup()
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(depositNumberFld, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                            .addComponent(stakeNumberFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxShakeBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxDepositBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stakeYAOBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(depositYAOBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(poolPledgedFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshBtn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pledgedAmountFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xiangPendingTokenFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(withdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stakeNumberFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stakeYAOBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxShakeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(depositYAOBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(depositNumberFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxDepositBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXPanel5.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel9, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel10, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel11, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel11.text")); // NOI18N

        lpXiangPendingTokenFld.setEditable(false);
        lpXiangPendingTokenFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpXiangPendingTokenFld.text")); // NOI18N

        lpPledgedAmountFld.setEditable(false);
        lpPledgedAmountFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpPledgedAmountFld.text")); // NOI18N

        lpPoolPledgedFld.setEditable(false);
        lpPoolPledgedFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpPoolPledgedFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn5, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.refreshBtn5.text")); // NOI18N
        refreshBtn5.setEnabled(false);
        refreshBtn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtn5ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lpWithdrawBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpWithdrawBtn.text")); // NOI18N
        lpWithdrawBtn.setEnabled(false);
        lpWithdrawBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpWithdrawBtnActionPerformed(evt);
            }
        });

        lpStakeNumberFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpStakeNumberFld.text")); // NOI18N
        lpStakeNumberFld.setPrompt(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpStakeNumberFld.prompt")); // NOI18N

        lpDepositNumberFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpDepositNumberFld.text")); // NOI18N
        lpDepositNumberFld.setPrompt(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpDepositNumberFld.prompt")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lpMaxShakeBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpMaxShakeBtn.text")); // NOI18N
        lpMaxShakeBtn.setEnabled(false);
        lpMaxShakeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpMaxShakeBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lpMaxDepositBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpMaxDepositBtn.text")); // NOI18N
        lpMaxDepositBtn.setEnabled(false);
        lpMaxDepositBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpMaxDepositBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lpStakeYAOBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpStakeYAOBtn.text")); // NOI18N
        lpStakeYAOBtn.setEnabled(false);
        lpStakeYAOBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpStakeYAOBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lpDepositYAOBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.lpDepositYAOBtn.text")); // NOI18N
        lpDepositYAOBtn.setEnabled(false);
        lpDepositYAOBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpDepositYAOBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXPanel5Layout = new javax.swing.GroupLayout(jXPanel5);
        jXPanel5.setLayout(jXPanel5Layout);
        jXPanel5Layout.setHorizontalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lpPoolPledgedFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lpPledgedAmountFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lpXiangPendingTokenFld, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(refreshBtn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpWithdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jXPanel5Layout.createSequentialGroup()
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lpDepositNumberFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lpStakeNumberFld, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel5Layout.createSequentialGroup()
                        .addComponent(lpMaxShakeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lpStakeYAOBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel5Layout.createSequentialGroup()
                        .addComponent(lpMaxDepositBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lpDepositYAOBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jXPanel5Layout.setVerticalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpPoolPledgedFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshBtn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpPledgedAmountFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpXiangPendingTokenFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpWithdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lpStakeNumberFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpMaxShakeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpStakeYAOBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lpDepositNumberFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpMaxDepositBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpDepositYAOBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn2, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.refreshBtn2.text")); // NOI18N
        refreshBtn2.setEnabled(false);
        refreshBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtn2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn4, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.refreshBtn4.text")); // NOI18N
        refreshBtn4.setEnabled(false);
        refreshBtn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtn4ActionPerformed(evt);
            }
        });

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(withdrawAllBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.withdrawAllBtn.text")); // NOI18N
        withdrawAllBtn.setToolTipText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.withdrawAllBtn.toolTipText")); // NOI18N
        withdrawAllBtn.setEnabled(false);
        withdrawAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdrawAllBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(pickCardBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.pickCardBtn.text")); // NOI18N
        pickCardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickCardBtnActionPerformed(evt);
            }
        });

        pwdFld.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.pwdFld.text")); // NOI18N

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(peekUserDataBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.peekUserDataBtn.text")); // NOI18N
        peekUserDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peekUserDataBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(needWithdrawXiangCheckBox, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.needWithdrawXiangCheckBox.text")); // NOI18N

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(fluxContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                    .addComponent(moonContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guGuoContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kaoZiContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(410, Short.MAX_VALUE))
            .addComponent(jSeparator3)
            .addComponent(jSeparator4)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(jXLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalReleasedFld, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pendingTokenFld, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(withdrawPoolAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshBtn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(jXPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(withdrawAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pwdFld, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(needWithdrawXiangCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pickCardBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(peekUserDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jXLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalReleasedFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pendingTokenFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(withdrawPoolAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(refreshBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jSeparator6))
                    .addComponent(refreshBtn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guGuoContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kaoZiContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moonContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fluxContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jXPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jXPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(withdrawAllBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pickCardBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pwdFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(needWithdrawXiangCheckBox))
                        .addComponent(jSeparator7))
                    .addComponent(peekUserDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jXPanel1);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(reloadBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.reloadBtn.text")); // NOI18N
        reloadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn1, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.refreshBtn1.text")); // NOI18N
        refreshBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(walletFld, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectWalletBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yaoBalanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xiangBalanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(reloadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(walletFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(selectWalletBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(yaoBalanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(xiangBalanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(reloadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(refreshBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectWalletBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectWalletBtnActionPerformed
        SelectWalletDialog dlg = new SelectWalletDialog(null, Constants.CFX_BLOCKCHAIN_SYMBAL);
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == SelectWalletDialog.RET_OK) {
	    wallet = dlg.getSelectedWallet();
	    walletFld.setText(wallet.getName() + "(地址：" + wallet.getAddress() + ")");
            
            myInit();
	}
    }//GEN-LAST:event_selectWalletBtnActionPerformed

    private void walletFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_walletFldMouseClicked
        selectWalletBtnActionPerformed(null);
    }//GEN-LAST:event_walletFldMouseClicked

    private void withdrawPoolAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdrawPoolAllBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                
                setBtnEnabled(false);
                // 获得余额及质押余额
                final ProgressHandle ph = ProgressHandle.createHandle("正在提取，请稍候");
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        ph.start();

                        IStakingYAOContract contract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                        return contract.withdrawPoolAll(privateKey);
                    }

                    @Override
                    protected void done() {
                        try {
                            String hash = get();
                            System.out.println("hash==================" + hash);
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        } finally {
                            ph.finish();
                            setBtnEnabled(true);
                        }
                    }
                };
                worker.execute();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_withdrawPoolAllBtnActionPerformed

    private void reloadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadBtnActionPerformed
        myInit();
    }//GEN-LAST:event_reloadBtnActionPerformed

    private void withdrawBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdrawBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());

                withdrawBtn.setEnabled(false);
                // 获得余额及质押余额
                final ProgressHandle ph = ProgressHandle.createHandle("正在提取，请稍候");
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        ph.start();

                        IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                        return contract.withdrawPool(privateKey, BigInteger.ZERO);
                    }

                    @Override
                    protected void done() {
                        try {
                            String hash = get();
                            System.out.println("hash==================" + hash);
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        ph.finish();
                        withdrawBtn.setEnabled(true);
                    }
                };
                worker.execute();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_withdrawBtnActionPerformed

    private void stakeYAOBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stakeYAOBtnActionPerformed
//        stakeYAOBtn.setEnabled(false);
        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
        BigInteger stakeNumber = yaoCoin.mainUint2MinUint(new BigDecimal(stakeNumberFld.getText()));
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ph.start();
                
                // 首先判断钱包里的yao是否足够
                IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                BigInteger yaoBalance = yaoContract.yaoBalance(wallet.getAddress());
                return stakeNumber.compareTo(yaoBalance) <= 0;
            }

            @Override
            protected void done() {
                try {
                    Boolean result = get();
                    if(result) {
                        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
                        passwordVerifyDialog.setVisible(true);
                        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
                            String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                            
                            stakeYAOBtn.setEnabled(true);
                            final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候");
                            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                                @Override
                                protected String doInBackground() throws Exception {
                                    ph.start();

                                    IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                                    return contract.depositERC20(privateKey, stakeNumber, BigInteger.ZERO);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        String hash = get();
                                        System.out.println("hash==================" + hash);
                                    } catch (InterruptedException | ExecutionException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } finally {
                                        ph.finish();
                                        stakeYAOBtn.setEnabled(true);
                                    }
                                }
                            };
                            worker.execute();
                        }
                    } else {
                        // 提示“质押的金额超过了现有余额，请重新输入”
                        stakeNumberFld.requestFocus();
                        try {
                            JLabel lbl = new JLabel("质押的金额超过了现有余额，请重新输入");
                            BalloonTip balloonTip = new BalloonTip(stakeNumberFld, 
                                            lbl,
                                            Utils.createBalloonTipStyle(),
                                            Utils.createBalloonTipPositioner(), 
                                            null);
                            TimingUtils.showTimedBalloon(balloonTip, 2000);
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } catch (InterruptedException | ExecutionException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
//                    stakeYAOBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_stakeYAOBtnActionPerformed

    private void depositYAOBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depositYAOBtnActionPerformed
//        depositYAOBtn.setEnabled(false);
        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
        BigInteger depositNumber = yaoCoin.mainUint2MinUint(new BigDecimal(depositNumberFld.getText()));
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ph.start();
                
                // 首先判断钱包里的yao是否足够
                IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                BigInteger pledgeAmount = xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ZERO);
                return depositNumber.compareTo(pledgeAmount) <= 0;
            }

            @Override
            protected void done() {
                try {
                    Boolean result = get();
                    if(result) {
                        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
                        passwordVerifyDialog.setVisible(true);
                        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
                            String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                            
                            depositYAOBtn.setEnabled(true);
                            final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候");
                            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                                @Override
                                protected String doInBackground() throws Exception {
                                    ph.start();

                                    IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                                    return contract.withdrawERC20(privateKey, depositNumber, BigInteger.ZERO);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        String hash = get();
                                        System.out.println("hash==================" + hash);
                                    } catch (InterruptedException | ExecutionException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } finally {
                                        ph.finish();
                                        depositYAOBtn.setEnabled(true);
                                    }
                                }
                            };
                            worker.execute();
                        }
                    } else {
                        // 提示“质押的金额超过了现有余额，请重新输入”
                        depositNumberFld.requestFocus();
                        try {
                            JLabel lbl = new JLabel("取回的金额超过了现有余额，请重新输入");
                            BalloonTip balloonTip = new BalloonTip(depositNumberFld, 
                                            lbl,
                                            Utils.createBalloonTipStyle(),
                                            Utils.createBalloonTipPositioner(), 
                                            null);
                            TimingUtils.showTimedBalloon(balloonTip, 2000);
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } catch (InterruptedException | ExecutionException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
//                    depositYAOBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_depositYAOBtnActionPerformed

    private void maxShakeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxShakeBtnActionPerformed
        maxShakeBtn.setEnabled(false);
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<BigInteger, Void> worker = new SwingWorker<BigInteger, Void>() {
            @Override
            protected BigInteger doInBackground() throws Exception {
                ph.start();

                IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                return yaoContract.yaoBalance(wallet.getAddress());
            }

            @Override
            protected void done() {
                try {
                    BigInteger result = get();

                    ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                    stakeNumberFld.setText(yaoCoin.minUnit2MainUint(result).setScale(6, RoundingMode.DOWN).toPlainString());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
                    maxShakeBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_maxShakeBtnActionPerformed

    private void maxDepositBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxDepositBtnActionPerformed
        maxDepositBtn.setEnabled(false);
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<BigInteger, Void> worker = new SwingWorker<BigInteger, Void>() {
            @Override
            protected BigInteger doInBackground() throws Exception {
                ph.start();

                IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                return xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ZERO);
            }

            @Override
            protected void done() {
                try {
                    BigInteger result = get();

                    ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                    depositNumberFld.setText(yaoCoin.minUnit2MainUint(result).setScale(6, RoundingMode.DOWN).toPlainString());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
                    maxDepositBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_maxDepositBtnActionPerformed

    private void refreshBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtn1ActionPerformed
        // 刷新钱包里的yao和xiang
        if(wallet != null) {
            setBtnEnabled(false);
            final ProgressHandle ph = ProgressHandle.createHandle("正在刷新，请稍候...");
            SwingWorker<List<BigInteger>, Void> worker = new SwingWorker<List<BigInteger>, Void>() {
                @Override
                protected List<BigInteger> doInBackground() throws Exception {
                    ph.start();
                    
                    IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                    IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                    
                    List<BigInteger> list = Lists.newArrayList();
                    // YAO
                    list.add(yaoContract.yaoBalance(wallet.getAddress()));
                    
                    // XIANG
                    list.add(xiangContract.xiangBalance(wallet.getAddress()));
                    
                    return list;
                }

                @Override
                protected void done() {
                    try {
                        List<BigInteger> list = get();
                        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                        yaoBalanceFld.setText(yaoCoin.minUnit2MainUint(list.get(0)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        xiangBalanceFld.setText(yaoCoin.minUnit2MainUint(list.get(1)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        ph.finish();
                        setBtnEnabled(true);
                    }
                }
            };
            worker.execute();
        } else {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("请选择钱包");
                BalloonTip balloonTip = new BalloonTip(selectWalletBtn, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_refreshBtn1ActionPerformed

    private void refreshBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtn2ActionPerformed
        if(wallet != null) {
            setBtnEnabled(false);
            final ProgressHandle ph = ProgressHandle.createHandle("正在刷新，请稍候...");
            SwingWorker<List<BigInteger>, Void> worker = new SwingWorker<List<BigInteger>, Void>() {
                @Override
                protected List<BigInteger> doInBackground() throws Exception {
                    ph.start();
                    
                    IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                    IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                    
                    List<BigInteger> list = Lists.newArrayList();
                    // totalReleased
                    list.add(yaoContract.totalReleased());
                    
                    // pendingToken
                    BigInteger pendingToken = yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_KAOZI_PID);
                    pendingToken = pendingToken.add(yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_MOON_PID));
                    pendingToken = pendingToken.add(yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_FLUX_PID));
                    pendingToken = pendingToken.add(yaoContract.pendingToken(wallet.getAddress(), Constants.GUGUO_GUGUO_PID));
                    list.add(pendingToken);
                    
                    return list;
                }

                @Override
                protected void done() {
                    try {
                        List<BigInteger> list = get();
                        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                        
                        totalReleasedFld.setText(yaoCoin.minUnit2MainUint(list.get(0)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        pendingTokenFld.setText(yaoCoin.minUnit2MainUint(list.get(1)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        ph.finish();
                        setBtnEnabled(true);
                    }
                }
            };
            worker.execute();
        } else {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("请选择钱包");
                BalloonTip balloonTip = new BalloonTip(selectWalletBtn, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_refreshBtn2ActionPerformed

    private void refreshBtn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtn3ActionPerformed
        if(wallet != null) {
            setBtnEnabled(false);
            final ProgressHandle ph = ProgressHandle.createHandle("正在初始化，请稍候...");
            SwingWorker<List<BigInteger>, Void> worker = new SwingWorker<List<BigInteger>, Void>() {
                @Override
                protected List<BigInteger> doInBackground() throws Exception {
                    ph.start();
                    
                    IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                    
                    List<BigInteger> list = Lists.newArrayList();
                    
                    // 获得池内质押总量爻
                    list.add(xiangContract.poolPledged(BigInteger.ZERO));
                    
                    // 我的质押
                    list.add(xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ZERO));
                    
                    // 当前收益
                    list.add(xiangContract.pendingToken(wallet.getAddress(), BigInteger.ZERO));
                    
                    return list;
                }

                @Override
                protected void done() {
                    try {
                        List<BigInteger> list = get();
                        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                        poolPledgedFld.setText(yaoCoin.minUnit2MainUint(list.get(0)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        poolPledgedFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(0)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        pledgedAmountFld.setText(yaoCoin.minUnit2MainUint(list.get(1)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        pledgedAmountFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(1)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        xiangPendingTokenFld.setText(yaoCoin.minUnit2MainUint(list.get(2)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        xiangPendingTokenFld.setToolTipText(yaoCoin.minUnit2MainUint(list.get(2)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        
                        stakeNumberFld.setText("");
                        depositNumberFld.setText("");
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        ph.finish();
                        setBtnEnabled(true);
                    }
                }
            };
            worker.execute();
        } else {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("请选择钱包");
                BalloonTip balloonTip = new BalloonTip(selectWalletBtn, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_refreshBtn3ActionPerformed

    private void refreshBtn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtn4ActionPerformed
        if(wallet != null) {
            setBtnEnabled(false);
            final ProgressHandle ph = ProgressHandle.createHandle("正在刷新，请稍候...");
            SwingWorker<Map<Integer, Pair<BigInteger[], BigInteger[]>>, Void> worker = new SwingWorker<Map<Integer, Pair<BigInteger[], BigInteger[]>>, Void>() {
                @Override
                protected Map doInBackground() throws Exception {
                    ph.start();
                    
                    IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                    
                    // 当前质押的烤仔nft编号及数量
                    Pair<BigInteger[], BigInteger[]> kaoZiPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_KAOZI_PID);
                    // 当前质押的月亮创世nft编号及数量
                    Pair<BigInteger[], BigInteger[]> moonPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_MOON_PID);
                    // 当前质押的flux nft编号及数量
                    Pair<BigInteger[], BigInteger[]> fluxPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_FLUX_PID);
                    // 当前质押的古国nft编号及数量
                    Pair<BigInteger[], BigInteger[]> guGuoPledged = yaoContract.pledgedERC1155(wallet.getAddress(), Constants.GUGUO_GUGUO_PID);
                    
                    Map<Integer, Pair<BigInteger[], BigInteger[]>> map = Maps.newHashMap();
                    map.put(Constants.GUGUO_KAOZI_PID, kaoZiPledged);
                    map.put(Constants.GUGUO_MOON_PID, moonPledged);
                    map.put(Constants.GUGUO_FLUX_PID, fluxPledged);
                    map.put(Constants.GUGUO_GUGUO_PID, guGuoPledged);
                    
                    return map;
                }

                @Override
                protected void done() {
                    try {
                        Map<Integer, Pair<BigInteger[], BigInteger[]>> map = get();
                        initPanels(map.get(Constants.GUGUO_KAOZI_PID), kaoZiPanels);
                        initPanels(map.get(Constants.GUGUO_MOON_PID), moonPanels);
                        initPanels(map.get(Constants.GUGUO_FLUX_PID), fluxPanels);
                        initPanels(map.get(Constants.GUGUO_GUGUO_PID), guGuoPanels);
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        ph.finish();
                        setBtnEnabled(true);
                    }
                }
            };
            worker.execute();
        } else {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("请选择钱包");
                BalloonTip balloonTip = new BalloonTip(selectWalletBtn, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_refreshBtn4ActionPerformed

    private void refreshBtn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtn5ActionPerformed
        if(wallet != null) {
            setBtnEnabled(false);
            final ProgressHandle ph = ProgressHandle.createHandle("正在初始化，请稍候...");
            SwingWorker<List<BigInteger>, Void> worker = new SwingWorker<List<BigInteger>, Void>() {
                @Override
                protected List<BigInteger> doInBackground() throws Exception {
                    ph.start();
                    
                    IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                    
                    List<BigInteger> list = Lists.newArrayList();
                    
                    // 获得池内质押总量爻
                    list.add(xiangContract.poolPledged(BigInteger.ONE));
                    
                    // 我的质押
                    list.add(xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ONE));
                    
                    // 当前收益
                    list.add(xiangContract.pendingToken(wallet.getAddress(), BigInteger.ONE));
                    
                    return list;
                }

                @Override
                protected void done() {
                    try {
                        List<BigInteger> list = get();
                        ICoin yaoCfxCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_CFX_PAIR_SYMBOL);
                        lpPoolPledgedFld.setText(yaoCfxCoin.minUnit2MainUint(list.get(0)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        lpPoolPledgedFld.setToolTipText(yaoCfxCoin.minUnit2MainUint(list.get(0)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        lpPledgedAmountFld.setText(yaoCfxCoin.minUnit2MainUint(list.get(1)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        lpPledgedAmountFld.setToolTipText(yaoCfxCoin.minUnit2MainUint(list.get(1)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        lpXiangPendingTokenFld.setText(yaoCfxCoin.minUnit2MainUint(list.get(2)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        lpXiangPendingTokenFld.setToolTipText(yaoCfxCoin.minUnit2MainUint(list.get(2)).setScale(6, RoundingMode.HALF_DOWN).toPlainString());
                        
                        lpStakeNumberFld.setText("");
                        lpDepositNumberFld.setText("");
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        ph.finish();
                        setBtnEnabled(true);
                    }
                }
            };
            worker.execute();
        } else {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("请选择钱包");
                BalloonTip balloonTip = new BalloonTip(selectWalletBtn, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_refreshBtn5ActionPerformed

    private void lpWithdrawBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpWithdrawBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());

                lpWithdrawBtn.setEnabled(false);
                // 获得余额及质押余额
                final ProgressHandle ph = ProgressHandle.createHandle("正在提取，请稍候");
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        ph.start();

                        IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                        return contract.withdrawPool(privateKey, BigInteger.ONE);
                    }

                    @Override
                    protected void done() {
                        try {
                            String hash = get();
                            System.out.println("hash==================" + hash);
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        ph.finish();
                        lpWithdrawBtn.setEnabled(true);
                    }
                };
                worker.execute();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_lpWithdrawBtnActionPerformed

    private void lpMaxShakeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpMaxShakeBtnActionPerformed
        lpMaxShakeBtn.setEnabled(false);
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<BigInteger, Void> worker = new SwingWorker<BigInteger, Void>() {
            @Override
            protected BigInteger doInBackground() throws Exception {
                ph.start();

                IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                return yaoContract.yaoCfxBalance(wallet.getAddress());
            }

            @Override
            protected void done() {
                try {
                    BigInteger result = get();

                    ICoin yaoCfxCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_CFX_PAIR_SYMBOL);
                    lpStakeNumberFld.setText(yaoCfxCoin.minUnit2MainUint(result).setScale(6, RoundingMode.DOWN).toPlainString());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
                    lpMaxShakeBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_lpMaxShakeBtnActionPerformed

    private void lpMaxDepositBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpMaxDepositBtnActionPerformed
        lpMaxDepositBtn.setEnabled(false);
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<BigInteger, Void> worker = new SwingWorker<BigInteger, Void>() {
            @Override
            protected BigInteger doInBackground() throws Exception {
                ph.start();

                IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                return xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ONE);
            }

            @Override
            protected void done() {
                try {
                    BigInteger result = get();

                    ICoin yaoCfxCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_CFX_PAIR_SYMBOL);
                    lpDepositNumberFld.setText(yaoCfxCoin.minUnit2MainUint(result).setScale(6, RoundingMode.DOWN).toPlainString());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
                    lpMaxDepositBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_lpMaxDepositBtnActionPerformed

    private void lpStakeYAOBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpStakeYAOBtnActionPerformed
        ICoin yaoCfxCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_CFX_PAIR_SYMBOL);
        BigInteger stakeNumber = yaoCfxCoin.mainUint2MinUint(new BigDecimal(lpStakeNumberFld.getText()));
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ph.start();
                
                // 首先判断钱包里的yao是否足够
                IStakingYAOContract yaoContract = Lookup.getDefault().lookup(IStakingYAOContract.class);
                BigInteger yaoCfxBalance = yaoContract.yaoCfxBalance(wallet.getAddress());
                return stakeNumber.compareTo(yaoCfxBalance) <= 0;
            }

            @Override
            protected void done() {
                try {
                    Boolean result = get();
                    if(result) {
                        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
                        passwordVerifyDialog.setVisible(true);
                        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
                            String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                            
                            lpStakeYAOBtn.setEnabled(true);
                            final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候");
                            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                                @Override
                                protected String doInBackground() throws Exception {
                                    ph.start();

                                    IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                                    return contract.depositERC20(privateKey, stakeNumber, BigInteger.ONE);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        String hash = get();
                                        System.out.println("hash==================" + hash);
                                    } catch (InterruptedException | ExecutionException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } finally {
                                        ph.finish();
                                    }
                                }
                            };
                            worker.execute();
                        }
                    } else {
                        // 提示“质押的金额超过了现有余额，请重新输入”
                        lpStakeNumberFld.requestFocus();
                        try {
                            JLabel lbl = new JLabel("质押的金额超过了现有余额，请重新输入");
                            BalloonTip balloonTip = new BalloonTip(lpStakeNumberFld, 
                                            lbl,
                                            Utils.createBalloonTipStyle(),
                                            Utils.createBalloonTipPositioner(), 
                                            null);
                            TimingUtils.showTimedBalloon(balloonTip, 2000);
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } catch (InterruptedException | ExecutionException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
                    lpStakeYAOBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_lpStakeYAOBtnActionPerformed

    private void lpDepositYAOBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpDepositYAOBtnActionPerformed
        ICoin yaoCfxCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_CFX_PAIR_SYMBOL);
        BigInteger depositNumber = yaoCfxCoin.mainUint2MinUint(new BigDecimal(lpDepositNumberFld.getText()));
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ph.start();
                
                // 首先判断钱包里的yao是否足够
                IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                BigInteger pledgeAmount = xiangContract.pledgedAmount(wallet.getAddress(), BigInteger.ONE);
                return depositNumber.compareTo(pledgeAmount) <= 0;
            }

            @Override
            protected void done() {
                try {
                    Boolean result = get();
                    if(result) {
                        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
                        passwordVerifyDialog.setVisible(true);
                        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
                            String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                            
                            lpDepositYAOBtn.setEnabled(true);
                            final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候");
                            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                                @Override
                                protected String doInBackground() throws Exception {
                                    ph.start();

                                    IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                                    return contract.withdrawERC20(privateKey, depositNumber, BigInteger.ONE);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        String hash = get();
                                        System.out.println("hash==================" + hash);
                                    } catch (InterruptedException | ExecutionException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } finally {
                                        ph.finish();
                                        lpDepositYAOBtn.setEnabled(true);
                                    }
                                }
                            };
                            worker.execute();
                        }
                    } else {
                        // 提示“质押的金额超过了现有余额，请重新输入”
                        lpDepositNumberFld.requestFocus();
                        try {
                            JLabel lbl = new JLabel("取回的金额超过了现有余额，请重新输入");
                            BalloonTip balloonTip = new BalloonTip(lpDepositNumberFld, 
                                            lbl,
                                            Utils.createBalloonTipStyle(),
                                            Utils.createBalloonTipPositioner(), 
                                            null);
                            TimingUtils.showTimedBalloon(balloonTip, 2000);
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } catch (InterruptedException | ExecutionException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_lpDepositYAOBtnActionPerformed

    private void withdrawAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdrawAllBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());

                withdrawAllBtn.setEnabled(false);
                // 获得余额及质押余额
                final ProgressHandle ph = ProgressHandle.createHandle("正在提取，请稍候");
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        ph.start();

                        IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                        return contract.withdrawPoolAll(privateKey);
                    }

                    @Override
                    protected void done() {
                        try {
                            String hash = get();
                            System.out.println("hash==================" + hash);
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        ph.finish();
                        withdrawAllBtn.setEnabled(true);
                    }
                };
                worker.execute();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_withdrawAllBtnActionPerformed

    private void pickCardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickCardBtnActionPerformed

        if(wallet != null) {
            // 启动线程执行
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String password = String.valueOf(pwdFld.getPassword());
                        String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), password);
                        Lookup.getDefault().lookup(IGuGuoNFT.class).pickCards2(privateKey, BigInteger.ONE, needWithdrawXiangCheckBox.isSelected());
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                
            }).start();

        } else {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("请选择钱包");
                BalloonTip balloonTip = new BalloonTip(selectWalletBtn, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }        
    }//GEN-LAST:event_pickCardBtnActionPerformed

    private void peekUserDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_peekUserDataBtnActionPerformed
        TopComponent tc = WindowManager.getDefault().findTopComponent("PeekUserDataTopComponent");
	if(tc != null) {
	    tc.open();
	    tc.requestActive();
	}
    }//GEN-LAST:event_peekUserDataBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTextField depositNumberFld;
    private org.jdesktop.swingx.JXButton depositYAOBtn;
    private org.jdesktop.swingx.JXPanel fluxContainer;
    private org.jdesktop.swingx.JXPanel guGuoContainer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel10;
    private org.jdesktop.swingx.JXLabel jXLabel11;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private org.jdesktop.swingx.JXLabel jXLabel4;
    private org.jdesktop.swingx.JXLabel jXLabel5;
    private org.jdesktop.swingx.JXLabel jXLabel6;
    private org.jdesktop.swingx.JXLabel jXLabel7;
    private org.jdesktop.swingx.JXLabel jXLabel8;
    private org.jdesktop.swingx.JXLabel jXLabel9;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private org.jdesktop.swingx.JXPanel jXPanel5;
    private org.jdesktop.swingx.JXPanel kaoZiContainer;
    private org.jdesktop.swingx.JXTextField lpDepositNumberFld;
    private org.jdesktop.swingx.JXButton lpDepositYAOBtn;
    private org.jdesktop.swingx.JXButton lpMaxDepositBtn;
    private org.jdesktop.swingx.JXButton lpMaxShakeBtn;
    private org.jdesktop.swingx.JXTextField lpPledgedAmountFld;
    private org.jdesktop.swingx.JXTextField lpPoolPledgedFld;
    private org.jdesktop.swingx.JXTextField lpStakeNumberFld;
    private org.jdesktop.swingx.JXButton lpStakeYAOBtn;
    private org.jdesktop.swingx.JXButton lpWithdrawBtn;
    private org.jdesktop.swingx.JXTextField lpXiangPendingTokenFld;
    private org.jdesktop.swingx.JXButton maxDepositBtn;
    private org.jdesktop.swingx.JXButton maxShakeBtn;
    private org.jdesktop.swingx.JXPanel moonContainer;
    private javax.swing.JCheckBox needWithdrawXiangCheckBox;
    private org.jdesktop.swingx.JXButton peekUserDataBtn;
    private org.jdesktop.swingx.JXTextField pendingTokenFld;
    private org.jdesktop.swingx.JXButton pickCardBtn;
    private org.jdesktop.swingx.JXTextField pledgedAmountFld;
    private org.jdesktop.swingx.JXTextField poolPledgedFld;
    private javax.swing.JPasswordField pwdFld;
    private org.jdesktop.swingx.JXButton refreshBtn1;
    private org.jdesktop.swingx.JXButton refreshBtn2;
    private org.jdesktop.swingx.JXButton refreshBtn3;
    private org.jdesktop.swingx.JXButton refreshBtn4;
    private org.jdesktop.swingx.JXButton refreshBtn5;
    private org.jdesktop.swingx.JXButton reloadBtn;
    private org.jdesktop.swingx.JXButton selectWalletBtn;
    private org.jdesktop.swingx.JXTextField stakeNumberFld;
    private org.jdesktop.swingx.JXButton stakeYAOBtn;
    private org.jdesktop.swingx.JXTextField totalReleasedFld;
    private org.jdesktop.swingx.JXTextField walletFld;
    private org.jdesktop.swingx.JXButton withdrawAllBtn;
    private org.jdesktop.swingx.JXButton withdrawBtn;
    private org.jdesktop.swingx.JXButton withdrawPoolAllBtn;
    private org.jdesktop.swingx.JXTextField xiangBalanceFld;
    private org.jdesktop.swingx.JXTextField xiangPendingTokenFld;
    private org.jdesktop.swingx.JXTextField yaoBalanceFld;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
