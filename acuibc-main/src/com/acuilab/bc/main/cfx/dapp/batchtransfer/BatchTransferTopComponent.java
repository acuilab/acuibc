package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.TransferRecordTableModel;
import com.acuilab.bc.main.wallet.Wallet;
import com.acuilab.bc.main.wallet.common.SelectCoinDialog;
import com.acuilab.bc.main.wallet.common.SelectWalletDialog;
import java.awt.Color;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultCellEditor;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.acuilab.bc.main.cfx.dapp.batchtransfer//BatchTransfer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BatchTransferTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.acuilab.bc.main.cfx.dapp.batchtransfer.BatchTransferTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BatchTransferAction",
        preferredID = "BatchTransferTopComponent"
)
@Messages({
    "CTL_BatchTransferAction=批量转账",
    "CTL_BatchTransferTopComponent=批量转账",
    "HINT_BatchTransferTopComponent=批量转账"
})
public final class BatchTransferTopComponent extends TopComponent {
    private final BatchTransferTableModel tableModel;

    private Wallet wallet;
    private ICoin coin;
    
    public BatchTransferTopComponent() {
        initComponents();
        setName(Bundle.CTL_BatchTransferTopComponent());
        setToolTipText(Bundle.HINT_BatchTransferTopComponent());

        tableModel = new BatchTransferTableModel(table);
        tableModel.addTableModelListener((TableModelEvent e) -> {
            updateStatusBar();
        });
	
        // set the table model after setting the factory
        table.setModel(tableModel);
	
        table.setColumnControlVisible(true);
        table.setColumnSelectionAllowed(true);		       // 允许列选择
        table.getTableHeader().setReorderingAllowed(false);     // 表头不可拖动
	
        // 序号
        TableColumnExt indexColumn = table.getColumnExt(BatchTransferTableModel.INDEX_COLUMN);
        indexColumn.setMinWidth(40);
        indexColumn.setMaxWidth(40);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        // 不起作用，可能被下面的ColorHighlighter覆盖掉了, 使用下面的indexHighlighter实现同样的效果
//        render.setBackground(table.getTableHeader().getBackground()); 
        indexColumn.setCellRenderer(render);
        indexColumn.setSortable(false);
	
//        TableColumn addressColumn = table.getColumn(BatchTransferTableModel.ADDRESS_COLUMN);
//	addressColumn.setCellEditor(new AddressTableCellEditor());
	
        TableColumn valueColumn = table.getColumn(BatchTransferTableModel.VALUE_COLUMN);
        valueColumn.setCellRenderer(new com.acuilab.bc.main.cfx.dapp.batchtransfer.ValueTableCellRenderer());
	valueColumn.setCellEditor(new ValueTableCellEditor());

	ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
	ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
	ColorHighlighter indexHighlighter = new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(TransferRecordTableModel.INDEX_COLUMN), table.getTableHeader().getBackground(), null);
	table.setHighlighters(evenHighlighter, oddHighlighter, indexHighlighter);
	
