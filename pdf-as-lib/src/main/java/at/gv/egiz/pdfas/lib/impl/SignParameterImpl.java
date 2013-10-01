package at.gv.egiz.pdfas.lib.impl;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class SignParameterImpl extends PdfAsParameterImpl implements SignParameter {
	protected String signatureProfileId = null;
	protected String signaturePosition = null;
	protected DataSink output = null;
	protected IPlainSigner signer = null;
	
	public SignParameterImpl(Configuration configuration, 
			DataSource dataSource) {
		super(configuration, dataSource);
	}

	// ========================================================================
	
	public String getSignatureProfileId() {
		return signatureProfileId;
	}

	public void setSignatureProfileId(String signatureProfileId) {
		this.signatureProfileId = signatureProfileId;
	}

	public String getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(String signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	public void setOutput(DataSink output) {
		this.output = output;
	}

	public DataSink getOutput() {
		return this.output;
	}

	public void setPlainSigner(IPlainSigner signer) {
		this.signer = signer;
	}
	
	public IPlainSigner getPlainSigner() {
		return this.signer;
	}
	
}
