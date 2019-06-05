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
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: afitzek Date: 8/28/13 Time: 12:42 PM To
 * change this template use File | Settings | File Templates.
 */
public class StringUtils {

	private static Logger log = LoggerFactory.getLogger(StringUtils.class);

	/**
	 * Maps selection of some diacritic unicode characters to their WinANSI-compatible counterparts.
	 * Both key and value must not be {@code null}.
	 */
	private static final Map<Character, Character> dcMap;
	static {

		Map<Character, Character> map = new HashMap<>();
		// A
		map.put('\u00C3', 'A');
		map.put('\u00c0', 'A');
		map.put('\u00c1', 'A');
		map.put('\u00c2', 'A');
//		map.put('\u00c4', 'A');  // Ä
		map.put('\u00c5', 'A');
		map.put('\u00e0', 'a');
		map.put('\u00e1', 'a');
		map.put('\u00e2', 'a');
		map.put('\u00e3', 'a');
//		map.put('\u00e4', 'a');  // ä
		map.put('\u00e5', 'a');
		map.put('\u0100', 'A');
		map.put('\u0101', 'a');
		map.put('\u0102', 'A');
		map.put('\u0103', 'a');
		map.put('\u0104', 'A');
		map.put('\u0105', 'a');
		map.put('\u01cd', 'A');
		map.put('\u01ce', 'a');
		map.put('\u01de', 'Ä');  // LATIN CAPITAL LETTER A WITH DIAERESIS AND MACRON
		map.put('\u01df', 'ä');  // LATIN SMALL LETTER A WITH DIAERESIS AND MACRON
		map.put('\u0226', 'A');
		map.put('\u0227', 'a');
		// B
		map.put('\u1e02', 'B');
		map.put('\u1e03', 'b');
		// C
		map.put('\u00c7', 'C');
		map.put('\u00e7', 'c');
		map.put('\u0106', 'C');
		map.put('\u0107', 'c');
		map.put('\u0108', 'C');
		map.put('\u0109', 'c');
		map.put('\u010a', 'C');
		map.put('\u010b', 'c');
		map.put('\u010c', 'C');
		map.put('\u010d', 'c');
		// D
		map.put('\u00d0', 'D');
		map.put('\u00f0', 'd');
		map.put('\u010e', 'D');
		map.put('\u010f', 'd');
		map.put('\u0110', 'D');
		map.put('\u0111', 'd');
		map.put('\u1e0a', 'D');
		map.put('\u1e0b', 'd');
		map.put('\u1e10', 'D');
		map.put('\u1e11', 'd');
		// E
		map.put('\u00c8', 'E');
		map.put('\u00c9', 'E');
		map.put('\u00ca', 'E');
		map.put('\u00cb', 'E');
		map.put('\u00e8', 'e');
		map.put('\u00e9', 'e');
		map.put('\u00ea', 'e');
		map.put('\u00eb', 'e');
		map.put('\u0112', 'E');
		map.put('\u0113', 'e');
		map.put('\u0114', 'E');
		map.put('\u0115', 'e');
		map.put('\u0116', 'E');
		map.put('\u0117', 'e');
		map.put('\u0118', 'E');
		map.put('\u0119', 'e');
		map.put('\u011a', 'E');
		map.put('\u011b', 'e');
		map.put('\u0204', 'E');
		map.put('\u0205', 'e');
		// F
		map.put('\u1e1e', 'F');
		map.put('\u1e1f', 'f');
		// G
		map.put('\u011c', 'G');
		map.put('\u011d', 'g');
		map.put('\u011e', 'G');
		map.put('\u011f', 'g');
		map.put('\u0120', 'G');
		map.put('\u0121', 'g');
		map.put('\u0122', 'G');
		map.put('\u0123', 'g');
		map.put('\u01e6', 'G');
		map.put('\u01e7', 'g');
		map.put('\u1e21', 'g');
		// H
		map.put('\u0124', 'H');
		map.put('\u0125', 'h');
		map.put('\u0126', 'H');
		map.put('\u0127', 'h');
		map.put('\u021e', 'H');
		map.put('\u021f', 'h');
		map.put('\u1e22', 'H');
		map.put('\u1e23', 'h');
		map.put('\u1e24', 'H');
		map.put('\u1e25', 'h');
		// I
		map.put('\u00cc', 'I');
		map.put('\u00cd', 'I');
		map.put('\u00ce', 'I');
		map.put('\u00cf', 'I');
		map.put('\u00ec', 'i');
		map.put('\u00ed', 'i');
		map.put('\u00ee', 'i');
		map.put('\u00ef', 'i');
		map.put('\u0128', 'I');
		map.put('\u0129', 'i');
		map.put('\u012a', 'I');
		map.put('\u012b', 'i');
		map.put('\u012c', 'I');
		map.put('\u012d', 'i');
		map.put('\u012e', 'I');
		map.put('\u012f', 'i');
		map.put('\u0130', 'I');
		map.put('\u0131', 'i');
		map.put('\u01cf', 'I');
		map.put('\u01d0', 'i');
		// J
		map.put('\u0134', 'J');
		map.put('\u0135', 'j');
		map.put('\u01f0', 'j');
		map.put('\u0237', 'j');
		// K
		map.put('\u0136', 'K');
		map.put('\u0137', 'k');
		map.put('\u01e8', 'K');
		map.put('\u01e9', 'k');
		map.put('\u1e30', 'K');
		map.put('\u1e31', 'k');
		map.put('\u1e32', 'K');
		map.put('\u1e33', 'k');
		// L
		map.put('\u0139', 'L');
		map.put('\u013a', 'l');
		map.put('\u013b', 'L');
		map.put('\u013c', 'l');
		map.put('\u013d', 'L');
		map.put('\u013e', 'l');
		map.put('\u013f', 'L');
		map.put('\u0140', 'l');
		map.put('\u0141', 'L');
		map.put('\u0142', 'l');
		map.put('\u1e36', 'L');
		map.put('\u1e37', 'l');
		// M
		map.put('\u1e40', 'M');
		map.put('\u1e41', 'm');
		map.put('\u1e42', 'M');
		map.put('\u1e43', 'm');
		// N
		map.put('\u00d1', 'N');
		map.put('\u00f1', 'n');
		map.put('\u0143', 'N');
		map.put('\u0144', 'n');
		map.put('\u0145', 'N');
		map.put('\u0146', 'n');
		map.put('\u0147', 'N');
		map.put('\u0148', 'n');
		map.put('\u1e44', 'N');
		map.put('\u1e45', 'n');
		// O
		map.put('\u00d2', 'O');
		map.put('\u00d3', 'O');
		map.put('\u00d4', 'O');
		map.put('\u00d5', 'O');
//		map.put('\u00d6', 'O');  // Ö
		map.put('\u00d8', 'O');
		map.put('\u00f2', 'o');
		map.put('\u00f3', 'o');
		map.put('\u00f4', 'o');
		map.put('\u00f5', 'o');
//		map.put('\u00f6', 'o');  // ö
		map.put('\u00f8', 'o');
		map.put('\u014c', 'O');
		map.put('\u014d', 'o');
		map.put('\u014e', 'O');
		map.put('\u014f', 'o');
		map.put('\u0150', 'O');
		map.put('\u0151', 'o');
		map.put('\u01d1', 'O');
		map.put('\u01d2', 'o');
		map.put('\u01ea', 'O');
		map.put('\u01eb', 'o');
		map.put('\u01fe', 'O');
		map.put('\u01ff', 'o');
		map.put('\u022a', 'Ö');  // LATIN CAPITAL LETTER O WITH DIAERESIS AND MACRON
		map.put('\u022b', 'ö');  // LATIN SMALL LETTER O WITH DIAERESIS AND MACRON
		map.put('\u022c', 'O');
		map.put('\u022d', 'o');
		map.put('\u022e', 'O');
		map.put('\u022f', 'o');
		map.put('\u1Ecc', 'O');
		map.put('\u1Ecd', 'o');
		// P
		map.put('\u1e56', 'P');
		map.put('\u1e57', 'p');
		// R
		map.put('\u0154', 'R');
		map.put('\u0155', 'r');
		map.put('\u0156', 'R');
		map.put('\u0157', 'r');
		map.put('\u0158', 'R');
		map.put('\u0159', 'r');
		map.put('\u1e58', 'R');
		map.put('\u1e59', 'r');
		// S
		map.put('\u015a', 'S');
		map.put('\u015b', 's');
		map.put('\u015c', 'S');
		map.put('\u015d', 's');
		map.put('\u015e', 'S');
		map.put('\u015f', 's');
		map.put('\u0160', 'S');
		map.put('\u0161', 's');
		map.put('\u0218', 'S');
		map.put('\u0219', 's');
		map.put('\u1e60', 'S');
		map.put('\u1e61', 's');
		// T
		map.put('\u0162', 'T');
		map.put('\u0163', 't');
		map.put('\u0164', 'T');
		map.put('\u0165', 't');
		map.put('\u0166', 'T');
		map.put('\u0167', 't');
		map.put('\u021a', 'T');
		map.put('\u021b', 't');
		map.put('\u1e6a', 'T');
		map.put('\u1e6b', 't');
		// U
		map.put('\u00d9', 'U');
		map.put('\u00da', 'U');
		map.put('\u00db', 'U');
//		map.put('\u00dc', 'U');  // Ü
		map.put('\u00f9', 'u');
		map.put('\u00fa', 'u');
		map.put('\u00fb', 'u');
//		map.put('\u00fc', 'u');  // ü
		map.put('\u0168', 'U');
		map.put('\u0169', 'u');
		map.put('\u016a', 'U');
		map.put('\u016b', 'u');
		map.put('\u016c', 'U');
		map.put('\u016d', 'u');
		map.put('\u016e', 'U');
		map.put('\u016f', 'u');
		map.put('\u0170', 'U');
		map.put('\u0171', 'u');
		map.put('\u0172', 'U');
		map.put('\u0173', 'u');
		map.put('\u01d3', 'U');
		map.put('\u01d4', 'u');
		// W
		map.put('\u0174', 'W');
		map.put('\u0175', 'w');
		// Y
		map.put('\u00dd', 'Y');
		map.put('\u00fd', 'y');
		map.put('\u00ff', 'y');
		map.put('\u0176', 'Y');
		map.put('\u0177', 'y');
		map.put('\u0178', 'Y');
		map.put('\u0232', 'Y');
		map.put('\u0233', 'y');
		// Z
		map.put('\u0179', 'Z');
		map.put('\u017a', 'z');
		map.put('\u017b', 'Z');
		map.put('\u017c', 'z');
		map.put('\u017d', 'Z');
		map.put('\u017e', 'z');
		map.put('\u01b5', 'Z');
		map.put('\u01b6', 'z');

		dcMap = Collections.unmodifiableMap(map);

		if (log.isTraceEnabled()) {

			// output map with sorted unicode keys

			Comparator<Character> comparator = new Comparator<Character>() {

				@Override
				public int compare(Character o1, Character o2) {
					int i1 = o1 != null ? (int)o1.charValue() : 0;
					int i2 = o2 != null ? (int)o2.charValue() : 0;
					return Integer.compare(i1,  i2);
				}

			};
			TreeMap<Character, Character> sortedMap = new TreeMap<>(comparator);
			sortedMap.putAll(dcMap);
			StringBuilder sb = new StringBuilder();
			sb.append("Diacritics map: ");
			for (Entry<Character, Character> entry : sortedMap.entrySet()) {
				Character key = entry.getKey();
				Character value = entry.getValue();
				sb.append("\n'").append(key).append("' (").append(String.format ("\\u%04x", (int)key)).append(") -> '").append(value).append('\'');
			}

			log.trace(sb.toString());
		}
	}

	/**
	 * Uses internal static map (not necessarily complete) to replace diacritics by their non-diacritic counterparts.
	 *
	 * @param character The character.
	 * @return An {@link Optional} with either the new character or empty if there was no mapping for the given
	 *         character.
	 */
	public static Optional<Character> replaceDiacritic(char character) {
		return Optional.ofNullable(dcMap.get(character));
	}

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
