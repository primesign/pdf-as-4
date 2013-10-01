package at.gv.egiz.pdfas.lib.impl.stamping;

import at.gv.egiz.pdfas.common.settings.IProfileConstants;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.PdfAsImpl;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: afitzek Date: 9/11/13 Time: 11:11 AM To
 * change this template use File | Settings | File Templates.
 */
public class ValueResolver implements IProfileConstants, IResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(ValueResolver.class);

	public static final String PatternRegex = "\\$(\\{[^\\$]*\\})";

	private static final String defaultDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String EXP_START = "${";
	public static final String EXP_END = "}";

	// TODO: Currently just for proof of concept ...
	// Should support Reading Fields from Certificate and DATETIME

	// TODO: Use status in real implementation to get currently needed
	// informations...
	public String resolve(String key, String value,
			SignatureProfileSettings settings, RequestedSignature signature) {

		logger.debug("Resolving value for key: " + key);
		logger.debug("Resolving value with value: " + value);

		if (value != null) {

			Pattern pattern = Pattern.compile(PatternRegex);
			Matcher matcher = pattern.matcher(value);
			CertificateResolver certificateResolver = new CertificateResolver(
					signature.getCertificate());
			String result = "";
			int curidx = 0;
			if (matcher.find()) {
				do {
					int idx = matcher.start(0);
					int idxe = matcher.end(0);
					result += value.substring(curidx, idx);
					curidx = idxe;
					result += certificateResolver.resolve(key,
							matcher.group(1), settings, signature);
				} while (matcher.find());
			} else {
				result = value;
			}
			/*
			 * if (value.startsWith(EXP_START) && value.endsWith(EXP_END)) { //
			 * TODO: handle OGNL expression for key and value // TODO: Here we
			 * need the certificate
			 * 
			 * String exp = value.replace('$', ' '); return }
			 */
			return result;
		}

		if (key.equals(SIG_DATE)) {
			if (value == null) {
				value = defaultDateFormat;
			}
			// Value holds the date format!
			SimpleDateFormat formater = new SimpleDateFormat(value);
			Calendar cal = Calendar.getInstance();
			return formater.format(cal.getTime());
		}

		return value;
	}

}
