package at.gv.egiz.pdfas.lib.api;

import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public interface PdfAs {	
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
	public List<VerifyResult> verify(VerifyParameter parameter) throws PdfAsException;
	
	/**
	 * Gets a copy of the PDF-AS configuration, to allow the application to 
	 * override configuration parameters at runtime.
	 * 
	 * @return A private copy of the pdf as configuration
	 */
	public Configuration getConfiguration();
	
	/**
	 * Starts a signature process
	 * 
	 * After the process has to be startet the status request has to be services by the user application
	 * 
	 * @param parameter The sign parameter
	 * @return A status request
	 * @throws PdfAsException
	 */
	public StatusRequest startSign(SignParameter parameter) throws PdfAsException;
	
	/**
	 * Continues an ongoing signature process 
	 * 
	 * @param statusRequest The current status
	 * @return A status request
	 * @throws PdfAsException
	 */
	public StatusRequest process(StatusRequest statusRequest) throws PdfAsException;
	
	/**
	 * Finishes a signature process
	 * 
	 * @param statusRequest The current status
	 * @return A signature result
	 * @throws PdfAsException
	 */
	public SignResult    finishSign(StatusRequest statusRequest) throws PdfAsException;
}
