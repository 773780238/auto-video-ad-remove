package cs576;

import cs576.sound.playWave.PlaySound;
import cs576.sound.playWave.PlayWaveException;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class addAdRGB {
    static int width = ImageDisplay.width;
    static int height = ImageDisplay.height;

    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    public static void writeRGB(String imgPath, String ad1RGBPath, int ad1Pos, String ad2RGBPath, int ad2Pos) {
        try {
            if (ad1Pos > ad2Pos) {
                ad2Pos = Utils.swap(ad1Pos, ad1Pos = ad2Pos);
                ad2RGBPath = Utils.swap(ad1RGBPath, ad1RGBPath = ad2RGBPath);
            }

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            int frameLength = width * height * 3;
            byte[] bytes = new byte[frameLength];

            FileOutputStream outputStream = new FileOutputStream("AdOut.rgb");
            int frame = 0;
            while (raf.read(bytes) != -1) {
                int ind = 0;
                frame++;
                outputStream.write(bytes);
                if (frame == ad1Pos) writeAd(outputStream, ad1RGBPath);
                if (frame == ad2Pos) writeAd(outputStream, ad2RGBPath);
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
            int frame = 0;
            while (raf.read(bytes) != -1) {
                outputStream.write(bytes);
            }
            System.out.println("write the ad output file finish");
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
        writeRGB(args[0],args[1],5260,args[2],2065);
    }
}

