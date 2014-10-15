/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
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
			@SuppressWarnings("unchecked")
			Enumeration<String> parameterNames = httpRequest.getParameterNames();
			while(parameterNames.hasMoreElements()) {
				String name = parameterNames.nextElement();
				String value = httpRequest.getParameter(name);
				request.setAttribute(name, value);
				logger.debug("Setting attribute: " + name + " - " + value);
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
	}

}
