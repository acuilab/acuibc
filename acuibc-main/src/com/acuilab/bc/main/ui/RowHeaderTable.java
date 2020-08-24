package com.acuilab.bc.main.ui;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 * 带有显示行号功能的JTable 支持设置行高，添加行，删除行等动态事件
 *
 * @author Administrator
 * @see http://acuilab.com:8080/articles/2020/08/23/1598188948906.html
 *
 */
public class RowHeaderTable extends JXTable {

    private static final long serialVersionUID = 1L;

    private int rowHeaderWidth = 40;
    private final RowHeader rowHeader = new RowHeader();
    private boolean rowHeaderAutoShow = false;

    /*
	 * 添加行表头到滚动面板里
	 * @see javax.swing.JTable#configureEnclosingScrollPane()
     */
    @Override
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();

        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            JViewport port = (JViewport) parent;
            Container gp = port.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                scrollPane.setRowHeaderView(rowHeader);
                rowHeaderAutoShow = true;
            }
        }
    }

    @Override
    public void setRowHeight(int rowHeight) {
        // TODO Auto-generated method stub
        super.setRowHeight(rowHeight);
        if (rowHeader != null) {
            rowHeader.setRowHeight(rowHeight);
        }
    }

    @Override
    public void setRowHeight(int row, int rowHeight) {
        // TODO Auto-generated method stub
        super.setRowHeight(row, rowHeight);
        rowHeader.setRowHeight(row, rowHeight);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        // TODO Auto-generated method stub
        super.tableChanged(e);
        if (rowHeaderAutoShow) {
            rowHeader.updateAndrepaint();
        }
    }

    // TODO: 该方法不起作用
    public void setRowHeaderWidth(int width) {
        this.rowHeaderWidth = width;
        rowHeader.updateAndrepaint();
    }

    class RowHeader extends JTable {

        private static final long serialVersionUID = 1L;

        private RowHeader() {
            super(new RowHeaderTableModel());
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            this.getColumnModel().getColumn(0).setPreferredWidth(rowHeaderWidth);
            this.setPreferredScrollableViewportSize(new Dimension(rowHeaderWidth, 0));
            this.setRowHeight(RowHeaderTable.this.getRowHeight());
            setBackground(getTableHeader().getBackground());
            setAutoCreateColumnsFromModel(false);
            
            // 设置行号居中
            DefaultTableCellRenderer render = new DefaultTableCellRenderer();
            render.setHorizontalAlignment(SwingConstants.CENTER);
            this.getColumnModel().getColumn(0).setCellRenderer(render);

            //统一选择行
            this.setSelectionModel(RowHeaderTable.this.getSelectionModel());
        }

        public void updateAndrepaint() {
            revalidate();
            repaint();
        }
    }

    class RowHeaderTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        @Override
        public int getRowCount() {
            return RowHeaderTable.this.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int row, int column) {
            return row + 1;
        }
    }
}
