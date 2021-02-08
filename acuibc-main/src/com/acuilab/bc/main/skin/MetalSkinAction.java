package com.acuilab.bc.main.skin;

import javax.swing.UIManager;

/**
 *
 * @author admin
 */
public class MetalSkinAction extends SkinAction {

    public MetalSkinAction() {
	super("Metal");
    }

    @Override
    public String getSkinClass() {
	return "javax.swing.plaf.metal.MetalLookAndFeel";
    }
    
}
