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

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;

public class PlaceholderFilter implements IConfigurationConstants {
	
	public static SignaturePlaceholderData checkPlaceholderSignature(
			OperationStatus status, ISettings settings)
			throws PdfAsException, IOException {
		
		if (status.getPlaceholderConfiguration().isGlobalPlaceholderEnabled()) {
			SignaturePlaceholderData signaturePlaceholderData = SignaturePlaceholderExtractor
					.extract(status.getPdfObject().getDocument(), null, 1);

			return signaturePlaceholderData;
			/*
			if (signaturePlaceholderData != null) {
				RequestedSignature requestedSignature = status
						.getRequestedSignature();

				if (signaturePlaceholderData.getProfile() != null) {
					requestedSignature
							.setSignatureProfileID(signaturePlaceholderData
									.getProfile());
				}

				//String signatureProfileID = requestedSignature
				//		.getSignatureProfileID();

				TablePos tablePos = signaturePlaceholderData.getTablePos();

				return tablePos;
				
				*/
				/*
				SignatureProfileSettings signatureProfileSettings = TableFactory
						.createProfile(signatureProfileID, settings);

				Table main = TableFactory.createSigTable(
						signatureProfileSettings, MAIN, settings,
						requestedSignature);

				IPDFStamper stamper = StamperFactory
						.createDefaultStamper(settings);
				
				IPDFVisualObject visualObject = stamper.createVisualPDFObject(
						status.getPdfObject(), main);

				PDDocument originalDocument = PDDocument
						.load(new ByteArrayInputStream(status.getPdfObject()
								.getOriginalDocument()));

				PositioningInstruction positioningInstruction = Positioning
						.determineTablePositioning(tablePos, "",
								originalDocument, visualObject, false);
				
				return positioningInstruction;*/
			//}
		}
		return null;
	}
}
