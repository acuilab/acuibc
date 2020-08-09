package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.dao.WalletDAO;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.google.common.collect.Maps;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import org.jdesktop.swingx.JXTaskPane;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.acuilab.bc.main.wallet//WalletList//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "WalletListTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "com.acuilab.bc.main.wallet.WalletListTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_WalletListAction",
        preferredID = "WalletListTopComponent"
)
@Messages({
    "CTL_WalletListAction=我的钱包们",
    "CTL_WalletListTopComponent=我的钱包们",
    "HINT_WalletListTopComponent=我的钱包们"
})
public final class WalletListTopComponent extends TopComponent {
    
    private final Map<String, JXTaskPane> taskPaneMap = Maps.newHashMap();  // blockChainSysbol
    private final Map<String, WalletPanel> walletPanelMap = Maps.newHashMap();  // name, WalletPanel
    private final Map<String, Component> strutMap = Maps.newHashMap();          // name, strut

    public WalletListTopComponent() {
        initComponents();
        setName(Bundle.CTL_WalletListTopComponent());
        setToolTipText(Bundle.HINT_WalletListTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);

        myInit();
    }
    
    public void myInit() {
        // 清空
        jXTaskPaneContainer1.removeAll();
        
        // 加载钱包
        try {
            // 从数据库加载所有钱包
            List<Wallet> list = WalletDAO.getList();
            Map<String,List<Wallet>> walletGroupMap = list.stream().collect(Collectors.groupingBy(Wallet::getBlockChainSymbol));
            walletGroupMap.entrySet().forEach(entry -> {
                BlockChain bc = BlockChainManager.getDefault().getBlockChain(entry.getKey());
                JXTaskPane taskPane = new JXTaskPane(bc.getSymbol(), bc.getIcon(16));
                taskPane.setLayout(new BoxLayout(taskPane.getContentPane(), BoxLayout.Y_AXIS));
                
                List<Wallet> wallets = entry.getValue();
                
                for(int i=0; i<wallets.size(); i++) {
                    if(i > 0) {
                        Component strut = Box.createVerticalStrut(10);
                        taskPane.getContentPane().add(strut);
                        strutMap.put(wallets.get(i).getName(), strut);
                    }
                    WalletPanel walletPanel = new WalletPanel(wallets.get(i));
                    taskPane.add(walletPanel);
                    walletPanelMap.put(wallets.get(i).getName(), walletPanel);
                }
                
                jXTaskPaneContainer1.add(taskPane);
                taskPaneMap.put(bc.getSymbol(), taskPane);
            });

        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void addWallet(Wallet wallet) {
        JXTaskPane taskPane = taskPaneMap.get(wallet.getBlockChainSymbol());
        WalletPanel walletPanel = new WalletPanel(wallet);
        if(taskPane != null) {
            // 在该任务面板尾部插入钱包面板
            Component strut = Box.createVerticalStrut(10);
            taskPane.getContentPane().add(strut);
            strutMap.put(wallet.getName(), strut);
            taskPane.add(walletPanel);
        } else {
            // 新建任务面板并插入钱包面板
            BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());
            taskPane = new JXTaskPane(bc.getSymbol(), bc.getIcon(16));
            taskPane.setLayout(new BoxLayout(taskPane.getContentPane(), BoxLayout.Y_AXIS));
            taskPane.add(walletPanel);
                
            jXTaskPaneContainer1.add(taskPane);
            taskPaneMap.put(bc.getSymbol(), taskPane);
        }
        walletPanelMap.put(wallet.getName(), walletPanel);
    }
    
    public void deleteWallet(Wallet wallet) {
        
        JXTaskPane taskPane = taskPaneMap.get(wallet.getBlockChainSymbol());
        if(taskPane != null) {
            WalletPanel walletPanel = walletPanelMap.get(wallet.getName());
            taskPane.remove(walletPanel);
            // 移除相应的strut
            Component strut = strutMap.get(wallet.getName());
            if(strut != null) {
                taskPane.remove(strut);
            }
            walletPanelMap.remove(wallet.getName());
            strutMap.remove(wallet.getName());
            if(walletPanelMap.isEmpty()) {
                jXTaskPaneContainer1.remove(taskPane);
                taskPaneMap.remove(wallet.getBlockChainSymbol());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jXTaskPaneContainer1 = new org.jdesktop.swingx.JXTaskPaneContainer();

        org.jdesktop.swingx.VerticalLayout verticalLayout1 = new org.jdesktop.swingx.VerticalLayout();
        verticalLayout1.setGap(14);
        jXTaskPaneContainer1.setLayout(verticalLayout1);
        jScrollPane1.setViewportView(jXTaskPaneContainer1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTaskPaneContainer jXTaskPaneContainer1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        System.out.println("componentOpened");
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        System.out.println("componentClosed");
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
