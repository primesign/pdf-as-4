package at.gv.egiz.status.impl;

import at.gv.egiz.status.TestResult;

/**
 * The Class TestRunResult.
 */
public class TestRunResult {
	
	/** The test result. */
	private TestResult testResult;
	
	/** The execution timestamp. */
	private long executionTimestamp;

	/**
	 * Instantiates a new test run result.
	 */
	public TestRunResult() {
		this.init(null, 0);
	}

	/**
	 * Instantiates a new test run result.
	 *
	 * @param testResult the test result
	 */
	public TestRunResult(TestResult testResult) {
		this.init(testResult, 0);
	}

	/**
	 * Instantiates a new test run result.
	 *
	 * @param testResult the test result
	 * @param executionTimestamp the execution timestamp
	 */
	public TestRunResult(TestResult testResult, long executionTimestamp) {
		this.init(testResult, executionTimestamp);
	}
	
	/**
	 * Inits the.
	 *
	 * @param testResult the test result
	 * @param executionTimestamp the execution timestamp
	 */
	private void init(TestResult testResult, long executionTimestamp) {
		this.testResult = testResult;
		this.executionTimestamp = executionTimestamp;
	}

	/**
	 * Gets the test result.
	 *
	 * @return the test result
	 */
	public TestResult getTestResult() {
		return testResult;
	}

	/**
	 * Sets the test result.
	 *
	 * @param testResult the new test result
	 */
	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	/**
	 * Gets the execution timestamp.
	 *
	 * @return the execution timestamp
	 */
	public long getExecutionTimestamp() {
		return executionTimestamp;
	}

	/**
	 * Sets the execution timestamp.
	 *
	 * @param executionTimestamp the new execution timestamp
	 */
	public void setExecutionTimestamp(long executionTimestamp) {
		this.executionTimestamp = executionTimestamp;
	}
}
