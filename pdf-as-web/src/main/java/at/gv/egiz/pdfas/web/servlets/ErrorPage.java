package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
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
