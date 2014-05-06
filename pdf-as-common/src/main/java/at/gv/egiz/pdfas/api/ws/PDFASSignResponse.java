package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class PDFASSignResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6369697640117556071L;
	
	String requestID;
	String error;
	byte[] signedPDF;
	
	@XmlElement(required = true, nillable = false)
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	@XmlElement(required = false, nillable = false)
	public byte[] getSignedPDF() {
		return signedPDF;
	}
	public void setSignedPDF(byte[] signedPDF) {
		this.signedPDF = signedPDF;
	}
	
	@XmlElement(required = false)
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
