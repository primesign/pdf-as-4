package at.gv.egiz.param_tests;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.param_tests.provider.BaseSignatureDataProvider;
import at.gv.egiz.param_tests.provider.BaseSignatureTestData;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSink;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;
import at.gv.egiz.sl.util.BKUSLConnector;
import at.gv.egiz.sl.util.MOAConnector;

/**
 * Base class for parameterized signature tests, which provides fields and
 * methods common to all parameterized signature test.
 * 
 * @author mtappler
 *
 */
public class SignatureTest {

    /**
     * test data common to all test types, like connector type
     */
    protected BaseSignatureTestData baseTestData;
    /**
     * positioning string which specifies the position of the signature block
     */
    protected String positionString;
    /**
     * logger for this class
     */
    private static final Logger logger = LoggerFactory
            .getLogger(PDFASignatureTest.class);
    
    /**
     * a JUnit TestWatcher, this object gets notified several times a
     * subclass of this class is run as test
     */
    @Rule
    public TestRule watchman = new SignatureTestWatcher();
    
    /**
     * PdfAs used for signing and creating a configuration
     */
    protected PdfAs pdfAs;
    /**
     * the sign parameter used for signing in the test
     */
    protected SignParameter signParameter;
    /**
     * the connector used for signing in the test
     */
    protected IPlainSigner slConnector;
    
    /**
     * This method signs a file, all parameter like input file name are specified 
     * through fields of this class.
     * 
     * @return the signed file File-object
     * @throws IOException
     * @throws FileNotFoundException
     * @throws CertificateException
     * @throws PdfAsException
     */
    protected File signPDFFile() throws IOException, FileNotFoundException,
            CertificateException, PdfAsException {
        File inputFile = new File(baseTestData.getPdfFile());
        File outputPdfFile = new File(baseTestData.getOutputFile());
        DataSource dataSource = new ByteArrayDataSource(
                StreamUtils.inputStreamToByteArray(new FileInputStream(
                        inputFile)));
        ByteArrayDataSink dataSink = new ByteArrayDataSink();
        pdfAs = null;

        pdfAs = PdfAsFactory.createPdfAs(new File(baseTestData
                .getConfigurationFile()));
        Configuration configuration = pdfAs.getConfiguration();

        signParameter = PdfAsFactory.createSignParameter(configuration,
                dataSource);

        String id = UUID.randomUUID().toString();
        signParameter.setTransactionId(id);
        logger.debug("Transaction: " + id);

        slConnector = null;

        if (baseTestData.getConnectorData() != null) {
            if (baseTestData.getConnectorData().getConnectorType()
                    .equals("bku")) {
                slConnector = new PAdESSigner(new BKUSLConnector(configuration));
            } else if (baseTestData.getConnectorData().getConnectorType()
                    .equals("moa")) {
                slConnector = new PAdESSigner(new MOAConnector(configuration));
            } else if (baseTestData.getConnectorData().getConnectorType()
                    .equals("ks")) {
                Map<String, String> params = baseTestData.getConnectorData()
                        .getConnectorParameters();
                String keystoreFilename = params
                        .get(BaseSignatureDataProvider.KS_FILE_NAME);
                String keystoreAlias = params
                        .get(BaseSignatureDataProvider.KS_ALIAS);
                String keystoreType = params
                        .get(BaseSignatureDataProvider.KS_TYPE);
                String keystoreStorepass = params
                        .get(BaseSignatureDataProvider.KS_PASS);
                String keystoreKeypass = params
                        .get(BaseSignatureDataProvider.KS_KEY_PASS);

                assertNotNull(
                        "You need to provide a keystore file if using ks connector",
                        keystoreFilename);
                assertNotNull(
                        "You need to provide a key alias if using ks connector",
                        keystoreAlias);

                slConnector = new PAdESSignerKeystore(keystoreFilename,
                        keystoreAlias, keystoreStorepass, keystoreKeypass,
                        keystoreType);
            }
        }
        if (slConnector == null) {
            slConnector = new PAdESSigner(new BKUSLConnector(configuration));
        }

        signParameter.setOutput(dataSink);
        signParameter.setPlainSigner(slConnector);
        signParameter.setDataSource(dataSource);
        // this is not needed for PDF-A test
        if (positionString != null)
            signParameter.setSignaturePosition(positionString);
        signParameter.setSignatureProfileId(baseTestData.getProfilID());
        logger.debug("Starting signature for " + baseTestData.getPdfFile());
        logger.debug("Selected signature Profile " + baseTestData.getProfilID());
        /* SignResult result = */pdfAs.sign(signParameter);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputPdfFile, false);
            fos.write(dataSink.getData());
            fos.close();
        } catch (IOException e) {
            logger.debug("IO exception occured while writing PDF output file",
                    e);
            throw e;
        } finally {
            IOUtils.closeQuietly(fos);
        }

        return outputPdfFile;
    }
}
