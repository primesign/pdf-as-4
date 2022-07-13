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
package at.gv.egiz.pdfas.lib.api;

import java.security.cert.CertificateException;

import javax.annotation.Nullable;

import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

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
	boolean needCertificate();
	
	/**
	 * If true PDF-AS requires a the CAdES signature
	 * 
	 * use getSignatureData() and getSignatureDataByteRange() to retrieve the
	 * data to be signed and set the signature via setSigature
	 * 
	 * @return
	 */
	boolean needSignature();
	
	/**
	 * If true finishSign in PdfAs can be called to retrieve the signed pdf
	 * @return
	 */
	boolean isReady();
	
	/**
	 * Gets the data to be signed
	 * @return
	 */
	byte[] getSignatureData();
	
	/**
	 * Gets the byte range of the data to be signed
	 * @return
	 */
	int[] getSignatureDataByteRange();
	
	/**
	 * Sets the signing certificate
	 * @param encodedCertificate
	 * @throws CertificateException
	 */
	void setCertificate(byte[] encodedCertificate) throws CertificateException;
	
	/**
	 * Sets the encoded signature.
	 * 
	 * @param encodedSignature The encoded signature.
	 * @deprecated Use {@link #setEncodedSignature(byte[])} instead.
	 */
	void setSigature(byte[] encodedSignature) ;
	
	SignParameter getSignParameter();
	
	/**
	 * Sets the encoded signature.
	 * 
	 * @param encodedSignature The encoded signature. (optional; may be {@code null})
	 */
	default void setEncodedSignature(@Nullable byte[] encodedSignature) {
		setSigature(encodedSignature);
	}
	
}
