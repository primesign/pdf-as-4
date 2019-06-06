package at.gv.egiz.pdfas.common.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing methods for normalizing text suitable for being used for visual signature representation
 * text.
 *
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public class PDFTextNormalizationUtils {

	private static Logger log = LoggerFactory.getLogger(PDFTextNormalizationUtils.class);

	private PDFTextNormalizationUtils() {
		throw new AssertionError();
	}

	/**
	 * Character that is taken in case normalization fails for a certain character.
	 */
	private static final char REPLACEMENT_FOR_NON_SUPPORTED_CHAR = '?';

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

	/**
	 * Normalizes a given text making sure each character is either covered by WinAnsiEncoding or canonically replaced
	 * by a WinAnsi-compatible character.
	 *
	 * @param text              The source text (optional; may be {@code null}).
	 * @param isWinAnsiEncoding A {@link Predicate} testing if the provided character is supported by WinAnsiEncoding
	 *                          (required; must not be {@code null}).
	 * @return The normalized text (may be {@code null}).
	 */
	public static String normalizeText(String text, Predicate<Character> isWinAnsiEncoding) {
		if (text == null) {
			return null;
		}

		// canonical decomposition, followed by canonical composition
		// for the case the text comes in already decomposed form (we want to evaluate char by char)
		String preNormalizedText = Normalizer.normalize(text, Form.NFC);

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < preNormalizedText.length(); i++) {
			char c = preNormalizedText.charAt(i); // fastest approach (refer to https://stackoverflow.com/a/11876086)
			if (c == '\n') {
				// skip normalization for newline which is used for layout control and is not a character actually
				stringBuilder.append(c);
			} else {
				stringBuilder.append(normalizeCharacter(c, isWinAnsiEncoding));
			}
		}

		String normalizedText = stringBuilder.toString();
		if (!normalizedText.equals(text)) {
			log.debug("Normalizing text for visual signature representation: \"{}\" -> \"{}\"", text, normalizedText);
		}

		return normalizedText;
	}

	/**
	 * Normalizes a given character making sure the (given) character is either covered by WinAnsiEncoding or
	 * canonically replaced by a WinAnsi-compatible character.
	 *
	 * @param character The given character.
	 * @param isWinAnsiEncoding A {@link Predicate} testing if the provided character is supported by WinAnsiEncoding
	 *                          (required; must not be {@code null}).
	 * @return The given character if WinAnsi-compatible, a WinAnsi-compatible equivalent (if possible) or a question mark.
	 */
	public static char normalizeCharacter(char character, Predicate<Character> isWinAnsiEncoding) {

		// first stage: is the given character already WinAnsi-compatible ?
		if (isWinAnsiEncoding.test(character)) {
			return character;
		}

		// second stage: use tool to strip accents (does not cover all diacritics)
		char withoutAccent = org.apache.commons.lang3.StringUtils.stripAccents(String.valueOf(character)).charAt(0);
		// is the result already WinAnsi-compatible?
		if (isWinAnsiEncoding.test(withoutAccent)) {
			return withoutAccent;
		}

		// third stage: use internal map (not necessarily complete) to replace diacritic characters with their WinAnsi-compatible counterparts
		Optional<Character> nonDiacritic = replaceDiacritic(withoutAccent);
		if (nonDiacritic.isPresent()) {
			return nonDiacritic.get();
		}

		log.info("Unable to map diacritic character '{}' ({}) to any non-diacritic counterpart.", withoutAccent, String.format ("\\u%04x", (int)withoutAccent));
		return REPLACEMENT_FOR_NON_SUPPORTED_CHAR;

	}

}
