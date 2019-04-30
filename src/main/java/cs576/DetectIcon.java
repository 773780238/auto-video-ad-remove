package cs576;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

import cs576.SIFTDetector;

// todo: add specific color to detect: hsv
// todo: search in ROI

public class DetectIcon {
    static int width = ImageDisplay.width;
    static int height = ImageDisplay.height;
    static BufferedImage imgOne;

    private String outputPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\LogoDetectResult\\";
    private SIFTDetector detector;
    private String imgPath;

    private int dynamicInterval = 15;
    private int logoCountDown = 3;

    private ArrayList<Integer> ad1;
    private ArrayList<Integer> ad2;
    int ad1Pos = -1;
    int ad2Pos = -1;

    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */

    private void readImageRGB() {
        try {
            long frameLength = width * height * 3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            byte[] bytes = new byte[(int) frameLength];

            int frame = 0;
            int prevFlag = -1; // whether previous frame has logos
            while (raf.read(bytes) != -1) {
                int ind = 0;
                frame += dynamicInterval;
                raf.seek(frame * frameLength);
                //read frame
                imgOne = Utils.bytes2Img(bytes);

                // detect the icon in frame
                int adFlag = -1;
                if (ad1Pos == -1 && detector.detectIcon(imgOne, 1)) {
                    adFlag = 1;
                } else if (ad2Pos == -1 && detector.detectIcon(imgOne, 2)) {
                    adFlag = 2;
                }
                //System.out.println("Frame: " + frame);

                //find the postion of the ad by detect a set of frame
                if ((prevFlag == adFlag) && (adFlag == 1 || adFlag == 2)) {
                    logoCountDown--;
                    dynamicInterval = 5;
                    System.out.println(logoCountDown);
                    if (logoCountDown == 0) {
                        if (adFlag == 1) {
                            ad1.add(frame);
                            calculateAd1Pos();
                        } else {
                            ad2.add(frame);
                            calculateAd2Pos();
                        }
                        System.out.println("Frame: " + frame + " find icon " + adFlag);
                        File outputfile = new File(outputPath + "image_" + frame + "_" + adFlag + ".jpg");
                        ImageIO.write(imgOne, "jpg", outputfile);
                        logoCountDown = 3;
                    }
                } else {
                    logoCountDown = logoCountDown < 3 ? logoCountDown++ : 3;
                    dynamicInterval = 15;
                }
                prevFlag = adFlag;
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    public DetectIcon(String[] args) {
        if (args.length != 3) {
            for (String s : args) {
                System.out.println(s);
            }
            System.err.println("usage: java video.rgb icon1.path, icon2.path");
            return;
        }
        imgPath = args[0];
        detector = new SIFTDetector(args[1], args[2]);
        ad1 = new ArrayList<>();
        ad2 = new ArrayList<>();
    }


    public void calculateAd1Pos() {
        if (ad1Pos == -1 && ad1.size() > 1) {
            int lastIdx = ad1.size() - 1;
            if (ad1.get(lastIdx) - ad1.get(lastIdx - 1) <= 15) {
                ad1Pos = ad1.get(lastIdx - 1);
            }
        }
    }

    public void calculateAd2Pos() {
        if (ad2Pos == -1 && ad2.size() > 1) {
            int lastIdx = ad2.size() - 1;
            if (ad2.get(lastIdx) - ad2.get(lastIdx - 1) <= 15) {
                ad2Pos = ad2.get(lastIdx - 1);
            }
        }
    }

    public static void main(String[] args) {
        DetectIcon d = new DetectIcon(args);
        DetectIcon.imgOne = new BufferedImage(ImageDisplay.width, ImageDisplay.height, BufferedImage.TYPE_INT_RGB);
        d.readImageRGB();
        System.out.println("The first Ad at frame:" + d.ad1Pos);
        System.out.println("The second Ad at frame:" + d.ad2Pos);
    }
}

