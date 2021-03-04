package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author admin
 */
public class HashTableCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
	
	JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if(column == BatchTransferTableModel.VALUE_COLUMN){
	    BatchTransferTableModel tableModel = (BatchTransferTableModel)table.getModel();
	    BatchTransfer batchTransfer = tableModel.getBatchTransfer(table.convertRowIndexToModel(row));
	    String val = batchTransfer.getValue();
	    label.setText(val);
	    label.addMouseListener(new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
		    System.out.println("mouseClicked...................................");
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	    });
	}
	
	return label;
	
	
//	JXHyperlink link = (JXHyperlink)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//	JXHyperlink link = new JXHyperlink();
//	if(column == BatchTransferTableModel.HASH_COLUMN){
//	    BatchTransferTableModel tableModel = (BatchTransferTableModel)table.getModel();
//	    BatchTransfer batchTransfer = tableModel.getBatchTransfer(table.convertRowIndexToModel(row));
//	    link.setText(batchTransfer.getHash());
//	    BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
//	    link.addActionListener(new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent e) {
//		    BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
//		    
//		    if(Desktop.isDesktopSupported()) {
//			try {
//			    if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//				// 打开默认浏览器
//				Desktop.getDesktop().browse(URI.create(bc.getTransactionDetailUrl(batchTransfer.getHash())));
//			    }
//			} catch (IOException ex) {
//			    Exceptions.printStackTrace(ex);
//			}
//		    }
//		}
//	    });
//	}
//	
//	return link;
    }
    
}
