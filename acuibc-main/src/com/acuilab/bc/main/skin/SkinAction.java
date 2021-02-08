package com.acuilab.bc.main.skin;

import com.acuilab.bc.main.util.Utils;
import com.google.common.collect.Lists;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.actions.Presenter;

/**
 *
 * @author cuizhf
 */
public abstract class SkinAction extends AbstractAction implements Presenter.Menu {
    
    public static final String ROOT_SKIN_KEY = "root.skin";
    
    // skinName <=> SkinAction
    private static final List<SkinAction> skinActions = Lists.newArrayList();
    private final String skinName;
    private final JCheckBoxMenuItem menuItem;
    
    public SkinAction(String skinName) {
        this.skinName = skinName;
        
        putValue(NAME, skinName);
        menuItem = new JCheckBoxMenuItem(this);
        menuItem.setSelected(StringUtils.equals(Utils.getSkinClassName(), getSkinClass()));
	
        skinActions.add(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        for(SkinAction skinAction : skinActions) {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem)skinAction.getMenuPresenter();
            if(item != menuItem && item.isSelected()) {
                item.setSelected(false);
            }
        }
        
        try {
	    // 切换外观
	    String skinClass = getSkinClass();
	    UIManager.setLookAndFeel(skinClass);
	    Utils.applyUIChanges(null);
            
            NbPreferences.root().put(ROOT_SKIN_KEY, skinClass);  // 保存到首选项
        } catch (Exception ex) {
	    Exceptions.printStackTrace(ex);
	}
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return menuItem;
    }
    
    public abstract String getSkinClass();
}
