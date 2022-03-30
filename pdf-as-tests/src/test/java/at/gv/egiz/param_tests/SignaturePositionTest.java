package at.gv.egiz.param_tests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.gv.egiz.param_tests.provider.BaseSignatureTestData;
import at.gv.egiz.param_tests.provider.SignaturePositionProvider;
import at.gv.egiz.param_tests.serialization.SerializiationManager;
import at.gv.egiz.param_tests.serialization.html.SignaturePositionHTMLSerializer;
import at.gv.egiz.param_tests.testinfo.SignaturePositionTestInfo;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

/**
 * Parameterized unit test for checking the correct positioning of the signature
 * block, or for capturing reference images (if it is known that the current
 * implementation is correct).
 * 
 * @author mtappler
 *
 */
@RunWith(Parameterized.class)
public class SignaturePositionTest extends SignatureTest {

    /**
     * Zoom value which controls the resolution of the image taken for image
     * comparison. It must be the same as for capturing the reference picture.
     */
    private static final float ZOOM = 4;
    /**
     * The page number of the page, which shows the signature block.
     */
    private int sigPageNumber = -1;
    /**
     * A list of rectangular areas, which will be ignored for image comparison
     */
    private List<Rectangle> ignoredAreas = null;
    /**
     * the file name of the reference file for image comparison
     */
    private String refImageFileName = null;
    /**
     * if set to true, a reference image will be captured, but no actual
     * comparison will be performed
     */
    private boolean captureReference = false;

    /**
     * Set up method to perform, when the class is loaded, which registers a
     * serializer for it.
     */
    @BeforeClass
    public static void setUpClass() {
        SerializiationManager.getInstance().registerSerializer(
                new SignaturePositionHTMLSerializer(),
                SignaturePositionTestInfo.class);
    }

    /**
     * Constructor for a single parameterized unit test, constructor parameters
     * are parameters for the test.
     * 
     * @param testDirectory
     *            directory, which defines this test
     * @param testName
     *            name of this test
     * @param baseTestData
     *            basic test data common to all parameterized signature tests
     * @param positionString
     *            string specifying the position of the signature block
     * @param sigPageNumber
     *            number of the page, on which the signature block should be
     *            shown
     * @param ignoredAreas
     *            areas, which are ignored during the image comparison
     * @param refImageFileName
     *            file name of the reference image
     * @param captureReference
     *            if set to true, a reference image will be captured, but no
     *            actual comparison will be performed
     */
    public SignaturePositionTest(String testDirectory, String testName,
            BaseSignatureTestData baseTestData, String positionString,
            int sigPageNumber, List<Rectangle> ignoredAreas,
            String refImageFileName, boolean captureReference) {
        this.baseTestData = baseTestData;
        this.positionString = positionString;
        this.sigPageNumber = sigPageNumber;
        this.ignoredAreas = ignoredAreas;
        if (baseTestData.getPdfFile() != null) { // should not happen actually
            this.refImageFileName = extractDirectoryString(baseTestData
                    .getPdfFile()) + refImageFileName;
        } else {
            this.refImageFileName = refImageFileName;
        }
        this.captureReference = captureReference;
    }

    /**
     * This methods truncates a file such that the base name of the given is cut
     * off, i.e. it does something like Unix' dirname, but adds a "/" at the
     * end.
     * 
     * @param fileName
     *            the name of the file
     * @return the directory name part of the file
     */
    private String extractDirectoryString(String fileName) {
        try {
            return new File(fileName).getCanonicalFile().getParent() + "/";
        } catch (IOException e) {
            // fall back
            return fileName.substring(0, fileName.lastIndexOf('/') + 1);
        }
    }

    /**
     * Static data-function, which is needed for JUnit's parameterized tests. It
     * returns one collection item per test, which contains one array element
     * per constructor parameter.
     * 
     * @return the parameterized test data
     */
    //@Parameters(name = "{index}-signature position test:<{0}> - {1}")
    @Parameters(name = "{index}-{1}")
    public static Collection<Object[]> data() {
        return new SignaturePositionProvider().gatherData();
    }

