/*******************************************************************************
 * <copyright> Copyright 2017 by PrimeSign GmbH, Graz, Austria </copyright>
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
package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Hex;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter.LTVMode;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData.CertificateAndRevocationStatus;

/**
 * Provides support for enriching PAdES signatures with LTV related information.
 *
 * @author Thomas Knall, PrimeSign GmbH
 * @see <a href=
 *      "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS
 *      102 778-4 v1.1.2, Annex A, "LTV extensions"</a>
 * @see <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS
 *      103 172 V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
 *
 */
public class LTVEnabledPADESPDFBOXSigner extends PADESPDFBOXSigner {

	private Logger log = LoggerFactory.getLogger(LTVEnabledPADESPDFBOXSigner.class);
	
	private LTVSupport ltvSupport = new LTVSupportImpl();

	/**
	 * Creates log entries for each certificate with its revocation check result.
	 * 
	 * @param chainCertsWithRevocationStatus A collection of certificates with revocation check results (required; must not
	 *                                       be {@code null} but may be empty).
	 * @implNote In case of log level DEBUG the certificate's subject dn is shown, otherwise the certificate's fingerprint
	 *           is logged.
	 */
	private void logTrustStatusCheckResults(@Nonnull Collection<CertificateAndRevocationStatus> chainCertsWithRevocationStatus) {
		chainCertsWithRevocationStatus.forEach(certWithRevStatus -> {
			if (log.isDebugEnabled()) {
				log.debug("Revocation status of certificate with subject dn '{}': {}", certWithRevStatus.getCertificate().getSubjectX500Principal(), certWithRevStatus.getRevocationStatus());
			} else if (log.isInfoEnabled()) {
				try {
					String sha1FingerPrint = Hex.encodeHexString(MessageDigest.getInstance("SHA-1").digest(certWithRevStatus.getCertificate().getEncoded()));
					log.info("Revocation status of certificate with SHA-1 fingerprint '{}': {}", sha1FingerPrint, certWithRevStatus.getRevocationStatus());
				} catch (CertificateEncodingException | NoSuchAlgorithmException e) {
					// do nothing
				}
				
			}
		});
	}

	/**
	 * Evaluates the certificate check results considering the provided {@code ltvMode}.
	 * 
	 * @param certificateVerificationData The certificate verification data (required; must not be {@code null}).
	 * @param ltvMode                     The LTV mode (required; must not be {@code null}).
	 * @throws PDFASError Throws an appropriate error in case the revocation status is not accepted.
	 */
	private void assessCertificateVerificationData(@Nonnull CertificateVerificationData certificateVerificationData, @Nonnull LTVMode ltvMode) throws PDFASError {
		for (CertificateAndRevocationStatus certAndRevStatus : certificateVerificationData.getChainCertsWithRevocationStatus()) {
			assess(certAndRevStatus, ltvMode);
		}
	}

	/**
	 * Evaluates a certificate with its revocation status considering the provided {@code ltvMode}.
	 * 
	 * @param certAndRevStatus The certificate with its revocation status. (required; must not be {@code null}).
	 * @param ltvMode          The LTV mode (required; must not be {@code null}).
	 * @throws PDFASError Throws an appropriate error in case the revocation status is not accepted.
	 */
	private void assess(@Nonnull CertificateAndRevocationStatus certAndRevStatus, @Nonnull LTVMode ltvMode) throws PDFASError {
		switch (certAndRevStatus.getRevocationStatus()) {
		case GOOD:
		case NOT_CHECKED:
			// ok
			break;
		case CHECK_FAILED:
			final String message = "Revocation status of certificate with subject DN '" + certAndRevStatus.getCertificate().getSubjectDN() + "' could not be determined.";
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_RETRIEVING_REQUIRED_DATA, message);
			}
			log.warn(message);
			break;
		default:
			throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_CERT_STATUS_NOT_VALID, "Revocation status of certificate with subject DN '"
					+ certAndRevStatus.getCertificate().getSubjectDN() + "' is " + certAndRevStatus.getRevocationStatus());
		}

	}

	@Override
	public void applyFilter(PDDocument pdDocument, RequestedSignature requestedSignature) throws PDFASError {

		// LTV mode controls if and how retrieval/embedding LTV data will be done
		LTVMode ltvMode = requestedSignature.getStatus().getSignParamter().getLTVMode();
		log.trace("LTV mode: {}", ltvMode);

		if (ltvMode == LTVMode.NONE) {
			// no need to determine and add any certificate verification data
			log.debug("Did not add LTV related data since LTV mode was {}.", ltvMode);
			return;
		}

		// we need to consider certificate verification data
		CertificateVerificationData ltvVerificationInfo = requestedSignature.getCertificateVerificationData();
		if (ltvVerificationInfo == null) {
			// we do not have any certificate verification data, LTV mode controls how this case is handled
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(
						ErrorConstants.ERROR_SIG_PADESLTV_NO_DATA,
						"No LTV data available for the signer's certificate while LTV-enabled signatures are required. Make sure appropriate verification data providers are available.");
			}
			
			// certificate verification data is not required
			log.debug("No LTV data available for the signer's certificate.");
			return;
		}

		logTrustStatusCheckResults(ltvVerificationInfo.getChainCertsWithRevocationStatus());
		
		assessCertificateVerificationData(ltvVerificationInfo, ltvMode);
		
		// we have data that can be added
		try {
			
			ltvSupport.addLTVInfo(pdDocument, ltvVerificationInfo);
			
		} catch (IOException e) {
			// we do not supress I/O errors (regardless of LTV mode)
			throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_IO_ADDING_DATA_TO_PDF, "I/O error adding LTV data to pdf document.", e);
			
		} catch (CertificateEncodingException | CRLException e) {
			// error embedding LTV data, LTV mode controls how errors are handled
			final String message = "Unable to encode LTV related data to be added to the document.";
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_INTERNAL_ADDING_DATA_TO_PDF, message, e);
			}
			log.warn(message, e);
			
		} catch (Exception e) {
			// catch all other errors
			final String message = "Unable to add LTV related data to the document.";
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_INTERNAL_ADDING_DATA_TO_PDF, message, e);
			}
			log.warn(message, e);
		}

	}

}
