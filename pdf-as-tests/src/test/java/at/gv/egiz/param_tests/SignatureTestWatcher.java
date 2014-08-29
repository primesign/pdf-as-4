package at.gv.egiz.param_tests;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import at.gv.egiz.param_tests.serialization.SerializiationManager;

/**
 * A test watcher, which provides some basic information about executed tests,
 * i.e. verdicts and console output. Basically a test watcher is some class,
 * which gets notified every time a test started or stopped.
 * 
 * @author mtappler
 *
 */
public class SignatureTestWatcher extends TestWatcher {
    @Override
    public Statement apply(Statement base, Description description) {
        return super.apply(base, description);
    }

    /**
     * This method tells the serialization manager that a test succeeded
     * 
     * @param description
     *            the description of the test, which contains the display name,
     *            which in turn contains the directory of the test, which
     *            uniquely identifies the test
     */
    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        SerializiationManager.getInstance().setSucceeded(
                extractDirectoryName(description.getDisplayName()));

    }

    /**
     * Helper method which extracts the directory name of a test given its
     * display name. By convention, the display name shall include directory
     * names enclosed in angle brackets.
     * 
     * @param displayName
     *            the name which is displayed for a test
     * @return the directory of the test
     */
    private String extractDirectoryName(String displayName) {
    	return displayName.substring(displayName.indexOf('[') + 1,
               displayName.lastIndexOf(']'));
    	
    	//return displayName.substring(displayName.indexOf('<') + 1,
        //       displayName.lastIndexOf('>'));
    }

    /**
     * This method tells the serialization manager that a test failed
     * 
     * @param description
     *            the description of the test, which contains the display name,
     *            which in turn contains the directory of the test, which
     *            uniquely identifies the test
     */
    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        SerializiationManager.getInstance().setFailed(
                extractDirectoryName(description.getDisplayName()), e);

    }

    /**
     * This method tells the serialization manager that a test was skipped,
     * because of a failed assumption.
     * 
     * @param description
     *            the description of the test, which contains the display name,
     *            which in turn contains the directory of the test, which
     *            uniquely identifies the test
     */
    protected void skipped(AssumptionViolatedException e,
            Description description) {
        SerializiationManager.getInstance().setInconclusive(
                extractDirectoryName(description.getDisplayName()), e);
    }

    /**
     * This method resets the logging of stdout and stderr, to be able to
     * retrieve it after the test has finished.
     * 
     * @param description
     *            description of a test which was just started
     */
    @Override
    protected void starting(Description description) {
        super.starting(description);
        ParameterizedSignatureTestSuite.getStdoutLog().reset();
        ParameterizedSignatureTestSuite.getStderrLog().reset();
    }

    /**
     * This method sets stdout and stderr, which was logged during the execution
     * of a test.
     * 
     * @param description
     *            the description of the test, which contains the display name,
     *            which in turn contains the directory of the test, which
     *            uniquely identifies the test
     */
    @Override
    protected void finished(Description description) {
        super.finished(description);
        SerializiationManager.getInstance().setStdOut(
                extractDirectoryName(description.getDisplayName()),
                ParameterizedSignatureTestSuite.getStdoutLog().toString());
        SerializiationManager.getInstance().setStdErr(
                extractDirectoryName(description.getDisplayName()),
                ParameterizedSignatureTestSuite.getStderrLog().toString());
    }
}