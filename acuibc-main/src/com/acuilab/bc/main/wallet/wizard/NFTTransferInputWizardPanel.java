package com.acuilab.bc.main.wallet.wizard;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.wallet.Wallet;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import com.acuilab.bc.main.nft.INFT;

public class NFTTransferInputWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    private final Wallet wallet;
    private final INFT nft;
    
    public NFTTransferInputWizardPanel(Wallet wallet, INFT nft) {
        this.wallet = wallet;
        this.nft = nft;
    }
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private NFTTransferInputVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public NFTTransferInputVisualPanel getComponent() {
        if (component == null) {
            component = new NFTTransferInputVisualPanel(wallet, nft);
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
        wiz.putProperty("recvAddress", component.getRecvAddressFld().getText());
        wiz.putProperty("value", component.getValueFld().getText());
        wiz.putProperty("isGasDefault", component.isGasDefault());
        wiz.putProperty("gas", component.getGasSlider().getValue());
    }

    @Override
    public void validate() throws WizardValidationException {
        BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());
        // 转账地址是有效的
        String recvAddress = component.getRecvAddressFld().getText();
        if(!bc.isValidAddress(recvAddress)) {
            component.getRecvAddressFld().requestFocus();
            throw new WizardValidationException(null, "地址格式错误", null);
        }
               
       // 不能给自己转账
       if(StringUtils.equals(wallet.getAddress(), recvAddress)) {
            component.getRecvAddressFld().requestFocus();
            throw new WizardValidationException(null, "收款人不能是自己", null);
       }
    }

}