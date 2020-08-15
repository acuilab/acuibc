package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.util.DateUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.RowFilter;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Administrator
 */
public class TransferRecordFiltering extends AbstractBean {
    private RowFilter<Object, Object> searchFilter;
    
    private RowFilter<Object, Object> allFilter;   // 全部
    private RowFilter<Object, Object> recvFilter;  // 收款
    private RowFilter<Object, Object> sendFilter;  // 转账
    private boolean showAll = false;
    private boolean showRecv = false;
    private boolean showSend = false;
    
    private String filterString;
    
    private final JXTable table;
    
    public TransferRecordFiltering(JXTable table) {
        this.table = table;
    }
    
    public boolean isFilteringByString() {
        return !isEmpty(getFilterString());
    }
    
    public boolean isEmpty(String filterString) {
        return filterString == null || filterString.length() == 0; 
    }
    
    public boolean isFiltering() {
        return isFilteringByString() || showAll || showRecv || showSend;
    }
    
    /** 
      * @param filterString the filterString to set 
      */ 
    public void setFilterString(String filterString) { 
        String oldValue = getFilterString(); 
        // Filter control 
        // set the filter string (bound to the input in the textfield) 
        // and update the search RowFilter 
        this.filterString = filterString; 
        updateSearchFilter(); 
        firePropertyChange("filterString", oldValue, getFilterString()); 
    } 
    
     /** 
      * @return the filterString 
      */ 
     public String getFilterString() { 
         return filterString; 
     }
     
    public boolean isShowAll() {
        return showAll;
    }
    
    public boolean isShowRecv() {
        return showRecv;
    }
    
    public boolean isShowSend() {
        return showSend;
    }
    
    private void updateAllFilter() {
        allFilter = showAll ? createAllFilter() : null;
        updateFilters();
    }
    
    private void updateRecvFilter() {
        recvFilter = showRecv ? createRecvFilter() : null;
        updateFilters();
    }
    
    private void updateSendFilter() {
        sendFilter = showSend ? createSendFilter() : null;
        updateFilters();
    }
    
    public void setShowAll(boolean showAll) {
        if(isShowAll() == showAll) {
            return;
        }
        boolean oldValue = isShowAll();
        this.showAll = showAll;
        updateAllFilter();
        firePropertyChange("showAll", oldValue, isShowAll()); 
    }
    
    public void setShowRecv(boolean showRecv) {
        if(isShowRecv() == showRecv) {
            return;
        }
        boolean oldValue = isShowRecv();
        this.showRecv = showRecv;
        updateRecvFilter();
        firePropertyChange("showRecv", oldValue, isShowRecv()); 
    }
    
    public void setShowSend(boolean showSend) {
        if(isShowSend() == showSend) {
            return;
        }
        boolean oldValue = isShowSend();
        this.showSend = showSend;
        updateSendFilter();
        firePropertyChange("showSend", oldValue, isShowSend()); 
    }
    
    private void updateSearchFilter() { 
        if ((filterString != null) && (filterString.length() > 0)) { 
            searchFilter = createSearchFilter(filterString); 
        } else { 
            searchFilter = null; 
        } 
        updateFilters();
    }
    
    private void updateFilters() {
        // status filter
        if(searchFilter == null  && allFilter == null && recvFilter == null && sendFilter == null) {
            table.setRowFilter(null);   // 没有filter时，这里必须设置为Null清除
            return;
        }
        
        List<RowFilter<Object, Object>> allRecvSendFilters = Lists.newArrayListWithCapacity(3);
        if(allFilter != null) {
            allRecvSendFilters.add(allFilter);
        }
        if(recvFilter != null) {
            allRecvSendFilters.add(recvFilter);
        }
        if(sendFilter != null) {
            allRecvSendFilters.add(sendFilter);
        }
	
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if(searchFilter != null) {
            filters.add(searchFilter);
        }
        if(!allRecvSendFilters.isEmpty()) {
            filters.add(RowFilter.orFilter(allRecvSendFilters));
        }
        
        table.setRowFilter(RowFilter.andFilter(filters));
    }
    
    // Filter control 
    // create and return a custom RowFilter specialized on Task 
    // 支持空格分隔的多条件检索，条件之间是and的关系
    private RowFilter<Object, Object> createSearchFilter(final String filterString) {
        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(Entry<? extends Object, ? extends Object> entry) {
                TransferRecordTableModel tableModel = (TransferRecordTableModel) entry.getModel();
                TransferRecord transferRecord = tableModel.getTransferRecord(((Integer) entry.getIdentifier()));
                
                if(StringUtils.contains(filterString, ";")) {
                    // 分号分隔，全|判断
                    return allOr(transferRecord, filterString);
                } else {
                    // 空格分隔，全&判断
                    return allAnd(transferRecord, filterString);
                }
            }
        };
    }
    
    private RowFilter<Object, Object> createAllFilter() {
        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
                // 返回所有记录
                return Boolean.TRUE;
            }
        };
    }
    
    private RowFilter<Object, Object> createRecvFilter() {
        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
                TransferRecordTableModel tableModel = (TransferRecordTableModel) entry.getModel();
                TransferRecord transferRecord = tableModel.getTransferRecord(((Integer) entry.getIdentifier()));
                
                return StringUtils.equalsIgnoreCase(transferRecord.getWalletAddress(), transferRecord.getRecvAddress());
            }
        };
    }
    
    private RowFilter<Object, Object> createSendFilter() {
        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
                TransferRecordTableModel tableModel = (TransferRecordTableModel) entry.getModel();
                TransferRecord transferRecord = tableModel.getTransferRecord(((Integer) entry.getIdentifier()));
                
                return StringUtils.equalsIgnoreCase(transferRecord.getWalletAddress(), transferRecord.getSendAddress());
            }
        };
    }
    
    // 全&判断（空格分隔）：每个关键字都匹配一遍，要求每一遍都至少有一个字段匹配上
    private boolean allAnd(TransferRecord transferRecord, String filterString) {
        for(String str : StringUtils.split(filterString, " ")) {
            Pattern p = Pattern.compile(".*" + str + ".*", Pattern.CASE_INSENSITIVE);
            
            if(match(p, transferRecord)) {
                continue;
            }
            
            // 没有任何一个字段匹配上
            return false;
        }

        return true;
    }
    
    // 全|判断（分号分隔）
    private boolean allOr(TransferRecord transferRecord, String filterString) {
        for(String str : StringUtils.split(filterString, ";")) {
            Pattern p = Pattern.compile(".*" + str + ".*", Pattern.CASE_INSENSITIVE);
            
            if(match(p, transferRecord)) {   // 计量管理状态
                return true;
            }
        }
        
        return false;
    }
    
    private boolean match(Pattern p, TransferRecord transferRecord) {
        return match(p, transferRecord.getRecvAddress()) || 
                match(p, transferRecord.getSendAddress()) || 
                match(p, transferRecord.getHash()) ||
                match(p, DateUtil.commonDateFormat(transferRecord.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
    }
     
    private boolean match(Pattern p, String input) {
        if(input == null) {
            return false;
        }
        
        return p.matcher(StringUtils.trim(input)).matches();
    }
}
