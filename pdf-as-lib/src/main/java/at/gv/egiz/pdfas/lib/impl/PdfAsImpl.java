package at.gv.egiz.pdfas.lib.impl;

import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.configuration.ConfigurationImpl;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderConfiguration;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

public class PdfAsImpl implements PdfAs, IConfigurationConstants {

	public SignResult sign(SignParameter parameter) throws PdfAsException {
		// TODO: verify signParameter
		
		// Status initialization
		if(!(parameter.getConfiguration() instanceof ISettings)) {
			
		}
		ISettings settings = (ISettings) parameter.getConfiguration();
		OperationStatus status = new OperationStatus(settings, parameter);
		PlaceholderConfiguration placeholderConfiguration = status.getPlaceholderConfiguration();
		// set Original PDF Document Data
		status.getPdfObject().setOriginalDocument(parameter.getDataSource().getByteData());
		
		
		// Placeholder search?
		if(placeholderConfiguration.isGlobalPlaceholderEnabled()) {
			// TODO: Do placeholder search
		}
		
		RequestedSignature requestedSignature = new RequestedSignature(status);
		
		// TODO get Certificate
		
		if(requestedSignature.isVisual()) {
			// TODO:  SignBlockCreationStage  (visual) -> create visual signature block (logicaly)

            // TODO:  PositioningStage (visual)  -> find position or use fixed position

            // TODO:  StampingStage (visual) -> stamp logical signature block to location (itext)
		} else {
			// Stamped Object is equal to original
			status.getPdfObject().setStampedDocument(status.getPdfObject().getOriginalDocument());
		}
		
		// TODO: Create signature
		
		return null;
	}

	public List<VerifyResult> verify(VerifyParameter parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	public Configuration getConfiguration() {
		return new ConfigurationImpl();
	}
	
}
