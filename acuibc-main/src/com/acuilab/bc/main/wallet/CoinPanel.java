package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.wallet.wizard.PasswordInputWizardPanel;
import com.acuilab.bc.main.wallet.wizard.TransferConfirmWizardPanel;
import com.acuilab.bc.main.wallet.wizard.TransferInputWizardPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.javatuples.Pair;
import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.sort.TableSortController;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

/**
 *
 * @author admin
 */
public class CoinPanel extends JXPanel {
    
    private final Wallet wallet;
    private final Coin coin;
    private final TransferRecordTableModel tableModel; 
    private final TransferRecordFiltering filterController; 

    /**
     * Creates new form CoinPanel
     * @param wallet
     * @param coin
     * @param balance
     * @param transferRecords
     */
    public CoinPanel(Wallet wallet, Coin coin, BigInteger balance, List<TransferRecord> transferRecords) {
        initComponents();
        this.wallet = wallet;
        this.coin = coin;
        
        buttonGroup1.add(allRadio);
        buttonGroup1.add(recvRadio);
        buttonGroup1.add(sendRadio);
        
        tableModel = new TransferRecordTableModel(table, coin);
        tableModel.addTableModelListener((TableModelEvent e) -> {
            updateStatusBar();
        });
        
        // set the table model after setting the factory
        table.setModel(tableModel);
        
        // Filter control
        filterController = new TransferRecordFiltering(table);
        // bind controller properties to input components
        BindingGroup filterGroup = new BindingGroup();
        // 全部/收款/转账
        filterGroup.addBinding(Bindings.createAutoBinding(READ,  
                 allRadio, BeanProperty.create("selected"), 
                 filterController, BeanProperty.create("showAll")));
        filterGroup.addBinding(Bindings.createAutoBinding(READ,  
                 recvRadio, BeanProperty.create("selected"),  
                 filterController, BeanProperty.create("showRecv")));  
        filterGroup.addBinding(Bindings.createAutoBinding(READ,  
                 sendRadio, BeanProperty.create("selected"),  
                 filterController, BeanProperty.create("showSend")));  
        
        // filterString
        filterGroup.addBinding(Bindings.createAutoBinding(READ,  
                 filterFld, BeanProperty.create("text"), 
                 filterController, BeanProperty.create("filterString"))); 
        
        // 全部/收款/转账
        filterGroup.addBinding(Bindings.createAutoBinding(READ, 
                filterController, BeanProperty.create("showAll"), 
                this, BeanProperty.create("statusContent")));
        filterGroup.addBinding(Bindings.createAutoBinding(READ, 
                filterController, BeanProperty.create("showRecv"), 
                this, BeanProperty.create("statusContent")));
        filterGroup.addBinding(Bindings.createAutoBinding(READ, 
                filterController, BeanProperty.create("showSend"), 
                this, BeanProperty.create("statusContent")));
        // filterString
        filterGroup.addBinding(Bindings.createAutoBinding(READ, 
                filterController, BeanProperty.create("filterString"), 
                this, BeanProperty.create("statusContent")));
        filterGroup.bind();
        
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
        table.setColumnSelectionAllowed(true);		       // 禁止列选择
        table.getTableHeader().setReorderingAllowed(false);     // 表头不可拖动
//	table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 24));    // 设置表头字体
//	table.setFont(new Font("宋体", Font.PLAIN, 24));		    // 设置表内容字体
        table.setRowHeight(42);
	// 禁止序号列排序
	TableSortController rowSorter = (TableSortController)table.getRowSorter();
	rowSorter.setSortable(0, false);
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
	// 索引列
	TableColumn indexColumn = table.getColumn(TransferRecordTableModel.INDEX_COLUMN);
	indexColumn.setMinWidth(64);
	indexColumn.setMaxWidth(64);	
        
        // 状态图标
	TableColumn statusColumn = table.getColumn(TransferRecordTableModel.STATUS_COLUMN);
	statusColumn.setMinWidth(36);
	statusColumn.setMaxWidth(36);
	statusColumn.setCellRenderer(new StatusTableCellRenderer());
        
        // 交易时间
        TableColumn createdColumn = table.getColumn(TransferRecordTableModel.CREATED_COLUMN);
        createdColumn.setMinWidth(250);
        createdColumn.setMaxWidth(250);
        
        // 交易额列
        TableColumn valueColumn = table.getColumn(TransferRecordTableModel.VALUE_COLUMN);
        valueColumn.setMinWidth(160);
        valueColumn.setMaxWidth(160);
        
        ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
        ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
        table.setHighlighters(evenHighlighter, oddHighlighter);
        
        // 余额
        balanceFld.setText(coin.minUnit2MainUint(balance).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + coin.getMainUnit());

        // 交易记录
        tableModel.add(transferRecords);
        table.setHorizontalScrollEnabled(true);
        table.packAll();
    }
    
    /**
     * Binding artefact method: crude hack to update the status bar on state changes
     * from controller.
     * @param dummy
     */
    public void setStatusContent(Object dummy) {
        updateStatusBar();
    }
    