    /**
     * Helper method, which captures a reference image, i.e. an image of the
     * page with page number specified by <code>sigPageNumber</code> of the
     * PDF-file after signing. The captured file is saved, as well as a modified
     * version of it. The modified version contains black rectangles for the
     * ignored areas.
     * 
     * @param testInfo
     *            a test info object, which is used to store the location of the
     *            reference image, as well as the location of the reference
     *            image with ignored areas
     * @throws IOException
     * @throws InterruptedException 
     */
    private void captureReferenceImage(SignaturePositionTestInfo testInfo)
            throws IOException, InterruptedException {
        String pdfName = baseTestData.getOutputFile();
        String referenceOutputFile = refImageFileName;
        int pageNumber = sigPageNumber;
        BufferedImage image = captureImage(pdfName, pageNumber);
        ImageIO.write(image, "png", new File(referenceOutputFile));
        Graphics refImageGraphics = image.createGraphics();
        ignoreAreas(refImageGraphics, image.getHeight());
        String refImageIgnored = extractDirectoryString(baseTestData
                .getOutputFile()) + "refImage_ignored.png";
        ImageIO.write(image, "png", new File(refImageIgnored));
        testInfo.setRefImageIgnored(refImageIgnored);
        refImageGraphics.dispose();
        image.flush();
    }

    /**
     * The actual test method, which captures an image of the signature block
     * page of the signed PDFs and compares it pixel by pixel with the reference
     * image.
     * 
     * @throws FileNotFoundException
     * @throws CertificateException
     * @throws IOException
     * @throws PdfAsException
     * @throws IndexOutOfBoundsException
     * @throws PrinterException
     * @throws PDFASError 
     * @throws InterruptedException 
     */
    @Ignore
    @Test
    public void signaturePositionTest() throws FileNotFoundException,
            CertificateException, IOException, PdfAsException,
            IndexOutOfBoundsException, PrinterException, PDFASError, InterruptedException {
        SignaturePositionTestInfo testInfo = SerializiationManager
                .getInstance().createTestInfo(SignaturePositionTestInfo.class,
                        baseTestData);
        testInfo.getAdditionParameters().setCaptureReferenceImage(
                captureReference);
        testInfo.getAdditionParameters().setPositionString(positionString);
        testInfo.getAdditionParameters().setIgnoredAreas(ignoredAreas);
        testInfo.getAdditionParameters().setRefImageFileName(refImageFileName);
        testInfo.getAdditionParameters().setSigPageNumber(sigPageNumber);

        Assume.assumeNotNull(positionString);
        Assume.assumeNotNull(refImageFileName);
        Assume.assumeFalse("A page number must be specified",
                sigPageNumber == -1);
        signPDFFile();
        if (captureReference) {
            captureReferenceImage(testInfo);
            return;
        }
        BufferedImage sigPageImage = captureImage(baseTestData.getOutputFile(),
                sigPageNumber);
        assertNotNull("Could not get image of page", sigPageImage);
        BufferedImage refImage = ImageIO.read(new File(refImageFileName));
        assertNotNull("Could not get reference image", sigPageImage);

        assertEquals("Width of image differs from reference",
                refImage.getWidth(), sigPageImage.getWidth());
        assertEquals("Height of image differs from reference",
                refImage.getHeight(), sigPageImage.getHeight());

        Graphics sigPageGraphics = sigPageImage.getGraphics();
        Graphics refImageGraphics = refImage.createGraphics();
        int imageHeight = sigPageImage.getHeight();
        ignoreAreas(sigPageGraphics, imageHeight);
        ignoreAreas(refImageGraphics, imageHeight);

        String refImageIgnored = extractDirectoryString(baseTestData
                .getOutputFile()) + "refImage_ignored.png";
        ImageIO.write(refImage, "png", new File(refImageIgnored));
        testInfo.setRefImageIgnored(refImageIgnored);
        String sigPageImageIgnored = extractDirectoryString(baseTestData
                .getOutputFile()) + "sigPageImage_ignored.png";
        ImageIO.write(sigPageImage, "png", new File(sigPageImageIgnored));
        testInfo.setSigPageImageIgnored(sigPageImageIgnored);

        // now perform the pixel by pixel comparison
        boolean same = true;
        BufferedImage differenceImage = new BufferedImage(refImage.getWidth(),
                refImage.getHeight(), refImage.getType());
        Graphics differenceGraphics = differenceImage.createGraphics();
        differenceGraphics.setColor(Color.WHITE);
        differenceGraphics.fillRect(0, 0, differenceImage.getWidth(),
                differenceImage.getHeight());
        for (int x = 0; x < refImage.getWidth(); x++) {
            for (int y = 0; y < refImage.getHeight(); y++) {
                // since both reference and signature page image are
                // produced in the same (use same color model) and we
                // want a pixel by pixel comparison, this should work
                boolean samePixel = refImage.getRGB(x, y) == sigPageImage
                        .getRGB(x, y);
                if (!samePixel) {
                    same = false;
                    differenceImage.setRGB(x, y, Color.RED.getRGB());
                }
            }
        }
        String diffImage = extractDirectoryString(baseTestData.getOutputFile())
                + "difference.png";
        ImageIO.write(differenceImage, "png", new File(diffImage));
        testInfo.setDiffImage(diffImage);

        differenceGraphics.dispose();
        differenceImage.flush();
        sigPageGraphics.dispose();
        sigPageImage.flush();
        refImageGraphics.dispose();
        refImage.flush();
        assertTrue("Images must be the same", same);
    }

