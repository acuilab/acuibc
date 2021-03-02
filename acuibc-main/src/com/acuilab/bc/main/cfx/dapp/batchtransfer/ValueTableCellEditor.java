package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author admin
 */
public class ValueTableCellEditor extends DefaultCellEditor {
    
    public ValueTableCellEditor(final JTextField textField) {
	super(textField);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	JTextField comp = (JTextField)editorComponent;
	
	// 在前端不以科学计数方法显示
	NumberFormat nf = NumberFormat.getInstance();
	// 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
	nf.setGroupingUsed(false);
	nf.setMaximumFractionDigits(18);    // 保留18位小数
	// 结果未做任何处理
	comp.setText(nf.format(value));
	
	return editorComponent;
    }
    
}
