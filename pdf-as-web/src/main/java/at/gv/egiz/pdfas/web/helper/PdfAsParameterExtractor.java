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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;

public class PdfAsParameterExtractor {

	public static final String PARAM_CONNECTOR = "connector";
	public static final String PARAM_TRANSACTION_ID = "transactionId";
	public static final String PARAM_CONNECTOR_DEFAULT = "bku";
	
	public static final String PARAM_FORMAT = "format";
	public static final String PARAM_HTML = "html";
	public static final String PARAM_JSON = "json";
	public static final String PARAM_KEYIDENTIFIER = "keyId";
	
	public static final String[] AVAILABLE_FORMATS = new String[] {
		PARAM_HTML, PARAM_JSON
	};
	
	public static final String PARAM_INVOKE_URL = "invoke-app-url";
	public static final String PARAM_INVOKE_URL_TARGET = "invoke-app-url-target";
	public static final String PARAM_INVOKE_URL_ERROR = "invoke-app-error-url";
	
	public static final String PARAM_VERIFY_LEVEL = "verify-level";
	public static final String PARAM_VERIFY_LEVEL_OPTION_FULL = "full";
	public static final String PARAM_VERIFY_LEVEL_OPTION_INT_ONLY = "intOnly";
	
	public static final String PARAM_LOCALE = "locale";
	public static final String PARAM_NUM_BYTES = "num-bytes";
	public static final String PARAM_PDF_URL = "pdf-url";
	public static final String PARAM_SIG_TYPE = "sig-type";
	public static final String PARAM_SIG_TYPE_ALIAS = "sig_type";
	public static final String PARAM_SIG_POS_P = "sig-pos-p";
	public static final String PARAM_SIG_POS_Y = "sig-pos-y";
	public static final String PARAM_SIG_POS_X = "sig-pos-x";
	public static final String PARAM_SIG_POS_W = "sig-pos-w";
	public static final String PARAM_SIG_POS_R = "sig-pos-r";
	public static final String PARAM_SIG_POS_F = "sig-pos-f";
	public static final String PARAM_SIG_IDX = "sig-idx";
	public static final String PARAM_FILENAME = "filename";
	public static final String PARAM_ORIGINAL_DIGEST = "origdigest";
	public static final String PARAM_PREPROCESSOR_PREFIX = "pp:";
	public static final String PARAM_OVERWRITE_PREFIX = "ov:";
	public static final String PARAM_QRCODE_CONTENT = "qrcontent";
	
	
	public static String getConnector(HttpServletRequest request) {
		String connector = (String)request.getAttribute(PARAM_CONNECTOR);
		if(connector != null) {
			return connector;
		} 
		return PARAM_CONNECTOR_DEFAULT;
	}
	
	public static String getQRCodeContent(HttpServletRequest request) {
		String qrcodeContent = (String)request.getAttribute(PARAM_QRCODE_CONTENT);
		return qrcodeContent;
	}
	
	public static String getTransactionId(HttpServletRequest request) {
		String transactionId = (String)request.getAttribute(PARAM_TRANSACTION_ID);
		return transactionId;
	}
	
	public static String getKeyIdentifier(HttpServletRequest request) {
		String keyIdentifier = (String)request.getAttribute(PARAM_KEYIDENTIFIER);
		return keyIdentifier;
	}
	
	public static String getFilename(HttpServletRequest request) {
		String filename = (String)request.getAttribute(PARAM_FILENAME);
		return filename;
	}
	
	public static String getInvokeURL(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_INVOKE_URL);
	}
	
	public static Map<String, String> getPreProcessorMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		
		Enumeration<String> parameterNames = request.getAttributeNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			if(parameterName.startsWith(PARAM_PREPROCESSOR_PREFIX)) {
				String key = parameterName.substring(PARAM_PREPROCESSOR_PREFIX.length());
				String value = (String)request.getAttribute(parameterName);
				map.put(key, value);
			}
		}
		
		return map;
	}
	
	public static Map<String, String> getOverwriteMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		
		Enumeration<String> parameterNames = request.getAttributeNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			if(parameterName.startsWith(PARAM_OVERWRITE_PREFIX)) {
				String key = parameterName.substring(PARAM_OVERWRITE_PREFIX.length());
				String value = (String)request.getAttribute(parameterName);
				map.put(key, value);
			}
		}
		
		return map;
	}
	
	public static SignatureVerificationLevel getVerificationLevel(HttpServletRequest request) {
		String value = (String)request.getAttribute(PARAM_VERIFY_LEVEL);
		if(value != null) {
			if(value.equals(PARAM_VERIFY_LEVEL_OPTION_FULL)) {
				return SignatureVerificationLevel.FULL_VERIFICATION;
			} else if(value.equals(PARAM_VERIFY_LEVEL_OPTION_INT_ONLY)) {
				return SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
			}
		}
		return SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
	}
	
	public static String getInvokeTarget(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_INVOKE_URL_TARGET);
	}
	
	public static String getFormat(HttpServletRequest request) {
		String format = (String)request.getAttribute(PARAM_FORMAT);
		
		String finalFormat = null;
		for(int i = 0; i < AVAILABLE_FORMATS.length; i++) {
			if(AVAILABLE_FORMATS[i].equals(format)) {
				finalFormat = AVAILABLE_FORMATS[i];
			}
		}
		
		if(finalFormat == null) {
			finalFormat = PARAM_HTML;
		}
		
		return finalFormat;
	}
	
	public static String getOrigDigest(HttpServletRequest request) {
		String url = (String)request.getAttribute(PARAM_ORIGINAL_DIGEST);
		return url;
	} 
	
	public static String getInvokeErrorURL(HttpServletRequest request) {
		String url = (String)request.getAttribute(PARAM_INVOKE_URL_ERROR);
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
		String value = (String)request.getAttribute(PARAM_SIG_TYPE);
		if(value == null) {
			value = (String)request.getAttribute(PARAM_SIG_TYPE_ALIAS);
		}
		return value;
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
	
	public static String getSigPosF(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_POS_F);
	}
	
	public static String getSigIdx(HttpServletRequest request) {
		return (String)request.getAttribute(PARAM_SIG_IDX);
	}
}
