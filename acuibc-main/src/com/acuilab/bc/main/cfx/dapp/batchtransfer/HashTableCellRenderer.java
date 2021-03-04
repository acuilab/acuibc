package com.acuilab.bc.main.cfx.dapp.batchtransfer;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.util.Constants;
import java.awt.Component;
import java.net.URI;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author admin
 */
public class HashTableCellRenderer implements TableCellRenderer {
    private final JXPanel panel;
    private final JButton link;
    
    public HashTableCellRenderer() {
        this.link = new JButton();
        // 设置按钮的大小及位置。
        this.link.setBounds(0, 0, 50, 15);
        // 这里不要添加事件，在渲染器里边添加按钮的事件是不会触发的
        
        this.panel = new JXPanel();
        this.panel.setLayout(null);         // panel使用绝对定位，这样link就不会充满整个单元格；这里正好需要link充满整个单元格
        
        this.panel.add(this.link);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        // 只为按钮赋值即可。也可以作其它操作，如绘背景等。
        this.link.setText(value == null ? "" : String.valueOf(value));

        return this.panel;
    }
}
