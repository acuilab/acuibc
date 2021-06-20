package com.acuilab.bc.main.cfx.dapp.guguo;

import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.ui.MyFindBar;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.AddressTableModel;
import com.acuilab.bc.main.wallet.TransferRecordTableModel;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
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
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class PeekUserDataDialog extends javax.swing.JDialog {
    
    private UserDataTableModel tableModel;

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form PeekUserDataDialog
     */
    public PeekUserDataDialog() {
        super((java.awt.Frame)null, false);
        initComponents();
        
        myInit();

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
                    
                    tableModel.clear();
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
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();
        findBar = new MyFindBar();
        refreshBtn = new org.jdesktop.swingx.JXButton();

        setTitle(org.openide.util.NbBundle.getMessage(PeekUserDataDialog.class, "PeekUserDataDialog.title")); // NOI18N
        setIconImage(ImageUtilities.loadImage("/resource/gourd32.png"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(PeekUserDataDialog.class, "PeekUserDataDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(PeekUserDataDialog.class, "PeekUserDataDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout findBarLayout = new javax.swing.GroupLayout(findBar);
        findBar.setLayout(findBarLayout);
        findBarLayout.setHorizontalGroup(
            findBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );
        findBarLayout.setVerticalGroup(
            findBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn, org.openide.util.NbBundle.getMessage(PeekUserDataDialog.class, "PeekUserDataDialog.refreshBtn.text")); // NOI18N
        refreshBtn.setEnabled(false);
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(findBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 569, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(findBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
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

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        peekUserData();
    }//GEN-LAST:event_refreshBtnActionPerformed
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private org.jdesktop.swingx.JXFindBar findBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private org.jdesktop.swingx.JXButton refreshBtn;
    private org.jdesktop.swingx.JXTable table;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
