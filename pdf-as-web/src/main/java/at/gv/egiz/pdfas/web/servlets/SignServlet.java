package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

/**
 * Servlet implementation class Sign
 */
public class SignServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SignServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
				.println(
						"<html><head><title>Hello</title></head><body>BODY</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doSignature(HttpServletRequest request,
			HttpServletResponse response, byte[] pdfData) {
		try {
			PdfAs pdfAs = PdfAsFactory.createPdfAs(null);
			// TODO: Build configuration and Sign Parameters
			Configuration config = pdfAs.getConfiguration();
			SignParameter signParameter = PdfAsFactory.createSignParameter(
					config, new ByteArrayDataSource(pdfData));
			
			
			
			StatusRequest statusRequest = pdfAs.startSign(signParameter);

		} catch (PdfAsException e) {
			e.printStackTrace();
		}
	}

}
