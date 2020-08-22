package com.acuilab.bc.main.wallet.wizard;

import com.google.common.collect.Lists;
import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class MnemonicConfirmWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, ContainerListener {

    private final Set<ChangeListener> listeners = new HashSet<>(1);
    private List<String> mnemonicWords;
    private boolean isValid = false;
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private MnemonicConfirmVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public MnemonicConfirmVisualPanel getComponent() {
        if (component == null) {
            component = new MnemonicConfirmVisualPanel();
            component.getJXPanel1().addContainerListener(this);
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
        return isValid;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
        mnemonicWords = (List<String>)wiz.getProperty("mnemonicWords");
        getComponent().initMnemonicWords(mnemonicWords);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
    }

    @Override
    public void validate() throws WizardValidationException {
    }
    
    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        change();
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        change();
    }
    
    private void change() {
        List<String> mnemonicWordsInput = Lists.newArrayListWithCapacity(component.getJXPanel1().getComponentCount());
        for(Component comp : component.getJXPanel1().getComponents()) {
            JLabel lbl = (JLabel)comp;
            mnemonicWordsInput.add(lbl.getText());
        }

        String mnemonicWordsInputJoined = StringUtils.join(mnemonicWordsInput, " ");
        String mnemonicWordsJoined =  StringUtils.join(mnemonicWords, " ");
        boolean isValidInput = StringUtils.equals(mnemonicWordsJoined, mnemonicWordsInputJoined);
        if (isValidInput) {
            setValid(true);
        } else {
            setValid(false);
        }
    }
    
    private void setValid(boolean val) {
        if (isValid != val) {
            isValid = val;
            fireChangeEvent();  // must do this to enable next/finish button
        }
    }
}
