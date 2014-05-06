package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class PDFASSignRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5572093903422676582L;

	String requestID;
	byte[] inputData;
	PDFASSignParameters parameters;
	
	@XmlElement(required = true, nillable = false)
	public String getRequestID() {
		return requestID;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	@XmlElement(required = true, nillable = false)
	public byte[] getInputData() {
		return inputData;
	}
	
	public void setInputData(byte[] inputData) {
		this.inputData = inputData;
	}
	
	@XmlElement(required = true, nillable = false)
	public PDFASSignParameters getParameters() {
		return parameters;
	}
	
	public void setParameters(PDFASSignParameters parameters) {
		this.parameters = parameters;
	}
}
