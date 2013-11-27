package at.gv.egiz.pdfas.lib.impl.verify;

import at.gv.egiz.pdfas.lib.api.verify.SignatureCheck;

public class SignatureCheckImpl implements SignatureCheck {

	private int code;
	private String message;
	
	public SignatureCheckImpl(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
