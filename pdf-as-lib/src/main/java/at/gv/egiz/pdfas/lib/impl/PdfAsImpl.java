package at.gv.egiz.pdfas.lib.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.Settings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.configuration.ConfigurationImpl;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderConfiguration;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;
import at.gv.egiz.pdfas.lib.impl.positioning.Positioning;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.stamping.StamperFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PdfAsImpl implements PdfAs, IConfigurationConstants {

	 private static final Logger logger = LoggerFactory.getLogger(PdfAsImpl.class);
	
	private Settings settings;
	
	public PdfAsImpl(File cfgFile) {
		logger.info("Initializing PDF-AS with config: " + cfgFile.getPath());
		this.settings = new Settings(cfgFile);
	}
	
	public SignResult sign(SignParameter parameter) throws PdfAsException {

		logger.trace("sign started");
		
		// TODO: verify signParameter

		try {
			// Status initialization
			if (!(parameter.getConfiguration() instanceof ISettings)) {
				throw new PdfAsSettingsException("Invalid settings object!");
			}
						
			ISettings settings = (ISettings) parameter.getConfiguration();
			OperationStatus status = new OperationStatus(settings, parameter);
			PlaceholderConfiguration placeholderConfiguration = status
					.getPlaceholderConfiguration();

			RequestedSignature requestedSignature = new RequestedSignature(
					status);
			// Only use this profileID because validation was done in
			// RequestedSignature
			String signatureProfileID = requestedSignature
					.getSignatureProfileID();

			logger.info("Selected signature Profile: " + signatureProfileID);
			
			SignatureProfileConfiguration signatureProfileConfiguration = status
					.getSignatureProfileConfiguration(signatureProfileID);

			// set Original PDF Document Data
			status.getPdfObject().setOriginalDocument(
					parameter.getDataSource().getByteData());

			// Placeholder search?
			if (placeholderConfiguration.isGlobalPlaceholderEnabled()) {
				// TODO: Do placeholder search
			}

			// TODO get Certificate

			if (requestedSignature.isVisual()) {
				logger.info("Creating visual siganture block");
				// ================================================================
				// SignBlockCreationStage (visual) -> create visual signature
				// block (logicaly)
				SignatureProfileSettings signatureProfileSettings = TableFactory
						.createProfile(signatureProfileID, settings);

				Table main = TableFactory.createSigTable(
						signatureProfileSettings, MAIN, settings);

				IPDFStamper stamper = StamperFactory.createDefaultStamper(settings);
				IPDFVisualObject visualObject = stamper.createVisualPDFObject(
						status.getPdfObject(), main);

				// ================================================================
				// PositioningStage (visual) -> find position or use fixed
				// position

				String posString = status.getSignParamter()
						.getSignaturePosition();

				if (posString == null) {
					posString = signatureProfileConfiguration
							.getDefaultPositioning();
				}

				TablePos tablePos = null;

				if (posString == null) {
					tablePos = new TablePos();
				} else {
					tablePos = new TablePos(posString);
				}

				PDDocument originalDocument = PDDocument
						.load(new ByteArrayInputStream(status.getPdfObject()
								.getOriginalDocument()));

				PositioningInstruction positioningInstruction = Positioning
						.determineTablePositioning(tablePos, "",
								originalDocument, visualObject);

				// ================================================================
				// StampingStage (visual) -> stamp logical signature block to
				// location (itext)

				byte[] incrementalUpdate = stamper.writeVisualObject(
						visualObject, positioningInstruction, status
								.getPdfObject().getOriginalDocument());
				status.getPdfObject().setStampedDocument(incrementalUpdate);
			} else {
				logger.info("No visual siganture block");
				// Stamped Object is equal to original
				status.getPdfObject().setStampedDocument(
						status.getPdfObject().getOriginalDocument());
			}

			// TODO: Create signature

			return null;
		} catch (Throwable e) {
			logger.error("sign failed " + e.getMessage(), e);
			throw new PdfAsException("sign Failed", e);
		} finally {
			logger.trace("sign done");
		}
	}

	public List<VerifyResult> verify(VerifyParameter parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	public Configuration getConfiguration() {
		return new ConfigurationImpl(this.settings);
	}

}
