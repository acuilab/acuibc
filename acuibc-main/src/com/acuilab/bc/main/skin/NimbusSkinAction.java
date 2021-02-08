package com.acuilab.bc.main.skin;

/**
 *
 * @author admin
 */
public class NimbusSkinAction extends SkinAction {

    public NimbusSkinAction() {
	super("Nimbus");
    }

    @Override
    public String getSkinClass() {
	return "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    }
    
}
