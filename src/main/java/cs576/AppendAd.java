package cs576;

public class AppendAd {
    static String srVideoPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test1.rgb";
    static String srAudioPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test1.wav";
    static String ad1RGBPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\Starbucks_Ad_15s.rgb";
    static String ad1WavPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\Starbucks_Ad_15s.wav";
    static String ad2RGBPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\Subway_Ad_15s.rgb";
    static String ad2WavPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Ads\\Subway_Ad_15s.wav";

    static String RGBOutPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test1AD.rgb";
    static String WavOutPath = "C:\\Users\\zexin\\ideaProjects\\final-project\\resource\\Videos\\data_test1AD.wav";



    public static void append2AdByPos(int ad1Pos, int ad2Pos) {
        if (ad1Pos > ad2Pos) {
            ad2Pos = Utils.swap(ad1Pos, ad1Pos = ad2Pos);
            ad2RGBPath = Utils.swap(ad1RGBPath, ad1RGBPath = ad2RGBPath);
            ad2WavPath = Utils.swap(ad1WavPath, ad1WavPath = ad2WavPath);
        }

        System.out.println("Adding ad1 at pos: " + ad1Pos);
        System.out.println("Adding ad2 at pos: " + ad2Pos);
        AppendAdRGB.writeRGB(srVideoPath, ad1RGBPath, ad1Pos, ad2RGBPath, ad2Pos);
        AppendAdSound.writeSound(srAudioPath, ad1WavPath, ad1Pos, ad2WavPath, ad2Pos);
    }
}
