package com.sergeykotov.operationmanagermvp.metrics;

public class ExecutorMetrics {
    private String name;
    private double cost;
    private double deviation;

    public ExecutorMetrics() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getDeviation() {
        return deviation;
    }

    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }
}