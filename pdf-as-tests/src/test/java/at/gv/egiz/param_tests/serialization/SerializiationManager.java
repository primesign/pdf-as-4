package at.gv.egiz.param_tests.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AssumptionViolatedException;

import at.gv.egiz.param_tests.provider.BaseSignatureTestData;
import at.gv.egiz.param_tests.testinfo.TestInfo;
import at.gv.egiz.param_tests.testinfo.TestVerdict;

/**
 * Singleton managing the serialization of test data. Generally there is one
 * serializer prototype per test type and one serializer clone per test which
 * was run. Each serializer clone holds the data of exactly one test and is able
 * to serizalize in some format. Additionally to that there is also a
 * TestSummaryWriter-object which writes a summary of all test results.
 * 
 * @author mtappler
 *
 */
public class SerializiationManager {

    /**
     * the singleton instance
     */
    private static SerializiationManager instance = null;

    /**
     * The singleton accessor method. It is not used synchronized as it is
     * expected that the parameterized unit tests won't be run in parallel.
     * 
     * @return the singleton instance
     */
    public static SerializiationManager getInstance() {
        // implement locking if needed
        if (instance == null) {
            instance = new SerializiationManager();
        }
        return instance;
    }

    /**
     * a map of serializer prototypes, the key is the canonical name of the
     * TestInfo-subclass which the prototype can serializer and the value is the
     * prototype itself
     */
    private Map<String, TestInfoSerializer<? extends TestInfo>> serializerPrototypes = new HashMap<String, TestInfoSerializer<? extends TestInfo>>();

    /**
     * a list of serializer clones, with one element for each test
     */
    private List<TestInfoSerializer<? extends TestInfo>> serializers = new ArrayList<TestInfoSerializer<? extends TestInfo>>();
    /**
     * the test summary, which writes the summary of test results all tests
     */
    private TestSummaryWriter testSummaryWriter;

    /**
     * protected constructor
     */
    protected SerializiationManager() {
    }

    /**
     * This method registers a serializer for a TestInfo-subclass.
     * 
     * @param serializer
     *            a serializer prototype
     * @param testInfoClass
     *            the class object corresponding to the TestInfo-subclass
     */
    public <T extends TestInfo> void registerSerializer(
            TestInfoSerializer<T> serializer, Class<T> testInfoClass) {
        serializerPrototypes.put(testInfoClass.getCanonicalName(), serializer);
    }

    /**
     * This methods creates an instance of a TestInfo-subclass and returns.
     * Actually a serializer is clone and the clone creates the instance and
     * holds it to serialize it later.
     * 
     * @param testInfoClass
     *            the class of the requested test information object
     * @param baseData
     *            basic test data which is set for all test information object
     * @return a test information object
     */
    public <T extends TestInfo> T createTestInfo(Class<T> testInfoClass,
            BaseSignatureTestData baseData) {
        @SuppressWarnings("unchecked")
        // unfortunately this cast is necessary
        TestInfoSerializer<T> serializer = (TestInfoSerializer<T>) serializerPrototypes
                .get(testInfoClass.getCanonicalName());
        // if there is no test info, the test will fail with a null pointer
        // exception
        // if it tries to access the test info
        if (serializer == null)
            return null;
        TestInfoSerializer<T> serializerClone = serializer.cloneSerializer();
        serializers.add(serializerClone);
        T tInfo = serializerClone.createTestInfo();
        tInfo.setBaseTestData(baseData);

        return tInfo;
    }

    /**
     * Sets a test given its directory name as succeeded. The directory name is
     * used, because it uniquely defines the test, as there is only one test per
     * directory and it is encoded in display name of the test.
     * 
     * @param directoryName
     *            the name of the directory of the test
     */
    public void setSucceeded(String directoryName) {
        TestInfo testInfo = getTestInfoForDirectory(directoryName);
        if (testInfo != null) {
            testInfo.setVerdict(TestVerdict.SUCCEEDED);
        }
    }

