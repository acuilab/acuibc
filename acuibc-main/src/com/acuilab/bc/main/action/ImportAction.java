package com.acuilab.bc.main.action;

import com.acuilab.bc.main.dao.WalletDAO;
import com.acuilab.bc.main.ui.ExportFailDialog;
import com.acuilab.bc.main.util.DateUtil;
import com.acuilab.bc.main.wallet.Wallet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.LifecycleManager;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.windows.WindowManager;

/**
 *
 * @author admin
 */
public class ImportAction extends AbstractAction {
    
    private static final Logger LOG = Logger.getLogger(ImportAction.class.getName());
    
    public ImportAction() {
        putValue(NAME, "迁入钱包");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/movein32.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // 打开文件
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);    // 可以同时新建多个
        chooser.setFileFilter(new FileNameExtensionFilter("支持文件(*.gw)", "gw"));
        int returnVal = chooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();


            final ProgressHandle ph = ProgressHandle.createHandle("正在迁入钱包，请稍候");
            SwingWorker<String, Void> exportWorker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    ph.start();
                    
                    try {
                        // 先解压
                        ZipFile zipFile = new ZipFile(file);
                        FileHeader fileHeader = zipFile.getFileHeader("wallet.txt");
                        InputStream inputStream = zipFile.getInputStream(fileHeader);

                        // 执行sql
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                        String sql = br.readLine();
                        if(sql != null) {
                            int count = NumberUtils.toInt(br.readLine());
                            for(int i=0; i<count; i++) {
                                // 钱包名称
                                String walletName = br.readLine();
                                // 钱包密码
                                String pwdMD5 = br.readLine();
                                // 区块链简称
                                String blockChainSymbol = br.readLine();
                                // 钱包地址
                                String walletAddress = br.readLine();
                                // 私钥
                                String privateKeyAES = br.readLine();
                                // 助记词
                                String mnemonicAES = br.readLine();
                                // 创建时间
                                String created = br.readLine();

                                Wallet wallet = new Wallet(walletName, pwdMD5, blockChainSymbol, walletAddress, privateKeyAES, mnemonicAES, DateUtil.commonDateParse(created, "yyyy-MM-dd HH:mm:ss"));
                                try {
                                    WalletDAO.insert(wallet);
                                } catch(SQLException e) {
    //                                LOG.log(Level.WARNING, "可能钱包名称冲突", e);
                                    // 可能钱包名称冲突
                                    wallet.setName(walletName + "." + System.currentTimeMillis());
                                    WalletDAO.insert(wallet);
                                }

                            }
                        }
                    } catch(Exception ex) {
                        return "无法迁入，gw文件损坏";
                    }


                    return null;
                }

                @Override
                protected void done() {
                    ph.finish();
                    
                    try {
                        String result = get();
                        if(StringUtils.isBlank(result)) {
                            // 迁入成功，通知用户重启应用，以便重新加载所有钱包
                            NotificationDisplayer.getDefault().notify(
                                    "迁入钱包已完成",
                                    ImageUtilities.loadImageIcon("resource/gourd32.png", false),
                                    "点击此处重新启动加载钱包",
                                    new RestartAction()
                            );
                        } else {
                            // 迁入失败
//                            NotifyDescriptor d = new NotifyDescriptor.Message(result);
//                            DialogDisplayer.getDefault().notify(d);
                            ExportFailDialog dlg = new ExportFailDialog(null, "迁入失败", result);
                            dlg.setVisible(true);
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    

                }
            };
            exportWorker.execute();
        }
    }
    
    private static final class RestartAction implements ActionListener {

        public RestartAction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LifecycleManager.getDefault().markForRestart();
            LifecycleManager.getDefault().exit();
        }
    }
}