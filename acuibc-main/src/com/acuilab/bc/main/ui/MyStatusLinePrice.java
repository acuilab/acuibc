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
    private static final JLabel danPriceLbl = new JLabel();
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
                Double cfxPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_CFX_SYMBOL);
                cfxPriceLbl.setText(formatPrice(cfxPrice) + " ");
                cfxPriceLbl.setToolTipText("CFX: " + cfxPrice + "$");
                
                Double fcPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_FC_SYMBOL);
                fcPriceLbl.setText(formatPrice(fcPrice) + " ");
                fcPriceLbl.setToolTipText("FC: " + fcPrice + "$");
                
                Double moonPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_MOON_SYMBOL);
                moonPriceLbl.setText(formatPrice(moonPrice) + " ");
                moonPriceLbl.setToolTipText("MOON: " + moonPrice + "$");
                
                Double danPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_DAN_SYMBOL);
                danPriceLbl.setText(formatPrice(danPrice) + " ");
                danPriceLbl.setToolTipText("DAN: " + danPrice + "$");
                
                Double yaoPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_YAO_SYMBOL);
                yaoPriceLbl.setText(formatPrice(yaoPrice) + " ");
                yaoPriceLbl.setToolTipText("YAO: " + yaoPrice + "$");
                
                Double treaPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_TREA_SYMBOL);
                treaPriceLbl.setText(formatPrice(treaPrice) + " ");
                treaPriceLbl.setToolTipText("TREA: " + treaPrice + "$");
                
                Double fluxPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_FLUX_SYMBOL);
                fluxPriceLbl.setText(formatPrice(fluxPrice) + " ");
                fluxPriceLbl.setToolTipText("Flux: " + fluxPrice + "$");
                
                Double itfPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_ITF_SYMBOL);
                itfPriceLbl.setText(formatPrice(itfPrice) + " ");
                itfPriceLbl.setToolTipText("ITF: " + itfPrice + "$");
                
                Double poolgoPrice = PriceManager.getDefault().getPrice(Constants.CFX_BLOCKCHAIN_SYMBAL, Constants.CFX_POOLGO_SYMBOL);
                poolgoPriceLbl.setText(formatPrice(poolgoPrice) + " ");
                poolgoPriceLbl.setToolTipText("PoolGo: " + poolgoPrice + "$");
            }
        });
        t.start();
        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        JXPanel pricePanel = new JXPanel();
        BoxLayout layout = new BoxLayout(pricePanel, BoxLayout.X_AXIS);
        pricePanel.setLayout(layout); 
        JLabel cfxLbl = new JLabel(" CFX: ");
        cfxLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(cfxLbl);
        cfxPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(cfxPriceLbl);

        JLabel fcLbl = new JLabel("FC: ");
        fcLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(fcLbl);
        fcPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(fcPriceLbl);
        
        JLabel moonLbl = new JLabel("MOON: ");
        moonLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(moonLbl);
        moonPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(moonPriceLbl);
        
        JLabel danLbl = new JLabel("DAN: ");
        danLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(danLbl);
        danPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(danPriceLbl);
        
        JLabel yaoLbl = new JLabel("YAO: ");
        yaoLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(yaoLbl);
        yaoPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(yaoPriceLbl);
        
        JLabel treaLbl = new JLabel("TREA: ");
        treaLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(treaLbl);
        treaPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(treaPriceLbl);
        
        JLabel fluxLbl = new JLabel("FLUX: ");
        fluxLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(fluxLbl);
        fluxPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(fluxPriceLbl);
        
        JLabel itfLbl = new JLabel("ITF: ");
        itfLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(itfLbl);
        itfPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(itfPriceLbl);
        
        JLabel poolgoLbl = new JLabel("PoolGo: ");
        poolgoLbl.setFont(new java.awt.Font("宋体", 1, 12));
        pricePanel.add(poolgoLbl);
        poolgoPriceLbl.setForeground(Color.BLUE);
        pricePanel.add(poolgoPriceLbl);
        
        panel.add(pricePanel, BorderLayout.CENTER);
    }

    @Override
    public Component getStatusLineElement() {
        return panel;
    }
    
    private String formatPrice(Double price) {
        if(price == null) {
            return "无";
        }
        
        return df.format(price);
    }
}
