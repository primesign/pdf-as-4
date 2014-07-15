package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="VerifyRequest")
public class PDFASVerifyRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8159503247524085992L;

	String requestID;
	byte[] inputData;
	VerificationLevel verificationLevel;
	Integer sigIdx;
	
	@XmlElement(required = true, nillable = false, name="requestID")
	public String getRequestID() {
		return requestID;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	@XmlElement(required = false, nillable = true, name="verificationLevel")
	public VerificationLevel getVerificationLevel() {
		return verificationLevel;
	}
	
	public void setVerificationLevel(VerificationLevel verificationLevel) {
		this.verificationLevel = verificationLevel;
	}
	
	@XmlElement(required = true, nillable = false, name="inputData")
	public byte[] getInputData() {
		return inputData;
	}
	
	public void setInputData(byte[] inputData) {
		this.inputData = inputData;
	}
	
	@XmlElement(required = false, nillable = true, name="signatureIndex")
	public Integer getSignatureIndex() {
		return sigIdx;
	}
	
	public void setSignatureIndex(Integer sigIdx) {
		this.sigIdx = sigIdx;
	}
}
