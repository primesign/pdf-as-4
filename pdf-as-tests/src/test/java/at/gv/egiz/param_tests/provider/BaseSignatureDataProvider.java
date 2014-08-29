package at.gv.egiz.param_tests.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.api.PdfAsFactory;

/**
 * Abstract base class for test data providers. The subclasses of it shall read
 * configuration properties and provide test parameters for parameterized unit
 * tests. This offers some functionality, which is needed by all tests, e.g. it
 * reads basic configuration data, which is needed for signing a PDF file, which
 * is done by all tests.
 * 
 * @author mtappler
 *
 */
public abstract class BaseSignatureDataProvider {

    /**
     * config-property key for specifying the type of the test
     */
    public static final String TEST_TYPE = "test.type";

    /**
     * config-property key for specifying the name of the test, if no name is
     * specified, the test directory will be used as name
     */
    public static final String TEST_NAME = "test.name";

    /**
     * config-property for specifying signature profile
     */
    public static final String PROFILE_ID = "profile.id";

    /**
     * config-property key for specifying the name of the output file
     */
    public static final String OUTPUT_FILE = "output.file";
    /**
     * config-property key for specifying the type of connector, which is used
     * for signing
     */
    public static final String CONNECTOR = "connector";
    /**
     * config-property key for PDF-AS configuration file location
     */
    public static final String CONFIG_FILE = "config.file";
    /**
     * config-property key for PDF input file name, the file which is signed
     */
    public static final String INPUT_FILE = "input.file";

    public static final String PARENT_CFG = "parent";
    
    /**
     * default location for PDF-AS config file
     */
    public static final String STANDARD_CONFIG_LOCATION = System
            .getProperty("user.home") + "/.pdfas/";

    /**
     * config-property key for the filename of the keystore, required if ks
     * (keystore) is used as connector
     */
    public static final String KS_FILE_NAME = "ks.filename";

    /**
     * config-property key for the key alias, required if ks (keystore) is used
     * as connector
     */
    public static final String KS_ALIAS = "ks.alias";

    /**
     * config-property key for the keystore type, optional value if ks
     * (keystore) is used as connector
     */
    public static final String KS_TYPE = "ks.type";

    /**
     * config-property key for the password of the keystore, required if ks
     * (keystore) is used as connector
     */
    public static final String KS_PASS = "ks.pass";

    /**
     * config-property key for the password of the key, required if ks
     * (keystore) is used as connector
     */
    public static final String KS_KEY_PASS = "ks.keypass";

    /**
     * logger for this class
     */
    private static final Logger logger = LoggerFactory
            .getLogger(BaseSignatureDataProvider.class);

    /**
     * Interface for test classes: This method should be invoked to get the data
     * for parameterized unit tests. It calls two template method style (design
     * pattern) methods to perform different actions for different test types.
     * Generally it loops over all directories in the test directory and filters
     * using a wildcard-filter and then calls abstract methods, one to decide if
     * the subclass is a provider the given test type
     * <code>isSupportedTestType</code> and one to extract configuration data
     * <code>extractDataFromConfig</code>.
     * 
     * @return parameters for the parameterized unit tests
     */
    public List<Object[]> gatherData() {
        String testDir = System.getProperty("test.dir");
        logger.info("Data from: " + testDir);
        String testFilter = System.getProperty("test.filter");
        List<Object[]> result = new ArrayList<Object[]>();
        File testDirFile = new File(testDir);
        File rootConfigFile = new File(testDirFile, "config.properties");
        Properties rootProp = new Properties();
        InputStream rootIn = null;
        try {
            rootIn = new FileInputStream(rootConfigFile);
            rootProp.load(rootIn);
        } catch (IOException e) {
            // if we can't get root properties, we just take empty root
            // properties
            rootProp = new Properties();
        } finally {
            IOUtils.closeQuietly(rootIn);
        }

        File[] childFiles = null;
        if (testFilter == null) {
            childFiles = testDirFile.listFiles();
        } else {
            String[] wildcards = testFilter.split(";");
            childFiles = testDirFile
                    .listFiles((FilenameFilter) new WildcardFileFilter(
                            wildcards));
        }
        int idx = 0;
        for (File child : childFiles) {
            if (child.isDirectory() && directoryContainsConfig(child)) {
                File configFile = new File(child, "config.properties");
                Properties prop = new Properties();
                InputStream in = null;
                try {
                    in = new FileInputStream(configFile);
                    prop.load(in);
                    
                    String parent = prop.getProperty(PARENT_CFG);
                    if(parent != null) {
                    	File parentFile = new File(child, parent);
                    	if(parentFile.exists()) {
                    		prop.clear();
                    		prop.load(new FileInputStream(parentFile));
                    		prop.load(new FileInputStream(configFile));
                    	}
                    }
                    
                    if (isSupportedTestType(prop)) {
                        Object[] data = extractDataFromConfig(configFile,
                                rootProp, prop);
                        result.add(data);
                        String testName = idx + "-" + data[1].toString();
                        ((BaseSignatureTestData)data[2]).setUniqueUnitTestName(testName);
                        idx++;
                    }
                } catch (IOException e) {
                    logger.warn(
                            "Could not run test with config:"
                                    + configFile.getAbsolutePath(), e);
                } finally {
                    if (in != null)
                        IOUtils.closeQuietly(in);
                }
            }
        }
        return result;
    }

