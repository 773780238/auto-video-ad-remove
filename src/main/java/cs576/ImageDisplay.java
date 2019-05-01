package cs576;

import cs576.sound.playWave.PlaySound;
import cs576.sound.playWave.PlayWaveException;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {
    private static int countdown = PlaySound.countdown;
    public static int adsVideoComeIn = countdown;
    private static boolean adsHasCame = false;
    private static double shreshold = 1.05;
    static private Object lock = new Object();
    static GridBagLayout gLayout = new GridBagLayout();
    static GridBagConstraints c = new GridBagConstraints();
    static JLabel lbIm1 = new JLabel();
    static JFrame frame = new JFrame();
    static int width = 480;
    static int height = 270;
    static Histogram Q;
    static Histogram P = new Histogram(width * height);
    static BufferedImage imgOne;

    private static String targetRGB = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test3AD.rgb";
    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    private static void readImageRGB(int width, int height, String imgPath, BufferedImage img) {
        try {
            int frameLength = width * height * 3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            synchronized (lock) {
                int frame = 0;
                while (raf.read(bytes) != -1) {
                    int ind = 0;
                    frame++;

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            byte r = bytes[ind];
                            byte g = bytes[ind + height * width];
                            byte b = bytes[ind + height * width * 2];
                            int yv = (int) ((int) (r & 0xff) * 0.3 + (int) (g & 0xff) * 0.59 + (int) (b & 0xff) * 0.11);
                            P.addDataPoint(yv);
                            int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                            //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                            imgOne.setRGB(x, y, pix);
                            ind++;
                        }
                    }

                    if (Q == null) {
                        Q = new Histogram(width * height);
                        P.copyData(Q);
                        P.cleanData();
                    } else {

                        double kl = P.computeKL(Q);
                        P.copyData(Q);
                        P.cleanData();

                        if ((adsVideoComeIn > 0 && PlaySound.adsAudioComeIn > 0) || kl > shreshold) {
                            if (kl > shreshold) adsVideoComeIn = countdown;
                            //if(adsVideoComeIn > 0 && PlaySound.adsAudioComeIn > 0)adsVideoComeIn = 0;

                            System.out.print("frame " + frame + "  scene changed");
                            if (PlaySound.adsAudioComeIn > 0) {
                                System.out.println(" ads");
                                adsHasCame = true;

                            } else {
                                System.out.println("  not ads");
                            }
                            kl = 0;

                        } else if (adsHasCame && PlaySound.adsAudioComeIn == 0 && adsVideoComeIn == 0) {
                            System.out.print("frame " + frame + "  scene changed  ads over");
                            adsHasCame = false;
                        }
                        if (adsVideoComeIn != 0) {
                            adsVideoComeIn--;
                        }
                    }
                    showIms();
                    lock.notify();
                    lock.wait();
                }
                lock.notify();
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void showIms() {
        // Use label to display the image
        lbIm1.setIcon(new ImageIcon(imgOne));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //audioThread start
        WaveThread waveThread = new WaveThread(args, lock);
        waveThread.start();

        ImageDisplay.frame.getContentPane().setLayout(gLayout);
        ImageDisplay.imgOne = new BufferedImage(ImageDisplay.width, ImageDisplay.height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(ImageDisplay.width, ImageDisplay.height, targetRGB, ImageDisplay.imgOne);
    }
}

class WaveThread extends Thread {

    private Thread t;
    private Object lock;
    private String args[];
    private static String targetWav = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test3AD.wav";

    WaveThread(String args[], Object lock) {
        this.args = args;
        this.lock = lock;
    }

    public void run() {
        // get the command line parameters
        if (args.length < 2) {
            System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
            return;
        }
        String filename = targetWav;
        // opens the inputStream
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        // initializes the playSound Object
        PlaySound playSound = new PlaySound(inputStream, lock);
        // plays the sound
        try {
            playSound.play();
        } catch (PlayWaveException e) {
            e.printStackTrace();
            return;
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}

