package com.acuilab.bc.main.cfx.dapp.guguo;

import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.ui.MyFindBar;
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
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Sextet;
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
        dtd = "-//com.acuilab.bc.main.cfx.dapp.guguo//PeekUserData//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PeekUserDataTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.acuilab.bc.main.cfx.dapp.guguo.PeekUserDataTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PeekUserDataAction",
        preferredID = "PeekUserDataTopComponent"
)
@Messages({
    "CTL_PeekUserDataAction=PeekUserData",
    "CTL_PeekUserDataTopComponent=PeekUserData Window",
    "HINT_PeekUserDataTopComponent=This is a PeekUserData window"
})
public final class PeekUserDataTopComponent extends TopComponent {
    
    private UserDataTableModel tableModel;

    public PeekUserDataTopComponent() {
        initComponents();
        setName(Bundle.CTL_PeekUserDataTopComponent());
        setToolTipText(Bundle.HINT_PeekUserDataTopComponent());

        myInit();
    }
    
    private void myInit() {
        findBar.setSearchable(table.getSearchable());
        
        tableModel = new UserDataTableModel(table);
        table.setModel(tableModel);
        
        // 悬浮提示单元格的值@see http://skull.iteye.com/blog/850320
        table.addMouseMotionListener(new MouseAdapter() {
            
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if(row>-1 && col>-1) {
		    
                    Object value = table.getValueAt(row, col);

                    if(value != null && StringUtils.isNotBlank(value.toString())) {
                        table.setToolTipText(value.toString()); // 悬浮显示单元格内容
                    } else {
                        table.setToolTipText(null);         // 关闭提示
                    }
                }
            }
        });
        
        table.setColumnControlVisible(true);
        table.setColumnSelectionAllowed(false);		       // 禁止列选择
        table.setHorizontalScrollEnabled(true); 
        table.getTableHeader().setReorderingAllowed(false);     // 表头不可拖动
        
	// 禁止序号列排序
	TableSortController rowSorter = (TableSortController)table.getRowSorter();
	rowSorter.setSortable(0, false);
	TableColumnExt indexColumn = table.getColumnExt(AddressTableModel.INDEX_COLUMN);
	indexColumn.setMinWidth(56);
	indexColumn.setMaxWidth(56);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        indexColumn.setCellRenderer(render);
        indexColumn.setSortable(false);        
        
        ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
        ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
        ColorHighlighter indexHighlighter = new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(TransferRecordTableModel.INDEX_COLUMN), table.getTableHeader().getBackground(), null);
        table.setHighlighters(evenHighlighter, oddHighlighter, indexHighlighter);
        
        // 从链上获取数据
        peekUserData();
    }
    
    private void peekUserData() {
        refreshBtn.setEnabled(false);
        tableModel.clear();
        final ProgressHandle ph = ProgressHandle.createHandle("正在获取数据，请稍候");
        SwingWorker<Sextet<String[], BigInteger[], BigInteger[], BigInteger[], BigInteger[], BigInteger[]> , Void> worker = new SwingWorker<Sextet<String[], BigInteger[], BigInteger[], BigInteger[], BigInteger[], BigInteger[]> , Void>() {
            @Override
            protected Sextet<String[], BigInteger[], BigInteger[], BigInteger[], BigInteger[], BigInteger[]>  doInBackground() throws Exception {
                ph.start();

                // 获得用户数量
                IStakingXIANGContract contract = Lookup.getDefault().lookup(IStakingXIANGContract.class);
                BigInteger userCount = contract.getUserCount();
                
                // 窥视用户数据
                return contract.peekUserData(BigInteger.ZERO, userCount.subtract(BigInteger.ONE));
            }

            @Override
            protected void done() {
                try {
                    Sextet<String[], BigInteger[], BigInteger[], BigInteger[], BigInteger[], BigInteger[]> ret = get();
                    
                    String[] addresses = ret.getValue0();
                    List<UserData> list = Lists.newArrayListWithCapacity(addresses.length);
                    
                    for(int i=0; i<addresses.length; i++) {
                        ICoin yaoCoin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                        list.add(new UserData(addresses[i], 
                                yaoCoin.minUnit2MainUint(ret.getValue1()[i]).setScale(6, RoundingMode.DOWN).doubleValue(), 
                                yaoCoin.minUnit2MainUint(ret.getValue2()[i]).setScale(6, RoundingMode.DOWN).doubleValue(), 
                                yaoCoin.minUnit2MainUint(ret.getValue3()[i]).setScale(6, RoundingMode.DOWN).doubleValue(), 
                                yaoCoin.minUnit2MainUint(ret.getValue4()[i]).setScale(6, RoundingMode.DOWN).doubleValue(),  
                                yaoCoin.minUnit2MainUint(ret.getValue5()[i]).setScale(6, RoundingMode.DOWN).doubleValue()));
                    }
                    
                    tableModel.add(list);
                    table.setHorizontalScrollEnabled(true);
                    table.packAll();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    refreshBtn.setEnabled(true);
                    ph.finish();
                }
            }
        };
        worker.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findBar = new MyFindBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();
        refreshBtn = new org.jdesktop.swingx.JXButton();

        javax.swing.GroupLayout findBarLayout = new javax.swing.GroupLayout(findBar);
        findBar.setLayout(findBarLayout);
        findBarLayout.setHorizontalGroup(
            findBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        findBarLayout.setVerticalGroup(
            findBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        table.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table);

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn, org.openide.util.NbBundle.getMessage(PeekUserDataTopComponent.class, "PeekUserDataTopComponent.refreshBtn.text")); // NOI18N
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(findBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(refreshBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(findBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        peekUserData();
    }//GEN-LAST:event_refreshBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXFindBar findBar;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXButton refreshBtn;
    private org.jdesktop.swingx.JXTable table;
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
