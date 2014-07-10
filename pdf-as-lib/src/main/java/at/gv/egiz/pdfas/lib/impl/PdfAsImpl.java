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
package at.gv.egiz.pdfas.lib.impl;

import iaik.x509.X509Certificate;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsValidationException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.Settings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.configuration.ConfigurationImpl;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;
import at.gv.egiz.pdfas.lib.impl.positioning.Positioning;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.PdfSignerFactory;
import at.gv.egiz.pdfas.lib.impl.signing.pdfbox.PdfboxSignerWrapper;
import at.gv.egiz.pdfas.lib.impl.signing.sig_interface.SignatureDataExtractor;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.stamping.StamperFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PDFAsVisualSignatureProperties;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PdfBoxVisualObject;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifyFilter;
import at.gv.egiz.pdfas.lib.impl.verify.VerifierDispatcher;
import at.gv.egiz.pdfas.lib.util.SignatureUtils;
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
			if (!settings.hasPrefix("sig_obj." + signatureProfile)) {
				throw new PdfAsValidationException("error.pdf.sig.09",
						signatureProfile);
			}
		}

		if (parameter.getDataSource() == null
				|| parameter.getDataSource().getByteData() == null) {
			throw new PdfAsValidationException("error.pdf.sig.10", null);
		}

		if (parameter.getOutput() == null) {
			throw new PdfAsValidationException("error.pdf.sig.11", null);
		}
	}

	private void verifyVerifyParameter(VerifyParameter parameter)
			throws PdfAsException {
		// Status initialization
		if (!(parameter.getConfiguration() instanceof ISettings)) {
			throw new PdfAsSettingsException("Invalid settings object!");
		}

		if (parameter.getDataSource() == null
				|| parameter.getDataSource().getByteData() == null) {
			throw new PdfAsValidationException("error.pdf.verify.01", null);
		}
	}

	public SignResult sign(SignParameter parameter) throws PdfAsException {

		logger.trace("sign started");

		verifySignParameter(parameter);
		OperationStatus status = null;
		try {
			// Status initialization
			if (!(parameter.getConfiguration() instanceof ISettings)) {
				throw new PdfAsSettingsException("Invalid settings object!");
			}

			ISettings settings = (ISettings) parameter.getConfiguration();
			status = new OperationStatus(settings, parameter);

			// set Original PDF Document Data
			status.getPdfObject().setOriginalDocument(
					parameter.getDataSource().getByteData());

			PDDocument doc = status.getPdfObject().getDocument();
			PDFUtils.checkPDFPermissions(doc);

			// PlaceholderConfiguration placeholderConfiguration = status
			// .getPlaceholderConfiguration();

			RequestedSignature requestedSignature = new RequestedSignature(
					status);

			status.setRequestedSignature(requestedSignature);

			requestedSignature.setCertificate(status.getSignParamter()
					.getPlainSigner().getCertificate(parameter));

			// Only use this profileID because validation was done in
			// RequestedSignature
			String signatureProfileID = requestedSignature
					.getSignatureProfileID();

			logger.info("Selected signature Profile: " + signatureProfileID);

			// SignatureProfileConfiguration signatureProfileConfiguration =
			// status
			// .getSignatureProfileConfiguration(signatureProfileID);

			// this.stampPdf(status);

			// Create signature
			IPdfSigner signer = PdfSignerFactory.createPdfSigner();
			signer.signPDF(status.getPdfObject(), requestedSignature,
					new PdfboxSignerWrapper(status.getSignParamter()
							.getPlainSigner(), parameter, requestedSignature));

			// ================================================================
			// Create SignResult
			SignResult result = createSignResult(status);

			return result;
		} catch (Throwable e) {
			logger.error("Failed to create signature [" + e.getMessage() + "]",
					e);
			throw new PdfAsException("error.pdf.sig.01", e);
		} finally {
			if (status != null) {
				status.clear();
			}
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
				// stampPdf(status);
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
			//byte[] input = PDFUtils.blackOutSignature(status.getPdfObject().getSignedDocument(), 
			//		request.getSignatureDataByteRange());
			VerifyResult verifyResult = SignatureUtils.verifySignature(request.getSignature(), request.getSignatureData());
			RequestedSignature requestedSignature = request.getStatus().getRequestedSignature();
			
			if(!StreamUtils.dataCompare(requestedSignature.getCertificate().getFingerprintSHA(),
					verifyResult.getSignerCertificate().getFingerprintSHA())) {
				throw new PdfAsSignatureException("Certificates missmatch!");
			}
			
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
		} finally {
			if (status != null) {
				status.clear();
			}
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

	public Image generateVisibleSignaturePreview(SignParameter parameter, X509Certificate cert, int resolution)
			throws PdfAsException {
		
		OperationStatus status = null;
		try {
			// Status initialization
			if (!(parameter.getConfiguration() instanceof ISettings)) {
				throw new PdfAsSettingsException("Invalid settings object!");
			}
			
			ISettings settings = (ISettings) parameter.getConfiguration();
			status = new OperationStatus(settings, parameter);
		
			RequestedSignature requestedSignature = new RequestedSignature(
					status);
			requestedSignature.setCertificate(cert);
			
			if (!requestedSignature.isVisual()) {
				logger.warn("Profile is invisible so not block image is generated");
				return null;
			}
			
			PDFObject pdfObject = status.getPdfObject();

			PDDocument origDoc = new PDDocument();
			origDoc.addPage(new PDPage(PDPage.PAGE_SIZE_A4));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			origDoc.save(baos);
			baos.close();
			
			pdfObject.setOriginalDocument(baos.toByteArray());

			SignatureProfileSettings signatureProfileSettings = TableFactory
					.createProfile(requestedSignature.getSignatureProfileID(),
							pdfObject.getStatus().getSettings());
			
			// create Table describtion
			Table main = TableFactory.createSigTable(
						signatureProfileSettings, MAIN, pdfObject.getStatus(),
						requestedSignature);

			IPDFStamper stamper = StamperFactory
						.createDefaultStamper(pdfObject.getStatus()
						.getSettings());

			IPDFVisualObject visualObject = stamper.createVisualPDFObject(
						pdfObject, main);
					
			SignatureProfileConfiguration signatureProfileConfiguration = pdfObject
					.getStatus().getSignatureProfileConfiguration(
							requestedSignature.getSignatureProfileID());
			
			String signaturePosString = signatureProfileConfiguration
					.getDefaultPositioning();
			PositioningInstruction positioningInstruction = null;
			if(signaturePosString != null) {
				positioningInstruction = Positioning.determineTablePositioning(new TablePos(signaturePosString), "", origDoc,
						visualObject, false);
			} else {
				positioningInstruction = Positioning.determineTablePositioning(new TablePos(), "", origDoc,
						visualObject, false);
			}
			
			origDoc.close();
			
			SignaturePositionImpl position = new SignaturePositionImpl();
			position.setX(positioningInstruction.getX());
			position.setY(positioningInstruction.getY());
			position.setPage(positioningInstruction.getPage());
			position.setHeight(visualObject.getHeight());
			position.setWidth(visualObject.getWidth());

			requestedSignature.setSignaturePosition(position);
			
			PDFAsVisualSignatureProperties properties = new PDFAsVisualSignatureProperties(
				pdfObject.getStatus().getSettings(), pdfObject, (PdfBoxVisualObject) visualObject,
				positioningInstruction);

			properties.buildSignature();
			PDDocument visualDoc = PDDocument.load(properties.getVisibleSignature());
			//PDPageable pageable = new PDPageable(visualDoc);
			List<PDPage> pages = new ArrayList<PDPage>();
			visualDoc.getDocumentCatalog().getPages().getAllKids(pages);
			
			PDPage firstPage = pages.get(0);
			
			float stdRes = 72;
			float targetRes = resolution;
			float factor = targetRes / stdRes;
			
			BufferedImage outputImage = firstPage.convertToImage(BufferedImage.TYPE_4BYTE_ABGR, (int)targetRes);
			
			BufferedImage cutOut = new BufferedImage((int)(position.getWidth() * factor), (int)(position.getHeight() * factor), 
					BufferedImage.TYPE_4BYTE_ABGR);
			
			Graphics2D graphics = (Graphics2D) cutOut.getGraphics();
	        
	        graphics.drawImage(outputImage, 0, 0, cutOut.getWidth(), cutOut.getHeight(),
	        		(int)(1 * factor), 
	        		(int)(outputImage.getHeight() - ((position.getHeight() + 1) * factor)),
	        		(int)((1 + position.getWidth()) * factor), 
	        		(int)(outputImage.getHeight() - ((position.getHeight() + 1) * factor) + (position.getHeight() * factor)),
	        		null);
			return cutOut;
		} catch(PdfAsException e) {
			logger.error("PDF-AS  Exception", e);
			throw e;
		}	catch(Throwable e) {
			logger.error("Throwable  Exception", e);
			throw new PdfAsException("", e);
		}
		
	}
}
