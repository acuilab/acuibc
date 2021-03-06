package com.acuilab.bc.main.wallet.common;

import com.acuilab.bc.main.dao.WalletDAO;
import com.acuilab.bc.main.dapp.IDApp;
import com.acuilab.bc.main.ui.MyFindBar;
import com.acuilab.bc.main.wallet.Wallet;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.TableColumnExt;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author admin
 */
public class SelectWalletDialog extends javax.swing.JDialog {
    
    private final String blockChainSymbol;
    private WalletTableModel tableModel;
    
    private Wallet selected;

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form WalletSelectorDialog
     */
    public SelectWalletDialog(java.awt.Frame parent, String blockChainSymbol) {
	super(parent, true);
	initComponents();
	
	this.blockChainSymbol = blockChainSymbol;
	
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
	this.setTitle("钱包列表(" + blockChainSymbol + ")");
        findBar.setSearchable(table.getSearchable());
        
        tableModel = new WalletTableModel(table);
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
	
        // 双击
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clicked = e.getClickCount();
                if(clicked == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if(row>-1 && col>-1) {
			okButtonActionPerformed(null);
                    }
                }
            }
        });
        
        table.setColumnControlVisible(true);
        table.setColumnSelectionAllowed(false);		       // 禁止列选择
        table.setHorizontalScrollEnabled(true);                // 盘点列太多，在平板上显示不开，故该项默认启用
        table.getTableHeader().setReorderingAllowed(false);     // 表头不可拖动
	// Allow only single a selection
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
	// 禁止序号列排序
	TableSortController rowSorter = (TableSortController)table.getRowSorter();
	rowSorter.setSortable(0, false);
	TableColumnExt indexColumn = table.getColumnExt(WalletTableModel.INDEX_COLUMN);
	indexColumn.setMinWidth(56);
	indexColumn.setMaxWidth(56);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        indexColumn.setCellRenderer(render);
        indexColumn.setSortable(false);        
        
        ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
        ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
        ColorHighlighter indexHighlighter = new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(WalletTableModel.INDEX_COLUMN), table.getTableHeader().getBackground(), null);
        table.setHighlighters(evenHighlighter, oddHighlighter, indexHighlighter);
        
        
        try {
            tableModel.add(WalletDAO.getListByBlockChainSymbol(blockChainSymbol));
	    
	    // TODO: 自动选择上一次使用的钱包
	    
            table.setHorizontalScrollEnabled(true);
            table.packAll();
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
	return returnStatus;
    }
    
    public Wallet getSelectedWallet() {
	return selected;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        infoLbl = new org.jdesktop.swingx.JXLabel();
        findBar = new MyFindBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();

        setIconImage(ImageUtilities.loadImage("/resource/gourd32.png"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(SelectWalletDialog.class, "SelectWalletDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(SelectWalletDialog.class, "SelectWalletDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        infoLbl.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(infoLbl, org.openide.util.NbBundle.getMessage(SelectWalletDialog.class, "SelectWalletDialog.infoLbl.text")); // NOI18N

        javax.swing.GroupLayout findBarLayout = new javax.swing.GroupLayout(findBar);
        findBar.setLayout(findBarLayout);
        findBarLayout.setHorizontalGroup(
            findBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
        );
        findBarLayout.setVerticalGroup(
            findBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 43, Short.MAX_VALUE)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(findBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 273, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(findBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(infoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        
	int selectedRow = table.getSelectedRow();
	if(selectedRow == -1) {
	    infoLbl.setText("请选择钱包");
	    table.requestFocus();
	    return;
	}
	
	selected = tableModel.getWallet(table.convertRowIndexToModel(selectedRow));
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
    
    private void doClose(int retStatus) {
	returnStatus = retStatus;
	setVisible(false);
	dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private org.jdesktop.swingx.JXFindBar findBar;
    private org.jdesktop.swingx.JXLabel infoLbl;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private org.jdesktop.swingx.JXTable table;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
