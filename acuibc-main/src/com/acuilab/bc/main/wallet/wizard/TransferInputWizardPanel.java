package com.acuilab.bc.main.wallet.wizard;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.util.RegExpValidatorUtils;
import com.acuilab.bc.main.wallet.Wallet;
import java.math.BigInteger;
import java.math.RoundingMode;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import com.acuilab.bc.main.coin.ICoin;

public class TransferInputWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    private final Wallet wallet;
    private final ICoin coin;
    
    public TransferInputWizardPanel(Wallet wallet, ICoin coin) {
        this.wallet = wallet;
        this.coin = coin;
    }
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private TransferInputVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public TransferInputVisualPanel getComponent() {
        if (component == null) {
            component = new TransferInputVisualPanel(wallet, coin);
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
       
       // 非空
       String value = component.getValueFld().getText();
       if(StringUtils.isBlank(value)) {
           component.getValueFld().requestFocus();
           throw new WizardValidationException(null, "转账数量不能为空", null);
       }
       
//       NumberUtils.toDouble(value);
//       if(StringUtils.equals(value, "0")) {
//           component.getValueFld().requestFocus();
//           throw new WizardValidationException(null, "转账数量不能为0", null);
//       }
       
       if(coin.isDivisible()) {
            // 可分割，验证非零正数
//            if(!RegExpValidatorUtils.isPositive(value)) {
//                component.getValueFld().requestFocus();
//                throw new WizardValidationException(null, "转账数量为0或格式错误", null);
//            }
            double doubleValue = NumberUtils.toDouble(value);
            if(doubleValue <= 0.0d) {
                component.getValueFld().requestFocus();
                throw new WizardValidationException(null, "转账数量无效或格式错误", null);
            }
       } else {
           // 不可分割，验证非零整数
//            if(!RegExpValidatorUtils.IsIntNumber(value)) {
//                component.getValueFld().requestFocus();
//                throw new WizardValidationException(null, "转账数量为0或格式错误，注意" + coin.getSymbol()+ "不可分割，请输入正整数", null);
//            }
            int intValue = NumberUtils.toInt(value);
            if(intValue <=0){
                component.getValueFld().requestFocus();
                throw new WizardValidationException(null, "转账数量无效或格式错误，注意" + coin.getSymbol()+ "不可分割，请输入正整数", null);
            }
       }
       
       // 数量不能超过余额（暂未考虑矿工费）
       // 矿工费有两种方式，一种是sdk估算，一种是用户指定
       if(!component.balanceAvailable()) {
           throw new WizardValidationException(null, "正在请求余额，请稍候...", null);
       }
       BigInteger balance = component.getBalance();
       BigInteger valueDrip = coin.mainUint2MinUint(NumberUtils.toDouble(component.getValueFld().getText()));
       if(balance.compareTo(valueDrip) < 0) {
           component.getValueFld().requestFocus();
           throw new WizardValidationException(null, "余额不足【" + "可用：" + coin.minUnit2MainUint(balance).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).toPlainString() + " " + coin.getMainUnit() + "】", null);
       }
       
    }

}
