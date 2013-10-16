package at.gv.egiz.pdfas.wrapper;

import java.util.List;

import at.gv.egiz.pdfas.api.verify.VerifyResult;
import at.gv.egiz.pdfas.api.verify.VerifyResults;

public class VerifyResultsImpl implements VerifyResults {

	private List<VerifyResult> list;
	
	public VerifyResultsImpl(List<VerifyResult> list) {
		this.list = list;
	}
	
	public List getResults() {
		return this.list;
	}

}
