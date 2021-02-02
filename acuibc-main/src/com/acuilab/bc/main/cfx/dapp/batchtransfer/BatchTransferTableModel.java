package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;
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
    public static final int RESULT_COLUMN = 3;
    public static final int COLUMN_COUNT = 4;
    
    private final List<BatchTransfer> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "地址",
        "数量",
        "结果"
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
                return "";
            case RESULT_COLUMN:
                return "";
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
	return !(columnIndex == INDEX_COLUMN || columnIndex == RESULT_COLUMN);
    }

    
}
