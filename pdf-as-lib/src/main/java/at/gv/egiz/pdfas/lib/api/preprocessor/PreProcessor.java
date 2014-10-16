package at.gv.egiz.pdfas.lib.api.preprocessor;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;

/**
 * The Interface PreProcessor.
 */
public interface PreProcessor {
	
	/**
	 * Sign.
	 *
	 * @param parameter the parameter
	 * @throws PDFASError the PDFAS error
	 */
	public void sign(SignParameter parameter) throws PDFASError;
	
	/**
	 * Verify.
	 *
	 * @param parameter the parameter
	 * @throws PDFASError the PDFAS error
	 */
	public void verify(VerifyParameter parameter) throws PDFASError;
	
	
	/**
	 * Registration position.
	 *
	 * @return the int
	 */
	public int registrationPosition();
}
