package com.acuilab.bc.main.skin;

import javax.swing.UIManager;

/**
 *
 * @author admin
 */
public class MotifSkinAction extends SkinAction {

    public MotifSkinAction() {
	super("Motif");
    }

    @Override
    public String getSkinClass() {
	return "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    }
    
}
