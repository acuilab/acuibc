package com.acuilab.bc.main.manager;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import java.io.File;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author acuilab.com
 */
public class PriceManager {
    private static PriceManager instance;
    
    private static ScheduledExecutorService service;
    private static final int REFRESH_INTERVAL = 60 * 1000;
    private static final int TERMINATE_TIMEOUT = 6 * 1000;
    
    private Browser browser;
    private File dataDir;
    
    // blockChainSymbol_coinSymbol, price
    private final Map<String, Double> map = Maps.newHashMap();
    
    public static PriceManager getDefault() {
        if (instance == null) {
            instance = new PriceManager();
        }
        return instance;
    }
    
    private PriceManager() {
    }
    
    public void start() {
        service = Executors.newScheduledThreadPool(1);
        
        dataDir = Files.createTempDir();
        browser = new Browser(BrowserType.LIGHTWEIGHT, new BrowserContext(new BrowserContextParams(dataDir.getAbsolutePath())));
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
//                if (event.isMainFrame()) {
//                    System.out.println("_____________________Main frame has finished loading_____________________");
//                    System.out.println("html-------------------------------------" + browser.getHTML());
////                    Document document = Jsoup.parse(browser.getHTML());
////                    
////                    Element bodyWrapperElm = document.getElementById("body-wrapper");
////                    System.out.println("bodyWrapperElm=============" + bodyWrapperElm.html());
////                    Elements elms = bodyWrapperElm.getElementsByClass("sc-hMrMfs hrdfMx css-vurnku");
////                    for(Element elm : elms) {
////                        Elements symbol = elm.getElementsByClass("sc-bfYoXt fisEvr");
////                        if(!symbol.isEmpty()) {
////                            System.out.println("symbol=================================" + symbol.get(0).text());
////                        }
////                        
////                        Elements value = elm.getElementsByClass("sc-jxGEyO gttRzd css-4cffwv");
////                        if(!value.isEmpty()) {
////                            System.out.println("value=================================" + value.get(0).text());
////                        }
////                    }
//                }
            }

            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent event) {
                System.out.println("onDocumentLoadedInMainFrame-------------------------------------------------------------");
            }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent event) {
                System.out.println("onDocumentLoadedInFrame-------------------------------------------------------------");
            }
            
            
        });
//        browser.loadURL("https://moonswap.fi/wallet");
        
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // 从https://moonswap.fi/wallet获得价格
                browser.reloadIgnoringCache();
            }
            
        }, REFRESH_INTERVAL, REFRESH_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    public void stop() throws InterruptedException {
        
        // 清空临时文件夹
        if(dataDir != null) {
            FileUtils.deleteQuietly(dataDir);
        }
        
        if(browser != null) {
            browser.dispose();
        }
        
        service.awaitTermination(TERMINATE_TIMEOUT, TimeUnit.MILLISECONDS);
    }
}
