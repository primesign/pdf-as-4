package at.gv.egiz.status.impl;

import java.util.ArrayList;
import java.util.List;

import at.gv.egiz.status.TestResult;
import at.gv.egiz.status.TestStatus;

/**
 * The Class BaseTestResult.
 */
public class BaseTestResult implements TestResult {

	/** The status. */
	private TestStatus status;
	
	/** The details. */
	private List<String> details;
	
	/**
	 * Instantiates a new base test result.
	 */
	public BaseTestResult() {
		this.init(TestStatus.INDETERMINATE, new ArrayList<String>());
	}
	
	/**
	 * Instantiates a new base test result.
	 *
	 * @param status the status
	 */
	public BaseTestResult(TestStatus status) {
		this.init(status, new ArrayList<String>());
	}
	
	/**
	 * Instantiates a new base test result.
	 *
	 * @param status the status
	 * @param details the details
	 */
	public BaseTestResult(TestStatus status, List<String> details) {
		this.init(status, details);
	}
	
	/**
	 * Inits the.
	 *
	 * @param status the status
	 * @param details the details
	 */
	private void init(TestStatus status, List<String> details) {
		this.status = status;
		this.details = details;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(TestStatus status) {
		this.status = status;
	}

	/**
	 * Sets the details.
	 *
	 * @param details the new details
	 */
	public void setDetails(List<String> details) {
		this.details = details;
	}

	/* (non-Javadoc)
	 * @see at.gv.egiz.status.TestResult#getStatus()
	 */
	@Override
	public TestStatus getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see at.gv.egiz.status.TestResult#getDetails()
	 */
	@Override
	public List<String> getDetails() {
		return details;
	}

}
