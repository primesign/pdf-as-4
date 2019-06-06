package at.gv.egiz.pdfas.common.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Predicate;

import org.junit.Test;

public class PDFTextNormalizationUtilsTest {

	private String replaceDiacritics(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			sb.append(PDFTextNormalizationUtils.replaceDiacritic(c).orElse(c));
		}
		return sb.toString();
	}

	@Test
	public void testReplaceDiacritic() {

		String candidate1 = "\u1e40\u01ce\u0078 \u1e42\u0169\u0219\u0167\u0119\u0155\u1e41\u01df\u00f1\u1e45";
		assertThat(replaceDiacritics(candidate1), equalTo("Max Mustermänn"));

		String candidate2 = "\u015e\u0061\u015e\u006e\u0075";
		assertThat(replaceDiacritics(candidate2), equalTo("SaSnu"));

		String candidate3 = "\u0058\u0058\u0058\u0052\u00fa\u00f9\u0064 \u0058\u0058\u0058\u0056\u00e0\u006e \u004e\u0069\u0073\u0074\u0065\u013a\u0072\u006f\u006f\u0079";
		assertThat(replaceDiacritics(candidate3), equalTo("XXXRuud XXXVan Nistelrooy"));

		String candidate4 = "\u0058\u0058\u0058\u0150\u007a\u0067\u00fc\u0072 \u0058\u0058\u0058\u0054\u00fc\u007a\u0065\u006b\u00e7";
		assertThat(replaceDiacritics(candidate4), equalTo("XXXOzgür XXXTüzekc"));

	}

	@Test
	public void testNormalizeText() {

		Predicate<Character> isWinAnsiEncoding = (c) -> {
			// fits this test only: the following chars are accepted as WinAnsi-compatible
			return "Max Mustermann".contains(String.valueOf(c));
		};

		// "Max Mustermann" with diacritic chars
		String normalizedText = PDFTextNormalizationUtils.normalizeText("\u1e40\u01ce\u0078 \u1e42\u0169\u0219\u0167\u0119\u0155\u1e41\u01df\u00f1\u1e45", isWinAnsiEncoding);
		assertThat(normalizedText, equalTo("Max Mustermann"));

	}

	@Test
	public void testNormalizeTextWithNewLines() {

		Predicate<Character> isWinAnsiEncoding = (c) -> {
			// fits this test only: the following chars are accepted as WinAnsi-compatible
			return "Max Mustermann".contains(String.valueOf(c));
		};

		// "Max Mustermann" with diacritic chars
		String normalizedText = PDFTextNormalizationUtils.normalizeText("\u1e40\u01ce\u0078\n\u1e42\u0169\u0219\u0167\u0119\u0155\u1e41\u01df\u00f1\u1e45", isWinAnsiEncoding);
		assertThat(normalizedText, equalTo("Max\nMustermann"));

	}

}
