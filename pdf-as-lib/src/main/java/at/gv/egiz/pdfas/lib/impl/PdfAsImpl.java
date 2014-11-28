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

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.Settings;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.preprocessor.PreProcessor;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.backend.PDFASBackend;
import at.gv.egiz.pdfas.lib.impl.backend.BackendLoader;
import at.gv.egiz.pdfas.lib.impl.configuration.ConfigurationImpl;
import at.gv.egiz.pdfas.lib.impl.preprocessor.PreProcessorLoader;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.PDFASSignatureExtractor;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.util.SignatureUtils;
import at.gv.egiz.sl.util.BKUHeader;

public class PdfAsImpl implements PdfAs, IConfigurationConstants,
		ErrorConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(PdfAsImpl.class);

	private ISettings settings;

	public PdfAsImpl(File cfgFile) {
		logger.info("Initializing PDF-AS with config: " + cfgFile.getPath());
		this.settings = new Settings(cfgFile);
	}

	public PdfAsImpl(ISettings cfgObject) {
		logger.info("Initializing PDF-AS with config: "
				+ cfgObject.getClass().getName());
		this.settings = cfgObject;
	}

	private void verifySignParameter(SignParameter parameter) throws PDFASError {
		// Status initialization
		if (!(parameter.getConfiguration() instanceof ISettings)) {
			throw new PDFASError(ERROR_SET_INVALID_SETTINGS_OBJ);
		}

		ISettings settings = (ISettings) parameter.getConfiguration();

		String signatureProfile = parameter.getSignatureProfileId();
		if (signatureProfile != null) {
			if (!settings.hasPrefix("sig_obj." + signatureProfile)) {
				throw new PDFASError(ERROR_SIG_INVALID_PROFILE,
						PDFASError.buildInfoString(ERROR_SIG_INVALID_PROFILE,
								signatureProfile));
			}
		}

		if (parameter.getDataSource() == null) {
			throw new PDFASError(ERROR_NO_INPUT);
		}

	}

	private void verifyVerifyParameter(VerifyParameter parameter)
			throws PDFASError {
		// Status initialization
		if (!(parameter.getConfiguration() instanceof ISettings)) {
			throw new PDFASError(ERROR_SET_INVALID_SETTINGS_OBJ);
		}

		if (parameter.getDataSource() == null) {
			throw new PDFASError(ERROR_NO_INPUT);
		}
	}

	public SignResult sign(SignParameter parameter) throws PDFASError {

		logger.trace("sign started");

		verifySignParameter(parameter);
		OperationStatus status = null;
		try {
			// Status initialization
			if (!(parameter.getConfiguration() instanceof ISettings)) {
				throw new PdfAsSettingsException("Invalid settings object!");
			}

			// execute pre Processors
			signPreProcessing(parameter);

			// allocated Backend
			PDFASBackend backend = BackendLoader.getPDFASBackend();

			if (backend == null) {
				throw new PDFASError(ERROR_NO_BACKEND);
			}

			ISettings settings = (ISettings) parameter.getConfiguration();
			status = new OperationStatus(settings, parameter, backend);

			IPdfSigner signer = backend.getPdfSigner();

			status.setPdfObject(signer.buildPDFObject(status));

			// set Original PDF Document Data
			status.getPdfObject()
					.setOriginalDocument(parameter.getDataSource());

			// Check PDF Permissions
			signer.checkPDFPermissions(status.getPdfObject());

			// PlaceholderConfiguration placeholderConfiguration = status
			// .getPlaceholderConfiguration();

			RequestedSignature requestedSignature = new RequestedSignature(
					status);

			status.setRequestedSignature(requestedSignature);

			try {
			requestedSignature.setCertificate(status.getSignParamter()
					.getPlainSigner().getCertificate(parameter));
			} finally {
				if (parameter instanceof BKUHeaderHolder) {
					BKUHeaderHolder holder = (BKUHeaderHolder) parameter;

					Iterator<BKUHeader> bkuHeaderIt = holder.getProcessInfo()
							.iterator();

					while (bkuHeaderIt.hasNext()) {
						BKUHeader header = bkuHeaderIt.next();
						if ("Server".equalsIgnoreCase(header.getName())) {
							requestedSignature
									.getStatus()
									.getMetaInformations()
									.put(ErrorConstants.STATUS_INFO_SIGDEVICEVERSION,
											header.getValue());
						} else if (ErrorConstants.STATUS_INFO_SIGDEVICE.equalsIgnoreCase(header.getName())) {
							requestedSignature
							.getStatus()
							.getMetaInformations()
							.put(ErrorConstants.STATUS_INFO_SIGDEVICE,
									header.getValue());
						}
					}
				}
			}
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
			try {
				signer.signPDF(status.getPdfObject(), requestedSignature, signer
					.buildSignaturInterface(status.getSignParamter()
							.getPlainSigner(), parameter, requestedSignature));
			} finally {
				if (parameter instanceof BKUHeaderHolder) {
					BKUHeaderHolder holder = (BKUHeaderHolder) parameter;

					Iterator<BKUHeader> bkuHeaderIt = holder.getProcessInfo()
							.iterator();

					while (bkuHeaderIt.hasNext()) {
						BKUHeader header = bkuHeaderIt.next();
						if ("Server".equalsIgnoreCase(header.getName())) {
							requestedSignature
									.getStatus()
									.getMetaInformations()
									.put(ErrorConstants.STATUS_INFO_SIGDEVICEVERSION,
											header.getValue());
						} else if (ErrorConstants.STATUS_INFO_SIGDEVICE.equalsIgnoreCase(header.getName())) {
							requestedSignature
							.getStatus()
							.getMetaInformations()
							.put(ErrorConstants.STATUS_INFO_SIGDEVICE,
									header.getValue());
						}
					}
				}
			}
			// ================================================================
			// Create SignResult
			SignResult result = createSignResult(status);

			return result;
		} catch (Throwable e) {
			logger.warn("Failed to create signature [" + e.getMessage() + "]",
					e);
			throw ErrorExtractor.searchPdfAsError(e, status);
		} finally {
			if (status != null) {
				status.clear();
			}
			logger.trace("sign done");
		}
	}

	public List<VerifyResult> verify(VerifyParameter parameter)
			throws PDFASError {

		verifyVerifyParameter(parameter);

		// execute pre Processors
		verifyPreProcessing(parameter);

		// allocated Backend
		PDFASBackend backend = BackendLoader.getPDFASBackend();

		if (backend == null) {
			throw new PDFASError(ERROR_NO_BACKEND);
		}

		try {
			return backend.getVerifier().verify(parameter);
		} catch (Throwable e) {
			throw ErrorExtractor.searchPdfAsError(e, null);
		}
	}

	public Configuration getConfiguration() {
		return new ConfigurationImpl(this.settings);
	}

	public StatusRequest startSign(SignParameter parameter) throws PDFASError {

		verifySignParameter(parameter);

		StatusRequestImpl request = new StatusRequestImpl();
		OperationStatus status = null;
		try {
			// Status initialization
			if (!(parameter.getConfiguration() instanceof ISettings)) {
				throw new PdfAsSettingsException("Invalid settings object!");
			}

			// execute pre Processors
			signPreProcessing(parameter);

			// allocated Backend
			PDFASBackend backend = BackendLoader.getPDFASBackend();

			if (backend == null) {
				throw new PDFASError(ERROR_NO_BACKEND);
			}

			ISettings settings = (ISettings) parameter.getConfiguration();
			status = new OperationStatus(settings, parameter,
					backend);

			IPdfSigner signer = backend.getPdfSigner();

			status.setPdfObject(signer.buildPDFObject(status));

			RequestedSignature requestedSignature = new RequestedSignature(
					status);

			status.setRequestedSignature(requestedSignature);

			request.setStatus(status);

			request.setNeedCertificate(true);

			return request;
		} catch (Throwable e) {
			logger.warn("startSign", e);
			throw ErrorExtractor.searchPdfAsError(e, status);
		}
	}

	public StatusRequest process(StatusRequest statusRequest) throws PDFASError {
		if (!(statusRequest instanceof StatusRequestImpl)) {
			throw new PDFASError(ERROR_SIG_INVALID_STATUS);
		}

		StatusRequestImpl request = (StatusRequestImpl) statusRequest;
		OperationStatus status = request.getStatus();

		if (request.needCertificate()) {
			try {
				status.getRequestedSignature().setCertificate(
						request.getCertificate());

				// set Original PDF Document Data
				status.getPdfObject().setOriginalDocument(
						status.getSignParamter().getDataSource());

				// STAMPER!
				// stampPdf(status);
				request.setNeedCertificate(false);

				status.setSigningDate(Calendar.getInstance());

				// GET Signature DATA
				String pdfFilter = status.getSignParamter().getPlainSigner()
						.getPDFFilter();
				String pdfSubFilter = status.getSignParamter().getPlainSigner()
						.getPDFSubFilter();

				IPdfSigner signer = status.getBackend().getPdfSigner();

				PDFASSignatureExtractor signatureDataExtractor = signer
						.buildBlindSignaturInterface(request.getCertificate(),
								pdfFilter, pdfSubFilter,
								status.getSigningDate());

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
				logger.warn("process", e);
				throw ErrorExtractor.searchPdfAsError(e, status);
			}
		} else if (request.needSignature()) {
			request.setNeedSignature(false);
			// Inject signature byte[] into signedDocument
			int offset = request.getSignatureDataByteRange()[1] + 1;

			byte[] pdfSignature = status.getBackend().getPdfSigner()
					.rewritePlainSignature(request.getSignature());
			// byte[] input =
			// PDFUtils.blackOutSignature(status.getPdfObject().getSignedDocument(),
			// request.getSignatureDataByteRange());
			VerifyResult verifyResult = SignatureUtils.verifySignature(
					request.getSignature(), request.getSignatureData());
			RequestedSignature requestedSignature = request.getStatus()
					.getRequestedSignature();

			if (!StreamUtils.dataCompare(requestedSignature.getCertificate()
					.getFingerprintSHA(), ((X509Certificate) verifyResult
					.getSignerCertificate()).getFingerprintSHA())) {
				throw new PDFASError(ERROR_SIG_CERTIFICATE_MISSMATCH);
			}

			for (int i = 0; i < pdfSignature.length; i++) {
				status.getPdfObject().getSignedDocument()[offset + i] = pdfSignature[i];
			}
			request.setIsReady(true);
		} else {
			throw new PDFASError(ERROR_SIG_INVALID_STATUS);
		}

		return request;
	}

	public SignResult finishSign(StatusRequest statusRequest) throws PDFASError {
		if (!(statusRequest instanceof StatusRequestImpl)) {
			throw new PDFASError(ERROR_SIG_INVALID_STATUS);
		}

		StatusRequestImpl request = (StatusRequestImpl) statusRequest;
		OperationStatus status = request.getStatus();

		if (!request.isReady()) {
			throw new PDFASError(ERROR_SIG_INVALID_STATUS);
		}

		try {
			return createSignResult(status);
		} catch (IOException e) {
			// new PdfAsException("error.pdf.sig.06", e);
			throw ErrorExtractor.searchPdfAsError(e, status);
		} finally {
			if (status != null) {
				status.clear();
			}
		}
	}

	private void listPreProcessors(List<PreProcessor> preProcessors) {
		logger.debug("--------------");
		logger.debug("Listing PreProcessors:");

		Iterator<PreProcessor> preProcessorsIterator = preProcessors.iterator();
		int idx = 0;
		while (preProcessorsIterator.hasNext()) {
			PreProcessor preProcessor = preProcessorsIterator.next();
			logger.debug("{}: {} [{}]", idx, preProcessor.getName(),
					preProcessor.getClass().getName());
			idx++;
		}
		logger.debug("--------------");
	}

	private void verifyPreProcessing(VerifyParameter parameter)
			throws PDFASError {
		List<PreProcessor> preProcessors = PreProcessorLoader
				.getPreProcessors(parameter.getConfiguration());

		listPreProcessors(preProcessors);

		logger.debug("executing PreProcessors for verifing:");
		Iterator<PreProcessor> preProcessorsIterator = preProcessors.iterator();

		while (preProcessorsIterator.hasNext()) {
			PreProcessor preProcessor = preProcessorsIterator.next();
			logger.debug("executing: {} [{}]", preProcessor.getName(),
					preProcessor.getClass().getName());
			preProcessor.verify(parameter);
			logger.debug("done executing: {} [{}]", preProcessor.getName(),
					preProcessor.getClass().getName());
		}

		logger.debug("executing PreProcessors for verifing done");
	}

	private void signPreProcessing(SignParameter parameter) throws PDFASError {
		List<PreProcessor> preProcessors = PreProcessorLoader
				.getPreProcessors(parameter.getConfiguration());

		listPreProcessors(preProcessors);

		logger.debug("executing PreProcessors for signing:");
		Iterator<PreProcessor> preProcessorsIterator = preProcessors.iterator();

		while (preProcessorsIterator.hasNext()) {
			PreProcessor preProcessor = preProcessorsIterator.next();
			logger.debug("executing: {} [{}]", preProcessor.getName(),
					preProcessor.getClass().getName());
			preProcessor.sign(parameter);
			logger.debug("done executing: {} [{}]", preProcessor.getName(),
					preProcessor.getClass().getName());
		}

		logger.debug("executing PreProcessors for signing done");
	}

	private SignResult createSignResult(OperationStatus status)
			throws IOException {
		// ================================================================
		// Create SignResult
		SignResultImpl result = new SignResultImpl();
		status.getSignParamter().getSignatureResult().write(status.getPdfObject().getSignedDocument());
		status.getSignParamter().getSignatureResult().flush();
		result.setSignerCertificate(status.getRequestedSignature()
				.getCertificate());
		result.setSignaturePosition(status.getRequestedSignature()
				.getSignaturePosition());
		result.getProcessInformations().putAll(status.getMetaInformations());
		return result;
	}

	public Image generateVisibleSignaturePreview(SignParameter parameter,
			java.security.cert.X509Certificate cert, int resolution)
			throws PDFASError {

		OperationStatus status = null;
		try {
			// Status initialization
			if (!(parameter.getConfiguration() instanceof ISettings)) {
				throw new PDFASError(ERROR_SET_INVALID_SETTINGS_OBJ);
			}
			X509Certificate iaikCert;
			if (!(cert instanceof X509Certificate)) {
				iaikCert = new X509Certificate(cert.getEncoded());
			} else {
				iaikCert = (X509Certificate) cert;
			}
			// allocated Backend
			PDFASBackend backend = BackendLoader.getPDFASBackend();

			ISettings settings = (ISettings) parameter.getConfiguration();
			status = new OperationStatus(settings, parameter, backend);

			IPdfSigner signer = backend.getPdfSigner();

			status.setPdfObject(signer.buildPDFObject(status));

			RequestedSignature requestedSignature = new RequestedSignature(
					status);
			requestedSignature.setCertificate(iaikCert);

			if (!requestedSignature.isVisual()) {
				logger.warn("Profile is invisible so not block image is generated");
				return null;
			}

			return signer.generateVisibleSignaturePreview(parameter, iaikCert,
					resolution, status, requestedSignature);
		} catch (PdfAsException e) {
			logger.warn("PDF-AS  Exception", e);
			throw ErrorExtractor.searchPdfAsError(e, status);
		} catch (Throwable e) {
			logger.warn("Throwable  Exception", e);
			throw ErrorExtractor.searchPdfAsError(e, status);
		}

	}
}
