package at.gv.egiz.param_tests.provider;

/**
 * Class holding data, which is used by all parameterized signature tests, e.g.
 * basic data needed for signing. It does not contain any logic at all
 * 
 * @author mtappler
 *
 */
public class BaseSignatureTestData {

    /**
     * location of the PDF-AS configuration file
     */
    private String configurationFile;
    /**
     * signature profile ID
     */
    private String profilID;
    /**
     * location of the input PDF file which should be signed
     */
    private String pdfFile;
    /**
     * location of the output PDF file which is the signed version of the input
     * file
     */
    private String outputFile;
    /**
     * connector data, which is used to determine the signer
     * (IPlainSigner-interface)
     */
    private ConnectorData connectorData;
    /**
     * the name of the test
     */
    private String testName;
    /**
     * directory name of the test
     */
    private String testDirectory;
    
    private String uniqueUnitTestName;

    /**
     * Constructor initializing all attributes.
     * 
     * @param testDirectory
     * @param testName
     * @param configurationFile
     * @param profilID
     * @param pdfFile
     * @param outputFile
     * @param connectorData
     */
    public BaseSignatureTestData(String testDirectory, String testName,
            String configurationFile, String profilID, String pdfFile,
            String outputFile, ConnectorData connectorData) {
        this.testDirectory = testDirectory;
        this.testName = testName;
        this.configurationFile = configurationFile;
        this.profilID = profilID;
        this.pdfFile = pdfFile;
        this.outputFile = outputFile;
        this.connectorData = connectorData;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public String getProfilID() {
        return profilID;
    }

    public void setProfilID(String profilID) {
        this.profilID = profilID;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public ConnectorData getConnectorData() {
        return connectorData;
    }

    public void setConnectorData(ConnectorData connectorData) {
        this.connectorData = connectorData;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDirectory() {
        return testDirectory;
    }

    public void setTestDirectory(String testDirectory) {
        this.testDirectory = testDirectory;
    }

	public String getUniqueUnitTestName() {
		return uniqueUnitTestName;
	}

	public void setUniqueUnitTestName(String uniqueUnitTestName) {
		this.uniqueUnitTestName = uniqueUnitTestName;
	}
}
