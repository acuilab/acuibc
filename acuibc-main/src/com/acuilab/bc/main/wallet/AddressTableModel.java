package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.util.DateUtil;
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
public class AddressTableModel extends AbstractTableModel {
    
    private final JXTable table;
    
    public static final int INDEX_COLUMN = 0;
    public static final int TYPE_COLUMN = 1;
    public static final int ADDRESS_COLUMN = 2;
    public static final int REMARK_COLUMN = 3;
    public static final int CREATED_COLUMN = 4;
    public static final int COLUMN_COUNT = 5;
    
    private final List<Address> list = new ArrayList<>();
    
    public static final String[] COLUMNIDS = {
        "",
        "类型",
        "地址",
        "备注",
        "创建日期",
    };
    
    public AddressTableModel(JXTable table) {
        this.table = table;
    }

    public void add(List<Address> newList) {
        
        int first = list.size();
        int last = first + newList.size() - 1;
        if(last > -1) {
            list.addAll(newList);
            fireTableRowsInserted(first, last);
        }
    }

    public void add(Address obj) {
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
        
        Address address = getAddress(row);
        switch (column) {
            case INDEX_COLUMN:
                return String.valueOf(table.convertRowIndexToView(row)+1);
            case TYPE_COLUMN:
                return StringUtils.trimToEmpty(address.getBlockChainSymbol());
            case ADDRESS_COLUMN:
                return StringUtils.trimToEmpty(address.getAddress());
            case REMARK_COLUMN:
                return StringUtils.trimToEmpty(address.getRemark());
            case CREATED_COLUMN:
                return DateUtil.commonDateFormat(address.getCreated(), "yyyy-MM-dd");
        }
        return null;
    }

    public Address getAddress(int row) {
        return list.get(row);
    }
    
    public List<Address> getAddresses() {
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
