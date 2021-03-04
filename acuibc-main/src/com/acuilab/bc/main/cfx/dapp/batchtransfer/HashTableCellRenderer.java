package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.util.Constants;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.util.Exceptions;

/**
 *
 * @author admin
 */
public class HashTableCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
//	JXHyperlink link = (JXHyperlink)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	JXHyperlink link = new JXHyperlink();
	if(column == BatchTransferTableModel.HASH_COLUMN){
	    BatchTransferTableModel tableModel = (BatchTransferTableModel)table.getModel();
	    BatchTransfer batchTransfer = tableModel.getBatchTransfer(table.convertRowIndexToModel(row));
	    link.setText(batchTransfer.getHash());
	    BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
//	    link.setURI(URI.create(bc.getTransactionDetailUrl(batchTransfer.getHash())));
	    link.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
		    
		    if(Desktop.isDesktopSupported()) {
			try {
			    if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				// 打开默认浏览器
				Desktop.getDesktop().browse(URI.create(bc.getTransactionDetailUrl(batchTransfer.getHash())));
			    }
			} catch (IOException ex) {
			    Exceptions.printStackTrace(ex);
			}
		    }
			    
		}
		
	    });
	}
	
	return link;
    }
    
}
