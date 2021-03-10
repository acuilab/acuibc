package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.ui.ConfirmDialog;
import com.acuilab.bc.main.ui.MessageDialog;
import com.acuilab.bc.main.ui.MyFindBar;
import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.util.DateUtil;
import com.acuilab.bc.main.util.Utils;
import com.acuilab.bc.main.wallet.TransferRecordTableModel;
import com.acuilab.bc.main.wallet.Wallet;
import com.acuilab.bc.main.wallet.common.PasswordVerifyDialog;
import com.acuilab.bc.main.wallet.common.SelectCoinDialog;
import com.acuilab.bc.main.wallet.common.SelectWalletDialog;
import com.csvreader.CsvReader;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.TimingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.javatuples.Pair;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.netbeans.api.progress.ProgressHandle;
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
    private final Icon chromeIcon = ImageUtilities.loadImageIcon("/resource/chrome16.png", false);
    private final BatchTransferTableModel tableModel;

    private Wallet wallet;
    private ICoin coin;
    
    private InnerSwingWorker<String, Pair<Integer, BatchTransfer>> innerWorker;	// 序号、BatchTransfer
    
    public BatchTransferTopComponent() {
        initComponents();
        setName(Bundle.CTL_BatchTransferTopComponent());
        setToolTipText(Bundle.HINT_BatchTransferTopComponent());
        
        // 初始化日志
        // 1 转账前将交易状态更新到最新
        // 2 开始按钮可重复执行，仅对交易状态为空或失败的记录进行转账
        // 3 导入格式为CSV(逗号分隔)(*.csv)，可通过excel另存为CSV(逗号分隔)(*.csv)
        insertNotice("1.当存在交易状态不确定的记录时不允许转账，请将交易状态更新到最新");
        insertNotice("2.开始按钮可重复执行，仅对交易状态为空或失败的记录进行转账");
        insertNotice("3.转账地址不可重复");
        insertNotice("4.转账进行时不要进行其他转账或调用合约的操作");
        insertNotice("5.导入格式为CSV(逗号分隔)(*.csv)，可通过excel另存为CSV(逗号分隔)(*.csv)");
        
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

                    // 工具栏提示
                    if (value != null && StringUtils.isNotBlank(value.toString())) {
                        table.setToolTipText(value.toString()); // 悬浮显示单元格内容
                    } else {
                        table.setToolTipText(null);         // 关闭提示
                    }
                }
            }
        });
        
        // 控制deleteBtn、clearBtn是否启用
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] selectedRows = table.getSelectedRows();
		deleteBtn.setEnabled(selectedRows.length > 0);
                if(selectedRows.length == 1) {
                    BatchTransfer bt = tableModel.getBatchTransfer(table.convertRowIndexToModel(selectedRows[0]));
		    if(StringUtils.isNotBlank(bt.getHash())) {
			scanBtn.setText("confluxscan: " + Utils.simplifyString(bt.getHash(), 6));
			scanBtn.setToolTipText("confluxscan: " + bt.getHash());
		    } else {
			scanBtn.setText("confluxscan");
			scanBtn.setToolTipText("confluxscan: 打开浏览器查看交易详情");
		    }
		    scanBtn.setEnabled(StringUtils.isNotBlank(bt.getHash()));
                    
                } else {
		    scanBtn.setText("confluxscan");
                    scanBtn.setToolTipText("confluxscan: 打开浏览器查看交易详情");
                    scanBtn.setEnabled(false);
                }
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
	
        TableColumn hashColumn = table.getColumn(BatchTransferTableModel.HASH_COLUMN);
        hashColumn.setCellRenderer(new com.acuilab.bc.main.cfx.dapp.batchtransfer.HashTableCellRenderer());
