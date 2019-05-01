package cs576;

import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import cs576.SIFTDetector;

public class DetectIcon {
    static int width = ImageDisplay.width;
    static int height = ImageDisplay.height;
    static BufferedImage imgOne;

    private String outputPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\LogoDetectResult\\";
    private String logo1Path = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\BrandImages\\ae_logo.bmp";
    private String logo2Path = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\BrandImages\\hrc_logo.bmp";
    private SIFTDetector detector;
    private String imgPath;

    private int dynamicInterval = 15;
    private int logoCountDown = 3;

    private ArrayList<Integer> ad1;
    private ArrayList<Integer> ad2;
    private ArrayList<ArrayList<Integer>> adSet = new ArrayList<ArrayList<Integer>>();
    public int[] adPos = new int[]{0, -1, -1};

    //Constructor
    public DetectIcon(String[] args) {
        imgPath = args[0];
        detector = new SIFTDetector(logo1Path, logo2Path);

        for (int i = 0; i < 3; i++) {
            adSet.add(new ArrayList<Integer>());
        }
    }

    public void readAndDetect() {
        try {
            System.out.println("Start detecting icons...");
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
                if (adPos[1] == -1 && detector.detectIcon(imgOne, 1)) {
                    adFlag = 1;
                } else if (adPos[2] == -1 && detector.detectIcon(imgOne, 2)) {
                    adFlag = 2;
                }
                //System.out.println("Frame: " + frame + "adFlag " + adFlag);
                //find the postion of the ad by detect a set of frame
                //only three continuous frames contains the same logo should be add in to corresponed list
                if ((prevFlag == adFlag) && (adFlag == 1 || adFlag == 2)) {
                    logoCountDown--;
                    dynamicInterval = 3;
                    //System.out.println(logoCountDown);
                    // detect the same logo in continuous 3 frame
                    if (logoCountDown == 0) {
                        //add frame to target ad list and do continuous frame analysis
                        adSet.get(adFlag).add(frame);
                        calculateAdPos(adFlag);
                        //write the detected frame
                        System.out.println("Frame: " + frame + " find icon " + adFlag);
//                        File outputfile = new File(outputPath + "image_" + frame + "_" + adFlag + ".jpg");
//                        ImageIO.write(imgOne, "jpg", outputfile);
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
            e.printStackTrace();
        }
    }

    //check if the detected frame is continuous
    public void calculateAdPos(int adNum) {
        if (adPos[adNum] == -1 && adSet.get(adNum).size() > 1) {
            int lastIdx = adSet.get(adNum).size() - 1;
            if (adSet.get(adNum).get(lastIdx) - adSet.get(adNum).get(lastIdx - 1) <= 30) {
                adPos[adNum] = adSet.get(adNum).get(lastIdx - 1);
            }
        }
    }

    public static void main(String[] args) {
        DetectIcon d = new DetectIcon(args);
        DetectIcon.imgOne = new BufferedImage(ImageDisplay.width, ImageDisplay.height, BufferedImage.TYPE_INT_RGB);
        d.readAndDetect();
        System.out.println("The first Ad at frame:" + d.adPos[1]);
        System.out.println("The second Ad at frame:" + d.adPos[2]);
    }
}

