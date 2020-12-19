package com.acuilab.bc.main.ui;

import com.acuilab.bc.main.nft.MetaData;
import javax.swing.JToolTip;
import org.jdesktop.swingx.JXImageView;

/**
 *
 * @author admin
 */
public class MyJXImageView extends JXImageView {
    
    private MetaData metaData;
    private ImageToolTip toolTip;

    public void setMetaData(MetaData metaData) {
	this.metaData = metaData;
    }

    @Override
    public JToolTip createToolTip() {
        if (toolTip == null) {
             toolTip = new ImageToolTip(metaData, this.getImage());
        }
       
        return toolTip;
    }
    
}
