package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.dao.AddressBookDAO;
import com.acuilab.bc.main.manager.BlockChainManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.examples.complete.Utils;
import net.java.balloontip.utils.TimingUtils;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author admin
 */
public class AddressEditDialog extends javax.swing.JDialog {
    
    private Address address;

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    
    public AddressEditDialog(java.awt.Frame parent) {
        this(parent, null);
    }

    /**
     * Creates new form AddressEditDialog
     * @param parent
     * @param address
     */
    public AddressEditDialog(java.awt.Frame parent, Address address) {
        super(parent, true);
        initComponents();
        
        this.address = address;
        
        myInit(address);

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
    
    private void myInit(Address address) {
        this.setTitle(address == null ? "新建地址" : "编辑地址");
        
        String[] symbols = BlockChainManager.getDefault().getBlockChainSymbols();
        for(String symbol : symbols) {
            typeComboBox.addItem(symbol);
        }
        
        if(address != null) {
            addressFld.setText(StringUtils.trimToEmpty(address.getAddress()));
            remarkFld.setText(StringUtils.trimToEmpty(address.getRemark()));
        }
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }
    
    public Address getAddress() {
        return address;
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
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        jXLabel2 = new org.jdesktop.swingx.JXLabel();
        addressFld = new org.jdesktop.swingx.JXTextField();
        jXLabel3 = new org.jdesktop.swingx.JXLabel();
        remarkFld = new org.jdesktop.swingx.JXTextField();

        setIconImage(ImageUtilities.loadImage("/resource/gourd32.png"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(AddressEditDialog.class, "AddressEditDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(AddressEditDialog.class, "AddressEditDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel1, org.openide.util.NbBundle.getMessage(AddressEditDialog.class, "AddressEditDialog.jXLabel1.text")); // NOI18N

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel2, org.openide.util.NbBundle.getMessage(AddressEditDialog.class, "AddressEditDialog.jXLabel2.text")); // NOI18N

        addressFld.setText(org.openide.util.NbBundle.getMessage(AddressEditDialog.class, "AddressEditDialog.addressFld.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jXLabel3, org.openide.util.NbBundle.getMessage(AddressEditDialog.class, "AddressEditDialog.jXLabel3.text")); // NOI18N

        remarkFld.setText(org.openide.util.NbBundle.getMessage(AddressEditDialog.class, "AddressEditDialog.remarkFld.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(460, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addGap(19, 19, 19))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(remarkFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addressFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remarkFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        
        String addr = addressFld.getText();
        // 地址不能为空
        if(StringUtils.isBlank(addr)) {
            // 气泡提示
	    try {
                JLabel lbl = new JLabel("地址不能为空");
		BalloonTip balloonTip = new BalloonTip(addressFld, 
				lbl,
				Utils.createBalloonTipStyle(),
				Utils.createBalloonTipPositioner(), 
				null);
		TimingUtils.showTimedBalloon(balloonTip, 2000);
	    } catch (Exception ex) {
		Exceptions.printStackTrace(ex);
	    }
            
            addressFld.requestFocus();
            return;
        }
        
        // 地址要合法
        String symbol = (String)typeComboBox.getSelectedItem();
        BlockChain bc = BlockChainManager.getDefault().getBlockChain(symbol);
        if(!bc.isValidAddress(addr)) {
            // 气泡提示
	    try {
                JLabel lbl = new JLabel("地址不合法");
		BalloonTip balloonTip = new BalloonTip(addressFld, 
				lbl,
				Utils.createBalloonTipStyle(),
				Utils.createBalloonTipPositioner(), 
				null);
		TimingUtils.showTimedBalloon(balloonTip, 2000);
	    } catch (Exception ex) {
		Exceptions.printStackTrace(ex);
	    }
            
            addressFld.requestFocus();
            return;
        }
        
        if(address == null) {
            // 新建地址，检查地址是否已存在
            try {
                boolean exist = AddressBookDAO.existByAddress(addr);
                if(exist) {
                    // 气泡提示
                    try {
                        JLabel lbl = new JLabel("地址已存在");
                        BalloonTip balloonTip = new BalloonTip(addressFld, 
                                        lbl,
                                        Utils.createBalloonTipStyle(),
                                        Utils.createBalloonTipPositioner(), 
                                        null);
                        TimingUtils.showTimedBalloon(balloonTip, 2000);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    addressFld.requestFocus();
                    return;
                }
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        // 
        if(address == null) {
            address = new Address();
            address.setId(UUID.randomUUID().toString());
            address.setCreated(new Date());
        }
        address.setBlockChainSymbol(symbol);
        address.setAddress(addr);
        address.setRemark(StringUtils.trimToEmpty(remarkFld.getText()));
        
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
    private org.jdesktop.swingx.JXTextField addressFld;
    private javax.swing.JButton cancelButton;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private javax.swing.JButton okButton;
    private org.jdesktop.swingx.JXTextField remarkFld;
    private javax.swing.JComboBox<String> typeComboBox;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
