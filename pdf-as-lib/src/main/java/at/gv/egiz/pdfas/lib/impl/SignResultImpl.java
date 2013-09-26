package at.gv.egiz.pdfas.lib.impl;

import java.security.cert.X509Certificate;

import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;

public class SignResultImpl implements SignResult {

	protected DataSink dataSink;
	
	public SignResultImpl(DataSink dataSink) {
		this.dataSink = dataSink;
	}
	
	public DataSink getOutputDocument() {
		return this.dataSink;
	}

	public X509Certificate getSignerCertificate() {
		// TODO Auto-generated method stub
		return null;
	}

}
