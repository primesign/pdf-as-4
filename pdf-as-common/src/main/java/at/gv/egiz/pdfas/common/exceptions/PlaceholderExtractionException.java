package at.gv.egiz.pdfas.common.exceptions;

public class PlaceholderExtractionException extends PdfAsException {
	public PlaceholderExtractionException(String msgId) {
        super(msgId);
    }

    public PlaceholderExtractionException(String msgId, Throwable e) {
        super(msgId, e);
    }
}
