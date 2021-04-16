package com.acuilab.bc.main.wallet.common;

import com.acuilab.bc.main.wallet.Wallet;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class SelectWalletWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    private final String blockChainSymbol;
    
    public SelectWalletWizardPanel(String blockChainSymbol) {
        this.blockChainSymbol = blockChainSymbol;
    }
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SelectWalletVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public SelectWalletVisualPanel getComponent() {
        if (component == null) {
            component = new SelectWalletVisualPanel(blockChainSymbol);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        wiz.putProperty("wallet", getComponent().getWallet());
    }

    @Override
    public void validate() throws WizardValidationException {
        Wallet wallet = component.getWallet();
        if(wallet == null) {
            throw new WizardValidationException(null, "请选择钱包", null);
        }
        
    }

}
