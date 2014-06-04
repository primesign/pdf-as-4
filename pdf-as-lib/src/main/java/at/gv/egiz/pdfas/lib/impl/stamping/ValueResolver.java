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
package at.gv.egiz.pdfas.lib.impl.stamping;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.IProfileConstants;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.status.ICertificateProvider;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;

/**
 * Created with IntelliJ IDEA. User: afitzek Date: 9/11/13 Time: 11:11 AM To
 * change this template use File | Settings | File Templates.
 */
public class ValueResolver implements IProfileConstants, IResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(ValueResolver.class);

	public static final String PatternRegex = "\\$(\\{[^\\$]*\\})";

	public static final String defaultDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String EXP_START = "${";
	public static final String EXP_END = "}";

	private CertificateResolver certificateResolver;
	
	public ValueResolver(ICertificateProvider certProvider, OperationStatus operationStatus) {
		certificateResolver = new CertificateResolver(
				certProvider.getCertificate(), operationStatus);
	}
	
	public String resolve(String key, String value,
			SignatureProfileSettings settings) {

		logger.debug("Resolving value for key: " + key);
		logger.debug("Resolving value with value: " + value);

		if (key.equals(SIG_DATE)) {
			if (value == null) {
				value = defaultDateFormat;
			}
			// Value holds the date format!
			SimpleDateFormat formater = new SimpleDateFormat(value);
			Calendar cal = Calendar.getInstance();
			return formater.format(cal.getTime());
		}
		
		if (value != null) {

			Pattern pattern = Pattern.compile(PatternRegex);
			Matcher matcher = pattern.matcher(value);
			
			String result = "";
			int curidx = 0;
			if (matcher.find()) {
				do {
					int idx = matcher.start(0);
					int idxe = matcher.end(0);
					result += value.substring(curidx, idx);
					curidx = idxe;
					result += certificateResolver.resolve(key,
							matcher.group(1), settings);
				} while (matcher.find());
			} else {
				result = value;
			}
			return result;
		}

		return value;
	}

}
