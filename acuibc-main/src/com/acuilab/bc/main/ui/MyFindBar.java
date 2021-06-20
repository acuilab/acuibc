package com.acuilab.bc.main.ui;

import java.awt.Color;
import javax.swing.BorderFactory;
import org.jdesktop.swingx.JXFindBar;

/**
 *
 * @author admin
 */
public class MyFindBar extends JXFindBar {

    @Override
    protected void bind() {
        super.bind(); //To change body of generated methods, choose Tools | Templates.
        searchLabel.setText("查找");
        findNext.setText("下一个");
        findPrevious.setText("上一个");
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    @Override
    protected int getSearchFieldWidth() {
        return 60;
    }
    
}
