package at.gv.egiz.pdfas.web.ws;

import iaik.x509.X509Certificate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.ws.PDFASVerification;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyRequest;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyResponse;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyResult;
import at.gv.egiz.pdfas.api.ws.VerificationLevel;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

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

			if (request.getVerificationLevel().equals(
					VerificationLevel.INTEGRITY_ONLY)) {
				lvl = SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
			} else if (request.getVerificationLevel().equals(
					VerificationLevel.FULL_CERT_PATH)) {
				lvl = SignatureVerificationLevel.FULL_VERIFICATION;
			}

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
		} catch (Exception e) {
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
