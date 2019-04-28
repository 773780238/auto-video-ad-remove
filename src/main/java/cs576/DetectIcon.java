package cs576;
import org.opencv.core.Core;

public class DetectIcon {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        System.out.println(Core.VERSION);
    }
}