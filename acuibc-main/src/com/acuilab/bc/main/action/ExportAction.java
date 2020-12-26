package com.acuilab.bc.main.action;

import com.acuilab.bc.main.dao.WalletDAO;
import com.acuilab.bc.main.util.DateUtil;
import com.acuilab.bc.main.wallet.Wallet;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.windows.WindowManager;

/**
 *
 * @author admin
 */
public class ExportAction extends AbstractAction {
    
    public ExportAction() {
        putValue(NAME, "迁出钱包");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/moveout32.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
 
       // 首先判断钱包是否存在
        try {
            int count = WalletDAO.getCount();
            if(count == 0) {
                // 提示钱包不存在
                JOptionPane.showMessageDialog(null, "钱包不存在，请先创建或导入钱包");
                return;
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        // 选择文件
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File("gourd wallet.gw"));  // 设置默认文件名
        chooser.setFileFilter(new FileNameExtensionFilter("gw 文件 (*.gw)", "gw"));
        // FileChooser的图标取自传入的jFrame.通过更改JFrame的图标,您还应该在JFileChooser中获得反映的图标更改.
        int returnVal = chooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            
            if(file.exists()) {
                int overwriteSelected = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "文件" + file.getName() + "已存在，是否覆盖？", "替换文件", JOptionPane.YES_NO_OPTION);
                if(overwriteSelected == JOptionPane.YES_OPTION) {
                    // 覆盖时，删除当前文件，由zip4j创建一个新文件（若当前文件不是一个合法的zip文件，zip4j会抛异常）
                    file.delete();
                } else {
                    return;
                }
            }
            
            final ProgressHandle ph = ProgressHandle.createHandle("正在迁出钱包，请稍候");
            SwingWorker<Void, Void> exportWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ph.start();
                    List<Wallet> wallets = WalletDAO.getList();
                    
                    // @see https://blog.csdn.net/fragrant_no1/article/details/84870068
                    // ByteArrayOutputStream ByteArrayInputStream 不需要关闭流，即使关闭了，它们对应的方法还是可以使用，因为它们是内存读写流，不同于指向硬盘的流，
                    // 它内部是使用字节数组读/写内存的，这个字节数组是它的成员变量，当这个数组不再使用变成垃圾的时候，Java的垃圾回收机制会将它回收。所以不需要关流。
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))) {
                        // 写入sql和钱包数量
                        writer.write("insert into wallet (wname, pwdMd5, blockChainSymbol, waddress, privateKeyAES, mnemonicAES, created) values (?, ?, ?, ?, ?, ?, ?)");
                        writer.newLine();
                        writer.write("" + wallets.size());
                        writer.newLine();
                        for (Wallet wallet : wallets) {
                            // 钱包名称
                            writer.write(wallet.getName());
                            writer.newLine();
                            // 钱包密码
                            writer.write(wallet.getPwdMD5());
                            writer.newLine();
                            // 区块链简称
                            writer.write(wallet.getBlockChainSymbol());
                            writer.newLine();
                            // 钱包地址
                            writer.write(wallet.getAddress());
                            writer.newLine();
                            // 私钥
                            writer.write(wallet.getPrivateKeyAES());
                            writer.newLine();
                            // 助记词
                            writer.write(wallet.getMnemonicAES());
                            writer.newLine();
                            // 创建时间
                            writer.write(DateUtil.commonDateFormat(wallet.getCreated(), "yyyy-MM-dd HH:mm:ss"));
                            writer.newLine();
                        }
                        writer.flush();
                        byte[] bytes = outputStream.toByteArray();
                        ZipParameters zipParameters = new ZipParameters();
                        zipParameters.setFileNameInZip("wallet.txt");
                        new ZipFile(file).addStream(new ByteArrayInputStream(bytes), zipParameters);
                    }

                    return null;
                }
                
                @Override
                protected void done() {
                    ph.finish();
		    NotificationDisplayer.getDefault().notify(
			    "迁出钱包",
			    ImageUtilities.loadImageIcon("resource/gourd16.png", false),
			    "迁出钱包已完成",
			    null
		    );
                }
            };
            exportWorker.execute();
        }
    }
}