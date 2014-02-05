package at.gv.egiz.pdfas.lib.api.verify;

import java.util.Date;

import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public interface VerifyParameter extends PdfAsParameter {

	/**
	 * Gets which signature should be verified 
	 * 
	 * This is a 0 based index of the signatures 
	 * @return
	 */
	public int getWhichSignature();
	
	/**
	 * Sets which signature should be verified
	 * 
	 * This is a 0 based index of the signatures 
	 * 
	 * @param which The index
	 */
	public void setWhichSignature(int which);
	
	/**
	 * Gets the verification time
	 * @return
	 */
	public Date getVerificationTime();
	
	/**
	 * Sets the verification time.
	 * 
	 * @param verificationTime
	 */
	public void setVerificationTime(Date verificationTime);
}
