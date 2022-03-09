package at.gv.egiz.pdfas.lib.impl.pdfbox2.placeholder;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.PDFBOXObject;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

import java.io.IOException;
import java.util.List;

public class PDFBoxPlaceholderExtractor implements PlaceholderExtractor {


	@Override
	public SignaturePlaceholderData extract(PDFObject doc, String placeholderId, int matchMode) throws PdfAsException {
		if (doc instanceof PDFBOXObject) {
			PDFBOXObject object = (PDFBOXObject) doc;
			try {
				SignaturePlaceholderExtractor extractor = new SignaturePlaceholderExtractor(placeholderId,
						matchMode, object.getDocument());
				return extractor.extract(object.getDocument(),
						placeholderId, matchMode);
			} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e2) {
				throw new PDFIOException("error.pdf.io.04", e2);
			}

		}
		throw new PdfAsException("INVALID STATE");
	}

	@Override
	public List<SignaturePlaceholderData> extractList(PDFObject doc, String placeholderId, int matchMode) throws PdfAsException {
		if (doc instanceof PDFBOXObject) {
			PDFBOXObject object = (PDFBOXObject) doc;
			try {
				SignaturePlaceholderExtractor extractor = new SignaturePlaceholderExtractor(placeholderId,
						matchMode, object.getDocument());
				return extractor.extractList(object.getDocument(),
						placeholderId, matchMode);
			} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e2) {
				throw new PDFIOException("error.pdf.io.04", e2);
			}
		}
		throw new PdfAsException("INVALID STATE");
	}
}