    /**
     * Method to extract test-type specific data/parameters from a config file.
     * 
     * @param configFile
     *            the File object corresponding to the config file
     * @param rootProps
     *            the properties read from the root config file
     * @param configProps
     *            the properties read from the config file
     * @return parameters for one parameterized unit test
     */
    protected abstract Object[] extractDataFromConfig(File configFile,
            Properties rootProps, Properties configProps);

    /**
     * Utility method to extract basic configuration data, which is needed by
     * all parameterized unit tests, i.e. test directory, test name and one
     * instance of <code>BaseSignatureTestData</code>.
     * 
     * @param configFile
     *            the File object corresponding to the config file
     * @param rootProps
     *            the properties read from the root config file
     * @param configProps
     *            the properties read from the config file
     * @return basic parameters for one parameterized unit test
     */
    protected Object[] extractStandardDataFromConfig(File configFile,
            Properties rootProps, Properties configProps) {
        String testDirectory = null;
        try {
            testDirectory = configFile.getParentFile().getCanonicalPath();
        } catch (IOException e) {
            // getCanonicalPath() might again throw an exception
            testDirectory = configFile.getAbsolutePath().replace(
                    "/config.properties", "");
        }
        String testName = getTestName(configFile, configProps);
        String configurationFile = provideConfigFile(rootProps, configProps);
        String profilID = getProperty(rootProps, configProps, PROFILE_ID);
        String pdfFile = getPDFFileName(configFile, configProps);
        String outputFile = getOutputFileName(pdfFile, configFile, configProps);
        ConnectorData connectorData = provideConnectorData(rootProps,
                configProps);
        return new Object[] {
                testDirectory,
                testName,
                new BaseSignatureTestData(testDirectory, testName,
                        configurationFile, profilID, pdfFile, outputFile,
                        connectorData) };
    }

    /**
     * Test if the concrete provider implementation supports a test given by
     * properties.
     * 
     * @param prop
     *            the properties file of this test
     * @return true if the test is suppported, false otherwise
     */
    protected abstract boolean isSupportedTestType(Properties prop);

    /**
     * Helper method to check if a directory contains a file called
     * "config.properties".
     * 
     * @param file
     *            the File object corresponding to the directory
     * @return true if the directory contains a config file
     */
    private boolean directoryContainsConfig(File file) {
        return Arrays.asList(file.list()).contains("config.properties");
    }

    /**
     * Retrieves the test name, which should either be specified in the config
     * file for the test case (not in the root properties) or be equal to the
     * directory name of the test.
     * 
     * @param configFile
     *            file object for the config file
     * @param configProps
     *            the properties defined in the config file
     * @return the test name
     */
    protected String getTestName(File configFile, Properties configProps) {
        if (configProps.containsKey(TEST_NAME)) {
            return configProps.getProperty(TEST_NAME);
        } else {
            int lastSlash = configFile.getAbsolutePath().lastIndexOf('/');
            int secondToLastSlash = configFile.getAbsolutePath().lastIndexOf(
                    '/', lastSlash - 1);
            return configFile.getAbsolutePath().substring(
                    secondToLastSlash + 1, lastSlash);
        }
    }

    /**
     * This method retrieves the configured location of the PDF-AS configuration
     * file. It may return the standard configuration location if no location is
     * specified.
     * 
     * @param rootProps
     *            root properties, i.e. properties specified in the test
     *            directory
     * @param configProps
     *            properties specific for one test case
     * @return location of the property file
     */
    protected String provideConfigFile(Properties rootProps,
            Properties configProps) {
        String configurationFile = getProperty(rootProps, configProps,
                CONFIG_FILE);
        if (configurationFile == null) {
            configurationFile = STANDARD_CONFIG_LOCATION;
            deployConfigIfNotexisting();
        }
        return configurationFile;
    }

    /**
     * Deploys a PDF-AS config in the standard location if it does not already
     * exist, same as in pdf-as-cli (Main.java).
     */
    private static void deployConfigIfNotexisting() {
        File configurationLocation = new File(STANDARD_CONFIG_LOCATION);
        try {
            if (!configurationLocation.exists()) {
                PdfAsFactory.deployDefaultConfiguration(configurationLocation);
            }
        } catch (Exception e) {
            logger.warn("Failed to deploy default confiuration to "
                    + configurationLocation.getAbsolutePath(), e);
        }
    }

