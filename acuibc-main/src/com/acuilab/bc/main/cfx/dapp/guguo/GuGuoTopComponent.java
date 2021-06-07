package com.acuilab.bc.main.cfx.dapp.guguo;

import com.acuilab.bc.main.cfx.IGuGuoContract;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.Wallet;
import com.acuilab.bc.main.wallet.common.SelectWalletDialog;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.javatuples.Pair;
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
            SwingWorker<Pair<BigInteger, BigInteger>, Void> worker = new SwingWorker<Pair<BigInteger, BigInteger>, Void>() {
                @Override
                protected Pair<BigInteger, BigInteger> doInBackground() throws Exception {
                    ph.start();
                    
                    IGuGuoContract contract = Lookup.getDefault().lookup(IGuGuoContract.class);
                    // YAO
                    BigInteger yaoBalance = contract.yaoBalance(wallet.getAddress());
                    
                    // XIANG
                    BigInteger xiangBalance = contract.xiangBalance(wallet.getAddress());
                    
                    // 当前质押的烤仔nft编号
                    BigInteger[] kaoZiIds = contract.pledgedERC1155(wallet.getAddress(), IGuGuoContract.KAOZI_PID);
                    // 当前质押的月亮创世nft编号
                    contract.pledgedERC1155(wallet.getAddress(), MOON_PID);
                    // 当前质押的flux nft编号
                    contract.pledgedERC1155(wallet.getAddress(), FLUX_PID);
                    // 当前质押的古国nft编号
                    contract.pledgedERC1155(wallet.getAddress(), GUGUO_PID);
                    
                    
                    return new Pair<>(yaoBalance, xiangBalance);
                }

                @Override
                protected void done() {
                    try {
                        Pair<BigInteger, BigInteger> result = get();
                        
                        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                        yaoBalanceFld.setText(yaoCoin.minUnit2MainUint(result.getValue0()).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                        xiangBalanceFld.setText(yaoCoin.minUnit2MainUint(result.getValue1()).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
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
        jXPanel3 = new org.jdesktop.swingx.JXPanel();
        jXPanel4 = new org.jdesktop.swingx.JXPanel();
        kaoZiContainer = new org.jdesktop.swingx.JXPanel();
        moonContainer = new org.jdesktop.swingx.JXPanel();
        fluxContainer = new org.jdesktop.swingx.JXPanel();

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

        jXPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXPanel3.border.title"))); // NOI18N

        javax.swing.GroupLayout jXPanel3Layout = new javax.swing.GroupLayout(jXPanel3);
        jXPanel3.setLayout(jXPanel3Layout);
        jXPanel3Layout.setHorizontalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jXPanel3Layout.setVerticalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jXPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.jXPanel4.border.title"))); // NOI18N

        javax.swing.GroupLayout jXPanel4Layout = new javax.swing.GroupLayout(jXPanel4);
        jXPanel4.setLayout(jXPanel4Layout);
        jXPanel4Layout.setHorizontalGroup(
            jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jXPanel4Layout.setVerticalGroup(
            jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 89, Short.MAX_VALUE)
        );

        kaoZiContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.kaoZiContainer.border.title"))); // NOI18N
        kaoZiContainer.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        kaoZiContainer.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        moonContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.moonContainer.border.title"))); // NOI18N
        moonContainer.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        moonContainer.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        fluxContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GuGuoTopComponent.class, "GuGuoTopComponent.fluxContainer.border.title"))); // NOI18N
        fluxContainer.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        fluxContainer.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jXPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(fluxContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                    .addComponent(moonContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guGuoContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kaoZiContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addComponent(guGuoContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kaoZiContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moonContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fluxContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXPanel fluxContainer;
    private org.jdesktop.swingx.JXPanel guGuoContainer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXPanel jXPanel3;
    private org.jdesktop.swingx.JXPanel jXPanel4;
    private org.jdesktop.swingx.JXPanel kaoZiContainer;
    private org.jdesktop.swingx.JXPanel moonContainer;
    private org.jdesktop.swingx.JXButton selectWalletBtn;
    private org.jdesktop.swingx.JXTextField walletFld;
    private org.jdesktop.swingx.JXTextField xiangBalanceFld;
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
