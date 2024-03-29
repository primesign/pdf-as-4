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
package at.gv.egiz.pdfas.moa;

import at.gv.e_government.reference.namespace.moa._20020822.*;
import iaik.x509.X509Certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.BindingProvider;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.e_government.reference.namespace.moa._20020822.CMSDataObjectInfoType.DataObject;
import at.gv.e_government.reference.namespace.moa._20020822.CreateCMSSignatureRequestType.SingleSignatureInfo;
import at.gv.e_government.reference.namespace.moa._20020822.CreateCMSSignatureRequestType.SingleSignatureInfo.DataObjectInfo;
import at.gv.e_government.reference.namespace.moa._20020822_.MOAFault;
import at.gv.e_government.reference.namespace.moa._20020822_.SignatureCreationPortType;
import at.gv.e_government.reference.namespace.moa._20020822_.SignatureCreationService;
import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsErrorCarrier;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsMOAException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.utils.SettingsUtils;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.util.SignatureUtils;
import at.gv.egiz.sl.util.ISignatureConnector;

public class MOAConnector implements ISignatureConnector,
		IConfigurationConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(MOAConnector.class);

	public static final String SIGNATURE_DEVICE = "MOA";
	
	private X509Certificate certificate;
	private String moaEndpoint;
	private String keyIdentifier;


	public MOAConnector(Configuration config,
			java.security.cert.Certificate certificate)
			throws CertificateException, FileNotFoundException, IOException {
		if(certificate != null) {
			if(certificate instanceof X509Certificate) {
				this.certificate = (X509Certificate)certificate;
			} else {
				this.certificate = new X509Certificate(certificate.getEncoded());
			}
		}
		init(config);
	}

	public MOAConnector(Configuration config) throws CertificateException,
			FileNotFoundException, IOException {
		init(config);
	}

	private void init(Configuration config) throws CertificateException,
			FileNotFoundException, IOException {

		// Load certificate if not set otherwise
		if (this.certificate == null) {

			if (config.getValue(MOA_SIGN_CERTIFICATE) == null) {
				logger.error(MOA_SIGN_CERTIFICATE
						+ " not configured for MOA connector");
				throw new PdfAsWrappedIOException(new PdfAsException(
						"Please configure: " + MOA_SIGN_CERTIFICATE
								+ " to use MOA connector"));
			}

			if (!(config instanceof ISettings)) {
				logger.error("Configuration is no instance of ISettings");
				throw new PdfAsWrappedIOException(new PdfAsException(
						"Configuration is no instance of ISettings"));
			}

			ISettings settings = (ISettings) config;

			String certificateValue = config.getValue(MOA_SIGN_CERTIFICATE);

			if (certificateValue.startsWith("http")) {
				logger.debug("Loading certificate from url: " + certificateValue);

				try {
					URL certificateURL = new URL(certificateValue);

					this.certificate = new X509Certificate(
							certificateURL.openStream());
				} catch (MalformedURLException e) {
					logger.error(certificateValue + " is not a valid url but starts with http!");
					throw new PdfAsWrappedIOException(new PdfAsException(
							certificateValue + " is not a valid url but!"));
				}
			} else {

				File certFile = new File(certificateValue);
				if (!certFile.isAbsolute()) {
					certificateValue = settings.getWorkingDirectory() + "/"
							+ config.getValue(MOA_SIGN_CERTIFICATE);
					certFile = new File(certificateValue);
				}

				logger.debug("Loading certificate from file: "
						+ certificateValue);

				this.certificate = new X509Certificate(new FileInputStream(
						certFile));
			}
		}
		
		this.moaEndpoint = config.getValue(MOA_SIGN_URL);
		this.keyIdentifier = config.getValue(MOA_SIGN_KEY_ID);
	}

	public X509Certificate getCertificate(SignParameter parameter)
			throws PdfAsException {
		return this.certificate;
	}


	public byte[] sign(byte[] input, int[] byteRange, SignParameter parameter,
			RequestedSignature requestedSignature) throws PdfAsException {

		logger.info("signing with MOA @ " + this.moaEndpoint);
		/*
		 * URL moaUrl; try { moaUrl = new URL(this.moaEndpoint+"?wsdl"); } catch
		 * (MalformedURLException e1) { throw new
		 * PdfAsException("Invalid MOA endpoint!", e1); }
		 */
		SignatureCreationService service = new SignatureCreationService();

		SignatureCreationPortType creationPort = service
				.getSignatureCreationPort();
		BindingProvider provider = (BindingProvider) creationPort;
		provider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.moaEndpoint);

		CreateCMSSignatureRequest request = new CreateCMSSignatureRequest();
		request.setKeyIdentifier(this.keyIdentifier.trim());
		SingleSignatureInfo sigInfo = new SingleSignatureInfo();
		sigInfo.setSecurityLayerConformity(Boolean.TRUE);
		DataObjectInfo dataObjectInfo = new DataObjectInfo();
		dataObjectInfo.setStructure("detached");
		DataObject dataObject = new DataObject();
		MetaInfoType metaInfoType = new MetaInfoType();


		if (parameter.getConfiguration().hasValue(IConfigurationConstants.SIG_PADES_FORCE_FLAG))
		{
			if (IConfigurationConstants.TRUE.equalsIgnoreCase(parameter.getConfiguration().getValue(IConfigurationConstants.SIG_PADES_FORCE_FLAG)))
			{
				metaInfoType.setMimeType("application/pdf");
				sigInfo.setPAdESConformity(true);
			}
			else
			{
				metaInfoType.setMimeType("application/pdf");
			}
		}
		else
		{
			metaInfoType.setMimeType("application/pdf");
		}
		dataObject.setMetaInfo(metaInfoType);

		CMSContentBaseType content = new CMSContentBaseType();
		content.setBase64Content(input);

		dataObject.setContent(content);

		dataObjectInfo.setDataObject(dataObject);
		sigInfo.setDataObjectInfo(dataObjectInfo);
		request.getSingleSignatureInfo().add(sigInfo);

		requestedSignature.getStatus().getMetaInformations()
		.put(ErrorConstants.STATUS_INFO_SIGDEVICE, SIGNATURE_DEVICE);
		// TODO: Find a way to get MOA-SPSS Version
		requestedSignature.getStatus().getMetaInformations()
		.put(ErrorConstants.STATUS_INFO_SIGDEVICEVERSION, "UNKNOWN");
		
		CreateCMSSignatureResponseType response;
		try {
			response = creationPort.createCMSSignature(request);
		} catch (MOAFault e) {
			logger.warn("MOA signing failed!", e);
			if (e.getFaultInfo() != null) {
				throw new PdfAsMOAException(e.getFaultInfo().getErrorCode()
						.toString(), e.getFaultInfo().getInfo(), "", "");
			} else {
				throw new PdfAsMOAException("", e.getMessage(), "", "");
			}
		}

		if (response.getCMSSignatureOrErrorResponse().size() != 1) {
			throw new PdfAsException("Invalid Response Count ["
					+ response.getCMSSignatureOrErrorResponse().size()
					+ "] from MOA!");
		}

		Object resp = response.getCMSSignatureOrErrorResponse().get(0);
		if (resp instanceof byte[]) {
			// done the signature!
			byte[] cmsSignatureData = (byte[]) resp;

			VerifyResult verifyResult;
			try {
				verifyResult = SignatureUtils.verifySignature(cmsSignatureData,
						input);
				if(SettingsUtils.getBooleanValue(requestedSignature.getStatus().getSettings(), 
						IConfigurationConstants.KEEP_INVALID_SIGNATURE, false)) {
					Base64 b64 = new Base64();
					requestedSignature
					.getStatus()
					.getMetaInformations()
					.put(ErrorConstants.STATUS_INFO_INVALIDSIG,
							b64.encodeToString(cmsSignatureData));
				}
			} catch (PDFASError e) {
				throw new PdfAsErrorCarrier(e);
			}

			if (!StreamUtils.dataCompare(requestedSignature.getCertificate()
					.getFingerprintSHA(), ((X509Certificate) verifyResult
					.getSignerCertificate()).getFingerprintSHA())) {
				throw new PdfAsSignatureException("Certificates missmatch!");
			}

			return cmsSignatureData;
		} else if (resp instanceof ErrorResponseType) {
			ErrorResponseType err = (ErrorResponseType) resp;

			throw new PdfAsMOAException("", "", err.getInfo(), err
					.getErrorCode().toString());

		} else {
			throw new PdfAsException(
					"MOA response is not byte[] nor error but: "
							+ resp.getClass().getName());
		}
	}
}
