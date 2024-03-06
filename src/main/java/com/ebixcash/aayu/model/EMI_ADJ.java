package com.ebixcash.aayu.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EMI_ADJ {

    @JsonProperty(value = "THRESH")
    private double ThreshValue = 0.0d;
    @JsonProperty(value = "EMI_OP")
    private int EmiOPValue = 0;
    @JsonProperty(value = "ONBASIS")
    private int OnbasisValue = 0;
    @JsonProperty(value = "ADJ")
    private int Adj = 0;
    
    public int getAdj() {
        return Adj;
    }

    public void setAdj(int adj) {
        Adj = adj;
    }

    /* set get functions for the creation of new row for last installment */
    public int getEmiOPValue() {
        return EmiOPValue;
    }

    public void setEmiOPValue(int EmiOPValue) {
        this.EmiOPValue = EmiOPValue;
    }

    public int getOnbasisValue() {
        return OnbasisValue;
    }

    public void setOnbasisValue(int OnbasisValue) {
        this.OnbasisValue = OnbasisValue;
    }

    public double getThreshValue() {
        return ThreshValue;
    }

    public void setThreshValue(double ThreshValue) {
        this.ThreshValue = ThreshValue;
    }
}