//        hashColumn.setCellEditor(new com.acuilab.bc.main.cfx.dapp.batchtransfer.HashTableCellEditor());

	ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
	ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
	ColorHighlighter indexHighlighter = new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(TransferRecordTableModel.INDEX_COLUMN), table.getTableHeader().getBackground(), null);
	ColorHighlighter addressInvalidHighlighter = new AddressInvalidColorHighlighter(tableModel, new HighlightPredicate.ColumnHighlightPredicate(BatchTransferTableModel.ADDRESS_COLUMN), Color.CYAN);
	ColorHighlighter valueInvalidHighlighter = new ValueInvalidColorHighlighter(tableModel, new HighlightPredicate.ColumnHighlightPredicate(BatchTransferTableModel.VALUE_COLUMN), Color.CYAN);
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
        scanBtn = new org.jdesktop.swingx.JXHyperlink();
        statusBtn = new org.jdesktop.swingx.JXButton();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        logPane = new javax.swing.JTextPane();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        jXLabel2 = new org.jdesktop.swingx.JXLabel();
        coinFld = new org.jdesktop.swingx.JXTextField();
        selectCoinBtn = new org.jdesktop.swingx.JXButton();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        gasSlider = new javax.swing.JSlider();
        gasSpinner = new javax.swing.JSpinner();
        slowLbl = new org.jdesktop.swingx.JXLabel();
        fastLbl = new org.jdesktop.swingx.JXLabel();
        gasLbl = new org.jdesktop.swingx.JXLabel();
        jXLabel3 = new org.jdesktop.swingx.JXLabel();
        refreshBalanceBtn = new org.jdesktop.swingx.JXButton();

        walletFld.setEditable(false);
        walletFld.setText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.walletFld.text")); // NOI18N
        walletFld.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.walletFld.toolTipText")); // NOI18N
        walletFld.setPrompt(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.walletFld.prompt")); // NOI18N
        walletFld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                walletFldMouseClicked(evt);
            }
        });

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
        importBtn.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.importBtn.toolTipText")); // NOI18N
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
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(startBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.startBtn.text")); // NOI18N
        startBtn.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.startBtn.toolTipText")); // NOI18N
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
            .addGap(0, 43, Short.MAX_VALUE)
        );

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(scanBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.scanBtn.text")); // NOI18N
        scanBtn.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.scanBtn.toolTipText")); // NOI18N
        scanBtn.setEnabled(false);
        scanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(statusBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.statusBtn.text")); // NOI18N
        statusBtn.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.statusBtn.toolTipText")); // NOI18N
        statusBtn.setEnabled(false);
        statusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusBtnActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

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
                .addComponent(scanBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
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
                    .addComponent(tableRowsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(scanBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(statusBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(importBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(stopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2)))
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
        coinFld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                coinFldMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(selectCoinBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.selectCoinBtn.text")); // NOI18N
        selectCoinBtn.setToolTipText(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.selectCoinBtn.toolTipText")); // NOI18N
        selectCoinBtn.setEnabled(false);
        selectCoinBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCoinBtnActionPerformed(evt);
            }
        });

        jXPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXPanel2.border.title"))); // NOI18N

        gasSlider.setPaintTicks(true);
        gasSlider.setSnapToTicks(true);
        gasSlider.setEnabled(false);
        gasSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                gasSliderStateChanged(evt);
            }
        });

        gasSpinner.setEnabled(false);
        gasSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                gasSpinnerStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(slowLbl, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.slowLbl.text")); // NOI18N

        fastLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(fastLbl, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.fastLbl.text")); // NOI18N

        gasLbl.setForeground(new java.awt.Color(0, 0, 255));
        gasLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(gasLbl, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.gasLbl.text")); // NOI18N
        gasLbl.setEnabled(false);

        javax.swing.GroupLayout jXPanel2Layout = new javax.swing.GroupLayout(jXPanel2);
        jXPanel2.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel2Layout.createSequentialGroup()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jXPanel2Layout.createSequentialGroup()
                        .addComponent(slowLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gasLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fastLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(gasSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gasSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(gasSpinner)
                    .addComponent(gasSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(slowLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(gasLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(fastLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel3, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.jXLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(refreshBalanceBtn, org.openide.util.NbBundle.getMessage(BatchTransferTopComponent.class, "BatchTransferTopComponent.refreshBalanceBtn.text")); // NOI18N
        refreshBalanceBtn.setEnabled(false);
        refreshBalanceBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBalanceBtnActionPerformed(evt);
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(walletFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(coinFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(refreshBalanceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(selectWalletBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(selectCoinBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jXPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                    .addComponent(selectCoinBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshBalanceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
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
            refreshBalanceBtn.setEnabled(true);
	    
	    // 若coin存在，则刷新其余额
            refreshBalance();
	}
    }//GEN-LAST:event_selectWalletBtnActionPerformed
    
    private void selectCoinBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCoinBtnActionPerformed
        // 选择代币前先选择钱包
        SelectCoinDialog dlg = new SelectCoinDialog(null, Constants.CFX_BLOCKCHAIN_SYMBAL, coin != null ? coin.getSymbol() : null);
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == SelectCoinDialog.RET_OK) {
	    coin = CoinManager.getDefault().getCoin(Constants.CFX_BLOCKCHAIN_SYMBAL, dlg.getSelected());
            
            // 矿工费初始化
            int gasMin = coin.gasMin();
            int gasMax = coin.gasMax();
            int defaultValue = coin.gasDefault();
            gasSlider.setMinimum(gasMin);
            gasSlider.setMaximum(gasMax);
            gasSlider.setValue(defaultValue);
            gasSpinner.setModel(new SpinnerNumberModel(defaultValue, gasMin, gasMax, 1));
            JSpinner.NumberEditor editor = new JSpinner.NumberEditor(gasSpinner, "#");
            final JFormattedTextField textField = editor.getTextField();
            final DefaultFormatterFactory factory = (DefaultFormatterFactory)textField.getFormatterFactory();
            final NumberFormatter formatter = (NumberFormatter)factory.getDefaultFormatter();
            formatter.setCommitsOnValidEdit(true);
            gasSpinner.setEditor(editor);
            gasLbl.setText(coin.gasDesc(coin.gasDefault()));

            gasSlider.setEnabled(true);
            gasSpinner.setEnabled(true);
            gasLbl.setEnabled(true);
            
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

    public void initWalletAndCoin(Wallet wallet, ICoin coin) {
        this.wallet = wallet;
        this.coin = coin;
        
        // wallet
        walletFld.setText(wallet.getName() + "(地址：" + wallet.getAddress() + ")");
        selectCoinBtn.setEnabled(true);
        refreshBalanceBtn.setEnabled(true);        
        
        // 矿工费初始化
        int gasMin = coin.gasMin();
        int gasMax = coin.gasMax();
        int defaultValue = coin.gasDefault();
        gasSlider.setMinimum(gasMin);
        gasSlider.setMaximum(gasMax);
        gasSlider.setValue(defaultValue);
        gasSpinner.setModel(new SpinnerNumberModel(defaultValue, gasMin, gasMax, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(gasSpinner, "#");
        final JFormattedTextField textField = editor.getTextField();
        final DefaultFormatterFactory factory = (DefaultFormatterFactory)textField.getFormatterFactory();
        final NumberFormatter formatter = (NumberFormatter)factory.getDefaultFormatter();
        formatter.setCommitsOnValidEdit(true);
        gasSpinner.setEditor(editor);
        gasLbl.setText(coin.gasDesc(coin.gasDefault()));

        gasSlider.setEnabled(true);
        gasSpinner.setEnabled(true);
        gasLbl.setEnabled(true);
        
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
            
    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        startBtn.setEnabled(false);
        
	// 选择钱包
	if(wallet == null) {
	    MessageDialog msg = new MessageDialog(null,"注意","请选择钱包");
	    msg.setVisible(true);
            startBtn.setEnabled(true);
	    return;
	}
	
	// 选择币种
	if(coin == null) {
	    MessageDialog msg = new MessageDialog(null,"注意","请选择币种");
	    msg.setVisible(true);
            startBtn.setEnabled(true);
	    return;
	}
        
        // 转账信息列表不能为空
        final List<BatchTransfer> list = tableModel.getBatchTransfers();
        if(list.isEmpty()) {
	    MessageDialog msg = new MessageDialog(null,"注意","转账信息列表不能为空");
	    msg.setVisible(true);
            startBtn.setEnabled(true);
	    return;
        }
        
	double total = 0d;
	for(BatchTransfer bt : list) {
	    
	    String address = bt.getAddress();
            
            // 已经转账成功的记录可以忽略了
            if(bt.getStatus() == BlockChain.TransactionStatus.SUCCESS) {
                continue;
            }
            
            // 是否有转账结果不确定的bt(提示用户刷新状态)
            if(bt.getStatus() == BlockChain.TransactionStatus.UNKNOWN) {
		MessageDialog msg = new MessageDialog(null,"注意","交易状态不确定，请更新交易状态：\"" + Utils.simplifyString(address, 18) + "\"");
		msg.setVisible(true);
                showRefreshTooltip("单击此按钮更新交易状态");
                startBtn.setEnabled(true);

                return;
            }
            
            // 检查是否有无效转账地址
            BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
            if(!bc.isValidAddress(address)) {
		MessageDialog msg = new MessageDialog(null,"注意","无效转账地址：\"" + Utils.simplifyString(address, 18) + "\"");
		msg.setVisible(true);
                startBtn.setEnabled(true);
		return;
	    }
            
	    // 检查是否有重复地址（重复地址会导致重复发送，同时表格更新会有问题）
	    Set<String> set = Sets.newHashSet();
	    if(!set.contains(bt.getAddress())) {
		set.add(bt.getAddress());
	    } else {
		MessageDialog msg = new MessageDialog(null,"注意","转账地址重复：\"" + Utils.simplifyString(address, 18) + "\"");
		msg.setVisible(true);
                startBtn.setEnabled(true);
		return;
	    }
            
	    // 检查数量是否无效
	    String value = bt.getValue();
//	    if(!NumberUtils.isParsable(value)) {
            if(NumberUtils.toDouble(value) <= 0d) {
		MessageDialog msg = new MessageDialog(null,"注意","无效数量：\"" + value + "\"");
		msg.setVisible(true);
                startBtn.setEnabled(true);
		return;
	    } else {
                // 状态为空或失败的才重新发送（上面已经对转账成功和不确定状态进行了过滤）
		total += NumberUtils.toDouble(value);
	    }
	}
        
        // 总数大于0
        if(total <= 0d) {
            MessageDialog msg = new MessageDialog(null,"注意",coin.getName() + "待转账总数无效(" + total + ")");
            msg.setVisible(true);
            startBtn.setEnabled(true);
            return;
        }
	
        // 外层SwingWorker用于验证矿工费及余额是否足够；内层SwingWorker用于执行实际的转账
	final Double totalWrapped = total;
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
	    @Override
	    protected String doInBackground() throws Exception {
                // 1 检查gas费是否足够(预留1个cfx用于gas费)
                // 2 检查余额是否足够
		ICoin baseCoin = CoinManager.getDefault().getBaseCoin(Constants.CFX_BLOCKCHAIN_SYMBAL);
		BigInteger balance = baseCoin.balanceOf(wallet.getAddress());
		if(balance.compareTo(baseCoin.mainUint2MinUint(1l)) < 0) {
		    // cfx数量必须大于1
		    return "预留矿工费：" + coin.getName() + "数量必须大于1";
		}

		// 代币数量是否足够
		if(coin.isBaseCoin()) {
		    if(balance.subtract(coin.mainUint2MinUint(1l)).compareTo(coin.mainUint2MinUint(totalWrapped)) < 0) {
			return coin.getName() + "扣除预留的矿工费后数量不足以完成全部转账";
		    }
		} else {
		    BigInteger coinBalance = coin.balanceOf(wallet.getAddress());
		    if(coinBalance.compareTo(coin.mainUint2MinUint(totalWrapped)) < 0) {
			return coin.getName() + "数量不足以完成全部转账";
		    }
		}
                
                return "";
	    }

	    @Override
	    protected void done() {
		try {
		    String ret = get();
		    
		    if(StringUtils.isNotBlank(ret)) {
			MessageDialog msg = new MessageDialog(null,"注意",ret);
			msg.setVisible(true);
                        startBtn.setEnabled(true);
		    } else {
			// 请求密码获得私钥
			PasswordVerifyDialog passwordVerifyDialog = new PasswordVerifyDialog(null, wallet);
			passwordVerifyDialog.setVisible(true);
			if(passwordVerifyDialog.getReturnStatus() == PasswordVerifyDialog.RET_OK) {
			    // 内层SwingWorker
                            insertLine();
			    final ProgressHandle ph = ProgressHandle.createHandle("正在转账，请稍候");
			    innerWorker = new InnerSwingWorker<String, Pair<Integer, BatchTransfer>>() {	// 序号、描述、哈希
				
				@Override
				protected String doInBackground() throws Exception {
				    ph.start(list.size());
                                    try {
					String[] tos = new String[list.size()];
					BigInteger[] values = new BigInteger[list.size()];
					for(int i=0; i<list.size(); i++) {
					    final BatchTransfer bt = list.get(i);
					    
					    // 理论上这里不会出现交易状态不确定的情况，跳过交易成功的bt
                                            // 交易状态为空或失败，加入转账列表
                                            if(bt.getStatus() == null || bt.getStatus() == BlockChain.TransactionStatus.FAILED) {
                                                tos[i] = bt.getAddress();
                                                values[i] = coin.mainUint2MinUint(new BigDecimal(bt.getValue()));
                                            }
					}
					String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), passwordVerifyDialog.getPassword());
					BigInteger gas = coin.gas2MinUnit(gasSlider.getValue());
					final Map<String, BatchTransfer> map = tableModel.generateMap();
					coin.batchTransfer(privateKey, tos, values, gas, new ICoin.BatchTransferCallback() {
					    @Override
					    public void transferFinished(String address, String hash, int index, int size) {
						// 从tableModel中找到batchTransfer对象，更新ui
						BatchTransfer bt = map.get(address);
						bt.setHash(hash);
                                                
						innerWorker.publish0(new Pair<>(index, bt));
					    }
					});
                                    } catch(Exception e) {
                                        // SwingWorker.doInBackground里面需要捕获异常才会在控制台输出堆栈信息
                                        e.printStackTrace();
                                        return e.getMessage();
                                    }

				    return "";
				}

				@Override
				protected void process(List<Pair<Integer, BatchTransfer>> chunks) {
				    for(Pair<Integer, BatchTransfer> pair : chunks) {
					// 刷新表格
					table.repaint();
					
                                        int index = pair.getValue0();
                                        BatchTransfer bt = pair.getValue1();
                                        insertResult(bt.getAddress(), bt.getHash(), index);
                                        
					ph.progress(index+1);
				    }
				}
				
				@Override
				protected void done() {
//				    // repaint talbe
//				    table.repaint();
//				    
				    // 更新超链scanBtn
				    if(table.getSelectedRow() != -1) {
					BatchTransfer bt = tableModel.getBatchTransfer(table.convertRowIndexToModel(table.getSelectedRow()));
					scanBtn.setText("confluxscan: " + Utils.simplifyString(bt.getHash(), 6));
					scanBtn.setToolTipText("confluxscan: " + bt.getHash());
                                        scanBtn.setEnabled(true);
				    } else {
					scanBtn.setText("confluxscan");
					scanBtn.setToolTipText("confluxscan: 打开浏览器查看交易详情");
                                        scanBtn.setEnabled(false);
				    }
				    
				    ph.finish();
                                    
                                    try {
                                        // 等待1秒钟更新交易状态
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                    }
                                    statusBtnActionPerformed(null);
				}
			    };
			    innerWorker.execute();
			} else {
                            startBtn.setEnabled(true);
                        }
		    }
		} catch (InterruptedException | ExecutionException ex) {
		    Exceptions.printStackTrace(ex);
		}
	    }
	};
	worker.execute();
    }//GEN-LAST:event_startBtnActionPerformed

    private void showRefreshTooltip(String msg) {
        // 气泡提示
        try {
            JLabel lbl = new JLabel(msg);
            BalloonTip balloonTip = new BalloonTip(statusBtn, 
                            lbl,
                            net.java.balloontip.examples.complete.Utils.createBalloonTipStyle(),
                            net.java.balloontip.examples.complete.Utils.createBalloonTipPositioner(), 
                            null);
            TimingUtils.showTimedBalloon(balloonTip, 2000);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        ConfirmDialog dlg = new ConfirmDialog(null, "删除记录确认", "是否删除记录？");
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == ConfirmDialog.RET_OK) {
            tableModel.clear();
            table.repaint();
        }
    }//GEN-LAST:event_clearBtnActionPerformed

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

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        BatchTransfer bt = new BatchTransfer();
        bt.setValue("0");
        tableModel.add(bt);
    }//GEN-LAST:event_addBtnActionPerformed

    private void importBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBtnActionPerformed
        // 打开文件
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);    // 可以同时新建多个
        chooser.setFileFilter(new FileNameExtensionFilter("CSV(逗号分隔)(*.csv)", "csv"));
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
                csvReader.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_importBtnActionPerformed

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed

	
	
	
	
	
	
	
    }//GEN-LAST:event_stopBtnActionPerformed

    private void gasSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_gasSliderStateChanged
        int value = ((JSlider) evt.getSource()).getValue();
        gasLbl.setText(coin.gasDesc(value));
        gasSpinner.setValue(value);
    }//GEN-LAST:event_gasSliderStateChanged

    private void gasSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_gasSpinnerStateChanged
        int value = (Integer)((JSpinner) evt.getSource()).getValue();
        gasLbl.setText(coin.gasDesc(value));
        gasSlider.setValue(value);
    }//GEN-LAST:event_gasSpinnerStateChanged

    private void scanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanBtnActionPerformed
        BatchTransfer bt = tableModel.getBatchTransfer(table.convertRowIndexToModel(table.getSelectedRow()));
        if(Desktop.isDesktopSupported()) {
            try {
                if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    // 打开默认浏览器
                    BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
                    Desktop.getDesktop().browse(URI.create(bc.getTransactionDetailUrl(bt.getHash())));
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_scanBtnActionPerformed

    private void statusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusBtnActionPerformed
	statusBtn.setEnabled(false);
        final List<BatchTransfer> list = tableModel.getBatchTransfers();
	final ProgressHandle ph = ProgressHandle.createHandle("正在刷新交易状态，请稍候");
	SwingWorker<Void, Pair<Integer, BatchTransfer>> worker = new SwingWorker<Void, Pair<Integer, BatchTransfer>>() {
	    @Override
	    protected Void doInBackground() throws Exception {
		ph.start(list.size());
		BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
		for(int i=0; i<list.size(); i++) {
		    final BatchTransfer bt = list.get(i);
		    String hash = bt.getHash();
		    
		    if(StringUtils.isNotBlank(hash) && (bt.getStatus() == null || bt.getStatus() == BlockChain.TransactionStatus.UNKNOWN)) {
			
			bt.setStatus(bc.getTransactionStatusByHash(hash));

			publish(new Pair<>(i+1, bt));
		    }
		}
		
		return null;
	    }

	    @Override
	    protected void process(List<Pair<Integer, BatchTransfer>> chunks) {
		for(Pair<Integer, BatchTransfer> pair : chunks) {
		    // 刷新表格
		    table.repaint();

		    ph.progress(pair.getValue0());
		}
	    }

	    @Override
	    protected void done() {
		// repaint talbe
		table.repaint();
                
		ph.finish();
                
                // 启用开始按钮
                if(!startBtn.isEnabled()) {
                    startBtn.setEnabled(true);
                }
                if(!statusBtn.isEnabled()) {
                    statusBtn.setEnabled(true);
                }
                
                // 气泡提示
                showRefreshTooltip("单击此按钮更新交易状态");
	    }
	    
	};
	worker.execute();
    }//GEN-LAST:event_statusBtnActionPerformed

    private void walletFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_walletFldMouseClicked
        selectWalletBtnActionPerformed(null);
    }//GEN-LAST:event_walletFldMouseClicked

    private void coinFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coinFldMouseClicked
        selectCoinBtnActionPerformed(null);
    }//GEN-LAST:event_coinFldMouseClicked

    private void refreshBalanceBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBalanceBtnActionPerformed
        refreshBalance();
    }//GEN-LAST:event_refreshBalanceBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXButton addBtn;
    private org.jdesktop.swingx.JXButton clearBtn;
    private org.jdesktop.swingx.JXTextField coinFld;
    private org.jdesktop.swingx.JXButton deleteBtn;
    private org.jdesktop.swingx.JXLabel fastLbl;
    private org.jdesktop.swingx.JXFindBar findBar;
    private org.jdesktop.swingx.JXLabel gasLbl;
    private javax.swing.JSlider gasSlider;
    private javax.swing.JSpinner gasSpinner;
    private org.jdesktop.swingx.JXButton importBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private javax.swing.JTextPane logPane;
    private org.jdesktop.swingx.JXButton refreshBalanceBtn;
    private org.jdesktop.swingx.JXHyperlink scanBtn;
    private org.jdesktop.swingx.JXButton selectCoinBtn;
    private org.jdesktop.swingx.JXButton selectWalletBtn;
    private org.jdesktop.swingx.JXLabel slowLbl;
    private org.jdesktop.swingx.JXButton startBtn;
    private org.jdesktop.swingx.JXButton statusBtn;
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
    
    private void refreshBalance() {
        // 若coin存在，则刷新其余额
        if(coin != null) {
            // 求余额
            refreshBalanceBtn.setEnabled(false);
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
                    } finally {
                        refreshBalanceBtn.setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }
    
    public void insertNotice(String notice) {
        try {
            Document doc = logPane.getDocument();
            logPane.setCaretPosition(doc.getLength());  // 移动光标到最后
            logPane.setParagraphAttributes(Constants.ALIGNMENT_LEFT_ATTRIBUTE_SET, false);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
            logPane.insertIcon(flagIcon);   // 插入通知标志
            doc.insertString(doc.getLength(), notice, Constants.TEXT_NOTICE_ATTRIBUTE_SET);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
        } catch (BadLocationException ex) {
        }
    }
    
    public void insertResult(String address, String hash, int index) {
        try {
            Document doc = logPane.getDocument();
            logPane.setCaretPosition(doc.getLength());  // 移动光标到最后
            logPane.setParagraphAttributes(Constants.ALIGNMENT_LEFT_ATTRIBUTE_SET, false);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
            doc.insertString(doc.getLength(), 
                    index + "\t" + DateUtil.commonDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss") + "\t" + address + "\t" + hash + "\t", 
                    Constants.TEXT_RESULT_ATTRIBUTE_SET);
            // 插入按钮
            JXHyperlink link = new JXHyperlink();
            link.setIcon(chromeIcon);
            link.setText("查看");
            link.setToolTipText("打开浏览器查看交易详情");
            link.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(Desktop.isDesktopSupported()) {
                        try {
                            if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                // 打开默认浏览器
                                BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
                                Desktop.getDesktop().browse(URI.create(bc.getTransactionDetailUrl(hash)));
                            }
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
            link.setAlignmentY(0.95f);
            logPane.insertComponent(link);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
        } catch (BadLocationException ex) {
        }
    }
    
    public void insertLine() {
        try {
            Document doc = logPane.getDocument();
            logPane.setCaretPosition(doc.getLength());  // 移动光标到最后
            logPane.setParagraphAttributes(Constants.ALIGNMENT_LEFT_ATTRIBUTE_SET, false);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
            doc.insertString(doc.getLength(), 
                    "—————————————————————————————————————————————————————————————", 
                    Constants.TEXT_LINE_ATTRIBUTE_SET);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
        } catch (BadLocationException ex) {
        }
    }
    
    abstract class InnerSwingWorker<T, V> extends SwingWorker<T, V> {
        // 将publish暴露给外部
	public final void publish0(V... chunks) {
	    this.publish(chunks);
	}
	
    }
}
