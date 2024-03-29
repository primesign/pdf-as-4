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
package at.gv.egiz.pdfas.lib.api.sign;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import iaik.cms.ContentInfo;
import iaik.x509.X509Certificate;

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
	X509Certificate getCertificate(SignParameter parameter) throws PdfAsException;
	
	/**
	 * Sign the document
	 * @param input
	 * @param byteRange
	 * @return The <strong>encoded</strong> signature (e.g. the in the form of encoded CMS ContentInfo). (never {@code null}) 
	 * @throws PdfAsException
	 */
    byte[] sign(byte[] input, int[] byteRange, SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException;
    
    /**
     * Gets the PDF Subfilter for this signer
     * @return
     */
    String getPDFSubFilter();
    
    /**
     * Gets the PDF Filter for this signer
     * @return
     */
    String getPDFFilter();
    
	/**
	 * Retrieves certificate validation data (chains, OCSP responses, CRL...) required to support long term validation
	 * (LTV).
	 * 
	 * @param requestedSignature
	 *            The requested signature (required; must not be {@code null}).
	 * @return The verification data or {@code null} in case data could not be retrieved or LTV is not enabled.
	 * @throws PDFASError
	 *             Thrown in case of error.
	 * @throws IllegalStateException May be thrown in case {@code requestedSignature} does not provide a certificate.
	 */
    CertificateVerificationData getCertificateVerificationData(RequestedSignature requestedSignature) throws PDFASError;
	
	/**
	 * Uses individual pieces of information (like digest input data, signing certificate or the signing time), all of which
	 * go into creating a signature in order to create digest value, digest algorithm, signature algorithm and the
	 * {@link ExternalSignatureInfo#getSignatureObject() signature object}.
	 * 
	 * @param digestInputData    The digest input data (the data to be signed). (required; must not be {@code null}).
	 * @param signingCertificate The signing certificate. (required; must not be {@code null})
	 * @param signingTime        The assumed signing time. (required; must not be {@code null})
	 * @param enforceETSIPAdES   A flag indicating if the signature should enforce strict PAdES or not.
	 * @return The external signature info. (never {@code null})
	 * @throws PdfAsException Thrown in case of error.
	 */
    @Nonnull
	default ExternalSignatureInfo determineExternalSignatureInfo(@Nonnull byte[] digestInputData, @Nonnull X509Certificate signingCertificate, @Nonnull Date signingTime, boolean enforceETSIPAdES) throws PdfAsException {
		throw new UnsupportedOperationException(IPlainSigner.class.getSimpleName() + " implementation " + getClass().getName() + " does not support external signatures.");
	}

	/**
	 * Incorporates a plain signature value created by an external entity into the provided signature object (e.g.
	 * incorporating the signature value into an encoded ASN.1 {@link ContentInfo}).
	 * 
	 * @param externalSignatureValue The signature value. (required; must not be {@code null})
	 * @param signatureObject        The signature object. (required; must not be {@code null})
	 * @return The (updated) encoded signature object. (never {@code null})
	 * @throws PdfAsException Thrown in case of error.
	 */
    @Nonnull
	default byte[] applyPlainExternalSignatureValue(@Nonnull byte[] externalSignatureValue, @Nullable byte[] signatureObject) throws PdfAsException {
		throw new UnsupportedOperationException(IPlainSigner.class.getSimpleName() + " implementation " + getClass().getName() + " does not support external signatures.");
	}

}
