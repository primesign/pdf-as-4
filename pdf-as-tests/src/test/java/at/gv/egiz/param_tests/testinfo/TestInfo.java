package at.gv.egiz.param_tests.testinfo;

import at.gv.egiz.param_tests.provider.BaseSignatureTestData;

/**
 * Abstract test information class containing information common to all tests.
 * It is declared abstract to enforce that a subclass of it must be created for
 * each test-type, because serializer prototypes are registered for
 * <code>TestInfo</code>-subclasses.
 * 
 * @author mtappler
 *
 */
public abstract class TestInfo {
    /**
     * signature test parameters common to all tests
     */
    private BaseSignatureTestData baseTestData;
    /**
     * the verdict of a test
     */
    private TestVerdict verdict = TestVerdict.UNKNOWN;
    /**
     * the cause for failure, non-null if the verdict is inconclusive or fail
     */
    private Throwable failCause;
    /**
     * standard output data written during the test
     */
    private String stdOut;
    /**
     * standard error data written during the test
     */
    private String stdErr;

    public void setBaseTestData(BaseSignatureTestData baseTestData) {
        this.baseTestData = baseTestData;
    }

    public Throwable getFailCause() {
        return failCause;
    }

    public BaseSignatureTestData getBaseTestData() {
        return this.baseTestData;
    }

    public TestVerdict getVerdict() {
        return verdict;
    }

    public String getStdOut() {
        return stdOut;
    }

    public void setVerdict(TestVerdict result) {
        this.verdict = result;
    }

    public void setFailCause(Throwable e) {
        this.failCause = e;
    }

    public void setStdOut(String stdOutFromTest) {
        this.stdOut = stdOutFromTest;
    }

    public void setStdErr(String stdErrFromTest) {
        this.stdErr = stdErrFromTest;
    }

    public String getStdErr() {
        return stdErr;
    }

}
