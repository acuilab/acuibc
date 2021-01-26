package com.acuilab.bc.main.dapp;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cuizhf
 */
public class DAppTableModel extends AbstractTableModel {
    
    private final JXTable table;
    
    public static final int INDEX_COLUMN = 0;
    public static final int ICON_COLUMN = 1;
    public static final int BLOCK_CHAIN_SYMBOL_COLUMN = 2;
    public static final int NAME_COLUMN = 3;
    public static final int DESC_COLUMN = 4;
    public static final int COLUMN_COUNT = 5;
    
    private final List<IDApp> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "#",
        "链",
        "名称",
        "描述"
    };
    
    public DAppTableModel(JXTable table) {
        this.table = table;
    }

    public void add(List<IDApp> newList) {
        
        int first = list.size();
        int last = first + newList.size() - 1;
        System.out.println("first=" + first + ", last=" + last);
        if(last > -1) {
            list.addAll(newList);
            fireTableRowsInserted(first, last);
        }
    }

    public void add(IDApp obj) {
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
        
        IDApp dapp = getDApp(row);
        switch (column) {
            case INDEX_COLUMN:
                return String.valueOf(table.convertRowIndexToView(row)+1);
            case ICON_COLUMN:
                return dapp.getIcon();
            case BLOCK_CHAIN_SYMBOL_COLUMN:
                return dapp.getBlockChainSymbol();
            case NAME_COLUMN:
                return StringUtils.trimToEmpty(dapp.getName());
            case DESC_COLUMN:
                return StringUtils.trimToEmpty(dapp.getDesc());
        }
        return null;
    }

    public IDApp getDApp(int row) {
        return list.get(row);
    }
    
    public List<IDApp> getTransferRecords() {
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

}
