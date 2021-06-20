package com.acuilab.bc.main.cfx.dapp.guguo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cuizhf
 */
public class UserDataTableModel extends AbstractTableModel {
    
    private final JXTable table;
    
    public static final int INDEX_COLUMN = 0;
    public static final int USER_ADDRESS_COLUMN = 1;
    public static final int CURRENT_BALANCE_COLUMN = 2;
    public static final int CURRENT_PENDINGS_COLUMN = 3;
    public static final int TOTAL_XIANG_COLUMN = 4;
    public static final int CONSUMED_COLUMN = 5;
    public static final int TOTAL_CONSUMED_COLUMN = 6;
    public static final int TOTAL_CLEARED_COLUMN = 7;
    public static final int COLUMN_COUNT = 8;
    
    private final List<UserData> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "userAddress",
        "currentBalance",
        "currentPendings",
        "totalXIANG", 
        "consumed",
        "totalConsumed",
        "totalCleared",
    };
    
    public UserDataTableModel(JXTable table) {
        this.table = table;
    }

    public void add(List<UserData> newList) {
        
        int first = list.size();
        int last = first + newList.size() - 1;
        if(last > -1) {
            list.addAll(newList);
            fireTableRowsInserted(first, last);
        }
    }

    public void add(UserData obj) {
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
        
        UserData userData = getUserData(row);
        switch (column) {
            case INDEX_COLUMN:
                return String.valueOf(table.convertRowIndexToView(row)+1);
            case USER_ADDRESS_COLUMN:
                return StringUtils.trimToEmpty(userData.getUserAddress());
            case CURRENT_BALANCE_COLUMN:
                return userData.getCurrentBalance();
            case CURRENT_PENDINGS_COLUMN:
                return userData.getCurrentPendings();
            case TOTAL_XIANG_COLUMN:
                return userData.getTotalXiang();
            case CONSUMED_COLUMN:
                return userData.getConsumed();
            case TOTAL_CONSUMED_COLUMN:
                return userData.getTotalConsumed();
            case TOTAL_CLEARED_COLUMN:
                return userData.getTotalCleared();
        }
        return null;
    }

    public UserData getUserData(int row) {
        return list.get(row);
    }
    
    public List<UserData> getAllUserData() {
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
