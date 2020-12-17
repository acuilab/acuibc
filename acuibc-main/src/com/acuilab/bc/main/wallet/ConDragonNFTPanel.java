package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.nft.INFT;
import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.wallet.wizard.NFTTransferConfirmWizardPanel;
import com.acuilab.bc.main.wallet.wizard.NFTTransferInputWizardPanel;
import com.acuilab.bc.main.wallet.wizard.PasswordInputWizardPanel;
import java.awt.Component;
import java.awt.Image;
import java.math.BigInteger;
import org.jdesktop.swingx.JXPanel;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.examples.complete.Utils;
import net.java.balloontip.utils.TimingUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdesktop.swingx.JXHyperlink;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
/**
 *
 * @author admin
 */
public class ConDragonNFTPanel extends JXPanel {
    
    private final Wallet wallet;
    private final INFT nft;
    private final MetaData metaData;

    /**
     * Creates new form NFTPanel
     */
    public ConDragonNFTPanel(Wallet wallet, INFT nft, MetaData metaData) {
	initComponents();
	this.wallet = wallet;
	this.nft = nft;
	this.metaData = metaData;
	
        idLbl.setText("编号：" + metaData.getId());
        
        nameLbl.setText("名称：" + metaData.getName());
        platformLbl.setText("平台：" + metaData.getPlatform());
        descTextArea.setText("描述：" + metaData.getDesc());
	descTextArea.setToolTipText(metaData.getDesc());
        
        try {
            URL url = new URL(metaData.getImageUrl());
	    imageView.setImage(url);
	    Image image = imageView.getImage();
	    double scaleX = 178.0d/image.getWidth(null);
	    double scaleY = 178.0d/image.getHeight(null);
            imageView.setScale(Math.max(scaleX, scaleY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //this.jXImageView1.setImage(urlImage));
        //8016{"body":"000","eye":"013","nose":"001","auxiliary":"001","background":"000","clothes":"006","props":"000","title":"002_000_000_013_001_000_001_000","url":"002_000_000_013_001_000_001_000.png"}
        //http://cdn.tspace.online/image/finish/001_000_000_003_005_001_004_001.png
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLbl = new org.jdesktop.swingx.JXLabel();
        idLbl = new org.jdesktop.swingx.JXLabel();
        platformLbl = new org.jdesktop.swingx.JXLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descTextArea = new org.jdesktop.swingx.JXTextArea();
        transferBtn = new org.jdesktop.swingx.JXButton();
        imageView = new org.jdesktop.swingx.JXImageView();

        setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleGradient"));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setMaximumSize(new java.awt.Dimension(180, 320));
        setMinimumSize(new java.awt.Dimension(180, 320));
        setPreferredSize(new java.awt.Dimension(180, 320));

        org.openide.awt.Mnemonics.setLocalizedText(nameLbl, org.openide.util.NbBundle.getMessage(ConDragonNFTPanel.class, "ConDragonNFTPanel.nameLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(idLbl, org.openide.util.NbBundle.getMessage(ConDragonNFTPanel.class, "ConDragonNFTPanel.idLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(platformLbl, org.openide.util.NbBundle.getMessage(ConDragonNFTPanel.class, "ConDragonNFTPanel.platformLbl.text")); // NOI18N

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        descTextArea.setEditable(false);
        descTextArea.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleGradient"));
        descTextArea.setBorder(null);
        descTextArea.setColumns(20);
        descTextArea.setLineWrap(true);
        descTextArea.setRows(5);
        descTextArea.setText(org.openide.util.NbBundle.getMessage(ConDragonNFTPanel.class, "ConDragonNFTPanel.descTextArea.text")); // NOI18N
        jScrollPane1.setViewportView(descTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(transferBtn, org.openide.util.NbBundle.getMessage(ConDragonNFTPanel.class, "ConDragonNFTPanel.transferBtn.text")); // NOI18N
        transferBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transferBtnActionPerformed(evt);
            }
        });

        imageView.setMaximumSize(new java.awt.Dimension(178, 178));
        imageView.setMinimumSize(new java.awt.Dimension(178, 178));
        imageView.setPreferredSize(new java.awt.Dimension(178, 178));

        javax.swing.GroupLayout imageViewLayout = new javax.swing.GroupLayout(imageView);
        imageView.setLayout(imageViewLayout);
        imageViewLayout.setHorizontalGroup(
            imageViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        imageViewLayout.setVerticalGroup(
            imageViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 178, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nameLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(idLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(platformLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(transferBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(imageView, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(imageView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(platformLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transferBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void transferBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transferBtnActionPerformed
	List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
	panels.add(new NFTTransferInputWizardPanel(wallet, nft));
	panels.add(new PasswordInputWizardPanel(wallet));
	panels.add(new NFTTransferConfirmWizardPanel(wallet, nft));
	String[] steps = new String[panels.size()];
	for (int i = 0; i < panels.size(); i++) {
	    Component c = panels.get(i).getComponent();
	    // Default step name to component name of panel.
	    steps[i] = c.getName();
	    if (c instanceof JComponent) { // assume Swing components
		JComponent jc = (JComponent) c;
		jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
		jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
		jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
		jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
		jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
	    }
	}
	WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
	// {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
	wiz.setTitleFormat(new MessageFormat("{0}"));
	wiz.setTitle(wallet.getName() + "：" + nft.getSymbol() + "转账");
	if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
	    // do something
	    String recvAddress = (String)wiz.getProperty("recvAddress");
	    String value = (String)wiz.getProperty("value");
	    boolean isGasDefault = (boolean)wiz.getProperty("isGasDefault");
	    int gas = (int)wiz.getProperty("gas");
	    String pwd = (String)wiz.getProperty("password");

	    try {
		String hash = nft.safeTransferFrom(AESUtil.decrypt(wallet.getPrivateKeyAES(), pwd), wallet.getAddress(), recvAddress, new BigInteger(metaData.getId()));
//		JXHyperlink hashLink = parent.getHashLink();
//		hashLink.setText(hash);
//		// 气泡提示
//		try {
//		    JLabel lbl = new JLabel("最近一次交易哈希已更新，单击打开区块链浏览器查看交易状态");
//		    BalloonTip balloonTip = new BalloonTip(hashLink, 
//				    lbl,
//				    Utils.createBalloonTipStyle(),
//				    Utils.createBalloonTipPositioner(), 
//				    null);
//		    TimingUtils.showTimedBalloon(balloonTip, 3000);
//		} catch (Exception ex) {
//		    Exceptions.printStackTrace(ex);
//		}

		// 根据交易哈希查询转账结果
		System.out.println("根据交易哈希查询转账结果");
		final ProgressHandle ph = ProgressHandle.createHandle("正在查询交易状态，请稍候");
		SwingWorker<BlockChain.TransactionStatus, Void> worker = new SwingWorker<BlockChain.TransactionStatus, Void>() {
		    BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());

		    @Override
		    protected BlockChain.TransactionStatus doInBackground() throws Exception {
			ph.start();
			System.out.println("获得交易状态");
			return bc.getTransactionStatusByHash(hash);
		    }

		    @Override
		    protected void done() {
			ph.finish();
			System.out.println("通知用户");
			try {
			    BlockChain.TransactionStatus result = get();

			    if(result == BlockChain.TransactionStatus.SUCCESS) {
				// 交易成功，刷新余额及交易记录
//				refreshBtnActionPerformed(null);
			    } else if(result == BlockChain.TransactionStatus.FAILED) {
//				// 交易失败
//				NotificationDisplayer.getDefault().notify(
//					"交易失败",
//					ImageUtilities.loadImageIcon("resource/gourd32.png", false),
//					"点击此处打开区块链浏览器查询交易状态",
//					new CoinPanel.LinkAction(bc.getTransactionDetailUrl(hash))
//				);
			    } else {
//				// 交易为确认，提示用户手动查询交易结果
//				NotificationDisplayer.getDefault().notify(
//					"交易尚未确认",
//					ImageUtilities.loadImageIcon("resource/gourd32.png", false),
//					"点击此处打开区块链浏览器查询交易状态",
//					new CoinPanel.LinkAction(bc.getTransactionDetailUrl(hash))
//				);
			    }
			} catch (InterruptedException | ExecutionException ex) {
			    Exceptions.printStackTrace(ex);
			}
		    }
		};
		worker.execute();


	    } catch (Exception ex) {
		Exceptions.printStackTrace(ex);
	    }
	}
    }//GEN-LAST:event_transferBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTextArea descTextArea;
    private org.jdesktop.swingx.JXLabel idLbl;
    private org.jdesktop.swingx.JXImageView imageView;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXLabel nameLbl;
    private org.jdesktop.swingx.JXLabel platformLbl;
    private org.jdesktop.swingx.JXButton transferBtn;
    // End of variables declaration//GEN-END:variables
}
