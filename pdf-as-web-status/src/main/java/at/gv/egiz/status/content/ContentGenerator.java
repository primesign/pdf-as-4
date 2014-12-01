package at.gv.egiz.status.content;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.gv.egiz.status.TestResult;

/**
 * The Interface ContentGenerator.
 */
public interface ContentGenerator {
	
	/**
	 * Generate.
	 *
	 * @param request the request
	 * @param response the response
	 * @param details the details
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void generate(HttpServletRequest request, HttpServletResponse response, Map<String, TestResult> results, boolean details) throws IOException;
}
