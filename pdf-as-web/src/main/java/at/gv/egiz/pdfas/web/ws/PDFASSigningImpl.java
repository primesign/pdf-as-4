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
package at.gv.egiz.pdfas.web.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.ws.PDFASBulkSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASBulkSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSigning;
import at.gv.egiz.pdfas.api.ws.VerificationLevel;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.filter.UserAgentFilter;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.stats.StatisticEvent;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Operation;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Source;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Status;
import at.gv.egiz.pdfas.web.stats.StatisticFrontend;
import at.gv.egiz.pdfas.web.store.RequestStore;

@MTOM
@WebService(endpointInterface = "at.gv.egiz.pdfas.api.ws.PDFASSigning")
public class PDFASSigningImpl implements PDFASSigning {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFASSigningImpl.class);

	/*
	 * public byte[] signPDFDokument(byte[] inputDocument, PDFASSignParameters
	 * parameters) { checkSoapSignEnabled(); try { return
	 * PdfAsHelper.synchornousServerSignature(inputDocument, parameters); }
	 * catch (Throwable e) { logger.error("Server Signature failed.", e); if
	 * (WebConfiguration.isShowErrorDetails()) { throw new
	 * WebServiceException("Server Signature failed.", e); } else { throw new
	 * WebServiceException("Server Signature failed."); } } }
	 */

	public PDFASSignResponse signPDFDokument(PDFASSignRequest request) {
		logger.debug("Starting SOAP Sign Request");
		checkSoapSignEnabled();
		if (request == null) {
			logger.warn("SOAP Sign Request is null!");
			return null;
		}
		
		StatisticEvent statisticEvent = new StatisticEvent();
		statisticEvent.setSource(Source.SOAP);
		statisticEvent.setOperation(Operation.SIGN);
		statisticEvent.setUserAgent(UserAgentFilter.getUserAgent());
		statisticEvent.setStartNow();
		PDFASSignResponse response = new PDFASSignResponse();
		try {
			if(request.getParameters().getConnector() == null) {
				throw new WebServiceException(
						"Invalid connector value!");
			}
			
			statisticEvent.setFilesize(request.getInputData().length);
			statisticEvent.setProfileId(request.getParameters().getProfile());
			statisticEvent.setDevice(request.getParameters().getConnector().toString());
			
			Map<String, String> preProcessor = null;
			if(request.getParameters().getPreprocessor() != null) {
				preProcessor = request.getParameters().getPreprocessor().getMap();
			}
			
			if (request.getParameters().getConnector().equals(Connector.MOA)
					|| request.getParameters().getConnector()
							.equals(Connector.JKS)) {
				// Plain server based signatures!!
				response = PdfAsHelper.synchronousServerSignature(
						request.getInputData(), request.getParameters(), request.getSignatureBlockParameters());


				VerifyResult verifyResult = null;
				if (request.getVerificationLevel() != null && 
						request.getVerificationLevel().equals(
						VerificationLevel.FULL_CERT_PATH)) {
					List<VerifyResult> verResults = PdfAsHelper
							.synchronousVerify(
									response.getSignedPDF(),
									-1,
									SignatureVerificationLevel.FULL_VERIFICATION, 
									preProcessor);

					if (verResults.size() < 1) {
						throw new WebServiceException(
								"Document verification failed! " + verResults.size());
					}
					verifyResult = verResults.get(verResults.size() - 1);
				} else {
					List<VerifyResult> verResults = PdfAsHelper
							.synchronousVerify(
									response.getSignedPDF(),
									-1,
									SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION, 
									preProcessor);

					if (verResults.size() < 1) {
						throw new WebServiceException(
								"Document verification failed! " + verResults.size());
					}
					verifyResult = verResults.get(verResults.size() - 1);
				}

				if(verifyResult.getValueCheckCode().getCode() == 0) {
					statisticEvent.setStatus(Status.OK);
					statisticEvent.setEndNow();
					statisticEvent.setTimestampNow();
					StatisticFrontend.getInstance().storeEvent(statisticEvent);
					statisticEvent.setLogged(true);
				} else {
					statisticEvent.setStatus(Status.ERROR);
					statisticEvent.setErrorCode(verifyResult.getValueCheckCode().getCode());
					statisticEvent.setEndNow();
					statisticEvent.setTimestampNow();
					StatisticFrontend.getInstance().storeEvent(statisticEvent);
					statisticEvent.setLogged(true);
				}
				
				response.getVerificationResponse().setCertificateCode(
						verifyResult.getCertificateCheck().getCode());
				response.getVerificationResponse().setValueCode(
						verifyResult.getValueCheckCode().getCode());

			} else {
				// Signatures with user interaction!!
				String id = RequestStore.getInstance().createNewStoreEntry(
						request, statisticEvent);

				if (id == null) {
					throw new WebServiceException("Failed to store request");
				}

				String userEntryURL = PdfAsHelper.generateUserEntryURL(id);

				logger.debug("Generated request store: " + id);
				logger.debug("Generated UI URL: " + userEntryURL);

				if (userEntryURL == null) {
					throw new WebServiceException(
							"Failed to generate User Entry URL");
				}

				response.setRedirectUrl(userEntryURL);
			}
		} catch (Throwable e) {
			
			statisticEvent.setStatus(Status.ERROR);
			statisticEvent.setException(e);
			if(e instanceof PDFASError) {
				statisticEvent.setErrorCode(((PDFASError)e).getCode());
			}
			statisticEvent.setEndNow();
			statisticEvent.setTimestampNow();
			StatisticFrontend.getInstance().storeEvent(statisticEvent);
			statisticEvent.setLogged(true);
			
			logger.warn("Error in Soap Service", e);
			if (e.getCause() != null) {
				response.setError(e.getCause().getMessage());
			} else {
				response.setError(e.getMessage());
			}
		} finally {
			logger.debug("Done SOAP Sign Request");
		}
		response.setRequestID(request.getRequestID());
		return response;
	}

	public PDFASBulkSignResponse signPDFDokument(PDFASBulkSignRequest request) {
		logger.debug("Starting SOAP BulkSign Request");
		checkSoapSignEnabled();
		List<PDFASSignResponse> responses = new ArrayList<PDFASSignResponse>();
		if (request.getSignRequests() != null) {
			for (int i = 0; i < request.getSignRequests().size(); i++) {
				PDFASSignResponse response = signPDFDokument(request
						.getSignRequests().get(i));
				if (response != null) {
					responses.add(response);
				}
			}
			PDFASBulkSignResponse response = new PDFASBulkSignResponse();
			response.setSignResponses(responses);
			logger.debug("Done SOAP Sign Request");
			return response;
		}
		logger.warn("Server Signature failed. [PDFASBulkSignRequest is NULL]");
		
		if (WebConfiguration.isShowErrorDetails()) {
			throw new WebServiceException("PDFASBulkSignRequest is NULL");
		} else {
			throw new WebServiceException("Server Signature failed.");
		}
	}

	private void checkSoapSignEnabled() {
		if (!WebConfiguration.getSoapSignEnabled()) {
			throw new WebServiceException("Service disabled!");
		}
	}

}
