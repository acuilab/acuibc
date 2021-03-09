package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.BlockChain;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author admin
 */
public class HashTableCellRenderer extends DefaultTableCellRenderer {
    private static final Icon okIcon = new javax.swing.ImageIcon(HashTableCellRenderer.class.getResource("/resource/ok16.png"));
    private static final Icon errorIcon = new javax.swing.ImageIcon(HashTableCellRenderer.class.getResource("/resource/error16.png"));
    private static final Icon unknownIcon = new javax.swing.ImageIcon(HashTableCellRenderer.class.getResource("/resource/unknown16.png"));

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
	JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if(column == BatchTransferTableModel.HASH_COLUMN){
	    label.setText((String)value);

            BatchTransferTableModel tableModel = (BatchTransferTableModel)table.getModel();
	    BatchTransfer bt = tableModel.getBatchTransfer(table.convertRowIndexToModel(row));
	    if(bt.getStatus() == BlockChain.TransactionStatus.SUCCESS) {
		label.setIcon(okIcon);
	    } else if(bt.getStatus() == BlockChain.TransactionStatus.FAILED) {
		label.setIcon(errorIcon);
	    } else if(bt.getStatus() == BlockChain.TransactionStatus.UNKNOWN) {
		label.setIcon(unknownIcon);
	    } else {
		label.setIcon(null);
	    }
	}
	
	return label;
    }
}