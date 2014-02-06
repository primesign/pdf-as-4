package at.gv.egiz.pdfas.web.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

/**
 * Servlet Filter implementation class ExceptionCatchFilter
 */
public class ExceptionCatchFilter implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(ExceptionCatchFilter.class);
	
    /**
     * Default constructor. 
     */
    public ExceptionCatchFilter() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//try {
		
		if(request instanceof HttpServletRequest) {
			logger.debug("Processing Parameters into Attributes");
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			PdfAsHelper.logAccess(httpRequest);
			Enumeration<String> parameterNames = httpRequest.getParameterNames();
			while(parameterNames.hasMoreElements()) {
				String name = parameterNames.nextElement();
				String value = httpRequest.getParameter(name);
				request.setAttribute(name, value);
				logger.info("Setting attribute: " + name + " - " + value);
			}
		}
		
		
			chain.doFilter(request, response);
		/*} catch(Throwable e) {
			System.err.println("Unhandled Exception found!");
			e.printStackTrace(System.err);
			logger.error("Unhandled Exception found!", e);
		}*/
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		BasicConfigurator.configure();
	}

}
