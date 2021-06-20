package com.acuilab.bc.main.ui;

import com.acuilab.bc.main.manager.PriceManager;
import com.acuilab.bc.main.util.Constants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.jdesktop.swingx.JXPanel;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author admin
 */
@ServiceProvider(service = StatusLineElementProvider.class, position = 1)
public class MyStatusLinePrice implements StatusLineElementProvider {
    private static final DecimalFormat df = new DecimalFormat("#0.000");
    private static final JLabel cfxPriceLbl = new JLabel();
    private static final JLabel fcPriceLbl = new JLabel();
    private static final JLabel moonPriceLbl = new JLabel();
    private static final JLabel yaoPriceLbl = new JLabel();
    private static final JLabel treaPriceLbl = new JLabel();
    private static final JLabel fluxPriceLbl = new JLabel();
    private static final JLabel itfPriceLbl = new JLabel();
    private static final JLabel poolgoPriceLbl = new JLabel();
    private final JPanel panel = new JPanel(new BorderLayout());
    
    public MyStatusLinePrice() {
        // 每6秒刷新一次
        Timer t = new Timer(6000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cfxPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_CFX_SYMBOL)) + " ");
                cfxPriceLbl.setToolTipText("CFX: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_CFX_SYMBOL) + "$");
                fcPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_FC_SYMBOL)) + " ");
                fcPriceLbl.setToolTipText("FC: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_FC_SYMBOL) + "$");
                moonPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_MOON_SYMBOL)) + " ");
                moonPriceLbl.setToolTipText("MOON: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_MOON_SYMBOL) + "$");
                yaoPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL)) + " ");
                yaoPriceLbl.setToolTipText("YAO: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL) + "$");
                treaPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_TREA_SYMBOL)) + " ");
                treaPriceLbl.setToolTipText("TREA: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_TREA_SYMBOL) + "$");
                fluxPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_FLUX_SYMBOL)) + " ");
                fluxPriceLbl.setToolTipText("Flux: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_FLUX_SYMBOL) + "$");
                itfPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_ITF_SYMBOL)) + " ");
                itfPriceLbl.setToolTipText("ITF: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_ITF_SYMBOL) + "$");
                poolgoPriceLbl.setText(df.format(PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_POOLGO_SYMBOL)) + " ");
                poolgoPriceLbl.setToolTipText("PoolGo: " + PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_POOLGO_SYMBOL) + "$");
            }
        });
        t.start();
        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        JXPanel pricePanel = new JXPanel();
        BoxLayout layout = new BoxLayout(pricePanel, BoxLayout.X_AXIS);
        pricePanel.setLayout(layout); 
        JLabel cfxLbl = new JLabel(" CFX: ");
        cfxLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        cfxLbl.setForeground(Color.BLUE);
        pricePanel.add(cfxLbl);
        pricePanel.add(cfxPriceLbl);

        JLabel fcLbl = new JLabel("FC: ");
        fcLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        fcLbl.setForeground(Color.BLUE);
        pricePanel.add(fcLbl);
        pricePanel.add(fcPriceLbl);
        
        JLabel moonLbl = new JLabel("MOON: ");
        moonLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        moonLbl.setForeground(Color.BLUE);
        pricePanel.add(moonLbl);
        pricePanel.add(moonPriceLbl);
        
        JLabel yaoLbl = new JLabel("YAO: ");
        yaoLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        yaoLbl.setForeground(Color.BLUE);
        pricePanel.add(yaoLbl);
        pricePanel.add(yaoPriceLbl);
        
        JLabel treaLbl = new JLabel("TREA: ");
        treaLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        treaLbl.setForeground(Color.BLUE);
        pricePanel.add(treaLbl);
        pricePanel.add(treaPriceLbl);
        
        JLabel fluxLbl = new JLabel("FLUX: ");
        fluxLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        fluxLbl.setForeground(Color.BLUE);
        pricePanel.add(fluxLbl);
        pricePanel.add(fluxPriceLbl);
        
        JLabel itfLbl = new JLabel("ITF: ");
        itfLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        itfLbl.setForeground(Color.BLUE);
        pricePanel.add(itfLbl);
        pricePanel.add(itfPriceLbl);
        
        JLabel poolgoLbl = new JLabel("PoolGo: ");
        poolgoLbl.setFont(new java.awt.Font("宋体", 1, 12));
//        poolgoLbl.setForeground(Color.BLUE);
        pricePanel.add(poolgoLbl);
        pricePanel.add(poolgoPriceLbl);
        
        panel.add(pricePanel, BorderLayout.CENTER);
    }

    @Override
    public Component getStatusLineElement() {
        return panel;
    }
    
}
