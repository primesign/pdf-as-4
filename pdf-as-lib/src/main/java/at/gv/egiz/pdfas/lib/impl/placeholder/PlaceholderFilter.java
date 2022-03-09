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
package at.gv.egiz.pdfas.lib.impl.placeholder;

import java.io.IOException;
import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsErrorCarrier;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderWebConfiguration;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import org.apache.commons.lang3.StringUtils;

public class PlaceholderFilter implements IConfigurationConstants,
		PlaceholderExtractorConstants {

	public static SignaturePlaceholderData checkPlaceholderSignatureLocation(
			OperationStatus status, ISettings settings, String signatureLocation) throws PdfAsException,
			IOException {

		String placeholderID;

		if (status.getPlaceholderConfiguration().isGlobalPlaceholderEnabled()) {
			PlaceholderExtractor extractor = status.getBackend().getPlaceholderExtractor();

			if(StringUtils.isNotEmpty(signatureLocation)) {
				placeholderID = signatureLocation;
			} else {
				placeholderID = PlaceholderWebConfiguration.getValue(PLACEHOLDER_WEB_ID);
				if(StringUtils.isEmpty(placeholderID)) {
					placeholderID = settings.getValue(PLACEHOLDER_ID);
				}
			}

			String placeholderModeString = settings.getValue(PLACEHOLDER_MODE);
			int placeholderMode = PLACEHOLDER_MATCH_MODE_MODERATE;
			if (StringUtils.isNotEmpty(placeholderModeString)) {
				try {
					placeholderMode = Integer.parseInt(placeholderModeString);
					if (placeholderMode < PLACEHOLDER_MODE_MIN
							|| placeholderMode > PLACEHOLDER_MODE_MAX) {
						throw new PdfAsErrorCarrier(new PDFASError(
								PDFASError.ERROR_INVALID_PLACEHOLDER_MODE));
					}
				} catch (NumberFormatException e) {
					throw new PdfAsErrorCarrier(new PDFASError(
							PDFASError.ERROR_INVALID_PLACEHOLDER_MODE, e));
				}
			}
			SignaturePlaceholderData signaturePlaceholderData = extractor.extract(status.getPdfObject(), placeholderID, placeholderMode);
			return signaturePlaceholderData;

		} else if (status.getPlaceholderConfiguration().isProfileConfigurationEnabled(status.getRequestedSignature().getSignatureProfileID())) {
			//filter for local placeholder in selected profiles
			PlaceholderExtractor extractor = status.getBackend().getPlaceholderExtractor();
			int placeholderMode = PLACEHOLDER_MATCH_MODE_SORTED;

			placeholderID = status.getPlaceholderConfiguration().getProfilePlaceholderID(status.getRequestedSignature().getSignatureProfileID());
			if(StringUtils.isNotEmpty(placeholderID)) {
				placeholderMode = PLACEHOLDER_MATCH_MODE_MODERATE;
			}
			String placeholderModeString = settings.getValue(PLACEHOLDER_MODE);
			if (StringUtils.isNotEmpty(placeholderModeString))  {
				try {
					placeholderMode = Integer.parseInt(placeholderModeString);
					if (placeholderMode < PLACEHOLDER_MODE_MIN
							|| placeholderMode > PLACEHOLDER_MODE_MAX) {
						throw new PdfAsErrorCarrier(new PDFASError(
								PDFASError.ERROR_INVALID_PLACEHOLDER_MODE));
					}
				} catch (NumberFormatException e) {
					throw new PdfAsErrorCarrier(new PDFASError(
							PDFASError.ERROR_INVALID_PLACEHOLDER_MODE, e));
				}
			}
			SignaturePlaceholderData signaturePlaceholderData = extractor.extract(status.getPdfObject(), placeholderID, placeholderMode);
			return signaturePlaceholderData;
		}
		return null;
	}

	public static List<SignaturePlaceholderData> checkPlaceholderSignatureLocationList(OperationStatus status, ISettings settings, String signatureLocation) throws PdfAsException,
			IOException {
		String placeholderID;

		if (status.getPlaceholderConfiguration().isGlobalPlaceholderEnabled()) {
			PlaceholderExtractor extractor = status.getBackend().getPlaceholderExtractor();

			if(StringUtils.isNotEmpty(signatureLocation)) {
				placeholderID = signatureLocation;
			} else {
				placeholderID = PlaceholderWebConfiguration.getValue(PLACEHOLDER_WEB_ID);
				if(StringUtils.isEmpty(placeholderID)) {
					placeholderID = settings.getValue(PLACEHOLDER_ID);
				}
			}

			String placeholderModeString = settings.getValue(PLACEHOLDER_MODE);
			int placeholderMode = PLACEHOLDER_MATCH_MODE_MODERATE;
			if (StringUtils.isNotEmpty(placeholderModeString)) {
				try {
					placeholderMode = Integer.parseInt(placeholderModeString);
					if (placeholderMode < PLACEHOLDER_MODE_MIN
							|| placeholderMode > PLACEHOLDER_MODE_MAX) {
						throw new PdfAsErrorCarrier(new PDFASError(
								PDFASError.ERROR_INVALID_PLACEHOLDER_MODE));
					}
				} catch (NumberFormatException e) {
					throw new PdfAsErrorCarrier(new PDFASError(
							PDFASError.ERROR_INVALID_PLACEHOLDER_MODE, e));
				}
			}
			return extractor.extractList(status.getPdfObject(), placeholderID,
					placeholderMode);

		} else if (status.getPlaceholderConfiguration().isProfileConfigurationEnabled(status.getRequestedSignature().getSignatureProfileID())) {
			//filter for local placeholder in selected profiles
			PlaceholderExtractor extractor = status.getBackend().getPlaceholderExtractor();
			int placeholderMode = PLACEHOLDER_MATCH_MODE_SORTED;

			placeholderID = status.getPlaceholderConfiguration().getProfilePlaceholderID(status.getRequestedSignature().getSignatureProfileID());
			if(StringUtils.isNotEmpty(placeholderID)) {
				placeholderMode = PLACEHOLDER_MATCH_MODE_MODERATE;
			}
			String placeholderModeString = settings.getValue(PLACEHOLDER_MODE);
			if (StringUtils.isNotEmpty(placeholderModeString))  {
				try {
					placeholderMode = Integer.parseInt(placeholderModeString);
					if (placeholderMode < PLACEHOLDER_MODE_MIN
							|| placeholderMode > PLACEHOLDER_MODE_MAX) {
						throw new PdfAsErrorCarrier(new PDFASError(
								PDFASError.ERROR_INVALID_PLACEHOLDER_MODE));
					}
				} catch (NumberFormatException e) {
					throw new PdfAsErrorCarrier(new PDFASError(
							PDFASError.ERROR_INVALID_PLACEHOLDER_MODE, e));
				}
			}
			return extractor.extractList(status.getPdfObject(), placeholderID,
					placeholderMode);
		}
		return null;
	}
}