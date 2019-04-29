package cs576;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.*;
import org.opencv.highgui.*;

import java.io.*;
import javax.imageio.*;

public final class Utils {
    static int width = ImageDisplay.width;
    static int height = ImageDisplay.height;

    static int h = height;
    static int w = width;

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }


    public static Mat img2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        out = new Mat(height, width, CvType.CV_8UC3);
        data = new byte[height * width * (int) out.elemSize()];
        int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < dataBuff.length; i++) {
            data[i * 3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
            data[i * 3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
        }
        out.put(0, 0, data);
        return out;
    }

    public static BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        byte[] data = new byte[width * height * (int) in.elemSize()];
        int type;
        in.get(0, 0, data);
        out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        out.getRaster().setDataElements(0, 0, width, height, data);
        return out;
    }

    public static byte[] readImageBytes(String imgPath) {
        int frameLength = w * h * 3;
        long len = frameLength;
        byte[] bytes = new byte[(int) len];
        try {
            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            raf.read(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    static BufferedImage bytes2Img(byte[] bytes) {
        int ind = 0;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                byte a = 0;
                byte r = bytes[ind];
                byte g = bytes[ind + h * w];
                byte b = bytes[ind + h * w * 2];

                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                img.setRGB(x, y, pix);
                ind++;
            }
        }
        return img;
    }


    public static void main(String[] args){
        String testPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\sifttest\\trojan.rgb";
        byte[] bts = readImageBytes(testPath);
        BufferedImage img = bytes2Img(bts);
        Mat m = img2Mat(img);
        BufferedImage img2 = mat2Img(m);
        try{
            File outputfile1 = new File("image1.jpg");
            ImageIO.write(img, "jpg", outputfile1);
            Highgui.imwrite("imageMat.jpg", m);
            File outputfile2 = new File("image2.jpg");
            ImageIO.write(img2, "jpg", outputfile2);
        }catch(Exception e){

        }
        System.out.println("finish");
    }
}
