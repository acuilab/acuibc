package com.acuilab.bc.main.ui;

import com.acuilab.bc.main.nft.MetaData;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import javax.swing.JToolTip;

/**
 *
 * @author admin
 */
public class ImageToolTip extends JToolTip {
    
    private MetaData metaData;
    private final Image image;

    public ImageToolTip(MetaData metaData, Image image) {
	this.metaData = metaData;
	this.image = image;
	this.setPreferredSize(new Dimension(280, 280));
    }

    @Override
    protected void paintComponent(Graphics g) {
	Graphics2D g2d = (Graphics2D)g;
        int width = (int)(getPreferredSize().getWidth());
        int height = (int)(getPreferredSize().getHeight());
        Paint oldPaint = g2d.getPaint();
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.drawImage(image, 0, 0, width, height, this);
	if(metaData != null) {
	    g2d.setPaint(Color.WHITE);
	    Font font = g2d.getFont();
	    Font newFont = new Font(font.getName(), Font.BOLD, font.getSize());
	    
	    // 计算字体宽度和高度
	    // @see https://zhidao.baidu.com/question/685805523016097932.html
	    String content = "编号：" + metaData.getId();
	    FontMetrics fm = g2d.getFontMetrics(font);
	    int fWidth = fm.stringWidth(content);
	    int fHeight = fm.getHeight();
	    g2d.setBackground(Color.BLACK);//设置背景色
	    g2d.clearRect(0, 0, fWidth, fHeight);//通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
	    g2d.setFont(newFont);
	    g2d.drawString(content, 0, fm.getAscent());
	}
	g2d.setPaint(oldPaint);
    }
    
}
