package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author cuizhf
 */
public class TransferRecordTableModel extends AbstractTableModel {
    
    public static final int INDEX_COLUMN = 0;
    public static final int STATUS_COLUMN = 1;
    public static final int SENDADDRESS_COLUMN = 2;
    public static final int RECVADDRESS_COLUMN = 3;
    public static final int HASH_COLUMN = 4;
    public static final int CREATED_COLUMN = 5;
    public static final int REMARK_COLUMN = 6;
    public static final int COLUMN_COUNT = 7;
    
    private final List<TransferRecord> list = new ArrayList<>();
    private final JTable table;
    
    public static final String[] COLUMNIDS = {
        "序号",
        "交易状态",
        "发送方",
        "接收方",
        "交易哈希",
        "时间",
        "备注"
    };
    
    public TransferRecordTableModel(JTable table) {
	this.table = table;
    }

    public void add(List<TransferRecord> newList) {
        int first = list.size();
        int last = first + newList.size() - 1;
        if(last > -1) {
            list.addAll(newList);
            fireTableRowsInserted(first, last);
        }
    }

    public void add(TransferRecord obj) {
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
        
        TransferRecord tr = getTransferRecord(row);
        switch (column) {
            case INDEX_COLUMN:
                return String.valueOf(table.convertRowIndexToView(row)+1);
            case STATUS_COLUMN:
                return tr.getStatus();
            case SENDADDRESS_COLUMN:
                return tr.getSendAddress();
            case RECVADDRESS_COLUMN:
                return tr.getRecvAddress();
            case REMARK_COLUMN:
                return tr.getRemark();
            case HASH_COLUMN:
                return tr.getHash();
            case CREATED_COLUMN:
                return DateUtil.commonDateFormat(tr.getCreated(), "yyyy-MM-dd HH:mm:ss");
        }
        return null;
    }

    public TransferRecord getTransferRecord(int row) {
        return list.get(row);
    }
    
    public List<TransferRecord> getTransferRecords() {
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
