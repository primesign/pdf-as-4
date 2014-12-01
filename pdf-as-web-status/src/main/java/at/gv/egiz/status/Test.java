package at.gv.egiz.status;

/**
 * The Interface Test.
 */
public interface Test {
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the cache delay.
	 *
	 * @return the cache delay
	 */
	public long getCacheDelay();
	
	/**
	 * Run test.
	 *
	 * @return the test result
	 */
	public TestResult runTest();
}
