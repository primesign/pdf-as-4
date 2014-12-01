package at.gv.egiz.status.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.status.Test;
import at.gv.egiz.status.TestFactory;
import at.gv.egiz.status.TestResult;

/**
 * The Class TestManager.
 */
public class TestManager {

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(TestManager.class);

	/** The tests. */
	private Map<String, Test> tests = new HashMap<String, Test>();

	/** The test result cache. */
	private Map<String, TestRunResult> testResultCache = new HashMap<String, TestRunResult>();

	/** The loader. */
	private static ServiceLoader<TestFactory> loader = ServiceLoader
			.load(TestFactory.class);

	/**
	 * Instantiates a new test manager.
	 */
	public TestManager() {
		Iterator<TestFactory> factoryIterator = loader.iterator();

		while (factoryIterator.hasNext()) {
			TestFactory factory = factoryIterator.next();

			log.debug("Running Factory: " + factory.getClass().getName());

			List<Test> testList = factory.createTests();
			if (testList != null && !testList.isEmpty()) {
				log.debug("Factory: " + factory.getClass().getName()
						+ " produced " + testList.size() + " tests!");
				Iterator<Test> testIterator = testList.iterator();
				while (testIterator.hasNext()) {
					Test test = testIterator.next();
					log.debug("adding Test: " + test.getName() + " ["
							+ test.getClass().getName() + "]");
					tests.put(test.getName(), test);
				}
			} else {
				log.debug("Factory: " + factory.getClass().getName()
						+ " produced no tests!");
			}
		}
	}

	/**
	 * Store test result.
	 *
	 * @param testName the test name
	 * @param testResult the test result
	 * @return the test result
	 */
	private TestResult storeTestResult(String testName, TestResult testResult) {
		if(testResult != null) {
			TestRunResult runResult = new TestRunResult();
			runResult.setExecutionTimestamp(new Date().getTime());
			runResult.setTestResult(testResult);
			testResultCache.put(testName, runResult);
		}
		return testResult;
	}
 	
	/**
	 * Run test.
	 *
	 * @param testName the test name
	 * @return the test result
	 */
	public TestResult runTest(String testName) {
		if(tests.containsKey(testName)) {
			return storeTestResult(testName, tests.get(testName).runTest());
		} 
		log.debug("Not test \"" + testName + "\" available");
		return null;
	}
	
	/**
	 * Run test.
	 *
	 * @param testName the test name
	 * @param forceExecution the force execution
	 * @return the test result
	 */
	public TestResult runTest(String testName, boolean forceExecution) {
		if(!tests.containsKey(testName)) {
			log.debug("Not test \"" + testName + "\" available");
			return null;
		}
		
		if (forceExecution) {
			return runTest(testName);
		}
		
		Test test = tests.get(testName);
		
		if (testResultCache.containsKey(testName)) {
				//
			Date now = new Date();

			TestRunResult result = testResultCache.get(testName);
			long lastTest = result.getExecutionTimestamp();
			long delay = test.getCacheDelay();
			
			if (lastTest < now.getTime() - delay) {
				// Too old
				return runTest(testName);
			} else {
				// Cache is fine!
				return result.getTestResult();
			}
		}
		return runTest(testName);
	}

	/**
	 * Run all tests.
	 *
	 * @param forceExecution the force execution
	 * @return the map
	 */
	public Map<String, TestResult> runAllTests(boolean forceExecution) {
		Map<String, TestResult> results = new HashMap<String, TestResult>();
		Iterator<Test> testIterator = tests.values().iterator();
		while(testIterator.hasNext()) {
			Test test = testIterator.next();
			String testName = test.getName();
			results.put(testName, runTest(testName, forceExecution));
		}
		return results;
	}
}
