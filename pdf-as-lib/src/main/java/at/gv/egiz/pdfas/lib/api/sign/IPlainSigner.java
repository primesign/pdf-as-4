package at.gv.egiz.pdfas.lib.api.sign;

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

/**
 * Signer interface
 * 
 * PDF-AS uses an IPlainSigner instance to create the signature. Also custom IPlainSigner
 * may be used to sign PDF-AS documents.
 */
public interface IPlainSigner {
	
	/**
	 * Gets the signing certificate
	 * @return
	 * @throws PdfAsException
	 */
	public X509Certificate getCertificate() throws PdfAsException;
	
	/**
	 * Sign the document
	 * @param input
	 * @param byteRange
	 * @return
	 * @throws PdfAsException
	 */
    public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException;
    
    /**
     * Gets the PDF Subfilter for this signer
     * @return
     */
    public String getPDFSubFilter();
    
    /**
     * Gets the PDF Filter for this signer
     * @return
     */
    public String getPDFFilter();
}
