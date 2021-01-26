package com.acuilab.bc.main.dapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 将一个jar及其依赖的三方jar全部打到一个包中，这个包即为FatJar。
 * @author admin
 */
public abstract class FatJarDApp implements IDApp {

    @Override
    public void launch(String param) throws Exception {
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
	String fatJarDirPath = userDir + File.separator + "dapp";
	File fatJarDir = new File(fatJarDirPath);
	String fatJarFilePath = fatJarDirPath + File.separator + getId() + "-" + getFatJarVersion() + ".jar";
	if(!new File(fatJarFilePath).exists()) {
	    // 先删除之前的版本
	    File[] files = fatJarDir.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
		    // 1 只扫描当前目录，不包括子目录
		    // 2 getId()开头，jar结尾
		    return fatJarDir.equals(dir) && StringUtils.startsWith(name, getId()) && StringUtils.endsWith(name, ".jar");
		}
	    });
	    
	    // 程序退出后删除
	    for(File file : files) {
		file.deleteOnExit();
	    }
	    
	    // classpath根目录
	    InputStream is = this.getClass().getResourceAsStream("/" + getFatJarFileName());
	    IOUtils.copy(is, new FileOutputStream(fatJarFilePath));
	}

	try {
	    // 使用绑定jre执行
	    System.out.println("fatJarFilePath=" + fatJarFilePath);
	    Runtime.getRuntime().exec(bundledJre + " -jar " + fatJarFilePath + " " + param);
	} catch(IOException e) {
	    // 若发生异常，则使用系统默认jre执行(若再次发生异常，可能用户未安装jre，异常向上抛出)
	    Runtime.getRuntime().exec("java -jar " + fatJarFilePath + " " + param);
	}
    }

    /**
     * 获得fatjar的版本
     * @return 
     */
    public abstract String getFatJarVersion();
    
    /**
     * 获得fatjar的文件名
     * @return 
     */
    public abstract String getFatJarFileName();
    

    @Override
    public boolean isInternal() {
        return false;
    }
}
