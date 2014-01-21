package at.gv.egiz.pdfas.lib.impl;

import java.util.Date;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;

public class VerifyParameterImpl extends PdfAsParameterImpl implements VerifyParameter {
	
	protected int which = - 1;
	
	protected Date verificationTime = null;
	
	public VerifyParameterImpl(Configuration configuration,
			DataSource dataSource) {
		super(configuration, dataSource);
	}

	public int getWhichSignature() {
		return which;
	}

	public void setWhichSignature(int which) {
		this.which = which;
	}

	public Date getVerificationTime() {
		return verificationTime;
	}

	public void setVerificationTime(Date verificationTime) {
		this.verificationTime = verificationTime;
	}
}
