/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.lib.api.verify;

import java.security.cert.X509Certificate;
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
	
	/**
	 * Gets the signer certificate
	 * @return
	 */
	public X509Certificate getSignerCertificate();
	
	/**
	 * Gets the signed data for the signature
	 * @return
	 */
	public byte[] getSignatureData();
}
