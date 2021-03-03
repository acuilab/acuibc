package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.ui.ConfirmDialog;
import com.acuilab.bc.main.ui.MyFindBar;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.wallet.TransferRecordTableModel;
import com.acuilab.bc.main.wallet.Wallet;
import com.acuilab.bc.main.wallet.common.SelectCoinDialog;
import com.acuilab.bc.main.wallet.common.SelectWalletDialog;
import com.csvreader.CsvReader;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

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
    private static final Logger LOG = Logger.getLogger(BatchTransferTopComponent.class.getName());
    private final Icon flagIcon = ImageUtilities.loadImageIcon("/resource/flag16.png", false);
    private final BatchTransferTableModel tableModel;

    private Wallet wallet;
    private ICoin coin;
    
    public BatchTransferTopComponent() {
        initComponents();
        setName(Bundle.CTL_BatchTransferTopComponent());
        setToolTipText(Bundle.HINT_BatchTransferTopComponent());
        
        findBar.setSearchable(table.getSearchable());

        tableModel = new BatchTransferTableModel(table);
        tableModel.addTableModelListener((TableModelEvent e) -> {
            updateStatusBar();
        });
	
        // set the table model after setting the factory
        table.setModel(tableModel);
	
        table.setColumnControlVisible(true);
        table.setColumnSelectionAllowed(true);		       // 允许列选择
        table.getTableHeader().setReorderingAllowed(false);     // 表头不可拖动
        
        // 悬浮提示单元格的值@see http://skull.iteye.com/blog/850320
        table.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row > -1 && col > -1) {
                    Object value = table.getValueAt(row, col);

                    if (value != null && StringUtils.isNotBlank(value.toString())) {
                        table.setToolTipText(value.toString()); // 悬浮显示单元格内容
                    } else {
                        table.setToolTipText(null);         // 关闭提示
                    }
                }
            }
        });
        
        // TODO: 控制deleteBtn、clearBtn是否启用
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] selectedRows = table.getSelectedRows();
		deleteBtn.setEnabled(selectedRows.length > 0);
            }
        });
	
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
//        JTextField fld = new JXTextField();
//        fld.setHorizontalAlignment(JTextField.RIGHT);
//	addressColumn.setCellEditor(new DefaultCellEditor(fld));
	
        TableColumn valueColumn = table.getColumn(BatchTransferTableModel.VALUE_COLUMN);
        valueColumn.setCellRenderer(new com.acuilab.bc.main.cfx.dapp.batchtransfer.ValueTableCellRenderer());   // 数字按文本显示效果
        JTextField valueTextField = new JXTextField();
        valueTextField.setHorizontalAlignment(JTextField.RIGHT);    // 数字靠右对齐
        valueTextField.addFocusListener(new FocusListener( ) {
            @Override
            public void focusGained(FocusEvent e) {
                // 获得焦点后，光标移动到末尾
                Document doc = valueTextField.getDocument();
                valueTextField.setCaretPosition(doc.getLength());
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
            
        });
	valueColumn.setCellEditor(new ValueTableCellEditor(valueTextField));

	ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
	ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
	ColorHighlighter indexHighlighter = new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(TransferRecordTableModel.INDEX_COLUMN), table.getTableHeader().getBackground(), null);
	ColorHighlighter addressInvalidHighlighter = new AddressInvalidColorHighlighter(tableModel, new HighlightPredicate.ColumnHighlightPredicate(BatchTransferTableModel.ADDRESS_COLUMN), Color.RED);
	ColorHighlighter valueInvalidHighlighter = new ValueInvalidColorHighlighter(tableModel, new HighlightPredicate.ColumnHighlightPredicate(BatchTransferTableModel.VALUE_COLUMN), Color.RED);
	table.setHighlighters(evenHighlighter, oddHighlighter, indexHighlighter, addressInvalidHighlighter, valueInvalidHighlighter);
	
	table.setHorizontalScrollEnabled(true);
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
        importBtn = new org.jdesktop.swingx.JXButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();
        tableRowsLbl = new org.jdesktop.swingx.JXLabel();
        addBtn = new org.jdesktop.swingx.JXButton();
        deleteBtn = new org.jdesktop.swingx.JXButton();
        clearBtn = new org.jdesktop.swingx.JXButton();
        stopBtn = new org.jdesktop.swingx.JXButton();
        startBtn = new org.jdesktop.swingx.JXButton();
        findBar = new MyFindBar();
        jSeparator1 = new javax.swing.JSeparator();
        checkBtn = new org.jdesktop.swingx.JXButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        logPane = new javax.swing.JTextPane();
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

        org.openide.awt.Mnemonics.setLocalizedText(importBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.importBtn.text")); // NOI18N
        importBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importBtnActionPerformed(evt);
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

        tableRowsLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(tableRowsLbl, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.tableRowsLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.addBtn.text")); // NOI18N
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.deleteBtn.text")); // NOI18N
        deleteBtn.setEnabled(false);
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

        org.openide.awt.Mnemonics.setLocalizedText(stopBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.stopBtn.text")); // NOI18N
        stopBtn.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(startBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.startBtn.text")); // NOI18N
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBtnActionPerformed(evt);
            }
        });

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

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(checkBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.checkBtn.text")); // NOI18N
        checkBtn.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.checkBtn.toolTipText")); // NOI18N
        checkBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBtnActionPerformed(evt);
            }
        });

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
                .addComponent(importBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addComponent(findBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableRowsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(findBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tableRowsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane1.setLeftComponent(jXPanel1);

        logPane.setEditable(false);
        jScrollPane2.setViewportView(logPane);

        jSplitPane1.setRightComponent(jScrollPane2);

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
        ConfirmDialog dlg = new ConfirmDialog(null, "删除记录确认", "是否删除记录？");
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == ConfirmDialog.RET_OK) {
            final int[] rows = table.getSelectedRows();
            for(int i=0; i<rows.length; i++) {
                tableModel.removeRow(table.getSelectedRow());
            }

            table.repaint();
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        ConfirmDialog dlg = new ConfirmDialog(null, "删除记录确认", "是否删除记录？");
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == ConfirmDialog.RET_OK) {
            tableModel.clear();
            table.repaint();
        }
    }//GEN-LAST:event_clearBtnActionPerformed

    private void importBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBtnActionPerformed
        // 打开文件
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);    // 可以同时新建多个
        chooser.setFileFilter(new FileNameExtensionFilter("支持文件(*.csv)", "csv"));
        int returnVal = chooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();

            try {
                // 创建CSV读对象
                CsvReader csvReader = new CsvReader(file.getAbsolutePath(), ',', Charset.forName("UTF-8"));

//                // 读表头
//                csvReader.readHeaders();
                while (csvReader.readRecord()){
                    String address = csvReader.get(0);
                    BatchTransfer batchTransfer = new BatchTransfer();
                    batchTransfer.setAddress(address);
                    batchTransfer.setValue(csvReader.get(1));
                    batchTransfer.setRemark(csvReader.get(2));
                    tableModel.add(batchTransfer);
                }

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_importBtnActionPerformed

    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startBtnActionPerformed

    private void checkBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBtnActionPerformed
        // TODO: 检查转账地址及数量是否有效
        List<BatchTransfer> list = tableModel.getBatchTransfers();
        for(BatchTransfer bt : list) {
            // 1检查转账地址
            String address = bt.getAddress();
            BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
            if(bc.isValidAddress(address)) {
                // 转账地址无效，记入日志
                try {
                    Document doc = logPane.getDocument();
                    logPane.setCaretPosition(doc.getLength());  // 移动光标到最后
                    logPane.setParagraphAttributes(Constants.ALIGNMENT_CENTER_ATTRIBUTE_SET, false);    // 设置居中
                    doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
                    logPane.insertIcon(flagIcon);   // 插入通知标志
                    doc.insertString(doc.getLength(), "", Constants.TEXT_HEADER_ATTRIBUTE_SET);
                    doc.insertString(doc.getLength(), address + "地址无效\n", null);   // 换行重启一段落
                } catch (BadLocationException ex) {
                    LOG.log(Level.WARNING, "BadLocationException", ex);
                }
            }         
            
            // 2 检查数量
            String value = bt.getValue();
            
            
        }
    }//GEN-LAST:event_checkBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXButton addBtn;
    private org.jdesktop.swingx.JXButton checkBtn;
    private org.jdesktop.swingx.JXButton clearBtn;
    private org.jdesktop.swingx.JXTextField coinFld;
    private org.jdesktop.swingx.JXButton deleteBtn;
    private org.jdesktop.swingx.JXFindBar findBar;
    private org.jdesktop.swingx.JXButton importBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private javax.swing.JTextPane logPane;
    private org.jdesktop.swingx.JXButton selectCoinBtn;
    private org.jdesktop.swingx.JXButton selectWalletBtn;
    private org.jdesktop.swingx.JXButton startBtn;
    private org.jdesktop.swingx.JXButton stopBtn;
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
