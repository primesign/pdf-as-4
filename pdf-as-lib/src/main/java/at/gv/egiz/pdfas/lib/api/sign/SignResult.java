package at.gv.egiz.pdfas.lib.api.sign;

import java.security.cert.X509Certificate;

import at.gv.egiz.pdfas.lib.api.DataSink;

public interface SignResult {
	/**
	 * Returns the filled output data sink.
	 * 
	 * @return Returns the filled output data sink.
	 */
	public DataSink getOutputDocument();

	/**
	 * Returns the certificate of the signer.
	 * 
	 * @return Returns the certificate of the signer.
	 */
	public X509Certificate getSignerCertificate();

	/**
	 * Returns the position where the signature is finally placed.
	 * 
	 * <p>
	 * This information can be useful for post-processing the document.
	 * </p>
	 * 
	 * <p>
	 * Consult the PDF-AS documentation section Commandline for further
	 * information about positioning.
	 * </p>
	 * 
	 * @return Returns the position where the signature is finally placed. May
	 *         return null if no position information is available.
	 */
	//public SignaturePosition getSignaturePosition();
}
