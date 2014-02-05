package at.gv.egiz.pdfas.lib.api;

import java.security.cert.CertificateException;

/**
 * Status of a signture process
 */
public interface StatusRequest {
	
	/**
	 * If true PDF-AS requires the signature certificate
	 * 
	 * Retrieve the signing certificate and set it via setCertificate
	 * @return
	 */
	public boolean needCertificate();
	
	/**
	 * If true PDF-AS requires a the CAdES signature
	 * 
	 * use getSignatureData() and getSignatureDataByteRange() to retrieve the
	 * data to be signed and set the signature via setSigature
	 * 
	 * @return
	 */
	public boolean needSignature();
	
	/**
	 * If true finishSign in PdfAs can be called to retrieve the signed pdf
	 * @return
	 */
	public boolean isReady();
	
	/**
	 * Gets the data to be signed
	 * @return
	 */
	public byte[] getSignatureData();
	
	/**
	 * Gets the byte range of the data to be signed
	 * @return
	 */
	public int[] getSignatureDataByteRange();
	
	/**
	 * Sets the signing certificate
	 * @param encodedCertificate
	 * @throws CertificateException
	 */
	public void setCertificate(byte[] encodedCertificate) throws CertificateException;
	
	/**
	 * Sets the signature
	 * @param signatureValue
	 */
	public void setSigature(byte[] signatureValue) ;
	
}
