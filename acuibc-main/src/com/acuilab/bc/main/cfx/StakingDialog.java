package com.acuilab.bc.main.cfx;

import com.acuilab.bc.main.coin.ICFXCoin;
import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.PasswordVerifyDialog;
import com.acuilab.bc.main.wallet.Wallet;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.examples.complete.Utils;
import net.java.balloontip.utils.TimingUtils;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author admin
 */
public class StakingDialog extends javax.swing.JDialog {
    
    public static final String AMOUNT_MUST_BE_GRATE_OR_EQUAL_1 = "数量必须大于1";	
    
    private final Wallet wallet;
    private final ICFXCoin coin;

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form StakingDialog
     */
    public StakingDialog(java.awt.Frame parent, Wallet wallet, ICFXCoin coin) {
        super(parent, true);
        initComponents();
        
        myInit(wallet.getAddress(), coin);
        
        this.wallet = wallet;
        this.coin = coin;

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
    
    private void myInit(String address, ICFXCoin coin) {
        depositBtn.setEnabled(false);
        withdrawBtn.setEnabled(false);
        // 获得余额及质押余额
        final ProgressHandle ph = ProgressHandle.createHandle("正在请求余额/质押余额，请稍候");
        SwingWorker<Pair<BigInteger, BigInteger>, Void> worker = new SwingWorker<Pair<BigInteger, BigInteger>, Void>() {
            @Override
            protected Pair<BigInteger, BigInteger> doInBackground() throws Exception {
                ph.start();
                return new Pair<>(coin.balanceOf(address), coin.stakingBalanceOf(address));
            }

            @Override
            protected void done() {
                try {
                    Pair<BigInteger, BigInteger> result = get();
                    balanceFld.setText(coin.minUnit2MainUint(result.getValue0()).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + coin.getMainUnit());
                    stakingBalanceFld.setText(coin.minUnit2MainUint(result.getValue1()).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + coin.getMainUnit());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                ph.finish();
                depositBtn.setEnabled(true);
                withdrawBtn.setEnabled(true);
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
        balanceFld = new org.jdesktop.swingx.JXTextField();
        stakingBalanceFld = new org.jdesktop.swingx.JXTextField();
        depositBtn = new org.jdesktop.swingx.JXButton();
        withdrawBtn = new org.jdesktop.swingx.JXButton();
        refreshBtn = new org.jdesktop.swingx.JXButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTextArea1 = new org.jdesktop.swingx.JXTextArea();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();

        setTitle(org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.title")); // NOI18N
        setIconImage(ImageUtilities.loadImage("/resource/gourd32.png"));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel1, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.jXLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel2, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.jXLabel2.text")); // NOI18N

        balanceFld.setEditable(false);
        balanceFld.setText(org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.balanceFld.text")); // NOI18N

        stakingBalanceFld.setEditable(false);
        stakingBalanceFld.setText(org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.stakingBalanceFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(depositBtn, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.depositBtn.text")); // NOI18N
        depositBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depositBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(withdrawBtn, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.withdrawBtn.text")); // NOI18N
        withdrawBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdrawBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.refreshBtn.text")); // NOI18N
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        jXTextArea1.setEditable(false);
        jXTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jXTextArea1.setColumns(20);
        jXTextArea1.setLineWrap(true);
        jXTextArea1.setRows(5);
        jXTextArea1.setText(org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.jXTextArea1.text")); // NOI18N
        jScrollPane1.setViewportView(jXTextArea1);

        org.openide.awt.Mnemonics.setLocalizedText(jXHyperlink1, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.jXHyperlink1.text")); // NOI18N
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jXHyperlink2, org.openide.util.NbBundle.getMessage(StakingDialog.class, "StakingDialog.jXHyperlink2.text")); // NOI18N
        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stakingBalanceFld, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                            .addComponent(balanceFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(depositBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(withdrawBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXHyperlink1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXHyperlink2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(balanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(depositBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stakingBalanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(withdrawBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXHyperlink1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXHyperlink2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
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

    private void depositBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depositBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                
                depositBtn.setEnabled(false);
                withdrawBtn.setEnabled(false);
                // 获得余额及质押余额
                final ProgressHandle ph = ProgressHandle.createHandle("正在质押，请稍候");
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        ph.start();

                        BigInteger balance = coin.balanceOf(wallet.getAddress());
                        if(balance.compareTo(Constants.CFX_ONE) < 0) {
			    return AMOUNT_MUST_BE_GRATE_OR_EQUAL_1;
			} 
                        return coin.deposit(privateKey, balance.subtract(Constants.CFX_ONE));
                    }

                    @Override
                    protected void done() {
                        try {
                            String hash = get();
			    if(StringUtils.equals(hash, AMOUNT_MUST_BE_GRATE_OR_EQUAL_1)) {
				try {
				    JLabel lbl = new JLabel(AMOUNT_MUST_BE_GRATE_OR_EQUAL_1);
				    BalloonTip balloonTip = new BalloonTip(balanceFld, 
						    lbl,
						    Utils.createBalloonTipStyle(),
						    Utils.createBalloonTipPositioner(), 
						    null);
				    TimingUtils.showTimedBalloon(balloonTip, 3000);
				} catch (Exception ex) {
				    Exceptions.printStackTrace(ex);
				}
			    }
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        ph.finish();
                        depositBtn.setEnabled(true);
                        withdrawBtn.setEnabled(true);
                    }
                };
                worker.execute();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_depositBtnActionPerformed

    private void withdrawBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdrawBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                
                depositBtn.setEnabled(false);
                withdrawBtn.setEnabled(false);
                // 获得余额及质押余额
                final ProgressHandle ph = ProgressHandle.createHandle("正在取现，请稍候");
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        ph.start();

                        BigInteger stakingBalance = coin.stakingBalanceOf(wallet.getAddress());
                        return coin.withdraw(privateKey, stakingBalance);
                    }

                    @Override
                    protected void done() {
                        try {
                            String hash = get();
                            System.out.println("withdraw hash=" + hash);
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        ph.finish();
                        depositBtn.setEnabled(true);
                        withdrawBtn.setEnabled(true);
                    }
                };
                worker.execute();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_withdrawBtnActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        myInit(wallet.getAddress(), coin);
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        if(Desktop.isDesktopSupported()) {
            try {
                if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    // 打开默认浏览器
                    Desktop.getDesktop().browse(URI.create("https://www.jinse.com/news/blockchain/921888.html"));
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

    private void jXHyperlink2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink2ActionPerformed
        if(Desktop.isDesktopSupported()) {
            try {
                if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    // 打开默认浏览器
                    Desktop.getDesktop().browse(URI.create("https://juejin.cn/post/6876330619798814728"));
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_jXHyperlink2ActionPerformed
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTextField balanceFld;
    private org.jdesktop.swingx.JXButton depositBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXTextArea jXTextArea1;
    private javax.swing.JButton okButton;
    private org.jdesktop.swingx.JXButton refreshBtn;
    private org.jdesktop.swingx.JXTextField stakingBalanceFld;
    private org.jdesktop.swingx.JXButton withdrawBtn;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
