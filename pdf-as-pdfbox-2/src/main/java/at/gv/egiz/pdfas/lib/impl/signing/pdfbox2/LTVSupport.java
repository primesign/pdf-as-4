package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.pdfbox.pdmodel.PDDocument;

import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;

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
@ThreadSafe
public interface LTVSupport {

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
	void addLTVInfo(PDDocument pdDocument, CertificateVerificationData ltvVerificationInfo)
			throws CertificateEncodingException, CRLException, IOException;

}