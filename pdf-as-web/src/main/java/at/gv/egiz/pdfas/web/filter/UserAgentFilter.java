package at.gv.egiz.pdfas.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAgentFilter implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(UserAgentFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	private static final ThreadLocal<String> requestUserAgent = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return "unkown";
		}
		
	};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(request instanceof HttpServletRequest) {
			logger.debug("Processing Parameters into Attributes");
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			requestUserAgent.set(httpRequest.getHeader("User-Agent"));
		}
		try {
			chain.doFilter(request, response);
		} finally {
			requestUserAgent.remove();
		}
	}

	@Override
	public void destroy() {
	}

	public static String getUserAgent() {
		return requestUserAgent.get();
	}
	
}
