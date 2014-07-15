package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="VerifyResult")
public class PDFASVerifyResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1984406533559692943L;

	String requestID;
	int signatureIndex;
	boolean processed;
	String signedBy;
	int certificateCode;
	String certificateMessage;
	int valueCode;
	String valueMessage;
	String error;
	String certificate;
	String signedData;
	
	@XmlElement(required = true, nillable = false, name="requestID")
	public String getRequestID() {
		return requestID;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	@XmlElement(required = true, nillable = false, name="processed")
	public boolean getProcessed() {
		return processed;
	}
	
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	@XmlElement(required = true, nillable = false, name="signatureIndex")
	public int getSignatureIndex() {
		return signatureIndex;
	}
	
	public void setSignatureIndex(int signatureIndex) {
		this.signatureIndex = signatureIndex;
	}
	
	@XmlElement(required = true, nillable = false, name="signedBy")
	public String getSignedBy() {
		return signedBy;
	}
	
	public void setSignedBy(String signedBy) {
		this.signedBy = signedBy;
	}
	
	@XmlElement(required = true, nillable = false, name="certificateCode")
	public int getCertificateCode() {
		return certificateCode;
	}
	
	public void setCertificateCode(int certificateCode) {
		this.certificateCode = certificateCode;
	}
	
	@XmlElement(required = true, nillable = false, name="certificateMessage")
	public String getCertificateMessage() {
		return certificateMessage;
	}
	
	public void setCertificateMessage(String certificateMessage) {
		this.certificateMessage = certificateMessage;
	}
	
	@XmlElement(required = true, nillable = false, name="valueCode")
	public int getValueCode() {
		return valueCode;
	}
	
	public void setValueCode(int valueCode) {
		this.valueCode = valueCode;
	}
	
	@XmlElement(required = true, nillable = false, name="valueMessage")
	public String getValueMessage() {
		return valueMessage;
	}
	
	public void setValueMessage(String valueMessage) {
		this.valueMessage = valueMessage;
	}
	
	@XmlElement(required = true, nillable = false, name="error")
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	@XmlElement(required = true, nillable = false, name="certificate")
	public String getCertificate() {
		return certificate;
	}
	
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	
	@XmlElement(required = true, nillable = false, name="signedData")
	public String getSignedData() {
		return signedData;
	}
	
	public void setSignedData(String signedData) {
		this.signedData = signedData;
	}
}
