package at.gv.egiz.pdfas.wrapper;

import at.gv.egiz.pdfas.api.sign.SignParameters;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class SignParameterWrapper {

	private SignParameter signParameter4;
	private SignParameters signParameters;
	
	public SignParameterWrapper(SignParameters signParameters, SignParameter signParameter4) {
		this.signParameter4 = signParameter4;
		this.signParameters = signParameters;
	}
	
	private void syncOldToNew() {
		// TODO
	}
	
	private void syncNewToOld() {
		// TODO
	}
	
	
	public SignParameter getSignParameter4() {
		return this.signParameter4;
	}
	
	public SignParameters getSignParameters() {
		return this.signParameters;
	}
}
