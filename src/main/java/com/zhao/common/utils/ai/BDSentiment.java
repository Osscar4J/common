package com.zhao.common.utils.ai;

/**
 * 情感分析结果
 * @Author: zhaolianqi
 * @Date: 2021/7/13 14:01
 * @Version: v1.0
 */
public class BDSentiment {

    // {"positive_prob":0.145559,"sentiment":0,"confidence":0.676536,"negative_prob":0.854441}
    private double negativeProb;
    private double positiveProb;
    private double confidence;

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getNegativeProb() {
        return negativeProb;
    }

    public void setNegativeProb(double negativeProb) {
        this.negativeProb = negativeProb;
    }

    public double getPositiveProb() {
        return positiveProb;
    }

    public void setPositiveProb(double positiveProb) {
        this.positiveProb = positiveProb;
    }
}
