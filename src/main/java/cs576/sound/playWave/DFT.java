package cs576.sound.playWave;

import java.util.*;

public class DFT {
    static List<Float> process(double results[], float sampleRate, int numSamples, int sigma) {
        double average = 0;
        double shreshold = 0.2;
        for (int i = 0; i < results.length; i++) {
            average += results[i];
        }
        average = average / results.length;

        double sums = 0;
        for (int i = 0; i < results.length; i++) {
            sums += (results[i] - average) * (results[i] - average);
        }

        double stdev = Math.sqrt(sums / (results.length - 1));

        ArrayList<Float> found = new ArrayList<Float>();
        double max = Integer.MIN_VALUE;
        int maxF = -1;
        for (int f = 0; f < results.length / 2; f++) {
            if (results[f] > shreshold/*average+sigma*stdev*/) {
                if (results[f] > max) {
                    max = results[f];
                    maxF = f;
                }
            } else {
                if (maxF != -1) {
                    found.add(maxF * sampleRate / numSamples);
                    max = Integer.MIN_VALUE;
                    maxF = -1;
                }
            }
        }

        return (found);
    }
}