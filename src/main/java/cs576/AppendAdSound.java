package cs576;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.*;

public class AppendAdSound {
    //private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    static private final int EXTERNAL_BUFFER_SIZE = 3200;
    static String WavOutPath = AppendAd.WavOutPath;

    public static void writeSound(String wavPath, String ad1WavPath, ArrayList<Integer> adsStart, String ad2WavPath, ArrayList<Integer> adsEnd) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(wavPath));
            AudioFormat srcFormat = audioInputStream.getFormat();

            //allocate bytes and read wav samples
            byte[] bytesIn1 = new byte[EXTERNAL_BUFFER_SIZE * adsStart.get(0)];
            byte[] bytesIn2 = new byte[EXTERNAL_BUFFER_SIZE * (adsStart.get(1) - adsEnd.get(0))];
            byte[] bytesIn3 = new byte[(int) ((audioInputStream.getFrameLength() / 1600 - adsEnd.get(1)) * EXTERNAL_BUFFER_SIZE)];

            //read the audio in sequence, drop extra Wav frame
            audioInputStream.read(bytesIn1);
            audioInputStream.read(new byte[adsEnd.get(0) - adsStart.get(0)]);
            audioInputStream.read(bytesIn2);
            audioInputStream.read(new byte[adsEnd.get(1) - adsStart.get(1)]);
            audioInputStream.read(bytesIn3);

            //convert the bytes of samples to audioInput
            AudioInputStream audioSeg1 = new AudioInputStream(new ByteArrayInputStream(bytesIn1), srcFormat, bytesIn1.length);
            AudioInputStream audioSeg2 = new AudioInputStream(new ByteArrayInputStream(bytesIn2), srcFormat, bytesIn2.length);
            AudioInputStream audioSeg3 = new AudioInputStream(new ByteArrayInputStream(bytesIn3), srcFormat, bytesIn3.length);
            AudioInputStream adSeg1 = AudioSystem.getAudioInputStream(new File(ad1WavPath));
            AudioInputStream adSeg2 = AudioSystem.getAudioInputStream(new File(ad1WavPath));

            //join audio
            AudioInputStream in = audioAppender(audioSeg1, adSeg1);
            in = audioAppender(in, audioSeg2);
            in = audioAppender(in, adSeg2);
            in = audioAppender(in, audioSeg3);

            //write output file
            FileOutputStream outputStream = new FileOutputStream(WavOutPath);
            AudioSystem.write(in, AudioFileFormat.Type.WAVE, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public static AudioInputStream audioAppender(AudioInputStream clip1, AudioInputStream clip2) {
        AudioInputStream in = null;
        try {
            in =
                    new AudioInputStream(
                            new SequenceInputStream(clip1, clip2),
                            clip1.getFormat(),
                            clip1.getFrameLength() + clip2.getFrameLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            for (String s : args) {
                System.out.println(s);
            }
            System.err.println("usage: java soundSource ad1SoundPath ad2SoundPath");
            return;
        }
    }
}
