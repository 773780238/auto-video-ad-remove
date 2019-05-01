package cs576;

import java.util.ArrayList;
import java.util.Arrays;

public class AppendAd {
    static String srVideoPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test3.rgb";
    static String srAudioPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test3.wav";
    static String ad1RGBPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\ae_Ad_15s.rgb";
    static String ad1WavPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\ae_Ad_15s.wav";
    static String ad2RGBPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\hrc_ad_15s.rgb";
    static String ad2WavPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\hrc_Ad_15s.wav";
    static String RGBOutPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test3AD.rgb";
    static String WavOutPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test3AD.wav";


    public static void append2AdByPos(ArrayList<Integer> adsStart, ArrayList<Integer> adsEnd) {
        System.out.println("Adding ad1 at pos: " + adsStart.get(0));
        System.out.println("Adding ad2 at pos: " + adsStart.get(1));
        AppendAdRGB.writeRGB(srVideoPath, ad1RGBPath, adsStart, ad2RGBPath, adsEnd);
        AppendAdSound.writeSound(srAudioPath, ad1WavPath, adsStart, ad2WavPath, adsEnd);
    }
    public static void main(String[] args){
        Integer [] a = {4492,8705};
        Integer [] b = {4916,9999};
        append2AdByPos(new ArrayList<Integer>(Arrays.asList(a)),new ArrayList<Integer>((Arrays.asList(b))));
    }
}
