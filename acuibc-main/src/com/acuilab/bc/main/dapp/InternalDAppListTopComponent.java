package com.acuilab.bc.main.dapp;

import com.acuilab.bc.main.manager.DAppManager;
import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.wallet.TransferRecordTableModel;
import com.acuilab.bc.main.wallet.Wallet;
import com.acuilab.bc.main.wallet.common.PasswordVerifyWizardPanel;
import com.acuilab.bc.main.wallet.common.SelectWalletWizardPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.commons.lang3.StringUtils;
import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
	dtd = "-//com.acuilab.bc.main.dapp//DAppList//EN",
	autostore = false
)
@TopComponent.Description(
	preferredID = "InternalDAppListTopComponent",
	//iconBase="SET/PATH/TO/ICON/HERE", 
	persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.acuilab.bc.main.dapp.InternalDAppListTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
	displayName = "#CTL_InternalDAppListAction",
	preferredID = "InternalDAppListTopComponent"
)
@Messages({
    "CTL_InternalDAppListAction=DApp列表",
    "CTL_InternalDAppListTopComponent=DApp列表",
    "HINT_InternalDAppListTopComponent=DApp列表"
})
public final class InternalDAppListTopComponent extends TopComponent {
    
    private final DAppTableModel tableModel;
    private final DAppFiltering filterController;

    public InternalDAppListTopComponent() {
	initComponents();
	setName(Bundle.CTL_InternalDAppListTopComponent());
	setToolTipText(Bundle.HINT_InternalDAppListTopComponent());
	putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.FALSE);
	putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.FALSE);
	putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.FALSE);
	putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.FALSE);
        
        tableModel = new DAppTableModel(table);
        tableModel.addTableModelListener((TableModelEvent e) -> {
            updateStatusBar();
        });

        // set the table model after setting the factory
        table.setModel(tableModel);

        // Filter control
        filterController = new DAppFiltering(table);
        // bind controller properties to input components
        BindingGroup filterGroup = new BindingGroup();

        // filterString
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterFld, BeanProperty.create("text"),
                filterController, BeanProperty.create("filterString")));

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
        
        // 双击
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clicked = e.getClickCount();
                if(clicked == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if(row>-1 && col>-1) {
                        // 两种情况需要处理，一种是需要账号密码的（指定钱包），一种是不需要的
                        try {
                            IDApp dapp = tableModel.getDApp(table.convertRowIndexToModel(row));
                            if(dapp.needWalletSpecified()) {
                                List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
                                panels.add(new SelectWalletWizardPanel(dapp.getBlockChainSymbol()));
                                panels.add(new PasswordVerifyWizardPanel());
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
                                WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
                                // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
                                wiz.setTitleFormat(new MessageFormat("{0}"));
                                wiz.setTitle("选择钱包");
                                if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                                    Wallet wallet = (Wallet)wiz.getProperty("wallet");
                                    String password = (String)wiz.getProperty("password");
                                    try {
                                        String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), password);

                                        table.setEnabled(false);
                                        final ProgressHandle ph = ProgressHandle.createHandle("正在启动" + dapp.getName() + "，请稍候");
                                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                                            @Override
                                            protected Void doInBackground() throws Exception {
                                                ph.start();
                                                dapp.launch(wallet.getAddress(), privateKey);
                                                return null;
                                            }

                                            @Override
                                            protected void done() {
                                                ph.finish();
                                                table.setEnabled(true);
                                            }
                                        };
                                        worker.execute();
                                    } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                                
//                                // 选择钱包，输入密码，获得私钥
//                                WalletSelectorDialog dlg = new WalletSelectorDialog(null);
//                                dlg.setVisible(true);
//                                if(dlg.getReturnStatus() == WalletSelectorDialog.RET_OK) {
//                                    Wallet wallet = dlg.getSelectedWallet();
//                                    String password = dlg.getPassword();
//                                    try {
//                                        String privateKey = AESUtil.decrypt(wallet.getPrivateKeyAES(), password);
//
//                                        table.setEnabled(false);
//                                        final ProgressHandle ph = ProgressHandle.createHandle("正在启动" + dapp.getName() + "，请稍候");
//                                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
//                                            @Override
//                                            protected Void doInBackground() throws Exception {
//                                                ph.start();
//                                                dapp.launch(wallet.getAddress(), privateKey);
//                                                return null;
//                                            }
//
//                                            @Override
//                                            protected void done() {
//                                                ph.finish();
//                                                table.setEnabled(true);
//                                            }
//                                        };
//                                        worker.execute();
//                                    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
//                                        Exceptions.printStackTrace(ex);
//                                    }
//                                }
                            } else {
                                dapp.launch(null, null);
                            }
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        });

        table.setColumnControlVisible(true);
        table.setColumnSelectionAllowed(false);		       // 允许列选择
        table.getTableHeader().setReorderingAllowed(false);     // 表头不可拖动
        
        // 序号
        TableColumnExt indexColumn = table.getColumnExt(TransferRecordTableModel.INDEX_COLUMN);
        indexColumn.setMinWidth(40);
        indexColumn.setMaxWidth(40);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        // 不起作用，可能被下面的ColorHighlighter覆盖掉了, 使用下面的indexHighlighter实现同样的效果