	table.setHorizontalScrollEnabled(true);
	
//	// enter
//        InputMap enter = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);        
//        enter.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER ,0),"ENTER");       
//        table.getActionMap().put("ENTER", new AbstractAction() {
//	    @Override
//	    public void actionPerformed(ActionEvent e) {
//		System.out.println("ENTER...........................................");
//	    }
//	    
//	});
//	
//	table.addFocusListener(new FocusListener() {
//	    @Override
//	    public void focusGained(FocusEvent e) {
//	    }
//
//	    // 焦点在单元格时enter键水平移动
//	    @Override
//	    public void focusLost(FocusEvent e) {
//		// 在编辑单元格时，按enter键执行；如果不在被编辑单元格添键盘监听，
//		// 那么处于编辑状态时，必须按2次enter键才会水平移动;
//		// 因为第一次按enter时，焦点不在table中，不会触发enter事件
//		if(table.isEditing()) {
//		    table.getEditorComponent().addKeyListener(new KeyListener() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//			}
//
//			@Override
//			public void keyPressed(KeyEvent e) {
//			    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
//				int rowCount = table.getRowCount();
//				int colCount = table.getColumnCount();
//
//				if (rowCount <= 0) {
//				    return;
//				}
//				//注意取编辑时的row/col
//				int row = table.getEditingRow();
//				int col = table.getEditingColumn();
//
//				if (row < 0 || col < 0) {
//				    return;
//				}
//
//				if (col < colCount - 1) {
//				    col += 1;
//				} else {
//				    if (row < rowCount - 1) {
//					col = 0;
//					row += 1;
//				    } else {
//					row = col = 0;
//				    }
//				    // 注意这个换行时，有一个坑；在换行时会调用2次该方法，
//				    // 如果有对单元格的编辑后的校验，那就坑爹了，
//				    // 因此为了避免这个坑，可以在这里停止table的编辑
//				    // 如果没有编辑后事件校验，单纯的想用enter代替tab功能，那就不用stopEditing；
//				    if (table.isEditing()) {
//					table.getCellEditor().stopCellEditing();
//				    }
//
//				}
////				table.setRowSelectionInterval(row, row);		
////				table.setColumnSelectionInterval(col, col);
//				table.changeSelection(row, col, false, false);			
//			    }
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//			}
//		    });
//		}
//	    }
//	    
//	});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        walletFld = new org.jdesktop.swingx.JXTextField();
        selectWalletBtn = new org.jdesktop.swingx.JXButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        jXButton6 = new org.jdesktop.swingx.JXButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();
        tableRowsLbl = new org.jdesktop.swingx.JXLabel();
        addBtn = new org.jdesktop.swingx.JXButton();
        deleteBtn = new org.jdesktop.swingx.JXButton();
        clearBtn = new org.jdesktop.swingx.JXButton();
        jXButton2 = new org.jdesktop.swingx.JXButton();
        jXButton3 = new org.jdesktop.swingx.JXButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jXEditorPane1 = new org.jdesktop.swingx.JXEditorPane();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        jXLabel2 = new org.jdesktop.swingx.JXLabel();
        coinFld = new org.jdesktop.swingx.JXTextField();
        selectCoinBtn = new org.jdesktop.swingx.JXButton();

