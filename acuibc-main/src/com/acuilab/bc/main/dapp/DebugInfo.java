package com.acuilab.bc.main.dapp;

/**
 *
 * @author acuilab.com
 */
public class DebugInfo {
    private final String remoteDebuggingUrl;
    private final String title;

    public DebugInfo(String title, String remoteDebuggingUrl) {
        this.title = title;
        this.remoteDebuggingUrl = remoteDebuggingUrl;
    }

    public String getTitle() {
        return title;
    }
    
    public String getRemoteDebuggingUrl() {
        return remoteDebuggingUrl;
    }
}
