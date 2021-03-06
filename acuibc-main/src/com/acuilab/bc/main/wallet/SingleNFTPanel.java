package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.nft.INFT;
import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.ui.MyJXImageView;
import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.wallet.wizard.NFTTransferConfirmWizardPanel;
import com.acuilab.bc.main.wallet.wizard.NFTTransferInputWizardPanel;
import com.acuilab.bc.main.wallet.wizard.PasswordInputWizardPanel;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
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
import org.apache.commons.lang3.StringUtils;
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
public class SingleNFTPanel extends JXPanel {
    private final NFTListPanel parent;
    private final Wallet wallet;
    private final INFT nft;
    private final MetaData metaData;

    /**
     * Creates new form NFTPanel
     * @param parent
     * @param wallet
     * @param nft
     * @param index
     * @param metaData
     */
    public SingleNFTPanel(NFTListPanel parent, Wallet wallet, INFT nft, int index, MetaData metaData) {
	initComponents();
        this.parent = parent;
	this.wallet = wallet;
	this.nft = nft;
	this.metaData = metaData;
        
        String id = metaData.getId();
        String number = metaData.getNumber();
        
        idLbl.setText("编号：" + number);
        idLbl.setToolTipText(number);
        
        nameLbl.setText("名称：" + metaData.getName());
        platformLbl.setText("平台：" + metaData.getPlatform());
        descLbl.setText("描述：" + metaData.getDesc());
	this.setToolTipText(metaData.getDesc());
        
        // 求余额
        SwingWorker<BigInteger, Void> worker = new SwingWorker<BigInteger, Void>() {
            @Override
            protected BigInteger doInBackground() throws Exception {
                return nft.balanceOf(wallet.getAddress(), new BigInteger(metaData.getId()));
            }

            @Override
            protected void done() {
                try {
                    BigInteger balance = get();
                    indexLbl.setText("#" + (index+1) + " 余额：" + balance.toString());
                    transferBtn.setEnabled(balance.compareTo(BigInteger.ZERO) > 0); // 数量大于0允许转账
                    if(!transferBtn.isEnabled()) {
                        transferBtn.setToolTipText("余额不足，无法转账");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        worker.execute();
        
	MyJXImageView myImageView = (MyJXImageView)imageView;
	myImageView.setToolTipText(metaData.getDesc());		// 这句必须调用，否则ImageToolTip弹不出来
	myImageView.setMetaData(metaData);
        if (metaData.getImage() != null) {
            try {
                myImageView.setImage(metaData.getImage());
                Image image = myImageView.getImage();
                double scaleX = 178.0d / image.getWidth(null);
                double scaleY = 178.0d / image.getHeight(null);
                myImageView.setScale(Math.max(scaleX, scaleY));
//                myImageView.setImageLocation(new Point2D.Double(image.getWidth(null) / 2, image.getWidth(null) / 2));
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else if (!StringUtils.isBlank(metaData.getImageUrl())) {
            try {
                URL url = new URL(metaData.getImageUrl());
                myImageView.setImage(url);
                Image image = myImageView.getImage();
                double scaleX = 178.0d / image.getWidth(null);
                double scaleY = 178.0d / image.getHeight(null);
                myImageView.setScale(Math.max(scaleX, scaleY));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
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
        transferBtn = new org.jdesktop.swingx.JXButton();
        imageView = new MyJXImageView();
        indexLbl = new org.jdesktop.swingx.JXLabel();
        descLbl = new org.jdesktop.swingx.JXLabel();

        setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleGradient"));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setMaximumSize(new java.awt.Dimension(180, 342));
        setMinimumSize(new java.awt.Dimension(180, 342));
        setPreferredSize(new java.awt.Dimension(180, 342));

        org.openide.awt.Mnemonics.setLocalizedText(nameLbl, org.openide.util.NbBundle.getMessage(SingleNFTPanel.class, "SingleNFTPanel.nameLbl.text")); // NOI18N
        nameLbl.setMaximumSize(new java.awt.Dimension(178, 18));
        nameLbl.setMinimumSize(new java.awt.Dimension(178, 18));
        nameLbl.setPreferredSize(new java.awt.Dimension(178, 18));

        org.openide.awt.Mnemonics.setLocalizedText(idLbl, org.openide.util.NbBundle.getMessage(SingleNFTPanel.class, "SingleNFTPanel.idLbl.text")); // NOI18N
        idLbl.setMaximumSize(new java.awt.Dimension(178, 18));
        idLbl.setMinimumSize(new java.awt.Dimension(178, 18));
        idLbl.setPreferredSize(new java.awt.Dimension(178, 18));

        org.openide.awt.Mnemonics.setLocalizedText(platformLbl, org.openide.util.NbBundle.getMessage(SingleNFTPanel.class, "SingleNFTPanel.platformLbl.text")); // NOI18N
        platformLbl.setMaximumSize(new java.awt.Dimension(178, 18));
        platformLbl.setMinimumSize(new java.awt.Dimension(178, 18));
        platformLbl.setPreferredSize(new java.awt.Dimension(178, 18));

        org.openide.awt.Mnemonics.setLocalizedText(transferBtn, org.openide.util.NbBundle.getMessage(SingleNFTPanel.class, "SingleNFTPanel.transferBtn.text")); // NOI18N
        transferBtn.setEnabled(false);
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

        indexLbl.setForeground(java.awt.Color.blue);
        indexLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(indexLbl, org.openide.util.NbBundle.getMessage(SingleNFTPanel.class, "SingleNFTPanel.indexLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(descLbl, org.openide.util.NbBundle.getMessage(SingleNFTPanel.class, "SingleNFTPanel.descLbl.text")); // NOI18N
        descLbl.setMaximumSize(new java.awt.Dimension(178, 18));
        descLbl.setMinimumSize(new java.awt.Dimension(178, 18));
        descLbl.setPreferredSize(new java.awt.Dimension(178, 18));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(platformLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(indexLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transferBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(imageView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(descLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nameLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(idLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(descLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transferBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(indexLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void transferBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transferBtnActionPerformed
	List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
	panels.add(new NFTTransferInputWizardPanel(wallet, nft, metaData));
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
	    int value = (int)wiz.getProperty("value");
	    int gas = (int)wiz.getProperty("gas");
	    String pwd = (String)wiz.getProperty("password");

	    try {
		String hash = nft.safeTransferFrom(AESUtil.decrypt(wallet.getPrivateKeyAES(), pwd), wallet.getAddress(), recvAddress, new BigInteger(metaData.getId()), BigInteger.valueOf(value), BigInteger.valueOf(gas));
		JXHyperlink hashLink = parent.getWalletTopComponent().getHashLink();
		hashLink.setText(hash);
                hashLink.setToolTipText(hash);
		// 气泡提示
		try {
		    JLabel lbl = new JLabel("最近一次交易哈希已更新，单击打开区块链浏览器查看交易状态");
		    BalloonTip balloonTip = new BalloonTip(hashLink, 
				    lbl,
				    Utils.createBalloonTipStyle(),
				    Utils.createBalloonTipPositioner(), 
				    null);
		    TimingUtils.showTimedBalloon(balloonTip, 3000);
		} catch (Exception ex) {
		    Exceptions.printStackTrace(ex);
		}

		// 根据交易哈希查询转账结果
		final ProgressHandle ph = ProgressHandle.createHandle("正在查询交易状态，请稍候");
		SwingWorker<BlockChain.TransactionStatus, Void> worker = new SwingWorker<BlockChain.TransactionStatus, Void>() {
		    BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());

		    @Override
		    protected BlockChain.TransactionStatus doInBackground() throws Exception {
			ph.start();
			// 获得交易状态（最多请求8次）
			Thread.sleep(3000l);

			int count = 8;
			BlockChain.TransactionStatus status = BlockChain.TransactionStatus.UNKNOWN;
			while(status == BlockChain.TransactionStatus.UNKNOWN && count > 0) {
			    status = bc.getTransactionStatusByHash(hash);

			    // 这里不直接跳出，而是等待延时结束，给区块链留一点时间更新状态
//			    if(status != BlockChain.TransactionStatus.UNKNOWN) {
//				break;
//			    }

			    count--;
			    // 延时2秒
			    Thread.sleep(2000l);
			}
			return status;
		    }

		    @Override
		    protected void done() {
			ph.finish();
			// 通知用户
			try {
			    BlockChain.TransactionStatus result = get();

			    if(result == BlockChain.TransactionStatus.SUCCESS) {
				// 交易成功，刷新余额及交易记录
				parent.refreshBtnActionPerformed();
			    } else if(result == BlockChain.TransactionStatus.FAILED) {
				// 交易失败
				NotificationDisplayer.getDefault().notify(
					"交易失败",
					ImageUtilities.loadImageIcon("resource/gourd16.png", false),
					"点击此处打开区块链浏览器查询交易状态",
					new LinkAction(bc.getTransactionDetailUrl(hash))
				);
			    } else {
				// 交易为确认，提示用户手动查询交易结果
				NotificationDisplayer.getDefault().notify(
					"交易尚未确认",
					ImageUtilities.loadImageIcon("resource/gourd16.png", false),
					"点击此处打开区块链浏览器查询交易状态",
					new LinkAction(bc.getTransactionDetailUrl(hash))
				);
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

    private static final class LinkAction implements ActionListener {
        
        private final String transactionDetailUrl;

        public LinkAction(String transactionDetailUrl) {
            this.transactionDetailUrl = transactionDetailUrl;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(Desktop.isDesktopSupported()) {
                try {
                    if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        // 打开默认浏览器
                        Desktop.getDesktop().browse(URI.create(transactionDetailUrl));
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXLabel descLbl;
    private org.jdesktop.swingx.JXLabel idLbl;
    private org.jdesktop.swingx.JXImageView imageView;
    private org.jdesktop.swingx.JXLabel indexLbl;
    private org.jdesktop.swingx.JXLabel nameLbl;
    private org.jdesktop.swingx.JXLabel platformLbl;
    private org.jdesktop.swingx.JXButton transferBtn;
    // End of variables declaration//GEN-END:variables
}
