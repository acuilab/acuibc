package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author acuilab
 */
public class BatchTransferTableModel extends AbstractTableModel {
    
    private final JXTable table;
    
    public static final int INDEX_COLUMN = 0;
    public static final int ADDRESS_COLUMN = 1;
    public static final int VALUE_COLUMN = 2;
    public static final int REMARK_COLUMN = 3;
    public static final int HASH_COLUMN = 4;
    public static final int COLUMN_COUNT = 5;
    
    private final List<BatchTransfer> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "地址",
        "数量",
        "备注",
        "交易哈希"
    };
    
    public BatchTransferTableModel(JXTable table) {
        this.table = table;
    }

    public void add(List<BatchTransfer> newList) {
        
        int first = list.size();
        int last = first + newList.size() - 1;
        if(last > -1) {
            list.addAll(newList);
            fireTableRowsInserted(first, last);
        }
    }

    public void add(BatchTransfer obj) {
        int index = list.size();
        list.add(obj);
        fireTableRowsInserted(index, index);
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNIDS[column];
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case VALUE_COLUMN:
                return Double.class;
        }
        return getValueAt(0, column).getClass();
    }

    @Override
    public Object getValueAt(int row, int column) {
        // PENDING JW: solve in getColumnClass instead of hacking here
        if (row >= getRowCount()) {
            return new Object();
        }
        
        BatchTransfer obj = getBatchTransfer(row);
        switch (column) {
            case INDEX_COLUMN:
                return String.valueOf(table.convertRowIndexToView(row)+1);
            case ADDRESS_COLUMN:
                return StringUtils.trimToEmpty(obj.getAddress());
            case VALUE_COLUMN:
                return NumberUtils.toDouble(obj.getValue());
            case REMARK_COLUMN:
                return StringUtils.trimToEmpty(obj.getRemark());
            case HASH_COLUMN:
                return StringUtils.trimToEmpty(obj.getHash());
        }
        return null;
    }

    public BatchTransfer getBatchTransfer(int row) {
        return list.get(row);
    }
    
    public List<BatchTransfer> getBatchTransfers() {
        return list;
    }

    public void removeRow(int row) {
        list.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void clear() {
        list.clear();
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return !(columnIndex == INDEX_COLUMN || columnIndex == HASH_COLUMN);
    }

    @Override
    public void setValueAt(Object val, int row, int column) {
	BatchTransfer obj = getBatchTransfer(row);
        switch (column) {
            case INDEX_COLUMN:
		return;
            case ADDRESS_COLUMN:
		obj.setAddress((String)val);
		break;
            case VALUE_COLUMN:
		// 在前端不以科学计数方法显示
		Double valDouble = NumberUtils.toDouble((String)val);
		NumberFormat nf = NumberFormat.getInstance();
		// 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(18);    // 保留18位小数
		// 结果未做任何处理
		obj.setValue(nf.format(valDouble));
		break;
            case REMARK_COLUMN:
		obj.setRemark((String)val);
		break;
            case HASH_COLUMN:
                return;
        }
	fireTableCellUpdated(row, column); // informe any object about changes
    }
}