    private void updateStatusBar() {
        if(filterController.isFiltering()) {
            tableRowsLbl.setText("共" + table.getRowCount() + "条");
        } else {
            tableRowsLbl.setText("共" + tableModel.getRowCount() + "条");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        jXButton1 = new org.jdesktop.swingx.JXButton();
        balanceFld = new org.jdesktop.swingx.JXTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();
        refreshBtn = new org.jdesktop.swingx.JXButton();
        limitSpinner = new javax.swing.JSpinner();
        sendRadio = new javax.swing.JRadioButton();
        recvRadio = new javax.swing.JRadioButton();
        allRadio = new javax.swing.JRadioButton();
        filterFld = new org.jdesktop.swingx.JXTextField();
        tableRowsLbl = new org.jdesktop.swingx.JXLabel();
        jSeparator1 = new javax.swing.JSeparator();
        resetBtn = new org.jdesktop.swingx.JXButton();

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel1, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.jXLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXButton1, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.jXButton1.text")); // NOI18N
        jXButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXButton1ActionPerformed(evt);
            }
        });

        balanceFld.setEditable(false);
        balanceFld.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        balanceFld.setForeground(java.awt.Color.magenta);
        balanceFld.setText(org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.balanceFld.text")); // NOI18N

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

        org.openide.awt.Mnemonics.setLocalizedText(refreshBtn, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.refreshBtn.text")); // NOI18N
        refreshBtn.setToolTipText(org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.refreshBtn.toolTipText")); // NOI18N
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        limitSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 0, 1000, 10));

        org.openide.awt.Mnemonics.setLocalizedText(sendRadio, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.sendRadio.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(recvRadio, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.recvRadio.text")); // NOI18N

        allRadio.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(allRadio, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.allRadio.text")); // NOI18N

        filterFld.setText(org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.filterFld.text")); // NOI18N
        filterFld.setToolTipText(org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.filterFld.toolTipText")); // NOI18N
        filterFld.setPrompt(org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.filterFld.prompt")); // NOI18N

        tableRowsLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(tableRowsLbl, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.tableRowsLbl.text")); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(resetBtn, org.openide.util.NbBundle.getMessage(CoinPanel.class, "CoinPanel.resetBtn.text")); // NOI18N
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1558, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jXButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(balanceFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(limitSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(filterFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(allRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(recvRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableRowsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jXButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(balanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(limitSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sendRadio, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(recvRadio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(allRadio)
                        .addComponent(filterFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tableRowsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jXButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXButton1ActionPerformed
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new TransferInputWizardPanel(wallet, coin));
        panels.add(new PasswordInputWizardPanel(wallet));
        panels.add(new TransferConfirmWizardPanel(wallet, coin));
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
        wiz.setTitle("转账");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            // do something
            String recvAddress = (String)wiz.getProperty("recvAddress");
            String value = (String)wiz.getProperty("value");
            int gas = (int)wiz.getProperty("gas");
            String pwd = (String)wiz.getProperty("password");
            
            try {
                String hash = coin.transfer(AESUtil.decrypt(wallet.getPrivateKeyAES(), pwd), recvAddress, coin.mainUint2MinUint(NumberUtils.toDouble(value)), BigInteger.valueOf(gas));
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_jXButton1ActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        refreshBtn.setEnabled(false);
        final ProgressHandle ph = ProgressHandle.createHandle("正在请求余额及交易记录，请稍候");
        SwingWorker<Pair<BigInteger, List<TransferRecord>>, Void> worker = new SwingWorker<Pair<BigInteger, List<TransferRecord>>, Void>() {
            @Override
            protected Pair<BigInteger, List<TransferRecord>> doInBackground() throws Exception {
                ph.start();
                // 请求余额
                BigInteger balance = coin.balanceOf(wallet.getAddress());

                // 请求历史记录
                List<TransferRecord> transferRecords = coin.getTransferRecords(wallet, coin, wallet.getAddress(), (Integer)limitSpinner.getValue());
                
                return new Pair(balance, transferRecords);
            }

            @Override
            protected void done() {
                try {
                    Pair<BigInteger, List<TransferRecord>> pair = get();
                    
                    balanceFld.setText(coin.minUnit2MainUint(pair.getValue0()).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + coin.getMainUnit());
                    tableModel.clear();
                    tableModel.add(pair.getValue1());
                    table.setHorizontalScrollEnabled(true);
                    table.packAll();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                
                ph.finish();
                refreshBtn.setEnabled(true);
            }
        };
        worker.execute();
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        filterFld.setText("");
	filterFld.requestFocus();
    }//GEN-LAST:event_resetBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton allRadio;
    private org.jdesktop.swingx.JXTextField balanceFld;
    private javax.swing.ButtonGroup buttonGroup1;
    private org.jdesktop.swingx.JXTextField filterFld;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private org.jdesktop.swingx.JXButton jXButton1;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private javax.swing.JSpinner limitSpinner;
    private javax.swing.JRadioButton recvRadio;
    private org.jdesktop.swingx.JXButton refreshBtn;
    private org.jdesktop.swingx.JXButton resetBtn;
    private javax.swing.JRadioButton sendRadio;
    private org.jdesktop.swingx.JXTable table;
    private org.jdesktop.swingx.JXLabel tableRowsLbl;
    // End of variables declaration//GEN-END:variables
}
