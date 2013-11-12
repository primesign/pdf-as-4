package at.gv.egiz.pdfas.web.helper;

import java.io.Serializable;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class PDFASSession implements Serializable {

	public static final String SESSION_TAG = "PDFASSession";
	
	private StatusRequest statusRequest;
	private SignParameter signParameter;
	private Configuration config;
	
	public PDFASSession(SignParameter parameter) {
		this.signParameter = parameter;
	}

	public StatusRequest getStatusRequest() {
		return statusRequest;
	}

	public void setStatusRequest(StatusRequest statusRequest) {
		this.statusRequest = statusRequest;
	}

	public SignParameter getSignParameter() {
		return signParameter;
	}

	public void setSignParameter(SignParameter signParameter) {
		this.signParameter = signParameter;
	}
	
}
