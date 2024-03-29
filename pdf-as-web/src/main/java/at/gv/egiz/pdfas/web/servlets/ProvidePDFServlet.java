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
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.stax2.io.EscapingWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.helper.UrlParameterExtractor;

/**
 * Servlet implementation class ProvidePDF
 */
public class ProvidePDFServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(ProvidePDFServlet.class);
	
	private static final String PDF_DATA_URL = "##PDFDATAURL##";
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProvidePDFServlet() {
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
			String invokeURL = PdfAsHelper.getInvokeURL(request, response);

			if (invokeURL == null || !WebConfiguration.isProvidePdfURLinWhitelist(invokeURL)) {
				
				if(invokeURL != null) {
					logger.warn(invokeURL + " is not allowed by whitelist");
				}
				
				if (PdfAsHelper.getResponseMode(request, response).equals(PdfAsHelper.PDF_RESPONSE_MODES.htmlform)) {								
					String template = PdfAsHelper.getProvideTemplate();
					template = template.replace(PDF_DATA_URL, PdfAsHelper.generatePdfURL(request, response));
					// Deliver to Browser directly!
					response.setContentType("text/html");
					response.getWriter().write(template);
					response.getWriter().close();
					
				} else {
					logger.debug("PDFResult directMode: Forward to PDFData Servlet directly");
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PDFData");
					dispatcher.forward(request, response);
								
				}
					
			} else {
				// Redirect Browser
				String template = PdfAsHelper.getInvokeRedirectTemplateSL();
				
				URL url = new URL(invokeURL);
				int p=url.getPort();
				//no port, but http or https --> use default port
				if((url.getProtocol().equalsIgnoreCase("https") || url.getProtocol().equalsIgnoreCase("http")) && p == -1){
					p=url.getDefaultPort();
				}
				String invokeUrlProcessed = url.getProtocol() + "://" +   // "http" + "://
						url.getHost() +       // "myhost"
			             ":" +                           // ":"
			             p +       // "8080"
			             url.getPath();  
				
				template = template.replace("##INVOKE_URL##", invokeUrlProcessed);
				
				String extraParams = UrlParameterExtractor.buildParameterFormString(url);
				template = template.replace("##ADD_PARAMS##", extraParams);
				
				byte[] signedData = PdfAsHelper.getSignedPdf(request, response);
				if (signedData != null) {
					template = template.replace("##PDFLENGTH##",
							String.valueOf(signedData.length));
				} else {
					throw new PdfAsException("No Signature data available");
				}

				String target = PdfAsHelper.getInvokeTarget(request, response);
				
				if(target == null) {
					target = "_self";
				}
				
				template = template.replace("##TARGET##", StringEscapeUtils.escapeHtml4(target));
				
				template = template.replace("##PDFURL##",
						URLEncoder.encode(PdfAsHelper.generatePdfURL(request, response), 
								"UTF-8"));
				response.setContentType("text/html");
				response.getWriter().write(template);
				response.getWriter().close();
			}
		} catch (Exception e) {
			PdfAsHelper.setSessionException(request, response, e.getMessage(),
					e);
			PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}
}
