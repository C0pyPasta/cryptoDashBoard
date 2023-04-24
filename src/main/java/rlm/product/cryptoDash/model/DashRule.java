package rlm.product.cryptoDash.model;

import jakarta.persistence.*;

@Entity
public class DashRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cryptoName;
    private double totalAmount;
    private double totalBuyPrice;
    private double totalValueIn;
    private double totalValueOut;
    private double dollarCostAverage;
    private double sessionLow;
    private double sessionHigh;
    private double currentUpdatedValue;
    private double resultInPercentage;
    private double totalOut;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalBuyPrice() {
        return totalBuyPrice;
    }

    public void setTotalBuyPrice(double totalBuyPrice) {
        this.totalBuyPrice = totalBuyPrice;
    }

    public double getTotalValueIn() {
        return totalValueIn;
    }

    public void setTotalValueIn(double totalValueIn) {
        this.totalValueIn = totalValueIn;
    }

    public double getTotalValueOut() {
        return totalValueOut;
    }

    public void setTotalValueOut(double totalValueOut) {
        this.totalValueOut = totalValueOut;
    }

    public double getDollarCostAverage() {
        return dollarCostAverage;
    }

    public void setDollarCostAverage(double dollarCostAverage) {
        this.dollarCostAverage = dollarCostAverage;
    }

    public double getSessionLow() {
        return sessionLow;
    }

    public void setSessionLow(double sessionLow) {
        this.sessionLow = sessionLow;
    }

    public double getSessionHigh() {
        return sessionHigh;
    }

    public void setSessionHigh(double sessionHigh) {
        this.sessionHigh = sessionHigh;
    }

    public double getCurrentUpdatedValue() {
        return currentUpdatedValue;
    }

    public void setCurrentUpdatedValue(double currentUpdatedValue) {
        this.currentUpdatedValue = currentUpdatedValue;
    }

    public double getResultInPercentage() {
        return resultInPercentage;
    }

    public void setResultInPercentage(double resultInPercentage) {
        this.resultInPercentage = resultInPercentage;
    }

    public double getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(double totalOut) {
        this.totalOut = totalOut;
    }
}