    /**
     * Retrieves the file name of the PDF input file for a test, i.e. the file
     * which should be signed. If the configuration properties do not specify an
     * input file, the first file found in the test case directory is used.
     * 
     * @param configFile
     *            the config file for the test
     * @param configProps
     *            the config properties for the test
     * @return the input file name
     */
    protected String getPDFFileName(File configFile, Properties configProps) {
        String dirString = configFile.getParent() + "/";
        if (configProps.containsKey(INPUT_FILE))
            return dirString + configProps.getProperty(INPUT_FILE);
        else {
            // just return the first pdf file found in the TC directory
            // or null for which we need to check in the TC
            String[] pdfsInDir = configFile.getParentFile().list(
                    new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".pdf");
                        }
                    });
            if (pdfsInDir.length == 0)
                return null;
            else
                return dirString + pdfsInDir[0];

        }
    }

    /**
     * Retrieves the file name of the PDF output file for a test, i.e. the file
     * which is signed. If the configuration properties do not specify an output
     * file, "_signed" is appended to the name of the input file and used as
     * name.
     * 
     * @param configFile
     *            the config file for the test
     * @param configProps
     *            the config properties for the test
     * @return the output file name
     */
    protected String getOutputFileName(String pdfFile, File configFile,
            Properties configProps) {
        String outputFile = configProps.getProperty(OUTPUT_FILE);
        if (outputFile == null) {
            if (pdfFile.endsWith(".pdf")) {
                outputFile = pdfFile.subSequence(0,
                        pdfFile.length() - ".pdf".length())
                        + "_signed.pdf";
            } else {
                outputFile = pdfFile + "_signed.pdf";
            }
            outputFile = outputFile.substring(outputFile.lastIndexOf('/') + 1,
                    outputFile.length());
        }
        new File(configFile.getParent() + "/out/").mkdir();
        return configFile.getParent() + "/out/" + outputFile;

    }

    /**
     * This method reads configuration properties and returns connector data,
     * i.e. data specifying which connector should be used together with which
     * parameters.
     * 
     * @param rootProps
     *            root properties, i.e. properties specified in the test
     *            directory
     * @param configProps
     *            properties specific for one test case
     * @return connector data
     */
    protected ConnectorData provideConnectorData(Properties rootProps,
            Properties configProps) {
        if (getProperty(rootProps, configProps, CONNECTOR) != null) {
            String connectorType = getProperty(rootProps, configProps,
                    CONNECTOR);
            if (connectorType.equalsIgnoreCase("bku")) {
                return new ConnectorData("bku",
                        Collections.<String, String> emptyMap());
            } else if (connectorType.equalsIgnoreCase("moa")) {
                return new ConnectorData("moa",
                        Collections.<String, String> emptyMap());
            } else if (connectorType.equalsIgnoreCase("ks")) {
                Map<String, String> connectorParamaters = new HashMap<String, String>();
                connectorParamaters.put(KS_FILE_NAME,
                        getProperty(rootProps, configProps, KS_FILE_NAME));
                connectorParamaters.put(KS_ALIAS,
                        getProperty(rootProps, configProps, KS_ALIAS));
                if (getProperty(rootProps, configProps, KS_TYPE) != null) {
                    connectorParamaters.put(KS_TYPE,
                            getProperty(rootProps, configProps, KS_TYPE));
                } else {
                    connectorParamaters.put(KS_TYPE, "PKCS12");
                    logger.debug("Defaulting to PKCS12 keystore type.");
                }
                if (getProperty(rootProps, configProps, KS_PASS) != null) {
                    connectorParamaters.put(KS_PASS,
                            getProperty(rootProps, configProps, KS_PASS));
                } else {
                    connectorParamaters.put(KS_PASS, "");
                }
                if (getProperty(rootProps, configProps, KS_KEY_PASS) != null) {
                    connectorParamaters.put(KS_KEY_PASS,
                            getProperty(rootProps, configProps, KS_KEY_PASS));
                } else {
                    connectorParamaters.put(KS_KEY_PASS, "");
                }
                return new ConnectorData("ks", connectorParamaters);
            }
        }
        return null;
    }

    /**
     * Helper method which looks in the Properties-maps for the value of a given
     * key. If the value is found in <code>configProps</code> then this value is
     * used, otherwise the value found in <code>rootProps</code> is used. This
     * way, test properties can override root properties.
     * 
     * @param rootProps
     *            root properties, i.e. properties specified in the test
     *            directory
     * @param configProps
     *            properties specific for one test case
     * @param key
     *            a config-property key
     * @return the value for the key
     */
    protected String getProperty(Properties rootProps, Properties configProps,
            String key) {
        if (configProps.containsKey(key))
            return configProps.getProperty(key);
        else if (rootProps.containsKey(key))
            return rootProps.getProperty(key);
        else
            return null;
    }
}
