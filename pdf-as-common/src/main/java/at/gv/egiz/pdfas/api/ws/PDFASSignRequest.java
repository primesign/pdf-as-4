package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SignRequest")
public class PDFASSignRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5572093903422676582L;

	String requestID;
	byte[] inputData;
	PDFASSignParameters parameters;
	
	@XmlElement(required = true, nillable = false, name="requestID")
	public String getRequestID() {
		return requestID;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	@XmlElement(required = true, nillable = false, name="inputData")
	public byte[] getInputData() {
		return inputData;
	}
	
	public void setInputData(byte[] inputData) {
		this.inputData = inputData;
	}
	
	@XmlElement(required = true, nillable = false, name="parameters")
	public PDFASSignParameters getParameters() {
		return parameters;
	}
	
	public void setParameters(PDFASSignParameters parameters) {
		this.parameters = parameters;
	}
}
