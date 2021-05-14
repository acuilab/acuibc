package com.acuilab.bc.main.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;

/**
 *
 * @author admin
 */
public class Cfx123Action4Menu extends AbstractAction {
    
    public Cfx123Action4Menu() {
        putValue(NAME, "123cfx.com");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/123cfx16.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(Desktop.isDesktopSupported()) {
            try {
                if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    // 打开默认浏览器
                    Desktop.getDesktop().browse(URI.create("https://123cfx.com/"));
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}