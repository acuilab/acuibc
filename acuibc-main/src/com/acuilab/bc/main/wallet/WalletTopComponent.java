package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.wallet.common.PasswordVerifyDialog;
import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.dao.WalletDAO;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.util.AESUtil;
import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.examples.complete.Utils;
import net.java.balloontip.utils.TimingUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.springframework.util.DigestUtils;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.NFTManager;
import com.acuilab.bc.main.nft.INFT;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "WalletTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
public final class WalletTopComponent extends TopComponent implements Observer {
    
    private static final AtomicInteger ID = new AtomicInteger();
    private final String PREFERRED_ID;  // 20200802
    private final Wallet wallet;

    public WalletTopComponent(Wallet wallet) {
        initComponents();
        setName(wallet.getName() + "(" + StringUtils.substring(wallet.getAddress(), StringUtils.length(wallet.getAddress()) - 4) + ")");
        setToolTipText(wallet.getName());
        BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());
        this.setIcon(bc.getIconImage(16));
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.FALSE);

        this.wallet = wallet;
        
        // 收款二维码图像
        try {  
            Map hints = Maps.newHashMap();  
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  
            hints.put(EncodeHintType.MARGIN, 0);  
            BitMatrix byteMatrix = new MultiFormatWriter().encode(wallet.getAddress(), BarcodeFormat.QR_CODE, 128, 128, hints);
            barcodeLbl.setIcon(new ImageIcon(MatrixToImageWriter.toBufferedImage(byteMatrix)));
        } catch (WriterException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        jXLabel1.setIcon(bc.getIcon(128));
        walletNameFld.setText(wallet.getName());
        walletAddressFld.setText(wallet.getAddress());
        hashLink.setEnabled(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
        hashLink.setText("");
        
        mnemonicExportBtn.setEnabled(StringUtils.isNotBlank(wallet.getMnemonicAES()));
        if(StringUtils.isBlank(wallet.getMnemonicAES())) {
            mnemonicExportBtn.setToolTipText("私钥导入，无助记词");
        }
	
	// 初始化coinPane
	for(ICoin coin : CoinManager.getDefault().getCoinList(wallet.getBlockChainSymbol())) {
	    coinPane.addTab(coin.getSymbol(), coin.getIcon(16), new CoinPanel(WalletTopComponent.this, wallet, coin), coin.getName());
	}
	
	// 初始化NFTPanel
	int i=0;
	for(INFT nft : NFTManager.getDefault().getNFTList(wallet.getBlockChainSymbol())) {
	    JPanel nftPanel = new NFTListPanel(WalletTopComponent.this, wallet, nft, i);
	    nftPane.addTab(nft.getName(), nft.getIcon(16), nftPanel, nft.getName());
	    i++;
	}
        
        // 这是一个新打开的窗口，生成新的窗口id并保存
        int id = ID.incrementAndGet();
        PREFERRED_ID = NbBundle.getMessage(WalletTopComponent.class, "ID_WalletTopComponent", Integer.toString(id));
    }
    
    public JXHyperlink getHashLink() {
        return hashLink;
    }
    
    public void setNftPaneTitleAt(int index, String title) {
	nftPane.setTitleAt(index, title);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane1 = new javax.swing.JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        coinPane = new javax.swing.JTabbedPane();
        nftPane = new javax.swing.JTabbedPane();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        walletNameFld = new org.jdesktop.swingx.JXTextField();
        walletAddressFld = new org.jdesktop.swingx.JXTextField();
        jXLabel2 = new org.jdesktop.swingx.JXLabel();
        jXLabel3 = new org.jdesktop.swingx.JXLabel();
        mnemonicExportBtn = new org.jdesktop.swingx.JXButton();
        privateKeyBtn = new org.jdesktop.swingx.JXButton();
        barcodeLbl = new org.jdesktop.swingx.JXLabel();
        pwdEditBtn = new org.jdesktop.swingx.JXButton();
        nameEditBtn = new org.jdesktop.swingx.JXButton();
        jXLabel4 = new org.jdesktop.swingx.JXLabel();
        hashLink = new org.jdesktop.swingx.JXHyperlink();
        copyHashBtn = new org.jdesktop.swingx.JXButton();
        jXLabel5 = new org.jdesktop.swingx.JXLabel();
        jXLabel6 = new org.jdesktop.swingx.JXLabel();
        copyAddressBtn = new org.jdesktop.swingx.JXButton();

        tabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPane1StateChanged(evt);
            }
        });

        coinPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                coinPaneStateChanged(evt);
            }
        });
        tabbedPane1.addTab(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.coinPane.TabConstraints.tabTitle"), coinPane); // NOI18N

        nftPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                nftPaneStateChanged(evt);
            }
        });
        tabbedPane1.addTab(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.nftPane.TabConstraints.tabTitle"), nftPane); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel1, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.jXLabel1.text")); // NOI18N
        jXLabel1.setMaximumSize(new java.awt.Dimension(128, 128));
        jXLabel1.setMinimumSize(new java.awt.Dimension(128, 128));
        jXLabel1.setPreferredSize(new java.awt.Dimension(128, 128));

        walletNameFld.setEditable(false);
        walletNameFld.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        walletNameFld.setForeground(new java.awt.Color(0, 0, 255));
        walletNameFld.setText(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.walletNameFld.text")); // NOI18N

        walletAddressFld.setEditable(false);
        walletAddressFld.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        walletAddressFld.setForeground(new java.awt.Color(0, 0, 255));
        walletAddressFld.setText(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.walletAddressFld.text")); // NOI18N
        walletAddressFld.setMaximumSize(new java.awt.Dimension(100, 20));
        walletAddressFld.setMinimumSize(new java.awt.Dimension(100, 20));
        walletAddressFld.setPreferredSize(new java.awt.Dimension(100, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel2, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.jXLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel3, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.jXLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(mnemonicExportBtn, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.mnemonicExportBtn.text")); // NOI18N
        mnemonicExportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnemonicExportBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(privateKeyBtn, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.privateKeyBtn.text")); // NOI18N
        privateKeyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateKeyBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(barcodeLbl, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.barcodeLbl.text")); // NOI18N
        barcodeLbl.setToolTipText(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.barcodeLbl.toolTipText")); // NOI18N
        barcodeLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                barcodeLblMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(pwdEditBtn, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.pwdEditBtn.text")); // NOI18N
        pwdEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pwdEditBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(nameEditBtn, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.nameEditBtn.text")); // NOI18N
        nameEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameEditBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel4, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.jXLabel4.text")); // NOI18N
        jXLabel4.setToolTipText(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.jXLabel4.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(hashLink, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.hashLink.text")); // NOI18N
        hashLink.setMaximumSize(new java.awt.Dimension(100, 18));
        hashLink.setMinimumSize(new java.awt.Dimension(100, 18));
        hashLink.setPreferredSize(new java.awt.Dimension(100, 18));
        hashLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hashLinkActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(copyHashBtn, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.copyHashBtn.text")); // NOI18N
        copyHashBtn.setToolTipText(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.copyHashBtn.toolTipText")); // NOI18N
        copyHashBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyHashBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel5, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.jXLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel6, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.jXLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(copyAddressBtn, org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.copyAddressBtn.text")); // NOI18N
        copyAddressBtn.setToolTipText(org.openide.util.NbBundle.getMessage(WalletTopComponent.class, "WalletTopComponent.copyAddressBtn.toolTipText")); // NOI18N
        copyAddressBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyAddressBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jXLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jXLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jXLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(walletNameFld, javax.swing.GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(walletAddressFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(hashLink, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(copyHashBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(copyAddressBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jXLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jXLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(3, 3, 3))))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(139, 139, 139)
                                .addComponent(nameEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pwdEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mnemonicExportBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(privateKeyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barcodeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(walletNameFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(walletAddressFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jXLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(copyAddressBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jXLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(hashLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(copyHashBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jXLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nameEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(pwdEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mnemonicExportBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(privateKeyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(barcodeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mnemonicExportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnemonicExportBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String mnemonic = AESUtil.decrypt(wallet.getMnemonicAES(), passwordVerifyDialog.getPassword());
                ExportMnemonicDialog exportMnemonicDialog = new ExportMnemonicDialog(null, wallet, mnemonic);
                exportMnemonicDialog.setVisible(true);
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_mnemonicExportBtnActionPerformed

    private void privateKeyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateKeyBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
                ExportPrivateKeyDialog exportPrivateKeyDialog = new ExportPrivateKeyDialog(null, wallet, privateKey);
                exportPrivateKeyDialog.setVisible(true);
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_privateKeyBtnActionPerformed

    private void barcodeLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barcodeLblMouseClicked
        if(evt.getClickCount() == 2){   
            Transferable trans = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.imageFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.imageFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    if (isDataFlavorSupported(flavor)) {
                        return ((ImageIcon)barcodeLbl.getIcon()).getImage();
                    }
                    throw new UnsupportedFlavorException(flavor);
                }

            };
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
            
            // 气泡提示
	    try {
                JLabel lbl = new JLabel("二维码图像已复制到剪贴板");
		BalloonTip balloonTip = new BalloonTip(barcodeLbl, 
				lbl,
				Utils.createBalloonTipStyle(),
				Utils.createBalloonTipPositioner(), 
				null);
		TimingUtils.showTimedBalloon(balloonTip, 6000);
	    } catch (Exception ex) {
		Exceptions.printStackTrace(ex);
	    }
        }
    }//GEN-LAST:event_barcodeLblMouseClicked

    private void nameEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameEditBtnActionPerformed
        PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
        passwordVerifyDialog.setVisible(true);
        if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
            try {
                WalletNameModifyDialog dlg = new WalletNameModifyDialog(null, wallet.getName());
                dlg.setVisible(true);
                if(dlg.getReturnStatus() == WalletNameModifyDialog.RET_OK) {
                    WalletDAO.updateName(wallet.getName(), dlg.getNewName());
                }
                
                // 更新Wallet(同时通过观察者模式更新相关UI)
                wallet.setName(dlg.getNewName());
                
                // 更新TopComponent标题
                this.setName(dlg.getNewName());
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
            
        }
    }//GEN-LAST:event_nameEditBtnActionPerformed

    private void pwdEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pwdEditBtnActionPerformed
        PasswordModifyDialog dlg = new PasswordModifyDialog(null, wallet);
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == PasswordModifyDialog.RET_OK) {
            try {
                // 先根据旧密码获得助记词及私钥
                String oldPwd = dlg.getOldPwd();
                String mnemonicWords = AESUtil.decrypt(wallet.getMnemonicAES(), oldPwd);
                String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), oldPwd);

                // 根据新密码加密
                String newPwd = dlg.getNewPwd();
                String pwdMD5 = DigestUtils.md5DigestAsHex(newPwd.getBytes()); 
                String mnemonicAES = AESUtil.encrypt(StringUtils.join(mnemonicWords, " "), newPwd);
                String privateKeyAES = AESUtil.encrypt(privateKey, newPwd);

                WalletDAO.updatePwd(wallet.getName(), pwdMD5, mnemonicAES, privateKeyAES);
                
                // 更新wallet相关值
                wallet.setPwdMD5(pwdMD5);
                wallet.setMnemonicAES(mnemonicAES);
                wallet.setPrivateKeyAES(privateKeyAES);
                
                // 气泡提示
                try {
                    JLabel lbl = new JLabel("新密码已设置");
                    BalloonTip balloonTip = new BalloonTip(pwdEditBtn, 
                                    lbl,
                                    Utils.createBalloonTipStyle(),
                                    Utils.createBalloonTipPositioner(), 
                                    null);
                    TimingUtils.showTimedBalloon(balloonTip, 2000);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            } catch (SQLException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_pwdEditBtnActionPerformed

    private void hashLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hashLinkActionPerformed
        if(StringUtils.isBlank(hashLink.getText())) {
            return;
        }
        
        if(Desktop.isDesktopSupported()) {
            try {
                if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());
                    String transactionDetailUrl = bc.getTransactionDetailUrl(hashLink.getText());
                    if(transactionDetailUrl != null) {
                        // 打开默认浏览器
                       Desktop.getDesktop().browse(URI.create(transactionDetailUrl));
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_hashLinkActionPerformed

    private void copyHashBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyHashBtnActionPerformed
        // 复制交易哈希
        String hash = hashLink.getText();
        if(StringUtils.isNotBlank(hash)) {
            Transferable str = new StringSelection(hash); 
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);
            
            // 气泡提示
	    try {
                JLabel lbl = new JLabel("交易哈希复制成功");
		BalloonTip balloonTip = new BalloonTip(copyHashBtn, 
				lbl,
				Utils.createBalloonTipStyle(),
				Utils.createBalloonTipPositioner(), 
				null);
		TimingUtils.showTimedBalloon(balloonTip, 2000);
	    } catch (Exception ex) {
		Exceptions.printStackTrace(ex);
	    }
        } else {
            // 气泡提示
	    try {
                JLabel lbl = new JLabel("交易哈希不存在");
		BalloonTip balloonTip = new BalloonTip(copyHashBtn, 
				lbl,
				Utils.createBalloonTipStyle(),
				Utils.createBalloonTipPositioner(), 
				null);
		TimingUtils.showTimedBalloon(balloonTip, 2000);
	    } catch (Exception ex) {
		Exceptions.printStackTrace(ex);
	    }
        }

    }//GEN-LAST:event_copyHashBtnActionPerformed

    private void copyAddressBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyAddressBtnActionPerformed
        // 复制钱包地址
        String address = walletAddressFld.getText();
        if(StringUtils.isNotBlank(address)) {
            Transferable str = new StringSelection(address); 
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);
            
            // 气泡提示
	    try {
                JLabel lbl = new JLabel("钱包地址复制成功");
		BalloonTip balloonTip = new BalloonTip(copyAddressBtn  , 
				lbl,
				Utils.createBalloonTipStyle(),
				Utils.createBalloonTipPositioner(), 
				null);
		TimingUtils.showTimedBalloon(balloonTip, 2000);
	    } catch (Exception ex) {
		Exceptions.printStackTrace(ex);
	    }
        }
    }//GEN-LAST:event_copyAddressBtnActionPerformed

    private void coinPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_coinPaneStateChanged
	// 代币被选中
        tabbedPane1StateChanged(evt);
    }//GEN-LAST:event_coinPaneStateChanged

    private void nftPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_nftPaneStateChanged
        // 藏品被选中
        tabbedPane1StateChanged(evt);
    }//GEN-LAST:event_nftPaneStateChanged

    private void tabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPane1StateChanged
	switch (tabbedPane1.getSelectedIndex()) {
            case 0:
                // 代币被选中
                CoinPanel selectedCoinPanel = (CoinPanel)coinPane.getSelectedComponent();
                if(selectedCoinPanel != null) {
                    if(selectedCoinPanel.isFirstOpen()){
			System.out.println("selectedCoinPanel.refreshBtnActionPerformed()");
                        selectedCoinPanel.refreshBtnActionPerformed();
                    }
                }
                break;
            case 1:
                // 藏品被选中
                NFTListPanel selectedNFTListPanel = (NFTListPanel)nftPane.getSelectedComponent();
                if(selectedNFTListPanel != null) {
                    //如果是首次调用则重载，如果非首次（已有）则不做操作。
                    if(selectedNFTListPanel.isFirstOpen()){
                        selectedNFTListPanel.reload();
                    }
                }
                break;
            default:
                break;
        }
    }//GEN-LAST:event_tabbedPane1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXLabel barcodeLbl;
    private javax.swing.JTabbedPane coinPane;
    private org.jdesktop.swingx.JXButton copyAddressBtn;
    private org.jdesktop.swingx.JXButton copyHashBtn;
    private org.jdesktop.swingx.JXHyperlink hashLink;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private org.jdesktop.swingx.JXLabel jXLabel4;
    private org.jdesktop.swingx.JXLabel jXLabel5;
    private org.jdesktop.swingx.JXLabel jXLabel6;
    private org.jdesktop.swingx.JXButton mnemonicExportBtn;
    private org.jdesktop.swingx.JXButton nameEditBtn;
    private javax.swing.JTabbedPane nftPane;
    private org.jdesktop.swingx.JXButton privateKeyBtn;
    private org.jdesktop.swingx.JXButton pwdEditBtn;
    private javax.swing.JTabbedPane tabbedPane1;
    private org.jdesktop.swingx.JXTextField walletAddressFld;
    private org.jdesktop.swingx.JXTextField walletNameFld;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        wallet.addObserver(this);
    }

    @Override
    public void componentClosed() {
        wallet.addObserver(this);
    }
    
    // 覆盖此方法，为TopComponent设置preferredID
    // Subclasses are encouraged to override this method to provide preferred value for unique TopComponent ID returned 
    // by WindowManager.findTopComponentID(org.openide.windows.TopComponent). Returned value should be a String, preferably 
    // describing semantics of TopComponent subclass, such as "PieChartViewer" or "HtmlEditor" etc. Value is then used by window 
    // system as prefix value for creating unique TopComponent ID. Returned String value should be preferably unique, but need not be. 
    @Override
    protected String preferredID() {
        return PREFERRED_ID;
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

    @Override
    public void update(Observable o, Object arg) {
        walletNameFld.setText(((Wallet)o).getName());
    }
}
