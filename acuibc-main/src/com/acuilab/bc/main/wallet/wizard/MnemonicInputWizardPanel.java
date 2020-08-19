package com.acuilab.bc.main.wallet.wizard;

import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class MnemonicInputWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private MnemonicInputPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public MnemonicInputPanel getComponent() {
        if (component == null) {
            component = new MnemonicInputPanel();
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
        wiz.putProperty("importType", (String)component.getImportType());
        wiz.putProperty("mnemonicOrPrivate", component.getMnemonicArea().getText());
    }

    @Override
    public void validate() throws WizardValidationException {
        String mnemonic = component.getMnemonicArea().getText();
        String ipmportType = (String)component.getImportType();
        if(StringUtils.isBlank(mnemonic)) {
            component.getMnemonicArea().requestFocus();
            
            if(StringUtils.equals(ipmportType, "助记词")) {
                throw new WizardValidationException(null, "请输入您的助记词", null);
            } else {
                throw new WizardValidationException(null, "请输入您的私钥", null);
            }
        }
        
        // TODO: 12个单词，空格分隔
        if(StringUtils.equals(ipmportType, "助记词")) {
            String[] mnemonicArray = StringUtils.split(mnemonic, " ");
            if(mnemonicArray.length != 12) {
                component.getMnemonicArea().requestFocus();
                throw new WizardValidationException(null, "您输入的助记词不满足以空格分隔的12个英文单词", null);
            } 
        }

    }

}
