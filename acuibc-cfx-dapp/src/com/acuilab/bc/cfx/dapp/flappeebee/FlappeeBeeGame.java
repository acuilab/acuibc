package com.acuilab.bc.cfx.dapp.flappeebee;

import com.acuilab.bc.main.dapp.FatJarDApp;
import com.acuilab.bc.main.util.Constants;
import java.awt.Image;

/**
 *
 * @author admin
 */
public class FlappeeBeeGame extends FatJarDApp {

    @Override
    public void init() {
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getName() {
	return "Flappee Bee";
    }

    @Override
    public String getDesc() {
	return "";
    }

    @Override
    public Image getImage() {
	return null;
    }

    @Override
    public String getFatJarVersion() {
	return "1.0.0";
    }
    
    @Override
    public String getFatJarFileName() {
	return "flappeebee.jar";
    }
}
