package com.acuilab.bc.main.wallet;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author admin
 */
public class BlockChainSymbolTableCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
	JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if(column == WalletList2TableModel.BLOCK_CHAIN_SYMBOL_COLUMN){
	    label.setText(null);
	    WalletList2TableModel tableModel = (WalletList2TableModel)table.getModel();
	    Wallet wallet = tableModel.getWallet(table.convertRowIndexToModel(row));
	    BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());
            label.setIcon(bc.getIcon(16));
	}
	
	return label;
    }
    
}
