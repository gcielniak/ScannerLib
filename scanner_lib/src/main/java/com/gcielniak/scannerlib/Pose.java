package com.gcielniak.scannerlib;

/**
 * Ground truth pose - used mainly by Tango
 *
 */
public class Pose {
    public double translation[];
    public double rotation[];

    public Pose() {
        translation = new double[3];
        rotation = new double[4];
    }
}
