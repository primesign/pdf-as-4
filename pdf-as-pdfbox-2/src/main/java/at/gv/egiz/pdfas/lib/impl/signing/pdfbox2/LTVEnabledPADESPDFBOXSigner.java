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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDStream;
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
 *      "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">PAdES
 *      ETSI TS 102 778-4 v1.1.2, Annex A, "LTV extensions"</a>
 *
 */
public class LTVEnabledPADESPDFBOXSigner extends PADESPDFBOXSigner {

	private Logger log = LoggerFactory.getLogger(LTVEnabledPADESPDFBOXSigner.class);

	/**
	 * Adds previously collected LTV verification data to the provided pdf document.
	 *
	 * @param pdDocument
	 *            The pdf document (required; must not be {@code null}).
	 * @param ltvVerificationInfo
	 *            The certificate verification info data (required; must not be {@code null}).
	 * @throws CertificateEncodingException
	 *             In case of an error with certificate encoding.
	 * @throws CRLException
	 *             In case there was an error encoding CRL data.
	 * @throws IOException
	 *             In case there was an error adding a pdf stream to the document.
	 */
	private void addLTVInfo(PDDocument pdDocument, CertificateVerificationData ltvVerificationInfo) throws CertificateEncodingException, CRLException, IOException {
		
		// expect at least the certificate(s)
		if (CollectionUtils.isEmpty(Objects.requireNonNull(ltvVerificationInfo).getChainCerts())) {
			throw new IllegalStateException("LTV data has not been retrieved yet. At least the signer certificate's chain is must be provided.");
		}
		
		log.debug("Adding LTV info to document.");
		addDSS(Objects.requireNonNull(pdDocument), ltvVerificationInfo);
		
		// DSS reflects an extension to ISO 32000-1:2008 (PDF-1.7), so the document should be labeled as 1.7+ document
		if (pdDocument.getVersion() < 1.7f) {
			if (log.isDebugEnabled()) {
				log.debug("Updating pdf version: {} -> 1.7", pdDocument.getVersion());
			}
			pdDocument.setVersion(1.7f);
		}
		
		// announce that an extension to PDF-1.7 has been added
		addOrUpdateExtensions(pdDocument);
		
		if (CollectionUtils.isNotEmpty(ltvVerificationInfo.getCRLs()) || CollectionUtils.isNotEmpty(ltvVerificationInfo.getEncodedOCSPResponses())) {
			log.info("LTV data (certchain and revocation info) added to document.");
		} else {
			log.info("LTV data (certchain but no revocation info) added to document.");
		}
	}

	/**
	 * Adds the DSS dictionary as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">PAdES
	 * ETSI TS 102 778-4 v1.1.2, Annex A, "LTV extensions"</a>.
	 *
	 * @param pdDocument
	 *            The pdf document (required; must not be {@code null}).
	 * @param ltvVerificationInfo
	 *            The certificate verification info data (required; must not be {@code null}).
	 * @throws CertificateEncodingException
	 *             In case of an error encoding certificates.
	 * @throws IOException
	 *             In case there was an error adding a pdf stream to the document.
	 * @throws CRLException
	 *             In case there was an error encoding CRL data.
	 */
	private void addDSS(PDDocument pdDocument, CertificateVerificationData ltvVerificationInfo) throws CertificateEncodingException, IOException, CRLException {
		final COSName COSNAME_DSS = COSName.getPDFName("DSS");
		PDDocumentCatalog root = Objects.requireNonNull(pdDocument).getDocumentCatalog();
		COSDictionary dssDictionary = (COSDictionary) root.getCOSObject().getDictionaryObject(COSNAME_DSS);
		if (dssDictionary == null) {
			log.trace("Adding new DSS dictionary.");
			// add new DSS dictionary
			dssDictionary = new COSDictionary();
			root.getCOSObject().setItem(COSNAME_DSS, dssDictionary);
			root.getCOSObject().setNeedToBeUpdated(true);
		}
		dssDictionary.setNeedToBeUpdated(true);

		// DSS/Certs
		addDSSCerts(pdDocument, dssDictionary, ltvVerificationInfo.getChainCerts());

		// DSS/OCSPs
		if (CollectionUtils.isNotEmpty(ltvVerificationInfo.getEncodedOCSPResponses())) {
			addDSSOCSPs(pdDocument, dssDictionary, ltvVerificationInfo.getEncodedOCSPResponses());
		}

		// DSS/CRLs
		if (CollectionUtils.isNotEmpty(ltvVerificationInfo.getCRLs())) {
			addDSSCRLs(pdDocument, dssDictionary, ltvVerificationInfo.getCRLs());
		}
	}
	
