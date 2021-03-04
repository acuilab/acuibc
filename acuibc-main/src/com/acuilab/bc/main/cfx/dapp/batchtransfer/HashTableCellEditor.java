package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.util.Constants;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author admin
 */
public class HashTableCellEditor extends DefaultCellEditor {
    private final JXPanel panel;
    private final JButton link;
    
    public HashTableCellEditor() {
        // DefautlCellEditor有此构造器，需要传入一个，但这个不会使用到，直接new一个即可。
	super(new JTextField());
        
        // 设置点击几次激活编辑。
        this.setClickCountToStart(1);
        
        this.link = new JButton();
        // 设置按钮的大小及位置。
        this.link.setBounds(0, 0, 50, 15);
        BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
 
        // 为按钮添加事件。这里只能添加ActionListner事件，Mouse事件无效。
        this.link.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // 触发取消编辑的事件，不会调用tableModel的setValue方法。
                HashTableCellEditor.this.fireEditingCanceled();
 
                // 这里可以做其它操作。
                // 可以将table传入，通过getSelectedRow,getSelectColumn方法获取到当前选择的行和列及其它操作等。
                System.out.println("-------------------------------------------");
            }
        });
        
        this.panel = new JXPanel();
        this.panel.setLayout(null);         // panel使用绝对定位，这样link就不会充满整个单元格。
        
        this.panel.add(this.link);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // 只为按钮赋值即可。也可以作其它操作。
        this.link.setText(value == null ? "" : String.valueOf(value));
 
        return this.panel;
    }
    
    /**
     * 重写编辑单元格时获取的值。如果不重写，这里可能会为按钮设置错误的值。
     */
    @Override
    public Object getCellEditorValue()
    {
        return this.link.getText();
    }
}
