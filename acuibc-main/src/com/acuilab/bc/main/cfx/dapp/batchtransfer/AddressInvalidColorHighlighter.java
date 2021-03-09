package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.util.Constants;
import java.awt.Color;
import java.awt.Component;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 *
 * @author admin
 */
public class AddressInvalidColorHighlighter extends ColorHighlighter {
    private final BatchTransferTableModel tableModel;

    public AddressInvalidColorHighlighter(BatchTransferTableModel tableModel, HighlightPredicate predicate, Color cellBackground) {
        super(predicate, cellBackground, null);
        this.tableModel = tableModel;
    }

    @Override
    protected void applyBackground(Component renderer, ComponentAdapter adapter) {
	BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
        BatchTransfer bt = tableModel.getBatchTransfer(adapter.convertRowIndexToModel(adapter.row));
        System.out.println("bt.getAddress(): " + bt.getAddress());
        if(!bc.isValidAddress(bt.getAddress())) {
	    super.applyBackground(renderer, adapter); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
