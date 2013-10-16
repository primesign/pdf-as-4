package at.gv.egiz.pdfas.lib.impl;

import java.security.cert.X509Certificate;

import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.SignaturePosition;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;

public class SignResultImpl implements SignResult {

	protected DataSink dataSink;
	protected X509Certificate certificate;
	protected SignaturePosition position;
	
	public SignResultImpl(DataSink dataSink) {
		this.dataSink = dataSink;
	}
	
	public DataSink getOutputDocument() {
		return this.dataSink;
	}

	public X509Certificate getSignerCertificate() {
		return this.certificate;
	}

	public SignaturePosition getSignaturePosition() {
		return this.position;
	}

	public void setSignerCertificate(X509Certificate certificate) {
		this.certificate = certificate;
	}

	public void setSignaturePosition(SignaturePosition position) {
		this.position = position;
	}

}
