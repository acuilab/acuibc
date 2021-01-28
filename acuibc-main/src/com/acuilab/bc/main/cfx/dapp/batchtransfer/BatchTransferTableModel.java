package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.dapp.*;
import com.acuilab.bc.main.wallet.*;
import com.acuilab.bc.main.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cuizhf
 */
public class BatchTransferTableModel extends AbstractTableModel {
    
    private final JXTable table;
    
    public static final int INDEX_COLUMN = 0;
    public static final int ADDRESS_COLUMN = 1;
    public static final int VALUE_COLUMN = 2;
    public static final int RESULT_COLUMN = 3;
    public static final int HASH_COLUMN = 4;
    public static final int COLUMN_COUNT = 5;
    
    private final List<Wallet> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "名称",
        "地址",
        "创建日期"
    };
    
    public BatchTransferTableModel(JXTable table) {
        this.table = table;
    }

    public void add(List<Wallet> newList) {
        
        int first = list.size();
        int last = first + newList.size() - 1;
        if(last > -1) {
            list.addAll(newList);
            fireTableRowsInserted(first, last);
        }
    }

    public void add(Wallet obj) {
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
        
        Wallet wallet = getWallet(row);
        switch (column) {
            case INDEX_COLUMN:
                return String.valueOf(table.convertRowIndexToView(row)+1);
            case NAME_COLUMN:
                return StringUtils.trimToEmpty(wallet.getName());
            case ADDRESS_COLUMN:
                return StringUtils.trimToEmpty(wallet.getAddress());
            case CREATED_COLUMN:
                return DateUtil.commonDateFormat(wallet.getCreated(), "yyyy-MM-dd");
        }
        return null;
    }

    public Wallet getWallet(int row) {
        return list.get(row);
    }
    
    public List<Wallet> getAddresses() {
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
