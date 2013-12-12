package at.gv.egiz.pdfas.common.exceptions;

public class PlaceholderExtractionException extends PdfAsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6879275004491426233L;

	public PlaceholderExtractionException(String msgId) {
        super(msgId);
    }

    public PlaceholderExtractionException(String msgId, Throwable e) {
        super(msgId, e);
    }
}
