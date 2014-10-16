package at.gv.egiz.pdfas.lib.api.preprocessor;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;

/**
 * The Interface PreProcessor.
 */
public interface PreProcessor {
	
	/**
	 * Initialize this instance. This Method is called once when the 
	 * PreProcessor is constructed!
	 *
	 * @param configuration the configuration
	 */
	public void initialize(Configuration configuration);
	
	/**
	 * Sign. This Method is called once for each sign call.
	 *
	 * @param parameter the parameter
	 * @throws PDFASError the PDFAS error
	 */
	public void sign(SignParameter parameter) throws PDFASError;
	
	/**
	 * Verify. This Method is called once for each verify call.
	 *
	 * @param parameter the parameter
	 * @throws PDFASError the PDFAS error
	 */
	public void verify(VerifyParameter parameter) throws PDFASError;
	
	
	/**
	 * The Position in which the PreProcessor should be registered.
	 * Lowest positions will be executed first.
	 * 
	 * Negative values mean that PDF-AS chooses where to register the PreProcessor
	 * This should be used if the PreProcessor has no dependencies.
	 * 
	 * @return the int
	 */
	public int registrationPosition();
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();
}
