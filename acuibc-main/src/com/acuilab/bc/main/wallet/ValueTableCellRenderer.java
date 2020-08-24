package com.acuilab.bc.main.wallet;

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
	if(column == TransferRecordTableModel.VALUE_COLUMN){
	    TransferRecordTableModel tableModel = (TransferRecordTableModel)table.getModel();
	    TransferRecord transferRecord = tableModel.getTransferRecord(table.convertRowIndexToModel(row));
            label.setText(transferRecord.getValue());
	}
	
	return label;
    }
    
}
