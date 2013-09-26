package at.gv.egiz.pdfas.lib.api;

import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public interface PdfAs {

	// Sign
	// Verify
	// Get Configuration
	
	/**
	 * Signs a PDF document using PDF-AS.
	 * 
	 * @param parameter
	 * @return
	 */
	public SignResult sign(SignParameter parameter) throws PdfAsException;
	
	/**
	 * Verifies a document with (potentially multiple) PDF-AS signatures.
	 *  
	 * @param parameter The verification parameter
	 * @return A list of verification Results
	 */
	public List<VerifyResult> verify(VerifyParameter parameter);
	
	/**
	 * Gets a copy of the PDF-AS configuration, to allow the application to 
	 * override configuration parameters at runtime.
	 * 
	 * @return A private copy of the pdf as configuration
	 */
	public Configuration getConfiguration();
}