        walletFld.setEditable(false);
        walletFld.setText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.walletFld.text")); // NOI18N
        walletFld.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.walletFld.toolTipText")); // NOI18N
        walletFld.setPrompt(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.walletFld.prompt")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(selectWalletBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.selectWalletBtn.text")); // NOI18N
        selectWalletBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectWalletBtnActionPerformed(evt);
            }
        });

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane1.setDividerLocation(400);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jXPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXButton6, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXButton6.text")); // NOI18N

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

        tableRowsLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(tableRowsLbl, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.tableRowsLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.addBtn.text")); // NOI18N
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.deleteBtn.text")); // NOI18N
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(clearBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.clearBtn.text")); // NOI18N
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jXButton2, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXButton2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXButton3, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXButton3.text")); // NOI18N

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableRowsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableRowsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane1.setLeftComponent(jXPanel1);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jScrollPane3.border.title"))); // NOI18N

        jXEditorPane1.setEditable(false);
        jScrollPane3.setViewportView(jXEditorPane1);

        jSplitPane1.setRightComponent(jScrollPane3);

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel1, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel2, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXLabel2.text")); // NOI18N

        coinFld.setEditable(false);
        coinFld.setText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.coinFld.text")); // NOI18N
        coinFld.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.coinFld.toolTipText")); // NOI18N
        coinFld.setPrompt(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.coinFld.prompt")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(selectCoinBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.selectCoinBtn.text")); // NOI18N
        selectCoinBtn.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.selectCoinBtn.toolTipText")); // NOI18N
        selectCoinBtn.setEnabled(false);
        selectCoinBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCoinBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(coinFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(walletFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(selectWalletBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectCoinBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(walletFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectWalletBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coinFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectCoinBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectWalletBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectWalletBtnActionPerformed
        SelectWalletDialog dlg = new SelectWalletDialog(null, Constants.CFX_BLOCKCHAIN_SYMBAL);
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == SelectWalletDialog.RET_OK) {
	    wallet = dlg.getSelectedWallet();
	    walletFld.setText(wallet.getName() + "(地址：" + wallet.getAddress() + ")");
            selectCoinBtn.setEnabled(true);
	    
	    // 若coin存在，则刷新其余额
	    if(coin != null) {
		// 求余额
		coinFld.setPrompt(coin.getName() + "(正在请求余额，请稍候...)");
		SwingWorker<BigInteger, Void> worker = new SwingWorker<BigInteger, Void>() {
		    @Override
		    protected BigInteger doInBackground() throws Exception {
			return coin.balanceOf(wallet.getAddress());
		    }

		    @Override
		    protected void done() {
			try {
			    BigInteger balance = get();
			    coinFld.setText(coin.getName() + "(余额：" + coin.minUnit2MainUint(balance).setScale(coin.getMainUnitScale(), RoundingMode.FLOOR).toPlainString() + " " + coin.getMainUnit() + ")");
			} catch (InterruptedException | ExecutionException ex) {
			    Exceptions.printStackTrace(ex);
			}
		    }
		};
		worker.execute();
	    }
	}
    }//GEN-LAST:event_selectWalletBtnActionPerformed

    private void selectCoinBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCoinBtnActionPerformed
        // 选择代币前先选择钱包
        SelectCoinDialog dlg = new SelectCoinDialog(null, Constants.CFX_BLOCKCHAIN_SYMBAL, coin != null ? coin.getSymbol() : null);
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == SelectWalletDialog.RET_OK) {
	    coin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, dlg.getSelected());
            
            // 求余额
            coinFld.setPrompt(coin.getName() + "(正在请求余额，请稍候...)");
            SwingWorker<BigInteger, Void> worker = new SwingWorker<BigInteger, Void>() {
                @Override
                protected BigInteger doInBackground() throws Exception {
                    return coin.balanceOf(wallet.getAddress());
                }

                @Override
                protected void done() {
                    try {
                        BigInteger balance = get();
                        coinFld.setText(coin.getName() + "(余额：" + coin.minUnit2MainUint(balance).setScale(coin.getMainUnitScale(), RoundingMode.FLOOR).toPlainString() + " " + coin.getMainUnit() + ")");
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            worker.execute();
	}
    }//GEN-LAST:event_selectCoinBtnActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed

	BatchTransfer bt = new BatchTransfer();
	bt.setValue("0");
	tableModel.add(bt);
    }//GEN-LAST:event_addBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
	final int[] rows = table.getSelectedRows();
	for(int i=0; i<rows.length; i++) {
	    tableModel.removeRow(table.getSelectedRow());
	}

	table.repaint();
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        tableModel.clear();
	table.repaint();
    }//GEN-LAST:event_clearBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXButton addBtn;
    private org.jdesktop.swingx.JXButton clearBtn;
    private org.jdesktop.swingx.JXTextField coinFld;
    private org.jdesktop.swingx.JXButton deleteBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private org.jdesktop.swingx.JXButton jXButton2;
    private org.jdesktop.swingx.JXButton jXButton3;
    private org.jdesktop.swingx.JXButton jXButton6;
    private org.jdesktop.swingx.JXEditorPane jXEditorPane1;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXButton selectCoinBtn;
    private org.jdesktop.swingx.JXButton selectWalletBtn;
    private org.jdesktop.swingx.JXTable table;
    private org.jdesktop.swingx.JXLabel tableRowsLbl;
    private org.jdesktop.swingx.JXTextField walletFld;
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
    
    
    /**
     * Binding artefact method: crude hack to update the status bar on state
     * changes from controller.
     *
     * @param dummy
     */
    public void setStatusContent(Object dummy) {
        updateStatusBar();
    }

    private void updateStatusBar() {
	tableRowsLbl.setText("共" + tableModel.getRowCount() + "条");
    }
}
