package at.gv.egiz.pdfas.lib.util;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;

public class SignatureUtils {

	public static int countSignatures(PDDocument doc) {
		int count = 0;
		COSDictionary trailer = doc.getDocument().getTrailer();
		COSDictionary root = (COSDictionary) trailer
				.getDictionaryObject(COSName.ROOT);
		COSDictionary acroForm = (COSDictionary) root
				.getDictionaryObject(COSName.ACRO_FORM);
		COSArray fields = (COSArray) acroForm
				.getDictionaryObject(COSName.FIELDS);
		for (int i = 0; i < fields.size(); i++) {
			COSDictionary field = (COSDictionary) fields.getObject(i);
			String type = field.getNameAsString("FT");
			if ("Sig".equals(type)) {
				count++;
			}
		}
		
		return count;
	}
}
