package com.acuilab.bc.main.cfx;

import com.acuilab.bc.main.cfx.IFCExchange.UserInfo;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.Wallet;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import org.javatuples.Quartet;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class FcCfxDialog extends javax.swing.JDialog {
    
    private final Wallet wallet;

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form FcCfxDialog
     */
    public FcCfxDialog(Wallet wallet) {
        super((java.awt.Frame)null, true);
        initComponents();
        
        this.wallet = wallet;
        myInit(wallet.getAddress());

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }
    
    private void myInit(String address) {
        refreshBtn.setEnabled(false);
        withdrawBtn.setEnabled(false);
        // 获得余额及质押余额
        final ProgressHandle ph = ProgressHandle.createHandle("正在初始化，请稍候...");
        SwingWorker<Quartet<BigInteger, BigInteger, BigInteger, UserInfo>, Void> worker = new SwingWorker<Quartet<BigInteger, BigInteger, BigInteger, UserInfo>, Void>() {
            @Override
            protected Quartet<BigInteger, BigInteger, BigInteger, UserInfo> doInBackground() throws Exception {
                ph.start();
                
                IFCExchange exchange = Lookup.getDefault().lookup(IFCExchange.class);
                BigInteger accCfxPerFc = exchange.accCfxPerFc();
                BigInteger fcSupply = exchange.fcSupply();
                BigInteger lastStakingAmount = exchange.lastStakingAmount();
                UserInfo userInfo = exchange.userInfos(address);
                
                return new Quartet<>(accCfxPerFc, fcSupply, lastStakingAmount, userInfo);
            }

            @Override
            protected void done() {
                try {
                    Quartet<BigInteger, BigInteger, BigInteger, UserInfo> result = get();
                    
                    BigInteger accCfxPerFc = result.getValue0();
                    BigInteger fcSupply = result.getValue1();
                    BigInteger lastStakingAmount = result.getValue2();
                    UserInfo userInfo = result.getValue3();
                    
                    ICoin cfxCoin = CoinManager.getDefault().getBaseCoin(Constants.CFX_BLOCKCHAIN_SYMBAL);
                    ICoin fcCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_FC_SYMBOL);
                    
                    
                    
                    fcSupplyFld.setText(fcCoin.minUnit2MainUint(fcSupply).setScale(fcCoin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + fcCoin.getMainUnit());
                    lastStakingAmountFld.setText(cfxCoin.minUnit2MainUint(lastStakingAmount).setScale(cfxCoin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + cfxCoin.getMainUnit());
                    apyFld.setText(String.format("%.2f", lastStakingAmount.doubleValue()/fcSupply.doubleValue() * 0.04 * 100) + "%");
                    
                    myFcAmount.setText(fcCoin.minUnit2MainUint(userInfo.getAmount()).setScale(fcCoin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + fcCoin.getMainUnit());
                    accFcAmountFld.setText(fcCoin.minUnit2MainUint(userInfo.getAccumulateAmount()).setScale(fcCoin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + fcCoin.getMainUnit());
                    
                    BigInteger pendingCfxAmount = userInfo.getAmount().multiply(accCfxPerFc).divide(BigInteger.TEN.pow(18)).subtract(userInfo.getProfitDebt());
                    pendingCfxAmountFld.setText(cfxCoin.minUnit2MainUint(pendingCfxAmount).setScale(cfxCoin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + cfxCoin.getMainUnit());
                    accCfxAmount.setText(cfxCoin.minUnit2MainUint(userInfo.getAccProfit()).setScale(cfxCoin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + cfxCoin.getMainUnit());
                
                    if(userInfo.isNftGranted()) {
                        grantedTokenIdFld.setText(userInfo.getGrantedTokenId().toString());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                ph.finish();
                withdrawBtn.setEnabled(true);
                refreshBtn.setEnabled(true);
            }
        };
        worker.execute();
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        jXLabel2 = new org.jdesktop.swingx.JXLabel();
        jXLabel3 = new org.jdesktop.swingx.JXLabel();
        jXLabel4 = new org.jdesktop.swingx.JXLabel();
        jXLabel5 = new org.jdesktop.swingx.JXLabel();
        jXLabel6 = new org.jdesktop.swingx.JXLabel();
        jXLabel7 = new org.jdesktop.swingx.JXLabel();
        fcSupplyFld = new org.jdesktop.swingx.JXTextField();
        apyFld = new org.jdesktop.swingx.JXTextField();
        myFcAmount = new org.jdesktop.swingx.JXTextField();
        accFcAmountFld = new org.jdesktop.swingx.JXTextField();
        pendingCfxAmountFld = new org.jdesktop.swingx.JXTextField();
        accCfxAmount = new org.jdesktop.swingx.JXTextField();
        jXLabel8 = new org.jdesktop.swingx.JXLabel();
        lastStakingAmountFld = new org.jdesktop.swingx.JXTextField();
        refreshBtn = new org.jdesktop.swingx.JXButton();
        withdrawBtn = new org.jdesktop.swingx.JXButton();
        grantedTokenIdFld = new org.jdesktop.swingx.JXTextField();

        setTitle(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.title")); // NOI18N
        setIconImage(ImageUtilities.loadImage("/resource/gourd32.png"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel1, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel2, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel3, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel4, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel5, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel6, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel7, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel7.text")); // NOI18N

        fcSupplyFld.setEditable(false);
        fcSupplyFld.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.fcSupplyFld.text")); // NOI18N

        apyFld.setEditable(false);
        apyFld.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.apyFld.text")); // NOI18N

        myFcAmount.setEditable(false);
        myFcAmount.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.myFcAmount.text")); // NOI18N

        accFcAmountFld.setEditable(false);
        accFcAmountFld.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.accFcAmountFld.text")); // NOI18N

        pendingCfxAmountFld.setEditable(false);
        pendingCfxAmountFld.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.pendingCfxAmountFld.text")); // NOI18N

        accCfxAmount.setEditable(false);
        accCfxAmount.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.accCfxAmount.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel8, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.jXLabel8.text")); // NOI18N

        lastStakingAmountFld.setEditable(false);
        lastStakingAmountFld.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.lastStakingAmountFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.refreshBtn.text")); // NOI18N
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(withdrawBtn, org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.withdrawBtn.text")); // NOI18N

        grantedTokenIdFld.setEditable(false);
        grantedTokenIdFld.setText(org.openide.util.NbBundle.getMessage(FcCfxDialog.class, "FcCfxDialog.grantedTokenIdFld.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(apyFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(myFcAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(accFcAmountFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(accCfxAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lastStakingAmountFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fcSupplyFld, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pendingCfxAmountFld, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(withdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(grantedTokenIdFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fcSupplyFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastStakingAmountFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(apyFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myFcAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accFcAmountFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pendingCfxAmountFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(withdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accCfxAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(grantedTokenIdFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        myInit(wallet.getAddress());
    }//GEN-LAST:event_refreshBtnActionPerformed
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTextField accCfxAmount;
    private org.jdesktop.swingx.JXTextField accFcAmountFld;
    private org.jdesktop.swingx.JXTextField apyFld;
    private org.jdesktop.swingx.JXTextField fcSupplyFld;
    private org.jdesktop.swingx.JXTextField grantedTokenIdFld;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private org.jdesktop.swingx.JXLabel jXLabel4;
    private org.jdesktop.swingx.JXLabel jXLabel5;
    private org.jdesktop.swingx.JXLabel jXLabel6;
    private org.jdesktop.swingx.JXLabel jXLabel7;
    private org.jdesktop.swingx.JXLabel jXLabel8;
    private org.jdesktop.swingx.JXTextField lastStakingAmountFld;
    private org.jdesktop.swingx.JXTextField myFcAmount;
    private javax.swing.JButton okButton;
    private org.jdesktop.swingx.JXTextField pendingCfxAmountFld;
    private org.jdesktop.swingx.JXButton refreshBtn;
    private org.jdesktop.swingx.JXButton withdrawBtn;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
