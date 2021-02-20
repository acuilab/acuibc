package com.acuilab.bc.main.wallet;

import java.util.regex.Pattern;
import javax.swing.RowFilter;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Administrator
 */
public class WalletList2Filtering extends AbstractBean {
    private RowFilter<Object, Object> searchFilter;
    
    private String filterString;
    
    private final JXTable table;
    
    public WalletList2Filtering(JXTable table) {
        this.table = table;
    }
    
    public boolean isFilteringByString() {
        return !isEmpty(getFilterString());
    }
    
    public boolean isEmpty(String filterString) {
        return filterString == null || filterString.length() == 0; 
    }
    
    public boolean isFiltering() {
        return isFilteringByString();
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
        if(searchFilter == null) {
            table.setRowFilter(null);   // 没有filter时，这里必须设置为Null清除
            return;
        }
        table.setRowFilter(searchFilter);
    }
    
    // Filter control 
    // create and return a custom RowFilter specialized on Task 
    // 支持空格分隔的多条件检索，条件之间是and的关系
    private RowFilter<Object, Object> createSearchFilter(final String filterString) {
        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(Entry<? extends Object, ? extends Object> entry) {
                WalletList2TableModel tableModel = (WalletList2TableModel) entry.getModel();
                Wallet wallet = tableModel.getWallet(((Integer) entry.getIdentifier()));
                
                if(StringUtils.contains(filterString, ";")) {
                    // 分号分隔，全|判断
                    return allOr(wallet, filterString);
                } else {
                    // 空格分隔，全&判断
                    return allAnd(wallet, filterString);
                }
            }
        };
    }
    
    // 全&判断（空格分隔）：每个关键字都匹配一遍，要求每一遍都至少有一个字段匹配上
    private boolean allAnd(Wallet wallet, String filterString) {
        for(String str : StringUtils.split(filterString, " ")) {
            Pattern p = Pattern.compile(".*" + str + ".*", Pattern.CASE_INSENSITIVE);
            
            if(match(p, wallet)) {
                continue;
            }
            
            // 没有任何一个字段匹配上
            return false;
        }

        return true;
    }
    
    // 全|判断（分号分隔）
    private boolean allOr(Wallet wallet, String filterString) {
        for(String str : StringUtils.split(filterString, ";")) {
            Pattern p = Pattern.compile(".*" + str + ".*", Pattern.CASE_INSENSITIVE);
            
            if(match(p, wallet)) {   // 计量管理状态
                return true;
            }
        }
        
        return false;
    }
    
    private boolean match(Pattern p, Wallet wallet) {
	    return match(p, wallet.getBlockChainSymbol()) || match(p, wallet.getName()) || match(p, wallet.getAddress());
    }
     
    private boolean match(Pattern p, String input) {
        if(input == null) {
            return false;
        }
        
        return p.matcher(StringUtils.trim(input)).matches();
    }
}
