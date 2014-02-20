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
	public static final String PARAM_SIG_POS_R = "sig-pos-r";
	public static final String PARAM_SIG_IDX = "sig-idx";
	
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
	
	public static String getSigPosR(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_POS_R);
	}
	
	public static String getSigIdx(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_IDX);
	}
}
