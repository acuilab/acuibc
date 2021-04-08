package com.acuilab.bc.main.dappbrowser;

/**
 *
 * @author admin
 */
public class JavaObject {

    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String sayHelloTo(String firstName) {
	return "Hello " + firstName + "!";
    }
}
