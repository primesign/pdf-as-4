package at.gv.egiz.param_tests.serialization;

import at.gv.egiz.param_tests.testinfo.TestInfo;

/**
 * interface defining methods for a test summary writer. This shall be
 * implemented in a technology-dependent way, like for HTML.
 * 
 * @author mtappler
 *
 */
public interface TestSummaryWriter {

    /**
     * This method shall write a header to a file.
     */
    public void writeHeader();

    /**
     * This method shall write a short summary of a test to a file.
     * 
     * @param tInfo
     *            test information for the test
     * @param testType
     *            the type of the test
     */
    public void writeSummaryOfTest(TestInfo tInfo, String testType);

    /**
     * This method shall write a footer to the file.
     */
    public void writeFooter();

    /**
     * This method shall initialize the writing process, e.g. by creating and
     * opening a file, to which the summary is written.
     */
    public void init();

    /**
     * This method shall terminate the writing process, e.g. by closing the
     * summary file.
     */
    public void close();
}
