package org.jpeek;

public enum Metrics {
    LCOM(true, null, null),
    CAMC(true, null, null),
    MMAC(true, 0.5d, 0.1d),
    LCOM5(true, 0.5d, -0.1d),
    LCOM4(true, 0.5d, -0.1d),
    NHD(false, null, null),
    LCOM2(true, null, null),
    LCOM3(true, null, null),
    SCOM(true, null, null),
    OCC(true, null, null),
    PCC(false, null, null),
    TCC(false, null, null),
    LCC(false, null, null),
    CCM(false, null, null),
    MWE(false, null, null);

    final boolean shouldIncludeParams;
    final Double mean;
    final Double sigma;

    Metrics(boolean shouldIncludeParams, Double mean, Double sigma) {
        this.shouldIncludeParams = shouldIncludeParams;
        this.mean = mean;
        this.sigma = sigma;
    }
}
