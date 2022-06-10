package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;

@Immutable
@ThreadSafe
public class LTVSupportImpl implements LTVSupport {

	private Logger log = LoggerFactory.getLogger(LTVSupportImpl.class);

	/**
	 * Adds previously collected LTV verification data to the provided pdf document and updates pdf version and extensions
	 * dictionary if needed.
	 *
	 * @param pdDocument          The pdf document (required; must not be {@code null}).
	 * @param ltvVerificationInfo The certificate verification info data (required; must not be {@code null}).
	 * @throws CertificateEncodingException In case of an error with certificate encoding.
	 * @throws CRLException                 In case there was an error encoding CRL data.
	 * @throws IOException                  In case there was an error adding a pdf stream to the document.
	 */
	@Override
	public void addLTVInfo(@Nonnull PDDocument pdDocument, @Nonnull CertificateVerificationData ltvVerificationInfo) throws CertificateEncodingException, CRLException, IOException {
		
		Objects.requireNonNull(pdDocument, "'pdDocument' must not be null.");
		Objects.requireNonNull(ltvVerificationInfo, "'ltvVerificationInfo' must not be null.");
		
		// expect at least the certificate(s)
		if (CollectionUtils.isEmpty(ltvVerificationInfo.getChainCerts())) {
			throw new IllegalStateException("LTV data has not been retrieved yet. At least the signer certificate's chain is must be provided.");
		}
		
		log.debug("Adding LTV info to document.");
		addOrUpdateDSS(pdDocument, ltvVerificationInfo);
		
		// DSS reflects an extension to ISO 32000-1:2008 (PDF-1.7), so the document should be labeled as 1.7+ document
		if (pdDocument.getVersion() < 1.7f) {
			if (log.isDebugEnabled()) {
				log.debug("Updating pdf version: {} -> 1.7", pdDocument.getVersion());
			}
			pdDocument.setVersion(1.7f);
			// There must be a path of objects that have {@link COSUpdateInfo#isNeedToBeUpdated()} set, starting from the document catalog.
			pdDocument.getDocumentCatalog().getCOSObject().setNeedToBeUpdated(true);
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
	 * Adds (or updates) the DSS dictionary as specified by <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS 102
	 * 778-4 v1.1.2, Annex A, "LTV extensions"</a> and
	 * <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS 103 172
	 * V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
	 *
	 * @param pdDocument          The pdf document (required; must not be {@code null}).
	 * @param ltvVerificationInfo The certificate verification info data (required; must not be {@code null}).
	 * @throws CertificateEncodingException In case of an error encoding certificates.
	 * @throws IOException                  In case there was an error adding a pdf stream to the document.
	 * @throws CRLException                 In case there was an error encoding CRL data.
	 * @implNote Marks the document root catalog and the dss dictionary dirty.
	 */
	void addOrUpdateDSS(@Nonnull PDDocument pdDocument, @Nonnull CertificateVerificationData ltvVerificationInfo) throws CertificateEncodingException, IOException, CRLException {
		
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		COSDictionary dssDictionary = (COSDictionary) rootDictionary.getDictionaryObject("DSS");
		if (dssDictionary == null) {
			log.trace("Adding new DSS dictionary.");
			// add new DSS dictionary
			dssDictionary = new COSDictionary();
			rootDictionary.setItem("DSS", dssDictionary);
		}
		// There must be a path of objects that have {@link COSUpdateInfo#isNeedToBeUpdated()} set, starting from the document catalog.
		rootDictionary.setNeedToBeUpdated(true);
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
	 * @implNote Marks the document root catalog dirty.
	 */
	void addOrUpdateExtensions(@Nonnull PDDocument pdDocument) {
		
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();

		COSDictionary extDictionary = (COSDictionary) rootDictionary.getDictionaryObject("Extensions");
		if (extDictionary == null) {
			log.trace("Adding new Extensions dictionary.");
			// add new Extensions dictionary
			extDictionary = new COSDictionary();
			extDictionary.setDirect(true);
			rootDictionary.setItem("Extensions", extDictionary);
		}
		rootDictionary.setNeedToBeUpdated(true);
		
		addOrUpdateADBEExtension(extDictionary);
	}

	/**
	 * Adds (or updates) the /ADBE dictionary to the provided extensions dictionary (if not already present) in order to
	 * announce DSS extension to ISO32000-1:2008.
	 * 
	 * @param extDictionary The extension dictionary. (required; must not be {@code null})
	 * @implNote Marks the provided Extensions dictionary and the nested ADBE dictionary dirty, if modified.
	 */
	void addOrUpdateADBEExtension(@Nonnull COSDictionary extDictionary) {
		
		COSDictionary adbeDictionary = (COSDictionary) extDictionary.getDictionaryObject("ADBE");
		if (adbeDictionary == null) {
			log.trace("Adding new ADBE extensions dictionary.");
			// add new ADBE dictionary
			adbeDictionary = new COSDictionary();
			adbeDictionary.setDirect(true);
			extDictionary.setItem("ADBE", adbeDictionary);
			extDictionary.setNeedToBeUpdated(true);
		}
		
		final COSName VERSION_1_7 = COSName.getPDFName("1.7");
		// only set entry if not already set
		if (!VERSION_1_7.equals(adbeDictionary.getItem("BaseVersion"))) {
			adbeDictionary.setItem("BaseVersion", VERSION_1_7);
			adbeDictionary.setNeedToBeUpdated(true);
			extDictionary.setNeedToBeUpdated(true);
		}
		// only set if not already set
		if (adbeDictionary.getInt("ExtensionLevel") != 5) {
			adbeDictionary.setInt("ExtensionLevel", 5);
			adbeDictionary.setNeedToBeUpdated(true);
			extDictionary.setNeedToBeUpdated(true);
		}
		
	}

	/**
	 * Adds the "Certs" dictionary to DSS dictionary as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS 102
	 * 778-4 v1.1.2, Annex A, "LTV extensions"</a> and
	 * <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS 103 172
	 * V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
	 *
	 * @param pdDocument    The pdf document (required; must not be {@code null}).
	 * @param dssDictionary The DSS dictionary (required; must not be {@code null}).
	 * @param certificates  The certificates (required; must not be {@code null}).
	 * @throws IOException                  In case there was an error adding a pdf stream to the document.
	 * @throws CertificateEncodingException In case of an error encoding certificates.
	 * @implNote Marks the provided DSS dictionary dirty.
	 */
	void addDSSCerts(@Nonnull PDDocument pdDocument, @Nonnull COSDictionary dssDictionary, @Nonnull Iterable<X509Certificate> certificates) throws IOException, CertificateEncodingException {
		
		final COSName COSNAME_CERTS = COSName.getPDFName("Certs");
		COSArray certsArray = (COSArray) dssDictionary.getDictionaryObject(COSNAME_CERTS);
		if (certsArray == null) {
			// add new "Certs" array
			log.trace("Adding new DSS/Certs dictionary.");
			// "An array of (indirect references to) streams, each containing one BER-encoded X.509 certificate (see RFC 5280 [7])"
			certsArray = new COSArray();
			dssDictionary.setItem(COSNAME_CERTS, certsArray);
		}
		dssDictionary.setNeedToBeUpdated(true);
		certsArray.setNeedToBeUpdated(true);
		
		// add BER-encoded X.509 certificates
		log.trace("Adding certificates to DSS/Certs dictionary.");
		for (X509Certificate certificate : certificates) {
			log.trace("Adding certificate to DSS: subject='{}' (issuer='{}', serial={})", certificate.getSubjectDN(), certificate.getIssuerDN(), certificate.getSerialNumber());
			try (InputStream in = new ByteArrayInputStream(certificate.getEncoded())) {
				certsArray.add(new PDStream(pdDocument, in, COSName.FLATE_DECODE));
			}
		}
		
	}

	/**
	 * Adds the "OCSPs" dictionary to DSS dictionary as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS 102
	 * 778-4 v1.1.2, Annex A, "LTV extensions"</a> and
	 * <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS 103 172
	 * V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
	 *
	 * @param pdDocument           The pdf document (required; must not be {@code null}).
	 * @param dssDictionary        The DSS dictionary (required; must not be {@code null}).
	 * @param encodedOcspResponses The encoded OCSP responses (required; must not be {@code null}).
	 * @throws IOException In case there was an error adding a pdf stream to the document.
	 * @implNote Marks the provided DSS dictionary dirty.
	 */
	void addDSSOCSPs(@Nonnull PDDocument pdDocument, @Nonnull COSDictionary dssDictionary, @Nonnull Iterable<byte[]> encodedOcspResponses) throws IOException {
		
		final COSName COSNAME_OCSPS = COSName.getPDFName("OCSPs");
		COSArray ocspssArray = (COSArray) dssDictionary.getDictionaryObject(COSNAME_OCSPS);
		if (ocspssArray == null) {
			log.trace("Adding new DSS/OCSPs dictionary.");
			// add "OCSPs" array
			// "An array of (indirect references to) streams, each containing a BER-encoded Online Certificate Status Protocol (OCSP) response (see RFC 2560 [8])."
			ocspssArray = new COSArray();
			dssDictionary.setItem(COSNAME_OCSPS, ocspssArray);
		}
		ocspssArray.setNeedToBeUpdated(true);
		dssDictionary.setNeedToBeUpdated(true);

		for (byte[] encodedOcspResponse : encodedOcspResponses) {
			try (InputStream in = new ByteArrayInputStream(encodedOcspResponse)) {
				ocspssArray.add(new PDStream(pdDocument, in, COSName.FLATE_DECODE));
			}
		}
		
	}

	/**
	 * Adds the "CRLs" dictionary to DSS dictionary as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS 102
	 * 778-4 v1.1.2, Annex A, "LTV extensions"</a> and
	 * <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS 103 172
	 * V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
	 *
	 * @param pdDocument    The pdf document (required; must not be {@code null}).
	 * @param dssDictionary The DSS dictionary (required; must not be {@code null}).
	 * @param crls          The CRLs (required; must not be {@code null}).
	 * @throws IOException  In case there was an error adding a pdf stream to the document.
	 * @throws CRLException In case there was an error encoding CRL data.
	 * @implNote Marks the provided DSS dictionary dirty.
	 */
	void addDSSCRLs(@Nonnull PDDocument pdDocument, @Nonnull COSDictionary dssDictionary, @Nonnull Iterable<X509CRL> crls) throws IOException, CRLException {

		final COSName COSNAME_CRLS = COSName.getPDFName("CRLs");
		COSArray crlsArray = (COSArray) dssDictionary.getDictionaryObject(COSNAME_CRLS);
		if (crlsArray == null) {
			log.trace("Adding new DSS/CRLs dictionary.");
			// add "CRLs" array
			// "An array of (indirect references to) streams, each containing a BER-encoded Certificate Revocation List (CRL) (see RFC 5280 [7])."
			crlsArray = new COSArray();
			dssDictionary.setItem(COSNAME_CRLS, crlsArray);
		}
		crlsArray.setNeedToBeUpdated(true);
		dssDictionary.setNeedToBeUpdated(true);

		for (X509CRL crl : crls) {
			try (InputStream in = new ByteArrayInputStream(crl.getEncoded())) {
				crlsArray.add(new PDStream(pdDocument, in, COSName.FLATE_DECODE));
			}
		}
	}

}
