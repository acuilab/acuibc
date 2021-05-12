package com.acuilab.bc.main.util;

/**
 *
 * @author acuilab.com
 */
public class GasRelated {
    private int gasMin;
    private int gasMax;
    private int gasDefault;

    public GasRelated(int gasMin, int gasMax, int gasDefault) {
        this.gasMin = gasMin;
        this.gasMax = gasMax;
        this.gasDefault = gasDefault;
    }
    
    public int getGasMin() {
        return gasMin;
    }

    public void setGasMin(int gasMin) {
        this.gasMin = gasMin;
    }

    public int getGasMax() {
        return gasMax;
    }

    public void setGasMax(int gasMax) {
        this.gasMax = gasMax;
    }

    public int getGasDefault() {
        return gasDefault;
    }

    public void setGasDefault(int gasDefault) {
        this.gasDefault = gasDefault;
    }
}
