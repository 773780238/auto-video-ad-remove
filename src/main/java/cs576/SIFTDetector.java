package cs576;

import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class SIFTDetector {
    static int width = ImageDisplay.width;
    static int height = ImageDisplay.height;
    static int counter = 0;

    private MatOfKeyPoint Icon1KeyPoints;
    private MatOfKeyPoint Icon1Descriptors;
    private MatOfKeyPoint Icon2KeyPoints;
    private MatOfKeyPoint Icon2Descriptors;

    private FeatureDetector featureDetector;
    private DescriptorExtractor descriptorExtractor;
    private DescriptorMatcher descriptorMatcher;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public SIFTDetector(String icon1, String icon2) {
        featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

        Mat iconImage1 = Highgui.imread(icon1, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat iconImage2 = Highgui.imread(icon2, Highgui.CV_LOAD_IMAGE_COLOR);

        Icon1KeyPoints = new MatOfKeyPoint();
        Icon1Descriptors = new MatOfKeyPoint();
        Icon2KeyPoints = new MatOfKeyPoint();
        Icon2Descriptors = new MatOfKeyPoint();
        getDescripter(iconImage1, Icon1KeyPoints, Icon1Descriptors);
        getDescripter(iconImage2, Icon2KeyPoints, Icon2Descriptors);
    }

    private void getDescripter(Mat m, MatOfKeyPoint objectKeyPoints, MatOfKeyPoint objectDescriptors) {
        featureDetector.detect(m, objectKeyPoints);
        descriptorExtractor.compute(m, objectKeyPoints, objectDescriptors);
    }

    public int detectIcon(BufferedImage img) {
        Mat m = Utils.img2Mat(img);
        if (detectIconImplementation(m, 1)) {
            return 1;
        } else if (detectIconImplementation(m, 2)) {
            return 2;
        } else {
            return -1;
        }
    }

    private boolean detectIconImplementation(Mat m, int choices) {
        MatOfKeyPoint senceKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint senceDescriptors = new MatOfKeyPoint();
        MatOfKeyPoint matchedDescriptor;

        getDescripter(m, senceKeyPoints, senceDescriptors);
        if (choices == 1) {
            matchedDescriptor = Icon1Descriptors;
        } else if (choices == 2) {
            matchedDescriptor = Icon2Descriptors;
        } else {
            return false;
        }

        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        descriptorMatcher.knnMatch(matchedDescriptor, senceDescriptors, matches, 2);

        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
        findGoodMatches(matches, goodMatchesList, 0.75f);

        return goodMatchesList.size() > 10;
    }

    private void findGoodMatches(List<MatOfDMatch> matches, LinkedList<DMatch> goodMatchesList, float nndrRatio) {
        //find the good match
        for(MatOfDMatch matofDMatch: matches){
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.addLast(m1);
            }
        }
    }
}