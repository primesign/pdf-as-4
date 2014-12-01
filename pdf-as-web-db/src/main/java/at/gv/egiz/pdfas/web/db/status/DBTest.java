package at.gv.egiz.pdfas.web.db.status;

import java.util.ArrayList;
import java.util.List;

import at.gv.egiz.pdfas.web.store.DBRequestStore;
import at.gv.egiz.status.Test;
import at.gv.egiz.status.TestResult;
import at.gv.egiz.status.TestStatus;
import at.gv.egiz.status.impl.BaseTestResult;

public class DBTest implements Test {

	private DBRequestStore requestStore;
	
	public DBTest() {
		requestStore = new DBRequestStore();
	}
	
	@Override
	public String getName() {
		return "DB";
	}

	@Override
	public long getCacheDelay() {
		return 300000;
	}

	@Override
	public TestResult runTest() {
		BaseTestResult result = new BaseTestResult();
		try {
			this.requestStore.cleanOldRequestException();
			result.setStatus(TestStatus.OK);
		} catch(Throwable e) {
			result.setStatus(TestStatus.FAILED);
			List<String> details = new ArrayList<String>();
			details.add(e.getMessage());
			result.setDetails(details);
		}
		return result;
	}

}
