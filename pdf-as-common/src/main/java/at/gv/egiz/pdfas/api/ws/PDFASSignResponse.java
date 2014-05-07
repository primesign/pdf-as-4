package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SignResponse")
public class PDFASSignResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6369697640117556071L;
	
	String requestID;
	String error;
	byte[] signedPDF;
	
	@XmlElement(required = true, nillable = false, name="requestID")
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	@XmlElement(required = false, nillable = false, name="signedPDF")
	public byte[] getSignedPDF() {
		return signedPDF;
	}
	public void setSignedPDF(byte[] signedPDF) {
		this.signedPDF = signedPDF;
	}
	
	@XmlElement(required = false, name="error")
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
