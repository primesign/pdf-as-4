package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

/**
 * Servlet implementation class PDFData
 */
public class PDFData extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(PDFData.class);
	
	private static String ORIGINAL_DIGEST = "origdigest";
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PDFData() {
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
		byte[] signedData = PdfAsHelper.getSignedPdf(request, response);

		String plainPDFDigest = request.getParameter(ORIGINAL_DIGEST);
		
		if (signedData != null) {
			if(plainPDFDigest != null) {
				String signatureDataHash = PdfAsHelper.getSignatureDataHash(request);
				if(!plainPDFDigest.equalsIgnoreCase(signatureDataHash)) {
					logger.error("Digest Hash mismatch!");
					logger.error("Requested digest: " + plainPDFDigest);
					logger.error("Saved     digest: " + signatureDataHash);
					
					PdfAsHelper.setSessionException(request, response,
							"Signature Data digest do not match!", null);
					PdfAsHelper.gotoError(getServletContext(), request, response);
					return;
				}
			}
			response.setHeader("Content-Disposition", "inline;filename=" + PdfAsHelper.getPDFFileName(request));
			response.setContentType("application/pdf");
			OutputStream os = response.getOutputStream();
			os.write(signedData);
			os.close();
			
			// When data is collected destroy session!
			request.getSession().invalidate();
		} else {
			PdfAsHelper.setSessionException(request, response,
					"No signed pdf document available.", null);
			PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}
}
