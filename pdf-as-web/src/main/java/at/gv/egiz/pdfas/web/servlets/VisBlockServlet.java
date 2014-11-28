package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

public class VisBlockServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(VisBlockServlet.class);

	private static String PARAM_PROFILE = "p";
	private static String PARAM_RESOLUTION = "r";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VisBlockServlet() {
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

			String profile = request.getParameter(PARAM_PROFILE);
			String resolutionString = request.getParameter(PARAM_RESOLUTION);

			int resolution = Integer.parseInt(resolutionString);

			if (resolution < 16 || resolution > 512) {
				throw new ServletException(PARAM_RESOLUTION
						+ " invalid value! Out of Range");
			}

			byte[] imageData = PdfAsHelper.generateVisualBlock(profile, resolution);
			
			if (imageData != null) {
				
				response.setHeader("Content-Disposition", "inline;filename="
						+ profile + "_" + resolution + ".png");
				response.setContentType("image/png");
				OutputStream os = response.getOutputStream();
				os.write(imageData);
				os.close();
			} else {
				logger.warn("generateVisualBlock returned null!");
				throw new ServletException("Invalid profile id");
			}
		} catch (NumberFormatException e) {
			throw new ServletException(PARAM_RESOLUTION
					+ " invalid value! Not a Number");
		} catch (CertificateException e) {
			logger.warn("CERT Error", e);
			throw new ServletException("Failed to find certificate");
		} catch (PDFASError e) {
			logger.warn("PDF_AS Error", e);
			throw new ServletException("Generic Error");
		} 
	}

}
