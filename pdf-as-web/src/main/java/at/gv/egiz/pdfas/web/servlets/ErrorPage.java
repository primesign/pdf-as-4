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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.HTMLFormater;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.helper.UrlParameterExtractor;

/**
 * Servlet implementation class ErrorPage
 */
public class ErrorPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ErrorPage.class);
	
	private static final String ERROR_STACK = "##ERROR_STACK##";
	private static final String ERROR_MESSAGE = "##ERROR_MESSAGE##";
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ErrorPage() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (PdfAsHelper.getFromDataUrl(request)) {
			// redirect to here!
			response.sendRedirect(PdfAsHelper.generateErrorURL(request,
					response));
			return;
		} else {
			String errorURL = PdfAsHelper.getErrorURL(request, response);
			Throwable e = PdfAsHelper
					.getSessionException(request, response);
			String message = PdfAsHelper.getSessionErrMessage(request,
					response);
			if (errorURL != null && WebConfiguration.isProvidePdfURLinWhitelist(errorURL)) {
				String template = PdfAsHelper.getErrorRedirectTemplateSL();
				template = template.replace("##ERROR_URL##",
						errorURL);
				
				URL url = new URL(errorURL);
				String extraParams = UrlParameterExtractor.buildParameterFormString(url);
				template = template.replace("##ADD_PARAMS##", extraParams);
				
				String target = PdfAsHelper.getInvokeTarget(request, response);
				
				if(target == null) {
					target = "_self";
				}
				
				template = template.replace("##TARGET##", target);
				
				if (e != null && WebConfiguration.isShowErrorDetails()) {
					template = template.replace("##CAUSE##",
							URLEncoder.encode(e.getMessage(), "UTF-8"));
				} else {
					template = template.replace("##CAUSE##",
							"");
				}
				if (message != null) {
					template = template.replace("##ERROR##", URLEncoder.encode(message, "UTF-8"));
				} else {
					template = template.replace("##ERROR##", "Unbekannter Fehler");
				}
				response.setContentType("text/html");
				response.getWriter().write(template);
				response.getWriter().close();
			} else {
				if(errorURL != null) {
					logger.warn(errorURL + " is not allowed by whitelist");
				}
				
				String template = PdfAsHelper.getErrorTemplate();
				if (message != null) {
					template = template.replace(ERROR_MESSAGE, message);
				} else {
					template = template.replace(ERROR_MESSAGE, "Unbekannter Fehler");
				}
				
				if (e != null && WebConfiguration.isShowErrorDetails()) {
					template = template.replace(ERROR_STACK,  HTMLFormater.formatStackTrace(e.getStackTrace()));
				} else {
					template = template.replace(ERROR_STACK,  "");
				}
				
				response.setContentType("text/html");
				response.getWriter().write(template);
				response.getWriter().close();
			}
		}
	}
}
