package com.acuilab.bc.main.wallet.action;

import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.dao.WalletDAO;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.wallet.Wallet;
import com.acuilab.bc.main.wallet.WalletList2TopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import com.acuilab.bc.main.wallet.WalletListTopComponent;
import com.acuilab.bc.main.wallet.wizard.MnemonicConfirmWizardPanel;
import com.acuilab.bc.main.wallet.wizard.MnemonicGenerateWizardPanel;
import com.acuilab.bc.main.wallet.wizard.NameCoinWizardPanel;
import com.acuilab.bc.main.wallet.wizard.PasswordWizardPanel;
import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JComponent;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;
import org.springframework.util.DigestUtils;

/**
 *
 * @author admin
 */
public class CreateAction extends AbstractAction {
    
    public CreateAction() {
        putValue(NAME, "创建钱包");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/create_wallet32.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
                try {
                    List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
                    panels.add(new NameCoinWizardPanel());
                    panels.add(new PasswordWizardPanel());
                    panels.add(new MnemonicGenerateWizardPanel());
                    panels.add(new MnemonicConfirmWizardPanel());
                    String[] steps = new String[panels.size()];
                    for (int i = 0; i < panels.size(); i++) {
                        Component c = panels.get(i).getComponent();
                        // Default step name to component name of panel.
                        steps[i] = c.getName();
                        if (c instanceof JComponent) { // assume Swing components
                            JComponent jc = (JComponent) c;
                            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                            jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                        }
                    }
                    WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
                    // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
                    wiz.setTitleFormat(new MessageFormat("{0}"));
                    wiz.setTitle("创建钱包");
                    if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                        String pwd = (String)wiz.getProperty("password");
                        String walletName = (String)wiz.getProperty("walletName");
                        String coinSymbal = (String)wiz.getProperty("coinSymbal");
                        List<String> mnemonicWords = (List<String>)wiz.getProperty("mnemonicWords");

                        // 保存钱包到数据库
                        BlockChain blockChain = BlockChainManager.getDefault().getBlockChain(coinSymbal);
                        Pair<String, String> pair = blockChain.createWalletByMnemonic(mnemonicWords);
                        
                        // 密码取md5
                        String pwdMD5 = DigestUtils.md5DigestAsHex(pwd.getBytes()); 

                        // 助记词和私钥加密
                        String mnemonicAES = AESUtil.encrypt(StringUtils.join(mnemonicWords, " "), pwd);
                        String privateKeyAES = AESUtil.encrypt(pair.getValue1(), pwd);

                        // 保存钱包
                        Wallet wallet = new Wallet(walletName, pwdMD5, blockChain.getSymbol(), pair.getValue0(), privateKeyAES, mnemonicAES, new Date());
                        WalletDAO.insert(wallet);

                        // 重新加载钱包列表
                        WalletListTopComponent tc = (WalletListTopComponent)WindowManager.getDefault().findTopComponent("WalletListTopComponent");
                        tc.addWallet(wallet);
                        WalletList2TopComponent tc2 = (WalletList2TopComponent)WindowManager.getDefault().findTopComponent("WalletList2TopComponent");
                        tc2.addWallet(wallet);
                    }
                } catch (SQLException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                    Exceptions.printStackTrace(ex);
                }
    }
}
