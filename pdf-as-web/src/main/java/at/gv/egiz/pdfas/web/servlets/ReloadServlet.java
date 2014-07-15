package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

public class ReloadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6108555300743896727L;

	private static final Logger logger = LoggerFactory
			.getLogger(ReloadServlet.class);

	public static final String PDF_AS_WEB_CONF = "pdf-as-web.conf";
	public static final String PARAM_PASSWD = "PASSWD";
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReloadServlet() {
		super();
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		if(!WebConfiguration.getReloadEnabled()) {
			logger.info("Reload Servlet disabled. " + request.getRemoteAddr() + " tried to call it");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentLength(0);
			return;
		}
		
		logger.info("Called Reload Servlet from: " + request.getRemoteAddr());
		
		logger.info("Checking Password!");
		
		String pwd = request.getParameter(PARAM_PASSWD);
		
		if(pwd == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentLength(0);
			return;
		}
		
		if(!pwd.equals(WebConfiguration.getReloadPassword())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentLength(0);
			return;
		}
		
		String webconfig = System.getProperty(PDF_AS_WEB_CONF);
		
		if(webconfig == null) {
			logger.error("No web configuration provided! Please specify: " + PDF_AS_WEB_CONF);
			throw new RuntimeException("No web configuration provided! Please specify: " + PDF_AS_WEB_CONF);
		}
		
		WebConfiguration.configure(webconfig);
		PdfAsHelper.reloadConfig();
		
		logger.info("Reloaded!");
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><head></head><body>OK</body></html>");
		
		response.setContentType("text/html");
		OutputStream os = response.getOutputStream();
		os.write(sb.toString().getBytes());
		os.close();
	}
}
