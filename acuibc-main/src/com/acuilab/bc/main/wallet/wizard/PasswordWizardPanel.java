package com.acuilab.bc.main.wallet.wizard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class PasswordWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private PasswordVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public PasswordVisualPanel getComponent() {
        if (component == null) {
            component = new PasswordVisualPanel();
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
        wiz.putProperty("password", component.getPwdField().getText());
    }

    @Override
    public void validate() throws WizardValidationException {
        String pwd = component.getPwdField().getText();
        // 正则表达式：必须要有一个小写字母，一个大写字母和一个数字，并且是8-16位
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9]{8,16}$");
        Matcher matcher = pattern.matcher(component.getPwdField().getText());
        if(!matcher.matches()) {
            throw new WizardValidationException(null, "密码不符合要求", null);
        }
        
        String pwdConfirm = component.getPwdConfirmFeild().getText();
        if(StringUtils.isBlank(pwdConfirm)) {
            throw new WizardValidationException(null, "请填写确认密码", null);
        }
        if(!StringUtils.equals(pwd, pwdConfirm)) {
            throw new WizardValidationException(null, "两次输入密码不一致", null);
        }
    }

}
