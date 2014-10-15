package at.gv.egiz.param_tests;

import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.param_tests.provider.BaseSignatureTestData;
import at.gv.egiz.param_tests.provider.PDFAProvider;
import at.gv.egiz.param_tests.serialization.SerializiationManager;
import at.gv.egiz.param_tests.serialization.html.PDFAHTMLSerizalier;
import at.gv.egiz.param_tests.testinfo.PDFATestInfo;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

/**
 * Parameterized unit-test for checking if a PDF-file is PDFA conform after
 * signing. The test result is inconclusive if the file is already not conform
 * before signing.
 * 
 * @author mtappler
 *
 */
@RunWith(Parameterized.class)
public class PDFASignatureTest extends SignatureTest {

    /**
     * the logger for this class
     */
    private static final Logger logger = LoggerFactory
            .getLogger(PDFASignatureTest.class);

    /**
     * Sets up this class, which includes registering a serializer for it,
     * respectively the test type.
     */
    @BeforeClass
    public static void setUpClass() {
        SerializiationManager.getInstance().registerSerializer(
                new PDFAHTMLSerizalier(), PDFATestInfo.class);
    }

    /**
     * Constructor which sets the parameter for this parameterized unit test.
     * 
     * @param testDirectory
     *            name of the directory of this test
     * @param testName
     *            the name of this test
     * @param testData
     *            basic test data, like connector, or signature profile
     */
    public PDFASignatureTest(String testDirectory, String testName,
            BaseSignatureTestData testData) {
        this.baseTestData = testData;
    }

    /**
     * Static data-function, which is needed for JUnit's parameterized tests. It
     * returns one collection item per test, which contains one array element
     * per constructor parameter.
     * 
     * @return the parameterized test data
     */
    //@Parameters(name = "{index}-pdfa signature test:<{0}> - {1}")
    @Parameters(name = "{index}-{1}")
    public static Collection<Object[]> data() {
        return new PDFAProvider().gatherData();
    }

    /**
     * The actual unit test which is executed, it checks if the input is
     * PDFA-conform, signs the PDF file and checks if the signing result is
     * PDFA-conform. If the input file is not PDF-conform the test is skipped as
     * being inconclusive.
     * 
     * @throws CertificateException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws PdfAsException
     * @throws PDFASError 
     */
    @Test
    public void pdfaTest() throws CertificateException, FileNotFoundException,
            IOException, PdfAsException, PDFASError {
        PDFATestInfo testInfo = SerializiationManager.getInstance()
                .createTestInfo(PDFATestInfo.class, baseTestData);
        assertTrue("No input file given", baseTestData.getPdfFile() != null);
        File inputFile = new File(baseTestData.getPdfFile());
        assertTrue("Given input file must exists", inputFile.exists());
        Pair<ValidationResult, Throwable> resultBeforeSign = checkPDFAConformance(inputFile);
        testInfo.setResultBeforeSign(resultBeforeSign);
        Assume.assumeTrue("Input should conform to PDF-A standard",
                resultBeforeSign.getLeft() != null
                        && resultBeforeSign.getLeft().isValid());
        File outputPdfFile = signPDFFile();
        logger.debug("Signed document " + baseTestData.getOutputFile());

        Pair<ValidationResult, Throwable> resultAfterSign = checkPDFAConformance(outputPdfFile);
        testInfo.setResultAfterSign(resultAfterSign);
        assertTrue("Output file must be PDF-A conform",
                resultAfterSign.getLeft() != null
                        && resultAfterSign.getLeft().isValid());
    }

    /**
     * Helper method for checking PDFA-conformance.
     * 
     * @param fd
     *            the File object corresponding to the file, which should be
     *            checked.
     * @return a pair consisting of the validation result and an exception,
     *         which might have been thrown during the validation (both possibly
     *         null)
     */
    private Pair<ValidationResult, Throwable> checkPDFAConformance(File fd) {
        PreflightDocument document = null;
        ValidationResult result = null;
        try {
            PreflightParser parser = new PreflightParser(fd);
            parser.parse();
            document = parser.getPreflightDocument();
            document.validate();
            document.close();
            result = document.getResult();
            return new ImmutablePair<ValidationResult, Throwable>(result, null);
        } catch (SyntaxValidationException e) {
            logger.debug("The file " + fd.getName()
                    + " is syntactically invalid.", e);
            return new ImmutablePair<ValidationResult, Throwable>(result, e);
        } catch (IOException e) {
            logger.debug("An IOException (" + e.getMessage()
                    + ") occurred, while validating the PDF-A conformance of "
                    + fd.getName(), e);
            return new ImmutablePair<ValidationResult, Throwable>(result, e);
        } catch (RuntimeException e) {
            logger.debug("An RuntimeException (" + e.getMessage()
                    + ") occurred, while validating the PDF-A conformance of "
                    + fd.getName(), e);
            return new ImmutablePair<ValidationResult, Throwable>(result, e);
        } finally {
            if (document != null) {
                IOUtils.closeQuietly((Closeable)document);
            }
        }
    }

}
