package at.gv.egiz.pdfas.wrapper;

import at.gv.egiz.pdfas.api.verify.SignatureCheck;

public class SignatureCheckWrapper implements SignatureCheck {

	private at.gv.egiz.pdfas.lib.api.verify.SignatureCheck newCheck;
	
	public SignatureCheckWrapper(at.gv.egiz.pdfas.lib.api.verify.SignatureCheck newCheck) {
		this.newCheck = newCheck;
	}

	public int getCode() {
		return this.newCheck.getCode();
	}

	public String getMessage() {
		return this.newCheck.getMessage();
	}
	
}
