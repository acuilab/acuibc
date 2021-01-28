package com.acuilab.bc.main.dapp;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author admin
 */
public class IconTableCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
	JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if(column == DAppTableModel.ICON_COLUMN){
	    label.setText(null);
	    DAppTableModel tableModel = (DAppTableModel)table.getModel();
	    IDApp dapp = tableModel.getDApp(table.convertRowIndexToModel(row));
            label.setIcon(dapp.getImageIcon());
	}
	
	return label;
    }
    
}
