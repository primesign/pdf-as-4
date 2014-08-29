package at.gv.egiz.param_tests.serialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.param_tests.testinfo.TestInfo;

/**
 * Base for all test information serializer. It uses template method pattern,
 * fixing an algorithm for serialization, but delegating the actual steps to the
 * subclasses. A two-step hierarchy shall be formed, with this class at the
 * upper-most level, technology-specific (like HTML) serializer at the next and
 * technology- and test-type-specific serializer at the lowest level.
 * 
 * @author mtappler
 *
 * @param <T>
 *            the type of TestInfo which instance of this class, respectively
 *            subclasses of it can serialize
 */
public abstract class TestInfoSerializer<T extends TestInfo> {

    /**
     * logger for this class
     */
    private static final Logger logger = LoggerFactory
            .getLogger(TestInfoSerializer.class);

    /**
     * basic test information
     */
    protected TestInfo baseTestInfo;

    /**
     * Method for creating an of a subclass of <code>TestInfo</code>. The test
     * information created using this method shall be retrieved, when
     * <code>getBaseTestInfo()</code> is called.
     * 
     * @return instance of a <code>TestInfo</code>-subclass
     */
    public abstract T createTestInfo();

    /**
     * Clone method for serializer, it shall create a shallow copy of
     * <code>this</code> and return it. It does not use
     * <code>Object.clone()</code> for type safety, because this method returns
     * an <code>Object</code>-instance.
     * 
     * @return a clone of <code>this</code>
     */
    public abstract TestInfoSerializer<T> cloneSerializer();

    /**
     * getter
     * 
     * @return basic test information
     */
    public TestInfo getBaseTestInfo() {
        return baseTestInfo;
    }

    /**
     * Main serialization method. This method creates a test result file in the
     * test directory and then calls the methods to perform the serialization
     * steps. The following parts shall be written:
     * <ol>
     * <li>header</li>
     * <li>test specific parameter</li>
     * <li>test result</li>
     * <li>test specific data</li>
     * <li>footer</li>
     * </ol>
     */
    public void serialize() {
        File outputFile = new File(baseTestInfo.getBaseTestData()
                .getTestDirectory() + "/test_result." + fileEnding());
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outputFile);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF8"));
            writeHeader(pw);
            writeTestSpecificParameters(pw);
            writeTestResult(pw);
            writeTestData(pw);
            writeFooter(pw);
            pw.flush();
        } catch (FileNotFoundException e) {
            logger.warn("File not found for test info serialization.", e);
        } catch (UnsupportedEncodingException e) {
            logger.warn("Use unsupported encoding for serialization.", e);
        } finally {
            if (os != null)
                IOUtils.closeQuietly(os);
        }
    }

    /**
     * This writes the test result in some format like HTML to the provided
     * <code>PrintWriter</code>-object.
     * 
     * @param pw
     *            the <code>PrintWriter</code>-object whill shall be used for
     *            serialization
     */
    protected abstract void writeTestResult(PrintWriter pw);

    /**
     * This writes test specific parameters in some format like HTML to the
     * provided <code>PrintWriter</code>-object.
     * 
     * @param pw
     *            the <code>PrintWriter</code>-object whill shall be used for
     *            serialization
     */
    protected abstract void writeTestSpecificParameters(PrintWriter pw);

    /**
     * This writes the file header in some format like HTML to the provided
     * <code>PrintWriter</code>-object.
     * 
     * @param pw
     *            the <code>PrintWriter</code>-object whill shall be used for
     *            serialization
     */
    protected abstract void writeHeader(PrintWriter pw);

    /**
     * This writes test specific data in some format like HTML to the provided
     * <code>PrintWriter</code>-object.
     * 
     * @param pw
     *            the <code>PrintWriter</code>-object whill shall be used for
     *            serialization
     */
    protected abstract void writeTestData(PrintWriter pw);

    /**
     * This writes the file footer in some format like HTML to the provided
     * <code>PrintWriter</code>-object.
     * 
     * @param pw
     *            the <code>PrintWriter</code>-object whill shall be used for
     *            serialization
     */
    protected abstract void writeFooter(PrintWriter pw);

    /**
     * This method returns the file ending used for the test result file, like
     * "html" or "xml".
     * 
     * @return the file ending string
     */
    public abstract String fileEnding();

    /**
     * This method returns a description of the concrete test type, which a
     * concrete implementation of this class serializes.
     * 
     * @return a test type description
     */
    public abstract String getTestType();
}
