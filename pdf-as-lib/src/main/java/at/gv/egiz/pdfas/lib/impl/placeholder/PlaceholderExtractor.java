package at.gv.egiz.pdfas.lib.impl.placeholder;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

import java.util.List;

public interface PlaceholderExtractor {
	SignaturePlaceholderData extract(PDFObject doc, String placeholderId, int matchMode) throws PdfAsException;

	List<SignaturePlaceholderData> extractList(PDFObject pdfObject, String placeholderID, int placeholderMode) throws PdfAsException;
}
