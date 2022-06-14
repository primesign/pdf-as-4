package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;

@Immutable
@ThreadSafe
public class LTVSupportImpl implements LTVSupport {

	private static Logger log = LoggerFactory.getLogger(LTVSupportImpl.class);

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

		// set appropriate pdf version
		ensurePdf17(pdDocument);
		
		// announce that an extension to PDF-1.7 has been added
		addOrUpdateExtensions(pdDocument);
		
		if (CollectionUtils.isNotEmpty(ltvVerificationInfo.getCRLs()) || CollectionUtils.isNotEmpty(ltvVerificationInfo.getEncodedOCSPResponses())) {
			log.info("LTV data (certchain and revocation info) added to document.");
		} else {
			log.info("LTV data (certchain but no revocation info) added to document.");
		}
		
	}
	
	/**
	 * Sets the pdf version to 1.7 is the current version is lower than 1.7.
	 * 
	 * @param pdDocument The underlying document. (required; must not be {@code null})
	 * @implNote Marks the document root catalog dirty if the version is modified.
	 */
	void ensurePdf17(@Nonnull PDDocument pdDocument) {
		
		// DSS reflects an extension to ISO 32000-1:2008 (PDF-1.7), so the document should be labeled as 1.7+ document
		if (pdDocument.getVersion() < 1.7f) {
			if (log.isDebugEnabled()) {
				log.debug("Updating pdf version: {} -> 1.7", pdDocument.getVersion());
			}
			pdDocument.setVersion(1.7f);
			// There must be a path of objects that have {@link COSUpdateInfo#isNeedToBeUpdated()} set, starting from the document catalog.
			pdDocument.getDocumentCatalog().getCOSObject().setNeedToBeUpdated(true);
		}
		
	}

	/**
	 * Adds (or updates) the DSS dictionary of the provided document as specified by <a href=
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
	 * @implNote Creates a new DSS dictionary if not already exists.
	 * @implNote Marks the document root catalog and the dss dictionary dirty.
	 */
	void addOrUpdateDSS(@Nonnull PDDocument pdDocument, @Nonnull CertificateVerificationData ltvVerificationInfo) throws CertificateEncodingException, IOException, CRLException {
		
		// DSS/Certs
		addDSSCerts(pdDocument, ltvVerificationInfo.getChainCerts());

		// DSS/OCSPs
		if (CollectionUtils.isNotEmpty(ltvVerificationInfo.getEncodedOCSPResponses())) {
			addDSSOCSPs(pdDocument, ltvVerificationInfo.getEncodedOCSPResponses());
		}

		// DSS/CRLs
		if (CollectionUtils.isNotEmpty(ltvVerificationInfo.getCRLs())) {
			addDSSCRLs(pdDocument, ltvVerificationInfo.getCRLs());
		}
		
	}
	
	/**
	 * Makes sure the provided document contains a DSS dictionary. Either creates a new dictionary or returns the existing
	 * dictionary.
	 * 
	 * @param pdDocument The underlying document. (required; must not be {@code null})
	 * @return The document's DSS dictionary. (never {@code null}).
	 * @implNote In case the dss dictionary is newly created, both the document and the dss dictionary are marked dirty.
	 */
	@Nonnull
	COSDictionary ensureDSSDictionary(@Nonnull PDDocument pdDocument) {
		
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		COSDictionary dssDictionary = (COSDictionary) rootDictionary.getDictionaryObject("DSS");
		if (dssDictionary == null) {
			log.trace("Adding new DSS dictionary.");
			// add new DSS dictionary
			dssDictionary = new COSDictionary();
			rootDictionary.setItem("DSS", dssDictionary);
			// There must be a path of objects that have {@link COSUpdateInfo#isNeedToBeUpdated()} set, starting from the document catalog.
			rootDictionary.setNeedToBeUpdated(true);
			dssDictionary.setNeedToBeUpdated(true);
		}
		return dssDictionary;
		
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
			log.trace("Adding new Extensions/ADBE dictionary.");
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
	 * Adds the "Certs" dictionary to DSS dictionary of the provided document as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS 102
	 * 778-4 v1.1.2, Annex A, "LTV extensions"</a> and
	 * <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS 103 172
	 * V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
	 *
	 * @param pdDocument    The pdf document (required; must not be {@code null}).
	 * @param certificates  The certificates (required; must not be {@code null}).
	 * @throws IOException                  In case there was an error adding a pdf stream to the document.
	 * @throws CertificateEncodingException In case of an error encoding certificates.
	 * @implNote Marks the provided DSS dictionary dirty.
	 * @implNote Creates a new DSS dictionary if not already exists.
	 * @implNote Certificates already present within the DSS are not added.
	 */
	void addDSSCerts(@Nonnull PDDocument pdDocument, @Nonnull Iterable<X509Certificate> certificates) throws IOException, CertificateEncodingException {
		
		final COSDictionary dssDictionary = ensureDSSDictionary(pdDocument);
		
		COSArray certsArray = (COSArray) dssDictionary.getDictionaryObject("Certs");
		if (certsArray == null) {
			// add new "Certs" array
			log.trace("Adding new DSS/Certs dictionary.");
			// "An array of (indirect references to) streams, each containing one BER-encoded X.509 certificate (see RFC 5280 [7])"
			certsArray = new COSArray();
			dssDictionary.setItem("Certs", certsArray);
		}
		
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		
		// build set of certificates already present within DSS
		Set<X509Certificate> alreadyPresent = new HashSet<>();
		// @formatter:off
		certsArray.forEach(
			cosBase -> toCOSStream(cosBase)                             // COSBase -> COSStream
				.ifPresent(cosStream -> toX509Certificate(cosStream)    // COSStream -> X509Certificate
					.ifPresent(alreadyPresent::add)));
		// @formatter:on
		
		// add BER-encoded X.509 certificates
		log.trace("Adding certificates to DSS/Certs dictionary.");
		for (X509Certificate certificate : certificates) {
			if (!alreadyPresent.contains(certificate))  {
				log.trace("Adding certificate to DSS/Certs: subject='{}' (issuer='{}', serial={})", certificate.getSubjectDN(), certificate.getIssuerDN(), certificate.getSerialNumber());
				try (InputStream in = new ByteArrayInputStream(certificate.getEncoded())) {
					certsArray.add(new PDStream(pdDocument, in, COSName.FLATE_DECODE));
				}
				
				dssDictionary.setNeedToBeUpdated(true);
				certsArray.setNeedToBeUpdated(true);
				// There must be a path of objects that have {@link COSUpdateInfo#isNeedToBeUpdated()} set, starting from the document catalog.
				rootDictionary.setNeedToBeUpdated(true);
				
				alreadyPresent.add(certificate);
			} else {
				log.trace("Do not add already existing certificate to DSS/Certs: subject='{}' (issuer='{}', serial={})", certificate.getSubjectDN(), certificate.getIssuerDN(), certificate.getSerialNumber());
			}
		}
		
	}

	/**
	 * Adds the "OCSPs" dictionary to DSS dictionary of the provided document as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS 102
	 * 778-4 v1.1.2, Annex A, "LTV extensions"</a> and
	 * <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS 103 172
	 * V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
	 *
	 * @param pdDocument           The pdf document (required; must not be {@code null}).
	 * @param encodedOcspResponses The encoded OCSP responses (required; must not be {@code null}).
	 * @throws IOException In case there was an error adding a pdf stream to the document.
	 * @implNote Creates a new DSS dictionary if not already exists. Dictionary will be marked dirty.
	 */
	void addDSSOCSPs(@Nonnull PDDocument pdDocument, @Nonnull Iterable<byte[]> encodedOcspResponses) throws IOException {
		
		final COSDictionary dssDictionary = ensureDSSDictionary(pdDocument);

		COSArray ocspssArray = (COSArray) dssDictionary.getDictionaryObject("OCSPs");
		if (ocspssArray == null) {
			log.trace("Adding new DSS/OCSPs dictionary.");
			// add "OCSPs" array
			// "An array of (indirect references to) streams, each containing a BER-encoded Online Certificate Status Protocol (OCSP) response (see RFC 2560 [8])."
			ocspssArray = new COSArray();
			dssDictionary.setItem("OCSPs", ocspssArray);
		}
		ocspssArray.setNeedToBeUpdated(true);
		dssDictionary.setNeedToBeUpdated(true);
		// There must be a path of objects that have {@link COSUpdateInfo#isNeedToBeUpdated()} set, starting from the document catalog.
		pdDocument.getDocumentCatalog().getCOSObject().setNeedToBeUpdated(true);

		for (byte[] encodedOcspResponse : encodedOcspResponses) {
			try (InputStream in = new ByteArrayInputStream(encodedOcspResponse)) {
				ocspssArray.add(new PDStream(pdDocument, in, COSName.FLATE_DECODE));
			}
		}
		
	}
	
	/**
	 * Tries to retrieve a COSStream from a provided COSBase object.
	 * 
	 * @param cosBase The COSBase object. (optional; may be {@code null})
	 * @return A resulting COSStream object wrapped by an Optional.
	 */
	static Optional<COSStream> toCOSStream(@Nullable COSBase cosBase) {
		
		// if cosBase is a reference dereference it
		if (cosBase instanceof COSObject) {
			cosBase = ((COSObject) cosBase).getObject();
		}
		
		if (cosBase instanceof COSStream) {
			return Optional.ofNullable((COSStream) cosBase);
		}
		
		log.trace("Unable to convert provided COSBase to COSStream.");
		return Optional.empty();
	}

	/**
	 * Adds the "CRLs" dictionary to DSS dictionary of the provided document as specified in <a href=
	 * "http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf">ETSI TS 102
	 * 778-4 v1.1.2, Annex A, "LTV extensions"</a> and
	 * <a href="https://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf">ETSI TS 103 172
	 * V2.2.2 (2013-04), Profile of ISO 32000-1 LTV Extensions</a>
	 *
	 * @param pdDocument    The pdf document (required; must not be {@code null}).
	 * @param crls          The CRLs (required; must not be {@code null}).
	 * @throws IOException  In case there was an error adding a pdf stream to the document.
	 * @throws CRLException In case there was an error encoding CRL data.
	 * @implNote Creates a new DSS dictionary if not already exists. Dictionary will be marked dirty.
	 */
	void addDSSCRLs(@Nonnull PDDocument pdDocument, @Nonnull Iterable<X509CRL> crls) throws IOException, CRLException {

		final COSDictionary dssDictionary = ensureDSSDictionary(pdDocument);

		COSArray crlsArray = (COSArray) dssDictionary.getDictionaryObject("CRLs");
		if (crlsArray == null) {
			log.trace("Adding new DSS/CRLs dictionary.");
			// add "CRLs" array
			// "An array of (indirect references to) streams, each containing a BER-encoded Certificate Revocation List (CRL) (see RFC 5280 [7])."
			crlsArray = new COSArray();
			dssDictionary.setItem("CRLs", crlsArray);
		}
		
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		
		// build set of clrs already present within DSS
		Set<X509CRL> alreadyPresent = new HashSet<>();
		// @formatter:off
		crlsArray.forEach(
			cosBase -> toCOSStream(cosBase)                     // COSBase -> COSStream
				.ifPresent(cosStream -> toX509CRL(cosStream)    // COSStream -> X509CRL
					.ifPresent(alreadyPresent::add)));
		// @formatter:on

		for (X509CRL crl : crls) {
			if (!alreadyPresent.contains(crl))  {
				if (log.isTraceEnabled()) {
					log.trace("Adding CRL to DSS/CRLs: issuer='{}', thisUpdate={}, nextUpdate={}", crl.getIssuerDN(), format(crl.getThisUpdate()), format(crl.getNextUpdate()));
				}
				try (InputStream in = new ByteArrayInputStream(crl.getEncoded())) {
					crlsArray.add(new PDStream(pdDocument, in, COSName.FLATE_DECODE));
				}
				crlsArray.setNeedToBeUpdated(true);
				dssDictionary.setNeedToBeUpdated(true);
				// There must be a path of objects that have {@link COSUpdateInfo#isNeedToBeUpdated()} set, starting from the document catalog.
				rootDictionary.setNeedToBeUpdated(true);
				
				alreadyPresent.add(crl);
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Do not add already existing CRL to DSS/CRLs: issuer='{}', thisUpdate={}, nextUpdate={}", crl.getIssuerDN(), format(crl.getThisUpdate()), format(crl.getNextUpdate()));
				}
			}
		}
	}
	
	@Nullable
	private static String format(@Nullable Date date) {
		if (date != null) {
			return DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(date);
		}
		return null;
	}
	
	/**
	 * Parses a X509Certificate from a given COSStream.
	 * 
	 * @param cosStream The cos stream. (required; must not be {@code null})
	 * @return The parsed certificate wrapped in Optional.
	 * @implNote Optional is empty, in case of any error.
	 */
	static Optional<X509Certificate> toX509Certificate(@Nonnull COSStream cosStream) {
		
		X509Certificate x509Certificate = null;
		try (InputStream in = cosStream.createInputStream()) {
			
			x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
			
		} catch (Exception e) {
			log.info("Unable to decode certificate from existing DSS dictionary: {}", String.valueOf(e));
		}
		
		return Optional.ofNullable(x509Certificate);
		
	}

	/**
	 * Parses a X509CRL from a given COSStream.
	 * 
	 * @param cosStream The cos stream. (required; must not be {@code null})
	 * @return The parsed crl wrapped in Optional.
	 * @implNote Optional is empty, in case of any error.
	 */
	static Optional<X509CRL> toX509CRL(@Nonnull COSStream cosStream) {
		
		X509CRL crl = null;
		try (InputStream in = cosStream.createInputStream()) {
			
			crl = (X509CRL) CertificateFactory.getInstance("X.509").generateCRL(in);
			
		} catch (Exception e) {
			log.info("Unable to decode crl from existing DSS dictionary: {}", String.valueOf(e));
		}
		
		return Optional.ofNullable(crl);
		
	}

}
