package at.gv.egiz.pdfas.lib.impl.placeholder;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

public interface PlaceholderExtractor {
	public SignaturePlaceholderData extract(PDFObject doc,
			String placeholderId, int matchMode) throws PdfAsException;
}
