package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import java.awt.Color;
import java.awt.Component;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 *
 * @author admin
 */
public class ValueInvalidColorHighlighter extends ColorHighlighter {
    private final BatchTransferTableModel tableModel;

    public ValueInvalidColorHighlighter(BatchTransferTableModel tableModel, HighlightPredicate predicate, Color cellBackground) {
        super(predicate, cellBackground, null);
        this.tableModel = tableModel;
    }

    @Override
    protected void applyBackground(Component renderer, ComponentAdapter adapter) {
        BatchTransfer bt = tableModel.getBatchTransfer(adapter.convertRowIndexToModel(adapter.row));
        if(!NumberUtils.isParsable(bt.getValue())) {
	    super.applyBackground(renderer, adapter); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
