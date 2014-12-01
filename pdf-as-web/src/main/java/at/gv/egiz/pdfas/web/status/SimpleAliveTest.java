package at.gv.egiz.pdfas.web.status;

import at.gv.egiz.status.Test;
import at.gv.egiz.status.TestResult;
import at.gv.egiz.status.TestStatus;
import at.gv.egiz.status.impl.BaseTestResult;

public class SimpleAliveTest implements Test {

	@Override
	public String getName() {
		return "ALIVE";
	}

	@Override
	public long getCacheDelay() {
		return 0;
	}

	@Override
	public TestResult runTest() {
		BaseTestResult result = new BaseTestResult();
		result.setStatus(TestStatus.OK);
		return result;
	}

}
