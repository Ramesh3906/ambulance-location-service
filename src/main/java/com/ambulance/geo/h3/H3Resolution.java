package com.ambulance.geo.h3;

final class H3Resolution {

    private static final double[] AVG_EDGE_KM = {
            1281.256011, 483.0568391, 182.5129565, 68.97922179,
            26.07175968, 9.854090990, 3.724532667, 1.406475763,
            0.531414010, 0.200786148, 0.075863783, 0.028663897,
            0.010830188, 0.004092010, 0.001546100, 0.000584169
    };

    private H3Resolution() {
    }

    static double averageEdgeKm(int resolution) {
        if (resolution < 0 || resolution >= AVG_EDGE_KM.length) {
            throw new IllegalArgumentException("Unsupported H3 resolution: " + resolution);
        }
        return AVG_EDGE_KM[resolution];
    }
}