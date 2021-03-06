package cs576;

import cs576.sound.playWave.PlaySound;
import cs576.sound.playWave.PlayWaveException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

//$ProjectFileDir$\resource\Videos\data_test3.rgb $ProjectFileDir$\resource\Videos\data_test3.wav 13000

public class ImageDisplay extends Application implements Runnable {
    private static String[] args;
    private static FXMLLoader loader;
    private static Controller controller;
    private static int countdown = PlaySound.countdown;
    public static int adsVideoComeIn = countdown;
    private static boolean adsHasCame = false;
    private static double shreshold = 1.05;
    static private Object lock = new Object();
    static int width = 480;
    static int height = 270;
    static Histogram Q;
    static Histogram P = new Histogram(width * height);
    static BufferedImage imgOne;
    static boolean[] arrayIsAds;
    static ArrayList<Integer> adsStart = new ArrayList<>();
    static ArrayList<Integer> adsEnd = new ArrayList<>();
    static boolean isDetect = false;
    static int frameNum;

    public static void getAdsLocation() {
        int adsMinimumLen = 200;
        for (int i = 0; i < arrayIsAds.length; i++) {
            if (arrayIsAds[i]) {
                adsStart.add(i);
                int adsLen = 0;
                int start = i;
                for (; i < arrayIsAds.length; i++) {
                    adsLen++;
                    if (arrayIsAds[i] == false) {
                        if (adsLen > adsMinimumLen) {
                            adsEnd.add(i);
                            break;
                        } else {
                            adsStart.remove(adsStart.size() - 1);
                            break;
                        }

                    }
                }
            }
        }

        //expand the range of Ads
        if (adsStart.size() != adsEnd.size()) {
            System.err.println("adsStart size does not match to adsEnd size");
        }
        for (int i = 0; i < adsStart.size(); i++) {
            int temp = adsStart.get(i) - 10 > 0 ? adsStart.get(i) - 10 : 0;
            adsStart.set(i, temp);

            temp = adsEnd.get(i) + 10 > frameNum ? frameNum : adsEnd.get(i) + 10;
            adsEnd.set(i, temp);
        }
    }

    public static void processArrayIsAds() {

        boolean[] consecutiveArray = new boolean[arrayIsAds.length];
        int windowSize = 200;
        int consecutive = 4;
        for (int i = 0; i < arrayIsAds.length; i++) {
            int k = i;
            for (; k < arrayIsAds.length && k < i + consecutive; k++) {
                if (!arrayIsAds[k]) {
                    break;
                }
            }
            if (k == i + consecutive) {
                for (int q = i; q <= k; q++) consecutiveArray[q] = true;
            }
        }
        ////
        for (int i = 0; i < consecutiveArray.length; i++) {
            if (consecutiveArray[i]) {
                int j = i + windowSize;
                if (j >= consecutiveArray.length) {
                    j = consecutiveArray.length - 1;
                }
                for (; j > i; j--) {
                    if (consecutiveArray[j]) {
                        for (int q = i; q <= j; q++) consecutiveArray[q] = true;
                        i = j - 1;
                        break;
                    }
                }
                if (i >= consecutiveArray.length - 2) break;
            }

        }
        arrayIsAds = consecutiveArray;
    }