//        render.setBackground(table.getTableHeader().getBackground()); 
        indexColumn.setCellRenderer(render);
        indexColumn.setSortable(false);

        // 状态图标
        TableColumn statusColumn = table.getColumn(TransferRecordTableModel.STATUS_COLUMN);
        statusColumn.setMinWidth(24);
        statusColumn.setMaxWidth(24);
        statusColumn.setCellRenderer(new IconTableCellRenderer());

        ColorHighlighter evenHighlighter = new ColorHighlighter(HighlightPredicate.EVEN, Color.WHITE, null);
        ColorHighlighter oddHighlighter = new HighlighterFactory.UIColorHighlighter(HighlightPredicate.ODD);
        ColorHighlighter indexHighlighter = new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(TransferRecordTableModel.INDEX_COLUMN), table.getTableHeader().getBackground(), null);
        table.setHighlighters(evenHighlighter, oddHighlighter, indexHighlighter);

        // 排序(交易额列按数值排序) ———————— 导致filterController失效
//        TableRowSorter sorter = new TableRowSorter(tableModel);
//        sorter.setComparator(TransferRecordTableModel.VALUE_COLUMN, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return NumberUtils.createDouble(o1).compareTo(NumberUtils.createDouble(o2));
//            }
//        });
//        table.setRowSorter(sorter);
        table.setHorizontalScrollEnabled(true);
        
        // 加载dapp
        tableModel.add(DAppManager.getDefault().getDAppList());
        table.repaint();
        table.packAll();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();
        filterFld = new org.jdesktop.swingx.JXTextField();
        resetBtn = new org.jdesktop.swingx.JXButton();
        tableRowsLbl = new org.jdesktop.swingx.JXLabel();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

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

        filterFld.setText(org.openide.util.NbBundle.getMessage(InternalDAppListTopComponent.class, "InternalDAppListTopComponent.filterFld.text")); // NOI18N
        filterFld.setToolTipText(org.openide.util.NbBundle.getMessage(InternalDAppListTopComponent.class, "InternalDAppListTopComponent.filterFld.toolTipText")); // NOI18N
        filterFld.setPrompt(org.openide.util.NbBundle.getMessage(InternalDAppListTopComponent.class, "InternalDAppListTopComponent.filterFld.prompt")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(resetBtn, org.openide.util.NbBundle.getMessage(InternalDAppListTopComponent.class, "InternalDAppListTopComponent.resetBtn.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(tableRowsLbl, org.openide.util.NbBundle.getMessage(InternalDAppListTopComponent.class, "InternalDAppListTopComponent.tableRowsLbl.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filterFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableRowsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableRowsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTextField filterFld;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXButton resetBtn;
    private org.jdesktop.swingx.JXTable table;
    private org.jdesktop.swingx.JXLabel tableRowsLbl;
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
        if (filterController.isFiltering()) {
            tableRowsLbl.setText("共" + table.getRowCount() + "条");
        } else {
            tableRowsLbl.setText("共" + tableModel.getRowCount() + "条");
        }
    }
}
