package com.acuilab.bc.main.ui;

import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.util.Utils;
import com.google.common.collect.Maps;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.autoupdate.InstallSupport;
import org.netbeans.api.autoupdate.OperationContainer;
import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.autoupdate.OperationSupport;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProviderFactory;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author admin
 */
public class CheckForUpdatesDialog extends javax.swing.JDialog {
    
    private static final Logger LOG = Logger.getLogger(CheckForUpdatesDialog.class.getName());
    private static final String UC_NAME = "com_acuilab_bc_main_update_center";
    
    private final Icon flagIcon = ImageUtilities.loadImageIcon("/resource/flag16.png", false);
    
    private final List<UpdateElement> install = new ArrayList<>();
    private final List<UpdateElement> update = new ArrayList<>();
    private final Map<String, String> installVersion = Maps.newHashMap();
    private boolean isRestartRequested = false;

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form CheckForUpdatesDialog
     */
    public CheckForUpdatesDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        
        // 启动后台线程查找新增或更新模块
        final ProgressHandle ph = ProgressHandle.createHandle("正在查找更新模块，请稍候");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ph.start();
                
                for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
                    try {
                        if (StringUtils.equals(UC_NAME, provider.getName())) {
                            provider.refresh(null, true);

                            for (UpdateUnit u : provider.getUpdateUnits()) {
                                if (!u.getAvailableUpdates().isEmpty()) {
                                    if (u.getInstalled() == null) {
                                        install.add(u.getAvailableUpdates().get(0));
                                    } else {
                                        installVersion.put(u.getAvailableUpdates().get(0).getCodeName(), u.getInstalled().getSpecificationVersion());
                                        update.add(u.getAvailableUpdates().get(0));
                                    }
                                }
                            }
                        }
                    } catch (IOException ex) {
                        LOG.severe(ex.getMessage());
                    }
                }
                
                return null;
            }

            @Override
            protected void done() {
                if(install.isEmpty() && update.isEmpty()) {
                    insertNotice("您的应用程序是最新的！");
                    insertText("\t没有可用的更新");
                    
                    okButton.setEnabled(false);
                    ph.finish();
                    return;
                }
                
                if(!install.isEmpty()) {
                    insertNotice("以下插件将被安装：");
                    
                    for(UpdateElement ue : install) {
//                        System.out.println("————————————————————install——————————————————————");
//                        System.out.println("author: " + ue.getAuthor());
//                        System.out.println("category: " + ue.getCategory());
//                        System.out.println("codeName: " + ue.getCodeName());
//                        System.out.println("date: " + ue.getDate());
//                        System.out.println("description: " + ue.getDescription());
//                        System.out.println("displayName: " + ue.getDisplayName());
//                        System.out.println("homepage: " + ue.getHomepage());
//                        System.out.println("licence" + ue.getLicence());
//                        System.out.println("licenseId: " + ue.getLicenseId());
//                        System.out.println("notification: " + ue.getNotification());
//                        System.out.println("source" + ue.getSource());
//                        System.out.println("sourceDescription: " + ue.getSourceDescription());
//                        System.out.println("specificationVersion: " + ue.getSpecificationVersion());
//                        System.out.println("downloadSize: " + ue.getDownloadSize());
//                        System.out.println("updateUnit: " + ue.getUpdateUnit());
//                        System.out.println("isEnabled: " + ue.isEnabled());
//                        System.out.println("sourceIcon: " + ue.getSourceIcon());
//                        System.out.println("toString: " + ue.toString());

                        insertText("\t" + ue.getDate() + " " + ue.getDisplayName() + " [" + ue.getSpecificationVersion() + "] " + Utils.getPrintSize(ue.getDownloadSize()));
                    }
                }

                
                if(!update.isEmpty()) {
                    insertNotice("以下插件将被更新：");
                    
                    for(UpdateElement ue : update) {
//                        System.out.println("———————————————————update———————————————————————");
//                        System.out.println("author: " + ue.getAuthor());
//                        System.out.println("category: " + ue.getCategory());
//                        System.out.println("codeName: " + ue.getCodeName());
//                        System.out.println("date: " + ue.getDate());
//                        System.out.println("description: " + ue.getDescription());
//                        System.out.println("displayName: " + ue.getDisplayName());
//                        System.out.println("homepage: " + ue.getHomepage());
//                        System.out.println("licence" + ue.getLicence());
//                        System.out.println("licenseId: " + ue.getLicenseId());
//                        System.out.println("notification: " + ue.getNotification());
//                        System.out.println("source" + ue.getSource());
//                        System.out.println("sourceDescription: " + ue.getSourceDescription());
//                        System.out.println("specificationVersion: " + ue.getSpecificationVersion());
//                        System.out.println("downloadSize: " + ue.getDownloadSize());
//                        System.out.println("updateUnit: " + ue.getUpdateUnit());
//                        System.out.println("isEnabled: " + ue.isEnabled());
//                        System.out.println("sourceIcon: " + ue.getSourceIcon());
//                        System.out.println("toString: " + ue.toString());

                        insertText("\t" + ue.getDate() + " " + ue.getDisplayName() + " [" + installVersion.get(ue.getCodeName()) + "->" + ue.getSpecificationVersion() + "] " + Utils.getPrintSize(ue.getDownloadSize()));
                    }
                }
                
                okButton.setEnabled(true);
                ph.finish();
            }
        };
        worker.execute();
        

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

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }
    
    // Searching New and Updated Modules
    public void searchNewAndUpdatedModules() {
        for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
            try {
                provider.refresh(null, true);
            } catch (IOException ex) {
                LOG.severe(ex.getMessage());
            }
            for (UpdateUnit u : provider.getUpdateUnits()) {
                if (!u.getAvailableUpdates().isEmpty()) {
                    if (u.getInstalled() == null) {
                        install.add(u.getAvailableUpdates().get(0));
                    } else {
                        update.add(u.getAvailableUpdates().get(0));
                    }
                }
            }
        }
    }

    // Searching Modules in a Special Update Center
    public void searchNewAndUpdateModulesInDedicatedUC() {
        for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
            try {
                if (StringUtils.equals(UC_NAME, provider.getName())) {
                    provider.refresh(null, true);

                    for (UpdateUnit u : provider.getUpdateUnits()) {
                        if (!u.getAvailableUpdates().isEmpty()) {
                            if (u.getInstalled() == null) {
                                install.add(u.getAvailableUpdates().get(0));
                            } else {
                                update.add(u.getAvailableUpdates().get(0));
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                LOG.severe(ex.getMessage());
            }
        }
    }
    
    public void installModules() {
        OperationContainer<InstallSupport> installContainer = addToContainer(OperationContainer.createForInstall(), install);
        installModules(installContainer);

        OperationContainer<InstallSupport> updateContainer = addToContainer(OperationContainer.createForUpdate(), update);
        installModules(updateContainer);
    }
    
    private OperationContainer<InstallSupport> addToContainer(OperationContainer<InstallSupport> container, List<UpdateElement> modules) {
        for (UpdateElement e : modules) {
            if (container.canBeAdded(e.getUpdateUnit(), e)) {
                OperationContainer.OperationInfo<InstallSupport> operationInfo = container.add(e);
                if (operationInfo != null) {
                    container.add(operationInfo.getRequiredElements());
                }
            }
        }

        return container;
    }
    
    private void installModules(OperationContainer<InstallSupport> container) {
        try {
            InstallSupport support = container.getSupport();
            if (support != null) {

                InstallSupport.Validator vali = support.doDownload(null, true, true);
                InstallSupport.Installer inst = support.doValidate(vali, null);
                OperationSupport.Restarter restarter = support.doInstall(inst, null);
                if (restarter != null) {
                    support.doRestartLater(restarter);
                    if (!isRestartRequested) {
                        NotificationDisplayer.getDefault().notify(
                                "该应用程序已更新",
                                ImageUtilities.loadImageIcon("resource/gourd16.png", false),
                                "点击此处重新启动",
                                new RestartAction(support, restarter)
                        );
                        isRestartRequested = true;
                    }
                }
            }
        } catch (OperationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private static final class RestartAction implements ActionListener {

        private final InstallSupport support;
        private final OperationSupport.Restarter restarter;

        public RestartAction(InstallSupport support, OperationSupport.Restarter restarter) {
            this.support = support;
            this.restarter = restarter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                support.doRestart(restarter, null);
            } catch (OperationException ex) {
                LOG.severe(ex.getMessage());
            }
        }

    }
    
    
    private void insertNotice(String notice) {
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
    
    private void insertText(String text) {
        try {
            Document doc = logPane.getDocument();
            logPane.setCaretPosition(doc.getLength());  // 移动光标到最后
            logPane.setParagraphAttributes(Constants.ALIGNMENT_LEFT_ATTRIBUTE_SET, false);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
            doc.insertString(doc.getLength(), text, Constants.TEXT_RESULT_ATTRIBUTE_SET);
            doc.insertString(doc.getLength(), "\n", null);   // 换行重启一段落
        } catch (BadLocationException ex) {
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

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        logPane = new javax.swing.JTextPane();

        setTitle(org.openide.util.NbBundle.getMessage(CheckForUpdatesDialog.class, "CheckForUpdatesDialog.title")); // NOI18N
        setIconImage(ImageUtilities.loadImage("/resource/gourd32.png"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(CheckForUpdatesDialog.class, "CheckForUpdatesDialog.okButton.text")); // NOI18N
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(CheckForUpdatesDialog.class, "CheckForUpdatesDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        logPane.setEditable(false);
        jScrollPane1.setViewportView(logPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 395, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
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
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane logPane;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
