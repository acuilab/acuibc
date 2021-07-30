package com.acuilab.bc.main.cfx.dapp.accountinfo;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.cfx.dapp.guguo.IStakingXIANGContract;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.AddressTableModel;
import com.acuilab.bc.main.wallet.TransferRecordTableModel;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.examples.complete.Utils;
import net.java.balloontip.utils.TimingUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.TableColumnExt;
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
        dtd = "-//com.acuilab.bc.main.cfx.dapp.accountinfo//AccountInfo//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "AccountInfoTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.acuilab.bc.main.cfx.dapp.accountinfo.AccountInfoTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AccountInfoAction",
        preferredID = "AccountInfoTopComponent"
)
@Messages({
    "CTL_AccountInfoAction=账户信息",
    "CTL_AccountInfoTopComponent=账户信息",
    "HINT_AccountInfoTopComponent=账户信息"
})
public final class AccountInfoTopComponent extends TopComponent {
    
    private CoinTableModel coinTableModel;
    
    public AccountInfoTopComponent() {
        initComponents();
        setName(Bundle.CTL_AccountInfoTopComponent());
        setToolTipText(Bundle.HINT_AccountInfoTopComponent());

        myInit();
    }
    
    private void myInit() {
        coinTableModel = new CoinTableModel(coinTable);
        coinTable.setModel(coinTableModel);
        
        // 悬浮提示单元格的值@see http://skull.iteye.com/blog/850320
        coinTable.addMouseMotionListener(new MouseAdapter() {
            
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = coinTable.rowAtPoint(e.getPoint());
                int col = coinTable.columnAtPoint(e.getPoint());
                if(row>-1 && col>-1) {
		    
                    Object value = coinTable.getValueAt(row, col);

                    if(value != null && StringUtils.isNotBlank(value.toString())) {
                        coinTable.setToolTipText(value.toString()); // 悬浮显示单元格内容
                    } else {
                        coinTable.setToolTipText(null);         // 关闭提示
                    }
                }
            }
        });
        
        coinTable.setColumnControlVisible(true);
        coinTable.setColumnSelectionAllowed(false);		       // 禁止列选择
        coinTable.setHorizontalScrollEnabled(true); 
        coinTable.getTableHeader().setReorderingAllowed(false);     // 表头不可拖动
        
	// 禁止序号列排序
	TableSortController rowSorter = (TableSortController)coinTable.getRowSorter();
	rowSorter.setSortable(0, false);
	TableColumnExt indexColumn = coinTable.getColumnExt(AddressTableModel.INDEX_COLUMN);
	indexColumn.setMinWidth(56);
	indexColumn.setMaxWidth(56);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        indexColumn.setCellRenderer(render);
        indexColumn.setSortable(false);        
        
        ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
        ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
        ColorHighlighter indexHighlighter = new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(TransferRecordTableModel.INDEX_COLUMN), coinTable.getTableHeader().getBackground(), null);
        coinTable.setHighlighters(evenHighlighter, oddHighlighter, indexHighlighter);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addressFld = new org.jdesktop.swingx.JXTextField();
        searchBtn = new org.jdesktop.swingx.JXButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        coinTable = new org.jdesktop.swingx.JXTable();

        addressFld.setText(org.openide.util.NbBundle.getMessage(AccountInfoTopComponent.class, "AccountInfoTopComponent.addressFld.text")); // NOI18N
        addressFld.setPrompt(org.openide.util.NbBundle.getMessage(AccountInfoTopComponent.class, "AccountInfoTopComponent.addressFld.prompt")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(searchBtn, org.openide.util.NbBundle.getMessage(AccountInfoTopComponent.class, "AccountInfoTopComponent.searchBtn.text")); // NOI18N
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        jScrollPane3.setBorder(null);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AccountInfoTopComponent.class, "AccountInfoTopComponent.jScrollPane1.border.title"))); // NOI18N

        coinTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(coinTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 416, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 108, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addressFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addressFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        
        String address = addressFld.getText();
        BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
        if(!bc.isValidAddress(address)) {
            // 提示选择钱包
            try {
                JLabel lbl = new JLabel("账户地址无效");
                BalloonTip balloonTip = new BalloonTip(addressFld, 
                                lbl,
                                Utils.createBalloonTipStyle(),
                                Utils.createBalloonTipPositioner(), 
                                null);
                TimingUtils.showTimedBalloon(balloonTip, 2000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            
            return;
        }

        // 获得所有代币并请求余额
        searchBtn.setEnabled(false);
        coinTableModel.clear();
        final ProgressHandle ph = ProgressHandle.createHandle("正在获取数据，请稍候");
        SwingWorker<List<Coin> , Void> worker = new SwingWorker<List<Coin>, Void>() {
            @Override
            protected List<Coin> doInBackground() throws Exception {
                List<ICoin> list = CoinManager.getDefault().getCoinList(Constants.CFX_BLOCKCHAIN_SYMBAL);
                ph.start();
                
                List<Coin> ret = Lists.newArrayList();
                for(ICoin coin : list) {
                    BigInteger value = coin.balanceOf(address);
                    if(Constants.CFX_YAO_SYMBOL.equalsIgnoreCase(coin.getSymbol())) {
                        IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                        value = value.add(xiangContract.pledgedAmount(address, BigInteger.ZERO));
                    } else if(Constants.CFX_YAO_CFX_PAIR_SYMBOL.equalsIgnoreCase(coin.getSymbol())) {
                        IStakingXIANGContract xiangContract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                        value = value.add(xiangContract.pledgedAmount(address, BigInteger.ONE));
                    }
                    
                    ret.add(new Coin(coin.getSymbol(), coin.minUnit2MainUint(value).setScale(4, RoundingMode.HALF_DOWN).doubleValue()));
                }
                
                return ret;
            }

            @Override
            protected void done() {
                try {
                    List<Coin> ret = get();
                    
                    coinTableModel.add(ret);
                    coinTable.setHorizontalScrollEnabled(true);
                    coinTable.packAll();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    searchBtn.setEnabled(true);
                    ph.finish();
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_searchBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTextField addressFld;
    private org.jdesktop.swingx.JXTable coinTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXButton searchBtn;
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
