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

import at.gv.egiz.pdfas.common.settings.IProfileConstants;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.status.ICertificateProvider;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;

import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA. User: afitzek Date: 9/11/13 Time: 11:11 AM To
 * change this template use File | Settings | File Templates.
 */
public class ValueResolver implements IProfileConstants, IResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(ValueResolver.class);

	public static final String PatternRegex = "\\$(\\{[^\\$]*\\})";

	public static final String defaultDateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";

	public static final String EXP_START = "${";
	public static final String EXP_END = "}";
	private static final Charset ISO = Charset.forName("ISO-8859-1");
	private static final Charset UTF_8 = Charset.forName("UTF-8");


	private IResolver internalResolver;
	public ValueResolver(ICertificateProvider certProvider, OperationStatus operationStatus) {
		internalResolver = new CertificateAndRequestParameterResolver(certProvider.getCertificate(),
				operationStatus);
	}

	public String resolve(String key, String value,
			SignatureProfileSettings settings) {

		logger.debug("Resolving value for key: " + key);
		logger.debug("Resolving value with value: " + value);

		//this needs to be encoded because of special characters
		if(settings.isLatin1Encoding()) {
			value = new String(value.getBytes(ISO), UTF_8);
		}

		if (key.equals(SIG_DATE)) {
			if (value == null) {
				value = defaultDateFormat;
			}
			
			// Value holds the date format!
			//
			SimpleDateFormat fdf = new SimpleDateFormat(value);
			String timeZone = settings.getProfileTimeZone();
			
			if(timeZone != null) {
				fdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			}
			Calendar cal = Calendar.getInstance();
			return fdf.format(cal.getTime());
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
					String tmp1 = value.substring(curidx, idx);
					result += tmp1;
					curidx = idxe;
					String tmpValue = matcher.group(1);
					String tmp2 = internalResolver.resolve(key, tmpValue, settings);
					result += tmp2;
				} while (matcher.find());
				if(value.length() > curidx){
					result += value.substring(curidx);
				}
			} else {
				result = value;
			}
			return result;
		}

		return value;
	}

}
