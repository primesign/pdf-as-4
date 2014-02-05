package at.gv.egiz.pdfas.lib.api.sign;

import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public interface SignParameter extends PdfAsParameter {
	
	/**
	 * Gets the signature profile to use
	 * @return
	 */
	public String getSignatureProfileId();

	/**
	 * Sets the signature profile to use
	 * 
	 * @param signatureProfileId The signature profile
	 */
	public void setSignatureProfileId(String signatureProfileId);

	/** 
	 * Gets the signature position
	 * @return
	 */
	public String getSignaturePosition();

	/**
	 * Sets the signature position
	 * @param signaturePosition The signature position string
	 */
	public void setSignaturePosition(String signaturePosition);

	/**
	 * Sets the data sink for the signature process
	 * @param output
	 */
	public void setOutput(DataSink output);
	
	/**
	 * Gets the data sink for the signature process
	 * @return
	 */
	public DataSink getOutput();
	
	/**
	 * Sets the signer to use
	 * 
	 * 
	 * @param signer
	 */
	public void setPlainSigner(IPlainSigner signer);
	
	/**
	 * Gets the signer to use.
	 * @return
	 */
	public IPlainSigner getPlainSigner();
}
