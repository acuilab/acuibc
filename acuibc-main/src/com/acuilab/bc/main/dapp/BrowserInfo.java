package com.acuilab.bc.main.dapp;

import com.teamdev.jxbrowser.chromium.Browser;

/**
 *
 * @author acuilab.com
 */
public class BrowserInfo {
    private final Browser browser;
    private final String title;
    private final String homeUrl;

    public BrowserInfo(Browser browser, String title, String homeUrl) {
        this.browser = browser;
        this.title = title;
        this.homeUrl = homeUrl;
    }
    
    public Browser getBrowser() {
        return browser;
    }

    public String getTitle() {
        return title;
    }
    
    public String getHomeUrl() {
        return homeUrl;
    }
}