    public static int countFrameNum(String imgPath) {
        int frameNum = 0;
        File file = new File(imgPath);
        RandomAccessFile raf = null;
        long len = width * height * 3;
        ;
        byte[] bytes = new byte[(int) len];
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            while (raf.read(bytes) != -1) {
                frameNum++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frameNum;
    }

    public static void detectAdsDisplay(int frame) {
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
                    arrayIsAds[frame] = true;
                    adsHasCame = true;

                } else {
                    System.out.println("  not ads");
                }
                kl = 0;

            } else if (adsHasCame && PlaySound.adsAudioComeIn == 0 && adsVideoComeIn == 0) {
                System.out.println("frame " + frame + "  scene changed  ads over");
                adsHasCame = false;
            }
            if (adsVideoComeIn != 0) {
                adsVideoComeIn--;
            }
        }
        showIms();

    }

    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    public static void readImageRGB(int width, int height, String imgPath, BufferedImage img) {
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
                  /*  if(controller.jumpButtonClicked){
                        raf.seek(Integer.parseInt(controller.frameTextField.getText())*len);
                        frame = Integer.parseInt(controller.frameTextField.getText());
                    }*/
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
                    if (isDetect) {
                        detectAdsDisplay(frame);
                    } else {
                        showIms();
                    }

                    lock.notify();
                    lock.wait(1000); //todo: warning unknown exception


                }
                if (isDetect) {
                    processArrayIsAds();
                    getAdsLocation();
                    System.out.println("::: result :::");
                    for (int i = 0; i < adsStart.size(); i++)
                        System.out.println("adsStart: " + adsStart.get(i));

                    for (int i = 0; i < adsEnd.size(); i++)
                        System.out.println("adsEnd: " + adsEnd.get(i));
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
        WritableImage wr = new WritableImage(imgOne.getWidth(), imgOne.getHeight());
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < imgOne.getWidth(); x++) {
            for (int y = 0; y < imgOne.getHeight(); y++) {
                pw.setArgb(x, y, imgOne.getRGB(x, y));
            }
        }

        controller.ivFX.setImage(wr);
        while (!controller.isTextPlay) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void process(boolean isDetect, String[] args) {
        ImageDisplay.isDetect = isDetect;
        frameNum = countFrameNum(args[0]);
        arrayIsAds = new boolean[frameNum];
        ImageDisplay.args = args;
        if (args.length < 3) {
            System.err.println("parameters: .rgb .wav waveThreshold");
        }
        int waveThreshold = Integer.parseInt(args[2]);

        //audioThread start
        WaveThread waveThread = new WaveThread(args, lock, controller, waveThreshold);
        waveThread.start();
        //videoThread start
        VideoThread videoThread = new VideoThread(args, lock);
        videoThread.start();

        try {
            videoThread.join();
            waveThread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //FXThread start
        Thread myThread = new Thread(new ImageDisplay());
        myThread.start();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        process(true, args);
        args[0] = AppendAd.RGBOutPath;
        args[1] = AppendAd.WavOutPath;
        process(false, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        loader = new FXMLLoader(getClass().getResource("ImageDisplay.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setScene(new Scene(root));

        primaryStage.show();
    }


    @Override
    public void run() {
        launch(args);
    }
}

class WaveThread extends Thread {
    private Thread t;
    private Object lock;
    private String args[];
    private Controller controller;
    private int waveThreshold;

    WaveThread(String args[], Object lock, Controller controller, int waveThreshold) {
        this.args = args;
        this.lock = lock;
        this.controller = controller;
        this.waveThreshold = waveThreshold;
    }

    public void run() {

        // get the command line parameters
        if (args.length < 2) {
            System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
            return;
        } else {
            System.out.println("the input path");
            System.out.println(args[0]);
            System.out.println(args[1]);
        }
        String filename = args[1];

        // opens the inputStream
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(filename);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // initializes the playSound Object
        PlaySound playSound = new PlaySound(inputStream, lock, controller, waveThreshold);

        // plays the sound
        try {
            playSound.play();
        } catch (PlayWaveException e) {
            e.printStackTrace();
            return;
        }


        System.out.println("waveThread killed");
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}

class VideoThread extends Thread {
    private Thread t;
    private Object lock;
    private String args[];

    VideoThread(String args[], Object lock) {
        this.args = args;
        this.lock = lock;
    }

    public void run() {
        if (args.length < 1) {
            System.out.println("args should not be null");
        }
        ImageDisplay.imgOne = new BufferedImage(ImageDisplay.width, ImageDisplay.height, BufferedImage.TYPE_INT_RGB);
        ImageDisplay.readImageRGB(ImageDisplay.width, ImageDisplay.height, args[0], ImageDisplay.imgOne);

        if(ImageDisplay.isDetect){
            //iconDetectThread
            IconDetectThread iconDetectThread = new IconDetectThread(args, lock);
            iconDetectThread.start();
            AppendAd.append2AdByPos(ImageDisplay.adsStart, ImageDisplay.adsEnd);

            try {
                iconDetectThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        System.out.println("videoThread killed");
    }
}

class IconDetectThread extends Thread {
    private Thread t;
    private Object lock;
    private String args[];

    public IconDetectThread(String args[], Object lock) {
        this.args = args;
        this.lock = lock;
    }

    public void run() {
        DetectIcon iconDetector = new DetectIcon(args);
        DetectIcon.imgOne = new BufferedImage(ImageDisplay.width, ImageDisplay.height, BufferedImage.TYPE_INT_RGB);
        iconDetector.readAndDetect();
        System.out.println("The first Ad at frame:" + iconDetector.adPos[1]);
        System.out.println("The second Ad at frame:" + iconDetector.adPos[2]);
        System.out.println("iconDetectThread killed");
    }
}