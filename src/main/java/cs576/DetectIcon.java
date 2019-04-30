package cs576;


import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import cs576.SIFTDetector;

// todo: add specific color to detect: hsv
// todo: search in ROI

public class DetectIcon {
    static GridBagLayout gLayout = new GridBagLayout();
    static GridBagConstraints c = new GridBagConstraints();
    static JLabel lbIm1 = new JLabel();
    static JFrame frame = new JFrame();
    static int width = 480;
    static int height = 270;
    static BufferedImage imgOne;

    private String outputPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\LogoDetectResult\\";
    private SIFTDetector detector;
    private String imgPath;

    private int dynamicInterval = 15;
    private int logoCountDown = 3;

    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    private void readImageRGB() {
        try {
            int frameLength = width * height * 3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            byte[] bytes = new byte[(int) frameLength];

            int frame = 0;
            int prevFlag = -1; // whether previous frame has logos
            while (raf.read(bytes) != -1) {
                int ind = 0;
                frame++;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        byte r = bytes[ind];
                        byte g = bytes[ind + height * width];
                        byte b = bytes[ind + height * width * 2];
                        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                        //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                        imgOne.setRGB(x, y, pix);
                        ind++;
                    }
                }

                if (frame % dynamicInterval == 0) {
                    int adFlag = detector.detectIcon(imgOne);
                    if ((prevFlag == adFlag) && (adFlag == 1 || adFlag == 2)) {
                        logoCountDown--;
                        dynamicInterval = 5;
                        System.out.println(logoCountDown);
                        if (logoCountDown == 0) {
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
                showIms();
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        frame.pack();
        frame.setVisible(true);
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
    }

    public static void main(String[] args) {
        DetectIcon d = new DetectIcon(args);
        DetectIcon.frame.getContentPane().setLayout(gLayout);
        DetectIcon.imgOne = new BufferedImage(ImageDisplay.width, ImageDisplay.height, BufferedImage.TYPE_INT_RGB);
        d.readImageRGB();
    }
}

