package com.acuilab.bc.main.dapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author admin
 */
public abstract class ExecutedJarDApp implements IDApp {

    @Override
    public void launch(String privateKey) throws Exception {
	// 1 ######################### 获得java路径 ##########################################
	// 1.1 绑定jre
	String userDir = System.getProperty("user.dir");
	System.out.println("userDir=" + userDir);
	
	String bundledJre = userDir + File.separator + "jre" + File.separator + "bin" + File.separator + "java";
	System.out.println("bundledJre=" + bundledJre);
	
//	Process p = Runtime.getRuntime().exec(bundledJre + " -version");
//	if(p.waitFor() == 0) {
//	    String output = IOUtils.toString(p.getInputStream(), "GBK");
//	    System.out.println("output=" + output);
//	}

	// 是否需要将可执行jar包重新拷贝到本地
	String executedJarDirPath = userDir + File.separator + "dapp";
	File executedJarDir = new File(executedJarDirPath);
	String executedJarFilePath = executedJarDirPath + File.separator + getId() + "-" + getExecutedJarVersion() + ".jar";
	if(!new File(executedJarFilePath).exists()) {
	    // 先删除之前的版本
	    File[] files = executedJarDir.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
		    // 1 只扫描当前目录，不包括子目录
		    // 2 getId()开头，jar结尾
		    return executedJarDir.equals(dir) && StringUtils.startsWith(name, getId()) && StringUtils.endsWith(name, ".jar");
		}
	    });
	    
	    // 程序退出后删除
	    for(File file : files) {
		file.deleteOnExit();
	    }
	    
	    // classpath根目录
	    InputStream is = this.getClass().getResourceAsStream("/" + getExecutedJarFileName());
	    System.out.println("is=" + is);
	    IOUtils.copy(is, new FileOutputStream(executedJarFilePath));
	}

	try {
	    // 使用绑定jre执行
	    System.out.println("executedJarFilePath=" + executedJarFilePath);
	    Runtime.getRuntime().exec(bundledJre + " -jar " + executedJarFilePath);
	} catch(IOException e) {
	    // 若发生异常，则使用系统默认jre执行(若再次发生异常，可能用户未安装jre，异常向上抛出)
	    Runtime.getRuntime().exec("java -jar " + executedJarFilePath);
	}
    }

    /**
     * 获得可执行jar包的版本
     * @return 
     */
    public abstract String getExecutedJarVersion();
    
    /**
     * 获得可执行jar包的文件名
     * @return 
     */
    public abstract String getExecutedJarFileName();
}
