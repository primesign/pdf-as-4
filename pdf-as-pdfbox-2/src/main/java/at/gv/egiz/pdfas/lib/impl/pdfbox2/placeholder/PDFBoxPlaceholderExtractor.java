package at.gv.egiz.pdfas.lib.impl.pdfbox2.placeholder;

import java.io.IOException;
import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.PDFBOXObject;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

public class PDFBoxPlaceholderExtractor implements PlaceholderExtractor {

	@Override
	public SignaturePlaceholderData extract(PDFObject doc, String placeholderId, int matchMode) throws PdfAsException {
		if (doc instanceof PDFBOXObject) {
			PDFBOXObject object = (PDFBOXObject) doc;
			return SignaturePlaceholderExtractor.extract(object.getDocument(), placeholderId, matchMode);
		}
		throw new PdfAsException("INVALID STATE");
	}

	@Override
	public List<SignaturePlaceholderData> extractList(PDFObject doc, String placeholderId, int matchMode) throws PdfAsException {
		if (doc instanceof PDFBOXObject) {
			PDFBOXObject object = (PDFBOXObject) doc;
			try {
				return SignaturePlaceholderExtractor.extract(object.getDocument());
			} catch (IOException e) {
				throw new PDFIOException("error.pdf.io.04", e);
			}
		}
		throw new PdfAsException("INVALID STATE");
	}
}
