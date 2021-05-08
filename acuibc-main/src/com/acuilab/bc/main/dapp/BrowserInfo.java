package com.acuilab.bc.main.dapp;

import com.teamdev.jxbrowser.chromium.Browser;

/**
 *
 * @author acuilab.com
 */
public class BrowserInfo {
    private final Browser browser;
    private final String title;

    public BrowserInfo(String title, Browser browser) {
        this.title = title;
        this.browser = browser;
    }

    public String getTitle() {
        return title;
    }
    
    public Browser getBrowser() {
        return browser;
    }
}
