package at.gv.egiz.sl.util;

import at.gv.egiz.sl.schema.CreateCMSSignatureRequestType;

public class RequestPackage {
	private CreateCMSSignatureRequestType requestType;
	private byte[] signatureData;
	private int[] byteRange;
	
	public CreateCMSSignatureRequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(CreateCMSSignatureRequestType requestType) {
		this.requestType = requestType;
	}
	public byte[] getSignatureData() {
		return signatureData;
	}
	public void setSignatureData(byte[] signatureData) {
		this.signatureData = signatureData;
	}
	public int[] getByteRange() {
		return byteRange;
	}
	public void setByteRange(int[] byteRange) {
		this.byteRange = byteRange;
	}
	
	
}
