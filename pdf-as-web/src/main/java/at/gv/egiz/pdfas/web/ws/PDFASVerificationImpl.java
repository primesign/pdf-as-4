package at.gv.egiz.pdfas.web.ws;

import at.gv.egiz.pdfas.api.ws.*;
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
import iaik.x509.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MTOM
@WebService(endpointInterface = "at.gv.egiz.pdfas.api.ws.PDFASVerification")
public class PDFASVerificationImpl implements PDFASVerification {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFASVerificationImpl.class);

	public PDFASVerifyResponse verifyPDFDokument(PDFASVerifyRequest request) {
		checkSoapVerifyEnabled();
		if (request == null) {
			logger.warn("SOAP Verify Request is null!");
			return null;
		}

		StatisticEvent statisticEvent = new StatisticEvent();
		statisticEvent.setSource(Source.SOAP);
		statisticEvent.setOperation(Operation.VERIFY);
		statisticEvent.setUserAgent(UserAgentFilter.getUserAgent());
		statisticEvent.setStartNow();
		
		PDFASVerifyResponse response = new PDFASVerifyResponse();
		response.setVerifyResults(new ArrayList<PDFASVerifyResult>());
		try {
			int sigIdx = -1;
			if (request.getSignatureIndex() != null) {
				sigIdx = request.getSignatureIndex().intValue();
			}

			Map<String, String> preProcessor = null;
			if(request.getPreprocessor() != null) {
				preProcessor = request.getPreprocessor().getMap();
			}
			
			SignatureVerificationLevel lvl = SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;

			if(request.getVerificationLevel() != null) {
				if (request.getVerificationLevel().equals(
						VerificationLevel.INTEGRITY_ONLY)) {
					lvl = SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
				} else if (request.getVerificationLevel().equals(
						VerificationLevel.FULL_CERT_PATH)) {
					lvl = SignatureVerificationLevel.FULL_VERIFICATION;
				}
			}
			statisticEvent.setFilesize(request.getInputData().length);
			statisticEvent.setProfileId(null);
			statisticEvent.setDevice(request.getVerificationLevel().toString());
			
			List<VerifyResult> results = PdfAsHelper.synchornousVerify(
					request.getInputData(), sigIdx, lvl, preProcessor);
			
			for(int i = 0; i < results.size(); i++) {
				VerifyResult result = results.get(i);
				
				PDFASVerifyResult webResult = new PDFASVerifyResult();
				
				X509Certificate cert = (X509Certificate) result
						.getSignerCertificate();

				int certCode = result.getCertificateCheck().getCode();
				String certMessage = result.getCertificateCheck().getMessage();

				int valueCode = result.getValueCheckCode().getCode();
				String valueMessage = result.getValueCheckCode().getMessage();

				Exception e = result.getVerificationException();
				
				webResult.setRequestID(request.getRequestID());
				webResult.setSignatureIndex(i);
				webResult.setProcessed(result.isVerificationDone());
				
				if (result.isVerificationDone()) {
					webResult.setSignedBy(cert.getSubjectDN().getName());
					webResult.setCertificateCode(certCode);
					webResult.setCertificateMessage(certMessage);
					
					webResult.setValueCode(valueCode);
					webResult.setValueMessage(valueMessage);
					if (e != null) {
						webResult.setError(e.getMessage());
					}
					webResult.setCertificate("signCert?SIGID=" + i);
					webResult.setSignedData("signData?SIGID=" + i);
				} else {
					webResult.setSignedBy("");
					webResult.setCertificateCode(certCode);
					webResult.setCertificateMessage(certMessage);
					
					webResult.setCertificateCode(valueCode);
					webResult.setCertificateMessage(valueMessage);
					if (e != null) {
						webResult.setError(e.getMessage());
					}
					webResult.setCertificate("");
					webResult.setSignedData("");
				}
			
				response.getVerifyResults().add(webResult);
			}
			
			statisticEvent.setStatus(Status.OK);
			statisticEvent.setEndNow();
			statisticEvent.setTimestampNow();
			StatisticFrontend.getInstance().storeEvent(statisticEvent);
			statisticEvent.setLogged(true);
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
			
			logger.warn("Failed to verify PDF", e);
			if (WebConfiguration.isShowErrorDetails()) {
				throw new WebServiceException("Generic Error", e);
			} else {
				throw new WebServiceException("Server Verification failed.");
			}
		}
		return response;
	}

	private void checkSoapVerifyEnabled() {
		if (!WebConfiguration.getSoapVerifyEnabled()) {
			throw new WebServiceException("Service disabled!");
		}
	}

}
