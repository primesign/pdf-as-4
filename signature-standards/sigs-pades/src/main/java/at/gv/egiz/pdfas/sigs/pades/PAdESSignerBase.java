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
package at.gv.egiz.pdfas.sigs.pades;

import static at.gv.egiz.pdfas.sigs.pades.PAdESConstants.FILTER_ADOBE_PPKLITE;
import static at.gv.egiz.pdfas.sigs.pades.PAdESConstants.SUBFILTER_ETSI_CADES_DETACHED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter.LTVMode;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.pki.CertificateVerificationDataService;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import at.gv.egiz.pdfas.lib.util.CertificateUtils;
import iaik.x509.X509Certificate;

public abstract class PAdESSignerBase implements IPlainSigner {

	private Logger log = LoggerFactory.getLogger(PAdESSignerBase.class);

	@Override
	public CertificateVerificationData getCertificateVerificationData(RequestedSignature requestedSignature) throws PDFASError {

		// LTV mode controls if and how retrieval/embedding LTV data will be done
		LTVMode ltvMode = requestedSignature.getStatus().getSignParamter().getLTVMode();
		log.trace("LTV mode: {}", ltvMode);

		if (ltvMode == LTVMode.NONE) {
			return null;
		}

		final X509Certificate eeCertificate = requestedSignature.getCertificate();
		if (eeCertificate == null) {
			throw new IllegalStateException("Retrieving certificate verification data required retrieval of the certificate beforehand.");
		}

		try {

			// fetch PDF-AS settings to be provided to verification data service/validation providers
			ISettings settings = requestedSignature.getStatus().getSettings();

			// fetch/create service in order to see if we can handle the signer's CA
			CertificateVerificationDataService ltvVerificationInfoService = CertificateVerificationDataService.getInstance();
			if (ltvVerificationInfoService.canHandle(eeCertificate, settings)) {
				
				// yes, we can
				log.debug("Retrieving LTV verification info.");
				return ltvVerificationInfoService.getCertificateVerificationData(eeCertificate, settings);
				
			} else {
				log.info("Unable to handle LTV retrieval for signer certificate with issuer (ski: {}): {}", CertificateUtils.getAuthorityKeyIdentifierHexString(eeCertificate).orElseGet(() -> null), eeCertificate.getIssuerDN());
			}

		} catch (Exception e) {
			
			// error retrieving LTV data, LTV mode controls how errors are handled
			final String message = "Unable to retrieve LTV related data.";
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_RETRIEVING_REQUIRED_DATA, message, e);
			}
			log.warn(message, e);
			
		}

		return null;

	}

	@Override
	public String getPDFSubFilter() {
		return SUBFILTER_ETSI_CADES_DETACHED;
	}

	@Override
	public String getPDFFilter() {
		return FILTER_ADOBE_PPKLITE;
	}

}
