package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author admin
 */
public class ValueTableCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
	JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if(column == BatchTransferTableModel.VALUE_COLUMN){
	    BatchTransferTableModel tableModel = (BatchTransferTableModel)table.getModel();
	    BatchTransfer batchTransfer = tableModel.getBatchTransfer(table.convertRowIndexToModel(row));
	    String val = batchTransfer.getValue();
	    label.setText(val);
	}
	
	return label;
    }
    
}