    /**
     * Helper method, which "clears" all areas specified by
     * <code>ignoredAreas</code>. "Cleared" are colored in the background color
     * of the image (black).
     * 
     * @param graphics
     *            Graphics object corresponding to an image
     * @param imageHeight
     *            the height of the image
     */
    protected void ignoreAreas(Graphics graphics, int imageHeight) {
        for (Rectangle r : ignoredAreas) {
            int effectiveX = (int) (r.x * ZOOM);
            // in awt 0 is at top, in PDF-AS 0 is at bottom
            int effectiveY = imageHeight - (int) (r.y * ZOOM);
            int effectiveWidth = (int) (r.width * ZOOM);
            int effectiveHeight = (int) (r.height * ZOOM);
            graphics.clearRect(effectiveX, effectiveY, effectiveWidth,
                    effectiveHeight);
        }
    }

    /**
     * This method captures an image of a page of a PDF document. This is done
     * using the rendering capabilities of ICEPDF.
     * 
     * @param fileName
     *            the name of the PDF file
     * @param pageNumber
     *            the page number which should be captured
     * @return the captured image
     * @throws InterruptedException 
     */
    private BufferedImage captureImage(String fileName, int pageNumber) throws InterruptedException {
        Document document = new Document();
        try {
            document.setFile(fileName);
        } catch (Exception e) {
            document.dispose();
            fail(String
                    .format("Not possible to capture page %d of file %s, because of %s.",
                            pageNumber, fileName, e.getMessage()));
        }
        Page page = document.getPageTree().getPage(pageNumber - 1);
        page.init();
        PDimension sz = page.getSize(Page.BOUNDARY_CROPBOX, 0, ZOOM);

        int pageWidth = (int) sz.getWidth();
        int pageHeight = (int) sz.getHeight();

        BufferedImage image = new BufferedImage(pageWidth, pageHeight,
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.createGraphics();
        page.paint(g, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX, 0,
                ZOOM);
        document.dispose();
        return image;
    }

}
