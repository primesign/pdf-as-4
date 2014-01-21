package at.gv.egiz.pdfas.lib.api.verify;

import java.util.Date;

import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public interface VerifyParameter extends PdfAsParameter {
	
	public int getWhichSignature();
	
	public void setWhichSignature(int which);
	
	public Date getVerificationTime();
	
	public void setVerificationTime(Date verificationTime);
}
