package at.gv.egiz.pdfas.web.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.ws.PDFASBulkSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASBulkSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSigning;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

@MTOM
@WebService(endpointInterface = "at.gv.egiz.pdfas.api.ws.PDFASSigning")
public class PDFASSigningImpl implements PDFASSigning {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFASSigningImpl.class);

	public byte[] signPDFDokument(byte[] inputDocument,
			PDFASSignParameters parameters) {
		try {
			return PdfAsHelper.synchornousServerSignature(inputDocument,
					parameters);
		} catch (Throwable e) {
			logger.error("Server Signature failed.", e);
			if (WebConfiguration.isShowErrorDetails()) {
				throw new WebServiceException("Server Signature failed.", e);
			} else {
				throw new WebServiceException("Server Signature failed.");
			}
		}
	}

	public PDFASSignResponse signPDFDokument(PDFASSignRequest request) {
		if (request == null) {
			logger.warn("SOAP Sign Request is null!");
			return null;
		}
		PDFASSignResponse response = new PDFASSignResponse();
		try {
			response.setSignedPDF(signPDFDokument(request.getInputData(),
					request.getParameters()));
		} catch (Throwable e) {
			if (e.getCause() != null) {
				response.setError(e.getCause().getMessage());
			} else {
				response.setError(e.getMessage());
			}
		}
		response.setRequestID(request.getRequestID());
		return response;
	}

	public PDFASBulkSignResponse signPDFDokument(PDFASBulkSignRequest request) {
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
			return response;
		}
		logger.error("Server Signature failed. [PDFASBulkSignRequest is NULL]");
		if (WebConfiguration.isShowErrorDetails()) {
			throw new WebServiceException("PDFASBulkSignRequest is NULL");
		} else {
			throw new WebServiceException("Server Signature failed.");
		}
	}

}
