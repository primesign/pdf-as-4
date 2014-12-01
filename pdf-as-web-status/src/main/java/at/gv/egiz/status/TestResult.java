package at.gv.egiz.status;

import java.util.List;

/**
 * The Interface TestResult.
 */
public interface TestResult {
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public TestStatus getStatus();
	
	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public List<String> getDetails();
}
