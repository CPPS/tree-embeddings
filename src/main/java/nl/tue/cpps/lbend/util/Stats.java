package nl.tue.cpps.lbend.util;

import java.util.*;


public class Stats {

    private double sum, sumSqrd;
    private long count;

    private final String tag;

    public Stats() {
        this("");
    }

    public Stats(String tag) {
        this.tag = tag;
        reset();
    }

    public synchronized void addVal(double val) {
        sum += val;
        sumSqrd += val * val;
        count++;
    }

    public double mean() {
        return sum / count;
    }

    public double stdev() {
        return Math.sqrt(variance());
    }

    public double variance() {
        return (count * sumSqrd - sum * sum) / (count * count);
    }

    public void reset() {
        sum = 0;
        sumSqrd = 0;
        count = 0;
    }

    @Override
    public String toString() {
        return "[" + tag + "] stats: { count: " + count + ", mean: " + mean() + ", stdev: " + stdev() + " }";
    }
}
