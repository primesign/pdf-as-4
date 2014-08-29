package at.gv.egiz.param_tests.testinfo;

import java.awt.Rectangle;
import java.util.List;

/**
 * Test information class for signature position test.
 * 
 * @author mtappler
 *
 */
public class SignaturePositionTestInfo extends TestInfo {
    /**
     * Class containing attributes for non-standard parameters of signature
     * position tests.
     * 
     * @author mtappler
     *
     */
    public static class SignaturePositionParameters {
        /**
         * positioning string which specifies the position of the signature
         * block
         */
        private String positionString;
        /**
         * The page number of the page, which shows the signature block.
         */
        private int sigPageNumber;
        /**
         * A list of rectangular areas, which will be ignored for image
         * comparison
         */
        private List<Rectangle> ignoredAreas;
        /**
         * the file name of the reference file for image comparison
         */
        private String refImageFileName;
        /**
         * if set to true, a reference image is captured during the test, but no
         * actual comparison will be performed
         */
        private boolean captureReferenceImage;

        public String getPositionString() {
            return positionString;
        }

        public void setPositionString(String positionString) {
            this.positionString = positionString;
        }

        public int getSigPageNumber() {
            return sigPageNumber;
        }

        public void setSigPageNumber(int sigPageNumber) {
            this.sigPageNumber = sigPageNumber;
        }

        public List<Rectangle> getIgnoredAreas() {
            return ignoredAreas;
        }

        public void setIgnoredAreas(List<Rectangle> ignoredAreas) {
            this.ignoredAreas = ignoredAreas;
        }

        public String getRefImageFileName() {
            return refImageFileName;
        }

        public void setRefImageFileName(String refImageFileName) {
            this.refImageFileName = refImageFileName;
        }

        public boolean isCaptureReferenceImage() {
            return captureReferenceImage;
        }

        public void setCaptureReferenceImage(boolean captureReferenceImage) {
            this.captureReferenceImage = captureReferenceImage;
        }
    }

    /**
     * additional/non-standard parameter of signature position tests
     */
    private SignaturePositionParameters additionParameters = new SignaturePositionParameters();

    /**
     * file name of the reference image with ignored areas
     */
    private String refImageIgnored;

    /**
     * file name of the signature page image with ignored areas
     */
    private String sigPageImageIgnored;

    /**
     * file name of difference image
     */
    private String diffImage;

    public String getRefImageIgnored() {
        return refImageIgnored;
    }

    public String getSigPageImageIgnored() {
        return sigPageImageIgnored;
    }

    public String getDiffImage() {
        return diffImage;
    }

    public SignaturePositionParameters getAdditionParameters() {
        return additionParameters;
    }

    public void setAdditionParameters(
            SignaturePositionParameters additionParameters) {
        this.additionParameters = additionParameters;
    }

    public void setRefImageIgnored(String refImageIgnored) {
        this.refImageIgnored = refImageIgnored;
    }

    public void setSigPageImageIgnored(String sigPageImageIgnored) {
        this.sigPageImageIgnored = sigPageImageIgnored;
    }

    public void setDiffImage(String diffImage) {
        this.diffImage = diffImage;
    }
}
