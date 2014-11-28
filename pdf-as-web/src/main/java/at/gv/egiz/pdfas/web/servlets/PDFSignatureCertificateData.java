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
import java.io.OutputStream;
import java.security.cert.CertificateEncodingException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

public class PDFSignatureCertificateData  extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(PDFSignatureCertificateData.class);

	public static final String SIGN_ID = "SIGID";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PDFSignatureCertificateData() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.process(request, response);
	}

	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			if(request.getParameter(SIGN_ID) == null) {
				throw new PdfAsException("Missing Parameter");
			}
			
			String sigID = request.getParameter(SIGN_ID);

			int id = Integer.parseInt(sigID);

			List<VerifyResult> vResult = PdfAsHelper
					.getVerificationResult(request);

			if (id < vResult.size()) {
				VerifyResult res =  vResult.get(id);
				
				response.setHeader(
						"Content-Disposition",
						"inline;filename=cert_" + id + ".cer");
				response.setContentType("application/pkix-cert");
				OutputStream os = response.getOutputStream();
				os.write(res.getSignerCertificate().getEncoded());
				os.close();
			} else {
				logger.warn("Verification CERT not found! for id " + request.getParameter(SIGN_ID) + " in session " + request.getSession().getId());
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (NumberFormatException e) {
			logger.warn("Verification CERT not found! for id " + request.getParameter(SIGN_ID) + " in session " + request.getSession().getId());
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (PdfAsException e) {
			logger.warn("Verification CERT not found:", e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (CertificateEncodingException e) {
			logger.warn("Verification CERT invalid:", e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} 
	}

}
