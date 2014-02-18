package at.gv.egiz.pdfas.lib.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsValidationException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.Settings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.configuration.ConfigurationImpl;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderConfiguration;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.positioning.Positioning;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.PdfSignerFactory;
import at.gv.egiz.pdfas.lib.impl.signing.pdfbox.PdfboxSignerWrapper;
import at.gv.egiz.pdfas.lib.impl.signing.sig_interface.SignatureDataExtractor;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.stamping.StamperFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifyFilter;
import at.gv.egiz.pdfas.lib.impl.verify.VerifierDispatcher;
import at.knowcenter.wag.egov.egiz.pdf.PDFUtilities;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PdfAsImpl implements PdfAs, IConfigurationConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(PdfAsImpl.class);

	private Settings settings;

	public PdfAsImpl(File cfgFile) {
		logger.info("Initializing PDF-AS with config: " + cfgFile.getPath());
		this.settings = new Settings(cfgFile);
	}

	private void verifySignParameter(SignParameter parameter)
			throws PdfAsException {
		// Status initialization
		if (!(parameter.getConfiguration() instanceof ISettings)) {
			throw new PdfAsSettingsException("Invalid settings object!");
		}

		ISettings settings = (ISettings) parameter.getConfiguration();

		String signatureProfile = parameter.getSignatureProfileId();
		if (signatureProfile != null) {
			if (!settings.hasPrefix("sig_obj." + signatureProfile + ".key")) {
				throw new PdfAsValidationException("error.pdf.sig.09",
						signatureProfile);
			}
		}

		if(parameter.getDataSource() == null || parameter.getDataSource().getByteData() == null) {
			throw new PdfAsValidationException("error.pdf.sig.10", null);
		}
		
		if(parameter.getOutput() == null) {
			throw new PdfAsValidationException("error.pdf.sig.11", null);
		}
		
		try {
			PDDocument doc = PDDocument.load(new ByteArrayInputStream(parameter.getDataSource().getByteData()));
			PDFUtils.checkPDFPermissions(doc);
			doc.close();
		} catch(IOException e) {
			throw new PdfAsValidationException("error.pdf.sig.12", null, e);
		}
		
		// TODO: verify Sign Parameter
	}

	private void verifyVerifyParameter(VerifyParameter parameter)
			throws PdfAsException {
		// Status initialization
		if (!(parameter.getConfiguration() instanceof ISettings)) {
			throw new PdfAsSettingsException("Invalid settings object!");
		}
		
		if(parameter.getDataSource() == null || parameter.getDataSource().getByteData() == null) {
			throw new PdfAsValidationException("error.pdf.verify.01", null);
		}

		// TODO: verify Verify Parameter
	}

	public SignResult sign(SignParameter parameter) throws PdfAsException {

		logger.trace("sign started");

		verifySignParameter(parameter);

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

			status.setRequestedSignature(requestedSignature);

			requestedSignature.setCertificate(status.getSignParamter()
					.getPlainSigner().getCertificate());

			// Only use this profileID because validation was done in
			// RequestedSignature
			String signatureProfileID = requestedSignature
					.getSignatureProfileID();

			logger.info("Selected signature Profile: " + signatureProfileID);

			// SignatureProfileConfiguration signatureProfileConfiguration =
			// status
			// .getSignatureProfileConfiguration(signatureProfileID);

			// set Original PDF Document Data
			status.getPdfObject().setOriginalDocument(
					parameter.getDataSource().getByteData());

			this.stampPdf(status);

			// Create signature
			IPdfSigner signer = PdfSignerFactory.createPdfSigner();
			signer.signPDF(status.getPdfObject(), requestedSignature,
					new PdfboxSignerWrapper(status.getSignParamter()
							.getPlainSigner()));

			// ================================================================
			// Create SignResult
			SignResultImpl result = new SignResultImpl(status.getSignParamter()
					.getOutput());
			OutputStream outputStream = result.getOutputDocument()
					.createOutputStream();

			outputStream.write(status.getPdfObject().getSignedDocument());

			outputStream.close();

			return result;
		} catch (Throwable e) {
			logger.error("Failed to create signature [" + e.getMessage() + "]",
					e);
			throw new PdfAsException("error.pdf.sig.01", e);
		} finally {
			logger.trace("sign done");
		}
	}

	public List<VerifyResult> verify(VerifyParameter parameter)
			throws PdfAsException {

		verifyVerifyParameter(parameter);

		int signatureToVerify = parameter.getWhichSignature();
		int currentSignature = 0;
		PDDocument doc = null;
		try {
			List<VerifyResult> result = new ArrayList<VerifyResult>();
			ISettings settings = (ISettings) parameter.getConfiguration();
			VerifierDispatcher verifier = new VerifierDispatcher(settings);
			doc = PDDocument.load(new ByteArrayInputStream(parameter
					.getDataSource().getByteData()));

			COSDictionary trailer = doc.getDocument().getTrailer();
			COSDictionary root = (COSDictionary) trailer
					.getDictionaryObject(COSName.ROOT);
			COSDictionary acroForm = (COSDictionary) root
					.getDictionaryObject(COSName.ACRO_FORM);
			COSArray fields = (COSArray) acroForm
					.getDictionaryObject(COSName.FIELDS);
			for (int i = 0; i < fields.size(); i++) {
				COSDictionary field = (COSDictionary) fields.getObject(i);
				String type = field.getNameAsString("FT");
				if ("Sig".equals(type)) {
					boolean verifyThis = true;

					if (signatureToVerify >= 0) {
						// verify only specific siganture!
						verifyThis = signatureToVerify == currentSignature;
					}

					if (verifyThis) {
						logger.trace("Found Signature: ");
						COSBase base = field.getDictionaryObject("V");
						COSDictionary dict = (COSDictionary) base;

						logger.debug("Signer: " + dict.getNameAsString("Name"));
						logger.debug("SubFilter: "
								+ dict.getNameAsString("SubFilter"));
						logger.debug("Filter: "
								+ dict.getNameAsString("Filter"));
						logger.debug("Modified: " + dict.getNameAsString("M"));
						COSArray byteRange = (COSArray) dict
								.getDictionaryObject("ByteRange");

						StringBuilder sb = new StringBuilder();
						int[] bytes = new int[byteRange.size()];
						for (int j = 0; j < byteRange.size(); j++) {
							bytes[j] = byteRange.getInt(j);
							sb.append(" " + bytes[j]);
						}

						logger.debug("ByteRange" + sb.toString());

						COSString content = (COSString) dict
								.getDictionaryObject("Contents");

						ByteArrayOutputStream contentData = new ByteArrayOutputStream();
						for (int j = 0; j < bytes.length; j = j + 2) {
							int offset = bytes[j];
							int length = bytes[j + 1];
							contentData.write(parameter.getDataSource()
									.getByteData(), offset, length);
						}
						contentData.close();

						IVerifyFilter verifyFilter = verifier.getVerifier(
								dict.getNameAsString("Filter"),
								dict.getNameAsString("SubFilter"));

						if (verifyFilter != null) {
							List<VerifyResult> results = verifyFilter.verify(
									contentData.toByteArray(),
									content.getBytes(), 
									parameter.getVerificationTime(), bytes);
							if (results != null && !results.isEmpty()) {
								result.addAll(results);
							}
						}
					}
					currentSignature++;
				}
			}
			return result;
		} catch (IOException e) {
			logger.error("Failed to verify document", e);
			throw new PDFIOException("error.pdf.verify.02", e);
		} catch (PdfAsException e) {
			logger.error("Failed to verify document", e);
			throw new PdfAsException("error.pdf.verify.02", e);
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					logger.info("Failed to close doc");
				}
			}
		}
	}

	public Configuration getConfiguration() {
		return new ConfigurationImpl(this.settings);
	}

	public StatusRequest startSign(SignParameter parameter)
			throws PdfAsException {

		verifySignParameter(parameter);
		
		StatusRequestImpl request = new StatusRequestImpl();

		try {
			// Status initialization
			if (!(parameter.getConfiguration() instanceof ISettings)) {
				throw new PdfAsSettingsException("Invalid settings object!");
			}

			ISettings settings = (ISettings) parameter.getConfiguration();
			OperationStatus status = new OperationStatus(settings, parameter);

			RequestedSignature requestedSignature = new RequestedSignature(
					status);

			status.setRequestedSignature(requestedSignature);

			request.setStatus(status);

			request.setNeedCertificate(true);

			return request;
		} catch (Throwable e) {
			logger.error("startSign", e);
			throw new PdfAsException("error.pdf.sig.03", e);
		}
	}

	public StatusRequest process(StatusRequest statusRequest)
			throws PdfAsException {
		if (!(statusRequest instanceof StatusRequestImpl)) {
			throw new PdfAsException("error.pdf.sig.04");
		}

		StatusRequestImpl request = (StatusRequestImpl) statusRequest;
		OperationStatus status = request.getStatus();

		if (request.needCertificate()) {
			try {
				status.getRequestedSignature().setCertificate(
						request.getCertificate());

				// set Original PDF Document Data
				status.getPdfObject().setOriginalDocument(
						status.getSignParamter().getDataSource().getByteData());

				// STAMPER!
				stampPdf(status);
				request.setNeedCertificate(false);

				status.setSigningDate(Calendar.getInstance());

				// GET Signature DATA
				String pdfFilter = status.getSignParamter().getPlainSigner()
						.getPDFFilter();
				String pdfSubFilter = status.getSignParamter().getPlainSigner()
						.getPDFSubFilter();
				SignatureDataExtractor signatureDataExtractor = new SignatureDataExtractor(
						request.getCertificate(), pdfFilter, pdfSubFilter,
						status.getSigningDate());

				IPdfSigner signer = PdfSignerFactory.createPdfSigner();
				signer.signPDF(status.getPdfObject(),
						status.getRequestedSignature(), signatureDataExtractor);

				StringBuilder sb = new StringBuilder();

				int[] byteRange = PDFUtils
						.extractSignatureByteRange(signatureDataExtractor
								.getSignatureData());

				for (int i = 0; i < byteRange.length; i++) {
					sb.append(" " + byteRange[i]);
				}

				logger.info("ByteRange: " + sb.toString());

				request.setSignatureData(signatureDataExtractor
						.getSignatureData());
				request.setByteRange(byteRange);
				request.setNeedSignature(true);

			} catch (Throwable e) {
				logger.error("process", e);
				throw new PdfAsException("error.pdf.sig.05", e);
			}
		} else if (request.needSignature()) {
			request.setNeedSignature(false);
			// Inject signature byte[] into signedDocument
			int offset = request.getSignatureDataByteRange()[1] + 1;

			String signature = new COSString(request.getSignature())
					.getHexString();
			byte[] pdfSignature = signature.getBytes();

			for (int i = 0; i < pdfSignature.length; i++) {
				status.getPdfObject().getSignedDocument()[offset + i] = pdfSignature[i];
			}
			request.setIsReady(true);
		} else {
			throw new PdfAsException("error.pdf.sig.04");
		}

		return request;
	}

	public SignResult finishSign(StatusRequest statusRequest)
			throws PdfAsException {
		if (!(statusRequest instanceof StatusRequestImpl)) {
			throw new PdfAsException("error.pdf.sig.04");
		}

		StatusRequestImpl request = (StatusRequestImpl) statusRequest;
		OperationStatus status = request.getStatus();

		if (!request.isReady()) {
			throw new PdfAsException("error.pdf.sig.04");
		}

		try {
			return createSignResult(status);
		} catch (IOException e) {
			throw new PdfAsException("error.pdf.sig.06", e);
		}
	}

	private boolean checkPlaceholderSignature(OperationStatus status)
			throws PdfAsException, IOException {
		if (status.getPlaceholderConfiguration().isGlobalPlaceholderEnabled()) {
			SignaturePlaceholderData signaturePlaceholderData = SignaturePlaceholderExtractor
					.extract(new ByteArrayInputStream(status.getPdfObject()
							.getOriginalDocument()), null, 1);

			if (signaturePlaceholderData != null) {
				RequestedSignature requestedSignature = status
						.getRequestedSignature();

				if (signaturePlaceholderData.getProfile() != null) {
					requestedSignature
							.setSignatureProfileID(signaturePlaceholderData
									.getProfile());
				}

				String signatureProfileID = requestedSignature
						.getSignatureProfileID();

				TablePos tablePos = signaturePlaceholderData.getTablePos();

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

				// ================================================================
				// StampingStage (visual) -> stamp logical signature block to
				// location (itext)

				byte[] incrementalUpdate = stamper.writeVisualObject(
						visualObject, positioningInstruction, status
								.getPdfObject().getOriginalDocument(),
						signaturePlaceholderData.getPlaceholderName());

				SignaturePositionImpl position = new SignaturePositionImpl();
				position.setX(positioningInstruction.getX());
				position.setY(positioningInstruction.getY());
				position.setPage(positioningInstruction.getPage());
				position.setHeight(visualObject.getHeight());
				position.setWidth(visualObject.getWidth());

				requestedSignature.setSignaturePosition(position);

				status.getPdfObject().setStampedDocument(incrementalUpdate);
				return true;
			}
		}
		return false;
	}

	private void stampPdf(OperationStatus status) throws PdfAsException,
			IOException {

		RequestedSignature requestedSignature = status.getRequestedSignature();
		String signatureProfileID = requestedSignature.getSignatureProfileID();
		SignatureProfileConfiguration signatureProfileConfiguration = status
				.getSignatureProfileConfiguration(signatureProfileID);

		if (checkPlaceholderSignature(status)) {
			logger.info("Placeholder found for Signature");
			return;
		}

		if (requestedSignature.isVisual()) {
			logger.info("Creating visual siganture block");
			// ================================================================
			// SignBlockCreationStage (visual) -> create visual signature
			// block (logicaly)
			SignatureProfileSettings signatureProfileSettings = TableFactory
					.createProfile(signatureProfileID, settings);

			Table main = TableFactory.createSigTable(signatureProfileSettings,
					MAIN, settings, requestedSignature);

			IPDFStamper stamper = StamperFactory.createDefaultStamper(settings);
			IPDFVisualObject visualObject = stamper.createVisualPDFObject(
					status.getPdfObject(), main);

			// ================================================================
			// PositioningStage (visual) -> find position or use fixed
			// position

			String posString = status.getSignParamter().getSignaturePosition();

			if (posString == null) {
				posString = signatureProfileConfiguration
						.getDefaultPositioning();
			}

			logger.debug("using Positioning: " + posString);

			boolean legacy32Position = signatureProfileConfiguration
					.getLegacy32Positioning();

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
					.determineTablePositioning(tablePos, "", originalDocument,
							visualObject, legacy32Position);

			// ================================================================
			// StampingStage (visual) -> stamp logical signature block to
			// location (itext)

			byte[] incrementalUpdate = stamper.writeVisualObject(visualObject,
					positioningInstruction, status.getPdfObject()
							.getOriginalDocument(), null);

			SignaturePositionImpl position = new SignaturePositionImpl();
			position.setX(positioningInstruction.getX());
			position.setY(positioningInstruction.getY());
			position.setPage(positioningInstruction.getPage());
			position.setHeight(visualObject.getHeight());
			position.setWidth(visualObject.getWidth());

			requestedSignature.setSignaturePosition(position);

			status.getPdfObject().setStampedDocument(incrementalUpdate);
		} else {
			logger.info("No visual siganture block");
			// Stamped Object is equal to original
			status.getPdfObject().setStampedDocument(
					status.getPdfObject().getOriginalDocument());
		}
	}

	private SignResult createSignResult(OperationStatus status)
			throws IOException {
		// ================================================================
		// Create SignResult
		SignResultImpl result = new SignResultImpl(status.getSignParamter()
				.getOutput());
		OutputStream outputStream = result.getOutputDocument()
				.createOutputStream();

		outputStream.write(status.getPdfObject().getSignedDocument());

		outputStream.close();

		result.setSignerCertificate(status.getRequestedSignature()
				.getCertificate());
		result.setSignaturePosition(status.getRequestedSignature()
				.getSignaturePosition());

		return result;
	}

}
