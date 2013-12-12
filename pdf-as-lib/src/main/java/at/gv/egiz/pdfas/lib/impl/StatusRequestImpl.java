package at.gv.egiz.pdfas.lib.impl;

import iaik.x509.X509Certificate;

import java.security.cert.CertificateException;

import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;

public class StatusRequestImpl implements StatusRequest {

	private boolean needCertificate = false;
	private boolean needSignature = false;
	private boolean isReady = false;
	private X509Certificate certificate;
	private byte[] encodedSignature;
	private byte[] signatureData;
	private int[] byteRange;
	
	private OperationStatus status;
	
	public OperationStatus getStatus() {
		return status;
	}

	public void setStatus(OperationStatus status) {
		this.status = status;
	}

	public void setSignatureData(byte[] signatureData) {
		this.signatureData = signatureData;
	}

	public void setByteRange(int[] byteRange) {
		this.byteRange = byteRange;
	}

	public X509Certificate getCertificate() {
		return this.certificate;
	}
	
	public byte[] getSignature() {
		return this.encodedSignature;
	}
	
	public void setNeedSignature(boolean value) {
		this.needSignature = value;
	}
	
	public void setNeedCertificate(boolean value) {
		this.needCertificate = value;
	}
	
	public boolean needCertificate() {
		return needCertificate;
	}

	public boolean needSignature() {
		return needSignature;
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void setIsReady(boolean value) {
		this.isReady = value;
	}

	public byte[] getSignatureData() {
		return signatureData;
	}

	public int[] getSignatureDataByteRange() {
		return byteRange;
	}

	public void setCertificate(byte[] encodedCertificate) throws CertificateException {
		this.certificate = new X509Certificate(encodedCertificate);
	}

	public void setSigature(byte[] signatureValue) {
		this.encodedSignature = signatureValue;
	}
}
