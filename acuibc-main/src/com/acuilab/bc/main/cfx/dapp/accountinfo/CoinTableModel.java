package com.acuilab.bc.main.cfx.dapp.accountinfo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cuizhf
 */
public class CoinTableModel extends AbstractTableModel {
    
    private final JXTable table;
    
    public static final int INDEX_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int BALANCE_COLUMN = 2;
    public static final int COLUMN_COUNT = 3;
    
    private final List<Coin> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "名称",
        "余额"
    };
    
    public CoinTableModel(JXTable table) {
        this.table = table;
    }

    public void add(List<Coin> newList) {
        
        int first = list.size();
        int last = first + newList.size() - 1;
        if(last > -1) {
            list.addAll(newList);
            fireTableRowsInserted(first, last);
        }
    }

    public void add(Coin obj) {
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
        
        Coin coin = getCoin(row);
        switch (column) {
            case INDEX_COLUMN:
                return String.valueOf(table.convertRowIndexToView(row)+1);
            case NAME_COLUMN:
                return StringUtils.trimToEmpty(coin.getSymbal());
            case BALANCE_COLUMN:
                return coin.getBalance();
        }
        return null;
    }

    public Coin getCoin(int row) {
        return list.get(row);
    }
    
    public List<Coin> getAllUserData() {
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
