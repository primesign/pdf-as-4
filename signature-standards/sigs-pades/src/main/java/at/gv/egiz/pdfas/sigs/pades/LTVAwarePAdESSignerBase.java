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

public abstract class LTVAwarePAdESSignerBase implements IPlainSigner {
	
	private Logger log = LoggerFactory.getLogger(LTVAwarePAdESSignerBase.class);

	@Override
	public CertificateVerificationData getCertificateVerificationData(RequestedSignature requestedSignature) throws PDFASError {
		
		// LTV mode controls if and how retrieval/embedding LTV data will be done
		LTVMode ltvMode = requestedSignature.getStatus().getSignParamter().getLTVMode();
		log.trace("LTV mode: {}", ltvMode);
		
		if (ltvMode == LTVMode.NONE) {
			// do not try to fetch any data in case of LTV disabled
			log.debug("Did not add LTV related data since LTV mode was {}.", ltvMode); 
			return null;
		}
		
		if (requestedSignature.getCertificate() == null) {
			throw new IllegalStateException("Retrieving certificate verification data required retrieval of the certificate beforehand.");
		}
		
		CertificateVerificationData ltvVerificationInfo = null;

		try {
			
			// fetch PDF-AS settings to be provided to verification data service/validation providers
			ISettings settings = requestedSignature.getStatus().getSettings();
			
			// fetch/create service in order to see if we can handle the signer's CA
			CertificateVerificationDataService ltvVerificationInfoService = CertificateVerificationDataService.getInstance();
			if (ltvVerificationInfoService.canHandle(requestedSignature.getCertificate(), settings)) {
				// yes, we can
				log.debug("Retrieving LTV verification info.");
				ltvVerificationInfo = ltvVerificationInfoService.getCertificateVerificationData(requestedSignature.getCertificate(), settings);
			}

		} catch (Exception e) {
			// error retrieving LTV data, LTV mode controls how errors are handled
			final String message = "Unable to retrieve LTV related data.";
			if (ltvMode == LTVMode.OPTIONAL) {
				log.warn(message, e);
				return null;
			}
			throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_RETRIEVING_REQUIRED_DATA, message, e);
		}

		// Did we get data to be embedded with signature ?
		if (ltvVerificationInfo == null) {

			// no data available, LTV mode controls how this case is handled
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(
						ErrorConstants.ERROR_SIG_PADESLTV_NO_DATA,
						"No LTV data available for the signers certificate while LTV-enabled signatures are required. Make sure appropriate verification data providers are available."
				);
			} else {
				log.info("No LTV data available for the signers certificate. Do not LTV-enable signature.");
			}

		}
		
		return ltvVerificationInfo;

	}

}
