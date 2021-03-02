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
//	    String val = batchTransfer.getValue();
//	    // 在前端不以科学计数方法显示
//	    NumberFormat nf = NumberFormat.getInstance();
//	    // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
//	    nf.setGroupingUsed(false);
//	    // 结果未做任何处理
//	    System.out.println("batchTransfer.getValue()======" + val);
//	    label.setText(nf.format(NumberUtils.toDouble(val)));
	    
	    label.setText(batchTransfer.getValue());
	}
	
	return label;
    }
    
}
