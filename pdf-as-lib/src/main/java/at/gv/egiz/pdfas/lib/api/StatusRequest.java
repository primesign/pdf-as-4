package at.gv.egiz.pdfas.lib.api;

import java.security.cert.CertificateException;

public interface StatusRequest {
	
	public boolean needCertificate();
	public boolean needSignature();
	public boolean isReady();
	
	public byte[] getSignatureData();
	public int[] getSignatureDataByteRange();
	public void setCertificate(byte[] encodedCertificate) throws CertificateException;
	public void setSigature(byte[] signatureValue) ;
	
}
