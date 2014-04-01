package at.gv.egiz.pdfas.lib.impl.placeholder;

import java.io.ByteArrayInputStream;
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
					.extract(new ByteArrayInputStream(status.getPdfObject()
							.getOriginalDocument()), null, 1);

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
