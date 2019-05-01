package cs576;
import java.util.ArrayList;

public class AppendAd {
    static String srVideoPath = "..\\Videos\\data_test3.rgb";
    static String srAudioPath = "..\\Videos\\data_test3.wav";
    static String ad1RGBPath = "..\\Ads\\ae_Ad_15s.rgb";
    static String ad1WavPath = "..\\Ads\\ae_Ad_15s.wav";
    static String ad2RGBPath = "..\\Ads\\hrc_Ad_15s.rgb";
    static String ad2WavPath = "..\\Ads\\hrc_Ad_15s.wav";
    static String RGBOutPath = "..\\Videos\\data_test3AD.rgb";
    static String WavOutPath = "..\\Videos\\data_test3AD.wav";


    public static void append2AdByPos(ArrayList<Integer> adsStart, ArrayList<Integer> adsEnd) {
        System.out.println("Adding ad1 at pos: " + adsStart.get(0));
        System.out.println("Adding ad2 at pos: " + adsStart.get(1));
        AppendAdRGB.writeRGB(srVideoPath, ad1RGBPath, adsStart, ad2RGBPath, adsEnd);
        AppendAdSound.writeSound(srAudioPath, ad1WavPath, adsStart, ad2WavPath, adsEnd);
    }
}
