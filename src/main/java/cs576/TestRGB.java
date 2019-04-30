package cs576;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class TestRGB {
    static GridBagLayout gLayout = new GridBagLayout();
    static GridBagConstraints c = new GridBagConstraints();
    static JLabel lbIm1 = new JLabel();
    static JFrame frame = new JFrame();
    static int width = 480;
    static int height = 270;
    static BufferedImage imgOne;

    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    private static void readImageRGB(int width, int height, String imgPath) {
        try {
            int frameLength = width * height * 3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            int frame = 0;
            while (raf.read(bytes) != -1) {
                int ind = 0;
                frame++;
                imgOne = Utils.bytes2Img(bytes);
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

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        TestRGB.frame.getContentPane().setLayout(gLayout);
        TestRGB.imgOne = new BufferedImage(ImageDisplay.width, ImageDisplay.height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(ImageDisplay.width, ImageDisplay.height, args[0]);
    }
}

