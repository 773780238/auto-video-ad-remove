package cs576;

import cs576.sound.playWave.PlaySound;
import cs576.sound.playWave.PlayWaveException;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;


public class AppendAdRGB {
    static int width = ImageDisplay.width;
    static int height = ImageDisplay.height;
    //    static int frameNum = ImageDisplay.frameNum;
    static int frameNum = ImageDisplay.frameNum;
    static String RGBOutPath = AppendAd.RGBOutPath;

    public static void writeRGB(String imgPath, String ad1RGBPath, ArrayList<Integer> adsStart, String ad2RGBPath, ArrayList<Integer> adsEnd) {
        try {
            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            int frameLength = width * height * 3;
            byte[] bytes = new byte[frameLength];

            FileOutputStream outputStream = new FileOutputStream(RGBOutPath);
            int frame = 0;
            while (raf.read(bytes) != -1 && frame < frameNum) {
                outputStream.write(bytes);
                if (adsStart.get(0) <= frame && frame <= adsEnd.get(0)) {
                    while (raf.read(bytes) != -1) {
                        frame++;
                        if (frame > adsEnd.get(0)) break;
                    }
                    writeAd(outputStream, ad1RGBPath);
                    System.out.println("remove and write the ad1 to file finish");
                }

                if (adsStart.get(1) <= frame && frame <= adsEnd.get(1)) {
                    while (raf.read(bytes) != -1) {
                        frame++;
                        if (frame > adsEnd.get(1) || frame > frameNum) break;
                    }
                    writeAd(outputStream, ad2RGBPath);
                    System.out.println("remove and write the ad2 to file finish");
                }
                frame++;
            }
            raf.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeAd(FileOutputStream outputStream, String adRGBPath) {
        File file = new File(adRGBPath);
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            int frameLength = width * height * 3;
            byte[] bytes = new byte[frameLength];
            while (raf.read(bytes) != -1) {
                outputStream.write(bytes);
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            for (String s : args) {
                System.out.println(s);
            }
            System.err.println("usage: java imgSource ad1RGBPath ad2RGBPath");
            return;
        }
    }
}

