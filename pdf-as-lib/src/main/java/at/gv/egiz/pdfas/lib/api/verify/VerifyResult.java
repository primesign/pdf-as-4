package at.gv.egiz.pdfas.lib.api.verify;

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

public interface VerifyResult {
	/**
	 * Returns if the verification was possible or could not even be startet.
	 * see {@link #getVerificationException()} for details.
	 * 
	 * @return
	 */
	public boolean isVerificationDone();

	/**
	 * Returns a verification exception if any. Shows that the verification
	 * could not be started. See {@link #isVerificationDone()}.
	 * 
	 * @return
	 */
	public PdfAsException getVerificationException();

	/**
	 * Returns the result of the certificate check.
	 * 
	 * @return Returns the result of the certificate check.
	 */
	public SignatureCheck getCertificateCheck();

	/**
	 * Returns the result of the value (and hash) check.
	 * 
	 * @return Returns the result of the value (and hash) check.
	 */
	public SignatureCheck getValueCheckCode();

	/**
	 * Returns the result of the manifest check.
	 * 
	 * @return Returns the result of the manifest check.
	 */
	public SignatureCheck getManifestCheckCode();

	/**
	 * Returns true, if the signer's certificate is a qualified certificate.
	 * 
	 * @return Returns true, if the signer's certificate is a qualified
	 *         certificate.
	 */
	public boolean isQualifiedCertificate();
	
	
	public X509Certificate getSignerCertificate();
}