	/**
	 * Adds an /Extensions dictionary (if not already present) and adds the Adobe extension (/ADBE) announcing use of DSS
	 * dictionary.
	 * 
	 * @param pdDocument The pdf document. (required; must not be {@code null}).
	 */
	private void addOrUpdateExtensions(PDDocument pdDocument) {
		
		final COSName COSNAME_EXTENSIONS = COSName.getPDFName("Extensions");
		PDDocumentCatalog root = Objects.requireNonNull(pdDocument).getDocumentCatalog();
		
		COSDictionary extDictionary = (COSDictionary) root.getCOSObject().getDictionaryObject(COSNAME_EXTENSIONS);
		if (extDictionary == null) {
			log.trace("Adding new Extensions dictionary.");
			// add new Extensions dictionary
			extDictionary = new COSDictionary();
			extDictionary.setDirect(true);
			root.getCOSObject().setItem(COSNAME_EXTENSIONS, extDictionary);
			root.getCOSObject().setNeedToBeUpdated(true);
		}
		
		addADBEExtension(extDictionary);
	}

	/**
	 * Adds the /ADBE dictionary to the provided extensions dictionary (if not already present) in order to announce
	 * DSS extension to ISO32000-1:2008.
	 * 
	 * @param extDictionary The extension dictionary. (required; must not be {@code null})
	 */
	private void addADBEExtension(COSDictionary extDictionary) {
		
		final COSName COSNAME_ADBE = COSName.getPDFName("ADBE");
		
		COSDictionary adbeDictionary = (COSDictionary) Objects.requireNonNull(extDictionary).getDictionaryObject(COSNAME_ADBE);
		if (adbeDictionary == null) {
			log.trace("Adding new ADBE extensions dictionary.");
			// add new ADBE dictionary
			adbeDictionary = new COSDictionary();
			adbeDictionary.setDirect(true);
			extDictionary.setItem(COSNAME_ADBE, adbeDictionary);
			extDictionary.setNeedToBeUpdated(true);
		}
		
		final COSName VERSION_1_7 = COSName.getPDFName("1.7");
		// only set entry if not already set
		if (!VERSION_1_7.equals(adbeDictionary.getItem("BaseVersion"))) {
			adbeDictionary.setItem("BaseVersion", VERSION_1_7);
			adbeDictionary.setNeedToBeUpdated(true);
		}
		// only set if not already set
		if (adbeDictionary.getInt("ExtensionLevel") != 5) {
			adbeDictionary.setInt("ExtensionLevel", 5);
			adbeDictionary.setNeedToBeUpdated(true);
		}
		
	}

	/**
	 * Adds the "Certs" dictionary to DSS dictionary as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">PAdES
	 * ETSI TS 102 778-4 v1.1.2, Annex A, "LTV extensions"</a>.
	 *
	 * @param pdDocument
	 *            The pdf document (required; must not be {@code null}).
	 * @param dssDictionary
	 *            The DSS dictionary (required; must not be {@code null}).
	 * @param certificates
	 *            The certificates (required; must not be {@code null}).
	 * @throws IOException
	 *             In case there was an error adding a pdf stream to the document.
	 * @throws CertificateEncodingException
	 *             In case of an error encoding certificates.
	 */
	private void addDSSCerts(PDDocument pdDocument, COSDictionary dssDictionary, Iterable<X509Certificate> certificates) throws IOException, CertificateEncodingException {
		final COSName COSNAME_CERTS = COSName.getPDFName("Certs");
		COSArray certsArray = (COSArray) Objects.requireNonNull(dssDictionary).getDictionaryObject(COSNAME_CERTS);
		if (certsArray == null) {
			// add new "Certs" array
			log.trace("Adding new DSS/Certs dictionary.");
			// "An array of (indirect references to) streams, each containing one BER-encoded X.509 certificate (see RFC 5280 [7])"
			certsArray = new COSArray();
			dssDictionary.setItem(COSNAME_CERTS, certsArray);
		}
		certsArray.setNeedToBeUpdated(true);

		// add BER-encoded X.509 certificates
		log.trace("Adding certificates to DSS/Certs dictionary.");
		for (X509Certificate certificate : certificates) {
			log.trace("Adding certificate for subject: {}", certificate.getSubjectDN());
			try (InputStream in = new ByteArrayInputStream(certificate.getEncoded())) {
				PDStream pdStream = new PDStream(pdDocument, in);
				pdStream.addCompression();
				certsArray.add(pdStream);
			}
		}
	}

