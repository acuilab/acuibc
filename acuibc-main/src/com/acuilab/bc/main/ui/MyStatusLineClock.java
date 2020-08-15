package com.acuilab.bc.main.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author admin
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class MyStatusLineClock implements StatusLineElementProvider {
    private static final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINESE);
    private static final JLabel time = new JLabel(" " + format.format(new Date()) + " ");
    private final JPanel panel = new JPanel(new BorderLayout());
    
    public MyStatusLineClock() {
        Timer t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time.setText(" " + format.format(new Date()) + " ");
            }
        });
        t.start();
        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        panel.add(time, BorderLayout.CENTER);
    }

    @Override
    public Component getStatusLineElement() {
        return panel;
    }
    
}
