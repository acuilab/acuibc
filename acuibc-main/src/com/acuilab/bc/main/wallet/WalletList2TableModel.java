package com.acuilab.bc.main.wallet;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cuizhf
 */
public class WalletList2TableModel extends AbstractTableModel {
    
    private final JXTable table;
    
    public static final int INDEX_COLUMN = 0;
    public static final int BLOCK_CHAIN_SYMBOL_COLUMN = 1;
    public static final int NAME_COLUMN = 2;
    public static final int ADDRESS_COLUMN = 3;
    public static final int COLUMN_COUNT = 4;
    
    private final List<Wallet> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "链",
        "名称",
	"地址"
    };
    
    public WalletList2TableModel(JXTable table) {
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
            case BLOCK_CHAIN_SYMBOL_COLUMN:
                return wallet.getBlockChainSymbol();
            case NAME_COLUMN:
                return StringUtils.trimToEmpty(wallet.getName());
	    case ADDRESS_COLUMN:
		return StringUtils.trimToEmpty(wallet.getAddress());
        }
        return null;
    }

    public Wallet getWallet(int row) {
        return list.get(row);
    }
    
    public List<Wallet> getTransferRecords() {
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
