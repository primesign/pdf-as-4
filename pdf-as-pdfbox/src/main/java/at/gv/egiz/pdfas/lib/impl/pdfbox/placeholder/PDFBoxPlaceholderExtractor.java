package at.gv.egiz.pdfas.lib.impl.pdfbox.placeholder;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.pdfbox.PDFBOXObject;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

public class PDFBoxPlaceholderExtractor implements PlaceholderExtractor {

	@Override
	public SignaturePlaceholderData extract(PDFObject doc,
			String placeholderId, int matchMode) throws PdfAsException {
		if (doc instanceof PDFBOXObject) {
			PDFBOXObject object = (PDFBOXObject) doc;
			return SignaturePlaceholderExtractor.extract(object.getDocument(),
					placeholderId, matchMode);
		}
		throw new PdfAsException("INVALID STATE");
	}
}