	/**
	 * Adds the "OCSPs" dictionary to DSS dictionary as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">PAdES
	 * ETSI TS 102 778-4 v1.1.2, Annex A, "LTV extensions"</a>.
	 *
	 * @param pdDocument
	 *            The pdf document (required; must not be {@code null}).
	 * @param dssDictionary
	 *            The DSS dictionary (required; must not be {@code null}).
	 * @param encodedOcspResponses
	 *            The encoded OCSP responses (required; must not be {@code null}).
	 * @throws IOException
	 *             In case there was an error adding a pdf stream to the document.
	 */
	private void addDSSOCSPs(PDDocument pdDocument, COSDictionary dssDictionary, Iterable<byte[]> encodedOcspResponses) throws IOException {
		final COSName COSNAME_OCSPS = COSName.getPDFName("OCSPs");
		COSArray ocspssArray = (COSArray) Objects.requireNonNull(dssDictionary).getDictionaryObject(COSNAME_OCSPS);
		if (ocspssArray == null) {
			log.trace("Adding new DSS/OCSPs dictionary.");
			// add "OCSPs" array
			// "An array of (indirect references to) streams, each containing a BER-encoded Online Certificate Status Protocol (OCSP) response (see RFC 2560 [8])."
			ocspssArray = new COSArray();
			dssDictionary.setItem(COSNAME_OCSPS, ocspssArray);
		}
		ocspssArray.setNeedToBeUpdated(true);

		for (byte[] encodedOcspResponse : encodedOcspResponses) {
			try (InputStream in = new ByteArrayInputStream(encodedOcspResponse)) {
				PDStream pdStream = new PDStream(pdDocument, in);
				pdStream.addCompression();
				ocspssArray.add(pdStream);
			}
		}
	}

	/**
	 * Adds the "CRLs" dictionary to DSS dictionary as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">PAdES
	 * ETSI TS 102 778-4 v1.1.2, Annex A, "LTV extensions"</a>.
	 *
	 * @param pdDocument
	 *            The pdf document (required; must not be {@code null}).
	 * @param dssDictionary
	 *            The DSS dictionary (required; must not be {@code null}).
	 * @param crls
	 *            The CRLs (required; must not be {@code null}).
	 * @throws IOException
	 *             In case there was an error adding a pdf stream to the document.
	 * @throws CRLException
	 *             In case there was an error encoding CRL data.
	 */
	private void addDSSCRLs(PDDocument pdDocument, COSDictionary dssDictionary, Iterable<X509CRL> crls) throws IOException, CRLException {
		final COSName COSNAME_CRLS = COSName.getPDFName("CRLs");
		COSArray crlsArray = (COSArray) Objects.requireNonNull(dssDictionary).getDictionaryObject(COSNAME_CRLS);
		if (crlsArray == null) {
			log.trace("Adding new DSS/CRLs dictionary.");
			// add "CRLs" array
			// "An array of (indirect references to) streams, each containing a BER-encoded Certificate Revocation List (CRL) (see RFC 5280 [7])."
			crlsArray = new COSArray();
			dssDictionary.setItem(COSNAME_CRLS, crlsArray);
		}
		crlsArray.setNeedToBeUpdated(true);

		for (X509CRL crl : crls) {
			try (InputStream in = new ByteArrayInputStream(crl.getEncoded())) {
				PDStream pdStream = new PDStream(pdDocument, in);
				pdStream.addCompression();
				crlsArray.add(pdStream);
			}
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
			
			addLTVInfo(pdDocument, ltvVerificationInfo);
			
		} catch (CertificateEncodingException | CRLException e) {
			// error embedding LTV data, LTV mode controls how errors are handled
			final String message = "Unable to encode LTV related data to be added to the document.";
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_INTERNAL_ADDING_DATA_TO_PDF, message, e);
			}
			log.warn(message, e);
		} catch (IOException e) {
			// we do not supress I/O errors (regardless of LTV mode)
			throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_IO_ADDING_DATA_TO_PDF, "I/O error adding LTV data to pdf document.", e);
		}

	}
	
	/**
	 * Creates log entries for each certificate with its revocation check result.
	 * 
	 * @param chainCertsWithRevocationStatus A collection of certificates with revocation check results (required; must not
	 *                                       be {@code null} but may be empty).
	 * @implNote In case of log level DEBUG the certificate's subject dn is shown, otherwise the certificate's fingerprint
	 *           is logged.
	 */
	private void logTrustStatusCheckResults(Collection<CertificateAndRevocationStatus> chainCertsWithRevocationStatus) {
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
	private void assessCertificateVerificationData(CertificateVerificationData certificateVerificationData, LTVMode ltvMode) throws PDFASError {
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
	private void assess(CertificateAndRevocationStatus certAndRevStatus, LTVMode ltvMode) throws PDFASError {
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

}
