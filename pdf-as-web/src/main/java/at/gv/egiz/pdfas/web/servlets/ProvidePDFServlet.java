package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

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

			if (invokeURL == null || WebConfiguration.isProvidePdfURLinWhitelist(invokeURL)) {
				
				if(invokeURL != null) {
					logger.warn(invokeURL + " is not allowed by whitelist");
				}
				
				String template = PdfAsHelper.getProvideTemplate();
				template = template.replace(PDF_DATA_URL, PdfAsHelper.generatePdfURL(request, response));
				// Deliver to Browser directly!
				response.setContentType("text/html");
				response.getWriter().write(template);
				response.getWriter().close();
			} else {
				// Redirect Browser
				String template = PdfAsHelper.getInvokeRedirectTemplateSL();
				template = template.replace("##INVOKE_URL##", invokeURL);

				byte[] signedData = PdfAsHelper.getSignedPdf(request, response);
				if (signedData != null) {
					template = template.replace("##PDFLENGTH##",
							String.valueOf(signedData.length));
				} else {
					throw new PdfAsException("No Signature data available");
				}

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
