package at.gv.egiz.pdfas.web.helper;

import javax.servlet.http.HttpServletRequest;

public class PdfAsParameterExtractor {

	public static final String PARAM_CONNECTOR = "connector";
	public static final String PARAM_CONNECTOR_DEFAULT = "bku";
	
	
	public static final String PARAM_INVOKE_URL = "invoke-app-url";
	public static final String PARAM_INVOKE_URL_ERROR = "invoke-app-error-url";
	public static final String PARAM_LOCALE = "locale";
	public static final String PARAM_NUM_BYTES = "num-bytes";
	public static final String PARAM_PDF_URL = "pdf-url";
	public static final String PARAM_SIG_TYPE = "sig_type";
	public static final String PARAM_SIG_POS_P = "sig-pos-p";
	public static final String PARAM_SIG_POS_Y = "sig-pos-y";
	public static final String PARAM_SIG_POS_X = "sig-pos-x";
	public static final String PARAM_SIG_POS_W = "sig-pos-w";
	
	public static String getConnector(HttpServletRequest request) {
		String connector = (String)request.getAttribute(PARAM_CONNECTOR);
		if(connector != null) {
			return connector;
		} 
		return PARAM_CONNECTOR_DEFAULT;
	}
	
	public static String getInvokeURL(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_INVOKE_URL);
	}
	
	public static String getInvokeErrorURL(HttpServletRequest request) {
		String url = (String)request.getAttribute(PARAM_INVOKE_URL_ERROR);
		if(url != null) {
			//TODO validation!
		}
		return url;
	}
	
	public static String getLocale(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_LOCALE);
	}
	
	public static String getNumBytes(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_NUM_BYTES);
	}
	
	public static String getPdfUrl(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_PDF_URL);
	}
	
	public static String getSigType(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_TYPE);
	}
	
	public static String getSigPosP(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_POS_P);
	}
	
	public static String getSigPosY(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_POS_Y);
	}
	
	public static String getSigPosX(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_POS_X);
	}
	
	public static String getSigPosW(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_POS_W);
	}
	
	// legacy Parameter
	public static final String PARAM_PDF_ID = "pdf-id";
	public static final String PARAM_SESSION_ID = "session-id";
	
	public static String getPdfId(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_PDF_ID);
	}
	
	public static String getSessionId(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SESSION_ID);
	}
}
