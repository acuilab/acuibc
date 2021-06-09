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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.examples.complete.Utils;
import net.java.balloontip.utils.TimingUtils;
import org.javatuples.Pair;
import org.javatuples.Septet;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

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
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
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
    }
    
    private void myInit() {
        if(wallet != null) {
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
                    list.add(xiangContract.poolPledged());
                    
                    // 我的质押
                    list.add(xiangContract.pledgedAmount(wallet.getAddress()));
                    
                    // 当前收益
                    list.add(xiangContract.pendingToken(wallet.getAddress()));
                    
                    return new Pair<>(list, map);
                }

                @Override
                protected void done() {
                    try {
                        withdrawPoolAllBtn.setEnabled(true);
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
                        pledgedAmountFld.setText(yaoCoin.minUnit2MainUint(list.get(5)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        
                        xiangPendingTokenFld.setText(yaoCoin.minUnit2MainUint(list.get(6)).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        
                        Map<Integer, Pair<BigInteger[], BigInteger[]>> map = result.getValue1();
                        initPanels(map.get(Constants.GUGUO_KAOZI_PID), kaoZiPanels);
                        initPanels(map.get(Constants.GUGUO_MOON_PID), moonPanels);
                        initPanels(map.get(Constants.GUGUO_FLUX_PID), fluxPanels);
                        initPanels(map.get(Constants.GUGUO_GUGUO_PID), guGuoPanels);
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        ph.finish();
                    }
                }
            };
            worker.execute();
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
        refreshBtn = new org.jdesktop.swingx.JXButton();
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
        jXPanel5 = new org.jdesktop.swingx.JXPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jXLabel9 = new org.jdesktop.swingx.JXLabel();
        jXTextField1 = new org.jdesktop.swingx.JXTextField();

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

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.refreshBtn.text")); // NOI18N
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
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
        withdrawBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdrawBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(stakeYAOBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.stakeYAOBtn.text")); // NOI18N
        stakeYAOBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stakeYAOBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(depositYAOBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.depositYAOBtn.text")); // NOI18N
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
        maxShakeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxShakeBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(maxDepositBtn, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.maxDepositBtn.text")); // NOI18N
        maxDepositBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxDepositBtnActionPerformed(evt);
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
                            .addComponent(poolPledgedFld, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(withdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(poolPledgedFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        javax.swing.GroupLayout jXPanel5Layout = new javax.swing.GroupLayout(jXPanel5);
        jXPanel5.setLayout(jXPanel5Layout);
        jXPanel5Layout.setHorizontalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );
        jXPanel5Layout.setVerticalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel9, org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXLabel9.text")); // NOI18N

        jXTextField1.setText(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXTextField1.text")); // NOI18N

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
                .addContainerGap(136, Short.MAX_VALUE))
            .addComponent(jSeparator3)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addComponent(jXLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalReleasedFld, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pendingTokenFld, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(withdrawPoolAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSeparator4)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(jXPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(jXLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalReleasedFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pendingTokenFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(withdrawPoolAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jXPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jXPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jXPanel1);

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
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
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
                
                withdrawPoolAllBtn.setEnabled(false);
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
                        }

                        ph.finish();
                        withdrawPoolAllBtn.setEnabled(true);
                    }
                };
                worker.execute();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_withdrawPoolAllBtnActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        myInit();
    }//GEN-LAST:event_refreshBtnActionPerformed

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
        stakeYAOBtn.setEnabled(false);
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
                                    return contract.depositERC20(privateKey, stakeNumber);
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
                    stakeYAOBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_stakeYAOBtnActionPerformed

    private void depositYAOBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depositYAOBtnActionPerformed
        depositYAOBtn.setEnabled(false);
        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
        BigInteger depositNumber = yaoCoin.mainUint2MinUint(new BigDecimal(depositNumberFld.getText()));
        final ProgressHandle ph = ProgressHandle.createHandle("正在处理，请稍候...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ph.start();
                
                // 首先判断钱包里的yao是否足够
                IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                BigInteger pledgeAmount = xiangContract.pledgedAmount(wallet.getAddress());
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
                                    return contract.withdrawERC20(privateKey, depositNumber);
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
                    depositYAOBtn.setEnabled(true);
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
                return xiangContract.pledgedAmount(wallet.getAddress());
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
    private org.jdesktop.swingx.JXLabel jXLabel1;
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
    private org.jdesktop.swingx.JXTextField jXTextField1;
    private org.jdesktop.swingx.JXPanel kaoZiContainer;
    private org.jdesktop.swingx.JXButton maxDepositBtn;
    private org.jdesktop.swingx.JXButton maxShakeBtn;
    private org.jdesktop.swingx.JXPanel moonContainer;
    private org.jdesktop.swingx.JXTextField pendingTokenFld;
    private org.jdesktop.swingx.JXTextField pledgedAmountFld;
    private org.jdesktop.swingx.JXTextField poolPledgedFld;
    private org.jdesktop.swingx.JXButton refreshBtn;
    private org.jdesktop.swingx.JXButton selectWalletBtn;
    private org.jdesktop.swingx.JXTextField stakeNumberFld;
    private org.jdesktop.swingx.JXButton stakeYAOBtn;
    private org.jdesktop.swingx.JXTextField totalReleasedFld;
    private org.jdesktop.swingx.JXTextField walletFld;
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
