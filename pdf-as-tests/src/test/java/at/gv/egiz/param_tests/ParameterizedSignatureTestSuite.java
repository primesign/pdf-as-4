package at.gv.egiz.param_tests;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import at.gv.egiz.param_tests.serialization.SerializiationManager;
import at.gv.egiz.param_tests.serialization.html.HTMLTestSummaryWriter;

/**
 * Test suite which groups all available parameterized tests in
 * <code>Suite.SuiteClasses</code>-annotation.
 * 
 * @author mtappler
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ PDFASignatureTest.class, SignaturePositionTest.class })
public class ParameterizedSignatureTestSuite {
    /**
     * variable to save the standard output <code>PrintStream</code> after
     * redirecting it, to be able to reset it afterwards, this should only be
     * necessary if this class is loaded and run dynamically (not using right
     * mouse -> Run As -> JUnit Test)
     */
    private static PrintStream stdOutSave;
    /**
     * variable to save the standard error <code>PrintStream</code> after
     * redirecting it
     */
    private static PrintStream stderrSave;
    /**
     * stream which logs everything which is written to stdout during test
     * execution
     */
    private static ByteArrayOutputStream stdoutLog;
    /**
     * stream which logs everything which is written to stderr during test
     * execution
     */
    private static ByteArrayOutputStream stderrLog;

    /**
     * the location of the log4j configuration
     */
    private static final URL log4j = ParameterizedSignatureTestSuite.class
            .getResource("/log4j.properties");
    /**
     * If set to true, logging will be configured, when this class is loaded.
     * This ensures that logging will be available in tests before logging is
     * configured by pdf-as.
     */
    private static final boolean CONFIGURE_LOGGING = true;

    /**
     * Getter
     * 
     * @return the byte array output stream which captures stdout
     */
    public static ByteArrayOutputStream getStdoutLog() {
        // to avoid null NPE, if a test is started without testsuite
        return stdoutLog != null ? stdoutLog : new ByteArrayOutputStream();
    }

    /**
     * Getter
     * 
     * @return the byte array output stream which captures stderr
     */
    public static ByteArrayOutputStream getStderrLog() {
        // to avoid null NPE
        return stderrLog != null ? stderrLog : new ByteArrayOutputStream();
    }

    static {
    	String testDir = System.getProperty("test.dir");
    	System.out.println("Running Tests from: " + testDir);
    }
    
    /**
     * Sets up the test run by redirecting stdout and stderr such that both are
     * written to the console and to a byte array output stream and configures
     * logging if the corresponding flag is set.
     * 
     * @throws InitializationError
     *             if no test directory is given via the system property
     *             "test.dir"
     */
    @BeforeClass
    public static void setUpClass() throws InitializationError {
        stdoutLog = new ByteArrayOutputStream();
        TeeOutputStream teeStdOut = new TeeOutputStream(System.out, stdoutLog);
        stdOutSave = System.out;
        System.setOut(new PrintStream(teeStdOut));
        stderrLog = new ByteArrayOutputStream();
        TeeOutputStream teeStdErr = new TeeOutputStream(System.err, stderrLog);
        stderrSave = System.err;
        System.setErr(new PrintStream(teeStdErr));
        if (CONFIGURE_LOGGING && log4j != null) {
            try {
                PropertyConfigurator.configure(new FileInputStream(log4j
                        .getFile()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String testDir = System.getProperty("test.dir");
        if (testDir == null)
            throw new InitializationError("Test directory is not set");

        SerializiationManager.getInstance().setTestSummaryWriter(
                new HTMLTestSummaryWriter(testDir));
    }

    /**
     * Resets stdout and stderr redirection and serializes all test results.
     */
    @AfterClass
    public static void tearDownClass() {
        System.setOut(stdOutSave);
        System.setErr(stderrSave);
        SerializiationManager.getInstance().serializeAll();
    }

}