    /**
     * Retrieves test information for a test.
     * 
     * @param directoryName
     *            the name of the directory of the test
     * @return basic test information
     */
    private TestInfo getTestInfoForDirectory(String directoryName) {
        for (TestInfoSerializer<?> s : serializers) {
            if (directoryName.startsWith(s.getBaseTestInfo().getBaseTestData()
                    .getUniqueUnitTestName()))
                return s.getBaseTestInfo();
        }
        return null;
    }

    /**
     * Sets a test given its directory name as failed and sets a
     * <code>Throwable</code>-object as fail cause. The directory name is used,
     * because it uniquely defines the test, as there is only one test per
     * directory and it is encoded in display name of the test.
     * 
     * @param directoryName
     *            the name of the directory of the test
     * @param e
     *            the cause of the fail
     */
    public void setFailed(String directoryName, Throwable e) {
        TestInfo testInfo = getTestInfoForDirectory(directoryName);
        if (testInfo != null) {
            testInfo.setVerdict(TestVerdict.FAILED);
            testInfo.setFailCause(e);
        }
    }

    /**
     * Serializes all test results including a summary. After a call to this
     * function there will be an index file with the summary in the root test
     * directory and a test results file in all directories containing tests.
     */
    public void serializeAll() {
        testSummaryWriter.init();
        testSummaryWriter.writeHeader();
        Collections.sort(serializers,
                new Comparator<TestInfoSerializer<? extends TestInfo>>() {
                    public int compare(
                            TestInfoSerializer<? extends TestInfo> o1,
                            TestInfoSerializer<? extends TestInfo> o2) {
                        String firstDir = o1.getBaseTestInfo()
                                .getBaseTestData().getTestDirectory();
                        String secondDir = o2.getBaseTestInfo()
                                .getBaseTestData().getTestDirectory();
                        return firstDir.compareTo(secondDir);
                    }
                });

        for (TestInfoSerializer<?> s : serializers) {
            testSummaryWriter.writeSummaryOfTest(s.getBaseTestInfo(),
                    s.getTestType());
            s.serialize();
        }
        testSummaryWriter.writeFooter();
        testSummaryWriter.close();
    }

    /**
     * Sets a test summary writer. This method must be call before
     * serializeAll() is called, because this method relies on the presence of a
     * test summary writer.
     * 
     * @param testSummaryWriter
     *            a concrete implementation of <code>TestSummaryWriter</code>
     */
    public void setTestSummaryWriter(TestSummaryWriter testSummaryWriter) {
        this.testSummaryWriter = testSummaryWriter;
    }

    /**
     * Sets the content which was written to the standard output during a test
     * for a test.
     * 
     * @param directoryName
     *            the directory name of the test
     * @param stdOutFromTest
     *            the standard output content
     */
    public void setStdOut(String directoryName, String stdOutFromTest) {
        TestInfo testInfo = getTestInfoForDirectory(directoryName);
        testInfo.setStdOut(stdOutFromTest);
    }

    /**
     * Sets the content which was written to the standard error during a test
     * for a test.
     * 
     * @param directoryName
     *            the directory name of the test
     * @param stdErrFromTest
     *            the standard error content
     */
    public void setStdErr(String directoryName, String stdErrFromTest) {
        TestInfo testInfo = getTestInfoForDirectory(directoryName);
        testInfo.setStdErr(stdErrFromTest);
    }

    /**
     * Sets a test given its directory name as inconclusive and sets a
     * <code>AssumptionViolatedException</code>-object as cause for the verdict.
     * The directory name is used, because it uniquely defines the test, as
     * there is only one test per directory and it is encoded in display name of
     * the test. A test may be inconclusive if an assumption is violated,e.g. if
     * PDF-A conformance should be checked after signing and the input file does
     * not conform to the PDF-A standard then the test cannot perform any
     * meaningful checks.
     * 
     * @param directoryName
     *            the name of the directory of the test
     * @param e
     *            the cause of the fail
     */
    public void setInconclusive(String directoryName,
            AssumptionViolatedException e) {
        TestInfo testInfo = getTestInfoForDirectory(directoryName);
        testInfo.setVerdict(TestVerdict.INCONCLUSIVE);
        testInfo.setFailCause(e);
    }

}
