package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

/**
 * Servlet implementation class VerifyServlet
 */
public class VerifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doVerify(HttpServletRequest request, HttpServletResponse response, 
			byte[] pdfData, int whichSignature) {
		PdfAs pdfAs = PdfAsFactory.createPdfAs(null);
		Configuration conf = pdfAs.getConfiguration();
		VerifyParameter parameter = PdfAsFactory.createVerifyParameter(conf, new ByteArrayDataSource(pdfData));
		parameter.setWhichSignature(whichSignature);
		
		List<VerifyResult> results = pdfAs.verify(parameter);
		
		// Create HTML Snippet for each Verification Result
		// Put these results into the web page
		// Or create a JSON response with the verification results for automated processing
		
	}
	
}
