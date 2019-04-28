package cs576;

public class Histogram implements Cloneable {
    public final double[] freq;   // freq[i] = # occurences of value i
    private int numPix;

    // Create a new histogram. 
    public Histogram(int numPix) {
        freq = new double[256];
        this.numPix = numPix;
    }

    // Add one occurrence of the value i. 
    public void addDataPoint(int i) {
        freq[i]++;
    }

    public double computeKL(Histogram Q) {
        double sum = 0;
        for (int i = 0; i < freq.length; i++) {
            sum = sum + (this.freq[i] / numPix) * (Math.log(Q.freq[i] / numPix + 0.001) / Math.log(this.freq[i] / numPix + 0.001));
        }
        return sum;
    }

    public void cleanData() {
        for (int i = 0; i < freq.length; i++) {
            freq[i] = 0;
        }
    }

    public void copyData(Histogram Q) {
        for (int i = 0; i < freq.length; i++) {
            Q.freq[i] = this.freq[i];
        }
    }
}