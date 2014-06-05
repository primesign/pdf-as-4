package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsStoreException;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import at.gv.egiz.pdfas.web.helper.DigestHelper;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.helper.PdfAsParameterExtractor;
import at.gv.egiz.pdfas.web.store.RequestStore;

public class UIEntryPointServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String REQUEST_ID_PARAM = "reqId";

	private static final Logger logger = LoggerFactory
			.getLogger(UIEntryPointServlet.class);

	public UIEntryPointServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doProcess(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doProcess(req, resp);
	}

	protected void doProcess(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String storeId = req.getParameter(REQUEST_ID_PARAM);

			if (storeId == null) {
				throw new PdfAsStoreException("Wrong Parameters");
			}
			
			PDFASSignRequest pdfAsRequest = RequestStore.getInstance()
					.fetchStoreEntry(storeId);
			
			if(pdfAsRequest == null) {
				throw new PdfAsStoreException("Invalid " + REQUEST_ID_PARAM + " value");
			}
			
			Connector connector = pdfAsRequest.getParameters().getConnector();
			
			String invokeUrl = pdfAsRequest.getParameters().getInvokeURL();
			PdfAsHelper.setInvokeURL(req, resp, invokeUrl);
			
			String errorUrl = pdfAsRequest.getParameters().getInvokeErrorURL();
			PdfAsHelper.setErrorURL(req, resp, errorUrl);
			
			if(pdfAsRequest.getInputData() == null) {
				throw new PdfAsException("No Signature data available");
			}
			
			String pdfDataHash = DigestHelper.getHexEncodedHash(pdfAsRequest.getInputData());
			
			PdfAsHelper.setSignatureDataHash(req, pdfDataHash);
			logger.debug("Storing signatures data hash: " + pdfDataHash);
			
			logger.debug("Starting signature creation with: " + connector);
			
			//IPlainSigner signer;
			if (connector.equals(Connector.BKU) || connector.equals(Connector.ONLINEBKU) || connector.equals(Connector.MOBILEBKU)) {
				// start asynchronous signature creation
				
				if(connector.equals(Connector.BKU)) {
					if(WebConfiguration.getLocalBKUURL() == null) {
						throw new PdfAsWebException("Invalid connector bku is not supported");
					}
				}
				
				if(connector.equals(Connector.ONLINEBKU)) {
					if(WebConfiguration.getLocalBKUURL() == null) {
						throw new PdfAsWebException("Invalid connector onlinebku is not supported");
					}
				}
				
				if(connector.equals(Connector.MOBILEBKU)) {
					if(WebConfiguration.getLocalBKUURL() == null) {
						throw new PdfAsWebException("Invalid connector mobilebku is not supported");
					}
				}
				
				PdfAsHelper.startSignature(req, resp, getServletContext(), pdfAsRequest.getInputData(), 
						connector.toString(), pdfAsRequest.getParameters().getTransactionId());
			} else {
				throw new PdfAsWebException("Invalid connector (" + Connector.BKU + " | " + Connector.ONLINEBKU + " | " + Connector.MOBILEBKU + ")");
			}
			
			
		} catch (Throwable e) {
			PdfAsHelper.setSessionException(req, resp, e.getMessage(), e);
			PdfAsHelper.gotoError(getServletContext(), req, resp);
		}
	}
}
