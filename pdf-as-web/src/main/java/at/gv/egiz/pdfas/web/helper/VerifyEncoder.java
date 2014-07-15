package at.gv.egiz.pdfas.web.helper;

import java.util.HashMap;
import java.util.Map;

public class VerifyEncoder {
	private static Map<String, VerifyResultEncoder> encoders = new HashMap<String, VerifyResultEncoder>();
	
	static {
		encoders.put(PdfAsParameterExtractor.PARAM_HTML, new VerifyResultHTMLEncoder());
		encoders.put(PdfAsParameterExtractor.PARAM_JSON, new VerifyResultJSONEncoder());
	}
	
	public static VerifyResultEncoder getEncoder(String type) {
		return encoders.get(type);
	}
}
