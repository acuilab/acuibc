package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.nft.INFT;
import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.ui.WrapLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.SwingWorker;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.javatuples.Pair;
import org.jdesktop.swingx.JXPanel;
import org.netbeans.api.progress.ProgressHandle;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author admin
 */
public class NFTListPanel extends JXPanel {
    private final WalletTopComponent parent;
    private final Wallet wallet;
    private final INFT nft;
    private final int index;
    private boolean firstOpen;
    
    /**
     * Creates new form NFTPanel
     * @param parent
     * @param wallet
     * @param nft
     * @param index
     */
    public NFTListPanel(WalletTopComponent parent, Wallet wallet, INFT nft, int index) {
	initComponents();
        this.parent = parent;
	this.nftDisplayPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
	this.wallet = wallet;
	this.nft = nft;
	this.index = index;
        
        firstOpen = true;
        websiteLink.setText(nft.getWebsite());
        contractAddressFld.setText(nft.getContractAddress());
    }
    
    public WalletTopComponent getWalletTopComponent() {
	return parent;
    }

    public boolean isFirstOpen() {
        return firstOpen;
    }
    
    // 重新加载nft列表(后台线程执行)
    public void reload() {
	firstOpen=false;
	refreshBtn.setEnabled(false);
        nftDisplayPanel.removeAll(); 
        final ProgressHandle ph = ProgressHandle.createHandle("正在请求NFT列表，请稍候");
        SwingWorker<Integer, Pair<Integer, MetaData>> worker = new SwingWorker<Integer, Pair<Integer, MetaData>>() {
            @Override
            protected Integer doInBackground() throws Exception {
                BigInteger[] tockens = nft.tokensOf(wallet.getAddress());
                
                ph.start(tockens.length);
                for(int i=0; i<tockens.length; i++) {
                    MetaData metaData = nft.getMetaData(tockens[i]);
                    
                    if(metaData.getImage() == null && metaData.getImageUrl() != null) {
                        // 远程获取图像
                        X509TrustManager xtm = new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] x509Certificates = new X509Certificate[0];
                                return x509Certificates;
                            }
                        };

                        SSLContext sslContext = null;
                        try {
                            sslContext = SSLContext.getInstance("SSL");
                            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
                        } catch (NoSuchAlgorithmException | KeyManagementException e) {
                            e.printStackTrace();
                        }
                        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        };
                        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(20, TimeUnit.SECONDS)
                                .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                                .hostnameVerifier(DO_NOT_VERIFY)
                                .build();
                        
                        final okhttp3.Request request = new okhttp3.Request.Builder()
                                .url(metaData.getImageUrl())
                                .build();
                        final Call call = okHttpClient.newCall(request);
                        okhttp3.Response response = call.execute();             // java.net.SocketTimeoutException
                        ResponseBody body = response.body();
                        if(body != null) {
                            metaData.setImage(ImageIO.read(body.byteStream()));
                        }
                    }
                    
                    publish(Pair.with(i, metaData));
                }

                return tockens.length;
            }

            @Override
            protected void process(List<Pair<Integer, MetaData>> chunks) {
                for(Pair<Integer, MetaData> chunk : chunks) {
		    try {
			NFTListPanel.this.nftDisplayPanel.add(new SingleNFTPanel(NFTListPanel.this, wallet, nft, chunk.getValue0(), chunk.getValue1()));
		    } catch (Exception ex) {
		    }
                    ph.progress(chunk.getValue0()+1);
                }
            }

            @Override
            protected void done() {
		try {
		    // 重置tab页标题
		    Integer count = get();
		    parent.setNftPaneTitleAt(index, nft.getName() + "(" + count + ")");
		    ph.finish();
		    NFTListPanel.this.repaint();
		} catch (InterruptedException | ExecutionException ex) {
		    Exceptions.printStackTrace(ex);
		}
		refreshBtn.setEnabled(true);
            }
        };
        worker.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        refreshBtn = new org.jdesktop.swingx.JXButton();
        contractAddressLbl = new org.jdesktop.swingx.JXLabel();
        contractAddressFld = new org.jdesktop.swingx.JXTextField();
        websiteLbl = new org.jdesktop.swingx.JXLabel();
        websiteLink = new org.jdesktop.swingx.JXHyperlink();
        jScrollPane1 = new javax.swing.JScrollPane();
        nftDisplayPanel = new org.jdesktop.swingx.JXPanel();

        setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.FIT);
        setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.FIT);

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn, org.openide.util.NbBundle.getMessage(NFTListPanel.class, "NFTListPanel.refreshBtn.text")); // NOI18N
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(contractAddressLbl, org.openide.util.NbBundle.getMessage(NFTListPanel.class, "NFTListPanel.contractAddressLbl.text")); // NOI18N

        contractAddressFld.setEditable(false);
        contractAddressFld.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        contractAddressFld.setText(org.openide.util.NbBundle.getMessage(NFTListPanel.class, "NFTListPanel.contractAddressFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(websiteLbl, org.openide.util.NbBundle.getMessage(NFTListPanel.class, "NFTListPanel.websiteLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(websiteLink, org.openide.util.NbBundle.getMessage(NFTListPanel.class, "NFTListPanel.websiteLink.text")); // NOI18N
        websiteLink.setMinimumSize(new java.awt.Dimension(0, 0));
        websiteLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                websiteLinkActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        nftDisplayPanel.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        nftDisplayPanel.setScrollableWidthHint(org.jdesktop.swingx.ScrollableSizeHint.PREFERRED_STRETCH);
        jScrollPane1.setViewportView(nftDisplayPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(contractAddressLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contractAddressFld, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(websiteLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(websiteLink, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contractAddressLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contractAddressFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(websiteLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(websiteLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void refreshBtnActionPerformed() {
	refreshBtnActionPerformed(null);
    }    
    
    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        reload();
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void websiteLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_websiteLinkActionPerformed
                                                
        if(StringUtils.isBlank(websiteLink.getText())) {
            return;
        }
        
        if(Desktop.isDesktopSupported()) {
            try {
                if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    if(websiteLink.getText() != null) {
                        // 打开默认浏览器
                       Desktop.getDesktop().browse(URI.create(websiteLink.getText()));
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
       
        }
    }//GEN-LAST:event_websiteLinkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTextField contractAddressFld;
    private org.jdesktop.swingx.JXLabel contractAddressLbl;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXPanel nftDisplayPanel;
    private org.jdesktop.swingx.JXButton refreshBtn;
    private org.jdesktop.swingx.JXLabel websiteLbl;
    private org.jdesktop.swingx.JXHyperlink websiteLink;
    // End of variables declaration//GEN-END:variables
}
