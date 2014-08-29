package at.gv.egiz.param_tests.provider;

import java.io.File;
import java.util.Properties;

/**
 * Signature data provider for PDF-A conformance tests
 * 
 * @author mtappler
 *
 */
public class PDFAProvider extends BaseSignatureDataProvider {

    /**
     * This method extracts signature test parameters for performing 
     * PDF-A conformance tests. It only provides standard parameters, because
     * it does not need any other parameters.
     */
    @Override
    protected Object[] extractDataFromConfig(File configFile,
            Properties rootProps, Properties configProps) {
        return extractStandardDataFromConfig(configFile, rootProps, configProps);
    }

    /**
     * This method checks if this provider supports a given test configuration.
     * In order to be supported the test type must be "pdfa".
     */
    @Override
    protected boolean isSupportedTestType(Properties configProps) {
        return configProps.containsKey(TEST_TYPE)
                && configProps.get(TEST_TYPE).equals("pdfa");
    }
}
