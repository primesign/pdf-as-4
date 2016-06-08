package at.gv.egiz.pdfas.lib.impl.pdfbox2.utils;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsValidationException;

public class PdfBoxUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(PdfBoxUtils.class);
	
	public static void checkPDFPermissions(PDDocument doc)
			throws PdfAsValidationException {

		AccessPermission accessPermission = doc.getCurrentAccessPermission();
		if (doc.isEncrypted()) {
			throw new PdfAsValidationException("error.pdf.sig.12", null);
		}

		if (!accessPermission.isOwnerPermission()) {
			throw new PdfAsValidationException("error.pdf.sig.12", null);
		}

	}

	public static int countSignatures(PDDocument doc, String sigName) {
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
				String name = field.getString(COSName.T);
				if (name != null) {
					logger.debug("Found Sig: " + name);
					try {
						if (name.startsWith(sigName)) {
							String numberString = name.replace(sigName, "");

							logger.debug("Found Number: " + numberString);

							int SigIDX = Integer.parseInt(numberString);
							if (SigIDX > count) {
								count = SigIDX;
							}
						}
					} catch (Throwable e) {
						logger.info("Found a different Signature, we do not need to count this.");
					}
				}
			}

		}

		count++;

		logger.debug("Returning sig number: " + count);

		return count;
	}

}
