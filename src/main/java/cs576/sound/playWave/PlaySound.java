package cs576.sound.playWave;


import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


import cs576.Controller;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.*;


import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;

/**
 * <Replace this with a short description of the class.>
 *
 * @author Giulio
 */
public class PlaySound {
    public static int countdown = 10;
    public static int adsAudioComeIn = 0;
    private Object lock;
    private InputStream waveStream;
    private Controller controller;

    //private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    private final int EXTERNAL_BUFFER_SIZE = 3200;
    private final int buffersize = 4096;//accuracy of the output frequency
    private final int threshold = 20000;//threshold of frequency to detect ads

    /**
     * CONSTRUCTOR
     */
    public PlaySound(InputStream waveStream, Object lock, Controller controller) {
        this.waveStream = waveStream;
        this.lock = lock;
        this.controller = controller;
    }

    public void seek(InputStream input, int position)
            throws IOException {
        input.reset();
        input.skip(position);
    }

    public void play() throws PlayWaveException {

        AudioInputStream audioInputStream = null;

        try {
            //audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);

            //add buffer for mark/reset support, modified by Jian

            audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(this.waveStream));
            //System.out.println("total length: "+audioInputStream.available());;


        } catch (UnsupportedAudioFileException e1) {
            throw new PlayWaveException(e1);
        } catch (IOException e1) {
            throw new PlayWaveException(e1);
        }

        // Obtain the information about the AudioInputStream
        AudioFormat audioFormat = audioInputStream.getFormat();
        Info info = new Info(SourceDataLine.class, audioFormat);
        AudioFormat format = audioInputStream.getFormat();
        float rate = format.getFrameRate();
        int numChannels = format.getChannels();

        System.out.println(audioFormat.toString());
        // opens the audio channel
        SourceDataLine dataLine = null;

        try {
            dataLine = (SourceDataLine) AudioSystem.getLine(info);

            dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
        } catch (LineUnavailableException e1) {
            throw new PlayWaveException(e1);
        }

        // Starts the music :P

        dataLine.start();

        int readBytes = 0;
        byte[] bytesIn = new byte[this.EXTERNAL_BUFFER_SIZE];


        try {
            synchronized (lock) {
                while (readBytes != -1) {
                    //   if(controller.jumpButtonClicked){
                    //      seek(audioInputStream,50000);

                    //      controller.jumpButtonClicked = false;
                    //  }


                    readBytes = audioInputStream.read(bytesIn, 0,
                            bytesIn.length);
                    if (readBytes >= 0) {
                        dataLine.write(bytesIn, 0, readBytes);
                    }
                    ///liuzihui
                    ///liuzihui
                    double[] buffer = new double[buffersize];
                    int idx = 0;
                    for (int i = 0; i < bytesIn.length && idx < buffer.length; i += 2) {
                        byte blow = bytesIn[i];
                        byte bhigh = bytesIn[i + 1];

                        buffer[idx++] = (blow & 0xFF | bhigh << 8) / 32767.0F;
                        if (numChannels == 2) {
                            i += 2;
                        }
                    }

                    FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
                    Complex resultC[] = fft.transform(buffer, TransformType.FORWARD);

                    double[] results = new double[resultC.length];
                    for (int i = 0; i < resultC.length; i++) {
                        double real = resultC[i].getReal();
                        double imaginary = resultC[i].getImaginary();
                        results[i] = Math.sqrt(real * real + imaginary * imaginary);
                    }

                    List<Float> found = DFT.process(results, rate, resultC.length, 7);
                    double fd;
                    double sum = 0;
                    if (found.isEmpty()) {
                        fd = -1;
                    } else {
                        fd = Collections.max(found);


                    }

                    if (fd > threshold) {
                        adsAudioComeIn = countdown;
                    } else {

                    }
                    if (adsAudioComeIn != 0) {
                        adsAudioComeIn--;
                    }
                    ///liuzihui
                    ///liuzihui
                    lock.notify();
                    lock.wait();
                }
                lock.notify();//let the last frame or video exit
            }

        } catch (IOException e1) {
            throw new PlayWaveException(e1);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        } finally {
            // plays what's left and and closes the audioChannel
            dataLine.drain();
            dataLine.close();
        }

    }
}
