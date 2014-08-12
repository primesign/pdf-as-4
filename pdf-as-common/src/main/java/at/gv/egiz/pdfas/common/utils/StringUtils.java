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
package at.gv.egiz.pdfas.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: afitzek Date: 8/28/13 Time: 12:42 PM To
 * change this template use File | Settings | File Templates.
 */
public class StringUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(StringUtils.class);

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);

		Formatter formatter = new Formatter(sb);
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		formatter.close();

		return sb.toString();
	}

	public static String extractLastID(String id) {
		int lastIDX = id.lastIndexOf('.');
		String result = id;
		if (lastIDX > 0) {
			result = id.substring(lastIDX + 1);
		}
		return result;
	}

	public static String convertStringToPDFFormat(String value)
			throws UnsupportedEncodingException {
		
		if(value == null) {
			logger.warn("Trying to convert null string!");
			return value;
		}
		
		byte[] replace_bytes = applyWinAnsiEncoding(value);

		String restored_value = unapplyWinAnsiEncoding(replace_bytes);
		if (!value.equals(restored_value)) {
			// Cannot encode String with CP1252 have to use URL encoding ...
			return URLEncoder.encode(value, "UTF-8");
		}
		return value;
	}

	public static byte[] applyWinAnsiEncoding(String text)
			throws UnsupportedEncodingException {
		byte[] replace_bytes;
		replace_bytes = text.getBytes("windows-1252");// CP1252 =
														// WinAnsiEncoding
		return replace_bytes;
	}

	/**
	 * Unapplies the WinAnsi encoding.
	 * 
	 * @param replace_bytes
	 *            The bytes.
	 * @return Returns the decoded String.
	 * @throws UnsupportedEncodingException 
	 */
	public static String unapplyWinAnsiEncoding(byte[] replace_bytes) throws UnsupportedEncodingException {
		String text = new String(replace_bytes, "windows-1252");
		return text;
	}
	
	public static String whiteSpaceTrim(String string) {
		String str = startStrip(string);
		return endStrip(str);
	}

	private static String startStrip(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		int start = 0;
		while (start != strLen && isEmptySpace(str.charAt(start))) {
			start++;
		}
		return str.substring(start);
	}
	
	private static String endStrip(final String str) {
		int end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}
		while (end != 0 && isEmptySpace(str.charAt(end - 1))) {
			end++;
		}

		return str.substring(0, end);
	}
	
	private static boolean isEmptySpace(char c) {
		return Character.isWhitespace(c) || Character.isSpaceChar(c);
	}
}
