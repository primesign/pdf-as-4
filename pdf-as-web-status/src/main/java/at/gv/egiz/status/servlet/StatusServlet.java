package at.gv.egiz.status.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.status.TestResult;
import at.gv.egiz.status.content.ResponseBuilder;
import at.gv.egiz.status.content.ResponseBuilder.ContentType;
import at.gv.egiz.status.impl.TestManager;

/**
 * Servlet implementation class StatusServlet
 */
@WebServlet(name="statusServlet", urlPatterns={"/status"}) 
public class StatusServlet extends HttpServlet {
       
	private static final long serialVersionUID = 1201254769913428186L;

	public static final String PARAM_CONTENT_TYPE = "content";
	public static final String PARAM_FORCE = "force";
	public static final String PARAM_DETAILS = "details";
	public static final String PARAM_DETAILS_TRUE = "true";
	public static final String PARAM_TEST = "test";
	
	private final Logger log = LoggerFactory.getLogger(StatusServlet.class);
	
	private TestManager manager;
	
	private ResponseBuilder builder;
	
	private boolean showDetails = false;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public StatusServlet() {
        super();
        
        manager = new TestManager();
        builder = new ResponseBuilder();
    }

	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		// TODO: Environment Parameter to show details -> showDetails
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doProcess(request, response);
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String content = request.getParameter(PARAM_CONTENT_TYPE);
		
		if(content == null) {
			content = ContentType.HTML.toString();
		} 

		log.debug("Producing Content: " + content);
		
		// Parameter to force execution
		boolean force = true;
		
		String forceExec = request.getParameter(PARAM_FORCE);
		
		if(forceExec != null) {
			if(forceExec.equalsIgnoreCase("false")) {
				force = false;
			}
		}
		boolean showingDetails = showDetails;
		String detail = request.getParameter(PARAM_DETAILS);
		if(detail != null) {
			showingDetails = detail.equalsIgnoreCase(PARAM_DETAILS_TRUE);
		}
		
		// Parameter for specific test
		String test = request.getParameter(PARAM_TEST);
		
		Map<String, TestResult> results = null; 
		
		if(test != null) {
			results = new HashMap<String, TestResult>();
			TestResult result = this.manager.runTest(test, force);
			if(result != null) {
				results.put(test, result);
			}
		} else {
			results = this.manager.runAllTests(force);
		}

		this.builder.generate(request, response, results, showingDetails, content);
	}
}
