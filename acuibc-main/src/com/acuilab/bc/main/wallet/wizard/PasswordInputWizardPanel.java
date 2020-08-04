package com.acuilab.bc.main.wallet.wizard;

import com.acuilab.bc.main.wallet.Wallet;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.springframework.util.DigestUtils;

public class PasswordInputWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {
    
    private final Wallet wallet;
    
    public PasswordInputWizardPanel(Wallet wallet) {
        this.wallet = wallet;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private PasswordInputVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public PasswordInputVisualPanel getComponent() {
        if (component == null) {
            component = new PasswordInputVisualPanel();
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
        wiz.putProperty("password", component.getPasswordFld().getText());
    }

    @Override
    public void validate() throws WizardValidationException {
        // 密码不能为空
        String pwd = component.getPasswordFld().getText();
        if(StringUtils.isBlank(pwd)) {
            component.getPasswordFld().requestFocus();
            throw new WizardValidationException(null, "请填写密码", null);
        }
        
        // 密码要一致
        String pwdMD5 = DigestUtils.md5DigestAsHex(pwd.getBytes()); 
        if(!StringUtils.equals(wallet.getPwdMD5(), pwdMD5)) {
            component.getPasswordFld().requestFocus();
            throw new WizardValidationException(null, "密码错误", null);
        }
    }

}
