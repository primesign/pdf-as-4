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
package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsStoreException;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import at.gv.egiz.pdfas.web.helper.DigestHelper;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
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

			if (pdfAsRequest == null) {
				throw new PdfAsStoreException("Invalid " + REQUEST_ID_PARAM
						+ " value");
			}

			Connector connector = pdfAsRequest.getParameters().getConnector();

			String invokeUrl = pdfAsRequest.getParameters().getInvokeURL();
			PdfAsHelper.setInvokeURL(req, resp, invokeUrl);

			String invokeTarget = pdfAsRequest.getParameters()
					.getInvokeTarget();
			PdfAsHelper.setInvokeTarget(req, resp, invokeTarget);

			String errorUrl = pdfAsRequest.getParameters().getInvokeErrorURL();
			PdfAsHelper.setErrorURL(req, resp, errorUrl);

			SignatureVerificationLevel lvl = SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
			if (pdfAsRequest.getVerificationLevel() != null) {
				switch (pdfAsRequest.getVerificationLevel()) {
				case INTEGRITY_ONLY:
					lvl = SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
					break;
				default:
					lvl = SignatureVerificationLevel.FULL_VERIFICATION;
					break;
				}
			}
			PdfAsHelper.setVerificationLevel(req, lvl);

			if (pdfAsRequest.getInputData() == null) {
				throw new PdfAsException("No Signature data available");
			}

			String pdfDataHash = DigestHelper.getHexEncodedHash(pdfAsRequest
					.getInputData());

			PdfAsHelper.setSignatureDataHash(req, pdfDataHash);
			logger.debug("Storing signatures data hash: " + pdfDataHash);

			logger.debug("Starting signature creation with: " + connector);

			// IPlainSigner signer;
			if (connector.equals(Connector.BKU)
					|| connector.equals(Connector.ONLINEBKU)
					|| connector.equals(Connector.MOBILEBKU)) {
				// start asynchronous signature creation

				if (connector.equals(Connector.BKU)) {
					if (WebConfiguration.getLocalBKUURL() == null) {
						throw new PdfAsWebException(
								"Invalid connector bku is not supported");
					}
				}

				if (connector.equals(Connector.ONLINEBKU)) {
					if (WebConfiguration.getLocalBKUURL() == null) {
						throw new PdfAsWebException(
								"Invalid connector onlinebku is not supported");
					}
				}

				if (connector.equals(Connector.MOBILEBKU)) {
					if (WebConfiguration.getLocalBKUURL() == null) {
						throw new PdfAsWebException(
								"Invalid connector mobilebku is not supported");
					}
				}
				Map<String, String> map = null;
				if(pdfAsRequest.getParameters().getPreprocessor() != null) {
					map = pdfAsRequest.getParameters().getPreprocessor().getMap();
				}
				
				PdfAsHelper.startSignature(req, resp, getServletContext(),
						pdfAsRequest.getInputData(), connector.toString(),
						pdfAsRequest.getParameters().getPosition(),
						pdfAsRequest.getParameters().getTransactionId(),
						pdfAsRequest.getParameters().getProfile(), 
						map);
			} else {
				throw new PdfAsWebException("Invalid connector ("
						+ Connector.BKU + " | " + Connector.ONLINEBKU + " | "
						+ Connector.MOBILEBKU + ")");
			}

		} catch (Throwable e) {
			logger.warn("Failed to process Request: ", e);
			PdfAsHelper.setSessionException(req, resp, e.getMessage(), e);
			PdfAsHelper.gotoError(getServletContext(), req, resp);
		}
	}
}
