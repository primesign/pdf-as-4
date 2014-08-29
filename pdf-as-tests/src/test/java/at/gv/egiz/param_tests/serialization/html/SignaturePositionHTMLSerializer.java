package at.gv.egiz.param_tests.serialization.html;

import java.awt.Rectangle;
import java.io.PrintWriter;
import java.util.List;

import at.gv.egiz.param_tests.serialization.TestInfoSerializer;
import at.gv.egiz.param_tests.testinfo.SignaturePositionTestInfo;
import at.gv.egiz.param_tests.testinfo.SignaturePositionTestInfo.SignaturePositionParameters;

/**
 * Concrete test information serializer for signature position tests using HTML
 * and Twitter-bootstrap.
 * 
 * @author mtappler
 *
 */
public class SignaturePositionHTMLSerializer extends
        HTMLSerializer<SignaturePositionTestInfo> {
    /**
     * the test information object which is serialized, it is the same as
     * <code>TestInfoSerializer.baseTestInfo</code>
     */
    private SignaturePositionTestInfo testInfo;

    /**
     * Default contructor used for registering it as prototype.
     */
    public SignaturePositionHTMLSerializer() {
        this(null);
    }

    /**
     * Package protected constructor used for cloning
     * 
     * @param testInfo
     */
    SignaturePositionHTMLSerializer(SignaturePositionTestInfo testInfo) {
        this.testInfo = testInfo;
    }

    @Override
    public SignaturePositionTestInfo createTestInfo() {
        testInfo = new SignaturePositionTestInfo();
        baseTestInfo = testInfo;
        return testInfo;
    }

    @Override
    public TestInfoSerializer<SignaturePositionTestInfo> cloneSerializer() {
        return new SignaturePositionHTMLSerializer(testInfo);
    }

    @Override
    protected void writeTestData(PrintWriter pw) {
        if (testInfo.getAdditionParameters().isCaptureReferenceImage()) {
            writeTestDataCapture(pw);
        } else {
            writeTestDataTest(pw);
        }
    }

    /**
     * Method for writing test data if only a reference image was captured
     * 
     * @param pw
     *            object which should be used for writing
     */
    private void writeTestDataCapture(PrintWriter pw) {
        pw.println("<h2>Captured reference image</h2>");
        writePanel(
                pw,
                "Reference image",
                getImageString(testInfo.getAdditionParameters()
                        .getRefImageFileName(), "reference image"));
        if (testInfo.getRefImageIgnored() != null) {
            writePanel(
                    pw,
                    "Reference image with ignored areas",
                    getImageString(testInfo.getRefImageIgnored(),
                            "reference image (ignored)"));
        }
    }

    /**
     * Method for writing test data if an actual signature position test was
     * performed.
     * 
     * @param pw
     *            object which should be used for writing
     */
    private void writeTestDataTest(PrintWriter pw) {
        pw.println("<h2>Image data captured during test</h2>");
        writePanel(
                pw,
                "Reference image with ignored areas",
                getImageString(testInfo.getRefImageIgnored(),
                        "reference image (ignored)"));
        writePanel(
                pw,
                "Signature page image with ignored areas",
                getImageString(testInfo.getSigPageImageIgnored(),
                        "signature page image (ignored)"));
        writePanel(pw, "Difference image",
                getImageString(testInfo.getDiffImage(), "difference image"));
    }

    /**
     * This method creates HTML-image-tag for an image file.
     * 
     * @param imageFileName
     *            location of the image file
     * @param altText
     *            the alternative text
     * @return HTML-image-tag
     */
    private String getImageString(String imageFileName, String altText) {
        String imageString;
        if (imageFileName != null)
            imageString = String.format(
                    "<img class=\"img-responsive\" src=\"%s\" alt=\"%s\">",
                    imageFileName, altText);
        else
            imageString = "<p>This image was not captured correctly. "
                    + "The test may have been aborted before because of an IO error.</p>";
        return imageString;
    }

    @Override
    public String getTestType() {
        return "Signature Position";
    }

    @Override
    protected void writeTestSpecificParameters(PrintWriter pw) {

        SignaturePositionParameters additionalParameters = testInfo
                .getAdditionParameters();
        StringBuilder panelBody = new StringBuilder();

        panelBody.append("<dl>");
        panelBody.append(createDescription("Signature positioning string",
                additionalParameters.getPositionString()));
        panelBody.append(createDescription("Signature page number",
                Integer.toString(additionalParameters.getSigPageNumber())));
        panelBody.append(createDescription("Reference image file name",
                additionalParameters.getRefImageFileName()));
        panelBody.append(createDescription("Ignored Areas",
                createIgnoredAreasDescription(additionalParameters
                        .getIgnoredAreas())));
        panelBody.append("</dl>");
        writePanel(pw, "Test specific parameters", panelBody.toString());
    }

    /**
     * This method creates an unordered HTML list for the ignored areas for a
     * signature position test.
     * 
     * @param ignoredAreas
     *            list of ignored areas
     * @return HTML-string representing the ignored areas
     */
    private String createIgnoredAreasDescription(List<Rectangle> ignoredAreas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (Rectangle r : ignoredAreas) {
            sb.append("<li>");
            sb.append(String.format("x:%d y:%d width:%d height:%d", r.x, r.y,
                    r.width, r.height));
            sb.append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

}
