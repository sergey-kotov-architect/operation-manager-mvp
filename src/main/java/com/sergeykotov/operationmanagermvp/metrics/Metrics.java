package com.sergeykotov.operationmanagermvp.metrics;

import java.util.List;

public class Metrics {
    private int opCount;
    private int taskCount;
    private int executorCount;
    private int periodCount;
    private int groupCount;

    private long start;
    private long end;

    private double minCost;
    private String minCostExecutor;

    private double meanCost;

    private double maxCost;
    private String maxCostExecutor;

    private double minDeviation;
    private String minDeviationExecutor;

    private double meanDeviation;

    private double maxDeviation;
    private String maxDeviationExecutor;

    private List<ExecutorMetrics> executorMetrics;

    public Metrics() {
    }

    public int getOpCount() {
        return opCount;
    }

    public void setOpCount(int opCount) {
        this.opCount = opCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getExecutorCount() {
        return executorCount;
    }

    public void setExecutorCount(int executorCount) {
        this.executorCount = executorCount;
    }

    public int getPeriodCount() {
        return periodCount;
    }

    public void setPeriodCount(int periodCount) {
        this.periodCount = periodCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public String getMinCostExecutor() {
        return minCostExecutor;
    }

    public void setMinCostExecutor(String minCostExecutor) {
        this.minCostExecutor = minCostExecutor;
    }

    public double getMeanCost() {
        return meanCost;
    }

    public void setMeanCost(double meanCost) {
        this.meanCost = meanCost;
    }

    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public String getMaxCostExecutor() {
        return maxCostExecutor;
    }

    public void setMaxCostExecutor(String maxCostExecutor) {
        this.maxCostExecutor = maxCostExecutor;
    }

    public double getMinDeviation() {
        return minDeviation;
    }

    public void setMinDeviation(double minDeviation) {
        this.minDeviation = minDeviation;
    }

    public String getMinDeviationExecutor() {
        return minDeviationExecutor;
    }

    public void setMinDeviationExecutor(String minDeviationExecutor) {
        this.minDeviationExecutor = minDeviationExecutor;
    }

    public double getMeanDeviation() {
        return meanDeviation;
    }

    public void setMeanDeviation(double meanDeviation) {
        this.meanDeviation = meanDeviation;
    }

    public double getMaxDeviation() {
        return maxDeviation;
    }

    public void setMaxDeviation(double maxDeviation) {
        this.maxDeviation = maxDeviation;
    }

    public String getMaxDeviationExecutor() {
        return maxDeviationExecutor;
    }

    public void setMaxDeviationExecutor(String maxDeviationExecutor) {
        this.maxDeviationExecutor = maxDeviationExecutor;
    }

    public List<ExecutorMetrics> getExecutorMetrics() {
        return executorMetrics;
    }

    public void setExecutorMetrics(List<ExecutorMetrics> executorMetrics) {
        this.executorMetrics = executorMetrics;
    }
}